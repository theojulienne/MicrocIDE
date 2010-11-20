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


import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IContributionItem;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.StatusLineManager;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.resource.FontRegistry;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.window.ApplicationWindow;
import org.eclipse.swt.events.ShellEvent;
import org.eclipse.swt.events.ShellListener;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.*;
import org.eclipse.swt.*;

import com.apple.eawt.Application;

import boldinventions.ansi_console.*;
import boldinventions.ansi_console.serial.*;

import java.io.UnsupportedEncodingException;
import java.util.prefs.*;


public class BirdTerm extends ApplicationWindow implements IStatusTextAccepter
{
   public static final String m_strVersion="1.0";
   
   protected FontRegistry m_fontRegistry; 
    StatusLineManager m_slm = new StatusLineManager();
    AnsiSerialConsole m_console;
    
    // Remember the parent so we can pick up a size later
    Composite m_compositeParent;
    
    AppSettings m_appSettings;
    
    protected static final String TEXT_TITLE = "Serial Port Terminal";
    
    
    /**
     * RunnableTryToAutoConnect contains a run() method which tries to 
     * get the last the serial port used and open it with the settings last
     * used.   If there is a problem, an ExceptionAnsiSerialConsole will be 
     * thrown.  This class contains message dialogs for the user if that 
     * occurs.
     * @author kevin
     *
     */
    protected class RunnableTryToAutoConnect implements Runnable
    {
		public void run() {
		      SerialPortSettingsContainer settings=null;
		      boolean bGotException=false;
			  try
			  {
                 settings = m_console.tryToOpenPortWithoutCreatingDialog(
            		          m_appSettings.getLastSerialPortSettings());
			  } catch ( ExceptionAnsiSerialConsole easc)
			  {
			      settings=null;
	    	      bGotException=true;
				  switch(easc.getID())
				  {

				      case ExceptionAnsiSerialConsole.ID_PORT_DOES_NOT_EXIST:
				    	  MessageDialog.openError(
				    			  getShell(),
				    			  "Serial Port Does Not Exist: " + easc.getPortName(),
				    			  easc.getMessage());

				    	  break;
				    	  
				      case ExceptionAnsiSerialConsole.ID_PORT_OPEN_FAILURE:
				    	  MessageDialog.openError(
				    			  getShell(),
				    			  "Could not open port: " + easc.getPortName(),
				    			  "Perhaps another application is using it, or there is a hardware failure.");
				    	  break;

				      case ExceptionAnsiSerialConsole.ID_PORT_SETTINGS_FAILURE:
				    	  MessageDialog.openError(
				    			  getShell(),
				    			  "Port: " + easc.getPortName() + " could not set desired settings.",
				    			  easc.getMessage());
				    	  break;
				      
				      case ExceptionAnsiSerialConsole.ID_UNKNOWN:
				      default:
				    	  MessageDialog.openError(
				    			  getShell(),
				    			  "Unknown Error opening Port: " + easc.getPortName(),
				    			  easc.getMessage());
					  break;
					  
				    	  
				  }
			  }
             if(null==settings)
             {
            	 if(!bGotException)
            	 {
                    MessageDialog.openInformation(getShell(), 
                    "Auto Open Port '" + m_appSettings.getLastSerialPortSettings() + "' Failed.", 
                    "Perhaps the port did not exist, or it is in use.");
            	 }
             }
             
             if(bGotException)
             {
            	 boolean bTurnOff=MessageDialog.openQuestion(getShell(),
            	   "Auto Connect to Port with settings: " + 
            	       m_appSettings.getLastSerialPortSettings() + " failed.",
            	   "Should Auto Connect be turned off?");
            	 if(bTurnOff) 
                 {
            		 m_appSettings.setAutoConnect(false);
            		 m_menuItemAutoConnect.setChecked(false);
                 }
             }
             
             if( (null!=settings) && !bGotException)
             {
            	 informPortIsOpen(true);
 
             }
		}
    	
    } // class RunnableTryToAutoConnect
    
