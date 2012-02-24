package de.sciss.swingosc;

import javax.swing.JFrame;
import javax.swing.JRadioButton;
import javax.swing.WindowConstants;
import java.awt.Color;
import java.awt.Container;
import java.awt.EventQueue;
import java.awt.FlowLayout;

public class RotaryKnobTest implements Runnable {
    public static void main( String[] args ) {
        EventQueue.invokeLater( new RotaryKnobTest() );
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
        knob2.setKnobColor( Color.red );
        knob3.setKnobColor( Color.black );
        knob3.setHandColor( Color.white );
        knob4.setEnabled( false );
        knob5.setFocusable( false );
        knob6.setKnobColor( Color.red );
        knob6.setFocusable( false );
        knob7.setKnobColor( Color.red );
        knob7.setEnabled( false );
        knob8.setKnobColor( Color.black );
        knob8.setHandColor( Color.white );
        knob8.setEnabled( false );
        knob9.setFocusable( false );
        knob9.setKnobColor( Color.black );
        knob9.setHandColor( Color.white );

        knob1.setValue(   0 );
        knob2.setValue(  20 );
        knob3.setValue(  40 );
        knob4.setValue(  60 );
        knob5.setValue(  80 );
        knob6.setValue( 100 );
        knob7.setValue(  47 );
        knob8.setValue(  33 );

        cp.add( knob1 );
        cp.add( knob2 );
        cp.add( knob3 );
        cp.add( knob5 );
        cp.add( knob6 );
        cp.add( knob9 );
        cp.add( knob4 );
        cp.add( knob7 );
        cp.add( knob8 );

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
