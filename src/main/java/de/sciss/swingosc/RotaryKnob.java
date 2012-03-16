/*
 *  RotaryKnob.java
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

import javax.swing.BoundedRangeModel;
import javax.swing.JSlider;
import java.awt.Color;

public class RotaryKnob extends JSlider {
    protected Color colrKnob	= null;
    protected Color colrHand    = null;
    protected Color colrRange   = null;
    protected Color colrTrack   = null;

    private boolean centered    = false;

    public RotaryKnob() {
        super();
    }

    public RotaryKnob( BoundedRangeModel m ) {
        super( m );
    }

    public RotaryKnob( int min, int max ) {
        super( min, max );
    }

    public RotaryKnob( int min, int max, int value ) {
        super( min, max, value );
    }

    public boolean isCentered() { return centered; }
    public void setCentered( boolean b ) {
        if( centered != b ) {
            centered = b;
            firePropertyChange( "centered", !b, b );
        }
    }

    public Color getKnobColor() { return colrKnob; }
    public void setKnobColor( Color c ) {
        if( (colrKnob == null && c != null) || (colrKnob != null && !colrKnob.equals( c ))) {
            colrKnob = c;
            repaint();
        }
    }

    public Color getHandColor() { return colrHand; }
    public void setHandColor( Color c ) {
        if( (colrHand == null && c != null) || (colrHand != null && !colrHand.equals( c ))) {
            colrHand = c;
            repaint();
        }
    }

    public Color getRangeColor() { return colrRange; }
    public void setRangeColor( Color c ) {
        if( (colrRange == null && c != null) || (colrRange != null && !colrRange.equals( c ))) {
            colrRange = c;
            repaint();
        }
    }

    public Color getTrackColor() { return colrTrack; }
    public void setTrackColor( Color c ) {
        if( (colrTrack == null && c != null) || (colrTrack != null && !colrTrack.equals( c ))) {
            colrTrack = c;
            repaint();
        }
    }

    @Override public void updateUI() {
        setUI( new RotaryKnobUI( this ));
        updateLabelUIs();
    }
}
