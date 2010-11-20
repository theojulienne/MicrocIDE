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

import gnu.io.SerialPort;


/**
 * SerialPortSettingsContainer is just a structure to hold the settings of a serial port.
 * @author Kevin
 *
 */
public class SerialPortSettingsContainer 
{
    public static String parityToString(int parity)
    {
        String strParity;
        switch( parity )
        {
        case SerialPort.PARITY_NONE:
            strParity="N";
            break;
        case SerialPort.PARITY_EVEN:
            strParity="E";
            break;
        case SerialPort.PARITY_ODD:
            strParity="O";
            break;
        case SerialPort.PARITY_MARK:
            strParity="M";
            break;
        case SerialPort.PARITY_SPACE:
            strParity="S";
            break;
            default:
                strParity="(Parity unknown): "+parity;
                break;
        }
        return strParity;
    }
    
    public static String dataBitsToString(int iDataBits)
    {
        String strDataBits;
        switch(iDataBits)
        {
        case SerialPort.DATABITS_5:
            strDataBits="5";
            break;
        case SerialPort.DATABITS_6:
            strDataBits="6";
            break;
        case SerialPort.DATABITS_7:
            strDataBits="7";
            break;
        case SerialPort.DATABITS_8:
            strDataBits="8";
            break;
            default:
                strDataBits="(Unknown nDataBits: " + iDataBits;
                break;
        }
        return strDataBits;
    }
    
    public static String stopBitsToString(int iStopBits)
    {
        String strStopBits;
        switch(iStopBits)
        {
        case SerialPort.STOPBITS_1:
            strStopBits="1";
            break;
        case SerialPort.STOPBITS_1_5:
            strStopBits="1.5";
            break;
        case SerialPort.STOPBITS_2:
            strStopBits="2";
            break;
            default:
                strStopBits="(Unknown nStopBits: " + iStopBits;
                break;
        }
        return strStopBits;
    }
    
    public static String flowControlToString(int iFlowControl)
    {
        String strFlowControl;
        switch(iFlowControl)
        {
        case SerialPort.FLOWCONTROL_NONE:
            strFlowControl="NONE";
            break;
        case SerialPort.FLOWCONTROL_RTSCTS_IN:
            strFlowControl="RTSCTS_IN";
            break;
        case SerialPort.FLOWCONTROL_RTSCTS_OUT:
            strFlowControl="RTSCTS_OUT";
            break;
        case SerialPort.FLOWCONTROL_XONXOFF_IN:
            strFlowControl="XONXOFF_IN";
            break;
        case SerialPort.FLOWCONTROL_XONXOFF_OUT:
            strFlowControl="XONXOFF_OUT";
            break;
            default:
                strFlowControl="Unknown Flow Control: " + iFlowControl;
                break;
        }
        return strFlowControl;
    }
    
    public String name;
    public int baudRate;
    public int dataBits;
    public int stopBits;
    public int parity;
    public int flowControl;
    public SerialPortSettingsContainer(
            String strName,
            int iBaudRate,
            int iDataBits,
            int iStopBits,
            int iParity,
            int iFlowControl)
    {
        name=strName;
        baudRate=iBaudRate;
        dataBits=iDataBits;
        stopBits=iStopBits;
        parity=iParity;
        flowControl=iFlowControl;
    }
    
    public SerialPortSettingsContainer(String strSettings) throws ExceptionSettingsParse
    {
    	parse(strSettings);
    }
    
    @Override
    public String toString()
    {

        String sRet=new String(name + "," + baudRate + "," + 
                parityToString(parity) + "," +
                dataBitsToString(dataBits) + "," +
                stopBitsToString(stopBits) + "," +
                flowControlToString(flowControl));
        return sRet;
    }
    
    public class ExceptionSettingsParse extends Exception
    {

		/**
		 * 
		 */
		private static final long serialVersionUID = 4582419542642392040L;

    	public ExceptionSettingsParse(String msg)
    	{
    		super(msg);
    	}
    }
    
