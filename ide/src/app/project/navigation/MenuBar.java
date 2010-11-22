package app.project.navigation;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import app.Application;
import app.CocoaUIEnhancer;
import app.project.ProjectWindow;
import app.project.document.DocumentTab;

public class MenuBar {
	private ProjectWindow projectWindow;
	
	private MenuItem fileNewProjItem;
	//private MenuItem fileNewItem;
	private MenuItem fileOpenProjItem;
	private MenuItem fileCloseItem;
	private MenuItem fileCloseTabItem;
	private MenuItem fileSaveItem;
	private MenuItem fileSaveAsItem;
	private MenuItem fileSaveAllItem;
	private MenuItem fileExitItem;
	
	private MenuItem editUndoItem;
	private MenuItem editRedoItem;
	
	private MenuItem editCutItem;
	private MenuItem editCopyItem;
	private MenuItem editPasteItem;
	private MenuItem editSelectAllItem;
	
	private MenuItem editFindItem;
	private MenuItem editFindNextItem;
	private MenuItem editFindPrevItem;
	
	private MenuItem editPreferences;
	
	private MenuItem projectSettings;
	private MenuItem projectBuild;
	private MenuItem projectDeploy;
	
	private MenuItem serialAppOpen;

	private static final int CUT = 0;
	private static final int COPY = 1;
	private static final int PASTE = 2;
	private static final int UNDO = 3;
	private static final int REDO = 4;
	private static final int SELECT_ALL = 5;
	
	
	
	public void setProjectEnabled( boolean enabled ) {
		//fileNewItem.setEnabled( enabled );
		fileCloseItem.setEnabled( enabled );
		fileSaveAllItem.setEnabled( enabled );
		projectSettings.setEnabled( enabled );
		projectBuild.setEnabled( enabled );
		projectDeploy.setEnabled( enabled );
	}
	
	public void setDocumentEnabled( boolean enabled ) {
		editRedoItem.setEnabled( enabled );
		editUndoItem.setEnabled( enabled );
		
		editFindItem.setEnabled( enabled );
		editFindNextItem.setEnabled( enabled );
		editFindPrevItem.setEnabled( enabled );
		
		fileSaveItem.setEnabled( enabled );
		fileSaveAsItem.setEnabled( enabled );
		
		if ( enabled ) {
			setTabEnabled( enabled );
		}
	}
	
	public void setTabEnabled( boolean enabled ) {
		fileCloseTabItem.setEnabled( enabled );
	}
	
	private void about( ) {
		MessageDialog.openInformation( projectWindow.getDisplay().getActiveShell(), "About", Application.aboutString );
	}
	
	private void preferences( ) {
		Application.getInstance().showPreferences( );
	}
	
	private boolean quit( ) {
		return projectWindow.canQuit( );
	}
	
