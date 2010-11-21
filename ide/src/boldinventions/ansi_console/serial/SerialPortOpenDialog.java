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

import gnu.io.*;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.swt.widgets.*;
import org.eclipse.swt.*;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.*;


public class SerialPortOpenDialog extends Dialog 
{
    final static String getPortTypeName ( int portType )
    {
        switch ( portType )
        {
            case CommPortIdentifier.PORT_I2C:
                return "I2C";
            case CommPortIdentifier.PORT_PARALLEL:
                return "Parallel";
            case CommPortIdentifier.PORT_RAW:
                return "Raw";
            case CommPortIdentifier.PORT_RS485:
                return "RS485";
            case CommPortIdentifier.PORT_SERIAL:
                return "Serial";
            default:
                return "unknown type";
        }
    }
    
    
    final static CommPortIdentifier findCommPortByName(String name)
    {
      CommPortIdentifier portIdentifier=null;
      CommPortIdentifier portReturn=null;
      

      // Loop through all the serial ports and add each one to the combo box.
        @SuppressWarnings("rawtypes")
		java.util.Enumeration portEnum = CommPortIdentifier.getPortIdentifiers();
        while ( portEnum.hasMoreElements() ) 
        {
            portIdentifier = (CommPortIdentifier) portEnum.nextElement();
            if( CommPortIdentifier.PORT_SERIAL == portIdentifier.getPortType())
            {
               if(name.startsWith(portIdentifier.getName()))
               {
                   portReturn=portIdentifier;
                   break;
               }
            }
//            System.out.println(portIdentifier.getName()  +  " - " +  getPortTypeName(portIdentifier.getPortType()) );
        } 
        return portReturn;
    }
    
    
    protected Combo comboPortSelect;
    //protected List comboPortSelect;
    protected SerialPortSettingsGroup m_grpSettings;
    protected SerialPort m_commPort;
    protected ISerialPortSettingsListener m_settingsListener;
    protected boolean m_bAutoConnect;
    protected String m_strAutoConnectSerialPortSettings;

    protected SerialPortOpenDialog(Shell parentShell, ISerialPortSettingsListener settingsListener) 
    {
        super(parentShell);
        m_commPort=null;
        m_settingsListener=settingsListener;
        m_bAutoConnect=false;
        m_strAutoConnectSerialPortSettings=null;
    }
    
    protected SerialPortOpenDialog(Shell parentShell, 
    		ISerialPortSettingsListener settingsListener,
    		String strAutoConnectSerialPortSettings,
    		boolean bAutoConnect) 
    {
        super(parentShell);
        m_commPort=null;
        m_settingsListener=settingsListener;
        m_bAutoConnect=bAutoConnect;
        m_strAutoConnectSerialPortSettings=strAutoConnectSerialPortSettings;
    }
    
    

    
    protected SerialPortOpenDialog(Shell parentShell) 
    {
        super(parentShell);
        m_commPort=null;
        m_settingsListener=null;
    }
    
    public SerialPort getCommPort()
    {
        return m_commPort;
    }

