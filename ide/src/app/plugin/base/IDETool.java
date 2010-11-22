package app.plugin.base;

import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Control;

public abstract class IDETool {
	public String getName( ) {
		return null;
	}

	public Control getControl() {
		return null;
	}

	public Image getIcon() {
		return null;
	}
	
	public void setProjectEnabled( boolean enabled ) {
		
	}
	
	public void setDocumentEnabled( IDEDocument document, boolean enabled ) {
		
	}
}
