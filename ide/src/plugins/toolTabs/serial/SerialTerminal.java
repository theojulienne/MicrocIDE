package plugins.toolTabs.serial;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;

import boldinventions.ansi_console.AnsiCharColors;
import boldinventions.ansi_console.serial.AnsiSerialConsole;
import boldinventions.ansi_console.serial.ExceptionAnsiSerialConsole;
import boldinventions.birdterm.AppSettings;
import boldinventions.birdterm.BirdTerm;
import app.plugin.base.PluginTool;
import app.plugin.interfaces.IMenuBar;
import app.plugin.interfaces.IPlugin;
import app.plugin.interfaces.parents.IToolParent;



public class SerialTerminal extends PluginTool {

	private Image connectImg, disconnectImg;

    private AppSettings m_appSettings;
    private AnsiSerialConsole console;
    
    private Button connectButton;
    private Button disconnectDeploy;

    private Image icon;
    
    private Composite container;

	private boolean deployDisconnect;

	private IToolParent parent;
	
	private BirdTerm termWindow;

	private MenuItem serialAppOpen;
    
	public String getName( ) {
		return "Serial Terminal";
	}

	public Control getControl() {
		return container;
	}

	public Image getIcon() {
		return icon;
	}

	public void openTerminalApp( ) {
		if ( termWindow == null || termWindow.getShell() == null || termWindow.getShell().isDisposed() ) {
			termWindow = new BirdTerm( null );
        	termWindow.setBlockOnOpen( false );
        	termWindow.open( );
		} else {
			termWindow.getShell().setFocus();
		}
	}
    
	public SerialTerminal( IPlugin plugin, IToolParent parent ) {
		this.parent = parent;	
		
		
		
		IMenuBar menuBar = this.parent.getMenuBar();
		
		Menu serialMenu = menuBar.addNewMenu( "&Serial" );
		
		serialAppOpen = new MenuItem( serialMenu, SWT.PUSH );
		serialAppOpen.setText( "&Open Serial Terminal Window" );
		serialAppOpen.setAccelerator( SWT.MOD1 | 'T' );
		
		serialAppOpen.addSelectionListener( new SelectionListener() {
			public void widgetDefaultSelected(SelectionEvent e) {
			}
			public void widgetSelected(SelectionEvent e) {
				openTerminalApp( );
			}
		} );
		
		this.connectImg = new Image( parent.getDisplay(), "connect.png" );
		this.disconnectImg = new Image( parent.getDisplay(), "disconnect.png" );
		
		this.icon = disconnectImg;
		
		container = new Composite( parent.getComposite(), SWT.BORDER );
		GridLayout layout = new GridLayout( 5, false );
		layout.marginHeight = 0;
		layout.marginWidth = 0;
		container.setLayout( layout );
		

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
			font = new Font( console.getDisplay(), new FontData("Monaco", 12, SWT.NONE) );
		} else {
	    	font = new Font( console.getDisplay(), new FontData("Courier New", 12, SWT.NONE) );
	    }
		
		GridData consoleData = new GridData( SWT.FILL, SWT.FILL, true, true, 5, 1 );
		console.setLayoutData( consoleData );
		
        // Set it to a non-proportional font
        console.setFont( font );
        
        // Set Status Message updater
        // console.addStatusTextListener(this);
        
        console.setLocalEcho(m_appSettings.getLocalEcho());
		
        Button localEchoButton = new Button( container, SWT.CHECK );
        localEchoButton.setText( "Local Echo" );
        localEchoButton.setSelection( m_appSettings.getLocalEcho( ) );
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
		GridData buttonData = new GridData( );
		buttonData.minimumWidth = 120;
		buttonData.widthHint = 120;
		connectButton.setLayoutData( buttonData );
		connectButton.setImage( connectImg );
		connectButton.addSelectionListener( new SelectionListener() {
			public void widgetDefaultSelected(SelectionEvent e) {
			}
			public void widgetSelected(SelectionEvent e) {
				deployDisconnect = false;
				if ( console.isPortOpen() ) {
					disconnect( );
				} else {
					connect( );
                }
			}
			
		} );
		
	}
	
	public boolean didDisconnectBeforeDeployment( ) {
		return this.deployDisconnect;
	}
	
	public void deployDisconnect( ) {
		if ( console.isPortOpen() ) {
			this.deployDisconnect = true;
			disconnect( );
		}
	}
	
	public boolean shouldDisconnectBeforeDeployment( ) {
		return disconnectDeploy.getSelection( );
	}
	
	public void disconnect( ) {
		if ( console.isPortOpen() ) {
			console.CloseSerialPort( );
		}
		
		connectButton.setText( "Connect" );
		connectButton.setImage( connectImg );
		icon = disconnectImg;
		parent.updateIcon( this );
	}

	public void autoConnect() {
		parent.setActiveTool( this );
		try {
			console.tryToOpenPortWithoutCreatingDialog( m_appSettings.getLastSerialPortSettings( ) );
			doConnectUpdate( );
		} catch ( ExceptionAnsiSerialConsole e ) {
			connect( );
		}
	}
	
	public void connect() {
		console.setAutoConnect(m_appSettings.getAutoConnect());
		m_appSettings.setLastSerialPortSettings(console.OpenAndSetUpSerialPort());
		doConnectUpdate( );
	}
	
	private void doConnectUpdate( ) {
		if( console.isPortOpen() ) {
			connectButton.setImage( disconnectImg );
			connectButton.setText( "Disconnect" );
			icon = connectImg;
			parent.updateIcon( this );
		}
	}

}
