package de.sciss.swingosc;

import javax.swing.LookAndFeel;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

public class Nimbus {
    public static void setLookAndFeel() {
        try {
            final LookAndFeel current = UIManager.getLookAndFeel();
            if( !current.getName().toLowerCase().equals( "nimbus" )) {
                final UIManager.LookAndFeelInfo[] infos = UIManager.getInstalledLookAndFeels();
                for( int i = 0; i < infos.length; i++ ) {
                    if( infos[ i ].getName().toLowerCase().equals( "nimbus" )) {
                        UIManager.setLookAndFeel( infos[ i ].getClassName() );
                    }
                }
            }
        }
        catch( UnsupportedLookAndFeelException e1 ) {
            e1.printStackTrace();
        }
        catch( ClassNotFoundException e1 ) {
            e1.printStackTrace();
        }
        catch( InstantiationException e1 ) {
            e1.printStackTrace();
        }
        catch( IllegalAccessException e1 ) {
            e1.printStackTrace();
        }
    }
}