	public MenuBar( Shell shell, ProjectWindow projectWindow ) {
		
		Listener quitListener = new Listener( ) {
			public void handleEvent(Event event) {
				event.doit = quit( );
			}
		};
		
		Listener aboutListener = new Listener( ) {
			public void handleEvent(Event event) {
				about( );
			}			
		};

		Listener preferenceListener = new Listener( ) {
			public void handleEvent(Event event) {
				preferences( );
			}			
		};
		
		
		if ( Application.isMac( ) ) {
	        CocoaUIEnhancer enhancer = new CocoaUIEnhancer( Application.appName );
	        enhancer.hookApplicationMenu( shell.getDisplay(), quitListener, aboutListener, preferenceListener );
		}
		
		
		
		
		Menu menu = new Menu( shell, SWT.BAR );
		
		this.projectWindow = projectWindow;
		
		// File Menu
		MenuItem fileMenuItem = new MenuItem( menu, SWT.CASCADE );
		fileMenuItem.setText( "&File" );
		
		Menu fileMenu = new Menu( shell, SWT.DROP_DOWN );
		fileMenuItem.setMenu( fileMenu );
		/*
		fileNewItem = new MenuItem( fileMenu, SWT.PUSH );
		fileNewItem.setText("&New Tab");
		fileNewItem.setAccelerator( SWT.MOD1 | 'N' );
		fileNewItem.setEnabled( false );
		*/
		
		fileNewProjItem = new MenuItem( fileMenu, SWT.PUSH );
		fileNewProjItem.setText( "New Pro&ject" );
		fileNewProjItem.setAccelerator( SWT.MOD2 | SWT.MOD1 | 'N' );
		
		fileOpenProjItem = new MenuItem( fileMenu, SWT.PUSH );
		fileOpenProjItem.setText( "&Open Project" );
		fileOpenProjItem.setAccelerator( SWT.MOD1 | 'O' );
		
		
		new MenuItem( fileMenu, SWT.SEPARATOR );

		fileCloseItem = new MenuItem( fileMenu, SWT.PUSH );
		fileCloseItem.setText("Close &Project");
		fileCloseItem.setAccelerator( SWT.MOD2 | SWT.MOD1 | 'W' );
		fileCloseItem.setEnabled( false );
		
		fileCloseTabItem = new MenuItem( fileMenu, SWT.PUSH );
		fileCloseTabItem.setText("Close &Tab");
		fileCloseTabItem.setAccelerator( SWT.MOD1 | 'W' );
		fileCloseTabItem.setEnabled( false );
		
		fileSaveItem = new MenuItem( fileMenu, SWT.PUSH );
		fileSaveItem.setText("&Save");
		fileSaveItem.setAccelerator( SWT.MOD1 | 'S' );
		fileSaveItem.setEnabled( false );
		
		fileSaveAsItem = new MenuItem( fileMenu, SWT.PUSH );
		fileSaveAsItem.setText("Save &As");
		fileSaveAsItem.setAccelerator( SWT.MOD2 | SWT.MOD1 | 'S' );
		fileSaveAsItem.setEnabled( false );
		
		fileSaveAllItem = new MenuItem( fileMenu, SWT.PUSH );
		fileSaveAllItem.setText("Save A&ll");
		fileSaveAllItem.setAccelerator( SWT.MOD3 | SWT.MOD1 | 'S' );
		

		if ( !Application.isMac( ) ) {
			new MenuItem( fileMenu, SWT.SEPARATOR );

			fileExitItem = new MenuItem(fileMenu, SWT.PUSH );
			fileExitItem.setText("E&xit");
			fileExitItem.setAccelerator( SWT.ALT | SWT.F4 );
		} else {
			fileExitItem = null;
		}
		
		// Edit Menu
		MenuItem editMenuItem = new MenuItem( menu, SWT.CASCADE );
		editMenuItem.setText( "&Edit" );
		
		Menu editMenu = new Menu( shell, SWT.DROP_DOWN );
		editMenuItem.setMenu( editMenu );
		
		editUndoItem = new MenuItem( editMenu, SWT.PUSH );
		editUndoItem.setText( "&Undo" );
		editUndoItem.setAccelerator( SWT.MOD1 | 'Z' );
		editUndoItem.setEnabled( false );
		
		editRedoItem = new MenuItem( editMenu, SWT.PUSH );
		editRedoItem.setText( "&Redo" );
		editRedoItem.setAccelerator( SWT.MOD2 | SWT.MOD1 | 'Z' );
		editRedoItem.setEnabled( false );
		
		new MenuItem( editMenu, SWT.SEPARATOR );
		
		editCutItem = new MenuItem( editMenu, SWT.PUSH );
		editCutItem.setText( "&Cut" );
		editCutItem.setAccelerator( SWT.MOD1 | 'X' );
		//editCutItem.setEnabled( false );
		
		editCopyItem = new MenuItem( editMenu, SWT.PUSH );
		editCopyItem.setText( "C&opy" );
		editCopyItem.setAccelerator( SWT.MOD1 | 'C' );
		//editCopyItem.setEnabled( false );
		
		editPasteItem = new MenuItem( editMenu, SWT.PUSH );
		editPasteItem.setText( "&Paste" );
		editPasteItem.setAccelerator( SWT.MOD1 | 'V' );
		//editPasteItem.setEnabled( false );
		
		new MenuItem( editMenu, SWT.SEPARATOR );
		
		editSelectAllItem = new MenuItem( editMenu, SWT.PUSH );
		editSelectAllItem.setText( "Select &All" );
		editSelectAllItem.setAccelerator( SWT.MOD1 | 'A' );
		
		new MenuItem( editMenu, SWT.SEPARATOR );
		
		editFindItem = new MenuItem( editMenu, SWT.PUSH );
		editFindItem.setText( "&Find" );
		editFindItem.setAccelerator( SWT.MOD1 | 'F' );
		editFindItem.setEnabled( false );
		
		
		editFindNextItem = new MenuItem( editMenu, SWT.PUSH );
		editFindNextItem.setText( "Find &Next" );
		editFindNextItem.setAccelerator( SWT.MOD1 | 'G' );
		editFindNextItem.setEnabled( false );
		
		editFindPrevItem = new MenuItem( editMenu, SWT.PUSH );
		editFindPrevItem.setText( "Find &Previous" );
		editFindPrevItem.setAccelerator( SWT.MOD2 | SWT.MOD1 | 'G' );
		editFindPrevItem.setEnabled( false );
		
		
		new MenuItem( editMenu, SWT.SEPARATOR );
		
		editPreferences = new MenuItem( editMenu, SWT.PUSH );
		editPreferences.setText( "P&references" );
		editPreferences.setAccelerator( SWT.MOD3 | SWT.MOD1 | 'P' );
		
		// Project Menu
		MenuItem projectMenuItem = new MenuItem( menu, SWT.CASCADE );
		projectMenuItem.setText( "&Project" );
		
		Menu projectMenu = new Menu( shell, SWT.DROP_DOWN );
		projectMenuItem.setMenu( projectMenu );
		
		projectSettings = new MenuItem( projectMenu, SWT.PUSH );
		projectSettings.setText( "&Settings" );
		projectSettings.setAccelerator( SWT.MOD1 | 'P' );
		projectSettings.setEnabled( false );
		
		new MenuItem( projectMenu, SWT.SEPARATOR );
		
		projectBuild = new MenuItem( projectMenu, SWT.PUSH );
		projectBuild.setText( "&Build" );
		projectBuild.setAccelerator( SWT.MOD1 | 'B' );
		projectBuild.setEnabled( false );
		
		projectDeploy = new MenuItem( projectMenu, SWT.PUSH );
		projectDeploy.setText( "&Deploy" );
		projectDeploy.setAccelerator( SWT.MOD1 | 'D' );
		projectDeploy.setEnabled( false );
		
		// Serial Menu

		MenuItem serialMenuItem = new MenuItem( menu, SWT.CASCADE );
		serialMenuItem.setText( "&Serial" );
		
		Menu serialMenu = new Menu( shell, SWT.DROP_DOWN );
		serialMenuItem.setMenu( serialMenu );
		
		serialAppOpen = new MenuItem( serialMenu, SWT.PUSH );
		serialAppOpen.setText( "&Open Serial Terminal Window" );
		serialAppOpen.setAccelerator( SWT.MOD1 | 'T' );
		
		
		addHandlers( );
		
		shell.setMenuBar( menu );
	}
	