    @Override
    protected Control createDialogArea(Composite parent)
    {
        Composite comp= (Composite) super.createDialogArea(parent);
        int iPort;
        FormLayout fl=new FormLayout();
        
        fl.spacing=12;
        
        comp.setLayout(fl);
        
        Label lblComPortSelect = new Label(comp, SWT.RIGHT);
        lblComPortSelect.setText("Select a Serial Port");
        
        FormData data = new FormData();
        data.top = new FormAttachment(2,2);
        data.left = new FormAttachment(2, 2);
        
        lblComPortSelect.setLayoutData(data);
        
        m_grpSettings = new SerialPortSettingsGroup(comp, SWT.NONE);
        

        
        // Initialize the Serial Port Selection Combo.
        comboPortSelect=new Combo( comp, SWT.READ_ONLY );
        //comboPortSelect = new List( comp, SWT.SINGLE | SWT.BORDER );
        iPort=0;
        comboPortSelect.add("Select a Port",iPort++);
        
        // Loop through all the serial ports and add each one to the combo box.
          @SuppressWarnings("rawtypes")
		java.util.Enumeration portEnum = CommPortIdentifier.getPortIdentifiers();
          while ( portEnum.hasMoreElements() ) 
          {
              CommPortIdentifier portIdentifier = (CommPortIdentifier) portEnum.nextElement();
              String portString=portIdentifier.getName()  +  " - " +  getPortTypeName(portIdentifier.getPortType()) ;
              if( CommPortIdentifier.PORT_SERIAL == portIdentifier.getPortType())
              {
                comboPortSelect.add(portString,iPort++);
              }
//            System.out.println(portIdentifier.getName()  +  " - " +  getPortTypeName(portIdentifier.getPortType()) );
          } 
        
  //    combo.add("option 1", 0);
  //    combo.add("option 2", 1);
  //    combo.add("option 3", 2);
        comboPortSelect.select(0);
        comboPortSelect.pack();
        
        data = new FormData();
        data.top=new FormAttachment(2,2);
        data.left=new FormAttachment(lblComPortSelect);
        
        comboPortSelect.setLayoutData(data);
        
        data = new FormData();
        data.top = new FormAttachment(2, 2);
        data.left = new FormAttachment(comboPortSelect);
        m_grpSettings.setLayoutData(data);
        
        comboPortSelect.addSelectionListener(
                new SelectionListener()
                {

                    public void widgetDefaultSelected(SelectionEvent e) {
                        // TODO Auto-generated method stub
                        
                    }

                    public void widgetSelected(SelectionEvent e) {
                        CommPortIdentifier portIdentifier = 
                            findCommPortByName(comboPortSelect.getItem(comboPortSelect.getSelectionIndex()));
                        if(null != portIdentifier)
                        {
                          e.data = (Object) portIdentifier;
                          doPortSelected(e);
                        }
                    }
                    
                }
              );
        
        return comp;
    } // end createDialogArea()
    
    protected void doPortSelected(SelectionEvent e)
    {

      CommPortIdentifier portIdentifier = (CommPortIdentifier) e.data;
      
      try {
          m_commPort = (SerialPort) portIdentifier.open("SerialTest", 1000);
      } catch (PortInUseException e1) {
         Status status = new Status(IStatus.ERROR, "Can't Comply", 0,
        		 "Because Port " + portIdentifier.getName() + 
        		 " is in Use by another Application.", null);
         ErrorDialog.openError(getShell(), "Cannot Open Serial Port" , portIdentifier.getName(), status);
      }
      
      if(null!=m_commPort)
      {
          m_grpSettings.getSettings().name=portIdentifier.getName();
      
      try {
          m_grpSettings.setBaudRate(m_commPort.getBaudRate());
      } catch (ExceptionSerialPortSetting e1) {
          // TODO Auto-generated catch block
          e1.printStackTrace();
      }
      
      try {
          m_grpSettings.setDataBits(m_commPort.getDataBits());
      } catch (ExceptionSerialPortSetting e1) {
          // TODO Auto-generated catch block
          e1.printStackTrace();
      }
      
      try {
          m_grpSettings.setStopBits(m_commPort.getStopBits());
      } catch (ExceptionSerialPortSetting e1) {
          // TODO Auto-generated catch block
          e1.printStackTrace();
      }
      
      try {
          m_grpSettings.setParity(m_commPort.getParity());
      } catch (ExceptionSerialPortSetting e1) {
          // TODO Auto-generated catch block
          e1.printStackTrace();
      }
      
      try {
          m_grpSettings.setFlowControl(m_commPort.getFlowControlMode());
      } catch (ExceptionSerialPortSetting e1) {
          // TODO Auto-generated catch block
          e1.printStackTrace();
      }
      
      m_grpSettings.setEnabled(true);
      
      } // if(null != m_commPort)
      
     
    }
    
    int getBaudRate()
    {
        return m_grpSettings.getBaudRate();
    }
    
    int getDataBits()
    {
        return m_grpSettings.getDataBits();
    }
    
    int getStopBits()
    {
        return m_grpSettings.getStopBits();
        
    }
    
    int getParity()
    {
        return m_grpSettings.getParity();
    }
    
    int getFlowControl()
    {
        return m_grpSettings.getFlowControl();
    }
    
    /**
     * getSettingsString builds a String with the current parameter settings like 'COM1 8,N,1 NONE'
     * @return the String
     */
    String getSettingsString()
    {
        return m_grpSettings.getSettings().toString();
    }
    
    SerialPortSettingsContainer getSerialPortSettings()
    {
    	return m_grpSettings.getSettings();
    }

    @Override
    protected void okPressed()
    {
        m_grpSettings.getSettingsFromControls();  // Update settings while controls are still valid.
        if(null!= m_settingsListener)
        {
            m_settingsListener.NotifySettingChanged(0, m_grpSettings.getSettings());
        }
        super.okPressed();
    }
}
