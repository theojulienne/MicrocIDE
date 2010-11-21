package app.project;

import java.io.File;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Hashtable;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;

import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabFolder2Listener;
import org.eclipse.swt.custom.CTabFolderEvent;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.custom.StyledText;

import org.eclipse.swt.events.*;

import org.eclipse.swt.graphics.Image;
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
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import boldinventions.birdterm.BirdTerm;
import build.CmdRunner;

import files.FileIO;

import app.Application;
import app.Preferences;
import app.dialogs.ProjectSettingsDialog;
import app.project.document.DocumentTab;
import app.project.navigation.MenuBar;
import app.project.navigation.ProjectTreeMenu;
import app.toolTabs.BuildConsoleTab;
import app.toolTabs.SerialTerminalTab;

public class ProjectWindow {
	private static final int BUILD  = 1;
	private static final int DEPLOY = 2;
	
	private Shell shell;
	private CTabFolder docTabs;
	
	private BuildConsoleTab buildConsole;
	
	private SerialTerminalTab serialTerminal;
	
	private Hashtable<File, DocumentTab> documents;
	private MenuBar menuBar;
	
	ProjectTreeMenu projectMenu;
	
	private CmdRunner deployRunner;
	private CmdRunner buildRunner;
	
	private HashMap<String, Image> images;
	
	private BirdTerm termWindow;
	
	public ProjectWindow() {
		documents = new Hashtable<File, DocumentTab>( );
		images = new HashMap<String, Image>( );
	}
	
	private void addConsoleTabHandlers( CTabFolder consoleTabs ) {
		consoleTabs.addCTabFolder2Listener( new CTabFolder2Listener() {

		      public void close( CTabFolderEvent evt ) {
		    	  
		      }

		      public void minimize( CTabFolderEvent evt ) {

		    	  CTabFolder folder = (CTabFolder) evt.widget;
		    	  SashForm parent = (SashForm) folder.getParent();
		    	  
		    	  if ( !folder.getMaximized() && !folder.getMinimized() ) {
		    		  folder.setData( parent.getWeights() );
		    	  }
		    	  
		    	  
		    	  parent.setMaximizedControl( null );
		    	  
		    	  int heightRatio = parent.getClientArea().height / (folder.getTabHeight() + 5);
		    	  System.out.println( heightRatio );
		    	  parent.setWeights( new int[]{ heightRatio, 1 } );
		    	  
		    	  folder.setMinimized( true );
		      }

		      public void maximize( CTabFolderEvent evt ) {
		    	  CTabFolder folder = (CTabFolder) evt.widget;
		    	  SashForm parent = (SashForm) folder.getParent();
		    	  
		    	  if ( !folder.getMaximized() && !folder.getMinimized() ) {
		    		  folder.setData( parent.getWeights() );
		    	  }
		    	  
		    	  folder.setMaximized( true );
		    	  parent.setWeights( new int[]{ 1, 10 } );
		    	  parent.setMaximizedControl( folder );
		      }

		      public void restore( CTabFolderEvent evt ) {
		    	  CTabFolder folder = (CTabFolder) evt.widget;
		    	  folder.setMaximized( false );
		    	  folder.setMinimized( false );
		    	  SashForm parent = (SashForm) folder.getParent();
		    	  
		    	  if ( folder.getData() != null ) {
		    		  parent.setWeights( (int[]) folder.getData() );
		    	  } else {
		    		  parent.setWeights( new int[]{ 7, 3 } );
		    	  }
		    	  
		    	  parent.setMaximizedControl( null );
		      }

		      public void showList(CTabFolderEvent evt) {
		    	  
		      }
		} );
		
		consoleTabs.addControlListener( new ControlListener() {
			public void controlMoved(ControlEvent e) {
			}
			public void controlResized(ControlEvent e) {
				CTabFolder folder = (CTabFolder) e.widget;
				folder.setMinimized( false );
			}
		} );
	}
	
	private void addDocTabsListeners( ) {
		
		docTabs.addCTabFolder2Listener( new CTabFolder2Listener( ) {
			public void close(CTabFolderEvent event) {
				DocumentTab tab = (DocumentTab)event.item;
				if ( !closeDocument( tab ) ) {
					event.doit = false;
				}
			}
			public void maximize(CTabFolderEvent event) {
			}
			public void minimize(CTabFolderEvent event) {
			}
			public void restore(CTabFolderEvent event) {
			}
			public void showList(CTabFolderEvent event) {
			}
			
		} );
	}
	
	public Display getDisplay( ) {
		return this.shell.getDisplay();
	}
	
	public boolean closeSelectedTab( ) {
		boolean closed = true;
		DocumentTab tab = getFocussedDocumentTab( );
		if ( tab != null ) {
			closed = closeDocument( tab );
		}
		return closed;
	}
	
