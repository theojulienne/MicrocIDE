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


import java.io.*;

import org.eclipse.swt.graphics.Font;

/**
 * ITextConsole is an interface for a text console which can display
 * a document with colored text, scroll up and down, and 
 * has a visible cursor which can be placed anywhere in the document.
 * @author Dad
 *
 */


public interface ITextConsole 
{
	public int getNumColumns();
	public int getNumRows();
	
	public int getCursorX();
	public int getCursorY();
	
	public int setCursorXY(int iCol, int iRow);
	public int moveCursorRelative( int iDelCol, int iDelRow);
	
	public int sendString( String str );
	
   public AnsiChar getCharAt(int iCol, int iRow);
	
   public void setFont(Font font);

	public int putCharAt( AnsiChar ach, int iCol, int iRow );
	
	public int putCharAtCursor( AnsiChar ach );
	
	/**
	 * The normal routine to accept a character.  Special characters like CR or LF will be
	 * interpreted, if the underlying object supports it.
	 * @param ch
	 */
	public void putChar( char ch );
	
	public boolean setFocus();
	
	public int scrollUp(int nLines);
	public int scrollDown(int nLines);
	
	public int clearScreen();
	
	public int clearSome(int iColStart, int iColEnd, int iRow);
	
	public void addKeyboardCharAccepter(ICharArrayAccepter writer);

	
	public void removeKeyboardCharAccepter(ICharArrayAccepter writer);

   public byte getForegroundColorCode();
   public byte getBackgroundColorCode();
   
}
