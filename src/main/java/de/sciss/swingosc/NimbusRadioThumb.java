/*
 *  NimbusRadioThumb.java
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

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.LinearGradientPaint;
import java.awt.MultipleGradientPaint;
import java.awt.Paint;
import java.awt.geom.Ellipse2D;

/**
 * A painter which is imitating the nimbus appearance of radio button knobs.
 * The colors have been slightly tuned so they look good with colored knobs.
 */
public class NimbusRadioThumb {
    private static final Ellipse2D ellip = new Ellipse2D.Float();

    private static final float[] grad1frac  = new float[] { 0.0f, 1.0f };

    private static final float[] enabledGrad2frac  = new float[] { 0.06344411f, 0.43674698f, 0.52409637f, 0.88554215f };

    private static final float[] grad2frac = new float[] { 0.06344411f, 0.36858007f, 0.72809666f, 0.82175225f, 1.0f };

    private static final float[] pressedGrad2frac = new float[] { 0.06344411f, 0.35240963f, 0.5481928f, 0.9487952f };

    private static Color[] enabledGrad1colr( Color blueGrey ) {
        return new Color[] {
            NimbusHelper.adjustColor( blueGrey, 0.0f, -0.053201474f, -0.12941176f, 0 ),
            NimbusHelper.adjustColor( blueGrey, 0.0f, 0.006356798f, -0.44313726f, 0 )
        };
    }

    private static Color[] enabledGrad2colr( Color blueGrey ) {
        return new Color[] {
            NimbusHelper.adjustColor( blueGrey, 0.055555582f, -0.10654225f, 0.23921567f, 0 ),
            NimbusHelper.adjustColor( blueGrey, 0.0f, -0.07016757f, 0.12941176f, 0 ),
            NimbusHelper.adjustColor( blueGrey, 0.0f, -0.07016757f, 0.12941176f, 0 ),
            NimbusHelper.adjustColor( blueGrey, 0.0f, -0.07206477f, 0.17254901f, 0 )
        };
    }

    private static Color[] disabledGrad1colr( Color blueGrey ) {
        return new Color[] {
            NimbusHelper.adjustColor( blueGrey, 0.0f, -0.06766917f, 0.07843137f, 0 ),
            NimbusHelper.adjustColor( blueGrey, 0.0f, -0.06413457f, 0.015686274f, 0 )
        };
    }

    private static Color[] disabledGrad2colr( Color blueGrey ) {
        return new Color[] {
            NimbusHelper.adjustColor( blueGrey, 0.0f, -0.08466425f, 0.16470587f, 0 ),
            NimbusHelper.adjustColor( blueGrey, 0.0f, -0.07016757f, 0.12941176f, 0 ),
            NimbusHelper.adjustColor( blueGrey, 0.0f, -0.07016757f, 0.12941176f, 0 ),
            NimbusHelper.adjustColor( blueGrey, 0.0f, -0.070703305f, 0.14117646f, 0 ),
            NimbusHelper.adjustColor( blueGrey, 0.0f, -0.07052632f, 0.1372549f, 0 )
        };
    }

    private static Color[] overGrad1colr( Color blueGrey ) {
        return new Color[] {
            NimbusHelper.adjustColor( blueGrey, -0.00505054f, -0.027819552f, -0.2235294f, 0 ),
            NimbusHelper.adjustColor( blueGrey, 0.0f, 0.24241486f, -0.6117647f, 0 )
        };
    }

    // This is a more reasonable setting for colored knobs!
    private static Color[] overGrad2colr( Color blueGrey ) {
        return new Color[] {
//                NimbusHelper.adjustColor( blueGrey, -0.111111104f, -0.10655806f, 0.24313724f, 0 ),
            NimbusHelper.adjustColor( blueGrey, 0.055555582f, -0.10655806f, 0.24313724f, 0 ),
            NimbusHelper.adjustColor( blueGrey, 0.0f, -0.17333623f, 0.20392156f, 0 ),
//                NimbusHelper.adjustColor( blueGrey, 0.0f, -0.07333623f, 0.20392156f, 0 ),
            NimbusHelper.adjustColor( blueGrey, 0.0f, -0.167389056f, 0.20392156f, 0 ),
//                NimbusHelper.adjustColor( blueGrey, 0.08585858f -0.067389056f, 0.25490195f, 0 ),
//                NimbusHelper.adjustColor( blueGrey, 0.03f, -0.067389056f, 0.24313724f, 0 ),
            NimbusHelper.adjustColor( blueGrey, 0.015f, -0.17333623f, 0.20392156f, 0 ),
//                NimbusHelper.adjustColor( blueGrey, -0.111111104f, -0.10628903f, 0.18039215f, 0 )
            NimbusHelper.adjustColor( blueGrey, 0.03f, -0.16628903f, 0.24313724f, 0 )
        };
    }

