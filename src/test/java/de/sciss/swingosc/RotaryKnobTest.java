package de.sciss.swingosc;

import javax.swing.JFrame;
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
        knob2.setKnobColor( Color.red );
        knob3.setKnobColor( Color.black );
        knob4.setEnabled( false );

        cp.add( knob1 );
        cp.add( knob2 );
        cp.add( knob3 );
        cp.add( knob4 );
        f.pack();
//        f.setSize( 180, 180 );
        f.setLocationRelativeTo( null );
        f.setDefaultCloseOperation( WindowConstants.EXIT_ON_CLOSE );
        f.setVisible( true );
    }
}
