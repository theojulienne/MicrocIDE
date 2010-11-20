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

package boldinventions.ansi_console;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Composite;

import java.io.UnsupportedEncodingException;
import java.util.*;

/**
 * AnsiTextConsole is a TextConsole which can accept ANSI sequence
 * input and applies color changes or cursor movement.   This original
 * implementation only implements a subset of the full ANSI spec.
 * @author kevin
 *
 */
public class AnsiTextConsole extends TextConsole 
    implements ITextConsoleDocChangeListener, ITextConsole, ICharArrayAccepter
{
	public static final int STATE_NORMAL=0;
	public static final int STATE_WAIT_BRACKET=1;
	public static final int STATE_WAIT_PARM1_OR_CMD=2;
	public static final int STATE_WAIT_PARM2_OR_CMD=3;
	
	public static final int ENCODE_SERIAL_BYTES_AS_UTF8=1;
	public static final int ENCODE_SERIAL_BYTES_AS_CP437=2;
	
	public static final char ESC = '\u001b';
	public static final char LBRACKET = '[';
	public static final char SEMICOLON = ';';

	protected int state;
	protected int m_parm1;
	protected int m_parm2;
	
	protected int m_iEncodingType;
	
	protected StringBuilder m_sbParm1;
	protected StringBuilder m_sbParm2;
	
	public int m_iCursorSaveCol=1;
	public int m_iCursorSaveRow=1;
	
   Object m_lockCharAccept;
	
	protected LinkedList<AnsiCommand> m_listCommands;
	
	protected LocalEchoAccepter m_localEcho;
	
    class LocalEchoAccepter implements  ICharArrayAccepter
    {
        boolean m_bEnable;
        
        public LocalEchoAccepter()
        {
           m_bEnable=false;   
        }
        
        public LocalEchoAccepter(boolean bEnable)
        {
            addKeyboardCharAccepter(this);
            m_bEnable=bEnable;
        }
        
        public void setEnable(boolean bEnable)
        {
           if(!bEnable)
           {
               if(m_bEnable)
               {
                   removeKeyboardCharAccepter(this);
               }
           } else
           {
               if(!m_bEnable)
               {
                   addKeyboardCharAccepter(this);
               }
           }
           m_bEnable=bEnable;
        }
        
        public boolean getEnable()
        {
            return m_bEnable;
        }


        public void acceptChar(char ch) 
        {
           putChar(ch); // Call AnsiTextConsole.putChar();
        }

        public void acceptChars(char[] chArr, int iOffset, int iLen) 
        {
            int i;

            for(i=0; i<iLen; i++)
            {
                acceptChar(chArr[iOffset+i]);
            }
            
        }
        
    }
	
	abstract class AnsiCommand
	{
		public static final int ATTR_CURSOR_MOVEMENT=1;
		public static final int ATTR_ERASE=2;
		public static final int ATTR_COLOR=3;
		public static final int ATTR_REPORT=4;
		char ch;
		int nParams;
		int attr;
		
		public AnsiCommand(char theChar, int the_nParams, int the_attr)
		{
			ch=theChar;
			nParams=the_nParams;
			attr=the_attr;
		}
		
		public abstract  void doCommand(int iParm1, int iParm2, ITextConsole oTextConsole);
	}
	
	/**
	 * AnsiCommandCUP implements the Ansi CUP command, which moves the cursor to an XY position
	 * @author Dad
	 *
	 */
   class AnsiCommandCUP extends AnsiCommand
   {
   	public AnsiCommandCUP()
   	{
   		super('H', 2, ATTR_CURSOR_MOVEMENT);
   	}

		@Override
      public void doCommand(int iParm1, int iParm2, ITextConsole oTextConsole) 
		{
	      oTextConsole.setCursorXY(iParm2, iParm1);
      }
   }
   
   /**
    * AnsiCommandHVP implements the Ansi HVP command, which moves the cursor to an XY position
    * @author Dad
    *
    */
   class AnsiCommandHVP extends AnsiCommand
   {
   	public AnsiCommandHVP()
   	{
   		super('f', 2, ATTR_CURSOR_MOVEMENT);
   	}

		@Override
      public void doCommand(int iParm1, int iParm2, ITextConsole oTextConsole) 
		{
	      oTextConsole.setCursorXY(iParm2, iParm1);
      }
   }
   
   /**
    * AnsiCommandCUU moves the cursor up
    * @author Dad
    *
    */
   class AnsiCommandCUU extends AnsiCommand
   {
   	public AnsiCommandCUU()
   	{
   		super('A', 1, ATTR_CURSOR_MOVEMENT);
   	}

		@Override
      public void doCommand(int iParm1, int iParm2, ITextConsole oTextConsole) 
		{
			if(0==iParm1) iParm1=1;
        oTextConsole.moveCursorRelative(0, 0-iParm1);
      }
   }
   
   /**
    * AnsiCommandCUD moves the cursor down
    * @author Dad
    *
    */
   class AnsiCommandCUD extends AnsiCommand
   {
   	public AnsiCommandCUD()
   	{
   		super('B', 1, ATTR_CURSOR_MOVEMENT);
   	}

		@Override
      public void doCommand(int iParm1, int iParm2, ITextConsole oTextConsole) 
		{
			if(0==iParm1) iParm1=1;
	      oTextConsole.moveCursorRelative(0, iParm1);
      }
   }
   
   /**
    * AnsiCommandCUF moves the cursor 'Forward' which means to the right
    * @author Dad
    *
    */
   class AnsiCommandCUF extends AnsiCommand
   {
   	public AnsiCommandCUF()
   	{
   		super('C', 1, ATTR_CURSOR_MOVEMENT);
   	}

		@Override
      public void doCommand(int iParm1, int iParm2, ITextConsole oTextConsole) 
		{
			if(0==iParm1) iParm1=1;
	      oTextConsole.moveCursorRelative(iParm1, 0);
      }
   }
   
   /**
    * AnsiCommandCUF moves the cursor 'Backward' which means to the left
    * @author Dad
    *
    */
   class AnsiCommandCUB extends AnsiCommand
   {
   	public AnsiCommandCUB()
   	{
   		super('D', 1, ATTR_CURSOR_MOVEMENT);
   	}

		@Override
      public void doCommand(int iParm1, int iParm2, ITextConsole oTextConsole) 
		{
			if(0==iParm1) iParm1=1;
	      oTextConsole.moveCursorRelative(0-iParm1, 0);
      }
   }
   
   /**
    * AnsiCommandDSR causes the console to report it's current cursor position
    * @author Dad
    *
    */
   class AnsiCommandDSR extends AnsiCommand
   {
   	public AnsiCommandDSR()
   	{
   		super('n', 1, ATTR_REPORT);
   	}

		@Override
      public void doCommand(int iParm1, int iParm2, ITextConsole oTextConsole) 
		{
	      StringBuilder sb=new StringBuilder();
	      sb.append(ESC);
	      sb.append(LBRACKET);
	      sb.append(  oTextConsole.getCursorY() );
	      sb.append(';');
	      sb.append(  oTextConsole.getCursorX() );
	      sb.append('R');
	      oTextConsole.sendString(sb.toString());
      }
   }
   
   /**
    * AnsiCommandSCP saves the current cursor position.
    * @author kevin
    *
    */
   class AnsiCommandSCP extends AnsiCommand
   {
	   public AnsiCommandSCP()
	   {
		   super('s', 0, ATTR_CURSOR_MOVEMENT);
	   }

	@Override
	public void doCommand(int iParm1, int iParm2, ITextConsole oTextConsole) 
	{
		m_iCursorSaveCol=oTextConsole.getCursorX();
		m_iCursorSaveRow=oTextConsole.getCursorY();
	}
	   
   }
   
   /**
    * AnsiCommandRCP restores the cursor postion saved by an earlier SCP cmd.
    * @author kevin
    *
    */
   class AnsiCommandRCP extends AnsiCommand
   {
	   public AnsiCommandRCP()
	   {
		   super('u', 0, ATTR_CURSOR_MOVEMENT);
	   }

	@Override
	public void doCommand(int iParm1, int iParm2, ITextConsole oTextConsole) 
	{
		oTextConsole.setCursorXY(m_iCursorSaveCol, m_iCursorSaveRow);
	}
   }
   
   /**
    * AnsiCommandED clears the screen and places the cursor at the upper left corner
    * @author kevin
    *
    */
   class AnsiCommandED extends AnsiCommand
   {
	   public AnsiCommandED()
	   {
		   super('J', 1, ATTR_ERASE);
	   }

	@Override
	public void doCommand(int iParm1, int iParm2, ITextConsole oTextConsole) 
	{
		oTextConsole.clearScreen();
		oTextConsole.setCursorXY(0, 0);
	}
   }

   /**
    * AnsiCommandEL erases from the current cursor position until the end of the line.
    * @author kevin
    *
    */
   class AnsiCommandEL extends AnsiCommand
   {
	   public AnsiCommandEL()
	   {
		   super('K', 0, ATTR_ERASE);
	   }

	@Override
	public void doCommand(int iParm1, int iParm2, ITextConsole oTextConsole) 
	{
        oTextConsole.clearSome( oTextConsole.getCursorX(), oTextConsole.getNumColumns()-1, 
        		oTextConsole.getCursorY());
	}
   }
   
   /**
    * AnsiCommandSGR sets the default color and attributes to use
    * @author kevin
    *
    */
   class AnsiCommandSGR extends AnsiCommand
   {
	   public AnsiCommandSGR()
	   {
		   super('m', 2, ATTR_COLOR);
	   }

	   void setColorOrAttr(int iParm)
	   {
		   switch(iParm)
		   {
		   case 0:
			   m_acCurrentColor.setAttr(0);
			   break;
		   case 1:
			   m_acCurrentColor.orAttr( AnsiChar.ATTR_FORE_HIGH_INTENSITY );
			   break;
		   case 2:
			   m_acCurrentColor.andAttr(~AnsiChar.ATTR_BACK_HIGH_INTENSITY);
			   break;
		   case 3:
			   // unsupported: Italic
			   break;
		   case 4:
			   // unsupported: Underline
			   break;
		   case 5:
			   // unsupported: Blink slowly
			   break;
		   case 6:
			   // unsupported Blank rapid
			   break;
		   case 7:
			   m_acCurrentColor.orAttr( AnsiChar.ATTR_NEGATIVE );
			   break;
		   case 8:
			   m_acCurrentColor.orAttr( AnsiChar.ATTR_HIDDEN );
			   break;
		   case 21:
			   // unsupported double underline
			   break;
		   case 22:
			   m_acCurrentColor.andAttr(~AnsiChar.ATTR_FORE_HIGH_INTENSITY);
			   break;
		   case 24:
			   // unsupported underline none
			   break;
		   case 25:
			   // unsupported blink off
			   break;
		   case 27:
			   m_acCurrentColor.andAttr( ~AnsiChar.ATTR_NEGATIVE );
			   break;
		   case 28:
			   m_acCurrentColor.andAttr( ~AnsiChar.ATTR_HIDDEN );
			   break;
			   default:
				   if( (iParm>=30) && (iParm<38) )
				   {
				     m_acCurrentColor.setForegroundColor(iParm-30);
				     m_acCurrentColor.andAttr( ~AnsiChar.ATTR_FORE_HIGH_INTENSITY);
				   } else if( (iParm>=40) && (iParm<48))
				   {
					 m_acCurrentColor.setBackgroundColor(iParm-40);
				     m_acCurrentColor.andAttr( ~AnsiChar.ATTR_BACK_HIGH_INTENSITY );
				   } else if( (iParm>=90) && (iParm<98))
				   {
					     m_acCurrentColor.setForegroundColor(iParm-90);
					     m_acCurrentColor.orAttr( AnsiChar.ATTR_FORE_HIGH_INTENSITY );
				   } else if( (iParm>=100) && (iParm<108))
				   {
					     m_acCurrentColor.setBackgroundColor(iParm-100);
					     m_acCurrentColor.orAttr( AnsiChar.ATTR_BACK_HIGH_INTENSITY );
				   }
				   break;
		   } // switch(iParm)
	   } // method setColorOrAttr
	   
	@Override
	public void doCommand(int iParm1, int iParm2, ITextConsole oTextConsole) 
	{
       setColorOrAttr(iParm1);
       if(0!=iParm2) setColorOrAttr(iParm2);
	}
	  
   }  // class AnsiCommandSGR
   
	public AnsiTextConsole(Composite parent, int styles, int nRows, int nColumns, int iColorBack, int iColorFore) {
	   super(parent, styles, nRows, nColumns, iColorBack, iColorFore);
	   // TODO Auto-generated constructor stub
	   state=STATE_NORMAL;
	   
	   /*
	    * Initialize list of commands.
	    */
	   m_listCommands = new LinkedList<AnsiCommand>();
	   m_listCommands.add(new AnsiCommandCUB());
	   m_listCommands.add(new AnsiCommandCUD());
	   m_listCommands.add(new AnsiCommandCUF());
	   m_listCommands.add(new AnsiCommandCUP());
	   m_listCommands.add(new AnsiCommandCUU());
	   m_listCommands.add(new AnsiCommandDSR());
	   m_listCommands.add(new AnsiCommandED());
	   m_listCommands.add(new AnsiCommandEL());
	   m_listCommands.add(new AnsiCommandHVP());
	   m_listCommands.add(new AnsiCommandSCP());
	   m_listCommands.add(new AnsiCommandSGR());
	   
	   m_lockCharAccept=new Object();
	   m_localEcho = new LocalEchoAccepter(true);
	   m_iEncodingType=ENCODE_SERIAL_BYTES_AS_CP437;

   }

	
	protected void rawPutChar(char ch)
	{
		AnsiChar ac = new AnsiChar(ch, m_acCurrentColor.getForegroundColor(), 
				m_acCurrentColor.getBackgroundColor(), m_acCurrentColor.getAttr());
      putCharAtCursor(ac);
	}
	
	protected void putCharNormal(char ch)
	{
		if(ESC == ch)
		{
			state=STATE_WAIT_BRACKET;
			m_parm1=m_parm2=0;
		} else
		{
			switch(ch)
			{
			  case SWT.CR:
				 carriageReturn();
				break;
				
			  case SWT.LF:
				 lineFeed();
				break;
				
			  case (int) '\b':
				  moveCursorRelative(-1, 0);
				  break;
			
			  default:
	             rawPutChar(ch);
				break;
			}
		}
	}
	
	protected void putCharBracket(char ch)
	{
	    if(LBRACKET==ch)
	    {
	   	 m_sbParm1=new StringBuilder();
	   	 m_sbParm2=new StringBuilder();
	   	 state=STATE_WAIT_PARM1_OR_CMD;
	    } else
	    {
	   	 state=STATE_NORMAL;
	   	 rawPutChar(ch);
	    }
	}
	
	/**
	 * execAnsiCommand will search the list for a command with a matching command
	 * character.  It will then call the command's doCommand() function with the
	 * member variables m_parm1 and m_parm2 as the parameters for the command.
	 *   If the command char is not in the list, it does nothing.
	 * @param ch
	 */
    protected void execAnsiCommand(char ch)
    {
    	/*
    	 * First, see if this character is in our list of possible commands.
    	 */
    	Iterator<AnsiCommand> iter = m_listCommands.iterator();
    	
    	while(iter.hasNext())
    	{
    		AnsiCommand aCmd;
    		aCmd=iter.next();
    		if( aCmd.ch == ch )
    		{
    			aCmd.doCommand(m_parm1, m_parm2, this);
    			break;
    		}
    	}
    }
	
	protected void putCharParm1(char ch)
	{
		if(Character.isDigit(ch))
		{
			m_sbParm1.append(ch);
		} else
		{
			if(0<m_sbParm1.length())
			{
        	  m_parm1 = Integer.parseInt(m_sbParm1.toString());
			}
			// If the char is not a digit, then it must be a semi-colon or a 
			// command character.
            if(';' == ch)
            {
               state=STATE_WAIT_PARM2_OR_CMD;
            } else
            {
               // So it is either a command character or some other character
               // representing either a mistaken sequence or an unsupported 
               // command.
               execAnsiCommand(ch);
               state=STATE_NORMAL;
            }
			
		}
	}
	
	protected void putCharParm2(char ch)
	{
		if(Character.isDigit(ch))
		{
			m_sbParm2.append(ch);
		} else
		{
			if(0<m_sbParm2.length())
			{
        	  m_parm2 = Integer.parseInt(m_sbParm2.toString());
			}


               // So it is either a command character or some other character
               // representing either a mistaken sequence or an unsupported 
               // command.
               execAnsiCommand(ch);
               state=STATE_NORMAL;

		}
	}
	
	public void putChar( char ch )
	{
		int iTest=(int) ch;
	   synchronized(m_lockCharAccept)
	   {

		switch(state)
		{
		   case STATE_NORMAL:
		   	if(iTest != 0x555555) putCharNormal(ch);
			break;
			
		   case STATE_WAIT_BRACKET:
		   	putCharBracket(ch);
			break;
			
		   case STATE_WAIT_PARM1_OR_CMD:
		   	putCharParm1(ch);
			break;
			
		   case STATE_WAIT_PARM2_OR_CMD:
		   	putCharParm2(ch);
			break;
			
			default:
				break;
		}

	   }
	}
//StringBuilder m_sb=new StringBuilder();

	public void acceptChars(char[] chArr, int iOffset, int iLen) 
	{
		int i;
//		m_sb.append(chArr, iOffset, iLen);
		for(i=0; i<iLen; i++)
		{
			putChar(chArr[iOffset+i]);
		}
	}


	public void acceptChar(char ch) 
	{
		  putChar(ch);
	}
	
    public void setLocalEcho(boolean bEnable)
    {
       m_localEcho.setEnable(bEnable);   
    }
    
    public boolean getLocalEcho()
    {
        return m_localEcho.getEnable();
    }
    
    public void setByteEncoding(int iEncodingType)
    {
   	 m_iEncodingType=iEncodingType;
    }
    
    static final char[] UNICODE_BOX_DRAWING_CHARS=
    {
   	 '\u2502', '\u2524', '\u2561', '\u2562', '\u2556', '\u2555', '\u2563', '\u2551', '\u2557', '\u255d',
   	 '\u255c', '\u255b', '\u2511', '\u2515', '\u2534', '\u252c', '\u251c', '\u2500', '\u253d', '\u255e',
   	 '\u255f', '\u255a', '\u2554', '\u2569', '\u2566', '\u2560', '\u2550', '\u256c', '\u2567', '\u2568',
   	 '\u2564', '\u2565', '\u2559', '\u2558', '\u2552', '\u2553', '\u256b', '\u2568', '\u2518', '\u250c'
    };
    
    /**
     * translateBoxDrawingBytesToUnicode watches for bytes in the range 0xb3 to 0xda and
     * translates these to the unicode drawing characters.
     * @param bytes
     * @param nBytesToEncode
     * @return
     */
    public String translateBoxDrawingBytesToUnicode(byte[] bytes, int nBytesToEncode)
    {
   	 String sRet;
   	 StringBuilder sb=new StringBuilder();
   	 int i;
   	 char ch='a';
   	 for(i=0; i<nBytesToEncode; i++)
   	 {
   		 int ich= (int) bytes[i];

   		 if(ich<0)  // Fix up signed byte as unsigned.
   		 {
   			 ich+=256;
   		 }
   		 if( (ich >= 0xb3) && (ich<0xdb))
   		 {
   			  ch= UNICODE_BOX_DRAWING_CHARS[ich-0xb3];
   			 sb.append(ch);
   		 } else
   		 {
   			 sb.append((char) ich);
   		 }
   	 }
   	 sRet=sb.toString();
   	 return sRet;
    }
    
    public String encode_bytes_into_string(byte[] bytes, int nBytesToEncode) throws UnsupportedEncodingException
    {
   	 String strRet;

   	 switch(m_iEncodingType)
   	 {
   	 
   	 case ENCODE_SERIAL_BYTES_AS_UTF8:
   		 strRet=new String(bytes, 0, nBytesToEncode, "UTF8");
   		 break;
   	 case ENCODE_SERIAL_BYTES_AS_CP437:
   	 default:
   		 strRet=translateBoxDrawingBytesToUnicode(bytes, nBytesToEncode);
   		 break;
   	 }

   	 return strRet;
    }

}
