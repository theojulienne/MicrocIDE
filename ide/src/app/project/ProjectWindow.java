package app.project;

import java.io.File;

import java.util.ArrayList;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;

import org.eclipse.swt.custom.SashForm;

import org.eclipse.swt.events.*;

import org.eclipse.swt.graphics.Rectangle;

import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;

import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;

import boldinventions.birdterm.BirdTerm;

import app.Application;
import app.Preferences;
import app.dialogs.ProjectSettingsDialog;

import app.plugin.interfaces.IToolBar;

import app.document.DocumentTab;
import app.document.DocumentTabFolder;
import app.project.navigation.MenuBar;
import app.project.navigation.ProjectTreeMenu;

import app.toolTab.ToolTabFolder;



public class ProjectWindow {
	
	private Shell shell;
	private MenuBar menuBar;

	ProjectTreeMenu projectMenu;
	
	private DocumentTabFolder docTabs;
	private ToolTabFolder toolTabs;
	
	// TODO: this be elsewhere (plugin)
	private BirdTerm termWindow;
	
	public ProjectWindow() {
	}

	public Shell open( Display display, File projectFile ){
		
		// create new window
		shell = new Shell( display );

		shell.addShellListener( new ShellListener( ) {
			public void shellActivated(ShellEvent e) {
				
			}
			public void shellClosed(ShellEvent e) {
				e.doit = canQuit( );
			}
			public void shellDeactivated(ShellEvent e) {
			}
			public void shellDeiconified(ShellEvent e) {
			}
			public void shellIconified(ShellEvent e) {
			}
		});
		
		// window title
		shell.setText( Application.appName );
		
		
		
		// make UI
		createUI( projectFile );
		
		// pack up that window
		
		Rectangle screenSize = shell.getDisplay().getPrimaryMonitor().getBounds();
		
		shell.pack( );
		shell.setSize( (int)(screenSize.width*0.75), (int)(screenSize.height*0.75) );

		shell.open( );
		shell.setFocus();
		
		return shell;
	}
	
	
	private void createUI( File projectDir ) {
		
		shell.setLayout( new GridLayout( 1, true ) );
		
		menuBar = new MenuBar( shell, this );
		
		GridData sashGridData = new GridData(SWT.FILL, SWT.FILL, true, true);
		SashForm sashForm = new SashForm( shell, SWT.VERTICAL );
		sashForm.setLayoutData( sashGridData );
		
		
		SashForm fileForm = new SashForm( sashForm, SWT.HORIZONTAL );
		
		projectMenu = new ProjectTreeMenu( fileForm, projectDir, this );
		
		docTabs = new DocumentTabFolder( fileForm, SWT.CLOSE | SWT.TOP | SWT.BORDER ); // | SWT.FLAT
		docTabs.setProjectWindow( this );
		
		fileForm.setWeights( new int[]{ 1, 4 } );
		
		toolTabs = new ToolTabFolder( sashForm, SWT.TOP | SWT.BORDER ); // | SWT.FLAT
		toolTabs.setProjectWindow( this );
		
		sashForm.setWeights( new int[]{ 7, 3 } );
		
		Label statusBar = new Label( shell, SWT.NONE );
		statusBar.setLayoutData( new GridData( SWT.FILL, SWT.CENTER, true, false, 1, 1 ) );
		statusBar.setAlignment( SWT.LEFT );
		
		projectMenu.setStatusWidget( statusBar );
		projectMenu.setOpenListener( new Listener() {
			public void handleEvent(Event event) {
				File fileToOpen = (File)event.data;
				
				openDocument( fileToOpen );
			}
		} );
		
		
		// create the tool tabs
		toolTabs.createTools( );

		//buildConsole = new BuildConsoleTab( consoleTabs );
		//serialTerminal = new SerialTerminalTab( consoleTabs );
		
	}
	
	
	
	
	public Display getDisplay( ) {
		return this.shell.getDisplay();
	}
	
	public boolean closeSelectedTab( ) {
		boolean closed = true;
		DocumentTab tab = getFocussedDocumentTab( );
		if ( tab != null ) {
			closed = tab.close( );
		}
		return closed;
	}
	
	public DocumentTab getFocussedDocumentTab( ) {
		Control control = this.shell.getDisplay( ).getFocusControl( );
		DocumentTab focussedTab = null;
		
		if ( control != null ) {
			for ( DocumentTab tab : docTabs.getDocumentTabs() ) {
				if ( tab.getDocument( ).getMainWidget( ) == control ) {
					focussedTab = tab;
					break;
				}
			}
		}
		
		if ( focussedTab == null ) {
			focussedTab = (DocumentTab)docTabs.getSelection( );
		}
		
		return focussedTab;
	}
	
	
	public void updateTree( ) {
		projectMenu.update( );
	}
	
	public void saveSelectedTab( ) {
		DocumentTab selTab = getFocussedDocumentTab( );
		if ( selTab != null ) {
			selTab.getDocument( ).save( );
		}
	}

	public void saveAsSelectedTab( ) {
		DocumentTab selTab = getFocussedDocumentTab( );
		if ( selTab != null ) {
			selTab.getDocument( ).saveAs( );
		}
	}
	
	public void saveAll( ) {
		for ( DocumentTab tab : docTabs.getDocumentTabs( ) ) {
			tab.getDocument( ).save( );
		}
	}
	
	public void openProject( ) {
		openProject( null );
	}

