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

import org.eclipse.swt.widgets.*;
import org.eclipse.swt.events.*;
import org.eclipse.swt.*;
import org.eclipse.swt.layout.*;

import boldinventions.ansi_console.serial.SerialPortSettingsContainer.ExceptionSettingsParse;
import gnu.io.*;

/**
 * SerialPortSettingsGroup is a group of widgets for setting 
 * the parameters of a serial port.
 * @author Kevin Stokes of BoldInventions.com
 *
 */
public class SerialPortSettingsGroup extends Composite 
{
    /*
     * Constants for margins and so on.
     */
    static final int IOFF_TOP=2;  // distance from upper left corner
    static final int IOFF_LEFT=2;
    static final int IHORIZ_MARGIN=5;  // Margins on sides
    static final int IVERT_MARGIN=5;   // Margin on top and bottom
    static final int ISPACING=10;      // Spacing between controls
    
    


    public static final SettingNameAndValue[] g_baudRates={

        new SettingNameAndValue("300", 300),
        new SettingNameAndValue("1200", 1200),
        new SettingNameAndValue("2400", 2400),
        new SettingNameAndValue("4800", 4800),
        new SettingNameAndValue("9600", 9600),
        new SettingNameAndValue("19200", 19200),
        new SettingNameAndValue("38400", 38400),
        new SettingNameAndValue("115200", 115200)
    };
    
    public static final SettingNameAndValue[] g_dataBits=
    {
      new SettingNameAndValue("7", SerialPort.DATABITS_7),
      new SettingNameAndValue("8", SerialPort.DATABITS_8)
    };
    
    public static final SettingNameAndValue[] g_stopBits=
    {
      new SettingNameAndValue("1", SerialPort.STOPBITS_1),
      new SettingNameAndValue("2", SerialPort.STOPBITS_2)
    };
    
    public static final SettingNameAndValue[] g_parity=
    {
        new SettingNameAndValue("even", SerialPort.PARITY_EVEN),
        new SettingNameAndValue("odd", SerialPort.PARITY_ODD),
        new SettingNameAndValue("none", SerialPort.PARITY_NONE)
    };
    
    public static final SettingNameAndValue[] g_flowControl=
    {
        new SettingNameAndValue("none", SerialPort.FLOWCONTROL_NONE),
        new SettingNameAndValue("RTSCTS_IN", SerialPort.FLOWCONTROL_RTSCTS_IN),
        new SettingNameAndValue("RTSCTS_OUT", SerialPort.FLOWCONTROL_RTSCTS_OUT),
        new SettingNameAndValue("XON/XOFF_IN", SerialPort.FLOWCONTROL_XONXOFF_IN),
        new SettingNameAndValue("XON/XOFF_OUT", SerialPort.FLOWCONTROL_XONXOFF_OUT)
    };

    final static int findIndexFromName(SettingNameAndValue[] namesAndValues, String strValue)
    {
        int i;
        int iRet=-1;
        for(i=0; i<namesAndValues.length; i++)
        {
            if(namesAndValues[i].name.equals(strValue))
            {
                iRet=i;
                break;
            }
        }
        return iRet;
    }
    
    final static int findIndexFromValue(SettingNameAndValue[] namesAndValues, int iValue)
    {
        int i;
        int iRet=-1;
        for(i=0; i<namesAndValues.length; i++)
        {
            if(namesAndValues[i].value == iValue)
            {
                iRet=i;
                break;
            }
        }
        return iRet;
    }

    

    
    protected SerialPortSettingsContainer m_currentSettings; // The current settings for this instance.
    

    
    /**
     * SerialPortSettingsGroup constructor.
     * @param parent the parent
     * @param style the swt style
     */
    public SerialPortSettingsGroup(Composite parent, int style) {
        super(parent, style);
        m_currentSettings=new SerialPortSettingsContainer(
                "default",
                9600,
                SerialPort.DATABITS_8,
                SerialPort.STOPBITS_1,
                SerialPort.PARITY_NONE,
                SerialPort.FLOWCONTROL_NONE
                );


        buildControls();
    }
    
    public SerialPortSettingsGroup(Composite parent, int style, String strSettings)
    {
        super(parent, style);
        try {
			m_currentSettings=new SerialPortSettingsContainer(strSettings);
		} catch (ExceptionSettingsParse e) 
		{
	        m_currentSettings=new SerialPortSettingsContainer(
	                "default",
	                9600,
	                SerialPort.DATABITS_8,
	                SerialPort.STOPBITS_1,
	                SerialPort.PARITY_NONE,
	                SerialPort.FLOWCONTROL_NONE
	                );
		}
        buildControls();
    }
    
