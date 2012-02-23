package de.sciss.swingosc;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.WindowConstants;
import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.EventQueue;

public class RotaryKnobTest implements Runnable {
    public static void main( String[] args ) {
        EventQueue.invokeLater( new RotaryKnobTest() );
    }

    public void run() {
        final JFrame f = new JFrame( "Rotary Knob" );
        final Container cp = f.getContentPane();
        final RotaryKnob knob = new RotaryKnob();

        cp.add( knob, BorderLayout.CENTER );
        cp.add( new JButton( "..." ), BorderLayout.SOUTH );
        // f.pack();
        f.setSize( 180, 220 );
        f.setLocationRelativeTo( null );
        f.setDefaultCloseOperation( WindowConstants.EXIT_ON_CLOSE );
        f.setVisible( true );
    }
}
