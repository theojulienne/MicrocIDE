/*
    BirdTerm - Copyright (C) 2009 Kevin Stokes.

    Bold Inventions Really Dumb Terminal

    It emulates an RS232 (Serial Port) terminal with minimal
    support for ANSI command sequences to set color, and
    cursor position.

    This file is part of the BirdTerm distribution
    originally available from http://www.boldinventions.com

    This program is free software; you can redistribute it and/or modify
    it under the terms of the Eclipse Public License - v 1.0.

    This software has NO WARRANTY.  Use it at your own risk;   It 
    has no warranty of any kind, implied or otherwise.

    There should be a copy of the Eclipse Public License along with
    this source code.  If not, it can be found on the eclipse.org
        website:  http://www.eclipse.org

*/

package boldinventions.birdterm;

import java.util.prefs.Preferences;

import org.eclipse.swt.graphics.Rectangle;


import boldinventions.ansi_console.AnsiCharColors;
import boldinventions.ansi_console.serial.AnsiSerialConsole;

public class AppSettings 
{
	 // A reference to a Preferences object
   private Preferences myPreferences = null;
   

   // Default values for this frame's preferences
   public static final int DEFAULT_WINDOW_X = 50;
   public static final int DEFAULT_WINDOW_Y = 50;
   public static final int DEFAULT_WINDOW_WIDTH = 640;
   public static final int DEFAULT_WINDOW_HEIGHT = 480;
   
   public static final int DEFAULT_BACKGROUND_COLOR = AnsiCharColors.CLR_WHITE | 0x8;
   public static final int DEFAULT_FOREGROUND_COLOR = AnsiCharColors.CLR_BLACK;
   
   public static final int DEFAULT_NROWS_TEXTCONDOC = 200;
   public static final int DEFAULT_NCOLS_TEXTCONDOC = 80;
   
   public static final String DEFAULT_SERIAL_PORT_SETTINGS = "default";
   public static final boolean DEFAULT_AUTO_CONNECT=false;
   public static final boolean DEFAULT_LOCAL_ECHO=false;
   
   public static final String ENCODING_TYPE_STRING_UTF8="UTF8";
   public static final String ENCODING_TYPE_STRING_CP437="CP437";
   public static final int DEFAULT_ENCODING=AnsiSerialConsole.ENCODE_SERIAL_BYTES_AS_CP437;
   public static final String DEFAULT_ENCODING_STRING=ENCODING_TYPE_STRING_CP437;

   // Keys for this frame's preferences
   public static final String WINDOW_X_KEY = "TEST_WINDOW_X";
   public static final String WINDOW_Y_KEY = "TEST_WINDOW_Y";
   public static final String WINDOW_WIDTH_KEY = "TEST_WINDOW_WIDTH";
   public static final String WINDOW_HEIGHT_KEY = "TEST_WINDOW_HEIGHT";
   
   public static final String CONSOLE_TEXT_BACKGROUND_COLOR = "CONSOLE_TEXT_BACK_CLR";
   public static final String CONSOLE_TEXT_FOREGROUND_COLOR = "CONSOLE_TEXT_FORE_CLR";
   
   public static final String KEY_NROWS_TEXTCONDOC = "NROWS_TEXTCONDOC";
   public static final String KEY_NCOLS_TEXTCONDOC = "NCOLS_TEXTCONDOC";   
   
   public static final String KEY_SERIAL_PORT_SETTINGS = "LAST_SERIAL_PORT_SETTINGS";
   public static final String KEY_AUTO_CONNECT = "AUTO_CONNECT";
   public static final String KEY_LOCAL_ECHO = "LOCAL_ECHO";
   
   public static final String KEY_ENCODING= "ENCODING_TYPE";

   
   public String getLastSerialPortSettings() 
   { 
   	String sRet=null;
   	if(!m_strLastSerialPortSettings.equalsIgnoreCase(DEFAULT_SERIAL_PORT_SETTINGS))
   	{
   		sRet=m_strLastSerialPortSettings;
   	}
   	return sRet; 
   }
   
   public boolean getAutoConnect() { return m_bAutoConnect; }
   public boolean getLocalEcho() { return m_bLocalEcho; }
   
   public int getEncodingType() { return m_iEncodingType; }
   public void setEncodingType(int iEncodingType) { m_iEncodingType=iEncodingType; }
   
   public void setLastSerialPortSettings( String name ) 
   {
   	if(null==name)
   	{
   		m_strLastSerialPortSettings  = DEFAULT_SERIAL_PORT_SETTINGS;
   	} else
   	{
   	   m_strLastSerialPortSettings = name; 
   	}
   }
   public void setAutoConnect(boolean bAutoConnect) { m_bAutoConnect = bAutoConnect; }
   public void setLocalEcho(boolean bLocalEcho) { m_bLocalEcho = bLocalEcho; }
   
   protected Rectangle m_appBounds;
   protected int m_iColorTextBackground;
   protected int m_iColorTextForeground;
   protected int m_nRowsTextConDoc;
   protected int m_nColsTextConDoc;
   protected String m_strLastSerialPortSettings;
   protected boolean m_bAutoConnect;
   protected boolean m_bLocalEcho;
   protected int m_iEncodingType;
   