    /*
     * Controls
     */
    protected Group m_group;
    protected Combo m_comboBaudRate;
    protected Combo m_comboDataBits;
    protected Combo m_comboStopBits;
    protected Combo m_comboParity;
    protected Combo m_comboFlowControl;
    
    
    /**
     * buildControls is called by the constructor to build all the controls for the group.
     */
    protected void buildControls()
    {
        int i;
        
        /*
         * The composite object that we are embedded in will have one 
         * control in it- a group.  That group will fill the entire
         * composite.
         */
        FillLayout baseLayout = new FillLayout();
        setLayout(baseLayout);
        
        /*
         *  The group itself will use a FormLayout.
         *  We will use FormLayout so all controls are relative to each other.
         */
        GridLayout fl = new GridLayout(2, false);

        m_group=new Group(this, SWT.None);
        
        
        /*
         * Now we create all the combo controls 
         */
        






        m_group.setText("Serial Port Settings");
        
        m_group.setLayout(fl);
        
        fl.marginLeft=IHORIZ_MARGIN;
        fl.marginRight=IHORIZ_MARGIN;
        fl.marginTop=IVERT_MARGIN;
        fl.marginBottom=IVERT_MARGIN;
        
        Label lblBaudRate = new Label(m_group, SWT.RIGHT);        
        m_comboBaudRate = new Combo(m_group, SWT.READ_ONLY);

        Label lblDataBits = new Label(m_group, SWT.RIGHT);
        m_comboDataBits = new Combo(m_group, SWT.READ_ONLY);
        
        Label lblStopBits = new Label(m_group, SWT.RIGHT);
        m_comboStopBits = new Combo(m_group, SWT.READ_ONLY);
        
        Label lblParity = new Label(m_group, SWT.RIGHT);
        m_comboParity = new Combo(m_group, SWT.READ_ONLY);
        
        Label lblFlowControl = new Label(m_group, SWT.RIGHT);
        m_comboFlowControl = new Combo(m_group, SWT.READ_ONLY);

        lblBaudRate.setText("Baud Rate");
        lblDataBits.setText("Data Bits");
        lblStopBits.setText("Stop Bits");
        lblParity.setText("Parity");
        lblFlowControl.setText("Flow Control");
        
        
        /*
         * We start out with them all disabled since no serial port is selected.
         */
        


        
        /*
         * Now we populate our combo controls
         */
        
        for(i=0; i<g_baudRates.length; i++)
        {
            m_comboBaudRate.add(g_baudRates[i].name, i);
        }
        m_comboBaudRate.select(0); 
        
        for(i=0; i<g_dataBits.length; i++)
        {
            m_comboDataBits.add(g_dataBits[i].name, i);
        }
        m_comboDataBits.select(0);
        
        for(i=0; i<g_stopBits.length; i++)
        {
            m_comboStopBits.add(g_stopBits[i].name, i);
        }
        m_comboStopBits.select(0);
        
        for(i=0; i<g_parity.length; i++)
        {
            m_comboParity.add(g_parity[i].name, i);
        }
        m_comboParity.select(0);
        
        for(i=0; i<g_flowControl.length; i++)
        {
            m_comboFlowControl.add(g_flowControl[i].name, i);
        }
        m_comboFlowControl.select(0);
        
       
        
        setEnabled(false);
        
    } // buildControls()
    

    @Override
    public void setEnabled(boolean enabled)
    {
        m_comboBaudRate.setEnabled(enabled);
        m_comboDataBits.setEnabled(enabled);
        m_comboStopBits.setEnabled(enabled);
        m_comboParity.setEnabled(enabled);
        m_comboFlowControl.setEnabled(enabled);
    }
    
    /**
     * Fills m_currentSettings from controls.  If the controls have been disposed, does nothing.
     */
    protected void getSettingsFromControls()
    {
      if(!m_comboBaudRate.isDisposed())
      {
          m_currentSettings.baudRate = g_baudRates[m_comboBaudRate.getSelectionIndex()].value;
      }
      if(!m_comboDataBits.isDisposed())
      {
          m_currentSettings.dataBits = g_dataBits[m_comboDataBits.getSelectionIndex()].value;
      }
      if(!m_comboStopBits.isDisposed())
      {
          m_currentSettings.stopBits = g_stopBits[m_comboStopBits.getSelectionIndex()].value;
      }
      if(!m_comboParity.isDisposed())
      {
          m_currentSettings.parity = g_parity[m_comboParity.getSelectionIndex()].value;
      }
      if(!m_comboFlowControl.isDisposed())
      {
          m_currentSettings.flowControl = g_flowControl[m_comboFlowControl.getSelectionIndex()].value;
      }
    }
    /*
     * Getters and Setters for the settings.
     */
    
