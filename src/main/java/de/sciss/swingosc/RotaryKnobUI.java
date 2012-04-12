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
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

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

    private final float[] dashTrackHigh = new float[] { 2f, 1f };

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
    private static final Arc2D arc              = new Arc2D.Float( 0, 0, 10, 10, arcStartDeg, -arcExtentDeg, Arc2D.PIE );
    private Area shpTrack                       = null;
    private final Insets trackBufIn             = new Insets( 0, 0, 0, 0 );
    private Stroke strkTrackHigh                = null;
    private final Arc2D arcTrackHigh            = new Arc2D.Float( 0, 0, 10, 10, arcStartDeg, 0, Arc2D.OPEN );

//private boolean GUGU = false;

    private final PropertyChangeListener trackCentered = new PropertyChangeListener() {
        public void propertyChange( PropertyChangeEvent e ) {
//System.out.println( "PROPERTY CHANGE" );
            calculateThumbLocation();
            knob.repaint();
        }
    };

    public RotaryKnobUI( final RotaryKnob knob ) {
        super( knob );
        this.knob = knob;
    }

    private Color getHandColor() {
        final Color c = NimbusHelper.mixColorWithAlpha( NimbusHelper.getTextColor(), knob.getHandColor() );
        return knob.isEnabled() ? c : NimbusHelper.adjustColor( c, 0f, 0f, 0f, -112 );
    }

    private Color getTrackColor() {
        final Color c = NimbusHelper.mixColorWithAlpha( NimbusHelper.getControlHighlighColor(), knob.getTrackColor() );
        return knob.isEnabled() ? c : NimbusHelper.adjustColor( c, 0f, 0f, 0f, -112 );
    }

    private Color getRangeColor() {
        final Color c = NimbusHelper.mixColorWithAlpha( NimbusHelper.getBaseColor(), knob.getRangeColor() );
        return knob.isEnabled() ? c : NimbusHelper.adjustColor( c, 0f, 0f, 0f, -112 );
    }

    @Override public void paintThumb( Graphics g ) {
//        System.out.println( "::: paintThumb :::" );

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
//        System.out.println( "::: paintTrack :::" );

        final Graphics2D g2 = (Graphics2D) g;
        final RenderingHints hintsOld = g2.getRenderingHints();
        g2.setRenderingHint( RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON );
        g2.setRenderingHint( RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE );
//        g2.setColor( Color.green );
//        g2.fillRect( contentRect.x, contentRect.y, contentRect.width, contentRect.height );
        g2.setColor( getTrackColor() );
        g2.fill( shpTrack );
        g2.setColor( getRangeColor() );
        final Stroke strkOrig = g2.getStroke();
        g2.setStroke( strkTrackHigh );
        g2.draw( arcTrackHigh );
        g2.setStroke( strkOrig );
        g2.setRenderingHints( hintsOld );
    }