	public DocumentTab getFocussedDocumentTab( ) {
		Control control = this.shell.getDisplay( ).getFocusControl( );
		if ( control != null ) {
			if ( control instanceof StyledText ) {
				Enumeration<DocumentTab> docEnum = documents.elements();
				while ( docEnum.hasMoreElements( ) ) {
					DocumentTab tab = docEnum.nextElement( );
					if ( tab.getTextWidget() == (StyledText)control ) {
						return tab;
					}
				}
			}
		}
		
		return null;
	}
	
	
	private void createUI( File projectDir ) {
		
		shell.setLayout( new GridLayout( 1, true ) );
		
		menuBar = new MenuBar( shell, this );
		
		GridData sashGridData = new GridData(SWT.FILL, SWT.FILL, true, true);
		SashForm sashForm = new SashForm( shell, SWT.VERTICAL );
		sashForm.setLayoutData( sashGridData );
		
		
		SashForm fileForm = new SashForm( sashForm, SWT.HORIZONTAL );
		
		projectMenu = new ProjectTreeMenu( fileForm, projectDir, this );
		
		docTabs = new CTabFolder( fileForm, SWT.CLOSE | SWT.TOP | SWT.BORDER ); // | SWT.FLAT
		
		fileForm.setWeights( new int[]{ 1, 4 } );
		
		CTabFolder consoleTabs = new CTabFolder( sashForm, SWT.TOP | SWT.BORDER ); // | SWT.FLAT
		
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
		
		docTabs.setBorderVisible(true);
		// appears on mouse-over
		docTabs.setUnselectedCloseVisible( true );
		// makes them less ugly
		docTabs.setSimple( true );

		addDocTabsListeners( );
		
		// set up console tabs
		consoleTabs.setSimple( true );
		consoleTabs.setMaximizeVisible( true );
		consoleTabs.setMinimizeVisible( true );
		consoleTabs.setData( null );
		
		// resizing behaviours
		addConsoleTabHandlers( consoleTabs );
		

		docTabs.setSelection( 0 );
		
		buildConsole = new BuildConsoleTab( consoleTabs );
		serialTerminal = new SerialTerminalTab( consoleTabs );
		
		consoleTabs.setSelection( 0 );
		
	}
	
	public void updateTree( ) {
		projectMenu.update( );
	}
	
	public void saveSelectedTab( ) {
		DocumentTab selTab = getFocussedDocumentTab();
		if ( selTab != null ) {
			selTab.save( );
		}
	}

	public void saveAsSelectedTab( ) {
		DocumentTab selTab = getFocussedDocumentTab();
		if ( selTab != null ) {
			selTab.saveAs( );
		}	
	}
	
