package app.project.navigation;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;

import org.eclipse.swt.custom.SashForm;

import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;

import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;

import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.program.Program;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


import app.Application;
import app.ImageManager;
import app.dialogs.AppSettingsDialog;
import app.dialogs.FilenameDialog;
import app.plugin.PluginManager;
import app.project.ProjectWindow;
import app.project.navigation.FileBrowserToolBar;

public class ProjectTreeMenu {
	private GridLayout grid;
	private Composite widgetArea;
	private Tree treeView;
	private File path;
	private Listener openListener;
	private Label statusBar;
	private FileBrowserToolBar toolBar;
	private HashMap<File, Boolean> openDirs;
	private Menu popupMenu;
	
	ProjectWindow project;
	
	public ProjectTreeMenu( SashForm fileForm, File path, ProjectWindow project ) {
		openDirs = new HashMap<File, Boolean>( );
		
		this.project = project;
		
		grid = new GridLayout( 1, true );
		widgetArea = new Composite( fileForm, SWT.NONE );
		
		grid.marginHeight = 0;
		grid.marginWidth  = 0;
		grid.marginBottom = 0;
		grid.marginLeft   = 0;
		grid.marginRight  = 0;
		grid.marginTop    = 0;
		
		widgetArea.setLayout( grid );
		
		// reload project, new project, open project, project properties, create dir, create file, 
		
		treeView = new Tree( widgetArea, SWT.BORDER | SWT.SINGLE );
		GridData treeViewData = new GridData( SWT.FILL, SWT.FILL, true, true );
		treeView.setLayoutData( treeViewData );
		
		toolBar = new FileBrowserToolBar( project, this, widgetArea, SWT.FLAT | SWT.HORIZONTAL );
		GridData toolBarData = new GridData( SWT.FILL, SWT.LEFT, true, false );
		toolBar.setLayoutData( toolBarData );
		
		createPopupMenu( );
		
		this.path = path;
		addHandlers( );
		update( );
	}
	
	public Tree getTreeWidget( ) {
		return treeView;
	}
	
	public FileBrowserToolBar getToolBar( ) {
		return this.toolBar;
	}
	
	public File getSelectedFile( ) {
		File selFile = null;
		
		TreeItem[] items = treeView.getSelection();
		if ( path != null && items != null && items.length == 1 && items[0] != null) {
			selFile = (File)items[0].getData();
		}
		
		return selFile;
	}
	
	public File getSelectedDir( ) {

		File parent = null;
		
		TreeItem[] items = treeView.getSelection();
		if ( path != null && items != null && items.length == 1 && items[0] != null) {
			File file = (File)items[0].getData();
			if ( file != null && file.isDirectory() ) {
				parent = file;
			} else if ( file != null && file.isFile() ){
				parent = file.getParentFile( );
			}
		}
		
		return parent;
	}
	
	
	// Create Popup Menu
	private void createPopupMenu( ) {
		Menu menu = new Menu( treeView );
		menu.setEnabled( false );
		
		MenuItem rename = new MenuItem( menu, SWT.PUSH );
		rename.setText( "Rename file" );
		rename.addSelectionListener( new SelectionListener( ) {
			public void widgetDefaultSelected(SelectionEvent e) {
			}
			public void widgetSelected(SelectionEvent e) {
				renameFile( getSelectedFile( ) );
			} 
		} );
		
		MenuItem openExtern = new MenuItem( menu, SWT.PUSH );
		openExtern.setText( "Open externally" );
		openExtern.addSelectionListener( new SelectionListener( ) {
			public void widgetDefaultSelected(SelectionEvent e) {
			}
			public void widgetSelected(SelectionEvent e) {
				launchFileExternally( getSelectedFile( ) );
			} 
		} );
		
		MenuItem openFolder = new MenuItem( menu, SWT.PUSH );
		openFolder.setText( "Open containing folder" );		
		openFolder.addSelectionListener( new SelectionListener( ) {
			public void widgetDefaultSelected(SelectionEvent e) {
			}
			public void widgetSelected(SelectionEvent e) {
				openContainingDir( getSelectedFile( ) );
			} 
		} );
		
		new MenuItem( menu, SWT.SEPARATOR );
		
		MenuItem newFile = new MenuItem( menu, SWT.PUSH );
		newFile.setText( "Create new file" );
		newFile.addSelectionListener( new SelectionListener( ) {
			public void widgetDefaultSelected(SelectionEvent e) {
			}
			public void widgetSelected(SelectionEvent e) {
				createFile( getSelectedDir( ) );
			} 
		} );
		
		MenuItem newFolder = new MenuItem( menu, SWT.PUSH );
		newFolder.setText( "Create new folder" );
		newFolder.addSelectionListener( new SelectionListener( ) {
			public void widgetDefaultSelected(SelectionEvent e) {
			}
			public void widgetSelected(SelectionEvent e) {
				createDir( getSelectedDir( ) );
			} 
		} );
		
		MenuItem delFile = new MenuItem( menu, SWT.PUSH );
		delFile.setText( "Delete file" );
		delFile.addSelectionListener( new SelectionListener( ) {
			public void widgetDefaultSelected(SelectionEvent e) {
			}
			public void widgetSelected(SelectionEvent e) {
				deleteFile( getSelectedFile( ) );
			} 
		} );
		
		treeView.setMenu( menu );
		popupMenu = menu;
	}
	