    private static Color[] pressedGrad1colr( Color blueGrey ) {
        return new Color[] {
            NimbusHelper.adjustColor( blueGrey, 0.055555582f, 0.23947367f, -0.6666667f, 0 ),
            NimbusHelper.adjustColor( blueGrey, -0.0777778f, -0.06815343f, -0.28235295f, 0 )
        };
    }

    private static Color[] pressedGrad2colr( Color blueGrey ) {
        return new Color[] {
            NimbusHelper.adjustColor( blueGrey, 0.0f, -0.06866585f, 0.09803921f, 0 ),
            NimbusHelper.adjustColor( blueGrey, -0.0027777553f, -0.0018306673f, -0.02352941f, 0 ),
            NimbusHelper.adjustColor( blueGrey, -0.0027777553f, -0.0018306673f, -0.02352941f, 0 ),
            NimbusHelper.adjustColor( blueGrey, 0.002924025f, -0.02047892f, 0.082352936f, 0 )
        };
    }

    private Paint createEnabledGradient1( Color blueGrey, float shpX1, float shpY1, float shpW, float shpH ) {
        final float startX = shpX1 + 0.49789915966386533f * shpW;
        final float startY = shpY1 + -0.0042016806722689065f * shpH;
        final float endX   = shpX1 + 0.5f * shpW;
        final float endY   = shpY1 + 0.9978991596638656f * shpH;
        return new LinearGradientPaint( startX, startY, endX, endY, grad1frac, enabledGrad1colr( blueGrey ),
                MultipleGradientPaint.CycleMethod.NO_CYCLE );
    }

    private Paint createEnabledGradient2( Color blueGrey, float shpX1, float shpY1, float shpW, float shpH ) {
        final float startX = shpX1 + 0.49754901960784303f * shpW;
        final float startY = shpY1 + 0.004901960784313727f * shpH;
        final float endX   = shpX1 + 0.507352941176471f * shpW;
        final float endY   = shpY1 + 1.0f * shpH;
        return new LinearGradientPaint( startX, startY, endX, endY, enabledGrad2frac, enabledGrad2colr( blueGrey ),
                MultipleGradientPaint.CycleMethod.NO_CYCLE );
    }

    private Paint createDisabledGradient1( Color blueGrey, float shpX1, float shpY1, float shpW, float shpH ) {
        final float startX = shpX1 + 0.49789915966386533f * shpW;
        final float startY = shpY1 + -0.0042016806722689065f * shpH;
        final float endX   = shpX1 + 0.5f * shpW;
        final float endY   = shpY1 + 0.9978991596638656f * shpH;
        return new LinearGradientPaint( startX, startY, endX, endY, grad1frac, disabledGrad1colr( blueGrey ),
                MultipleGradientPaint.CycleMethod.NO_CYCLE );
    }

    private Paint createDisabledGradient2( Color blueGrey, float shpX1, float shpY1, float shpW, float shpH ) {
        final float startX = shpX1 + 0.49754901960784303f * shpW;
        final float startY = shpY1 + 0.004901960784313727f * shpH;
        final float endX   = shpX1 + 0.507352941176471f * shpW;
        final float endY   = shpY1 + 1.0f * shpH;
        return new LinearGradientPaint( startX, startY, endX, endY, grad2frac, disabledGrad2colr( blueGrey ),
                MultipleGradientPaint.CycleMethod.NO_CYCLE );
    }

    private Paint createOverGradient1( Color blueGrey, float shpX1, float shpY1, float shpW, float shpH ) {
        final float startX = shpX1 + 0.49789915966386533f * shpW;
        final float startY = shpY1 + -0.0042016806722689065f * shpH;
        final float endX   = shpX1 + 0.5f * shpW;
        final float endY   = shpY1 + 0.9978991596638656f * shpH;
        return new LinearGradientPaint( startX, startY, endX, endY, grad1frac, overGrad1colr( blueGrey ),
                MultipleGradientPaint.CycleMethod.NO_CYCLE );
    }

    private Paint createOverGradient2( Color blueGrey, float shpX1, float shpY1, float shpW, float shpH ) {
        final float startX = shpX1 + 0.49754901960784303f * shpW;
        final float startY = shpY1 + 0.004901960784313727f * shpH;
        final float endX   = shpX1 + 0.507352941176471f * shpW;
        final float endY   = shpY1 + 1.0f * shpH;
        return new LinearGradientPaint( startX, startY, endX, endY, grad2frac, overGrad2colr( blueGrey ),
                MultipleGradientPaint.CycleMethod.NO_CYCLE );
    }

