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



import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.window.IShellProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.*;
import org.eclipse.swt.layout.*;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;


public class DlgTextColorChooser extends Dialog implements IDlgColorSet
{


   /**
	 * GroupOfColors is a composite containing an array of color buttons
	 * for choosing one of the 7 Ansi colors.
	 * @author Dad
	 *
	 */
	public class GroupOfColors extends Composite
	{
		
		public class CanvasColorButton extends Canvas
		{
         byte m_iColor;
         byte m_iColorSel;
         IDlgColorSet m_oColorSetter;
 		boolean m_bSelected;		

         
			public CanvasColorButton(Composite parent, int style, byte iColor, byte iColorSel, IDlgColorSet clrSetter) 
			{
	         super(parent, style);
	         m_iColor=iColor;
	         m_iColorSel=iColorSel;
	         m_oColorSetter=clrSetter;
		      m_bSelected=false;
	         
	         setBackground(AnsiCharColors.getColor(iColor, false));
	         buildControl();
         }
			
			public boolean getSelected() { return m_bSelected; }
			public void setSelected(boolean bSelected) 
			{
				if(bSelected != m_bSelected)
				{
				   m_bSelected=bSelected;
				   redraw();
				}
			}
			
			protected void buildControl()
			{
				this.addMouseListener(
						new MouseAdapter()
						{
							public void mouseDown(MouseEvent e)
							{
								m_oColorSetter.setColor(m_iColor, m_iColorSel);
							}
						}
				);
				this.addPaintListener(m_PaintListener);
			} // buildControl()
			
			void repaintRect(GC gc, int x, int y, int width, int height)
			{
				if(getSelected())
				{
				  String str="x";
				  gc.setBackground(AnsiCharColors.getColor(m_iColor, false));
				  gc.setForeground(AnsiCharColors.getOppositeColor(m_iColor, false));
		          Point textSize = gc.textExtent(str);
		          gc.drawText(str, (getSize().x - textSize.x)/2, (getSize().y - textSize.y)/2);
				}
			}
			
			public PaintListener m_PaintListener = new PaintListener()
			{

				public void paintControl(PaintEvent e) 
				{
					repaintRect(e.gc, e.x, e.y, e.width, e.height);
//		         repaintAll(e.gc);
				}

			};
			
		}
		protected CanvasColorButton[] m_canvasArray;
		int m_xSz;
		int m_ySz;
		byte m_iColorSel;
		byte m_iCurColorIndex;

		IDlgColorSet m_oDlgColorSetter;


		public GroupOfColors(Composite parent, int style, int xSzClrBox, int ySzClrBox, 
				byte iCurColorIndex, byte iColorSel, IDlgColorSet oClrSetter) 
		{
	      super(parent, style);
	      m_xSz=xSzClrBox;
	      m_ySz=ySzClrBox;
	      m_iCurColorIndex=iCurColorIndex;
	      m_iColorSel=iColorSel;
	      m_oDlgColorSetter=oClrSetter;

	      buildControls();
      }

		public void setSelectedColor(byte iColor)
		{
			byte i;
			if( iColor < (byte) AnsiCharColors.NUM_COLORS)
			{
			  for(i=0; i<AnsiCharColors.NUM_COLORS; i++)
			  {
				m_canvasArray[i].setSelected(false);
			  }
			  m_canvasArray[iColor].setSelected(true);
			  m_iCurColorIndex=iColor;
			}
		}
		
		public void buildControls()
		{
			GridLayout gridLayout = new GridLayout(4, true);

			setLayout(gridLayout);
			m_canvasArray = new CanvasColorButton[AnsiCharColors.NUM_COLORS];
			int i;
			for(i=0; i<AnsiCharColors.NUM_COLORS; i++)
			{
				GridData data=new GridData();
				data.heightHint = m_ySz;
				data.widthHint = m_xSz;
				data.verticalIndent = 0;
				data.horizontalIndent = 0;
				m_canvasArray[i]=new CanvasColorButton(this, SWT.None, (byte) i, m_iColorSel, m_oDlgColorSetter);
				m_canvasArray[i].setLayoutData(data);
				if(i==(int) m_iCurColorIndex) m_canvasArray[i].setSelected(true);

			}
		} // buildControls()


		
	} // Class GroupOfColors

	public DlgTextColorChooser(Shell parentShell, 
			byte iCurBackgroundColor, byte iCurForegroundColor, IDlgColorSet oColorSet) 
	{
		super(parentShell);
		m_iBackgroundColor=iCurBackgroundColor;
		m_iForegroundColor=iCurForegroundColor;
		m_oDlgColorSetter=oColorSet;
		// TODO Auto-generated constructor stub
	}

	public DlgTextColorChooser(IShellProvider parentShell) 
	{
		super(parentShell);
		// TODO Auto-generated constructor stub
	}
	
	GroupOfColors m_colorGroupBackground;
	GroupOfColors m_colorGroupForeground;
	Button m_butApplyWholeDocument;
	byte m_iBackgroundColor;
	byte m_iForegroundColor;
	IDlgColorSet m_oDlgColorSetter;
	
   @Override
   protected Control createDialogArea(Composite parent)
   {
       Composite comp= (Composite) super.createDialogArea(parent);
       
  
       
       RowLayout f1 = new RowLayout(SWT.VERTICAL);
       
       comp.setLayout(f1);
       
       Label lb1 = new Label(comp, SWT.CENTER);
       lb1.setText("Background Color");
       m_colorGroupBackground = new GroupOfColors(comp, SWT.None, 24, 20, 
    		  m_iBackgroundColor, IDlgColorSet.BACKGROUND, this);
       
       
       Label lb2 = new Label(comp, SWT.CENTER);
       lb2.setText("Foreground Color");
       m_colorGroupForeground = new GroupOfColors(comp, SWT.NONE, 24, 20, 
    		  m_iForegroundColor, IDlgColorSet.FOREGROUND, this);
       
       m_butApplyWholeDocument=new Button(comp, SWT.CHECK);
       m_butApplyWholeDocument.setText("Apply now to entire window");
       m_butApplyWholeDocument.setSelection(true);

       return comp;
   } // createDialogArea()
   
   @Override
   protected void configureShell(Shell shell) {
      super.configureShell(shell);
      shell.setText("Choose Text Colors");
   } // configureShell()


   public void setColor(byte iColor, byte iColorSel) 
	{
	   switch(iColorSel)
	   {
	   case IDlgColorSet.BACKGROUND:
		  if(iColor != m_iBackgroundColor)
		  {
			  m_colorGroupBackground.setSelectedColor(iColor);
		  }
	   	  m_iBackgroundColor=(byte) iColor;
	   	break;
	   case IDlgColorSet.FOREGROUND:
		  if(iColor != m_iForegroundColor)
		  {
			  m_colorGroupForeground.setSelectedColor(iColor);
		  }
	   	  m_iForegroundColor=(byte) iColor;
	   	break;
	   	default:
	   		break;
	   }
	   
   } // setColor()

    @Override
    protected void okPressed()
    {
        m_oDlgColorSetter.setColor(m_iBackgroundColor, IDlgColorSet.BACKGROUND);
        m_oDlgColorSetter.setColor(m_iForegroundColor, IDlgColorSet.FOREGROUND);
        
        if(m_butApplyWholeDocument.getSelection())
        {
        	m_oDlgColorSetter.applyToEntireWindow();
        }
        super.okPressed();
    }


	public void applyToEntireWindow() 
	{
		// Nothing to do here and we should never get called here.
	}
	
}
