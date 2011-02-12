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


/**
 * ITextConsoleChangeListener is an interface for communicating changes to a 
 * TextConsoleDocument.  
 * @author Dad
 *
 */
public interface ITextConsoleDocChangeListener 
{
	public static final int ATTR_NONE=0;
	public static final int ATTR_CHAR=1;
	public static final int ATTR_COLOR=2;
	public static final int ATTR_ATTR=4;
	public static final int ATTR_NEWLINE=8;
	
/**
 * documentChanged reports a change to a TextConsoleDocument.  The attribute indicates
 * what changes have been made to the document.
 * @param doc the document which changed
 * @param iRowMin min row in the document which changed
 * @param iColMin min col in the document which changed
 * @param iRowMax max row of changes
 * @param iColMax max col of changes
 * @param iChangeAttr  attribute indicating what changed about the document.
 */
   public void documentChanged(TextConsoleDocument doc, 
   		int iRowMin, int iColMin, int iRowMax, int iColMax, int iChangeAttr);
}
