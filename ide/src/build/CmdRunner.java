package build;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

import app.project.ProjectWindow;
import app.toolTabs.BuildConsoleTab;

public class CmdRunner extends Thread {
	private ArrayList<String> commands;
	private BuildConsoleTab console;
	private File projectPath;
	private String actionName;
	private boolean running;
	private Process proc;
	private ProjectWindow project;
	private int id;
	
	public CmdRunner( ArrayList<String> commands, BuildConsoleTab console, ProjectWindow project, int id ) {
		this.console = console;
		this.commands = commands;
		this.projectPath = project.getPath();
		actionName = "Running";
		this.project = project;
		this.id = id;
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
				proc = Runtime.getRuntime().exec( cmd, null, projectPath );
			} catch ( IOException e ) {
				e.printStackTrace();
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

		project.finishCommand( id );
	}
}
