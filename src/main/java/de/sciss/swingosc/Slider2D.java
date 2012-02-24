/*
 *  Slider2D.java
 *  (SwingOSC)
 *
 *  Copyright (c) 2005-2012 Hanns Holger Rutz. All rights reserved.
 *
 *	This software is free software; you can redistribute it and/or
 *	modify it under the terms of the GNU General Public License
 *	as published by the Free Software Foundation; either
 *	version 2, june 1991 of the License, or (at your option) any later version.
 *
 *	This software is distributed in the hope that it will be useful,
 *	but WITHOUT ANY WARRANTY; without even the implied warranty of
 *	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 *	General Public License for more details.
 *
 *	You should have received a copy of the GNU General Public
 *	License (gpl.txt) along with this software; if not, write to the Free Software
 *	Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 *
 *
 *	For further information, please contact Hanns Holger Rutz at
 *	contact@sciss.de
 */

package de.sciss.swingosc;

import java.awt.*;

/**
 *	A pane with a knob moveable in two dimensions.
 */
public class Slider2D
extends MonoSlider
{
	private static int kHandleWidth		= 13; // 17; // 15;
	private static int kHandleHeight	= 13; // 17; // 15;
	
//	private static Image handleImg		= null;
//	private static Image handleImgD		= null;
//
//	private Image imgKnob 				= null;

    private final NimbusSliderThumb thumb = new NimbusSliderThumb();

	private static final Insets valInsets = new Insets(
		(kHandleHeight >> 1) + 1, (kHandleWidth >> 1) + 1,
		((kHandleHeight + 1) >> 1) + 1, ((kHandleWidth + 1) >> 1) + 1 );

	public Slider2D()
	{
		super( true, true, false, false );

//		if( handleImg == null ) {
//			try {
//				final URL url = getClass().getResource( "handle.png" );
//				handleImg = Toolkit.getDefaultToolkit().getImage( url );
//				waitForImage( handleImg );
//			}
//			catch( NullPointerException e1 ) { System.out.println( e1 );}
//		}
	}

    protected boolean frameFocus() { return true; }

//	public void dispose()
//	{
//		if( imgKnob != null ) {
//			imgKnob.flush();
//			imgKnob = null;
//		}
//		super.dispose();
//	}
	
	public void setKnobColor( Color c )
	{
//		if( imgKnob != null ) {
//			imgKnob.flush();
//			imgKnob = null;
//		}
//
		super.setKnobColor( c );
	}
	
	protected void paintKnob( Graphics2D g2, int w, int h ) {
		final int kx = (int) (knobX * (w - kHandleWidth - 2)) - 1; // + 1;
		final int ky = (int) ((1.0f - knobY) * (h - kHandleHeight - 2)) - 1; // + 1;

        g2.setRenderingHint( RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON );
        final int state = (isEnabled()  ? NimbusHelper.STATE_ENABLED : 0) |
//                          (hasFocus()   ? NimbusHelper.STATE_FOCUSED : 0) |
                          (mOver ? NimbusHelper.STATE_OVER    : 0) |
                          (mPressed ? NimbusHelper.STATE_PRESSED : 0);
        thumb.paint( state, colrKnob, g2, kx, ky, kHandleWidth + 4, kHandleHeight + 4 );
		
//		if( handleImg != null ) {
//			if( isEnabled() ) {
//				if( colrKnob != null ) {
//					if( imgKnob == null ) {
//						imgKnob = createImage( new FilteredImageSource(
//								handleImg.getSource(), fltKnob ));
//					}
//					g2.drawImage( imgKnob, kx, ky, this );
//				} else {
//					g2.drawImage( handleImg, kx, ky, this );
//				}
//				g2.setColor( colrBdLR );
//			} else {
//				if( handleImgD == null ) {
//					handleImgD = createImage( new FilteredImageSource(
//						handleImg.getSource(), fltKnobD ));
//				}
//				g2.drawImage( handleImgD, kx, ky, this );
//			}
//			g2.drawLine( kx, ky + kHandleHeight, kx + kHandleWidth - 1, ky + kHandleHeight );
//		} else {
//			g2.fillRect( kx, ky, kHandleWidth, kHandleHeight );
//		}
	}
		
	protected Insets getValueInsets()
	{
		return valInsets;
	}
}