   /**
    * getCodeFromEncodingName takes a string and returns an integer code value for the
    * encoding.
    * @param name
    * @return
    */
   public static int getCodeFromEncodingName(String name)
   {
   	int iRet=DEFAULT_ENCODING;
   	if(name.equalsIgnoreCase(ENCODING_TYPE_STRING_CP437))
   	{
   		iRet=AnsiSerialConsole.ENCODE_SERIAL_BYTES_AS_CP437;
   	} else if(name.equalsIgnoreCase(ENCODING_TYPE_STRING_UTF8))
   	{
   		iRet=AnsiSerialConsole.ENCODE_SERIAL_BYTES_AS_UTF8;
   	}
   	return iRet;
   }
   
   public static String getNameFromEncodingInt(int iEncoding)
   {
   	String sret;
   	switch(iEncoding)
   	{
   	case AnsiSerialConsole.ENCODE_SERIAL_BYTES_AS_UTF8:
   		sret=ENCODING_TYPE_STRING_UTF8;
   		break;
   	case AnsiSerialConsole.ENCODE_SERIAL_BYTES_AS_CP437:
		default:
   		sret=ENCODING_TYPE_STRING_CP437;
   		break;
   	}
   	return sret;
   }
   
   public AppSettings()
   {
      // Obtain a references to a Preferences object
      myPreferences = Preferences.userNodeForPackage(BirdTerm.class);
      
      m_appBounds=new Rectangle(
     		 myPreferences.getInt(WINDOW_X_KEY, DEFAULT_WINDOW_X), 
     		 myPreferences.getInt(WINDOW_Y_KEY, DEFAULT_WINDOW_Y), 
     		 myPreferences.getInt(WINDOW_WIDTH_KEY, DEFAULT_WINDOW_WIDTH), 
     		 myPreferences.getInt(WINDOW_HEIGHT_KEY, DEFAULT_WINDOW_HEIGHT));
      
      m_iColorTextBackground = myPreferences.getInt(CONSOLE_TEXT_BACKGROUND_COLOR, DEFAULT_BACKGROUND_COLOR);
      m_iColorTextForeground = myPreferences.getInt(CONSOLE_TEXT_FOREGROUND_COLOR, DEFAULT_FOREGROUND_COLOR);
      m_nRowsTextConDoc = myPreferences.getInt(KEY_NROWS_TEXTCONDOC, DEFAULT_NROWS_TEXTCONDOC);
      m_nColsTextConDoc = myPreferences.getInt(KEY_NCOLS_TEXTCONDOC, DEFAULT_NCOLS_TEXTCONDOC);
      m_strLastSerialPortSettings = myPreferences.get(KEY_SERIAL_PORT_SETTINGS, DEFAULT_SERIAL_PORT_SETTINGS);
      m_bAutoConnect = myPreferences.getBoolean(KEY_AUTO_CONNECT, DEFAULT_AUTO_CONNECT);
      m_bLocalEcho = myPreferences.getBoolean(KEY_LOCAL_ECHO, DEFAULT_LOCAL_ECHO);
      m_iEncodingType = getCodeFromEncodingName(myPreferences.get(KEY_ENCODING, DEFAULT_ENCODING_STRING));
   }
   
   public Rectangle getAppBounds() { return m_appBounds; }
   public void setAppBounds(Rectangle rect) { m_appBounds = rect; }
   
   public int getIColorTextBackground() { return m_iColorTextBackground; }
   public int getIColorTextForeground() { return m_iColorTextForeground; }
   public void setIColorTextBackground( int iColor ) { m_iColorTextBackground=iColor; }
   public void setIColorTExtForeground( int iColor ) { m_iColorTextForeground=iColor; }
   public int getNRowsTextConDoc() { return m_nRowsTextConDoc; }
   public int getNColsTextConDoc() { return m_nColsTextConDoc; }
   public void setNRowsTextConDoc( int nRows ) { m_nRowsTextConDoc = nRows; }
   public void setNColsTextConDoc( int nCols ) { m_nColsTextConDoc = nCols; }
   
   public void saveLastWindowPos()
   {
   	myPreferences.putInt(WINDOW_X_KEY, m_appBounds.x);
   	myPreferences.putInt(WINDOW_Y_KEY, m_appBounds.y);
   	myPreferences.putInt(WINDOW_WIDTH_KEY, m_appBounds.width);
   	myPreferences.putInt(WINDOW_HEIGHT_KEY, m_appBounds.height);
   }
   
   public void savePreferences()
   {
   	myPreferences.putInt(CONSOLE_TEXT_BACKGROUND_COLOR, m_iColorTextBackground);
   	myPreferences.putInt(CONSOLE_TEXT_FOREGROUND_COLOR, m_iColorTextForeground);
   	
   	myPreferences.putInt(KEY_NROWS_TEXTCONDOC, m_nRowsTextConDoc);
   	myPreferences.putInt(KEY_NROWS_TEXTCONDOC, m_nRowsTextConDoc);
   	
   	myPreferences.putInt(CONSOLE_TEXT_BACKGROUND_COLOR, m_iColorTextBackground);
   	myPreferences.putInt(CONSOLE_TEXT_FOREGROUND_COLOR, m_iColorTextForeground);

   	myPreferences.putInt(KEY_NROWS_TEXTCONDOC, m_nRowsTextConDoc);
   	myPreferences.putInt(KEY_NCOLS_TEXTCONDOC, m_nColsTextConDoc);

   	myPreferences.put(KEY_SERIAL_PORT_SETTINGS, m_strLastSerialPortSettings);
   	myPreferences.putBoolean(KEY_AUTO_CONNECT, m_bAutoConnect);
   	myPreferences.putBoolean(KEY_LOCAL_ECHO, m_bLocalEcho);   	
   	
   	myPreferences.put(KEY_ENCODING, getNameFromEncodingInt(m_iEncodingType));
   }
}
