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

import java.util.*;
/**
 * AnsiCharString for containing a string of like characters with the same
 * attributes and color
 * @author Dad
 *
 */
public class AnsiCharString
{
   byte cForegroundColor;
   byte cBackgroundColor;
   byte cAttr;
   String cChars;
   
   /**
    * Constructor for making a default with no characters in it.
    */
   public AnsiCharString()
   {
	   cForegroundColor=0;
	   cBackgroundColor=15;
	   cAttr=AnsiChar.ATTR_NONE;
	   cChars="";
   }
   
   /**
    * Constructor for making a string with default attributes
    * @param str
    */
   public AnsiCharString(String str)
   {
	   cForegroundColor=0;
	   cBackgroundColor=15;
	   cAttr=AnsiChar.ATTR_NONE;
	   cChars=str;
   }
   
   /**
    * Constructor for making a string from the parameters
    * @param str
    * @param clrForeground
    * @param clrBackground
    * @param attr
    */
   public AnsiCharString(String str, byte clrForeground, byte clrBackground, byte attr)
   {
		cForegroundColor=clrForeground;
		cBackgroundColor=clrBackground;
		cAttr=attr;
		cChars=str;
   }
   
   /**
    * This constructor constructs an AnsiCharString object from a subsection of an array
    * of AnsiChars.  It takes the attributes of the first AnsiChar and applies that to
    * the object, then adds only the characters.
    * @param acArr
    * @param offset
    * @param len
    */
   public AnsiCharString(AnsiChar[] acArr, int offset, int len)
   {
	   cForegroundColor=acArr[offset].getForegroundColor();
	   cBackgroundColor=acArr[offset].getBackgroundColor();
	   cAttr=acArr[offset].getAttr();

	   StringBuilder sb=new StringBuilder();
	   int i;
	   if(acArr.length - offset < len)
	   {
		   len=acArr.length - offset;
	   }
	   if(len>0)
	   {
		   for(i=0; i<len; i++)
		   {
			   sb.append(acArr[offset+i].getChar());
		   }
		   cChars= sb.toString();
	   } else
	   {
		   cChars=new String();
	   }
   }
   
   public void trimString()
   {
	   cChars.trim();
   }
   
   /**
    * Retrieve just the characters.
    * @return
    */
   public String getString()
   {
	   return cChars;
   }
   
   /**
    * append will append a character to the current string.
    * @param ch
    */
   public void append(char ch)
   {
	   cChars.concat(String.valueOf(ch));
   }
   
   /**
    * append will append a string to the current string.
    * @param str
    */
   public void append(String str)
   {
	   cChars.concat(str);
   }
}  // end class AnsiCharString