	// Popup Menu Drivers
	
	public void deleteFile( File file ) {
		Shell shell = widgetArea.getShell();
		
		if ( file == null ) return;
		if ( !file.canWrite() ) {
			MessageDialog.openError( shell, "Unable to delete", 
					"The selected file cannot be deleted.");
		}
		
		boolean isConfirmed = MessageDialog.openConfirm( shell, "Confirm Deletion",
				"Are you sure you want to delete the file " + file.getName() + "? This action cannot be undone." );
		if ( isConfirmed ) {
			if ( !file.delete() ) {
				MessageDialog.openError( shell, "Unable to delete", "Unable to delete " + file.getName( ) );
			}
		}
		
		update( );
	}
	
	public void createDir( File parent ) {
		if ( parent == null ) {
			parent = project.getPath( );
		}
		
		if ( parent != null ) {
			FilenameDialog fnDialog = new FilenameDialog( widgetArea.getShell( ) );
			String newFilename = fnDialog.open( );
			File newFile = new File( parent, newFilename );
			if ( !newFile.mkdir( ) ) {
				MessageDialog.openError( widgetArea.getShell( ), "Error", "Unable to create folder" );
			}
		}
		
		update( );
	}
	
	public void createFile( File parent ) {
		if ( parent == null ) {
			parent = this.path;
		}
		
		if ( parent != null ) {
			FilenameDialog fnDialog = new FilenameDialog( widgetArea.getShell( ) );
			String newFilename = fnDialog.open( );
			File newFile = new File( parent, newFilename );
			try {
				newFile.createNewFile( );
			} catch (IOException e) {
				MessageDialog.openError( widgetArea.getShell( ), "Error", "Unable to create file" );
				e.printStackTrace();
			}
		}
		
		update( );
	}
	
	public void renameFile( File file ) {
		if ( file == null ) return;
		
		Shell shell = widgetArea.getShell();
		
		if ( !file.canRead() || file.getParentFile() == null ) {
			MessageDialog.openError( shell, "Unable to rename", "Cannot rename " + file.getName() );
			return;
		}
		
		
		FilenameDialog fnDialog = new FilenameDialog( shell, file.getName() );
		String newName = fnDialog.open();
		File newFile = new File( file.getParentFile(), newName );
		
		if ( newFile.exists() ) {
			boolean canContinue = MessageDialog.openConfirm( shell, "File already exists",
					"A file named " + newFile.getName( ) + " already exists in this folder, Overwrite this file?" );
			if ( !canContinue ) {
				return;
			}
		}
		
		file.renameTo( newFile );
		project.renameDocument( file, newFile );
		update( );
	}
	
	public void openContainingDir( File file ) {
		if ( file == null ) return;
		
		if ( file.getParent() != null ) {
			Program.launch( file.getParent( ) );
		}
	}
	
	public void launchFileExternally( File file ) {
		if ( file == null ) return;
		
		Program.launch( file.getAbsolutePath( ) );
	}
	
	public void setOpenListener( Listener listener ) {
		openListener = listener;
	}
	
	public void setStatusWidget( Label label ) {
		statusBar = label;
	}
	
	public void openFile( File file ) {
		
		if ( openListener != null ) {
			Event e = new Event();
			e.widget = treeView;
			e.data = file;
			openListener.handleEvent( e );
			//System.out.println( "done opening file" );
		}
	}
	