    protected void informPortIsOpen(boolean bOpen)
    {
    	if(bOpen)
    	{
      	    getShell().setText(m_console.getPortName() + " - " + TEXT_TITLE);  
    		m_menuItemOpenPort.setEnabled(false);
    		m_menuItemOpenPort.setToolTipText("Must close currently open Serial Port in order to open one");
    		m_menuItemClosePort.setEnabled(true);
    		m_menuItemClosePort.setToolTipText("Closes the currently open serial port");
    	} else
    	{
    		getShell().setText(TEXT_TITLE + " (No Port Open)");
    		m_menuItemOpenPort.setEnabled(true);
    		m_menuItemOpenPort.setToolTipText("Opens a dialog for opening a serial port");
    		m_menuItemClosePort.setEnabled(false);
    		m_menuItemClosePort.setToolTipText("Cannot close the port because none is open");
    	}

    }
    
    public BirdTerm(Shell parentShell) 
    {
       super(parentShell);
       
        m_appSettings = new AppSettings();
        addStatusLine();
        addMenuBar();
    }
    
    @Override
    protected void handleShellCloseEvent()
    {
   	 m_appSettings.setAppBounds(m_compositeParent.getBounds());
   	 m_appSettings.setIColorTextBackground(m_console.getBackgroundColorCode());
   	 m_appSettings.setIColorTExtForeground(m_console.getForegroundColorCode());
   	 m_appSettings.setNColsTextConDoc(m_console.getNumColumns());
   	 m_appSettings.setNRowsTextConDoc(m_console.getNumRows());
   	 m_appSettings.setLocalEcho(m_console.getLocalEcho());
   	 m_appSettings.saveLastWindowPos();
   	 m_appSettings.savePreferences(); // TODO: for now we will just always save them.
        super.handleShellCloseEvent();
    }
    
    protected Control createContents(Composite parent)
    {
       m_compositeParent=parent;


       getShell().setText(TEXT_TITLE);

        // Set the size and location of the frame
//        parent.setBounds(windowX, windowY, windowWidth, windowHeight);
       parent.setBounds(m_appSettings.getAppBounds());
       
        // Create a font registry to make font access more simple
       m_fontRegistry = JFaceResources.getFontRegistry();
       
       if ( app.Application.isMac() ) {
    	   m_fontRegistry.put("code", new FontData[]{new FontData("Monaco", 12, SWT.BOLD)});
       } else {
    	   m_fontRegistry.put("code", new FontData[]{new FontData("Courier New", 12, SWT.BOLD)});
       }
//       m_fontRegistry.put("code", new FontData[]{new FontData("DejaVu Sans Mono", 10, SWT.BOLD)});
              
        // Initialize global colors
      AnsiCharColors.create(parent.getDisplay());
        
      // Create the text console control.
        m_console=new AnsiSerialConsole(parent, SWT.None, 
          m_appSettings.getNRowsTextConDoc(),
          m_appSettings.getNColsTextConDoc(),
          m_appSettings.getIColorTextBackground(),
          m_appSettings.getIColorTextForeground(),
          m_appSettings.getLastSerialPortSettings(),
          m_appSettings.getAutoConnect()
        );
        
        // Set it to a non-proportional font
        m_console.setFont(m_fontRegistry.get("code"));
        
        // Set Status Message updater
        m_console.addStatusTextListener(this);
        
        m_console.setLocalEcho(m_appSettings.getLocalEcho());
        
        // Default system status
        m_slm.setMessage("No Serial Port Open");

        // Set up for shell close so we can make sure the comm port gets closed on exit.
        addListeners();
        
        // If we are supposed to be auto connecting, let's give it a try.
        if(m_appSettings.getAutoConnect() && (null != m_appSettings.getLastSerialPortSettings()))
        {
          parent.getDisplay().asyncExec(
             new RunnableTryToAutoConnect()
          );
        }
        
        informPortIsOpen(false);  // Set the enable and tooltip text for open and close menu items.
        
        // We can either return 'parent' or our text canvas.  I do not understand
        // why would do one or the other.
        return m_console;
    }

    /**
     * @param args
     */
    public static void main(String[] args) 
    {
        BirdTerm window = new BirdTerm(null);
        
        window.setBlockOnOpen(true);
        window.open();

      Display.getCurrent().dispose();
    }
    
    protected StatusLineManager createStatusLineManager()
    {
        return m_slm;
    }
    
    class ActionLocalEcho extends Action
    {
        public ActionLocalEcho(String name, boolean bInitialState)
        {
           super(name, SWT.CHECK);
           this.setChecked(bInitialState);
        }
        
        public void run()
        {
            m_console.setLocalEcho(!m_console.getLocalEcho());
        }
    }
    
