package de.sciss.swingosc;

import javax.swing.LookAndFeel;
import javax.swing.UIDefaults;
import javax.swing.UIManager;
import java.awt.Color;

public class NimbusHelper {
    private static UIDefaults nimbusDefaults;
    private static final Color defaultFocusColor    = new Color( 115, 164, 209, 255 );
    private static final Color defaultBaseColor     = new Color(  51,  98, 140, 255 );
    private static final float[] hsbArr             = new float[ 3 ];

    static {
        try {
            final LookAndFeel current = UIManager.getLookAndFeel();
            if( current.getName().toLowerCase().equals( "nimbus" )) {
                nimbusDefaults = current.getDefaults();
            } else {
                final UIManager.LookAndFeelInfo[] infos = UIManager.getInstalledLookAndFeels();
                for( int i = 0; i < infos.length; i++ ) {
                    if( infos[ i ].getName().toLowerCase().equals( "nimbus" )) {
                        final Class clz         = Class.forName( infos[ i ].getClassName(), true, Thread.currentThread().getContextClassLoader() );
                        final LookAndFeel laf   = (LookAndFeel) clz.newInstance();
                        nimbusDefaults          = laf.getDefaults();
                        break;
                    }
                }
            }
        }
        catch( ClassNotFoundException e1 ) { /* ignore */ }
        catch( InstantiationException e1 ) { /* ignore */ }
        catch( IllegalAccessException e1 ) { /* ignore */ }
    }

    public static Color getFocusColor() {
        final Color c = nimbusDefaults == null ? null : nimbusDefaults.getColor( "nimbusFocus" );
        return c == null ? defaultFocusColor : c;
    }

    public static Color getBaseColor() {
        final Color c = nimbusDefaults == null ? null : nimbusDefaults.getColor( "nimbusBase" );
        return c == null ? defaultBaseColor : c;
    }

    public static Color getBlueGreyColor( Color base ) {
        final Color c = nimbusDefaults == null ? null : nimbusDefaults.getColor( "nimbusBlueGrey" );
        return c == null ? defaultBlueGreyColor( base ) : c;
    }

    private static Color defaultBlueGreyColor( Color base ) {
        return adjustColor( base, 0.032459438f, -0.52518797f, 0.19607842f, 0 );
    }

    public static Color adjustColor( Color c, float hueOffset, float satOffset, float briOffset, int alphaOffset ) {
        final boolean sameColor = hueOffset == 0f && satOffset == 0f && briOffset == 0f;
        final boolean sameAlpha = alphaOffset == 0;
        if( sameColor ) {
            if( sameAlpha ) return c;
            else return new Color( c.getRed(), c.getGreen(), c.getBlue(), Math.max( 0, Math.min( 0xFF, c.getAlpha() + alphaOffset )));
        }

        Color.RGBtoHSB( c.getRed(), c.getGreen(), c.getBlue(), hsbArr );
        final float hue = hsbArr[ 0 ] + hueOffset;
        final float sat = Math.max( 0f, Math.min( 1f, hsbArr[ 1 ] + satOffset ));
        final float bri = Math.max( 0f, Math.min( 1f, hsbArr[ 2 ] + briOffset ));
        final int rgb = Color.HSBtoRGB( hue, sat, bri );
        // (Bits 24-31 are alpha, 16-23 are red, 8-15 are green, 0-7 are blue).
//        final int r = (rgb >> 16) & 0xFF;
//        final int g = (rgb >> 8) & 0xFF;
//        final int b = rgb & 0xFF;
        final int a0   = c.getAlpha();
        final int a1   = sameAlpha ? a0 : Math.max( 0, Math.min( 0xFF, a0 + alphaOffset ));
        final int rgba = (rgb & 0xFFFFFF) | (a1 << 24);
        return new Color( rgba, true );
    }
}