    // Baud Rate
    
    /**
     * getBaudRate retrieves the current baud rate setting.
     */
    public int getBaudRate()
    {
        getSettingsFromControls();
        return this.m_currentSettings.baudRate;
    }
    
    /**
     * setBaudRate sets the current baud rate
     * @param iBaudRate the baud rate to set the controls too.
     * @throws ExceptionSerialPortSetting
     */
    public void setBaudRate(int iBaudRate) throws ExceptionSerialPortSetting
    {
        int iRes;
        iRes=SerialPortSettingsGroup.findIndexFromValue(g_baudRates, iBaudRate);
        if(0>iRes)
        {
            throw new ExceptionSerialPortSetting(ExceptionSerialPortSetting.enum_cause.ILLEGAL_BAUD_RATE);
        }
        m_currentSettings.baudRate = iBaudRate;
        m_comboBaudRate.select(iRes);
    }
    
    /**
     * getDataBits returns the current number of data bits setting.
     * @return
     */
    public int getDataBits()
    {
        getSettingsFromControls();
        return this.m_currentSettings.dataBits;
    }
    
    /**
     * setDataBits sets the currently displayed number of data bits.
     * @param iDataBits
     * @throws ExceptionSerialPortSetting
     */
    public void setDataBits(int iDataBits) throws ExceptionSerialPortSetting
    {
        int iRes;
        iRes=SerialPortSettingsGroup.findIndexFromValue(g_dataBits, iDataBits);
        if(0>iRes)
        {
            throw new ExceptionSerialPortSetting(ExceptionSerialPortSetting.enum_cause.ILLEGAL_DATA_BITS);
        }
        m_currentSettings.dataBits = iDataBits;
        m_comboDataBits.select(iRes);
    }

    /**
     * getDataBits returns the current number of stop bits setting.
     * @return
     */
    public int getStopBits()
    {
        getSettingsFromControls();
        return this.m_currentSettings.stopBits;
    }
    
    /**
     * setStopBits sets the currently displayed number of stop bits.
     * @param iStopBits
     * @throws ExceptionSerialPortSetting
     */
    public void setStopBits(int iStopBits) throws ExceptionSerialPortSetting
    {
        int iRes;
        iRes=SerialPortSettingsGroup.findIndexFromValue(g_stopBits, iStopBits);
        if(0>iRes)
        {
            throw new ExceptionSerialPortSetting(ExceptionSerialPortSetting.enum_cause.ILLEGAL_STOP_BITS);
        }
        m_currentSettings.stopBits = iStopBits;
        m_comboStopBits.select(iRes);
    }
    
    /**
     * getParity returns the current parity setting.
     * @return
     */
    public int getParity()
    {
        getSettingsFromControls();
        return this.m_currentSettings.parity;
    }
    
    /**
     * setParity sets the currently displayed parity setting.
     * @param iParity
     * @throws ExceptionSerialPortSetting
     */
    public void setParity(int iParity) throws ExceptionSerialPortSetting
    {
        int iRes;
        iRes=SerialPortSettingsGroup.findIndexFromValue(g_parity, iParity);
        if(0>iRes)
        {
            throw new ExceptionSerialPortSetting(ExceptionSerialPortSetting.enum_cause.ILLEGAL_PARITY);
        }
        m_currentSettings.parity = iParity;
        m_comboParity.select(iRes);
    }
    
    /**
     * getFlowControl returns the current flow control setting.
     * @return
     */
    public int getFlowControl()
    {
        getSettingsFromControls();
        return this.m_currentSettings.flowControl;
    }
    
    /**
     * setFlowControl sets the currently displayed flow control
     * @param iFlowControl
     * @throws ExceptionSerialPortSetting
     */
    public void setFlowControl(int iFlowControl) throws ExceptionSerialPortSetting
    {
        int iRes;
        iRes=SerialPortSettingsGroup.findIndexFromValue(g_flowControl, iFlowControl);
        if(0>iRes)
        {
            throw new ExceptionSerialPortSetting(ExceptionSerialPortSetting.enum_cause.ILLEGAL_FLOWCONTROL);
        }
        m_currentSettings.flowControl = iFlowControl;
        m_comboFlowControl.select(iRes);
    }
    
    public SerialPortSettingsContainer getSettings()
    {
        getSettingsFromControls();
        return m_currentSettings;
    }
    
}