	public void saveAll( ) {
		Enumeration<DocumentTab> docEnum = documents.elements();
		while ( docEnum.hasMoreElements( ) ) {
			DocumentTab tab = docEnum.nextElement( );
			tab.save( );
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
				for ( File file : projectPath.listFiles() ) {
					if ( file.getName().equals( Application.projectFileName ) ) {
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
		ProjectSettingsDialog settingsDialog = new ProjectSettingsDialog( shell, projectPath );
		settingsDialog.open( );
	}
	
	public void find( ) {
		DocumentTab tab = getFocussedDocumentTab( );
		if ( tab != null ) {
			tab.find( );
		}
	}
	
	public void findNext( ) {
		DocumentTab tab = getFocussedDocumentTab( );
		if ( tab != null ) {
			tab.findNext( );
		}
	}
	
	public void findPrev( ) {
		DocumentTab tab = getFocussedDocumentTab( );
		if ( tab != null ) {
			tab.findPrev( );
		}
	}
	
	public ArrayList<DocumentTab> getUnsaved( ) {
		ArrayList<DocumentTab> needSaving = new ArrayList<DocumentTab>( );
		Enumeration<DocumentTab> docEnum = documents.elements( );
		while ( docEnum.hasMoreElements( ) ) {
			DocumentTab tab = docEnum.nextElement( );
			if ( !tab.isSaved( ) ) {
				needSaving.add( tab );
			}
		}
		
		return needSaving;
	}
	
	public void openDocument( File fileToOpen ) {
		DocumentTab tab;
		if ( fileToOpen.getName().equals( Application.projectFileName ) ) {
			
			boolean openPrefs = MessageDialog.openQuestion( shell, "Opening Settings", "This is a project settings file.\nDo you want to open the settings dialog instead?" );
			if ( openPrefs ) {
				openProjectSettings( fileToOpen.getParentFile() );
				return;
			}
		}
		
		
		if ( documents.containsKey( fileToOpen ) ) {
			tab = documents.get( fileToOpen );
		} else {
			System.out.println("making tab");
			tab = new DocumentTab( this, docTabs, fileToOpen );
			documents.put( fileToOpen, tab );
		}
		
		tab.setFocus( );
	}
	
	public boolean canQuit( ) {
		return closeProject( );
	}
	
	public MenuBar getMenuBar( ) {
		return menuBar;
	}
	
	public boolean closeProject( ) {
		boolean canClose = true;
			
		Enumeration<DocumentTab> docEnum = documents.elements( );
		while ( docEnum.hasMoreElements( ) ) {
			DocumentTab tab = docEnum.nextElement( );
			File key = tab.getFile();
			if ( !tab.close( ) ) {
				canClose = false;
				break;
			} else {
				documents.remove( key );
			}
		}
		
		projectMenu.setPath( null );
		
		return canClose;
	}
	
	public void renameDocument( File file, File newFile ) {
		DocumentTab tab = documents.remove( file );
		if ( tab != null ) {
			tab.rename( newFile );
			documents.put( newFile, tab );
		}
	}

	
	public boolean closeDocument( DocumentTab tabToClose ) {
		File file = tabToClose.getFile();
		boolean closed = tabToClose.close( );
		if ( closed ) {
			documents.remove( file );
		}
		return closed;
	}
	
	public void finishCommand( int id ) {
		// SYNC
		
		final Runnable resetBuildButton = new Runnable() {
			public void run( ) {
				projectMenu.getBuildButton().setData( false );
				projectMenu.getBuildButton().setImage( images.get( "build" ) );
				menuBar.setBuildEnabled( true );
			}
		};
		
		final Runnable resetDeployButton = new Runnable() {
			public void run( ) {
				projectMenu.getDeployButton().setData( false );
				projectMenu.getDeployButton().setImage( images.get( "deploy" ) );
				menuBar.setDeployEnabled( true );
			}
		};
		
		switch ( id ) {
			case BUILD:
				getDisplay().syncExec( resetBuildButton );
				break;
			case DEPLOY:
				getDisplay().syncExec( resetDeployButton );
				break;
		}
	}
	
	public void build( ) {
		JSONObject settings = getProjectSettings( );
		
		if ( settings != null ) {
			try {
				JSONArray cmds = settings.getJSONObject( "commands" ).getJSONArray( "Build" );
				
				ArrayList<String> commands = new ArrayList<String>( );
				for ( int i = 0; i < cmds.length(); i++ ) {
					commands.add( cmds.getString(i) );
				}

				buildRunner = new CmdRunner( commands, buildConsole, this, BUILD );
				buildRunner.setActionName( "Building" );
				projectMenu.getBuildButton().setImage( images.get( "stop" ) );
				projectMenu.getBuildButton().setData( true );
				menuBar.setBuildEnabled( false );
				buildRunner.start( );
				
			} catch ( JSONException e ) {
				MessageDialog.openError( shell, "Missing Information", "Build information missing. Please check the project settings." );
			}
		}
	}
	
	
	public void deploy( ) {
		if ( serialTerminal.shouldDisconnectBeforeDeployment() ) {
			serialTerminal.disconnect();
		}
		
		JSONObject settings = getProjectSettings( );
		
		if ( settings != null ) {
			try {
				JSONArray cmds = settings.getJSONObject( "commands" ).getJSONArray( "Deploy" );
				
				ArrayList<String> commands = new ArrayList<String>( );
				for ( int i = 0; i < cmds.length(); i++ ) {
					commands.add( cmds.getString(i) );
				}

				deployRunner = new CmdRunner( commands, buildConsole, this, DEPLOY );
				deployRunner.setActionName( "Deploying" );
				projectMenu.getDeployButton().setImage( images.get( "stop" ) );
				projectMenu.getDeployButton().setData( true );
				menuBar.setDeployEnabled( false );
				deployRunner.start( );
				
			} catch ( JSONException e ) {
				MessageDialog.openError( shell, "Missing Information", "Deploy information missing. Please check the project settings." );
			}
		}
	}
	
	public void addImage( String key, Image image ) {
		images.put( key, image );
	}
	
	public Image getImage( String key ) {
		return images.get( key );
	}
	
	
	// Don't touch these, See finishCommand
	public void stopDeploy( ) {
		if ( deployRunner != null ) {
			deployRunner.kill( );
		}
	}
	
	public void stopBuild( ) {
		if ( buildRunner != null ) {
			buildRunner.kill( );
		}
	}
	
	public void openTerminalApp( ) {
		if ( termWindow == null || termWindow.getShell() == null || termWindow.getShell().isDisposed() ) {
			termWindow = new BirdTerm(null);
        	termWindow.setBlockOnOpen(false);
        	termWindow.open();
		} else {
			termWindow.getShell().setFocus();
		}
	}
	
	private JSONObject getProjectSettings() {
		String settings;
		JSONObject jsonSettings;
		File settingsFile = new File( getPath(), Application.projectFileName );
		if ( settingsFile.canRead() ) {
			settings = FileIO.readFile( settingsFile );
			try {
				jsonSettings = new JSONObject( settings );
			} catch (JSONException e) {
				e.printStackTrace();
				jsonSettings = null;
			}
		} else {
			jsonSettings = null;
			MessageDialog.openError( shell, "Unable to Read", "Unable to read settings file. Please check the project settings." );
		}
		
		return jsonSettings;
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
		shell.setText( Application.getInstance( ).getName( ) );
		
		
		
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
	
	public boolean isDisposed( ) {
		return shell.isDisposed( );
	}
}