    protected void parseName(String str) throws ExceptionSettingsParse
    {
    	if(0>=str.length()) 
    		throw new ExceptionSettingsParse("Missing Name in Serial Port Settings String");
    	this.name=new String(str);
    }
    protected void parseBaudRate(String str) throws ExceptionSettingsParse
    {
    	if(0>=str.length()) 
    		throw new ExceptionSettingsParse("Missing baud rate in Serial Port Settings String"); 
    	try
    	{
            this.baudRate=Integer.parseInt(str);
    	} catch (NumberFormatException nfe)
    	{
    		throw new ExceptionSettingsParse("Bad baud rate number in Serial Port Settings String");
    	}
    }
    protected void parseParity(String str) throws ExceptionSettingsParse
    {
    	if(0>=str.length()) 
    		throw new ExceptionSettingsParse("Missing parity in Serial Port Settings String"); 
        
    	if(str.equalsIgnoreCase(parityToString(SerialPort.PARITY_NONE)))
    	{
    		this.parity=SerialPort.PARITY_NONE;
    	} else
    	if(str.equalsIgnoreCase(parityToString(SerialPort.PARITY_EVEN)))
    	{
    		this.parity=SerialPort.PARITY_EVEN;
    	} else
        if(str.equalsIgnoreCase(parityToString(SerialPort.PARITY_ODD)))
    	{
    		this.parity=SerialPort.PARITY_ODD;
    	} else
        if(str.equalsIgnoreCase(parityToString(SerialPort.PARITY_MARK)))
    	{
    		this.parity=SerialPort.PARITY_MARK;
    	} else
        if(str.equalsIgnoreCase(parityToString(SerialPort.PARITY_SPACE)))
    	{
    		this.parity=SerialPort.PARITY_SPACE;
    	} else
    	{
    		throw new ExceptionSettingsParse("Bad value for parity in serial port settings string");
    	}
        
    }
    protected void parseDataBits(String str) throws ExceptionSettingsParse
    {
    	if(0>=str.length()) 
    		throw new ExceptionSettingsParse("Missing databits in Serial Port Settings String");    
    	try
    	{
            this.dataBits=Integer.parseInt(str);
    	} catch (NumberFormatException nfe)
    	{
    		throw new ExceptionSettingsParse("Bad dataBits number in Serial Port Settings String");
    	}
    }
    protected void parseStopBits(String str) throws ExceptionSettingsParse
    {
    	if(0>=str.length()) 
    		throw new ExceptionSettingsParse("Missing stopbits in Serial Port Settings String"); 
    	if(str.equalsIgnoreCase(stopBitsToString(SerialPort.STOPBITS_1)))
    	{
    		this.stopBits=SerialPort.STOPBITS_1;
    	} else
    	if(str.equalsIgnoreCase(stopBitsToString(SerialPort.STOPBITS_1_5)))
    	{
    		this.stopBits=SerialPort.STOPBITS_1_5;
    	} else
    	if(str.equalsIgnoreCase(stopBitsToString(SerialPort.STOPBITS_2)))
    	{
    		this.stopBits=SerialPort.STOPBITS_2;
    	} else
    	{
    		throw new ExceptionSettingsParse("Bad stopbits number in Serial Port Settings String");
    	}	
    }
    protected void parseFlowControl(String str) throws ExceptionSettingsParse
    {
    	if(0>=str.length()) 
    		throw new ExceptionSettingsParse("Missing flowcontrol in Serial Port Settings String"); 
    	if(str.equalsIgnoreCase(flowControlToString(SerialPort.FLOWCONTROL_NONE)))
    	{
    		this.parity=SerialPort.FLOWCONTROL_NONE;
    	} else
    	if(str.equalsIgnoreCase(flowControlToString(SerialPort.FLOWCONTROL_RTSCTS_IN)))
    	{
    		this.parity=SerialPort.FLOWCONTROL_RTSCTS_IN;
    	} else
    	if(str.equalsIgnoreCase(flowControlToString(SerialPort.FLOWCONTROL_RTSCTS_OUT)))
    	{
    		this.parity=SerialPort.FLOWCONTROL_RTSCTS_OUT;
    	} else
    	if(str.equalsIgnoreCase(flowControlToString(SerialPort.FLOWCONTROL_XONXOFF_IN)))
    	{
    		this.parity=SerialPort.FLOWCONTROL_XONXOFF_IN;
    	} else
    	if(str.equalsIgnoreCase(flowControlToString(SerialPort.FLOWCONTROL_XONXOFF_OUT)))
    	{
    		this.parity=SerialPort.FLOWCONTROL_XONXOFF_OUT;
    	} else
    	{
    		throw new ExceptionSettingsParse("Bad value for flowcontrol in serial port settings string");    		
    	}
    }
    protected String grabCharsBeforeComma(String str) throws ExceptionSettingsParse
    {
    	String sRet;
    	int iEnd;
    	iEnd = str.indexOf(',');
    	if(0>iEnd) throw new ExceptionSettingsParse("Missing Comma in Serial port Settings string.");
    	
    	sRet=str.substring(0, iEnd);
    	return sRet;
    }
    
    protected String grabCharsAfterComma(String str) throws ExceptionSettingsParse
    {
    	String sRet;
    	int iEnd;
    	iEnd = str.indexOf(',');
    	if(0>iEnd) throw new ExceptionSettingsParse("Missing Comma in Serial port Settings string.");
    	
    	sRet=str.substring(iEnd+1, str.length());
    	return sRet;
    }
    
    /**
     * parse sets all the member variables from the contents of a string in the 
     * format created by the toString() method.   If it cannot parse any one of
     * the settings, an exception will be thrown.
     * @param strSettings
     * @throws ExceptionSettingsParse
     */
    protected void parse(String strSettings)  throws ExceptionSettingsParse
    {
    	String strAfterComma;
    	String strBeforeComma;
    	if(null != strSettings)
    	{
    		  strAfterComma=strSettings;
    		  strBeforeComma=grabCharsBeforeComma(strAfterComma);
    		  strAfterComma=grabCharsAfterComma(strAfterComma);
    		  parseName(strBeforeComma.trim());
    		  strBeforeComma=grabCharsBeforeComma(strAfterComma);
    		  strAfterComma=grabCharsAfterComma(strAfterComma);
    		  parseBaudRate(strBeforeComma.trim());
    		  strBeforeComma=grabCharsBeforeComma(strAfterComma);
    		  strAfterComma=grabCharsAfterComma(strAfterComma);
    		  parseParity(strBeforeComma.trim());
    		  strBeforeComma=grabCharsBeforeComma(strAfterComma);
    		  strAfterComma=grabCharsAfterComma(strAfterComma);
              parseDataBits(strBeforeComma.trim());
    		  strBeforeComma=grabCharsBeforeComma(strAfterComma);
    		  strAfterComma=grabCharsAfterComma(strAfterComma);
    		  parseStopBits(strBeforeComma.trim());    		  
    		  parseFlowControl(strAfterComma.trim());
    	}
    }
    
    public static final String getPortNameFromSettingsString(String strSettings)
    {
    	String strName="(Bad Port Settings!)";
    	try {
			SerialPortSettingsContainer settings = new SerialPortSettingsContainer(strSettings);
			strName=new String(settings.name);
		} catch (ExceptionSettingsParse e) {
           // If we couldn't parse settings just return bad name
		}
    	return strName;
    }
    
}
