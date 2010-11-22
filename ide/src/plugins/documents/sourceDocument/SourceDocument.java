package plugins.documents.sourceDocument;

import java.io.File;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.ITextListener;
import org.eclipse.jface.text.ITextOperationTarget;
import org.eclipse.jface.text.TextEvent;
import org.eclipse.jface.text.rules.FastPartitioner;
import org.eclipse.jface.text.source.CompositeRuler;
import org.eclipse.jface.text.source.LineNumberRulerColumn;
import org.eclipse.jface.text.source.SourceViewer;
import org.eclipse.jface.text.source.SourceViewerConfiguration;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.FileDialog;

import plugins.documents.sourceDocument.syntax.c.CPartitionScanner;
import plugins.documents.sourceDocument.syntax.c.CSourceViewerConfiguration;

import common.FileIO;



import app.Application;
import app.plugin.base.IDEDocument;
import app.plugin.interfaces.IDocumentParent;

public class SourceDocument extends IDEDocument {
	
	private Document document;
	private SourceViewer sourceView;
	private FindReplaceDialog frDialog = null;

	public static String[] getAssociatedExtensions( ) {
		return new String[] { "c", "cpp", "pde", "txt", "xml" };
	}
	
	public SourceDocument( IDocumentParent parent, File file ) {
		super( parent, file );

		this.document = new Document( );
		

		CompositeRuler ruler = new CompositeRuler( 12 );
		LineNumberRulerColumn lineCol = new LineNumberRulerColumn( );
		lineCol.setBackground( new Color( parent.getDisplay(), 250, 250, 250 ) );
		lineCol.setForeground( new Color( parent.getDisplay(), 128, 128, 128 ) );
		
		ruler.addDecorator( 0, lineCol );
		
		Font sourceFont = Application.getInstance().getPreferences().getSourceFont( );
		
		this.sourceView = new SourceViewer( parent.getComposite( ), ruler, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL );
		this.sourceView.getTextWidget().setLeftMargin( 5 );
		this.sourceView.getTextWidget().setFont( sourceFont );
		

		String[] fileParts = file.getName().split("\\.");
		String ext = fileParts[fileParts.length-1];
		
		if ( isHighlightable( ext ) ) {
			SourceViewerConfiguration config = new CSourceViewerConfiguration();
			sourceView.configure( config );
		}
		
		sourceView.getTextWidget().addFocusListener( new FocusListener() {
			public void focusGained(FocusEvent e) {
				setMenuEnabled( true );
				checkFileValidity( );
			}
			public void focusLost(FocusEvent e) {
				setMenuEnabled( false );
			}
		} );
		
		sourceView.addTextListener( new ITextListener() {
			public void textChanged( TextEvent e ) {
				String newText = document.get();
				if ( !newText.equals( lastSavedData ) ) {
					setSaved( false );
				} else {
					setSaved( true );
				}
			}
		});
		
		setFile( this.file );
		
		setSaved( true );
	}
	
	public boolean save( ) {
		boolean isSuccessful = FileIO.writeFile( document.get( ), this.file );
		if ( isSuccessful ) {
			this.lastSavedData = document.get();
		}
		setSaved( isSuccessful );
		return isSuccessful;
	}
	
	public boolean saveAs( ) {
		boolean success = false;
		
		File projectPath = parent.getProjectPath( );
		//open dialog
		FileDialog fd = new FileDialog( parent.getShell( ), SWT.SAVE );
		fd.setText( "Save As" );
		fd.setFilterPath( projectPath.getAbsolutePath() );
		fd.setFilterExtensions( new String[] {"*.*"} );
		fd.setFilterNames( new String[] { "All Files" } );
		String selectedFile = fd.open( );
		File newFile = new File( selectedFile );
		
		if ( newFile != null ) {
			boolean confirm = true;
			if ( newFile.exists() ) {
				confirm = MessageDialog.openQuestion( parent.getShell( ), "File Already Exists", selectedFile + " already exists. Do you want to replace it?" );
			}
			if ( confirm ) {
				success = FileIO.writeFile( document.get( ), newFile );
				if ( success ) {
					parent.openDocument( newFile );
				}
			}
		}
		
		parent.updateFiles( );
		
		return success;
	}
	
	public boolean close( ) {
		boolean canProceed = super.close();
		
		if ( canProceed ) {
			if ( this.frDialog != null && !this.frDialog.isDisposed() ) {
				this.frDialog.close( );
			}
		}
		
		return canProceed;
	}

	public void copy( ) {
		sourceView.doOperation( ITextOperationTarget.COPY );
	}
	
	public void paste( ) {
		sourceView.doOperation( ITextOperationTarget.PASTE );
	}
	
	public void cut( ) {
		sourceView.doOperation( ITextOperationTarget.CUT );
	}
	
	public void selectAll( ) {
		sourceView.doOperation( ITextOperationTarget.SELECT_ALL );
	}
	
	public void undo( ) {
		sourceView.doOperation( ITextOperationTarget.UNDO );
	}
	
	public void redo( ) {
		sourceView.doOperation( ITextOperationTarget.REDO );
	}
	
	public void find( ) {
		if ( frDialog == null || frDialog.isDisposed() ) {
			frDialog = new FindReplaceDialog( parent.getShell(), document, sourceView );
			frDialog.open( );
		}
		
		frDialog.setVisible( true );
	}

	public void findNext( ) {
		if ( frDialog == null || frDialog.isDisposed() ) {
			frDialog = new FindReplaceDialog( parent.getShell(), document, sourceView );
			frDialog.open( );
		} else {
			frDialog.findAction( FindReplaceDialog.FIND_NEXT );
		}
	}
	
	public void findPrev( ) {
		if ( frDialog == null ) {
			frDialog = new FindReplaceDialog( parent.getShell(), document, sourceView );
			frDialog.open( );
		} else {
			frDialog.findAction( FindReplaceDialog.FIND_PREV );
		}		
	}

	public void setFile( File file ) {
		this.file = file;
		String content = FileIO.readFile( this.file );
		document = new Document( content );
		
		CPartitionScanner scanner = new CPartitionScanner( );
		FastPartitioner partitioner = new FastPartitioner( scanner, CPartitionScanner.PARTITION_TYPES );
		partitioner.connect( document );
		document.setDocumentPartitioner( partitioner );
		        
		sourceView.setDocument( document );
		lastSavedData = content;
		setSaved( true );
	}

	public void setFocus( ) {
		parent.setSelection( this );
		StyledText textWidget = sourceView.getTextWidget( );
		if ( textWidget != null ) {
			textWidget.setFocus( );
		}
	}
	
	public void selected( ) {
		checkFileValidity( );
	}
	
	private boolean isHighlightable( String ext ) {
		return ( ext.equals("c") || ext.equals("pde") || ext.equals("cpp") || ext.equals("h") );
	}
	
	public Control getControl( ) {
		return sourceView.getControl( );
	}
	
	public Control getMainWidget( ) {
		return sourceView.getTextWidget( );
	}

}
