package app.project.document;

import java.io.File;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.ITextListener;
import org.eclipse.jface.text.ITextOperationTarget;
import org.eclipse.jface.text.TextEvent;
import org.eclipse.jface.text.source.CompositeRuler;
import org.eclipse.jface.text.source.LineNumberRulerColumn;
import org.eclipse.jface.text.source.SourceViewer;
import org.eclipse.jface.text.source.SourceViewerConfiguration;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.FileDialog;

import files.FileIO;

import app.Application;
import app.project.ProjectWindow;

import syntax.c.CSourceViewerConfiguration;

public class DocumentTab extends CTabItem {

	private SourceViewer sourceView;
	private Document document;
	private CTabFolder parent;
	private File file;
	private String lastSave;
	private Image unsavedImage, savedImage;
	private ProjectWindow projectWindow;
	
	private FindReplaceDialog frDialog = null;
	
	boolean unsaved;
	
	private boolean isHighlightable( String ext ) {
		return ( ext.equals("c") || ext.equals("pde") || ext.equals("cpp") || ext.equals("h") );
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
	
	public DocumentTab( ProjectWindow projectWindow, CTabFolder parent, File file ) {
		super( parent, SWT.BORDER );
		
		this.projectWindow = projectWindow;
		this.document = new Document( );
		this.parent = parent;
		this.file = file;
		
		unsavedImage = new Image( this.getDisplay(), "unsaved.gif" );
		savedImage = new Image( this.getDisplay(), "saved.gif" );
		
		//item.setImage(image);
		this.setText( file.getName() );
		
		//IOverviewRuler overviewRuler = new OverviewRuler( null, 12, EditorsPlugin.getDefault().getSharedTextColors() );
		
		CompositeRuler ruler = new CompositeRuler( 12 );
		LineNumberRulerColumn lineCol = new LineNumberRulerColumn( );
		lineCol.setBackground( new Color( this.getDisplay(), 250, 250, 250 ) );
		lineCol.setForeground( new Color( this.getDisplay(), 128, 128, 128 ) );
		
		ruler.addDecorator( 0, lineCol );
		
		Font sourceFont = Application.getInstance().getPreferences().getSourceFont( );
		
		sourceView = new SourceViewer( parent, ruler, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL );
		sourceView.getTextWidget().setLeftMargin( 5 );
		
		sourceView.getTextWidget().setFont( sourceFont );
		
		String[] fileParts = file.getName().split("\\.");
		String ext = fileParts[fileParts.length-1];
		
		if ( isHighlightable( ext ) ) {
			SourceViewerConfiguration config = new CSourceViewerConfiguration();
			sourceView.configure( config );
		}
		
		sourceView.getTextWidget().addFocusListener( new FocusListener() {
			public void focusGained(FocusEvent e) {
				enableMenuBar( true );
				checkFileValidity( );
				
			}
			public void focusLost(FocusEvent e) {
				enableMenuBar( false );
			}
		} );
		sourceView.addTextListener( new ITextListener() {
			public void textChanged( TextEvent e ) {
				String newText = document.get();
				if ( !newText.equals( lastSave ) ) {
					setUnsaved( true );
				} else {
					setUnsaved( false );
				}
			}
		});
		
		this.setControl( sourceView.getControl() );
		
		updateFile( );
		
		setUnsaved( false );
	}
	
	private void enableMenuBar( boolean enabled ) {
		projectWindow.getMenuBar( ).setDocumentEnabled( enabled );
	}
	
	private void setUnsaved( boolean isUnsaved ) {
		if ( isUnsaved ) {
			this.setImage( unsavedImage );
			//this.setText( "*" + file.getName() );
			unsaved = true;
		} else {
			this.setImage( savedImage );
			//this.setText( file.getName() );
			unsaved = false;
		}
	}
	
	public boolean isSaved( ) {
		return ( lastSave.equals( document.get( ) ) ); 
	}

	
	public File getFile( ) {
		return this.file;
	}
	
	public void save( ) {
		FileIO.writeFile( document.get( ), this.file );
		lastSave = document.get();
		setUnsaved( false );
	}
	
	public void saveAs( ) {
		File projectPath = projectWindow.getPath( );
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
				FileIO.writeFile( document.get( ), newFile );
				projectWindow.openDocument( newFile );
			}
		}
		
		projectWindow.updateTree( );
	}

	public void rename( File newFile ) {
		this.file = newFile;
		setText( newFile.getName() );
	}
	
	public boolean close( ) {
		boolean canProceed = true;
		
		if ( !isSaved( ) ) {
			parent.setSelection( this );
			parent.getShell( ).setActive( );
			
			canProceed = MessageDialog.openConfirm( parent.getShell( ), "Unsaved File", "There are unsaved changes in file: " + getText() + "\nDo you want to continue? (Changes will be lost)" );
			/*
			MessageBox messageBox = new MessageBox( parent.getShell( ), SWT.ICON_WARNING | SWT.OK | SWT.CANCEL );
			messageBox.setText( "Unsaved File" );
			messageBox.setMessage( "There are unsaved changes in file: " + getText() + "\nDo you want to continue? (Changes will be lost)" );
			
			int buttonID = messageBox.open();
			
			if ( buttonID == SWT.CANCEL ) {
				canProceed = false;
			}
			*/
		}
		
		if ( canProceed ) {
			
			if ( this.frDialog != null && !this.frDialog.isDisposed() ) {
				this.frDialog.close( );
			}
			
			this.dispose( );
		}

		return canProceed;
	}
	
	
	public StyledText getTextWidget( ) {
		return sourceView.getTextWidget( );
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
	
	public void updateFile( ) {
		String content = FileIO.readFile( this.file );
		document = new Document( content );
		sourceView.setDocument( document );
		lastSave = content;
		setUnsaved( false );
	}
	
	
	
	public Document getDocument( ) {
		return document;
	}
	
	public int getIndex( ) {
		return parent.indexOf( this );
	}
	
	public void setSelection( ) {
		parent.setSelection( getIndex() );
	}
	

	public void setFocus( ) {
		setSelection( );
		StyledText textWidget = getTextWidget( );
		if ( textWidget != null ) {
			textWidget.setFocus( );
		}
	}
	
	private void promptFileChanged( ) {
		parent.setSelection( this );
		boolean canUpdate = MessageDialog.openConfirm( parent.getShell( ), "File Changed", "This file has been changed externally.\nUpdate this file to the new version?\n(This will lose unsaved changes)" );
		
		if ( canUpdate ) {
			updateFile( );
		}
	}

	public void checkFileValidity() {
		String currentData = FileIO.readFile( this.file );
		if ( !currentData.equals( lastSave ) ) {			
			promptFileChanged( );
		} else {
			//System.out.println( "valid" );
		}
	}
	
}
