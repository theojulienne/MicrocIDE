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
import java.io.*;
import java.util.*;
import gnu.io.*;
import boldinventions.ansi_console.*;
import boldinventions.ansi_console.serial.SerialPortSettingsContainer.ExceptionSettingsParse;


/**
 * AnsiSerialConsole is a canvas object which can open a serial port
 * and display the incoming text and send keypresses out.   It accepts
 * ANSI character sequences for character positioning and text color
 * processing.
 * @author kevin
 *
 */
public class AnsiSerialConsole extends AnsiTextConsole 
    implements ITextConsoleDocChangeListener, ITextConsole, ICharArrayAccepter,
      ISerialPortActions
{
    protected SerialPort m_commPort;
    protected Display m_display;
    protected byte[] m_inputLineBuffer;
    protected int m_inputLineBufferIndex;
    protected Queue<String> m_strAsyncRecvQueue;
    protected ASC_KeyCharAccepter m_keyAccepter;
    protected boolean m_bAutoConnect;
    protected String m_strPreferredSerialPortSettings;
    protected SerialPortSettingsContainer m_settings=null;
    
    protected LinkedList<IStatusTextAccepter> m_statusTextListeners;
    
    /**
     * ASC_KeyCharAccepter is a class which relays characters to the 
     * serial port using the ASCII translation provided by
     * OutputStreamWriter().
     * @author kevin
     *
     */
    public class ASC_KeyCharAccepter implements ICharArrayAccepter
    {
    	boolean m_bEnable;
        protected SerialPort m_serialPort;
        protected OutputStreamWriter m_writer;
        
        public ASC_KeyCharAccepter(SerialPort serialPort)
        {
        	m_writer=null;
        	setPort(serialPort);
            m_bEnable = (null != serialPort);
        }
        
        public void close()
        {
        	m_bEnable=false;
        	if(null != m_writer)
        	{
        		try {
					m_writer.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
        	}
        	m_serialPort=null;
        }
        
        public void setPort(SerialPort serialPort)
        {
        	m_serialPort=serialPort;
        	if(null != m_serialPort)
        	{
        		try {
					m_writer=new OutputStreamWriter(m_serialPort.getOutputStream(), "US-ASCII");
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
        	}
        }
        
        public void setEnable(boolean bEnable)
        {
        	m_bEnable=bEnable;
        }
        
        public boolean getEnable()
        {
        	return m_bEnable;
        }
        

		public void acceptChar(char ch) 
		{
          if(m_bEnable  && (null != m_serialPort) )
          {
        	  try
        	  {
        		  m_serialPort.getOutputStream().write( (byte) ch);
        	  } catch ( Exception e )
        	  {
        		  // Todo: Must decide what to do on error.
        		  m_bEnable=false;
        	  }
          }
		}


		public void acceptChars(char[] chArr, int iOffset, int iLen) 
		{
           int i;
           for(i=0; i<iLen; i++)
           {
        	   if(i<chArr.length)
        	   {
        		   acceptChar(chArr[i]);
        	   }
           }
		}
    	
    } // class ASC_KeyCharAccepter

    
    static final int MAX_BYTES_INPUT_BUFFER=16384;
	
	public AnsiSerialConsole(Composite parent, 
			int styles, 
			int nRows, int nColumns, 
			int iColorBack, int iColorFore, 
			String strPreferredSerialPortSettings,
			boolean bAutoConnect) 
	{
		super(parent, styles, nRows, nColumns, iColorBack, iColorFore);
		m_commPort=null;   // Leave this as null until one gets opened.
        m_strAsyncRecvQueue=new LinkedList<String>();

        m_inputLineBuffer=new byte[MAX_BYTES_INPUT_BUFFER];
        m_inputLineBufferIndex=0;
        m_keyAccepter = new ASC_KeyCharAccepter(m_commPort);
        m_statusTextListeners = new LinkedList<IStatusTextAccepter>();
        m_strPreferredSerialPortSettings=strPreferredSerialPortSettings;
        m_bAutoConnect=bAutoConnect;
	}


	public void CloseSerialPort() 
	{
		if(isPortOpen())
		{
		  removeKeyboardCharAccepter(m_keyAccepter);
		  m_keyAccepter.setEnable(false);
		  m_keyAccepter.setPort(null);
		  closeCommPort();
		}
	}
	
	protected void sendStatusTextToListeners(String str)
	{
        Iterator<IStatusTextAccepter> iter = m_statusTextListeners.iterator();
        while(iter.hasNext())
        {
            iter.next().SetStatusText(str);
        }
	}
	
	/**
	 * This function attempts to open a serial port without starting up the open dialog,
	 * based on a settings string with the serial port name and settings.  It could be 
	 * that the port doesn't exist (IE a USB serial port isn't plugged in), or
	 * perhaps the port is in use, or there is something wrong with the settings
	 * string, in which case the port will not be opened and a null will be returned.
	 * @param strSettings
	 * @return a settings object if opened ok, or null if anything went wrong.
	 */
    public SerialPortSettingsContainer 
       tryToOpenPortWithoutCreatingDialog(String strSettings) throws ExceptionAnsiSerialConsole
    {

    	SerialPortSettingsContainer settings=null;
    	CommPortIdentifier commID=null;
    	String status;
    	
    	// Try to read the settings from the string.  If we run into a problem, 
    	// we forget the whole thing and return false
    	try {
			settings = new SerialPortSettingsContainer(strSettings);
		} catch (ExceptionSettingsParse e) {
          throw new ExceptionAnsiSerialConsole(
        	 ExceptionAnsiSerialConsole.ID_PORT_BAD_SETTINGS,
        	 "(Unknown)",
        	 "Bad Port Settings String: " + strSettings
          );
		}
		
		// If the above went ok, then see if the comm port we want exists on this
		// system.

		{
		    commID = SerialPortOpenDialog.findCommPortByName(settings.name);
		    if(null==commID)
		    {
		        status = new String("Cannot Open Port Because Port " + settings.name + 
		        		 " does not exist on this sytem.");
		        throw new ExceptionAnsiSerialConsole(
		          ExceptionAnsiSerialConsole.ID_PORT_DOES_NOT_EXIST,
		          settings.name,
		          status
		        );
		    }
		}
		
		// If the comm port exists, then we try to open it.

		{
		      try {
		          m_commPort = (SerialPort) commID.open("SerialPort", 1000);
		      } catch (PortInUseException e1) {
		         status = new String("Cannot Open Because Port " + commID.getName() + 
		        		 " is in Use by another Application.");
			        throw new ExceptionAnsiSerialConsole(
					          ExceptionAnsiSerialConsole.ID_PORT_OPEN_FAILURE,
					          settings.name,
					          status
					        );
		      }
		}
		
		// Port is now open.  Now we set the settings to what we want.

		{
			try {
				m_commPort.setSerialPortParams(
				  settings.baudRate,
				  settings.dataBits,
				  settings.stopBits,
				  settings.parity);
			} catch (UnsupportedCommOperationException e) {
                 m_commPort.close();
                 m_commPort=null;
		         status = new String("Port '" + commID.getName() + 
		        		 "' had could not be setup with setting string:" + strSettings);
			        throw new ExceptionAnsiSerialConsole(
					          ExceptionAnsiSerialConsole.ID_PORT_SETTINGS_FAILURE,
					          settings.name,
					          status
					        );
			}
			   m_settings = settings;
	           setUpSerialPortDataListener();
	           m_keyAccepter.setPort(m_commPort);
	           m_keyAccepter.setEnable(true);
	           
	           this.addKeyboardCharAccepter(m_keyAccepter);
	           
	           setFocus();  // Make sure this control has the keyboard focus.
	           

	               String str;
	               str = settings.name + " opened (" + settings.toString() + ")";
	               sendStatusTextToListeners(str);
			
		}

    	return settings;
    }
    
    public void setAutoConnect(boolean b)
    {
    	m_bAutoConnect=b;
    }
    
	public String OpenAndSetUpSerialPort() 
	{
		  String sRet=null;
		  boolean bOpenedAndSetup=false;
          SerialPortOpenDialog dlg=null;
          SerialPortSettingsContainer settings=null;
/*        
        if(m_bAutoConnect)
        {
        	if(null!=m_strPreferredSerialPortSettings)
        	{
        	  settings=tryToOpenPortWithoutCreatingDialog(m_strPreferredSerialPortSettings);
        	  if(null!=settings)
        	  {
        	    sRet=settings.toString();
        	    bOpenedAndSetup=true;
        	  }
        	}
        }
*/
        if(!bOpenedAndSetup)
        {
         dlg=new SerialPortOpenDialog(
            		getShell(),
            		null,
            		m_strPreferredSerialPortSettings,
            		m_bAutoConnect);
          dlg.open();
          m_commPort = dlg.getCommPort();
          if(null!=m_commPort)
          {
              setUpSerialPort(dlg);   
              settings = dlg.getSerialPortSettings();
              bOpenedAndSetup=true;
          }
        }
        if(bOpenedAndSetup)
        {

           setUpSerialPortDataListener();
           m_keyAccepter.setPort(m_commPort);
           m_keyAccepter.setEnable(true);
           
           this.addKeyboardCharAccepter(m_keyAccepter);
           
           setFocus();  // Make sure this control has the keyboard focus.
           

               String str;
               str = settings.name + " opened (" + settings.toString() + ")";
               sendStatusTextToListeners(str);
               sRet= new String(settings.toString());
               m_settings=settings;

//           m_slm.setMessage(dlg.getSettingsString() + " Opened.");
        } else
        {
//            m_slm.setMessage("No Serial Port Selected");
        }
        return sRet;
	}
	
    /**
     * setUpSerialPort reads the required info from the dialog box and applies the settings
     * to the serial port.
     */
    protected void setUpSerialPort(SerialPortOpenDialog dlg)
    {
        if(null!=m_commPort)
        {
               try {
                   int iBaudRate;
                   int iDataBits;
                   int iStopBits;
                   int iParity;
                   int iFlowControl;
                   
                   iBaudRate=dlg.getBaudRate();
                   iDataBits=dlg.getDataBits();
                   iStopBits=dlg.getStopBits();
                   iParity=dlg.getParity();
                   iFlowControl = dlg.getFlowControl();
                  m_commPort.setSerialPortParams(iBaudRate, iDataBits, iStopBits, iParity);
                  m_commPort.setFlowControlMode(iFlowControl);
              } catch (UnsupportedCommOperationException e2) {
                  // TODO Auto-generated catch block
                  e2.printStackTrace();
                  return;
              }
              

              
        } // if null!=m_commPort
    } // method setUpSerialPort()
    /**
     * setUpSerialPortDataListener creates an object for listing to data
     * events coming in from the serial port.   These are queued and
     * then an async execution object is created to forward the characters
     * to the console text window.
     */
    protected void setUpSerialPortDataListener()
    {
        if(null==m_commPort) return;
        
        m_display=Display.getCurrent();
        
        // We definitely want to know when more data comes in.
        m_commPort.notifyOnDataAvailable(true);
        
        try {
            m_commPort.addEventListener( 
                 new SerialPortEventListener()
                 {


                    public void serialEvent(SerialPortEvent e) 
                    {
                        switch(e.getEventType())
                        {
                        case SerialPortEvent.DATA_AVAILABLE:
                            InputStream iStream;
                            try {
                                iStream= m_commPort.getInputStream();
                                int iByte;

                                // If we are sure we won't block, read a bunch of chars at once.
                                if(0<iStream.available())
                                {
                                	m_inputLineBufferIndex=iStream.read(m_inputLineBuffer);
                                	
                                } else
                                {
                                  // If available() reported 0 bytes, then we'll try reading bytes
                                  // one at a time, because some input streams always report 0
                                  // according to the doc.;
                                  while( 0 <= (iByte = iStream.read()) )
                                  {
                                     m_inputLineBuffer[m_inputLineBufferIndex]=(byte) iByte;
                                     if(MAX_BYTES_INPUT_BUFFER>m_inputLineBufferIndex+1) m_inputLineBufferIndex++;
                                  } // while (more characters in input stream.)
                                }
                                if(0 < m_inputLineBufferIndex)
                                {
                                	serialDataAvailableHandler(m_inputLineBuffer, m_inputLineBufferIndex);
                                }
                                m_inputLineBufferIndex=0;  // Clear queued characters.

                            } catch (IOException e1) {
                                // TODO Auto-generated catch block
                                e1.printStackTrace();
                                return;
                            }
                        
                            
                            break;
                        case SerialPortEvent.OUTPUT_BUFFER_EMPTY:
                            break;
                        default:
                            break;
                        }
                        
                    }
                     
                 } // new SerialPortListener anonymous class
              );
        } catch (TooManyListenersException e1) {
            // This should never ever happen.
            e1.printStackTrace();
        }
        
    } // method setUpSerialPortDataListener()
        
    
    /**
     * serialDataAvailableHandler handles an array of incoming bytes.   The 
     * array might be bigger than the contents, hence the nBytesRead parameter.
     * This handler queues the bytes for appending into the m_text control.
     * @param inBytes  bytes coming in from the serial port.
     * @param nBytesRead number of bytes present in the array.
     */
    protected void serialDataAvailableHandler(byte[] inBytes, int nBytesRead)
    {
        if(nBytesRead>0)
        {
            // Place the string in a queue
            // the async output thread can capture it and send it
            // to the control.   
            synchronized(m_strAsyncRecvQueue)
            {
                // Don't let the queue fill up forever if there is a problem.
                if(m_strAsyncRecvQueue.size()<200)
                {
                       try {
                           m_strAsyncRecvQueue.add(
                           		  this.encode_bytes_into_string(inBytes, nBytesRead)
 //                                  new String(inBytes, 0, nBytesRead, "US-ASCII")
                                   );
                       } catch (UnsupportedEncodingException e) {
                           // TODO Auto-generated catch block
                           e.printStackTrace();
                       }
                }
            }
            
            m_display.asyncExec
            (
               new Runnable()
               {

                   public void run() 
                   {
                   	String str;
                       synchronized(m_strAsyncRecvQueue)
                       {
                          str=m_strAsyncRecvQueue.remove();
                          int i;
                          for(i=0; i<str.length(); i++)
                          {
                          	putChar(str.charAt(i));
                          } // for(i=0; ..)
                       } // synchronized()
                   } // run()
                   
               } // new Runnable()
            );  // m_display.asyncExec()
        } // if(nBytesRead > 0)
    } // method serialDataAvailableHandler()
    
    public boolean isPortOpen()
    {
    	return m_commPort != null;
    }
    
    public String getPortName()
    {
    	String strRet="(None)";
    	if(isPortOpen())
    	{
    		if(null != m_settings)
    		{
    			strRet=new String(m_settings.name);
    		}
    	}
    	return strRet;
    }
    
    /**
     * closeCommPort() basically just does m_commPort.close(),
     * but doesn't do anything if it is null and ignores
     * exceptions.
     */
	protected void closeCommPort()
	{
        if(null != m_commPort)
        {
            sendStatusTextToListeners(m_commPort.getName() + " closed.");
            try
            {
              m_commPort.close();
            } catch (Exception ee)
            {
                // Do nothing.
            }
            m_commPort=null;
        }
        
	} // End class closeCommPort()
    
	public void addStatusTextListener(IStatusTextAccepter sta)
	{
	    if( !m_statusTextListeners.contains(sta))
	    {
	      m_statusTextListeners.add(sta);
	    }
	}
	
	protected void remoteStatusTextListener(IStatusTextAccepter sta)
	{
	    m_statusTextListeners.remove(sta);
	}
	
}
