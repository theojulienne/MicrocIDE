package app.project.navigation;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.ToolItem;

import app.Application;
import app.ImageManager;
import app.plugin.interfaces.IToolBar;
import app.project.ProjectWindow;

public class FileBrowserToolBar implements IToolBar {

	private ImageManager images;


	private ProjectWindow project;
	private ProjectTreeMenu treeMenu;
	private org.eclipse.swt.widgets.ToolBar toolBarWidget;
	
	public FileBrowserToolBar( ProjectWindow project, ProjectTreeMenu treeMenu, Composite parent, int style ) {
		toolBarWidget = new org.eclipse.swt.widgets.ToolBar( parent, style );
		
		this.project  = project;
		this.treeMenu = treeMenu;
		
		images = Application.getInstance( ).getImageManager( );

		images.addImage( "project settings", "project_settings.png" );
		images.addImage( "new folder", "folder_add.png" );
		images.addImage( "reload", "reload.png" );
		images.addImage( "new file", "file_add.png" );

		ToolItem reloadButton = new ToolItem( toolBarWidget, SWT.PUSH );
		reloadButton.setImage( images.getImage( "reload" ) );
		reloadButton.setToolTipText( "Reload Project" );
		reloadButton.addSelectionListener( new SelectionListener() {
			public void widgetDefaultSelected(SelectionEvent e) {
			}
			public void widgetSelected(SelectionEvent e) {
				update( );
			}
		} );
		
		new ToolItem( toolBarWidget, SWT.SEPARATOR );
		
		ToolItem projectSettingsButton = new ToolItem( toolBarWidget, SWT.PUSH );
		projectSettingsButton.setImage( images.getImage( "project settings") );
		projectSettingsButton.setToolTipText( "Project Settings" );
		projectSettingsButton.addSelectionListener( new SelectionListener() {
			public void widgetDefaultSelected(SelectionEvent e) {
			}
			public void widgetSelected(SelectionEvent e) {
				openProjSettings( );
			}
		} );
		
		new ToolItem( toolBarWidget, SWT.SEPARATOR );
		
		ToolItem createDirButton = new ToolItem( toolBarWidget, SWT.PUSH );
		createDirButton.setImage( images.getImage( "new folder" ) );
		createDirButton.setToolTipText( "New Folder" );
		createDirButton.addSelectionListener( new SelectionListener() {
			public void widgetDefaultSelected(SelectionEvent e) {
			}
			public void widgetSelected(SelectionEvent e) {
				createDir( );
			}
		} );
		
		ToolItem createFileButton = new ToolItem( toolBarWidget, SWT.PUSH );
		createFileButton.setImage( images.getImage( "new file") );
		createFileButton.setToolTipText( "New File" );
		createFileButton.addSelectionListener( new SelectionListener() {
			public void widgetDefaultSelected(SelectionEvent e) {
			}
			public void widgetSelected(SelectionEvent e) {
				createFile( );
			}
		} );
		
	}
	
	public void setEnabled( boolean enabled ) {
		this.toolBarWidget.setEnabled( enabled );
	}

	private void update( ) {
		treeMenu.update( );
	}
	
	private void createDir( ) {
		treeMenu.createDir( treeMenu.getSelectedDir() );
	}
	
	private void createFile( ) {
		treeMenu.createFile( treeMenu.getSelectedDir( ) );
	}
	
	private void openProjSettings( ) {
		project.openProjectSettings( );
	}
	
	
	
	
	public ToolItem addToolItem( Image image, String text, String toolTip, SelectionListener selectionListener ) {
		ToolItem item = addToolItem( image, toolTip, selectionListener );
		item.setText( text );
		return item;
	}
	
	public ToolItem addToolItem( Image image, String toolTip,
			SelectionListener selectionListener) {
		ToolItem customItem = new ToolItem( toolBarWidget, SWT.PUSH );
		customItem.setImage( image );
		customItem.setToolTipText( toolTip );
		customItem.addSelectionListener( selectionListener );
		
		return customItem;
	}
	
	public void addSeparator( int width ) {
		new ToolItem( toolBarWidget, SWT.SEPARATOR ).setWidth( width );
	}
	
	public void addSeparator( ) {
		new ToolItem( toolBarWidget, SWT.SEPARATOR );
	}

	public void setLayoutData(GridData toolBarData) {
		this.toolBarWidget.setLayoutData( toolBarData );
	}

	public void addSpacer( int width ) {
		Label spacer = new Label( toolBarWidget, SWT.NONE );
		ToolItem separator = new ToolItem( toolBarWidget, SWT.SEPARATOR );
		separator.setWidth( width );
		separator.setControl( spacer );
	}
}
