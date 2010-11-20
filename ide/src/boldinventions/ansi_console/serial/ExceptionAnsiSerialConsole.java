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

package boldinventions.ansi_console.serial;

public class ExceptionAnsiSerialConsole extends Exception 
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 494423994745366983L;
	public static final int ID_UNKNOWN = 0;
	public static final int ID_PORT_DOES_NOT_EXIST = 1;
	public static final int ID_PORT_OPEN_FAILURE = 2;
	public static final int ID_PORT_SETTINGS_FAILURE = 3;
	public static final int ID_PORT_BAD_SETTINGS = 4;
	
	int m_id;
	String m_strPortName;
	
	public ExceptionAnsiSerialConsole() {
		m_strPortName="Unknown";
		m_id=ID_UNKNOWN;
	}

	public ExceptionAnsiSerialConsole(String message) {
		super(message);
		m_strPortName="Unknown";
		m_id=ID_UNKNOWN;
	}

    public ExceptionAnsiSerialConsole(int id, String strPortName, String message)
    {
    	super(message);
    	m_strPortName=strPortName;
    	m_id=id;
    }
    
    public int getID() { return m_id; }
    public String getPortName() { return m_strPortName; }
    

}
