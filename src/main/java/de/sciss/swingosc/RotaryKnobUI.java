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
import javax.swing.plaf.basic.BasicSliderUI;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.MouseEvent;

public class RotaryKnobUI extends BasicSliderUI {
    private final NimbusRadioThumb thumb = new NimbusRadioThumb();

    private boolean mOver       = false;
    private boolean mPressed    = false;
//    protected Color	colrKnob	= null;
    private final RotaryKnob knob;

    public RotaryKnobUI( final RotaryKnob knob ) {
        super( knob );
        this.knob = knob;
    }

    @Override public void paintThumb( Graphics g ) {
        final int w = slider.getWidth();
        final int h = slider.getHeight();
        final Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint( RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON );
        final int state = (knob.isEnabled() ? NimbusHelper.STATE_ENABLED : 0) |
                          (knob.hasFocus()  ? NimbusHelper.STATE_FOCUSED : 0) |
                          (mOver            ? NimbusHelper.STATE_OVER    : 0) |
                          (mPressed         ? NimbusHelper.STATE_PRESSED : 0);
        thumb.paint( state, knob.getKnobColor(), g2, 0, 0, w, h );
    }

    @Override
    public void paintTrack( Graphics g ) {
        // nada
    }

    @Override
    public void paintFocus( Graphics g ) {}

    @Override
    protected TrackListener createTrackListener( JSlider knob ) {
        return new RangeTrackListener();
    }

    private class RangeTrackListener extends TrackListener {
        @Override
        public void mousePressed( MouseEvent e ) {
            if( !knob.isEnabled() ) return;

            mPressed = true;
            if( knob.isRequestFocusEnabled() ) knob.requestFocus();
            knob.repaint();
        }

        @Override
        public void mouseReleased( MouseEvent e ) {
            if( mPressed ) {
                mPressed = false;
                knob.repaint();
            }
        }
    }

    @Override
    protected Dimension getThumbSize() {
        return new Dimension( 32, 32 ); // XXX
    }

    @Override
    public Dimension getPreferredHorizontalSize() {
        return getThumbSize();
    }

    @Override
    public Dimension getPreferredVerticalSize() {
        return getThumbSize();
    }

    @Override
    protected void calculateTrackRect() {
        trackRect.x         = contentRect.x;
        trackRect.y         = contentRect.y;
        trackRect.width     = thumbRect.width;
        trackRect.height    = thumbRect.height;
    }

    @Override
    protected void calculateThumbLocation() {
        thumbRect.x = trackRect.x;
        thumbRect.y = trackRect.y;
    }

    @Override
    protected void calculateFocusRect() {
        focusRect.x         = insetCache.left;
        focusRect.y         = insetCache.top;
        final int ext       = Math.min(knob.getWidth(), knob.getHeight());
        focusRect.width     = ext - (insetCache.left + insetCache.right);
        focusRect.height    = ext - (insetCache.top + insetCache.bottom);
    }

    @Override
    protected void calculateTrackBuffer() {
        trackBuffer = 0;
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
//        d.width += focusInsets.left + focusInsets.right;
//        d.width += trackRect.width + tickRect.width + labelRect.width;
        return d;
    }
}