    private Paint createPressedGradient1( Color blueGrey, float shpX1, float shpY1, float shpW, float shpH ) {
        final float startX = shpX1 + 0.49789915966386533f * shpW;
        final float startY = shpY1 + -0.0042016806722689065f * shpH;
        final float endX   = shpX1 + 0.5f * shpW;
        final float endY   = shpY1 + 0.9978991596638656f * shpH;
        return new LinearGradientPaint( startX, startY, endX, endY, grad1frac, pressedGrad1colr( blueGrey ),
                MultipleGradientPaint.CycleMethod.NO_CYCLE );
    }

    private Paint createPressedGradient2( Color blueGrey, float shpX1, float shpY1, float shpW, float shpH ) {
        final float startX = shpX1 + 0.49754901960784303f * shpW;
        final float startY = shpY1 + 0.004901960784313727f * shpH;
        final float endX   = shpX1 + 0.507352941176471f * shpW;
        final float endY   = shpY1 + 1.0f * shpH;
        return new LinearGradientPaint( startX, startY, endX, endY, pressedGrad2frac, pressedGrad2colr( blueGrey ),
                MultipleGradientPaint.CycleMethod.NO_CYCLE );
    }

    public void paint( int state, Color c, Graphics2D g, int x, int y, int width, int height ) {
        final Color nimBase     = NimbusHelper.getBaseColor();
//        final Color blueGrey    = NimbusHelper.mixColorWithAlpha( NimbusHelper.getBlueGreyColor( nimBase ), c );
        if( (state & NimbusHelper.STATE_ENABLED) != 0 ) {
            final Color blueGrey    = NimbusHelper.mixColorWithAlpha( NimbusHelper.getBlueGreyColor( nimBase ), c );
            final boolean focused = (state & NimbusHelper.STATE_FOCUSED) != 0;
            if( (state & NimbusHelper.STATE_PRESSED) != 0 ) {
                if( focused ) {
                    paintFocusedPressed( g, blueGrey, x, y, width, height );
                } else {
                    paintPressed( g, blueGrey, x, y, width, height );
                }
            } else if( (state & NimbusHelper.STATE_OVER) != 0 ) {
                if( focused ) {
                    paintFocusedOver( g, blueGrey, x, y, width, height );
                } else {
                    paintOver( g, blueGrey, x, y, width, height );
                }
            } else {
                if( focused ) {
                    paintFocused( g, blueGrey, x, y, width, height );
                } else {
                    paintEnabled( g, blueGrey, x, y, width, height );
                }
            }
        } else {
            final Color c2          = c == null ? c : NimbusHelper.adjustColor( c, 0f, 0f, 0f, -112 ); // new Color( c.getRed(), c.getGreen(), c.getBlue(), c.getAlpha() >> 1 );
            final Color blueGrey    = NimbusHelper.mixColorWithAlpha( NimbusHelper.getBlueGreyColor( nimBase ), c2 );
//System.out.println( "c2.alpha = " + (c2 == null ? 0xFF : c2.getAlpha()) );
            paintDisabled( g, blueGrey, x, y, width, height );
        }
    }

    private void paintFocusedPressed( Graphics2D g, Color blueGrey, int x, int y, int width, int height ) {
        paintFocusedBack( g,           x, y, width, height );
        paintPressedTop(  g, blueGrey, x, y, width, height );
    }

    private void paintPressed( Graphics2D g, Color blueGrey, int x, int y, int width, int height ) {
        paintPressedBack( g, blueGrey, x, y, width, height );
        paintPressedTop(  g, blueGrey, x, y, width, height );
    }

    private void paintPressedBack( Graphics2D g, Color blueGrey, int x, int y, int width, int height ) {
        final float e1x = x + 2f;
        final float e1y = y + 3f;
        final float e1w = width - 4f;
        final float e1h = height - 4f;
        ellip.setFrame( e1x, e1y, e1w, e1h );
        g.setColor( NimbusHelper.adjustColor( blueGrey, 0.0f, -0.110526316f, 0.25490195f, 0 ));
        g.fill( ellip );
    }

    private void paintPressedTop( Graphics2D g, Color blueGrey, int x, int y, int width, int height ) {
        final float e2x = x + 2f;
        final float e2y = y + 2f;
        final float e2w = width - 4f;
        final float e2h = height - 4f;
        ellip.setFrame( e2x, e2y, e2w, e2h );
        g.setPaint( createPressedGradient1( blueGrey, e2x, e2y, e2w, e2h ));
        g.fill( ellip );

        final float e3x = x + 3f;
        final float e3y = y + 3f;
        final float e3w = width - 6f;
        final float e3h = height - 6f;
        ellip.setFrame( e3x, e3y, e3w, e3h );
        g.setPaint( createPressedGradient2( blueGrey, e3x, e3y, e3w, e3h ));
        g.fill( ellip );
    }

