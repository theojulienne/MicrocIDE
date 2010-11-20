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


import org.eclipse.swt.events.*;
import org.eclipse.swt.widgets.*;
import org.eclipse.swt.*;
import org.eclipse.swt.graphics.*;
import org.eclipse.jface.text.*;
import java.util.*;
import java.io.*;

/**
 * TextConsole is a canvas which implements a text display of
 * a TextConsoleDocument.   It depends on a non-proportional (fixed size)
 * font to display a document containing color characters and a fixed 
 * number of rows and columns.  
 *   It tries to be efficient about what needs to be redrawn, and will
 * draw text as a string instead of character by character whenever 
 * possible.  It will also only update the area of the screen which 
 * needs it.   A blinking text cursor is implemented, and will 
 * autoscroll to show an area of the document where the cursor is.
 * @author Dad
 *
 */
public class TextConsole extends Canvas 
   implements ITextConsoleDocChangeListener, ITextConsole, IDlgColorSet
{
	
	boolean m_bInitted;
	public String m_str;
	TextConsoleDocument m_doc;
   int m_iCharHeight;
   int m_iCharWidth;
	Rectangle m_tmpRect;

	int m_iOldCursorRow;
	int m_iOldCursorCol;
	int m_iCursorRow;
	int m_iCursorCol;
	boolean m_bCursorColorReverse;
	Rectangle m_rectCursor;

	
	
	
	protected AnsiChar m_acCurrentColor;
//	protected Writer m_outputFromKeyboard;
	
	protected LinkedList<ICharArrayAccepter> m_listKeystrokeAccepters;
	
	protected boolean m_bDiagnosticRectangles=false;
	

	
	public PaintListener m_PaintListener = new PaintListener()
	{

		public void paintControl(PaintEvent e) 
		{
			repaintRect(e.gc, e.x, e.y, e.width, e.height);
//         repaintAll(e.gc);
		}

	};
	
	/**
	 * Sets up the colors in the GC from an AnsiChar
	 * @param gc the Graphics Context
	 * @param aStr an AnsiChar to get the colors from.
	 */
	public void setup_GC_from_ansiString(GC gc, AnsiCharString aStr)
	{
 		Color clrForeground;
 		Color clrBackground;
 		byte byteAttr=aStr.cAttr;
 		if(0==(byteAttr & AnsiChar.ATTR_NEGATIVE))
 		{
    		  clrForeground=AnsiCharColors.getColor(aStr.cForegroundColor, 
	    				0!=(byteAttr & AnsiChar.ATTR_FORE_HIGH_INTENSITY));
    		  clrBackground=AnsiCharColors.getColor(aStr.cBackgroundColor, 
	    				0!=(byteAttr & AnsiChar.ATTR_BACK_HIGH_INTENSITY));	    			
 		} else
 		{
 			// If inverse bit is on, switch the colors between foreground and background.
 		  clrBackground=AnsiCharColors.getColor(aStr.cForegroundColor, 
 				0!=(byteAttr & AnsiChar.ATTR_FORE_HIGH_INTENSITY));
 		  clrForeground=AnsiCharColors.getColor(aStr.cBackgroundColor, 
 				0!=(byteAttr & AnsiChar.ATTR_BACK_HIGH_INTENSITY));
 		}
 		
 		// If the hidden attribute is given, make the foreground color the same as the background
 		// so the character doesn't show up.
 		if( 0 != (byteAttr & AnsiChar.ATTR_HIDDEN))
 		{
 			clrForeground=clrBackground;
 		}
 		if ( clrForeground != null ) {
 			gc.setForeground(clrForeground);
 		}
 		if ( clrBackground != null ) {
 			gc.setBackground(clrBackground);
 		}
	} // end method setup_GC_from_ansiChar()
	
	/**
	 * Sets up the colors in the GC from an AnsiChar
	 * @param gc the Graphics Context
	 * @param ach an AnsiChar to get the colors from.
	 */
	public void setup_GC_from_ansiChar(GC gc, AnsiChar ach)
	{
 		Color clrForeground;
 		Color clrBackground;
 		byte byteAttr=ach.getAttr();
 		if(0==(byteAttr & AnsiChar.ATTR_NEGATIVE))
 		{
    		  clrForeground=AnsiCharColors.getColor(ach.getForegroundColor(), 
	    				0!=(byteAttr & AnsiChar.ATTR_FORE_HIGH_INTENSITY));
    		  clrBackground=AnsiCharColors.getColor(ach.getBackgroundColor(), 
	    				0!=(byteAttr & AnsiChar.ATTR_BACK_HIGH_INTENSITY));	    			
 		} else
 		{
 			// If inverse bit is on, switch the colors between foreground and background.
 		  clrBackground=AnsiCharColors.getColor(ach.getForegroundColor(), 
 				0!=(byteAttr & AnsiChar.ATTR_FORE_HIGH_INTENSITY));
 		  clrForeground=AnsiCharColors.getColor(ach.getBackgroundColor(), 
 				0!=(byteAttr & AnsiChar.ATTR_BACK_HIGH_INTENSITY));
 		}
 		
 		// If the hidden attribute is given, make the foreground color the same as the background
 		// so the character doesn't show up.
 		if( 0 != (byteAttr & AnsiChar.ATTR_HIDDEN))
 		{
 			clrForeground=clrBackground;
 		}
 		gc.setForeground(clrForeground);
 		gc.setBackground(clrBackground);
	} // end method setup_GC_from_ansiChar()
	
	
	/**
	 * repaintTextLine repaints one row of text on the display.  It assumes that m_oCharRects is
	 * up to date.
	 * @param gc  The graphics context
	 * @param aStr  the Ansi string to draw
	 * @param lineX the x coordinate to draw the string
	 * @param liney the y coordinate to draw the string
	 */
	public void repaintTextLineAnsiString(GC gc, AnsiCharString aStr, int lineX, int lineY)
	{
		 setup_GC_from_ansiString(gc, aStr);
		 gc.drawText(aStr.getString(), lineX, lineY);
//		 m_str=String.format("lineX: %d lineY: %d aStr: %s", lineX, lineY, aStr.getString());
	} // end method repaintTextLineAnsiString()
	

	Rectangle m_oldPaintRect = new Rectangle(0,0,0,0);
	
	
	Rectangle m_tmpRect3 = new Rectangle(0,0,0,0);
	Rectangle m_tmpRect4 = new Rectangle(0,0,0,0);
	int m_count=0;
	/**
	 * repaintRect repaints any lines of text which intersect the given rect in the client area.
	 * @param gc the graphics context
	 * @param x origin of the given rect
	 * @param y origin of the given rect
	 * @param width width of the given rect
	 * @param height height of the given rect
	 */
	public void repaintRect(GC gc, int x, int y, int width, int height)
	{
      Rectangle clientArea = getClientArea();
      ScrollBar vscroll = getVerticalBar();
      ScrollBar hscroll = getHorizontalBar();
      Point ptExtent=new Point(0,0);
		Point strExtent=null;
      ListIterator<AnsiCharString> iter;
      int lineY=0;
      int iRowMin;
      int iRowMax;
      int iColMin;
      int iColMax;
      int nRowsDisplayed;
      int nColsDisplayed;
      int ix;
      Rectangle rectCursor=null;
      AnsiChar acUnderCursor=null;
      boolean bRecalcScroll=false;


 //     if(m_rectCursor==null)
 //     {
 //        m_str=String.format("repaint(%d) %d, %d, %d, %d mrct=null", m_count++, x, y, width, height);
 //     } else
 //     {
 //     	m_str=String.format("repaint(%d) mrct=%s", m_count++, m_rectCursor);
 //     }
 	
      if( (0 == m_iCharHeight) || (0 == m_iCharWidth))
      {
         strExtent=gc.stringExtent("Testing");
         m_iCharHeight = strExtent.y; // TODO: Do we have a better method to try here?
         m_iCharWidth= ((strExtent.x -1) / 7) + 1;
         bRecalcScroll = true;  // Since we may have just changed m_iCharHeight, we may need to recalc scroll bar.
      }
      nRowsDisplayed = ((clientArea.height - 1) / m_iCharHeight) + 1;
      nColsDisplayed = ((clientArea.width -1)/ m_iCharWidth) + 1;
      
      if(bRecalcScroll)
      {
      	recalc_vScroll();
      	recalc_hScroll();
      }
      


 	
      iRowMin= vscroll.getSelection();
      iRowMax= iRowMin + nRowsDisplayed;
      if(iRowMin < 0) iRowMin=0;
      if(iRowMax > m_doc.m_nRows) iRowMax= m_doc.m_nRows ;

    	
      iColMin= hscroll.getSelection();
      iColMax= iColMin + nColsDisplayed;
      if(iColMin < 0) iColMin=0;
      if(iColMax > m_doc.m_nColumns) iColMax= m_doc.m_nColumns ;
      
      synchronized(m_doc.m_docLock)
      {
        for(int iRow=iRowMin; iRow<iRowMax; iRow++)
        {

          LinkedList<AnsiCharString> rowList= m_doc.getRowList(iRow);
//          if(gc!=null) return; // debugging line
          iter=rowList.listIterator();
          ptExtent.x=0; 
          ptExtent.y=0;
          ix=0;
          while(iter.hasNext())
          {
            AnsiCharString aStr=iter.next();

            strExtent=gc.stringExtent(aStr.getString());
            m_tmpRect.x=ptExtent.x - (iColMin * m_iCharWidth);
            m_tmpRect.y=lineY;
            m_tmpRect.width=strExtent.x;
            m_tmpRect.height=strExtent.y;
            if(m_tmpRect.intersects(x, y, width, height))
            {

            	repaintTextLineAnsiString(gc, aStr, ptExtent.x-(iColMin*m_iCharWidth), lineY);
            } else
            {

                setup_GC_from_ansiString(gc, aStr);  // At least set colors.
            }
            
           
            
            // Width of the line would be the sum of the strings in the line.
            ptExtent.x += strExtent.x;
 		
            // If Find tallest line in the row
            if( strExtent.y > ptExtent.y ) ptExtent.y=strExtent.y;

            ix+=aStr.getString().length();
          } // while(iter.hasNext())...

          // Fill from end of text line to the right boundary of the region we are painting
          m_tmpRect.x=ptExtent.x-(iColMin*m_iCharWidth);
          m_tmpRect.width=x + width - (ptExtent.x-(iColMin*m_iCharWidth));
          m_tmpRect.y=lineY;
          m_tmpRect.height=ptExtent.y;
          if((x+width)>(ptExtent.x-(iColMin*m_iCharWidth)))
          {
 //             if(m_tmpRect.intersects(x, y, width, height))
              {
//        	    gc.setForeground(AnsiCharColors.getColor(AnsiCharColors.CLR_MAGENTA, true));
            	try {
            		gc.setBackground(
            				AnsiCharColors.getColor(m_acCurrentColor.getBackgroundColor(),
            						0!=(m_acCurrentColor.getAttr() & AnsiChar.ATTR_BACK_HIGH_INTENSITY)));
            	} catch (Exception e) {
            		
            	}
            	
                gc.fillRectangle(m_tmpRect);

   	            if(m_bDiagnosticRectangles)
	            {
                   gc.drawRectangle(m_tmpRect);
	            }
              }
          }

          lineY+=ptExtent.y;
        }// for(iRow ... )
        // Fill from bottom of last text line to bottom of region we are painting.
        if((y+height) > lineY )
        {

          m_tmpRect.x=x;
          m_tmpRect.width=width;
          m_tmpRect.y=lineY;
          m_tmpRect.height= y + height-lineY;
          if(m_tmpRect.intersects(x, y, width, height))
          {
             gc.fillRectangle(m_tmpRect);
          }
        }
      } // synchronized...
   
    	

      rectCursor=this.getCursorRect(m_rectCursor);
      acUnderCursor= m_doc.getAnsiChar(getCursorY(), getCursorX());
      
      // Now we need see if the cursor is in the area we are redrawing.
      if( (null!=rectCursor) && (null!=acUnderCursor) )
      {
    		  if(rectCursor.intersects(x, y, width, height))
    		  {
    			  
    			  if(m_bCursorColorReverse)
    			  {
    			    // Set color to opposite of the background color for the character.
    				  try {
    			    gc.setBackground(AnsiCharColors.getOppositeColor(
    					  acUnderCursor.getBackgroundColor(), 0!=(acUnderCursor.getAttr() & AnsiChar.ATTR_BACK_HIGH_INTENSITY)));
    				  } catch (Exception e) {
    					  
    				  }
    			  } else
    			  {
    				  try {
    				  gc.setBackground( AnsiCharColors.getColor(acUnderCursor.getBackgroundColor(), 
    								  0!=(acUnderCursor.getAttr() & AnsiChar.ATTR_BACK_HIGH_INTENSITY)));
    				  } catch (Exception e) {
    					  
    				  }
    			  }
    			  
    			  
    			  
    			  // Draw the cursor as a rectangle which is 1/4 of the character, located on the bottom.
    			  int yTmp;
    			  yTmp=3*rectCursor.height/4;
    			  if((rectCursor.height-yTmp)<3) yTmp=rectCursor.height-3;

    			  // CHANGED
    			  //gc.fillRectangle(rectCursor.x, rectCursor.y+yTmp, rectCursor.width, rectCursor.height-yTmp);
    			  gc.fillRectangle( rectCursor.x, rectCursor.y, 1, rectCursor.height );
    			  
    		      //gc.setForeground(AnsiCharColors.getColor( AnsiCharColors.CLR_WHITE, true));
    		      //gc.drawLine(rectCursor.x, rectCursor.y, rectCursor.x+rectCursor.width-1, rectCursor.y+rectCursor.height-1);
    			  
    		  } //  if(rectCursor.intersects(x, y, width, height))
      } //       if( (null!=rectCursor) && (null!=acUnderCursor))

//      if(m_bCursorColorReverse)
//      {
//      gc.setForeground(AnsiCharColors.getColor( AnsiCharColors.CLR_WHITE, true));
//      } else
//      {
//    	  gc.setForeground(AnsiCharColors.getColor( AnsiCharColors.CLR_GREEN, false));
//      }
      
	   if(m_bDiagnosticRectangles)
  	   {
        gc.setForeground(AnsiCharColors.getColor( AnsiCharColors.CLR_BLACK, false));
        gc.drawRectangle(m_oldPaintRect.x, m_oldPaintRect.y, m_oldPaintRect.width-1, m_oldPaintRect.height-1);
      
        gc.setForeground(AnsiCharColors.getColor( AnsiCharColors.CLR_MAGENTA, true));
        gc.drawRectangle(x, y, width-1, height-1);
        m_oldPaintRect.x=x;
        m_oldPaintRect.y=y;
        m_oldPaintRect.height = height;
        m_oldPaintRect.width= width;
  	   }

//      gc.setForeground(AnsiCharColors.getColor(AnsiCharColors.CLR_BLACK, false));
//      gc.setBackground(AnsiCharColors.getColor( AnsiCharColors.CLR_WHITE, true));

//      gc.drawText(m_str, clientArea.width/2- gc.stringExtent(m_str).x/2, clientArea.height/2);
	} // end method repaintRect()
	
	
	public ControlListener m_ControlListener = new ControlListener()
	{


		public void controlMoved(ControlEvent e) {
			// TODO Auto-generated method stub
			
		}


		public void controlResized(ControlEvent e) {
			// TODO Auto-generated method stub
         recalc_vScroll();
         recalc_hScroll();
		}
		
	};
	
	public SelectionAdapter m_selectionAdapter= new SelectionAdapter()
	{
		public void widgetSelected(SelectionEvent e)
		{
			if(getVerticalBar().equals((ScrollBar) e.widget))
			{
				scrollVertically ((ScrollBar) e.widget);
			}
			if(getHorizontalBar().equals((ScrollBar) e.widget))
			{
				scrollHorizontally((ScrollBar) e.widget);
			}
			

		}
	};
	
	/**
	 * scroll_to_show_cursor checks to make sure the cursor is on the currently displayed
	 * page.  if not, it changes the scroll position until it is.
	 * @return true if a redraw of the whole screen was requested.
	 */
	public boolean scroll_to_show_cursor()
	{
		ScrollBar vscroll = getVerticalBar();
		Rectangle clientArea = getClientArea();
		int nRowsDisplayed;
		int iRowMin;
		int iRowMax;
		int iNewRowMin=-1;
		boolean bRet=false;
		
		if(0 != m_iCharHeight)
		{
	      nRowsDisplayed = ((clientArea.height - 1) / m_iCharHeight) + 1;
//			nRowsDisplayed = (int) ((float) (clientArea.height) / m_iCharHeight);
	      iRowMin= vscroll.getSelection();
	      iRowMax= iRowMin + nRowsDisplayed - 1;
	      if(iRowMin < 0) iRowMin=0;
//	      if(iRowMax >= m_doc.m_nRows) iRowMax= m_doc.m_nRows - 1;
	      
	      if(getCursorY() < iRowMin)
	      {
	      	iNewRowMin=getCursorY();
	      } else if(getCursorY() >= iRowMax)
	      {
	      	iNewRowMin=getCursorY()-nRowsDisplayed+2;
	      }
	      if(0 <= iNewRowMin)
	      {
	      	vscroll.setSelection(iNewRowMin);
	      	clear_cursor_cache();
	      	redraw();
	      	bRet=true;
	      }
		}
		
		ScrollBar hscroll = getHorizontalBar();

		int nColsDisplayed;
		int iColMin;
		int iColMax;
		int iNewColMin=-1;
		
		if(0 != m_iCharHeight)
		{
			nColsDisplayed = (int) ((float) (clientArea.width) / m_iCharWidth);
	      iColMin= hscroll.getSelection();
	      iColMax= iColMin + nColsDisplayed;
	      if(iColMin < 0) iColMin=0;
	      if(iColMax > m_doc.m_nColumns) iColMax= m_doc.m_nColumns ;
	      
	      if(getCursorX() < iColMin)
	      {
	      	iNewColMin=getCursorX();
	      } else if(getCursorX() >= iColMax)
	      {
	      	iNewColMin=getCursorX()-nColsDisplayed+1;
	      }
	      if(0 <= iNewColMin)
	      {
	      	hscroll.setSelection(iNewColMin);
	      	clear_cursor_cache();
	      	redraw();
	      	bRet=true;
	      }
		}
		return bRet;
	}
	
	public void recalc_hScroll()
	{
		ScrollBar hscroll=getHorizontalBar();
		Rectangle clientArea = getClientArea();
		int nColsDisplayed;
		
		if(0==m_iCharWidth)
		{
			nColsDisplayed= (int) ((float) (clientArea.width) / 16.0f);
		} else
		{
			nColsDisplayed= (int) ((float) (clientArea.width) / m_iCharWidth);
		}
		int iHScrollMax;
		
		iHScrollMax = m_doc.m_nColumns;
		
		if(iHScrollMax < 1) iHScrollMax = 1;

      hscroll.setIncrement( 1 );
      hscroll.setPageIncrement( nColsDisplayed );
      hscroll.setMaximum( iHScrollMax );
      hscroll.setMinimum(0);
      hscroll.setThumb(nColsDisplayed);
      scroll_to_show_cursor();
		
	}
	
	public void recalc_vScroll()
	{
//		TextConsole thisConsole = (TextConsole) e.widget;
//		thisConsole.m_str = String.format("%d, %d", thisConsole.getSize().x, thisConsole.getSize().y);
    	ScrollBar vscroll = getVerticalBar();
    	Rectangle clientArea = getClientArea();
    	int nRowsDisplayed;

    	
    	if(0==m_iCharHeight)
    	{
    		// TODO: Do we have a better method to try here?  I don't see any way to get a GC object
    		// so I can't figure out the string extent.  So I just guess 16.0 and plan to repair it when
    		// we get our first screen repaint.
    	   nRowsDisplayed = (int) ((float) (clientArea.height) / 16.0f);
    	} else
    	{
    	   nRowsDisplayed = (int) ((float) (clientArea.height) / m_iCharHeight);
    	}
    	
    	int iVScrollMax;
    	
    	iVScrollMax = m_doc.m_nRows;
    	if(iVScrollMax < 1) iVScrollMax = 1;
         vscroll.setIncrement( 1 );
         vscroll.setPageIncrement( nRowsDisplayed );
         vscroll.setMaximum( iVScrollMax );
         vscroll.setMinimum(0);
         vscroll.setThumb(nRowsDisplayed);
         scroll_to_show_cursor();
	}
	
	void scrollVertically( ScrollBar vscroll)
	{
//		m_str = String.format("%d, %d", getVerticalBar().getSelection(), getVerticalBar().getMaximum());
		this.redraw();
	}
	
	void scrollHorizontally( ScrollBar vscroll)
	{
//		m_str = String.format("%d, %d", getVerticalBar().getSelection(), getVerticalBar().getMaximum());
		this.redraw();
	}
	
    public TextConsole(Composite parent, int styles, int nRows, int nColumns, int iColorBack, int iColorFore)
    {
        super(parent, styles | SWT.V_SCROLL | SWT.H_SCROLL | SWT.MULTI | SWT.NO_BACKGROUND);
        m_bInitted=false;
        m_doc=new TextConsoleDocument(nRows, nColumns, iColorBack, iColorFore);
//        m_doc.getRowList(190);
        m_doc.addChangeListener(this);
        m_str = "hello, world";
        m_iCharHeight=0;
        m_iCharWidth=0;
//        m_iCursorRow=m_doc.m_nRows-1;
        m_iCursorRow=0;
        m_iCursorCol=0;
        m_iOldCursorRow=0;
        m_iOldCursorCol=0;
        m_tmpRect=new Rectangle(0,0,0,0);
        m_rectCursor=new Rectangle(0,0,0,0);
        m_bCursorColorReverse=true;
        m_acCurrentColor=new AnsiChar();
        m_acCurrentColor.setBackgroundColor(iColorBack );
        m_acCurrentColor.setForegroundColor(iColorFore);
        addPaintListener(m_PaintListener);
        addControlListener(m_ControlListener);
        addKeyListener(m_keyListener);
        getVerticalBar().addSelectionListener(m_selectionAdapter);
        getHorizontalBar().addSelectionListener(m_selectionAdapter);
//        autoScrollDoc();
        autoCursorBlink();
        
//        m_outputFromKeyboard=null;
        m_listKeystrokeAccepters=new LinkedList<ICharArrayAccepter>();

    }
    
    
	public void addKeyboardCharAccepter(ICharArrayAccepter accepter)
	{
	    if( !m_listKeystrokeAccepters.contains(accepter))
	    {
		   m_listKeystrokeAccepters.add(accepter);
	    }
	}
	
	public void removeKeyboardCharAccepter(ICharArrayAccepter accepter)
	{
		m_listKeystrokeAccepters.remove(accepter);
	}
    
	Rectangle m_rectTmp5=new Rectangle(0,0,0,0);
    /**
     * Once called, this autoblinks the cursor every 1/2 second.
     */
    public void autoCursorBlink()
    {
    	if(isDisposed()) return;
    	
    	if(this.isCharInClientArea(getCursorY(), getCursorX()))
    	{
    			
    	  Rectangle rectCursor = this.getCursorRect(m_rectTmp5);
    	  if(null!=rectCursor)
    	  {
    		redraw(rectCursor.x, rectCursor.y, rectCursor.width, rectCursor.height, true);

    	  } else
    	  {
    		redraw();
    	  }
    	}
        m_bCursorColorReverse = !m_bCursorColorReverse;
    	getDisplay().timerExec(500, 
          new Runnable() 
    	  {

			public void run() 
			{
				autoCursorBlink();
			}
    		
    	  }
    	);

    }
    
    public void autoScrollDoc()
    {
      if(isDisposed()) return;
      
      synchronized(m_doc.m_docLock)
      {
    	  m_doc.rollUp(1);
      }

      getDisplay().timerExec(50, new Runnable() 
      {

		public void run() 
		{
			autoScrollDoc();
		}
    	  
      }
      );
    }
    
    Rectangle m_tmpRect2=new Rectangle(0,0,0,0);
    protected boolean isCharInClientArea(int iRow, int iCol)
    {
      boolean bRet=false;
      Rectangle rectChar = getSingleCharRect(m_tmpRect2, iRow, iCol);
      bRet= rectChar.intersects(getClientArea());
      return bRet;
    }

    protected Rectangle getSingleCharRect(Rectangle resultRect, int iRow, int iCol)
    {
    	return getPixelRectFromCharRect(resultRect, 
    			iCol, iRow, iCol+1, iRow+1);
    }
    
    protected Rectangle getCursorRect(Rectangle resultRect)
    {
    	return getSingleCharRect(resultRect, getCursorY(), getCursorX());
    }
    
    /**
     * GetPixelRectFromCharRect calculates a client area rectangle which 
     * surrounds the given range of characters.  Warning, this rectangle will
     * likely include areas outside the bounds of the client area, including
     * negative values.
     * @param resultRect
     * @param iRowMin
     * @param iColMin
     * @param iRowMax
     * @param iColMax
     * @return
     */
    Rectangle getPixelRectFromCharRect(Rectangle resultRect,  
   		 int iColMin, int iRowMin, int iColMax, int iRowMax)
    {
       Rectangle clientArea = getClientArea();
       ScrollBar vscroll = getVerticalBar();
       ScrollBar hscroll = getHorizontalBar();
       
       int iRowScroll= vscroll.getSelection();
       int iColScroll= hscroll.getSelection();
//       iRowMax= iRowMin + nRowsDisplayed;
//       if(iRowMin < 0) iRowMin=0;
//       if(iRowMax > m_doc.m_nRows) iRowMax= m_doc.m_nRows ;
       if( (0 == m_iCharHeight) || (0 == m_iCharWidth))
       {
      	 resultRect.x=clientArea.x;
      	 resultRect.y=clientArea.y;
      	 resultRect.width=clientArea.width;
      	 resultRect.height=clientArea.height;
       } else
       {
      	 resultRect.x=clientArea.x+(iColMin-iColScroll)*m_iCharWidth;
      	 resultRect.y=clientArea.y+(iRowMin-iRowScroll)*m_iCharHeight;
      	 resultRect.width= (iColMax-iColMin) * m_iCharWidth;
      	 resultRect.height= (iRowMax-iRowMin) * m_iCharHeight;
       }
       
       return resultRect;
    }


    
   public void documentChanged(TextConsoleDocument doc, int iRowMin,
         int iColMin, int iRowMax, int iColMax, int iChangeAttr) 
	{
	   // If the document changed, see if the changes are visible on our
		// screen.  If they are, then redraw.
		
		// TODO: Right now we will just be lazy and issue a redraw no 
		// matter what the change to the document is.
	   
		// If the character under the cursor has changed, update it.
		if( (getCursorX() >= iColMin) && (getCursorX()<=iColMax)
				&& (getCursorY() >= iRowMin) && (getCursorY() <= iRowMax )
				)
		{
			clear_cursor_cache();

		}
		
		// Redraw the whole thing
		if(  (m_doc.m_nRows > (iRowMax-iRowMin+2)) )
		{
		  
		   Rectangle scrRect= this.getPixelRectFromCharRect(m_tmpRect, 
				iColMin, iRowMin, iColMax, iRowMax);
		   if(iColMax >= m_doc.m_nColumns-1)
		   {
			   scrRect.width=100000;   // Go all the way to the right of the screen.
		   }
		   redraw(scrRect.x, scrRect.y, scrRect.width, scrRect.height, true);
		} else
		{
		   redraw();
		}
   }
	
   protected void clear_cursor_cache()
   {
      // We no longer store the rectangle and character under the cursor
   }
   
   
   private Rectangle m_moveCursorRect=new Rectangle(0,0,0,0);
   protected void moveCursor(int delx, int dely)
   {
      setCursorXY(delx+getCursorX(), dely+getCursorY());
   }
   
   protected void carriageReturn()
   {
	   if(0==getCursorX()) return;  // Nothing to do.
	   
	   setCursorXY(0, getCursorY());
	   
   }
   
   protected void lineFeed()
   {
	   int lastRow=m_doc.getNRows()-1;
	   if(getCursorY() >= lastRow )
	   {
		   this.scrollUp(getCursorY()-lastRow+1);
	   } else
	   {
		   setCursorXY(getCursorX(), getCursorY()+1);
	   }
   }
	
   protected void writeCommand(String strCmd)
   {
	   Iterator<ICharArrayAccepter> iter;
	   
	   iter=m_listKeystrokeAccepters.iterator();
	   while(iter.hasNext())
	   {
		   ICharArrayAccepter accepter= iter.next();
		   accepter.acceptChars(strCmd.toCharArray(), 0, strCmd.length());
	   }
   }
   
   protected void writeCommand(char chCmd )
   {
	   StringBuilder sb=new StringBuilder();
	   sb.append('\u001b');
	   sb.append('[');
	   sb.append(chCmd);
	   writeCommand(sb.toString());
	   
   }
   
   protected void writeCommand(char chCmd, int iParm1)
   {
	   StringBuilder sb=new StringBuilder();
	   sb.append('\u001b');
	   sb.append('[');
	   sb.append(iParm1);
	   sb.append(chCmd);
	   writeCommand(sb.toString());
   }
   
   protected void writeCommand(char chCmd, int iParm1, int iParm2)
   {
	   StringBuilder sb=new StringBuilder();
	   sb.append('\u001b');
	   sb.append('[');
	   sb.append(iParm1);
	   sb.append(';');
	   sb.append(iParm2);
	   sb.append(chCmd);
	   writeCommand(sb.toString());
   }
   
   protected void processKeyPressed(KeyEvent e)
   {

	   boolean bProcessed=false;
       int bits= SWT.MODIFIER_MASK;
       

	   // Handle the case where there are no shift, alt or control keys pressed.
	   if(0 == (e.stateMask & (bits) ))
	   {
	     switch(e.keyCode)
	     {
	     
	       case SWT.ARROW_UP:
//	    	   moveCursor(0, -1);
	    	   writeCommand('A', 1);
	    	   bProcessed=true;
		     break;
	       case SWT.ARROW_DOWN:
//	    	   moveCursor(0, 1);
	    	   writeCommand('B', 1);
	    	   bProcessed=true;
	    	   break;
	       case SWT.ARROW_RIGHT:
//	    	   moveCursor(1, 0);
	    	   writeCommand('C', 1);
	    	   bProcessed=true;	    	   
	    	   break;
	       case SWT.ARROW_LEFT:
//	    	   moveCursor(-1, 0);
	    	   writeCommand('D', 1);
	    	   bProcessed=true;
	    	   break;
	       default:
	    		   break;
	     }
	   }
	   
//	   if( SWT.CTRL == (e.stateMask & (SWT.CTRL | SWT.SHIFT | SWT.ALT)) )
//	   {
//		   
//	   }
   
         if(e.character == (int) '\0') bProcessed=true;  // Ignore NULL chars

         if(!bProcessed)
         {
		     Iterator<ICharArrayAccepter> iter;
		   
		     iter=m_listKeystrokeAccepters.iterator();
		     while(iter.hasNext())
		     {
				  iter.next().acceptChar(e.character);
		     }
         }

   }
   
   protected void processKeyReleased(KeyEvent e)
   {
	   
   }
	
   public KeyListener m_keyListener = new KeyListener()
   {


	public void keyPressed(KeyEvent e) 
	{
		processKeyPressed(e);
	}


	public void keyReleased(KeyEvent e) 
	{
		processKeyReleased(e);		
	}
   };
   
	public int getNumColumns()
	{
		int iRet=0;
		iRet=m_doc.getNCols();
		return iRet;
	}
	public int getNumRows()
	{
//      Rectangle clientArea = getClientArea();
//      int iCharHeight=16;
//      if(m_iCharHeight != 0) iCharHeight=m_iCharHeight;
//      int nRowsDisplayed = ((clientArea.height - 1) / iCharHeight) + 1;
//      if(nRowsDisplayed >= m_doc.getNRows()) nRowsDisplayed=m_doc.getNRows()-1;
		int iRet;
		iRet=m_doc.getNRows();
		return iRet;
	}
	
	public int getCursorX()
	{
		int iRet=0;
		iRet=m_iCursorCol;
		return iRet;
	}
	public int getCursorY()
	{
		int iRet=0;
		iRet=m_iCursorRow;
		return iRet;
	}
	
	public int setCursorXY(int iCol, int iRow)
	{
		int iRet=0;
		int iOldCursorRow=getCursorY();
		int iOldCursorCol=getCursorX();;
		boolean bRedraw=false;
		boolean bRedrawOldCursor=false;
		

		
		if(iCol<0) iCol=0;
		if(iCol>=m_doc.getNCols()) iCol=m_doc.getNCols()-1;
		
		if(iRow<0) iRow=0;
		if(iRow>=m_doc.getNRows()) iRow=m_doc.getNRows()-1;
		
		if(iCol != getCursorX()) 
		{
			bRedraw=true;
			m_iCursorCol=iCol;
		}
		
		if(iRow != getCursorY()) 
		{
			bRedraw=true;
			m_iCursorRow=iRow;
		}
		
		// TODO: if cursor isn't on the screen because of the scroll position, should we 
		// scroll so it is?
		
		if(bRedraw) 
	    {
		   /* If scroll_to_show_cursor() did a redraw anyway, then no since redrawing
		    * little bits on the screen.
		    */
		   bRedraw = !scroll_to_show_cursor();
		   if(bRedraw)
		   {
			   bRedrawOldCursor=isCharInClientArea(getCursorY(), getCursorX());
			
			   // Redraw a rect containing old cursor
			   if(bRedrawOldCursor)
			   {
			      // Save cursor rect before we move it.
			      getPixelRectFromCharRect(m_moveCursorRect, 
					   iOldCursorCol, iOldCursorRow, iOldCursorCol+1, iOldCursorRow+1);
			      redraw(m_moveCursorRect.x, m_moveCursorRect.y, m_moveCursorRect.width, m_moveCursorRect.height, true);
			   }
			   
			   // Redraw a rect containing new cursor position
			   this.getPixelRectFromCharRect(m_moveCursorRect, 
					   getCursorX(), getCursorY(), getCursorX()+1, getCursorY()+1);
			   redraw(m_moveCursorRect.x, m_moveCursorRect.y, m_moveCursorRect.width, m_moveCursorRect.height, true);
		   }
	   }
		
		return iRet;
	}
	
   public AnsiChar getCharAt(int iCol, int iRow)
	{
		AnsiChar achRet;
		achRet=null;
	   achRet = m_doc.getAnsiChar(iRow, iCol);
		
		return achRet;
	}
	

   Point m_ptCharAtTmp=new Point(0, 0);
	public int putCharAt( AnsiChar ach, int iCol, int iRow )
	{
		int iRet=0;

      m_ptCharAtTmp.x=iCol;
      m_ptCharAtTmp.y=iRow;
      m_doc.overwriteChar(m_ptCharAtTmp, ach);
 //     redraw();
		return iRet;
	}
	
	public int putCharAtCursor( AnsiChar ach )
	{
		int iRet=0;
	      m_ptCharAtTmp.x=getCursorX();
	      m_ptCharAtTmp.y=getCursorY();

		Point cursorPoint = m_doc.overwriteChar(m_ptCharAtTmp, ach);
		
		setCursorXY(cursorPoint.x, cursorPoint.y);

		return iRet;
	}
	
	public int scrollUp(int nLines)
	{
		int iRet=0;
		m_doc.rollUp(nLines);
		scroll_to_show_cursor();
		redraw();
		return iRet;
	}
	public int scrollDown(int nLines)
	{
		int iRet=0;
		// TODO: This is currently unimplemented.
		return iRet;
	}
	
	public int clearScreen()
	{
		int iRet=0;
		m_doc.clear(m_acCurrentColor);
		clear_cursor_cache();
		scroll_to_show_cursor();
		redraw();  // We no doubt have altered the screen with this one!
		return iRet;
	}
	
	public int clearSome(int iColStart, int iColEnd, int iRow)
	{
		int iRet=0;
		int iCol;
		AnsiChar ach;
		AnsiChar newCh;
		if(getCursorY() == iRow)
		{
		  if( (getCursorX()>=iColStart) && (getCursorX()<=iColEnd))
		  {
			  clear_cursor_cache();
		  }
		}
		for(iCol=iColStart; iCol<=iColEnd; iCol++)
		{
			ach=getCharAt(iCol, iRow);
			newCh = new AnsiChar(ach);
			newCh.cChar = ' ';
		   putCharAt( newCh, iCol, iRow );
		}
		return iRet;
	}


   public int moveCursorRelative(int iDelCol, int iDelRow) 
	{
	   moveCursor(iDelCol, iDelRow);
	   return 0;
   }


   public int sendString(String str) 
	{
		   Iterator<ICharArrayAccepter> iter;
		   
		   iter=m_listKeystrokeAccepters.iterator();
		   while(iter.hasNext())
		   {
			   ICharArrayAccepter accepter= iter.next();
			   accepter.acceptChars(str.toCharArray(), 0, str.length());
		   }
	   return 0;
   }


   public void putChar(char ch) 
	{
		AnsiChar ac = new AnsiChar(ch, m_acCurrentColor.getForegroundColor(), 
				m_acCurrentColor.getBackgroundColor(), m_acCurrentColor.getAttr());
      putCharAtCursor(ac);
   }


   public byte getBackgroundColorCode() 
	{
		byte bRet;
		
		bRet= m_acCurrentColor.getBackgroundColor();
		if( (m_acCurrentColor.getAttr() & AnsiChar.ATTR_BACK_HIGH_INTENSITY) != 0) bRet |= 0x8;
		return bRet;
   }


   public byte getForegroundColorCode() 
	{
		byte bRet;
		
		bRet= m_acCurrentColor.getForegroundColor();
		if( (m_acCurrentColor.getAttr() & AnsiChar.ATTR_FORE_HIGH_INTENSITY) != 0) bRet |= 0x8;
		return bRet;
   }

	
	public void setColor(byte iColor, byte iColorSel) 
	{
		switch(iColorSel)
		{
		case IDlgColorSet.BACKGROUND:
		       m_acCurrentColor.setBackgroundColor( iColor & 0x7 );
		       if(0 != (iColor & 0x8) ) 
		      	 m_acCurrentColor.setAttr(m_acCurrentColor.getAttr() | AnsiChar.ATTR_BACK_HIGH_INTENSITY);
		       else 
		      	 m_acCurrentColor.setAttr(m_acCurrentColor.getAttr() & ~AnsiChar.ATTR_BACK_HIGH_INTENSITY);
			break;
		case IDlgColorSet.FOREGROUND:
			   m_acCurrentColor.setForegroundColor( 0x7 & iColor );
			   if(0!=(iColor & 0x8) )
			   	m_acCurrentColor.setAttr(m_acCurrentColor.getAttr() | AnsiChar.ATTR_FORE_HIGH_INTENSITY);
			   else 
			   	m_acCurrentColor.setAttr(m_acCurrentColor.getAttr()& ~AnsiChar.ATTR_FORE_HIGH_INTENSITY);
			break;
			default:
				break;
		}

	}


	public void applyToEntireWindow() 
	{
		m_doc.applyColorToEntireDocument(m_acCurrentColor);
	}

}
