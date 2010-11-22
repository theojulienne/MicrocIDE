package plugins.documents.imageDocument;

import java.io.File;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

import app.plugin.base.IDEDocument;
import app.plugin.interfaces.IDocumentParent;

public class ImageDocument extends IDEDocument {

	private Image image;
	private Canvas canvas;
	private PaintListener paintListener;
	private Composite container;
	
	public static String[] getAssociatedExtensions( ) {
		return new String[] { "jpg", "jpeg", "gif", "png", "bmp" };
	}
	
	public ImageDocument( IDocumentParent parent, File file ) {
		super( parent, file );
		
		container = new Composite( parent.getComposite(), SWT.NONE );
		container.setLayout( new FillLayout() );
		canvas = new Canvas( container, SWT.NO_REDRAW_RESIZE | SWT.BORDER );
		
		setFile( file );
	}
	
	public void setFile( File file ) {
		image = new Image( parent.getDisplay( ), file.getAbsolutePath() );
		
		// wipe out any existing paint listener
		if ( paintListener != null ) {
			canvas.removePaintListener( paintListener );
		}
		
		// create a new one for the new image
		paintListener = new PaintListener() {
			public void paintControl(PaintEvent e) {
				int imgWidth  = image.getBounds().width;
				int imgHeight = image.getBounds().height; 
				
				int dominantImageDimension  = Math.min( imgWidth, imgHeight );
				int dominantClientDimension = Math.min( canvas.getClientArea().width, canvas.getClientArea().height );
				
				float scale = (float)dominantImageDimension / (float)dominantClientDimension;
				
				int width  = Math.min( imgWidth, (int)(imgWidth / scale) );
				int height = Math.min( imgHeight, (int)(imgHeight / scale) );
				

				int x = canvas.getClientArea().width/2  - width/2;
				int y = canvas.getClientArea().height/2 - height/2;
				
				e.gc.drawImage( image, 0, 0, imgWidth, imgHeight, x, y, width, height );
			}
		};
		
		// add this paint listener
		canvas.addPaintListener( paintListener );
	}

	public Control getControl( ) {
		return container;
	}
	
	public Control getMainWidget( ) {
		return canvas;
	}
	
	public void selected( ) {
		
	}
}
