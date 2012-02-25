/*
 *  RotaryKnobUI.java
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

import javax.swing.JComponent;
import javax.swing.JSlider;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.plaf.basic.BasicSliderUI;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
import java.awt.geom.Arc2D;
import java.awt.geom.Area;
import java.awt.geom.GeneralPath;

public class RotaryKnobUI extends BasicSliderUI {
    private static final float arcStartDeg      = -135f; // clock wise from 12 o'clock. thus 7 h 30 mins
    private static final float arcExtentDeg     = 270f;  // arcExtent * 180 / Math.PI;
    private static final double arcStart        = (-arcStartDeg + 90) * Math.PI / 180; // Math.PI * 1.25;   // aka 7 h 30 mins
    private static final double arcExtent       = arcExtentDeg * Math.PI / 180; // Math.PI * 1.50;  // thus the arcStop is at 4 h 30 mins
//    private static final double PI2             = Math.PI * 2;
    private static final double argHemi         = arcStart - Math.PI * 0.5;
    private static final NimbusRadioThumb thumb = new NimbusRadioThumb();
//    private static final Stroke strkHand        = new BasicStroke( 0.5f );
    private static final Stroke strkOut         = new BasicStroke( 6f );

    private boolean mOver       = false;
    private boolean mPressed    = false;
//    protected Color	colrKnob	= null;
    private final RotaryKnob knob;

//    private final Color colrHand = Color.black; // new Color( 0, 0, 0, 204 );

//    private final RoundRectangle2D rectHand     = new RoundRectangle2D.Float();
    private final GeneralPath pathHand          = new GeneralPath();
    private Shape shpHand                       = null;
    private final AffineTransform atHand        = new AffineTransform();
    private Area shpHandOut                     = null;
    private final Arc2D arc                     = new Arc2D.Float( 0, 0, 10, 10, arcStartDeg, -arcExtentDeg, Arc2D.PIE );
    private Area shpTrack                       = null;
    private final Insets trackBufIn             = new Insets( 0, 0, 0, 0 );

    private static int thumbFocusInsets = 3;
//    private float handWidth = 0.5f;

//private boolean GUGU = false;

    public RotaryKnobUI( final RotaryKnob knob ) {
        super( knob );
        this.knob = knob;
    }

    private Color getHandColor() {
        final Color c = NimbusHelper.mixColorWithAlpha( NimbusHelper.getTextColor(), knob.getHandColor() );
        return knob.isEnabled() ? c : NimbusHelper.adjustColor( c, 0f, 0f, 0f, -112 );
    }

    @Override public void paintThumb( Graphics g ) {
//        final int w = slider.getWidth();
//        final int h = slider.getHeight();
        final Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint( RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON );
//        g2.setRenderingHint( RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE );
        final int state = (knob.isEnabled() ? NimbusHelper.STATE_ENABLED : 0) |
                          (knob.hasFocus()  ? NimbusHelper.STATE_FOCUSED : 0) |
                          (mOver            ? NimbusHelper.STATE_OVER    : 0) |
                          (mPressed         ? NimbusHelper.STATE_PRESSED : 0);
        thumb.paint( state, knob.getKnobColor(), g2, thumbRect.x, thumbRect.y, thumbRect.width, thumbRect.height );
        g2.setColor( getHandColor() );
        g2.fill( shpHand );
    }

    @Override
    public void paintTrack( Graphics g ) {
        final Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint( RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON );
//        g2.setColor( Color.green );
//        g2.fillRect( contentRect.x, contentRect.y, contentRect.width, contentRect.height );
        final Color c = NimbusHelper.getControlHighlighColor(); // getSelectedTextColor();
        g2.setColor( knob.isEnabled() ? c : NimbusHelper.adjustColor( c, 0f, 0f, 0f, -112 ));
        g2.fill( shpTrack );
    }

    @Override
    public void paintFocus( Graphics g ) {}

    @Override
    protected TrackListener createTrackListener( JSlider knob ) {
        return new RangeTrackListener();
    }

    protected int valueForPosition( int x, int y ) {
        final int min       = knob.getMinimum();
        final int max       = knob.getMaximum();
        final float xc      = thumbRect.width  * 0.5f + thumbRect.x;
        final float yc      = thumbRect.height * 0.5f + thumbRect.y;
        final float dx      = x - xc;
        final double a      = Math.atan2( yc - y, dx );
        final double b      = Math.sin(a);
        final double c      = Math.min(argHemi, Math.acos(b));
        final double d      = (argHemi - c) / argHemi * 0.5;
        final double v      = dx < 0 ? d : 1.0 - d;
        return (int) (v * (max - min) + min + 0.5);
    }

    private class RangeTrackListener extends TrackListener {
        private int currentMouseX, currentMouseY;
        private boolean mDragging = false;

        @Override
        public void mousePressed( MouseEvent e ) {
            if( !knob.isEnabled() ) return;

            currentMouseX = e.getX();
            currentMouseY = e.getY();

            if( knob.isRequestFocusEnabled() ) {
                knob.requestFocus();
            }

            if( shpHandOut.contains( currentMouseX, currentMouseY )) {
                if( UIManager.getBoolean( "Slider.onlyLeftMouseButtonDrag" ) && !SwingUtilities.isLeftMouseButton( e )) {
                    return;
                }
//GUGU = true;
//                mDragging = true;
//                mPressed = true;
//                knob.repaint();
//                return;
            }

            if (!SwingUtilities.isLeftMouseButton(e)) {
                return;
            }

            mPressed = true;
//            mDragging = false;
            mDragging = true;

            knob.setValueIsAdjusting( true );
            knob.setValue( valueForPosition( e.getX(), e.getY() ));
            knob.repaint();
        }

        @Override
        public void mouseDragged( MouseEvent e ) {
            if( mDragging ) {
                knob.setValue( valueForPosition( e.getX(), e.getY() ));
            }
        }

        @Override
        public void mouseReleased( MouseEvent e ) {
            if( mPressed ) {
                mPressed = false;
                mDragging = false;
                knob.setValueIsAdjusting( false );
//GUGU = false;
                knob.repaint();
            }
        }

        @Override
        public void mouseEntered( MouseEvent e ) {
            if( !knob.isEnabled() ) return;

            mOver = true;
            knob.repaint();
        }

        @Override
        public void mouseExited( MouseEvent e ) {
            if( mOver ) {
                mOver = false;
                knob.repaint();
            }
        }
    }

    @Override
    protected Dimension getThumbSize() {
        final int w     = contentRect.width;
        final int h     = contentRect.height;
        final int ext;
        if( knob.getPaintTrack() ) {
//            trackBufIn.left     = 8;
//            trackBufIn.top      = 8;
//            trackBufIn.right    = 8;
//            trackBufIn.bottom   = 4;
            final int w1        = (int) (w * 0.75f);
            final int h1        = (int) (h * 0.875f);
            ext                 = Math.min( w1, h1 );
            final int xo        = contentRect.x + ((w1 - ext) >> 1);
            final int yo        = contentRect.y + ((h1 - ext) >> 1);
            trackBufIn.left     = (w - ext) >> 1;
            trackBufIn.top      = (h - ext) >> 1;
            trackBufIn.right    = (w - ext + 1) >> 1;
            trackBufIn.bottom   = 0;
            final double exto   = ext / 0.75f;
            final double exti   = ext / 0.875f;
            final double ring   = (exto - exti) * 0.5;
            arc.setFrame( xo, yo, exto, exto );
            shpTrack = new Area( arc );
            arc.setFrame( xo + ring, yo + ring, exti, exti );
            shpTrack.subtract( new Area( arc ));

        } else {
            trackBufIn.left     = 0;
            trackBufIn.top      = 0;
            trackBufIn.right    = 0;
            trackBufIn.bottom   = 0;
            ext = Math.min( w, h );
        }
//        final int w1    = w - (trackBufIn.left + trackBufIn.right);
//        final int h1    = h - (trackBufIn.top + trackBufIn.bottom);
        return new Dimension( ext, ext );
    }

    @Override
    public Dimension getPreferredHorizontalSize() {
        return new Dimension( 32, 32 ); // return getThumbSize();
    }

    @Override
    public Dimension getPreferredVerticalSize() {
        return new Dimension( 32, 32 ); // return getThumbSize();
    }

    @Override
    protected void calculateTrackRect() {
        trackRect.x         = contentRect.x + trackBufIn.left;
        trackRect.y         = contentRect.y + trackBufIn.top;
        final int w         = contentRect.width  - (trackBufIn.left + trackBufIn.right);
        final int h         = contentRect.height - (trackBufIn.top + trackBufIn.bottom);
        final int ext       = Math.min( w, h );
        trackRect.width     = ext;
        trackRect.height    = ext;
        trackRect.x        += (w - ext) >> 1;
        trackRect.y        += (h - ext) >> 1;

        final double handWidth = Math.sqrt( thumbRect.width / 56.0 );

//        final double x = (thumbRect.width - handWidth) * 0.5;
//        final double h = (thumbRect.height - thumbFocusInsets - thumbFocusInsets) * 0.5 + handWidth;
//        rectHand.setRoundRect( x, thumbFocusInsets, handWidth, h, handWidth, handWidth ); // 2.0, 2.0 );

        final double hwh = handWidth * 0.5;
        final double hwq = handWidth * 0.25;

        final float xc      = thumbRect.width  * 0.5f + thumbRect.x;
//        final float yc      = thumbRect.height * 0.5f + thumbRect.y;

        pathHand.reset();
//        pathHand.moveTo( (thumbRect.width - hwh) * 0.5f, thumbFocusInsets );
//        pathHand.lineTo( (thumbRect.width + hwh) * 0.5f, thumbFocusInsets );
//        pathHand.lineTo( x + handWidth, thumbFocusInsets + h );
//        pathHand.lineTo( x, thumbFocusInsets + h );
        final int y1    = thumbRect.y + thumbFocusInsets;
        final double y2 = (thumbRect.height - thumbFocusInsets - thumbFocusInsets) * 0.5 + handWidth + y1;
        pathHand.moveTo( xc - hwq, y1 );
        pathHand.lineTo( xc + hwq, y1 );
        pathHand.lineTo( xc + hwh, y2 );
        pathHand.lineTo( xc - hwh, y2 );
        pathHand.closePath();
    }

    @Override
    protected void calculateThumbLocation() {
        thumbRect.x = trackRect.x;
        thumbRect.y = trackRect.y;

        final int min       = knob.getMinimum();
        final int max       = knob.getMaximum();
        final double v      = (double) (knob.getValue() - min) / (max - min);
        final double ang    = v * arcExtent + arcStart;
        final float xc      = thumbRect.width  * 0.5f + thumbRect.x;
        final float yc      = thumbRect.height * 0.5f + thumbRect.y;
        atHand.setToRotation( ang, xc, yc );
        shpHand = atHand.createTransformedShape( pathHand );
        shpHandOut = new Area( strkOut.createStrokedShape( shpHand ));
        shpHandOut.add( new Area( shpHand ));
    }

    @Override
    protected void installDefaults( JSlider slider ) {
        super.installDefaults( slider );
        focusInsets.left    = 0;
        focusInsets.top     = 0;
        focusInsets.right   = 0;
        focusInsets.bottom  = 0;
    }

    @Override
    protected void calculateFocusRect() {
        focusRect.x         = insetCache.left;
        focusRect.y         = insetCache.top;
        final int ext       = Math.min( knob.getWidth(), knob.getHeight() );
        focusRect.width     = ext - (insetCache.left + insetCache.right);
        focusRect.height    = ext - (insetCache.top + insetCache.bottom);
    }

    @Override
    protected void calculateTrackBuffer() {
        trackBuffer = 0;
//        if( knob.getPaintTrack() ) {
//            trackBufIn.left     = 8;
//            trackBufIn.top      = 8;
//            trackBufIn.right    = 8;
//            trackBufIn.bottom   = 4;
//        } else {
//            trackBufIn.left     = 0;
//            trackBufIn.top      = 0;
//            trackBufIn.right    = 0;
//            trackBufIn.bottom   = 0;
//        }
    }

    @Override
    protected void calculateTickRect() {
        tickRect.x      = trackRect.x;
        tickRect.y      = trackRect.y;
        tickRect.width  = 0;
        tickRect.height = 0;
    }

    @Override
    protected void calculateLabelRect() {
        labelRect.x         = tickRect.x;
        labelRect.y         = tickRect.y;
        labelRect.width     = 0;
        labelRect.height    = 0;
    }

    @Override
    public Dimension getPreferredSize( JComponent c ) {
        recalculateIfInsetsChanged();
        final Dimension d = new Dimension( getPreferredVerticalSize() );
//        d.width  = insetCache.left + insetCache.right;
//        d.width += thumbFocusInsets.left + thumbFocusInsets.right;
//        d.width += trackRect.width + tickRect.width + labelRect.width;
        return d;
    }
}