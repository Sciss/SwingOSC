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