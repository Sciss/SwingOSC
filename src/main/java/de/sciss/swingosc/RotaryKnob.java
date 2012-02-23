package de.sciss.swingosc;

import javax.swing.JSlider;

public class RotaryKnob extends JSlider {
    public RotaryKnob() {
        this( 0, 100 );
    }

    public RotaryKnob( int min, int max ) {
        super( min, max );
    }

    @Override public void updateUI() {
        setUI( new RotaryKnobUI( this ));
        updateLabelUIs();
    }
}