    class ActionAutoConnect extends Action
    {
        public ActionAutoConnect(String name, boolean bInitialState)
        {
           super(name, SWT.CHECK);
           this.setChecked(bInitialState);
        }
        
        public void run()
        {
        	m_appSettings.setAutoConnect(!m_appSettings.getAutoConnect());
        }
    }
    
    protected ActionUTF8Encode m_actionEncodeUTF8=null;
    protected ActionCP437Encode m_actionEncodeCP437=null;
    
    class ActionUTF8Encode extends Action
    {
   	 public ActionUTF8Encode(String name, boolean bInitialState)
   	 {
   		 super(name, SWT.CHECK);
   		 this.setChecked(bInitialState);
   	 }
   	 
   	 public void run()
   	 {
   		 if(isChecked())
   		 {
   			 m_appSettings.setEncodingType(AnsiSerialConsole.ENCODE_SERIAL_BYTES_AS_UTF8);
   			 m_console.setByteEncoding(AnsiSerialConsole.ENCODE_SERIAL_BYTES_AS_UTF8);
   			 m_actionEncodeCP437.setChecked(false);
   		 }
   	 }
    }
    
    class ActionCP437Encode extends Action
    {
   	 public ActionCP437Encode(String name, boolean bInitialState)
   	 {
   		 super(name, SWT.CHECK);
   		 this.setChecked(bInitialState);
   	 }
   	 public void run()
   	 {
   		 if(isChecked())
   		 {
   			 m_appSettings.setEncodingType(AnsiSerialConsole.ENCODE_SERIAL_BYTES_AS_CP437);
   			 m_console.setByteEncoding(AnsiSerialConsole.ENCODE_SERIAL_BYTES_AS_CP437);
   			 m_actionEncodeUTF8.setChecked(false);
   		 }
   	 }
    }
    
    protected void closePort()
    {
    	getShell().setText(TEXT_TITLE);
    	m_console.CloseSerialPort();
    }
    
