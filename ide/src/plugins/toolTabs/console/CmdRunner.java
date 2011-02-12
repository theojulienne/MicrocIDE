package plugins.toolTabs.console;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;


public class CmdRunner extends Thread {
	private ArrayList<String> commands;
	private BuildConsole console;
	private File projectPath;
	private String actionName;
	private boolean running;
	private Process proc;
	private int id;
	private Map<String, String> environment;
	
	public CmdRunner( ArrayList<String> commands, Map<String,String> environment, BuildConsole console, File projectPath, int id ) {
		this.console = console;
		this.commands = commands;
		this.projectPath = projectPath;
		actionName = "Running";
		this.id = id;
		this.environment = environment;
	}
	
	public void setActionName( String name ) {
		this.actionName = name;
	}
	
	public String getActionName( ) {
		return actionName;
	}
	
	public void kill( ) {
		running = false;
		proc.destroy( );
	}
	
	public void run( ) {
		running = true;
		
		String line = null;
		String errLine = null;
		
		for ( String cmd : commands ) {

			// For each command in the list of commands
			
			// open a process for this command
			try {
				
				ProcessBuilder procBuilder = new ProcessBuilder( Arrays.asList( cmd.split( " " ) ) );
				procBuilder.directory( projectPath );
				Map<String,String> env = procBuilder.environment();
				
				for ( Map.Entry<String, String> entry : environment.entrySet( ) ) {
					String name = entry.getKey();
					String value = entry.getValue();
					
					env.put( name, value );
				}
				
				proc = procBuilder.start( );
				//proc = Runtime.getRuntime().exec( cmd, null, projectPath );
			} catch ( IOException e ) {
				console.addErrLine( "Unable to execute " + cmd );
				running = false;
				continue;
			}
			
			// create stdout and stderr readers
			BufferedReader stdout = new BufferedReader(
				new InputStreamReader( proc.getInputStream() ) );
		
			BufferedReader stderr = new BufferedReader(
				new InputStreamReader( proc.getErrorStream() ) );
		
			// output to the console the command about to be executed
			console.addInfoLine( getActionName() + ": " + cmd );


			try {
				while ( running &&
					( (line = stdout.readLine( )) != null || (errLine = stderr.readLine( )) != null )
				) {
					// read a line of stdout or stderr and add to the console,
					// until no more lines are to be added
					
					if ( line != null ) {
						console.addLine( line );
					}
			
					if ( errLine != null ) {
						console.addErrLine( errLine );
					}
				}
			} catch ( IOException err ) {
				System.out.println( "Killing the process broke the file descriptor" );
				running = false; // ensure the command list is to be cancelled
			}
			
			// close the readers
			try {
				stdout.close();
				stderr.close();
			} catch ( IOException e ) {
				e.printStackTrace();
			}
			
			
			try {
				// output that the process completed (or was cancelled)
				if ( running && proc.waitFor() != 0 ) {
					console.addInfoLine( "Done ( " + proc.exitValue() + " ).\n" );
				} else if ( running ) {
					console.addInfoLine( "Done.\n" );
				} else {
					console.addInfoLine( "Cancelled.\n" );
					break; // if a process was cancelled, don't run any more commands
					// escapes from command list loop
				}
			} catch ( InterruptedException e ) {
				e.printStackTrace();
				break; // something went wrong, don't run any more commands
			}
		}

		console.finishCommand( id );
	}
}
