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

public class NimbusRadioThumb {
    private static final Ellipse2D ellip = new Ellipse2D.Float();

    private static final float[] grad1frac  = new float[] { 0.0f, 1.0f };

    private static final float[] enabledGrad2frac  = new float[] { 0.06344411f, 0.43674698f, 0.52409637f, 0.88554215f };

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

    public void paint( int state, Color c, Graphics2D g, int x, int y, int width, int height ) {
        final Color nimBase     = NimbusHelper.getBaseColor();
        final Color blueGrey    = NimbusHelper.mixColorWithAlpha( NimbusHelper.getBlueGreyColor( nimBase ), c );
        if( (state & NimbusHelper.STATE_ENABLED) != 0 ) {
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
            paintDisabled( g, blueGrey, x, y, width, height );
        }
    }

    private void paintFocusedPressed( Graphics2D g, Color base, int x, int y, int width, int height ) {

    }

    private void paintPressed( Graphics2D g, Color base, int x, int y, int width, int height ) {

    }

    private void paintFocusedOver( Graphics2D g, Color base, int x, int y, int width, int height ) {

    }

    private void paintOver( Graphics2D g, Color base, int x, int y, int width, int height ) {

    }

    private void paintFocused( Graphics2D g, Color base, int x, int y, int width, int height ) {

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

    private void paintEnabled( Graphics2D g, Color blueGrey, int x, int y, int width, int height ) {
 //        System.out.println( "enabled " + x + ", " + y + ", " + width + ", " + height );

        g.setColor( Color.blue );
        g.fillRect( x, y, width, height );

        // ---- shadow layer ----

        final float e1x = x + 2f;
        final float e1y = y + 3f;
        final float e1w = width - 4f;
        final float e1h = height - 4f;
        ellip.setFrame( e1x, e1y, e1w, e1h );
        g.setColor( NimbusHelper.adjustColor( blueGrey, 0.0f, 0.0f, 0.0f, -112 ));
        g.fill( ellip );

        // ---- button layer ----

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

    private void paintDisabled( Graphics2D g, Color base, int x, int y, int width, int height ) {

    }
}