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

import org.eclipse.swt.graphics.*;



/**
 * This class has all static functions.  It is designed to create an array of ANSI colors
 * at startup and leave them in a static array, so they are always there.   The user must call
 * the static function create() at startup and remember to call dispose() at dispose time.
 * @author Dad
 *
 */
public class AnsiCharColors
{
	public static final byte CLR_BLACK=0;
	public static final byte CLR_RED=1;
	public static final byte CLR_GREEN=2;
	public static final byte CLR_YELLOW=3;
	public static final byte CLR_BLUE=4;
	public static final byte CLR_MAGENTA=5;
	public static final byte CLR_CYAN=6;
	public static final byte CLR_WHITE=7;
	
	public static final int NUM_COLORS=16;
	
	static protected int m_refCount=0;
	
	static org.eclipse.swt.graphics.Color g_colors[]=new org.eclipse.swt.graphics.Color[16];
	
	public static void create(Device dev)
	{
		if(m_refCount==0)
		{
			g_colors[CLR_BLACK]= new org.eclipse.swt.graphics.Color(dev, 0, 0, 0);
			g_colors[CLR_RED]=new org.eclipse.swt.graphics.Color(dev, 128, 0, 0);
			g_colors[CLR_GREEN]= new org.eclipse.swt.graphics.Color(dev, 0, 128, 0);
			g_colors[CLR_YELLOW]= new org.eclipse.swt.graphics.Color(dev, 128, 128, 0);
			g_colors[CLR_BLUE]= new org.eclipse.swt.graphics.Color(dev, 0, 0, 128);
			g_colors[CLR_MAGENTA]= new org.eclipse.swt.graphics.Color(dev, 128, 0, 128);
			g_colors[CLR_CYAN]= new org.eclipse.swt.graphics.Color(dev, 0, 128, 128);
			g_colors[CLR_WHITE]= new org.eclipse.swt.graphics.Color(dev, 128, 128, 128);
			g_colors[CLR_BLACK+8]= new org.eclipse.swt.graphics.Color(dev, 128, 128, 128);
			g_colors[CLR_RED+8]=new org.eclipse.swt.graphics.Color(dev, 255, 0, 0);
			g_colors[CLR_GREEN+8]= new org.eclipse.swt.graphics.Color(dev, 0, 255, 0);
			g_colors[CLR_YELLOW+8]= new org.eclipse.swt.graphics.Color(dev, 255, 255, 0);
			g_colors[CLR_BLUE+8]= new org.eclipse.swt.graphics.Color(dev, 0, 0, 255);
			g_colors[CLR_MAGENTA+8]= new org.eclipse.swt.graphics.Color(dev, 255, 0, 255);
			g_colors[CLR_CYAN+8]= new org.eclipse.swt.graphics.Color(dev, 0, 255, 255);
			g_colors[CLR_WHITE+8]= new org.eclipse.swt.graphics.Color(dev, 255, 255, 255);
			
		}
		m_refCount++;
	}
	
	/**
	 * getColor returns an ansi color 0-15, where 0-7 are low intensity and 8-15 are high-inten.
	 *   Can also use a 0-7 color, and a boolean to represent high inten.
	 * @param byteColor
	 * @param bHighInten
	 * @return the SWT color object
	 * @throws Exception 
	 */
	public static org.eclipse.swt.graphics.Color getColor( byte byteColor, boolean bHighInten ) 
	{
//		if(m_refCount<1)
//		{
//			throw new Exception("Oops, did you forget to call the static 'create()' function?");
//		}
		if(!bHighInten)
		    return g_colors[byteColor & 0xf ];
		else
			return g_colors[ (byteColor& 0x7) + 8];
	}
	
	protected static byte getInverseColorIndex( byte ic, boolean bHighInten)
	{
		byte iColor;
		if(bHighInten || ((ic & 0x8)!=0) ) 
	    {
			  switch(ic & 0x7)
			  {
			    case CLR_BLACK:
			    case CLR_BLUE:
			    case CLR_RED:
			    	iColor=CLR_WHITE + 0x8;
		    	break;
		    	
		    	default:
			    		iColor=CLR_BLACK;
	    		break;
			  }
	    } else
	    {
			iColor=CLR_WHITE + 0x8;		

	    }
		return iColor;
	}
	
	/**
	 * getOppositeColor returns black if the background was a bright color, or white if background is dark.
	 * @param byteColor
	 * @param bHighInten
	 * @return
	 */
	public static org.eclipse.swt.graphics.Color getOppositeColor(byte byteColor, boolean bHighInten)
	{
		return g_colors[getInverseColorIndex(byteColor, bHighInten)];
	}
	
	public static void dispose()
	{
		if(0==m_refCount) return;
		m_refCount--;
		if(0==m_refCount)
		{
	       for(int i=0; i<16; i++)
	       {
	    	   g_colors[i].dispose();
	       }
		}
	}
}