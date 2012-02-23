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

import javax.swing.plaf.basic.BasicSliderUI;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

public class RotaryKnobUI extends BasicSliderUI {
    private final NimbusRadioThumb thumb = new NimbusRadioThumb();

    public RotaryKnobUI( final RotaryKnob knob ) {
        super( knob );
    }

    @Override public void paintThumb( Graphics g ) {
        final int w = slider.getWidth();
        final int h = slider.getHeight();
        final Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint( RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON );
        final int state = (slider.isEnabled()  ? NimbusHelper.STATE_ENABLED : 0); // |
//                          (hasFocus()   ? NimbusHelper.STATE_FOCUSED : 0) |
//                          (mouseOver    ? NimbusHelper.STATE_OVER    : 0) |
//                          (mousePressed ? NimbusHelper.STATE_PRESSED : 0);
        thumb.paint( state, null /* color */, g2, 0, 0, w, h );
    }

    @Override
    public void paintFocus( Graphics g ) {}
}