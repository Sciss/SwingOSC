package de.sciss.swingosc;

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JRadioButton;
import javax.swing.WindowConstants;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.FlowLayout;

public class RotaryKnobTest implements Runnable {
    public static void main( String[] args ) {
        EventQueue.invokeLater( new RotaryKnobTest() );
    }

    private void constrain( JComponent c, int sz ) {
        c.setPreferredSize( new Dimension( sz, sz ));
    }

    public void run() {
        final JFrame f = new JFrame( "Rotary Knob" );
        final Container cp = f.getContentPane();
        cp.setLayout( new FlowLayout() );
        final RotaryKnob knob1 = new RotaryKnob();
        final RotaryKnob knob2 = new RotaryKnob();
        final RotaryKnob knob3 = new RotaryKnob();
        final RotaryKnob knob4 = new RotaryKnob();
        final RotaryKnob knob5 = new RotaryKnob();
        final RotaryKnob knob6 = new RotaryKnob();
        final RotaryKnob knob7 = new RotaryKnob();
        final RotaryKnob knob8 = new RotaryKnob();
        final RotaryKnob knob9 = new RotaryKnob();
//        knob2.setKnobColor(Color.red );
//        knob3.setKnobColor(Color.black );
//        knob3.setHandColor(Color.white);
//        knob4.setFocusable(false);
        knob5.setKnobColor(new Color(200, 100, 50)); // Color.red );
//        knob5.setFocusable(false);
//        knob6.setFocusable(false);
        knob6.setKnobColor(new Color(0, 100, 50));
        knob6.setHandColor(Color.white);
        knob7.setEnabled(false);
        knob8.setKnobColor(Color.red);
        knob8.setEnabled(false);
        knob9.setKnobColor(Color.black);
        knob9.setHandColor(Color.white);
        knob9.setEnabled(false);

        knob1.setValue(   0 );
        knob2.setValue(  20 );
        knob3.setValue(  40 );
        knob7.setValue(  60 );
        knob4.setValue(  80 );
        knob5.setValue( 100 );
        knob8.setValue(  47 );
        knob9.setValue(  33 );

        knob1.setPaintTrack( false );
//        constrain( knob1,  16 );
        knob2.setPaintTrack( false );
        constrain( knob2,  25 );
//        knob3.setPaintTrack( false );
//        constrain( knob3,  32 );
        constrain( knob4,  34 ); // 45 );
        constrain( knob5,  64 );
        constrain( knob6,  91 );
        constrain( knob7, 128 );
        constrain( knob8, 181 );
        constrain( knob9, 256 );

        cp.add( knob1 );
        cp.add( knob2 );
        cp.add( knob3 );
        cp.add( knob4 );
        cp.add( knob5 );
        cp.add( knob6 );
        cp.add( knob7 );
        cp.add( knob8 );
        cp.add( knob9 );

        final JRadioButton rb1 = new JRadioButton();
        final JRadioButton rb2 = new JRadioButton();
        rb2.setEnabled( false );
        cp.add( rb1 );
        cp.add( rb2 );

        f.pack();
//        f.setSize( 180, 180 );
        f.setLocationRelativeTo( null );
        f.setDefaultCloseOperation( WindowConstants.EXIT_ON_CLOSE );
        f.setVisible( true );
    }
}
