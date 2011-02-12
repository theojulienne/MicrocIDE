package plugins.toolTabs.console;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.ToolItem;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import plugins.toolTabs.serial.SerialTerminal;

import app.plugin.base.PluginTool;
import app.plugin.interfaces.IMenuBar;
import app.plugin.interfaces.IPlugin;
import app.plugin.interfaces.IToolBar;
import app.plugin.interfaces.parents.IToolParent;

public class BuildConsole extends PluginTool {

	private static final String BuildPreferenceText = "Build";
	private static final String DeployPreferenceText = "Deploy";
	private static final int BUILD  = 1;
	private static final int DEPLOY = 2;

	private CmdRunner deployRunner;
	private CmdRunner buildRunner;
	
	private Font font;
	private StyledText console;
	private String consoleText;
	private ArrayList<StyleRange> ranges;
	private int latestStart, latestLength;
	private String latestLine;
	
	private Image icon;
	private String name;
	
	private ToolItem buildButton;
	private ToolItem deployButton;
	
	private Image buildIcon;
	private Image deployIcon;
	private Image stopIcon;
	
	private MenuItem buildMenuItem;
	private MenuItem deployMenuItem;
	
	private IToolParent parent;
	private IPlugin plugin;
	private Map<String, String> environment;
	
	
	public BuildConsole( IPlugin plugin, IToolParent parent ) {
		this.plugin = plugin;
		this.parent = parent;
		
		ranges = new ArrayList<StyleRange>( );
		
		icon = new Image( parent.getDisplay(), "build_small.png" );
		name = "Build Console";
		
		console = new StyledText( parent.getComposite( ), SWT.BORDER | SWT.V_SCROLL | SWT.WRAP );
		console.setEditable( false );
		
		String fontFace;
		
		if ( parent.isMac( ) ) {
			fontFace = "Monaco";
		} else {
			fontFace = "Courier";
		}
		
		int fontSize = 12;
		
		JSONObject appSettings = parent.getPluginAppSettings( plugin );
		
		try {
			JSONObject fontSettings = appSettings.getJSONObject( "Console Font" );
			fontFace = fontSettings.getString( "Font Face" );
			fontSize = fontSettings.getInt( "Font Size" );
		} catch ( JSONException e ) {
			// not required
		}
		
		font = new Font( parent.getDisplay(), fontFace, fontSize, SWT.NONE );
		console.setFont( font );
		consoleText = "";
		console.setText( consoleText );
		console.setMargins( 5, 5, 5, 5 );
		
		buildIcon  = new Image( parent.getDisplay(), "build.png"  );
		deployIcon = new Image( parent.getDisplay(), "deploy.png" );
		stopIcon   = new Image( parent.getDisplay(), "stop.png"   );
		
		IToolBar toolBar = parent.getToolBar( );
		
		SelectionListener buildSelection  = new SelectionListener() {
			public void widgetDefaultSelected( SelectionEvent e ) {
			}
			public void widgetSelected( SelectionEvent e ) {
				Boolean building = (Boolean) e.widget.getData();
				
				if ( building != null && building) {
					stopBuild( );
				} else {
					build( );
				}
			}
		};
		
		SelectionListener deploySelection = new SelectionListener() {
			public void widgetDefaultSelected( SelectionEvent e ) {
			}
			public void widgetSelected( SelectionEvent e ) {

				Boolean deploying = (Boolean) e.widget.getData();
				if ( deploying != null && deploying) {
					stopDeploy( );
				} else {
					deploy( );
				}
			}
		};
		
		toolBar.addSpacer( 20 );
		
		buildButton = toolBar.addToolItem( buildIcon, "Build", "Build Project", buildSelection );
		
		toolBar.addSpacer( 10 );
		
		deployButton = toolBar.addToolItem( deployIcon, "Deploy", "Deploy Project", deploySelection );

		
		IMenuBar menuBar = parent.getMenuBar( );
		Menu buildMenu = menuBar.addNewMenu( "Build" );
		
		buildMenuItem = new MenuItem( buildMenu, SWT.PUSH );
		buildMenuItem.setText( "&Build" );
		buildMenuItem.setAccelerator( SWT.MOD1 | 'B' );
		buildMenuItem.setEnabled( false );
		buildMenuItem.addSelectionListener( buildSelection );
		
		deployMenuItem = new MenuItem( buildMenu, SWT.PUSH );
		deployMenuItem.setText( "&Deploy" );
		deployMenuItem.setAccelerator( SWT.MOD1 | 'D' );
		deployMenuItem.setEnabled( false );
		deployMenuItem.addSelectionListener( deploySelection );
	}
	
	public void setProjectEnabled( boolean enabled ) {
		buildMenuItem.setEnabled( enabled );
		deployMenuItem.setEnabled( enabled );
		buildButton.setEnabled( enabled );
		deployButton.setEnabled( enabled );
	}
	
	public Control getControl( ) {
		return this.console;
	}
	
	public Image getIcon( ) {
		return icon;
	}
	
	public String getName( ) {
		return name;
	}
	
	public void addLine( String line ) {
		latestLine = line + "\n";		
		consoleText += latestLine;
		
		final Runnable updateText = new Runnable() {
			public void run() {
				console.append( latestLine );
				console.setSelection( consoleText.length() );
		    }
		};
		console.getDisplay( ).syncExec( updateText );
	}
	
