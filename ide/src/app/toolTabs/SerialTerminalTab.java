package app.toolTabs;

// TODO: local echo

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

import boldinventions.ansi_console.AnsiCharColors;
import boldinventions.ansi_console.serial.AnsiSerialConsole;
import boldinventions.birdterm.AppSettings;


public class SerialTerminalTab extends CTabItem {
	private Image connectImg, disconnectImg;

    private AppSettings m_appSettings;
    private AnsiSerialConsole console;
    
    private Button connectButton;
    private Button disconnectDeploy;
    
	public SerialTerminalTab( CTabFolder parent ) {
		super( parent, SWT.BORDER );
		
		this.connectImg = new Image( parent.getDisplay(), "connect.png" );
		this.disconnectImg = new Image( parent.getDisplay(), "disconnect.png" );
		
		this.setImage( disconnectImg );
		this.setText( "Serial Terminal" );
		
		Composite container = new Composite( parent, SWT.BORDER );
		GridLayout layout = new GridLayout( 5, false );
		layout.marginHeight = 0;
		layout.marginWidth = 0;
		container.setLayout( layout );
		
		/*
		StyledText console = new StyledText( container, SWT.BORDER | SWT.V_SCROLL | SWT.WRAP );
		console.setEditable( false );
		GridData consoleData = new GridData( SWT.FILL, SWT.FILL, true, true, 5, 1 );
		console.setLayoutData( consoleData );
		
		
		console.setText( "Serial Terminal" );
		console.setMargins( 5, 5, 5, 5 );
		*/
		

	    m_appSettings = new AppSettings();

	    AnsiCharColors.create( container.getDisplay() );
	      
		console = new AnsiSerialConsole( container, SWT.None, 
		          m_appSettings.getNRowsTextConDoc(),
		          m_appSettings.getNColsTextConDoc(),
		          m_appSettings.getIColorTextBackground(),
		          m_appSettings.getIColorTextForeground(),
		          m_appSettings.getLastSerialPortSettings(),
		          m_appSettings.getAutoConnect()
		        );
		
		Font font;
		if ( app.Application.isMac() ) {
			font = new Font( console.getDisplay(), new FontData("Monaco", 12, SWT.BOLD) );
		} else {
	    	font = new Font( console.getDisplay(), new FontData("Courier New", 12, SWT.BOLD) );
	    }
		
		GridData consoleData = new GridData( SWT.FILL, SWT.FILL, true, true, 5, 1 );
		console.setLayoutData( consoleData );
		
        // Set it to a non-proportional font
        console.setFont( font );
        
        // Set Status Message updater
        // console.addStatusTextListener(this);
        
        console.setLocalEcho(m_appSettings.getLocalEcho());
		
		/*
		Combo device = new Combo( container, SWT.DROP_DOWN | SWT.BORDER );
		device.add( "/dev/cu.penguino-avr" );
		device.add( "COM 3" );
		device.add( "/dev/ttyACM0" );
		device.setText( "Serial Device" );
		
		GridData deviceGridData = new GridData();
		device.setLayoutData( deviceGridData );
		
		Combo baud = new Combo( container, SWT.DROP_DOWN | SWT.BORDER );
		baud.add( "110" );
		baud.add( "300" );
		baud.add( "600" );
		baud.add( "1200" );
		baud.add( "2400" );
		baud.add( "4800" );
		baud.add( "9600" );
		baud.add( "14400" );
		baud.add( "19200" );
		baud.add( "28800" );
		baud.add( "38400" );
		baud.add( "56000" );
		baud.add( "57600" );
		baud.add( "115200" );
		baud.setText( "Baud Rate" );
		*/
		
        Button localEchoButton = new Button( container, SWT.CHECK );
        localEchoButton.setText( "Local Echo" );
        localEchoButton.addSelectionListener( new SelectionListener() {			
			public void widgetSelected(SelectionEvent evt) {
				Button echoButton = (Button)evt.widget;
				console.setLocalEcho( echoButton.getSelection() );
			}
			public void widgetDefaultSelected(SelectionEvent evt) {
			}
		} );
        
		Label spacer = new Label( container, SWT.NONE );
		GridData spacerGridData = new GridData( SWT.FILL, SWT.CENTER, true, false, 2, 1 );
		spacer.setLayoutData( spacerGridData );
		
		disconnectDeploy = new Button( container, SWT.CHECK );
		disconnectDeploy.setText( "Disconnect before deployment" );
		
		connectButton = new Button( container, SWT.FLAT );
		connectButton.setText( "Connect" );
		connectButton.setImage( connectImg );
		connectButton.addSelectionListener( new SelectionListener() {
			public void widgetDefaultSelected(SelectionEvent e) {
			}
			public void widgetSelected(SelectionEvent e) {
				if ( console.isPortOpen() ) {
					console.CloseSerialPort( );
					connectButton.setText( "Connect" );
					connectButton.setImage( connectImg );
				} else {
					console.setAutoConnect(m_appSettings.getAutoConnect());
					m_appSettings.setLastSerialPortSettings(console.OpenAndSetUpSerialPort());
					if( console.isPortOpen() ) {
						connectButton.setImage( disconnectImg );
						connectButton.setText( "Disconnect" );
					}
                }
			}
			
		} );
		
		this.setControl( container );
		
	}
	
	public boolean shouldDisconnectBeforeDeployment( ) {
		return disconnectDeploy.getSelection();
	}
	
	public void disconnect( ) {
		if ( console.isPortOpen() ) {
			console.CloseSerialPort( );
		}

		connectButton.setText( "Connect" );
		connectButton.setImage( connectImg );
	}

}