	private void setStatus( String text ) {
		if ( statusBar != null ) {
			statusBar.setText( text );
		}
	}
	
	private void displayFiles( File fileParent, TreeItem itemParent ) {
		
		ImageManager images = Application.getInstance().getImageManager();
		
		if ( fileParent == null ) {
			return;
		}
		
		File dir = fileParent;
		File[] list = dir.listFiles();
		if ( list != null ) {
			for ( int i = 0; i < list.length; i++ ) {
				if ( list[i] == null || !list[i].canRead() ) {
					continue;
				}
				
				TreeItem subItem;
				
				if ( itemParent != null ) {
					subItem = new TreeItem( itemParent, SWT.NONE );
				} else {
					subItem = new TreeItem( this.treeView, SWT.NONE );
				}
				
				String fileName = list[i].getName();
				
				if ( list[i].isHidden() ) {
					subItem.setForeground( new Color(this.treeView.getDisplay(), 180, 180, 180) );
				}
				
				if ( list[i].isDirectory() ) {
					
					subItem.setBackground( new Color(this.treeView.getDisplay(), 245, 245, 255) );
					subItem.setImage( images.getImage("folder") );
					
					if ( shouldBeOpen( list[i] ) ) {
						// item should be open, expand it on load
						displayFiles( list[i], subItem );
						subItem.setExpanded( true );
					} else {
						// dummy element to lazily open the tree
						TreeItem dummy = new TreeItem( subItem, SWT.NONE );
						dummy.setData( null );
					}
					
				} else if ( list[i].isFile() ){
					
					String name = list[i].getName( );
					String[] parts = name.split( "\\." );
					
					Image extIcon = null;
					if ( parts.length > 1 ) {
						String ext = parts[parts.length-1];
						extIcon = images.getIconForExtension( ext );
					} 
					
					if ( extIcon == null ) {
						extIcon = images.getImage( "defaultIcon" );
					}
					
					subItem.setImage( extIcon );
				}
				
				subItem.setData( list[i] );
				subItem.setText( fileName );
				
			}
		}
	}
	

	
	private void handleNoProjectMenu( TreeItem item ) {
		if ( treeView.getItem( 0 ) == item ) {
			project.openProject( );
		} else if ( treeView.getItem( 1 ) == item ) {
			project.newProject( );
		} else {
			if ( item.getData() != null ) {
				File recentProjectPath = (File) item.getData( );
				project.openProject( recentProjectPath );
			}
		}
	}
	
	private void displayOpenMenu( ) {
		TreeItem openOption = new TreeItem( this.treeView, SWT.NONE );
		openOption.setText( "Open an existing project" );
		openOption.setImage( new Image( treeView.getDisplay(), "project_explore.png" ) );
		
		TreeItem newOption = new TreeItem( this.treeView, SWT.NONE );
		newOption.setText( "Create a new project" );
		newOption.setImage( new Image( treeView.getDisplay(), "project_new.png" ) );
		
		JSONObject appSettings = PluginManager.getAppInstanceSettings( );
		
		try {
			if ( appSettings.getBoolean( AppSettingsDialog.SHOW_RECENT) ) {
				new TreeItem( this.treeView, SWT.NONE );
			
				populateRecentProjects( );
			}
		} catch ( JSONException e ) {
			e.printStackTrace();
		}
	}
	
	private void populateRecentProjects( ) {
		ImageManager images = Application.getInstance().getImageManager();
		JSONArray recentList;
		
		try {
			recentList = PluginManager.getAppInstanceSettings( ).getJSONArray( "Recent List" );
			ArrayList<String> recentFiles = PluginManager.jsonArrayToStringList( recentList );
			
			if ( !recentFiles.isEmpty() ) {
				
				TreeItem recentProjects = new TreeItem( this.treeView, SWT.NONE );
				recentProjects.setText( "Recent Projects:" );
			
				for ( String recentFilename : recentFiles ) {
					File recentFile = new File( recentFilename );
	
					if ( recentFile.exists() && recentFile.isDirectory() ) {
						TreeItem newFilenameItem = new TreeItem( this.treeView, SWT.NONE );
						newFilenameItem.setText( recentFile.getName() );
						newFilenameItem.setData( recentFile );
						newFilenameItem.setImage( images.getImage( "folder" ) );
					}
				}
			}
			
		} catch ( JSONException e ) {
			try {
				PluginManager.getAppInstanceSettings( ).put( "Recent List", new JSONArray( ) );
				PluginManager.saveAppInstanceSettings( );
			} catch ( JSONException e2 ) {
				e2.printStackTrace( );
			}
		}
	}
	
