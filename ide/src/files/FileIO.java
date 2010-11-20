package files;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class FileIO {
	public static void writeFile( String content, File file ) {
		try {
			BufferedWriter writer = new BufferedWriter( new FileWriter( file.getAbsolutePath( ) ) );
			
			writer.write( content );
			
			writer.close( );
		} catch (IOException e) {
			e.printStackTrace();
		}
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
