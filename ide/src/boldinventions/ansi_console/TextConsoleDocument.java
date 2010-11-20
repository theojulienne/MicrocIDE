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

import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;

/*
 * Note: In order to get the compiler to be happy, I had to add org.eclipse.text plugin to the
 * the org.eclipse.swt project that I created.  Otherwise it kept complaining about IDocument
 * not being resolved.
 */


public class TextConsoleDocument
{
	public class RowCache
	{
		LinkedList<AnsiCharString> m_listRow;
		
		public RowCache() { m_listRow=null; }
		public void setList(LinkedList<AnsiCharString> rowList) { m_listRow=rowList; }
		public LinkedList<AnsiCharString> getList() { return m_listRow; }
		public void clearList() { m_listRow=null; }
	}


	/**
	 * This is a object which all callers which need a stable document should 
	 * synchronize to while getting the document data, or changing the data.
	 */
	public Object m_docLock;
	
	LinkedList<ITextConsoleDocChangeListener> m_changeListenerList;
	
	protected RowCache[] m_rowCacheArray;

    protected int m_nColumns;
    protected int m_nRows;
    protected int m_iStartingRow;
    protected AnsiChar[][] m_chars;
    
    protected boolean m_bTextWrap;
    
    public int getNRows() {  return m_nRows; }
    public int getNCols() { return m_nColumns; }
    
    private void clearMemberVariables()
    {
    	m_nColumns=0;
    	m_nRows=0;
    	m_iStartingRow=0;
    	m_docLock=new Object();
    	m_changeListenerList=null;
    	m_bTextWrap=false;

    	m_chars=null;
    }
    
    public TextConsoleDocument()
    {
       clearMemberVariables();
   	initRowCache();
    }
    
    public TextConsoleDocument(int nRows, int nColumns, int iColorBackground, int iColorForeground)
    {
    	int iRow;
    	int iCol;

    	clearMemberVariables();
    	m_nRows = nRows;
    	m_nColumns= nColumns;
    	initRowCache();
    	m_chars=new AnsiChar[nRows][nColumns];
    	for(iRow=0; iRow<m_nRows; iRow++)
    	{
    		for(iCol=0; iCol<m_nColumns; iCol++)
    		{
//    			m_chars[iRow][iCol]=new AnsiChar();
    			if(iRow<m_nRows-1)
    			{
//       			m_chars[iRow][iCol]=new AnsiChar('0', (byte) 7, (byte) 0, (byte) AnsiChar.ATTR_NONE);
    				m_chars[iRow][iCol]=new AnsiChar(' ', (byte) iColorForeground, (byte) iColorBackground, (byte) AnsiChar.ATTR_NONE);
    			} else
    			{
//    			m_chars[iRow][iCol]=new AnsiChar('X', (byte) 7, (byte) 0, (byte) AnsiChar.ATTR_NONE);
    			m_chars[iRow][iCol]=new AnsiChar(' ', (byte) iColorForeground, (byte) iColorBackground, (byte) AnsiChar.ATTR_NONE);
    			}
    		}
    	}
    }

    protected void initRowCache()
    {
    	m_rowCacheArray = new RowCache[m_nRows];
    	for(int i=0; i<m_nRows; i++) { m_rowCacheArray[i] = new RowCache(); }
    }
    
    
    public AnsiChar getAnsiChar(int iDisplayRow, int iCol)
    {
     	int iRow;
    	iRow = (iDisplayRow + m_iStartingRow) % m_nRows;
    	return m_chars[iRow][iCol];
    }
    
    /**
     * getRow() makes a list of AnsiCharStrings which make up a line of text on the display.
     * Each string has a color and attribute.
     * @param iRow
     * @return
     */
    public LinkedList<AnsiCharString> getRowList(int iDisplayRow)
    {
    	int iCol;
    	int iRow;
    	LinkedList<AnsiCharString> list;
    	iRow = (iDisplayRow + m_iStartingRow) % m_nRows;
    	
    	list = m_rowCacheArray[iRow].getList();
    	
    	if(null==list)
    	{

    	list=new LinkedList<AnsiCharString>();
    	
    	AnsiChar ach=m_chars[iRow][0];
    	
    	iCol=0;

    	do
    	{
    		int nSimilar=1;
    		int ii=iCol+1;
    		ach=m_chars[iRow][iCol];
    		// Count how many characters in the columns
    		while( (ii<m_nColumns) && (m_chars[iRow][ii].equalAttrs(ach)) )
    		{
    			nSimilar++;
    			ii++;
    		}
            AnsiCharString acStr=new AnsiCharString(m_chars[iRow], iCol, nSimilar);
            if( (iCol + nSimilar) >= m_nColumns ) 
            {
            	// TODO:  If this is the last line, then trim off all whitespace from the end of the 
            	// the line to the last non-space character
            }
            list.addLast(acStr);
            iCol+=nSimilar;
    	} while (iCol<m_nColumns);
    	} // if(null==list)

    	
    	return list;
    } // method getRowList()
    