	public void addColorLine( String line, Color color ) {
		
		int start = consoleText.length();
		int length = line.length( );
		
		latestLine = line + "\n";
		consoleText += latestLine;
		
		final Runnable updateText = new Runnable() {
			public void run() {
				console.append( latestLine );
		    }
		};
		
		console.getDisplay( ).syncExec( updateText );
		
		StyleRange style = new StyleRange();
		style.start = start;
		style.length = length;
		style.foreground = color;
		latestStart = start;
		latestLength = length;
		
		ranges.add( style );
		
		final Runnable updateStyle = new Runnable() {
			public void run() {
				console.replaceStyleRanges( latestStart, latestLength, (StyleRange[]) ranges.toArray( new StyleRange[ranges.size()] ) );
				console.setSelection( latestStart + latestLength + 1  );
		    }
		};
		
		console.getDisplay( ).syncExec( updateStyle );

	}
	
	public void addInfoLine( String line ) {
		Color infoCol = new Color( console.getDisplay( ), 0, 0, 230 );
		addColorLine( line, infoCol );
	}
	
	public void addErrLine( String line ) {
		Color errCol = new Color( console.getDisplay( ), 230, 0, 0 );
		addColorLine( line, errCol );
	}
	

	public void finishCommand( int id ) {
		// called from syncexec
		
		final Runnable resetBuildButton = new Runnable() {
			public void run( ) {
				buildButton.setData( false );
				buildButton.setImage( buildIcon );
				buildMenuItem.setEnabled( true );
			}
		};
		
		final Runnable resetDeployButton = new Runnable() {
			public void run( ) {
				deployButton.setData( false );
				deployButton.setImage( deployIcon );
				deployMenuItem.setEnabled( true );
				
				SerialTerminal terminal = null;
				for ( PluginTool tool : parent.getTools() ) {
					if ( tool instanceof SerialTerminal ) {
						terminal = (SerialTerminal)tool;
					}
				}
				if ( terminal != null ) {
					if ( terminal.didDisconnectBeforeDeployment() ) {
						terminal.autoConnect();
					}
				}
			}
		};
		
		switch ( id ) {
			case BUILD:
				((Display) parent.getDisplay( )).syncExec( resetBuildButton );
				break;
			case DEPLOY:
				((Display) parent.getDisplay( )).syncExec( resetDeployButton );
				break;
		}
	}
	
	public void setEnvironment( ) {
		JSONObject projSettings = parent.getPluginProjectSettings( plugin );
		
		Map<String, String> env = new HashMap<String, String>( );
		JSONObject jsonEnv;
		try {
			jsonEnv = projSettings.getJSONObject( BuildProjectPreferences.ENV_KEY );
		
			Iterator<String> keys = jsonEnv.keys( );
			
			while ( keys.hasNext( ) ) {
				String key = keys.next( );
				env.put( key, jsonEnv.getString( key ) );
			}
			

			this.environment = env;
		} catch ( JSONException e ) {
			this.environment = System.getenv( );
		}
	}
	
	public void build( ) {
		setEnvironment( );
		
		parent.setActiveTool( this );
		try {
			JSONArray cmds = parent.getPluginProjectSettings( this.plugin ).getJSONArray( BuildPreferenceText );
			
			ArrayList<String> commands = new ArrayList<String>( );
			for ( int i = 0; i < cmds.length(); i++ ) {
				commands.add( cmds.getString(i) );
			}

			buildRunner = new CmdRunner( commands, environment, this, parent.getProjectPath(), BUILD );
			buildRunner.setActionName( "Building with" );
			buildButton.setImage( stopIcon );
			buildButton.setData( true );
			buildMenuItem.setEnabled( false );
			buildRunner.start( );
			
		} catch ( JSONException e ) {
			MessageDialog.openError( parent.getShell( ), "Missing Information", "Build information missing. Please check the project settings." );
		}
	}
	
	
	public void deploy( ) {
		setEnvironment( );
		
		parent.setActiveTool( this );
		
		SerialTerminal terminal = null;
		for ( PluginTool tool : parent.getTools() ) {
			if ( tool instanceof SerialTerminal ) {
				terminal = (SerialTerminal)tool;
			}
		}
		if ( terminal != null ) {
			if ( terminal.shouldDisconnectBeforeDeployment() ) {
				terminal.deployDisconnect();
			}
		}
		
		try {
			JSONArray cmds = parent.getPluginProjectSettings( this.plugin ).getJSONArray( DeployPreferenceText );
			
			ArrayList<String> commands = new ArrayList<String>( );
			for ( int i = 0; i < cmds.length(); i++ ) {
				commands.add( cmds.getString(i) );
			}

			deployRunner = new CmdRunner( commands, environment, this, parent.getProjectPath(), DEPLOY );
			deployRunner.setActionName( "Deploying with" );
			deployButton.setImage( stopIcon );
			deployButton.setData( true );
			deployMenuItem.setEnabled( false );
			deployRunner.start( );
			
		} catch ( JSONException e ) {
			MessageDialog.openError( parent.getShell( ), "Missing Information", "Deploy information missing. Please check the project settings." );
		}
	}
	
	
	// Don't touch these, See finishCommand
	public void stopDeploy( ) {
		if ( deployRunner != null ) {
			deployRunner.kill( );
		}
	}
	
	public void stopBuild( ) {
		if ( buildRunner != null ) {
			buildRunner.kill( );
		}
	}
}