	private void doCommand( int cmd ) {
		DocumentTab focussedTab = projectWindow.getFocussedDocumentTab( );
		if ( focussedTab != null ) {
			
			if ( cmd == CUT ) {
				focussedTab.getDocument( ).cut( );
			} else if ( cmd == COPY ){
				focussedTab.getDocument( ).copy( );
			} else if ( cmd == PASTE ) {
				focussedTab.getDocument( ).paste( );
			} else if ( cmd == UNDO ) {
				focussedTab.getDocument( ).undo( );
			} else if ( cmd == REDO ) {
				focussedTab.getDocument( ).redo( );
			} else if ( cmd == SELECT_ALL ) {
				focussedTab.getDocument( ).selectAll( );
			}
			
		} else {
			Display display = projectWindow.getDisplay( );
			Control focussedControl = display.getFocusControl();
			if ( focussedControl != null ) {
				if ( focussedControl instanceof StyledText ) {
					
					StyledText textControl = (StyledText)focussedControl;					
					if ( cmd == CUT ) {
						textControl.cut( );
					} else if ( cmd == COPY ) {
						textControl.copy( );
					} else if ( cmd == PASTE ) {
						textControl.paste( );
					} else if ( cmd == SELECT_ALL ) {
						textControl.selectAll( );
					}
					
				} else if ( focussedControl instanceof Combo ) {
					
					Combo comboControl = (Combo)focussedControl;
					if ( cmd == CUT ) {
						comboControl.cut( );
					} else if ( cmd == COPY ) {
						comboControl.copy( );
					} else if ( cmd == PASTE ) {
						comboControl.paste( );
					}
					
				} else if ( focussedControl instanceof Text ) {
					
					Text textControl = (Text)focussedControl;
					
					if ( cmd == CUT ) {
						textControl.cut( );
					} else if ( cmd == COPY ) {
						textControl.copy( );
					} else if ( cmd == PASTE ) {
						textControl.paste( );
					} else if ( cmd == SELECT_ALL ) {
						textControl.selectAll( );
					}
					
				}
			}
		}
	}

	
	private void addHandlers( ) {
		
		// Cut/Copy/Paste/Select All
		
		editCopyItem.addSelectionListener( new SelectionListener() {
			public void widgetDefaultSelected(SelectionEvent e) {

			}
			public void widgetSelected(SelectionEvent e) {
				doCommand( COPY );
			}
		} );
		
		editCutItem.addSelectionListener( new SelectionListener() {
			public void widgetDefaultSelected(SelectionEvent e) {
			}
			public void widgetSelected(SelectionEvent e) {
				doCommand( CUT );
			}
		} );
		
		editPasteItem.addSelectionListener( new SelectionListener() {
			public void widgetDefaultSelected(SelectionEvent e) {
			}
			public void widgetSelected(SelectionEvent e) {
				doCommand( PASTE );
			}
		} );
		
		editRedoItem.addSelectionListener( new SelectionListener() {
			public void widgetDefaultSelected(SelectionEvent e) {
			}
			public void widgetSelected(SelectionEvent e) {
				doCommand( REDO );
			}
		} );
		
		editUndoItem.addSelectionListener( new SelectionListener() {
			public void widgetDefaultSelected(SelectionEvent e) {
			}
			public void widgetSelected(SelectionEvent e) {
				doCommand( UNDO );
			}
		} );
		
		editSelectAllItem.addSelectionListener( new SelectionListener() {
			public void widgetDefaultSelected(SelectionEvent e) {
			}
			public void widgetSelected(SelectionEvent e) {
				doCommand( SELECT_ALL );
			}
		} );
		
		fileNewProjItem.addSelectionListener( new SelectionListener() {
			public void widgetDefaultSelected(SelectionEvent e) {
			}
			public void widgetSelected(SelectionEvent e) {
				projectWindow.newProject( );
			}
		} );
		
		fileOpenProjItem.addSelectionListener( new SelectionListener() {
			public void widgetDefaultSelected(SelectionEvent e) {
			}
			public void widgetSelected(SelectionEvent e) {
				projectWindow.openProject( );
			}
		} );
		
		fileCloseItem.addSelectionListener( new SelectionListener() {
			public void widgetDefaultSelected(SelectionEvent e) {
			}
			public void widgetSelected(SelectionEvent e) {
				e.doit = projectWindow.closeProject( );
			}
		} );
		
		fileCloseTabItem.addSelectionListener( new SelectionListener() {
			public void widgetDefaultSelected(SelectionEvent e) {
			}
			public void widgetSelected(SelectionEvent e) {
				e.doit = projectWindow.closeSelectedTab( );
			}
		} );
		
		fileSaveItem.addSelectionListener( new SelectionListener() {
			public void widgetDefaultSelected(SelectionEvent e) {
			}
			public void widgetSelected(SelectionEvent e) {
				projectWindow.saveSelectedTab( );
			}
		} );
		
		fileSaveAsItem.addSelectionListener( new SelectionListener() {
			public void widgetDefaultSelected(SelectionEvent e) {
			}
			public void widgetSelected(SelectionEvent e) {
				projectWindow.saveAsSelectedTab( );
			}	
		} );
		
		fileSaveAllItem.addSelectionListener( new SelectionListener() {
			public void widgetDefaultSelected(SelectionEvent e) {
			}
			public void widgetSelected(SelectionEvent e) {
				projectWindow.saveAll( );
			}
		} );
		
		
		editPreferences.addSelectionListener( new SelectionListener() {
			public void widgetDefaultSelected(SelectionEvent e) {
			}
			public void widgetSelected(SelectionEvent e) {
				Application.getInstance().showPreferences( );
			}
		} );
		
		editFindItem.addSelectionListener( new SelectionListener() {
			public void widgetDefaultSelected(SelectionEvent e) {
			}
			public void widgetSelected(SelectionEvent e) {
				projectWindow.find( );
			}
		} );
		
		editFindNextItem.addSelectionListener( new SelectionListener() {
			public void widgetDefaultSelected(SelectionEvent e) {
			}
			public void widgetSelected(SelectionEvent e) {
				projectWindow.findNext( );
			}
		} );
		
		editFindPrevItem.addSelectionListener( new SelectionListener() {
			public void widgetDefaultSelected(SelectionEvent e) {
			}
			public void widgetSelected(SelectionEvent e) {
				projectWindow.findPrev( );
			}
		} );
		
		
		projectSettings.addSelectionListener( new SelectionListener() {
			public void widgetDefaultSelected(SelectionEvent e) {
			}
			public void widgetSelected(SelectionEvent e) {
				projectWindow.openProjectSettings( );
			}
		} );
		
		projectBuild.addSelectionListener( new SelectionListener() {
			public void widgetDefaultSelected(SelectionEvent e) {
			}
			public void widgetSelected(SelectionEvent e) {
				projectWindow.build( );
			}
		} );
		
		projectDeploy.addSelectionListener( new SelectionListener() {
			public void widgetDefaultSelected(SelectionEvent e) {
			}
			public void widgetSelected(SelectionEvent e) {
				projectWindow.deploy( );
			}
		} );
		
		serialAppOpen.addSelectionListener( new SelectionListener() {
			public void widgetDefaultSelected(SelectionEvent e) {
			}
			public void widgetSelected(SelectionEvent e) {
				projectWindow.openTerminalApp( );
			}
		} );
		
		if ( fileExitItem != null ) {
		
			fileExitItem.addSelectionListener( new SelectionListener() {
				public void widgetDefaultSelected(SelectionEvent e) {
				}
				public void widgetSelected(SelectionEvent e) {
					projectWindow.quit( );
				}
			} );
		
		}
		
	}
	
	public void setDeployEnabled( boolean enabled ) {
		projectDeploy.setEnabled( enabled );
	}
	
	public void setBuildEnabled( boolean enabled ) {
		projectBuild.setEnabled( enabled );
	}
}
