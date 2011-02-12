package app.project.navigation;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;

import app.plugin.interfaces.IToolBar;

public class TopToolbar implements IToolBar {
	private ToolBar toolBarWidget;
	
	public TopToolbar( Composite parent, int style ) {
		toolBarWidget = new ToolBar( parent, style );
		
	}
	
	public ToolBar getWidget( ) {
		return toolBarWidget;
	}
	
	public void setEnabled( boolean enabled ) {
		this.toolBarWidget.setEnabled( enabled );
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
		ToolItem separator = new ToolItem( toolBarWidget, SWT.SEPARATOR );
		separator.setWidth( width );
	}
	
	public void addSeparator( ) {
		new ToolItem( toolBarWidget, SWT.SEPARATOR );
	}

	public void setLayoutData( GridData toolBarData ) {
		this.toolBarWidget.setLayoutData( toolBarData );
	}
	
	public void addSpacer( int width ) {
		// SWT you are so hacky
		Label spacer = new Label( toolBarWidget, SWT.NONE );
		ToolItem separator = new ToolItem( toolBarWidget, SWT.SEPARATOR );
		separator.setWidth( width );
		separator.setControl( spacer );
	}


}