//    public void paint( Graphics g, JComponent c ) {
//        g.setColor( Color.green );
//        g.fillRect( 0, 0, c.getWidth(), c.getHeight() );
////        g.setColor( Color.yellow );
////        g.fillRect( contentRect.x, contentRect.y, contentRect.width, contentRect.height );
//        super.paint( g, c );
//    }

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
//System.out.println( "getThumbSize() -> " + thumbRect.width + ", " + thumbRect.height );
        return thumbRect.getSize();
    }

    @Override
    protected void calculateThumbSize() {
        final int x     = contentRect.x;
        final int y     = contentRect.y;
        final int w     = contentRect.width; // - ((1 - contentRect.width) & 1);
        final int h     = contentRect.height;
        final int ext;
//        if( false ) {
//            final int w1        = (int) (w * 0.75f);
//            final int h1        = (int) ((h - 1) * 0.875f); // nasty
//            ext                 = Math.min( w1, h1 );
//            final int tt  = (h - ext + 1) >> 1; // nasty
//            trackBufIn.left     = (w - ext) >> 1;
//            trackBufIn.top      = (int) (tt + 0.5f); // (h - ext) >> 1;
//            trackBufIn.right    = (w - ext + 1) >> 1;
//            trackBufIn.bottom   = 0;
//            final int exto     = (int) (ext / 0.75f - 0.5f);  // nasty
//            final float exti   = ext / 0.875f;
//            final float extm   = (exti + exto) * 0.5f;
//            final float ring   = (exto - exti) * 0.5f;
//            final float ringo  = ring * 0.25f;
//            final float ringh  = ring * 0.5f;
//            final float exto2  = exto - (ringo + ringo);
//            final float xo     = x + (w - exto) * 0.5f;
//            final float yo     = y + (h + tt - exto) * 0.5f;
//            arc.setFrame( xo + ringo, yo + ringo, exto2, exto2 );
//            shpTrack = new Area( arc );
//            final float exti2  = exti - (ringo + ringo);
//            arc.setFrame( xo + ringo + ring, yo + ringo + ring, exti2, exti2 );
//            shpTrack.subtract( new Area( arc ));
////            final float dash    = 2f; // Math.max( 2f, ring );
////            final float[] dashTrackHigh = new float[] { dash, dash * 0.5f };
//            strkTrackHigh       = new BasicStroke( ring, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0.5f, dashTrackHigh, 0f );
//            arcTrackHigh.setFrame( xo + ringh, yo + ringh, extm, extm );
////System.out.println( "calculateThumbSize(). w = " + w + ", h = " + h + ", w1 = " + w1 + ", h1 = " + h1 + ", ext = " + ext + ", xo = " + xo + ", yo " + yo + ", ringo = " + ringo );
//
//        } else
        if( knob.getPaintTrack() ) {
            final float margin0 = Math.min( w, h / 0.875f ) * 0.125f;
            ext                 = (int) (margin0 * 6);
            final float margin  = (float) ext / 6;
            final float diam    = margin * 8;
            final int inLeft    = (w - ext) >> 1;
            final float inTop   = (h - margin * 5) * 0.5f;
            final int inTopI    = (int) (inTop + 0.5f);
            trackBufIn.left     = inLeft;
            trackBufIn.right    = w - ext - inLeft;
            trackBufIn.top      = inTopI;
            trackBufIn.bottom   = h - ext - inTopI;

//System.out.println( "w = " + w + "; h = " + h + "; margin = " + margin + "; ext = " + ext + "; track.left = " + trackBufIn.left + "; track.right = " + trackBufIn.right + "; track.top = " + trackBufIn.top + "; track.bottom = " + trackBufIn.bottom );

            final float xo      = x + inLeft - margin;
            final float yo      = y + inTop  - margin;
            final float ring    = margin * 0.625f; // 0.5f;
            final float ringh   = ring * 0.5f;
            final float offLow1 = ring * 0.333f; // 0.25f;
            final float offLow2 = offLow1 + ring;
            final float extHigh = diam - ring;
            final float extLow1 = diam - offLow1 - offLow1;
            final float extLow2 = extLow1 - ring - ring;

            arc.setFrame( xo + offLow1, yo + offLow1, extLow1, extLow1 );
            shpTrack = new Area( arc );
            arc.setFrame( xo + offLow2, yo + offLow2, extLow2, extLow2 );
            shpTrack.subtract( new Area( arc ));

            strkTrackHigh       = new BasicStroke( ring, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0.5f, dashTrackHigh, 0f );
            arcTrackHigh.setFrame( xo + ringh, yo + ringh, extHigh, extHigh );

        } else {
            trackBufIn.left     = 0;
            trackBufIn.top      = 0;
            trackBufIn.right    = 0;
            trackBufIn.bottom   = 0;
            ext = Math.min( w, h );
        }
        thumbRect.setSize( ext, ext );
    }

    @Override
    public Dimension getPreferredHorizontalSize() {
        if( knob.getPaintTrack() ) {
            return new Dimension( 37, 32 );
        } else {
            return new Dimension( 27, 27 );
        }
    }

    @Override
    public Dimension getPreferredVerticalSize() {
        return getPreferredHorizontalSize();
    }

    @Override
    protected void calculateTrackRect() {
//        final int thumbFocusInsets = 3;

        trackRect.x         = contentRect.x + trackBufIn.left;
        trackRect.y         = contentRect.y + trackBufIn.top;
        final int w         = contentRect.width  - (trackBufIn.left + trackBufIn.right);
        final int h         = contentRect.height - (trackBufIn.top + trackBufIn.bottom);
        final int ext       = Math.min( w, h );
        trackRect.width     = ext;
        trackRect.height    = ext;
        trackRect.x        += (w - ext) >> 1;
        trackRect.y        += (h - ext) >> 1;

//System.out.println( "calculateTrackRect(). w = " + w + ", h = " + h + " -> " + trackRect.x + ", " + trackRect.y + ", " + trackRect.width + ", " + thumbRect.height );
    }

    @Override
    protected void calculateThumbLocation() {
        thumbRect.x = trackRect.x;
        thumbRect.y = trackRect.y;

        final int min       = knob.getMinimum();
        final int max       = knob.getMaximum();
        final double v      = (double) (knob.getValue() - min) / (max - min);
        final double ext    = v * arcExtent;
        final double ang    = ext + arcStart;
        final float xc      = thumbRect.width  * 0.5f + thumbRect.x;
        final float yc      = thumbRect.height * 0.5f + thumbRect.y;
        atHand.setToRotation( ang, xc, yc );

        pathHand.reset();
        final int thumbFocusInsets = 3;
        final int y1    = thumbRect.y + thumbFocusInsets;
        final double handWidth = Math.sqrt( thumbRect.width / 56.0 );
        final double hwh = handWidth * 0.5;
        final double hwq = handWidth * 0.25;
        final double y2 = (thumbRect.height - thumbFocusInsets - thumbFocusInsets) * 0.5 + handWidth + y1;
        pathHand.moveTo( xc - hwq, y1 );
        pathHand.lineTo( xc + hwq, y1 );
        pathHand.lineTo( xc + hwh, y2 );
        pathHand.lineTo( xc - hwh, y2 );
        pathHand.closePath();

        shpHand = atHand.createTransformedShape( pathHand );
        shpHandOut = new Area( strkOut.createStrokedShape( shpHand ));
        shpHandOut.add( new Area( shpHand ));

        if( knob.isCentered() ) {
            arcTrackHigh.setAngleStart( 90 );
            arcTrackHigh.setAngleExtent( ((0.5 - v) * arcExtent) * 180 / Math.PI );
        } else {
            arcTrackHigh.setAngleStart( arcStartDeg );
            arcTrackHigh.setAngleExtent( ext * -180 / Math.PI );
        }

//        System.out.println( "calculateThumbLocation(). x = " + thumbRect.x + ", y = " + thumbRect.y + ", xc = " + xc + ", yc = " + yc );
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
    protected void installListeners( JSlider slider ) {
        super.installListeners( slider );
        slider.addPropertyChangeListener( "centered", trackCentered );
    }

    @Override
    protected void uninstallListeners( JSlider slider ) {
        super.uninstallListeners( slider );
        slider.removePropertyChangeListener( "centered", trackCentered );
    }

//    @Override
//    protected void calculateFocusRect() {
//        focusRect.x         = insetCache.left;
//        focusRect.y         = insetCache.top;
//        final int ext       = Math.min( knob.getWidth(), knob.getHeight() );
//        focusRect.width     = ext - (insetCache.left + insetCache.right);
//        focusRect.height    = ext - (insetCache.top + insetCache.bottom);
//    }

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
        return new Dimension( getPreferredHorizontalSize() );
//        d.width  = insetCache.left + insetCache.right;
//        d.width += thumbFocusInsets.left + thumbFocusInsets.right;
//        d.width += trackRect.width + tickRect.width + labelRect.width;
    }
}