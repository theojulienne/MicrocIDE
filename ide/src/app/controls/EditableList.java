package app.controls;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.TableEditor;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.ControlListener;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;

public class EditableList {

	private TableEditor editor;
	private Table table;
	
	public EditableList( Composite parent, int style ) {
		table = new Table( parent, style );

		editor = new TableEditor( table );
		editor.horizontalAlignment = SWT.LEFT;
	    editor.grabHorizontal = true;
	    
	    table.addControlListener( new ControlListener( ) {
			public void controlResized(ControlEvent arg0) {
				editor.minimumWidth = table.getClientArea().width - 6;
			}
			public void controlMoved(ControlEvent arg0) {
			}
		});
	    
	    table.addSelectionListener( new SelectionAdapter() {
	    	public void widgetSelected( SelectionEvent e ) {
	    		// Clean up any previous editor control
	    		Control oldEditor = editor.getEditor( );
	    		if ( oldEditor != null && !oldEditor.isDisposed() ) {
	    			oldEditor.setVisible( false );
	    			oldEditor.dispose();
	    		}
			   
	    		// Identify the selected row
	    		TableItem item = (TableItem) e.item;
	    		if ( item == null ) {
	    			return;
	    		}

	    		// The control that will be the editor must be a child of the
	    		// Table
	    		Text newEditor = new Text( table, SWT.NONE );
	    		newEditor.setText( item.getText( ) );
	    		newEditor.addModifyListener( new ModifyListener() {
	    			public void modifyText( ModifyEvent me ) {
	    				Text text = (Text) editor.getEditor();
	    				editor.getItem().setText( text.getText() );
	    			}
	    		});
			   
	    		newEditor.selectAll();
	    		newEditor.setFocus();
	    		editor.setEditor( newEditor, item, 0 );

	    	}
	    } );
	}
	
	public void add( String text ) {
		TableItem newItem = new TableItem( table, SWT.NONE );
		newItem.setText( new String[]{ text } );
	}
	
	public void remove( int index ) {

		Control editControl = editor.getEditor();
		if ( editControl != null && !editControl.isDisposed() ) {
			editControl.setVisible( false );
			editControl.dispose();
		}
		
		table.remove( index );
	}
	
	public void addMouseListener( MouseListener listener ) {
		table.addMouseListener( listener );
	}
	
	public void setLayoutData( Object data ) {
		table.setLayoutData( data );
	}
	
	public void removeAll( ) {
		table.removeAll( );
	}
	
	public int getSelectionCount( ) {
		return table.getSelectionCount( );
	}
	
	public int getSelectionIndex( ) {
		return table.getSelectionIndex( );
	}
	
	private String[] tableItemArrayToStringArray( TableItem[] tableItems ) {
		String[] itemStrings = new String[tableItems.length];
		
		int i = 0;
		for ( TableItem tableItem : tableItems ) {
			itemStrings[i] = tableItem.getText( );
			i++;
		}
		
		return itemStrings;
	}
	
	public void setItemString( int index, String string ) {
		table.getItem( index ).setText( string );
	}
	
	public String[] getSelectionStrings( ) {
		return tableItemArrayToStringArray( table.getSelection( ) );
	}
	
	public String[] getItemStrings( ) {
		return tableItemArrayToStringArray(  table.getItems( ) );
	}

}
