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

import org.eclipse.swt.graphics.Device;




	/**
	 * AnsiChar is a single character with a foreground color, a background color and an attribute
	 * @author Dad
	 *
	 */
	public class AnsiChar
	{
		public static final byte ATTR_NONE=0;
		public static final byte ATTR_FORE_HIGH_INTENSITY=1;
		public static final byte ATTR_BACK_HIGH_INTENSITY=2;
		public static final byte ATTR_NEGATIVE=4;
		public static final byte ATTR_HIDDEN=8;
		

		
		
		private byte cForegroundColor;
		private byte cBackgroundColor;
		private byte cAttr;
		char cChar;
		

		
		/**
		 * AnsiChar constructor for making a default char
		 */
		public AnsiChar()
		{
			cForegroundColor=AnsiCharColors.CLR_BLACK;
			cBackgroundColor=AnsiCharColors.CLR_WHITE;
			cAttr=ATTR_BACK_HIGH_INTENSITY;
			cChar=' ';
		}
		
		/**
		 * Constructor for making one from arguments
		 * @param ch
		 * @param clrForeground
		 * @param clrBackground
		 * @param attr
		 */
		public AnsiChar(char ch, byte clrForeground, byte clrBackground, byte attr)
		{
			cForegroundColor=clrForeground;
			cBackgroundColor=clrBackground;
			cAttr=attr;
			cChar=ch;
		}
		
		public AnsiChar(AnsiChar ch)
		{
			cForegroundColor=ch.getForegroundColor();
			cBackgroundColor=ch.getBackgroundColor();
			cAttr=ch.getAttr();
			cChar=ch.getChar();
		}
		

		public boolean equals(AnsiChar ac)
		{
			return (cForegroundColor==ac.cForegroundColor) &&
			       (cBackgroundColor==ac.cBackgroundColor) &&
			       (cAttr==ac.cAttr) &&
			       (cChar==ac.cChar);
		}
		
		/**
		 * equalAttrs returns true if ach has the same color and attributes as the object.
		 * @param ach
		 * @return
		 */
		public boolean equalAttrs(AnsiChar ach)
		{
			boolean bRet;
			bRet= ach.cAttr==cAttr;
			bRet &= ach.cForegroundColor == cForegroundColor;
			bRet &= ach.cBackgroundColor == cBackgroundColor;
			return bRet;
		}
		
		/**
		 * equalAttrs returns true if astr has the same color and attributes as the object.
		 * @param astr
		 * @return
		 */
		public boolean equalAttrs(AnsiCharString astr)
		{
			boolean bRet;
			bRet= astr.cAttr==cAttr;
			bRet &= astr.cForegroundColor == cForegroundColor;
			bRet &= astr.cBackgroundColor == cBackgroundColor;
			return bRet;
		}
		
		public byte getForegroundColor() { return cForegroundColor; }
		public byte getBackgroundColor() { return cBackgroundColor; }
		public byte getAttr() { return cAttr; }
		public void andAttr(int bits) { cAttr &= (byte) bits; }
		public void orAttr(int bits) { cAttr |= (byte) bits; }
		public char getChar() { return cChar; }
		
		public void setForegroundColor( int iColor )
		{
		   cForegroundColor = (byte) (iColor & 0x7);
		   if(0 != (0x8 & iColor))
		   {
		   	cAttr |= ATTR_FORE_HIGH_INTENSITY;
		   } else
		   {
		   	cAttr &= ~ATTR_FORE_HIGH_INTENSITY;
		   }
		}
		
		public void setBackgroundColor(int iColor)
		{
		   cBackgroundColor = (byte) (iColor & 0x7);
		   if(0 != (0x8 & iColor))
		   {
		   	cAttr |= ATTR_BACK_HIGH_INTENSITY;
		   } else
		   {
		   	cAttr &= ~ATTR_BACK_HIGH_INTENSITY;
		   }			
		}
		
		public void setAttr(int iAttr)
		{
			cAttr= (byte) iAttr;
		}
		
	} // End class AnsiChar