	public void update( ) {
		setStatus( "" );
		
		if ( treeView.getItems() != null ) {
			for ( TreeItem item : treeView.getItems() ) {
				item.dispose( );
			}
		}
		
		if ( path != null ) {
			// project loaded
			popupMenu.setEnabled( true );
			
			try {
				displayFiles( path, null );
			} catch ( Exception e ) {
				e.printStackTrace();
			} finally {
				toolBar.setEnabled( true );
				project.setProjectEnabled( true );
			}
		} else {
			// no project loaded
			popupMenu.setEnabled( false );
			
			toolBar.setEnabled( false );
			project.setProjectEnabled( false );
			
			displayOpenMenu( );
		}
		
		/*
		try {
			filePath.setText( path.getCanonicalPath() );
		} catch( IOException e ) {
			e.printStackTrace();
		}
		*/
	}
	
	private void addHandlers( ) {
		
		treeView.addListener( SWT.Expand, new Listener() {
			public void handleEvent(Event e) {
				TreeItem item = (TreeItem)e.item;
				if ( item.getItemCount() > 0 && item.getItem(0).getData() == null ) {
					item.getItem(0).dispose(); // remove dummy
					File dirPath = (File)item.getData();
					displayFiles( dirPath, item );
					if ( dirPath != null ) {
						openDirs.put( dirPath, true );
					}
				}
			}
		} );
		
		treeView.addListener( SWT.Collapse, new Listener() {
			public void handleEvent(Event e) {
				TreeItem item = (TreeItem)e.item;
				File dirPath = (File)item.getData();
				
				if ( dirPath != null ) {
					openDirs.remove( dirPath );
				}
			}
		} );
		
		treeView.addSelectionListener( new SelectionListener() {
			public void widgetDefaultSelected(SelectionEvent e) {
			}
			public void widgetSelected(SelectionEvent e) {
				try {
					if ( PluginManager.getAppInstanceSettings().getBoolean( AppSettingsDialog.SINGLE_CLICK ) ) {
						TreeItem[] items = ((Tree)e.widget).getSelection();
						handleSelection( items );
					}
				} catch (JSONException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				//statusBar.setText( "Double-click to open" );
			}
		});
		
		treeView.addMouseListener( new MouseListener() {
			public void mouseDoubleClick( MouseEvent e ) {
				if ( e.button != 1 ) {
					return;
				}
				
				try {
					if ( !PluginManager.getAppInstanceSettings().getBoolean( AppSettingsDialog.SINGLE_CLICK ) ) {
						TreeItem[] items = ((Tree)e.widget).getSelection();
						handleSelection( items );
					}
				} catch (JSONException e1) {
					// by default, double-click opens
					TreeItem[] items = ((Tree)e.widget).getSelection();
					handleSelection( items );
				}
			}

			public void mouseDown(MouseEvent e) {
				// mouse down on treeView
				
			}

			public void mouseUp(MouseEvent e) {
				// mouse up on treeView
				
			}
			
		} );
	}
	
	private void handleSelection( TreeItem items[] ) {
		setStatus( "" );
		

		if ( items != null && items.length == 1 && items[0] != null) {
			TreeItem item = items[0];
			treeView.setFocus( );
			treeView.update();
			
			if ( path != null ) {
				File file = (File)item.getData();
				
				if ( file != null && file.isDirectory() ) {
					/*
					try {
						
						
						if ( file.canRead() ) {
							
							//path = file;
							//updateTree( );
						} else {
							setStatus( "Selected Folder is Unreadable" );
						}
					} catch ( SecurityException ex ) {
						setStatus( "Permission Denied" );
					}*/
					
					
				} else if ( file != null && file.isFile() ){
					// Open selected file on double-click
					openFile( file );
				}
			} else {
				// New/Create menu click
				handleNoProjectMenu( item );
			}
		}
	}
	
	private boolean shouldBeOpen( File path ) {
		return ( openDirs.containsKey( path ) && openDirs.get( path ) );
	}
	
	public void setPath( File path ) {
		this.path = path;
		update( );
	}

	public File getPath( ) {
		return this.path;
	}
}