    /**
     * modifiedRow lets the row cache know that we have changed something in a row and that the linked list
     * will have to be recreated the next time the text is rendered.
     * @param iRow
     */
    public void modifiedRow(int iRow)
    {
    	if( (iRow>=0) && (iRow<m_nRows) )
    	{
    		m_rowCacheArray[iRow].clearList();
    	}
    }
    
    public void addChangeListener(ITextConsoleDocChangeListener listener)
    {
   	 if(null == m_changeListenerList)
   	 {
   		 m_changeListenerList = new LinkedList<ITextConsoleDocChangeListener>();
   	 }
   	 m_changeListenerList.add(listener);
    }
    
    public void removeChangeListener(ITextConsoleDocChangeListener listener)
    {
   	 if(null!=m_changeListenerList)
   	 {
   		 if(m_changeListenerList.contains(listener))
   		 {
   			 m_changeListenerList.remove(listener);
   		 }
   	 }
    }
    
    void notifyChangeListeners(int iRowMin, int iColMin, int iRowMax, int iColMax, int iAttr)
    {
     	if(null != m_changeListenerList)
    	{
    		ListIterator<ITextConsoleDocChangeListener> iter=m_changeListenerList.listIterator();
    		while(iter.hasNext())
    		{
    			ITextConsoleDocChangeListener listener= iter.next();
    			if(null!=listener)
    			{
    	    	  listener.documentChanged(this, 
    	    			  iRowMin, iColMin, iRowMax, iColMax, iAttr);
    			}
    		}

    	}
    }

    
    void rollUp(int nLines)
    {
    	int i;
    	int iCol;
    	// Find last row so we can pick up the character color from it.
    	int iRow = (m_nRows - 1 + m_iStartingRow) % m_nRows;
    	
    	AnsiChar lastCh = new AnsiChar(m_chars[iRow][m_nColumns-1]);
    	lastCh.cChar = ' ';
    	
    	// Clear all the nLines at the top which will now be added to the bottom.
    	for(i=0; i<nLines; i++)
    	{
    		iRow= (m_iStartingRow + i) % m_nRows;
    		for(iCol=0; iCol<m_nColumns; iCol++)
    		{
    			m_chars[iRow][iCol]=new AnsiChar(lastCh);
    		}
    		modifiedRow(iRow);
    	}
    	
    	m_iStartingRow = (m_iStartingRow + nLines) % m_nRows;
    	// Notify our listener that there has been a change.
   	    notifyChangeListeners(0, 0, m_nRows-1, m_nColumns-1, ITextConsoleDocChangeListener.ATTR_NEWLINE);
    }

    public Point overwriteChar(Point loc, AnsiChar ach)
    {
    	int iRow = (loc.y + m_iStartingRow) % m_nRows;
    	int iCol=loc.x;
    	int iAttr=ITextConsoleDocChangeListener.ATTR_NONE;
    	AnsiChar curCh;
    	
    	if( (0 <= iRow) && (m_nRows > iRow) && (0<=iCol) && (m_nColumns>iCol) )
    	{
    	   curCh = m_chars[iRow][iCol];
    	   if( !curCh.equals(ach))
    	   {
    		 synchronized(m_docLock)
    		 {
    	        m_chars[iRow][iCol]=ach;
       	        modifiedRow(iRow);   // LinkedList will regenerate a new list for this line upon redraw
    		 }

    	     if(curCh.cChar != ach.cChar)
    	     {
    	    	 iAttr |= ITextConsoleDocChangeListener.ATTR_CHAR;
    	     }
    	     if(!curCh.equalAttrs(ach))
    	     {
    	    	 iAttr |= ITextConsoleDocChangeListener.ATTR_COLOR;
    	     }
    	     if( ITextConsoleDocChangeListener.ATTR_NONE != iAttr )
    	     {
    	        notifyChangeListeners(loc.y, iCol, loc.y+1, iCol+1, iAttr);
    	     }
    	   }
    	   if(m_nColumns > (loc.x+1) ) 
    	   {
    		   loc.x += 1;
    	   }
    	}
    	return loc;
    }
    
