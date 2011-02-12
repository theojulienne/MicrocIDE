package app.toolTab;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabItem;

import app.plugin.base.PluginTool;

public class ToolTab extends CTabItem {
	private PluginTool tool;
	
	public ToolTab( ToolTabFolder parent, PluginTool tool ) {
		super( parent, SWT.BORDER );
		setTool( tool );
		parent.setSelection( this );
	}
	
	public PluginTool getTool( ) {
		return this.tool;
	}
	
	public void setTool( PluginTool tool ) {
		this.tool = tool;
		
		this.setText( tool.getName() );
		updateIcon( );
		
		this.setControl( tool.getControl() );
	}
	
	public void updateIcon( ) {
		this.setImage( tool.getIcon() );
	}
}