	public void openProject( File projectPath ) {
		
		if ( closeProject( ) ) {
			
			File path = projectPath;
			
			if ( path == null ) {
				DirectoryDialog openDialog = new DirectoryDialog( shell );
	
				openDialog.setFilterPath( File.listRoots()[0].getAbsolutePath() );
				openDialog.setText( "Open Project" );
				openDialog.setMessage( "Select Project Directory to Open" );
				String projectToOpen = openDialog.open();
				
				if ( projectToOpen != null ) {
					path = new File( projectToOpen );
				} else {
					path = null;
				}
			}
			
			// confirm project file
			if ( path != null ) {
				boolean hasProjectFile = false;
				for ( File file : path.listFiles() ) {
					if ( file.getName().equals( Application.projectSettingsFileName ) ) {
						hasProjectFile = true;
						break;
					}
				}
				
				if ( !hasProjectFile ) {
		
					boolean canCreate = MessageDialog.openConfirm( shell, "No Project File", "The selected folder has no project definition file.\nCreate one now?" );
						
					if ( canCreate ) {
						openProjectSettings( path );
					} else {
						path = null;
					}
				}
			}
			
			// opening project, save as recently viewed
			if ( path != null ) {
				Preferences prefs = Application.getInstance().getPreferences();
				ArrayList<String> recentProjects = prefs.getRecentList( );
				
				recentProjects.remove( path.getAbsolutePath() );
				recentProjects.add( 0, path.getAbsolutePath() );
				
				prefs.setRecentList( recentProjects );
				prefs.save( );
			}
			
			
			projectMenu.setPath( path );
		}
	}
	
	public File getPath( ) {
		return projectMenu.getPath( );
	}
	
	public void quit( ) {
		this.shell.close( );
	}
	
	public void newProject( ) {
		if ( closeProject( ) ) {
			FileDialog chooseDirDialog = new FileDialog( shell, SWT.SAVE );

			chooseDirDialog.setFilterPath( File.listRoots()[0].getAbsolutePath() );
			chooseDirDialog.setText( "New Project" );
			
			String parentDir = chooseDirDialog.open();
			
			if ( parentDir != null ) {
				File parentPath = new File( parentDir );
				if ( parentPath.mkdir( ) ) {
					openProjectSettings( parentPath );
					projectMenu.setPath( parentPath );
				} else {
					MessageDialog.openError( shell, "Unable to Create New Project", "Unable to create a new project." );
				}
			}
		}
	}
	
	public void openProjectSettings( ) {
		openProjectSettings( getPath() );
	}
	
	public void openProjectSettings( File projectPath ) {
		ProjectSettingsDialog settingsDialog = new ProjectSettingsDialog( shell, this );
		settingsDialog.open( );
	}
	
	public void find( ) {
		DocumentTab tab = getFocussedDocumentTab( );
		if ( tab != null ) {
			tab.getDocument( ).find( );
		}
	}
	
	public void findNext( ) {
		DocumentTab tab = getFocussedDocumentTab( );
		if ( tab != null ) {
			tab.getDocument( ).findNext( );
		}
	}
	
	public void findPrev( ) {
		DocumentTab tab = getFocussedDocumentTab( );
		if ( tab != null ) {
			tab.getDocument( ).findPrev( );
		}
	}
	
	public void openDocument( File fileToOpen ) {
		if ( fileToOpen.getName().equals( Application.projectSettingsFileName ) ) {
			
			boolean openPrefs = MessageDialog.openQuestion( shell, "Opening Settings", "This is a project settings file.\nDo you want to open the settings dialog instead?" );
			if ( openPrefs ) {
				openProjectSettings( fileToOpen.getParentFile() );
				return;
			}
		}
		
		docTabs.openDocument( fileToOpen );
	}
	
	public boolean canQuit( ) {
		return closeProject( );
	}
	
	public MenuBar getMenuBar( ) {
		return menuBar;
	}
	
	public boolean closeProject( ) {
		boolean canClose = true;
		
		for ( DocumentTab tab : docTabs.getDocumentTabs() ) {
			if ( !tab.close() ) {
				canClose = false;
				break;
			}
		}
		
		if ( canClose ) {
			projectMenu.setPath( null );
		}
		
		return canClose;
	}
	
	
	public void openTerminalApp( ) {
		if ( termWindow == null || termWindow.getShell() == null || termWindow.getShell().isDisposed() ) {
			termWindow = new BirdTerm( null );
        	termWindow.setBlockOnOpen( false );
        	termWindow.open( );
		} else {
			termWindow.getShell().setFocus();
		}
	}
	
	public boolean isDisposed( ) {
		return shell.isDisposed( );
	}

	public void renameDocument( File fromFile, File newFile ) {
		docTabs.renameDocumentTab( fromFile, newFile );
	}

	public IToolBar getToolBar() {
		return projectMenu.getToolBar( );
	}

	public void setDocumentEnabled(boolean enabled) {
		getMenuBar( ).setDocumentEnabled( enabled );

		if ( toolTabs != null ) {
			toolTabs.setDocumentEnabled( getFocussedDocumentTab(), enabled );
		}
	}

	public void setProjectEnabled( boolean enabled ) {
		getMenuBar( ).setProjectEnabled( enabled );
		if ( toolTabs != null ) {
			toolTabs.setProjectEnabled( enabled );
		}
	}
}