    private void paintFocusedOver( Graphics2D g, Color blueGrey, int x, int y, int width, int height ) {
        paintFocusedBack( g,           x, y, width, height );
        paintOverTop(     g, blueGrey, x, y, width, height );
    }

    private void paintOver( Graphics2D g, Color blueGrey, int x, int y, int width, int height ) {
        paintEnabledBack( g, blueGrey, x, y, width, height );
        paintOverTop(     g, blueGrey, x, y, width, height );
    }

    private void paintOverTop( Graphics2D g, Color blueGrey, int x, int y, int width, int height ) {
        final float e2x = x + 2f;
        final float e2y = y + 2f;
        final float e2w = width - 4f;
        final float e2h = height - 4f;
        ellip.setFrame( e2x, e2y, e2w, e2h );
        g.setPaint( createOverGradient1( blueGrey, e2x, e2y, e2w, e2h ));
        g.fill( ellip );

        final float e3x = x + 3f;
        final float e3y = y + 3f;
        final float e3w = width - 6f;
        final float e3h = height - 6f;
        ellip.setFrame( e3x, e3y, e3w, e3h );
        g.setPaint( createOverGradient2( blueGrey, e3x, e3y, e3w, e3h ));
        g.fill( ellip );
    }

    private void paintFocused( Graphics2D g, Color blueGrey, int x, int y, int width, int height ) {
        paintFocusedBack( g,           x, y, width, height );
        paintEnabledTop(  g, blueGrey, x, y, width, height );
    }

    private void paintFocusedBack( Graphics2D g, int x, int y, int width, int height ) {
        final float e1x = x + 0.6f;
        final float e1y = y + 0.6f;
        final float e1w = width - 1.2f;
        final float e1h = height - 1.2f;
        ellip.setFrame( e1x, e1y, e1w, e1h);
        g.setColor( NimbusHelper.getFocusColor() );
        g.fill( ellip );
    }

    private void paintEnabled( Graphics2D g, Color blueGrey, int x, int y, int width, int height ) {
        paintEnabledBack( g, blueGrey, x, y, width, height );
        paintEnabledTop(  g, blueGrey, x, y, width, height );
    }

    private void paintEnabledBack( Graphics2D g, Color blueGrey, int x, int y, int width, int height ) {
        final float e1x = x + 2f;
        final float e1y = y + 3f;
        final float e1w = width - 4f;
        final float e1h = height - 4f;
        ellip.setFrame( e1x, e1y, e1w, e1h );
        g.setColor( NimbusHelper.adjustColor( blueGrey, 0.0f, 0.0f, 0.0f, -112 ));
        g.fill( ellip );
    }

    private void paintEnabledTop( Graphics2D g, Color blueGrey, int x, int y, int width, int height ) {
        final float e2x = x + 2f;
        final float e2y = y + 2f;
        final float e2w = width - 4f;
        final float e2h = height - 4f;
        ellip.setFrame( e2x, e2y, e2w, e2h );
        g.setPaint( createEnabledGradient1( blueGrey, e2x, e2y, e2w, e2h ));
        g.fill( ellip );

        final float e3x = x + 3f;
        final float e3y = y + 3f;
        final float e3w = width - 6f;
        final float e3h = height - 6f;
        ellip.setFrame( e3x, e3y, e3w, e3h );
        g.setPaint( createEnabledGradient2( blueGrey, e3x, e3y, e3w, e3h ));
        g.fill( ellip );
    }

    private void paintDisabled( Graphics2D g, Color blueGrey, int x, int y, int width, int height ) {
        final float e2x = x + 2f;
        final float e2y = y + 2f;
        final float e2w = width - 4f;
        final float e2h = height - 4f;
        ellip.setFrame( e2x, e2y, e2w, e2h );
        g.setPaint( createDisabledGradient1( blueGrey, e2x, e2y, e2w, e2h ));
        g.fill( ellip );

        final float e3x = x + 3f;
        final float e3y = y + 3f;
        final float e3w = width - 6f;
        final float e3h = height - 6f;
        ellip.setFrame( e3x, e3y, e3w, e3h );
        g.setPaint( createDisabledGradient2( blueGrey, e3x, e3y, e3w, e3h ));
        g.fill( ellip );
    }
}