    ActionAutoConnect m_menuItemAutoConnect=null;
    Action m_menuItemOpenPort=null;
    Action m_menuItemClosePort=null;

    
    protected MenuManager createMenuManager()
    {
        MenuManager mainMenu = new MenuManager(null);
        MenuManager actionMenu = new MenuManager("Comm");
        MenuManager viewMenu = new MenuManager("Screen");
        MenuManager helpMenu = new MenuManager("Help");
        
        mainMenu.add(actionMenu);
        mainMenu.add(viewMenu);
        mainMenu.add(helpMenu);
        
        m_menuItemOpenPort=
        new Action("Open Serial Port")
        {
            public void run()
            {
            	m_console.setAutoConnect(m_appSettings.getAutoConnect());
                m_appSettings.setLastSerialPortSettings(m_console.OpenAndSetUpSerialPort());
                if(m_console.isPortOpen())
                {
                	informPortIsOpen(true);
                }
            }
        };
        actionMenu.add(
        		m_menuItemOpenPort
        );
        
        m_menuItemClosePort=new Action("Close Serial Port")
        {
            public void run()
            {
                closePort();
                informPortIsOpen(false);
            }
        };
        
        actionMenu.add(
          m_menuItemClosePort
        );
        
        m_menuItemAutoConnect=new ActionAutoConnect("Auto Connect", m_appSettings.getAutoConnect());
        actionMenu.add(	m_menuItemAutoConnect );

/*      
        final MenuItem menuItemLocalEcho = new MenuItem(actionMenu.getMenu(), SWT.CHECK);
        menuItemLocalEcho.setText("Local Echo");
        menuItemLocalEcho.addListener(SWT.Selection, 
           new Listener()
           {

            @Override
            public void handleEvent(Event event) 
            {
               m_console.setLocalEcho(menuItemLocalEcho.getSelection());
            }
            
           }
        );
*/      
        
        actionMenu.add(
           new ActionLocalEcho("Local Echo", m_appSettings.getLocalEcho())
           {
               public void run()
               {
                   m_console.setLocalEcho(!m_console.getLocalEcho());
               }
           }
        );

        
        
        actionMenu.add(
              new Action("Exit")
              {
                  public void run()
                  {
                       m_console.CloseSerialPort();
                       handleShellCloseEvent(); 
                  }
              }
            );
        
        viewMenu.add(
        new Action("Set Text Color")
        {
            public void run()
            {
                DlgTextColorChooser dlg=new DlgTextColorChooser(getShell(), 
                        m_console.getBackgroundColorCode(), m_console.getForegroundColorCode(),
                        m_console);
                dlg.open();
            }
        }
        );
        
        viewMenu.add(
                new Action("Clear Screen")
                {
                    public void run()
                    {
                        m_console.clearScreen();
                        m_console.setCursorXY(0,0);
                    }
                }
                );     
        
        m_actionEncodeCP437=new ActionCP437Encode("Encode As Ascii", 
      		  AnsiSerialConsole.ENCODE_SERIAL_BYTES_AS_CP437 == m_appSettings.getEncodingType());

        viewMenu.add(
          m_actionEncodeCP437
        );
        
        m_actionEncodeUTF8=new ActionUTF8Encode("Encode As UTF8", 
      		  AnsiSerialConsole.ENCODE_SERIAL_BYTES_AS_UTF8 == m_appSettings.getEncodingType());

        viewMenu.add(m_actionEncodeUTF8);
        
        
        viewMenu.add(
          new Action("Unicode Drawing Chars")
          {
         	 public void run()
         	 {
         		 char ch = '\u2500';
         		 int i;
         		 int j;
         		 for(i=0; i<16; i++)
         		 {
         			 for(j=0; j<16; j++)
         			 {
         				 m_console.putChar(ch);
         				 ch++;
         			 }
         			 m_console.putChar(SWT.CR);
         			 m_console.putChar(SWT.LF);
         		 }
         	 }
          }
        );
        viewMenu.add(
      	 new Action("255 chars")
      	 {
      		 public void run()
      		 {
      			 byte[] b;
      			 int i;
      			 int ii;
      			 String str;
      			 b=new byte[16];
      			 for(i=2; i<16; i++)
      			 {
      				 for(ii=0; ii<16; ii++)
      				 {
      					 b[ii]=(byte)(ii+16*i);
      					 
      				 }
      					 try {
	                     str = new String(b, 0, 16, "UTF8");
                     } catch (UnsupportedEncodingException e) {
	                     // TODO Auto-generated catch block
	                     e.printStackTrace();
	                     break;
                     }
                     for(ii=0; ii<str.length(); ii++)
                     {
                        m_console.putChar(str.charAt(ii));
                     }
//      					 m_console.putChar( (char) b );

      				 m_console.putChar(SWT.CR);
      				 m_console.putChar(SWT.LF);
      			 }
      		 }
      	 }
        );
        
        helpMenu.add
        (
           new Action("About")
           {
               public void run()
               {
                   MessageDialog.openInformation(getShell(), 
                           "BirdTerm",
                           app.Application.getInstance().getName() + " uses the BirdTerm Serial Terminal Program (" + m_strVersion + ")" +
                           "\r\nBirdTerm is a Free Open Source Serial Terminal Program from BoldInventions.com\r\n\n" +
                            "http://www.boldinventions.com or search http://sourceforge.net for 'BirdTerm");
               }
           }
        );
        
        return mainMenu;
    }


    public void SetStatusText(String str) 
    {
       m_slm.setMessage(str);    
    }
/*    
    protected void saveWindowSizeAndPosition()
    {
        if((null!=m_compositeParent) && (null!=myPreferences))
        {
          Rectangle rect= m_compositeParent.getBounds();
          myPreferences.putInt(WINDOW_WIDTH_KEY, rect.width);
          myPreferences.putInt(WINDOW_HEIGHT_KEY, rect.height );
          myPreferences.putInt(WINDOW_X_KEY, rect.x);
          myPreferences.putInt(WINDOW_Y_KEY, rect.y);
        }
    }
*/    
    public void addListeners()
    {
        getShell().addShellListener(
                new ShellListener() 
                {


                 public void shellActivated(ShellEvent e) 
                 {
                     // TODO Auto-generated method stub
                 }


                 public void shellClosed(ShellEvent e) {

                     m_console.CloseSerialPort();
 //                    saveWindowSizeAndPosition();
                 }


                 public void shellDeactivated(ShellEvent e) {
                     // TODO Auto-generated method stub
                     
                 }

                 public void shellDeiconified(ShellEvent e) {
                     // TODO Auto-generated method stub
                     
                 }


                 public void shellIconified(ShellEvent e) {
                     // TODO Auto-generated method stub
                     
                 }
                    
                }
             );
    } // End Method addListeners()
    

}
