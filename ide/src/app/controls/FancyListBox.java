package app.controls;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

public class FancyListBox extends Composite {

	private EditableList itemList;
	private Text newItemText;
	private Button addButton;
	private Button delButton;
	private Shell shell;
	
	public String[] getItems( ) {
		return itemList.getItemStrings( );
	}
	
	public void add( String item ) {
		itemList.add( item );
	}
	
	public void reset( ) {
		newItemText.setText( "" );
		itemList.removeAll( );
	}
	
	private void createUI( ) {
		this.setLayout( new GridLayout( 3, false ) );
		
		shell = this.getShell( );
		itemList = new EditableList( this, SWT.BORDER | SWT.SINGLE );
		
		GridData listData = new GridData( SWT.FILL, SWT.FILL, true, true );
		listData.minimumHeight = 100;
		listData.minimumWidth  = 200;
		listData.horizontalSpan = 3;
		itemList.setLayoutData( listData );
		
		newItemText = new Text( this, SWT.BORDER | SWT.SINGLE );
		newItemText.setLayoutData( new GridData( SWT.FILL, SWT.FILL, true, false ) );
		newItemText.setText( "" );
		
		GridData buttonData = new GridData( 20, 20 );
		
		addButton = new Button( this, SWT.FLAT );
		addButton.setText( "+" );
		addButton.setLayoutData( buttonData );
		addButton.addSelectionListener( new SelectionListener( ) {
			public void widgetDefaultSelected(SelectionEvent arg0) {
			}
			public void widgetSelected(SelectionEvent arg0) {
				itemList.add( newItemText.getText() );
				newItemText.setText( "" );
			}
		} );
		
		delButton = new Button( this, SWT.FLAT );
		delButton.setText( "-" );
		delButton.setLayoutData( buttonData );
		delButton.addSelectionListener( new SelectionListener( ) {
			public void widgetDefaultSelected( SelectionEvent e ) {
			}
			public void widgetSelected( SelectionEvent e ) {
				if ( itemList.getSelectionCount() > 0 ) {
					if ( MessageDialog.openConfirm( shell, "Remove Item", "Are you sure you want to remove the selected item?") ) {
						itemList.remove( itemList.getSelectionIndex() );
					}	
				}
			}
		} );
	}
	
	public FancyListBox( Composite parent, int style ) {
		super(parent, style);
		createUI( );
	}

}
