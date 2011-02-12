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

public class ExceptionSerialPortSetting extends Exception 
{
    /**
     * 
     */
    private static final long serialVersionUID = 5406564419528314243L;
    
    
    public enum enum_cause
    {
        ILLEGAL_BAUD_RATE,
        ILLEGAL_DATA_BITS,
        ILLEGAL_STOP_BITS,
        ILLEGAL_PARITY,
        ILLEGAL_FLOWCONTROL
    }
    
    protected enum_cause m_cause;


    public ExceptionSerialPortSetting(enum_cause cause)
    {
        m_cause = cause;
    }
    
    @Override
    public String toString()
    {
        return "ExceptionSerialPortSetting: " + m_cause.toString();
    }
}