    public Point overwriteString(Point loc, AnsiCharString aStr)
    {
    	int iRow = (loc.y + m_iStartingRow) % m_nRows;
    	int iCol=loc.x;
    	char ch;
    	int i;
    	int iAttr=ITextConsoleDocChangeListener.ATTR_NONE;
    	AnsiChar curChar;

    	Rectangle textRect=new Rectangle(loc.x, loc.y, aStr.cChars.length(), 1);
    	
    	synchronized(m_docLock)
    	{
    	for(i=0; i<aStr.getString().length(); i++)
    	{
    	  if( (0 <= iRow) && (m_nRows > iRow) && (0<=iCol) && (m_nColumns>iCol) )
    	  {
    	    ch=aStr.getString().charAt(i);
    	    curChar=m_chars[iRow][iCol];
   	        if(!curChar.equalAttrs(aStr))
	        {
   	        	curChar.setAttr(aStr.cAttr);
   	        	curChar.setForegroundColor(aStr.cForegroundColor);
   	        	curChar.setBackgroundColor(aStr.cBackgroundColor);
	    	 iAttr |= ITextConsoleDocChangeListener.ATTR_COLOR;
	        }
   	        if( curChar.cChar != ch)
   	        {
   	    	   iAttr |= ITextConsoleDocChangeListener.ATTR_CHAR;
    	       m_chars[iRow][iCol].cChar=ch;
   	        }

    	    modifiedRow(iRow);   // LinkedList will regenerate a new list for this line upon redraw

    	    if(m_nColumns > (loc.x+1) ) 
    	    {
    		   loc.x += 1;
    	    }
    	  }
    	  iCol++;
    	  if(m_nColumns<=iCol)
    	  {
//    		  if(m_bTextWrap)
//    		  {
//    			  iCol=0;
//    			  if(iRow<(m_nRows-1) ) iRow++;  // TODO: Should we scroll up if hit the end?
//    		  } else
//    		  {
    			  iCol=m_nColumns-1;
//    		  }
    	  } 
   
    	} // for(i=0; ... )
    	} // synchronized(m_doc.docLock)
    	
    	// If the overwrite produced any changes to the document, let the listeners know
    	if(ITextConsoleDocChangeListener.ATTR_NONE != iAttr)
    	{
	      notifyChangeListeners(textRect.y, 
	    		  textRect.x, textRect.y+textRect.height, textRect.x+textRect.width, 
	    		iAttr);
    	}
    	return loc;
    }
    
    /**
     * clear makes the document be filled with spaces with low intensity white color.
     */
    void clear(AnsiChar ch)
    {
      int iRow;
      int iCol;
     	for(iRow=0; iRow<m_nRows; iRow++)
    	{
    		for(iCol=0; iCol<m_nColumns; iCol++)
    		{
    			m_chars[iRow][iCol]=new AnsiChar(ch);
    			m_chars[iRow][iCol].cChar= ' ';
//    			if(iRow<m_nRows-1)
//    			m_chars[iRow][iCol]=new AnsiChar('0', (byte) 7, (byte) ((iRow+(iCol>>3))&0x7), (byte) AnsiChar.ATTR_FORE_HIGH_INTENSITY);
//    			else
//    			m_chars[iRow][iCol]=new AnsiChar('X', (byte) 7, (byte) ((iRow+(iCol>>3))&0x7), (byte) AnsiChar.ATTR_FORE_HIGH_INTENSITY);

    		}
         // Let Row Cache now we have been modified.
       	m_rowCacheArray[iRow].clearList();

    	}
    }
    
    /**
     * applyColorToEntireDocument overwrites all the color information in the entire document
     * @param acColor
     */
    void applyColorToEntireDocument(AnsiChar acColor)
    {
        int iRow;
        int iCol;
       	for(iRow=0; iRow<m_nRows; iRow++)
      	{
      		for(iCol=0; iCol<m_nColumns; iCol++)
      		{
      			m_chars[iRow][iCol].setBackgroundColor(0x7 & acColor.getBackgroundColor());
      			m_chars[iRow][iCol].setForegroundColor(0x7 & acColor.getForegroundColor());
      			byte attr= acColor.getAttr();
      			byte mask=(AnsiChar.ATTR_BACK_HIGH_INTENSITY | AnsiChar.ATTR_FORE_HIGH_INTENSITY);
      			attr &= mask;
      			attr |= m_chars[iRow][iCol].getAttr() & ~(mask);
      			m_chars[iRow][iCol].setAttr( attr );
      		}
           	m_rowCacheArray[iRow].clearList();
      	}
       	notifyChangeListeners(0, 0, m_nRows, m_nColumns, ITextConsoleDocChangeListener.ATTR_COLOR);
    }
    
}  // end class TextConsoleDocument
