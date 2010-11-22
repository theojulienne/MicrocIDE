package app.plugin.interfaces;

import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.ToolItem;

public interface IToolBar {
	ToolItem addToolItem( Image icon, String toolTip, SelectionListener selectionListener );
	void addSeparator( );
}
