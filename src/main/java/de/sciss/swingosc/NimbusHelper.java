/*
 *  NimbusHelper.java
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

import javax.swing.LookAndFeel;
import javax.swing.UIDefaults;
import javax.swing.UIManager;
import java.awt.Color;
import java.awt.Transparency;

public class NimbusHelper {
    public static final int STATE_ENABLED   = 0x01;
    public static final int STATE_OVER      = 0x02;
    public static final int STATE_FOCUSED   = 0x04;
    public static final int STATE_PRESSED   = 0x08;

//    private static LookAndFeel nimbusLAF;
    private static UIDefaults nimbusDefaults;
    private static final Color defaultFocusColor        = new Color( 115, 164, 209, 255 );
    private static final Color defaultBaseColor         = new Color(  51,  98, 140, 255 );
    private static final Color defaultTextColor         = Color.black;
    private static final Color defaultSelectedTextColor = Color.white;
    private static final Color defaultControlHighlightColor  = new Color( 233, 236, 242, 255 );
    private static final Color defaultSelectionBackgroundColor = new Color( 57, 105, 138, 255 );
    private static final float[] hsbArr                 = new float[ 3 ];

    static {
//        try {
            final LookAndFeel current = UIManager.getLookAndFeel();
            if( current.getName().toLowerCase().equals( "nimbus" )) {
//                nimbusLAF       = current;
                nimbusDefaults = current.getDefaults();
//            } else {
//                final UIManager.LookAndFeelInfo[] infos = UIManager.getInstalledLookAndFeels();
//                for( int i = 0; i < infos.length; i++ ) {
//                    if( infos[ i ].getName().toLowerCase().equals( "nimbus" )) {
//                        final Class clz         = Class.forName( infos[ i ].getClassName(), true, Thread.currentThread().getContextClassLoader() );
//                        nimbusLAF               = (LookAndFeel) clz.newInstance();
//                        nimbusDefaults          = nimbusLAF.getDefaults();
//                        break;
//                    }
//                }
            }
//        }
//        catch( ClassNotFoundException e1 ) { /* ignore */ }
//        catch( InstantiationException e1 ) { /* ignore */ }
//        catch( IllegalAccessException e1 ) { /* ignore */ }
    }

    public static Color getFocusColor() {
        final Color c = nimbusDefaults == null ? null : nimbusDefaults.getColor( "nimbusFocus" );
        return c == null ? defaultFocusColor : c;
    }

    public static Color getBaseColor() {
        final Color c = nimbusDefaults == null ? null : nimbusDefaults.getColor( "nimbusBase" );
        return c == null ? defaultBaseColor : c;
    }

    public static Color getTextColor() {
        final Color c = nimbusDefaults == null ? null : nimbusDefaults.getColor( "text" );
        return c == null ? defaultTextColor : c;
    }

    public static Color getSelectedTextColor() {
        final Color c = nimbusDefaults == null ? null : nimbusDefaults.getColor( "selectedText" );
        return c == null ? defaultSelectedTextColor : c;
    }

    public static Color getControlHighlighColor() {
        final Color c = nimbusDefaults == null ? null : nimbusDefaults.getColor( "controlHighlight" );
        return c == null ? defaultControlHighlightColor : c;
    }

    public static Color getSelectionBackgroundColor() {
        final Color c = nimbusDefaults == null ? null : nimbusDefaults.getColor( "nimbusSelectionBackground" );
        return c == null ? defaultSelectionBackgroundColor : c;
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
            // don't know what's going on here. nimbus defaults ColorUIResources have alpha values of zero sometimes
            final int cAlpha = /* c.getTransparency() == Transparency.TRANSLUCENT ? */ c.getAlpha() /* : 0xFF */;
            return new Color( c.getRed(), c.getGreen(), c.getBlue(), Math.max( 0, Math.min( 0xFF, cAlpha + alphaOffset )));
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
        final int cAlpha = /* c.getTransparency() == Transparency.TRANSLUCENT ? */ c.getAlpha() /* : 0xFF */;
        final int a = sameAlpha ? cAlpha : Math.max( 0, Math.min( 0xFF, cAlpha + alphaOffset ));
        final int rgba = (rgb & 0xFFFFFF) | (a << 24);
        return new Color( rgba, true );
    }

    public static Color mixColorWithAlpha( Color base, Color mix ) {
        if( mix == null ) return base;
        final int a0 = mix.getAlpha();
        if( a0 == 0 ) { return base; } else if( a0 == 0xFF ) return mix;

        final float wm = (float) a0 / 0xFF;
        final float wb = 1f - wm;
        final int r = (int) (base.getRed()   * wb + mix.getRed()   * wm + 0.5f);
        final int g = (int) (base.getGreen() * wb + mix.getGreen() * wm + 0.5f);
        final int b = (int) (base.getBlue()  * wb + mix.getBlue()  * wm + 0.5f);
        return new Color( r, g, b );
    }
}