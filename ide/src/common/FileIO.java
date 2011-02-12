package common;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import org.json.JSONException;
import org.json.JSONObject;

public class FileIO {

	public static boolean writeJSONFile( JSONObject content, int indent, String filename ) {
		return writeJSONFile( content, indent, new File( filename ) );
	}
	
	public static boolean writeJSONFile( JSONObject content, int indent, File file ) {
		try {
			return writeFile( content.toString( indent ), file );
		} catch (JSONException e) {
			return false;
		}
	}
	
	public static boolean writeFile( String content, String filename ) {
		return writeFile( content, new File( filename ) );
	}
	
	public static boolean writeFile( String content, File file ) {
		boolean isSuccessful = true;
		try {
			BufferedWriter writer = new BufferedWriter( new FileWriter( file.getAbsolutePath( ) ) );
			
			writer.write( content );
			
			writer.close( );
		} catch (IOException e) {
			isSuccessful = false;
		}
		
		return isSuccessful;
	}
	
	public static JSONObject readJSONFile( String filename ) {
		return readJSONFile( new File( filename ) );
	}
	
	public static JSONObject readJSONFile( File file ) {
		String content = readFile( file );
		try {
			return new JSONObject( content );
		} catch (JSONException e) {
			return null;
		}
	}
	
	public static String readFile( String filename ) {
		return readFile( new File( filename ) );
	}
	
	public static String readFile( File file ) {
		String content = "";
		try {
			BufferedReader reader;
			reader = new BufferedReader( new FileReader( file.getAbsolutePath( ) ) );
			
			String line = reader.readLine();
			while ( line != null ) {
				content += line + "\n";
				line = reader.readLine();
			}
			
			reader.close( );
		} catch ( IOException e ) {
			e.printStackTrace( );
		}

		return content;
	}
}
