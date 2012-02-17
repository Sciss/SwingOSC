package de.sciss.swingosc;

import javax.swing.JComponent;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.LinearGradientPaint;
import java.awt.MultipleGradientPaint;
import java.awt.Paint;
import java.awt.geom.Ellipse2D;

public class NimbusSliderThumb {
    private static final Ellipse2D ellip = new Ellipse2D.Float();

    private static final float[] grad1frac  = new float[] { 0.0f, 1.0f };

    private static Color[] enabledGrad1colr( Color base ) {
        return new Color[] {
            NimbusHelper.adjustColor( base, 5.1498413e-4f, -0.34585923f, -0.007843137f, 0 ),
            NimbusHelper.adjustColor( base, -0.0017285943f, -0.11571431f, -0.25490198f, 0 )
        };
    }

    private static final float[] enabledGrad2frac  = new float[] { 0.0f, 0.42513368f, 0.69786096f, 1.0f };
    private static Color[] enabledGrad2colr( Color base ) {
        return new Color[] {
            NimbusHelper.adjustColor( base, -0.023096085f, -0.6238095f, 0.43921566f, 0 ),
            NimbusHelper.adjustColor( base, 5.1498413e-4f, -0.43866998f, 0.24705881f, 0 ),
            NimbusHelper.adjustColor( base, 5.1498413e-4f, -0.43866998f, 0.24705881f, 0 ),
            NimbusHelper.adjustColor( base, 5.1498413e-4f, -0.45714286f, 0.32941175f, 0 )
        };
    }


    private static Color[] pressedGrad1colr( Color base ) {
        return new Color[] {
            NimbusHelper.adjustColor( base, -0.57865167f, -0.6357143f, -0.54509807f, 0 ),
            NimbusHelper.adjustColor( base, 0.0015952587f, -0.04875779f, -0.18823531f, 0 )
        };
    }

    private static Color[] disabledGrad1colr( Color base ) {
        return new Color[] {
            NimbusHelper.adjustColor( base, 0.021348298f, -0.5625436f, 0.25490195f, 0 ),
            NimbusHelper.adjustColor( base, 0.015098333f, -0.55105823f, 0.19215685f, 0 )
        };
    }

    private static final float[] disabledGrad2frac  = new float[] { 0.0f, 0.42513368f, 1.0f };
    private static Color[] disabledGrad2colr( Color base ) {
        return new Color[] {
            NimbusHelper.adjustColor( base, 0.021348298f, -0.5924243f, 0.35686272f, 0 ),
            NimbusHelper.adjustColor( base, 0.021348298f, -0.56722116f, 0.3098039f, 0 ),
            NimbusHelper.adjustColor( base, 0.021348298f, -0.56844974f, 0.32549018f, 0 )
        };
    }

    private static final float[] pressedGrad2frac  = new float[] { 0.0f, 0.47593582f, 0.5962567f, 1.0f };
    private static Color[] pressedGrad2colr( Color base ) {
        return new Color[] {
            NimbusHelper.adjustColor( base, 2.9569864e-4f, -0.44943976f, 0.25098038f, 0 ),
            NimbusHelper.adjustColor( base, 0.0f, 0.0f, 0.0f, 0 ),
            NimbusHelper.adjustColor( base, 0.0f, 0.0f, 0.0f, 0 ),
            NimbusHelper.adjustColor( base, 8.9377165e-4f, -0.121094406f, 0.12156862f, 0 )
        };
    }

    private Paint createDisabledGradient1( Color base, float shpX1, float shpY1, float shpW, float shpH ) {
        final float startX = shpX1 + 0.510610079575597f * shpW;
        final float startY = shpY1 + -4.553649124439119e-18f * shpH;
        final float endX   = shpX1 + 0.4993368700265251f * shpW;
        final float endY   = shpY1 + 1.0039787798408488f * shpH;
        return new LinearGradientPaint( startX, startY, endX, endY, grad1frac, disabledGrad1colr( base ),
                MultipleGradientPaint.CycleMethod.NO_CYCLE );
    }

    private Paint createDisabledGradient2( Color base, float shpX1, float shpY1, float shpW, float shpH ) {
        final float startX = shpX1 + 0.5023510971786834f * shpW;
        final float startY = shpY1 + 0.001567398119122258f * shpH;
        final float endX   = shpX1 + 0.5023510971786838f * shpW;
        final float endY   = shpY1 + 1.0f * shpH;
        return new LinearGradientPaint( startX, startY, endX, endY, disabledGrad2frac, disabledGrad2colr( base ),
                MultipleGradientPaint.CycleMethod.NO_CYCLE );
    }

    private Paint createEnabledGradient1( Color base, float shpX1, float shpY1, float shpW, float shpH ) {
        final float startX = shpX1 + 0.51f * shpW;
        final float startY = shpY1 + -4.553649124439119e-18f * shpH;
        final float endX   = shpX1 + 0.51f * shpW;
        final float endY   = shpY1 + 1.0039787798408488f * shpH;
        return new LinearGradientPaint( startX, startY, endX, endY, grad1frac, enabledGrad1colr( base ),
                MultipleGradientPaint.CycleMethod.NO_CYCLE );
    }

    private Paint createEnabledGradient2( Color base, float shpX1, float shpY1, float shpW, float shpH ) {
        final float startX = shpX1 + 0.5f * shpW;
        final float startY = shpY1 + 0.0015673981191222587f * shpH;
        final float endX   = shpX1 + 0.5f * shpW;
        final float endY   = shpY1 + 1.0f * shpH;
        return new LinearGradientPaint( startX, startY, endX, endY, enabledGrad2frac, enabledGrad2colr( base ),
                MultipleGradientPaint.CycleMethod.NO_CYCLE );
    }

    private Paint createPressedGradient1( Color base, float shpX1, float shpY1, float shpW, float shpH ) {
        final float startX = shpX1 + 0.510610079575597f * shpW;
        final float startY = shpY1 + -4.553649124439119e-18f * shpH;
        final float endX   = shpX1 + 0.4993368700265251f * shpW;
        final float endY   = shpY1 + 1.0039787798408488f * shpH;
        return new LinearGradientPaint( startX, startY, endX, endY, grad1frac, pressedGrad1colr( base ),
                MultipleGradientPaint.CycleMethod.NO_CYCLE );
    }

    private Paint createPressedGradient2( Color base, float shpX1, float shpY1, float shpW, float shpH ) {
        final float startX = shpX1 + 0.5023510971786834f * shpW;
        final float startY = shpY1 + 0.0015673981191222587f * shpH;
        final float endX   = shpX1 + 0.5023510971786838f * shpW;
        final float endY   = shpY1 + 1.0f * shpH;
        return new LinearGradientPaint( startX, startY, endX, endY, pressedGrad2frac, pressedGrad2colr( base ),
                MultipleGradientPaint.CycleMethod.NO_CYCLE );
    }

    public void paint( int state, Color c, Graphics2D g, int x, int y, int width, int height ) {
        final Color base = NimbusHelper.mixColorWithAlpha( NimbusHelper.getBaseColor(), c );
        if( (state & NimbusHelper.STATE_ENABLED) != 0 ) {
            if( (state & NimbusHelper.STATE_PRESSED) != 0 ) {
                paintPressed( g, base, x, y, width, height );
            } else {
                paintEnabled( g, base, x, y, width, height );
            }
        } else {
            paintDisabled( g, base, x, y, width, height );
        }
    }

    private void paintPressed( Graphics2D g, Color base, int x, int y, int width, int height ) {
        final float e1x = x + 2f;
        final float e1y = y + 3f;
        final float e1w = width - 4f;
        final float e1h = height - 4f;
        ellip.setFrame( e1x, e1y, e1w, e1h );
        final Color c1 = NimbusHelper.adjustColor( NimbusHelper.getBlueGreyColor( base ),
                0f, -0.110526316f, 0.25490195f, -121 );
        g.setColor( c1 );
        g.fill( ellip );

        final float e2x = x + 2f;
        final float e2y = y + 2f;
        final float e2w = width - 4f;
        final float e2h = height - 4f;
        ellip.setFrame( e2x, e2y, e2w, e2h );
        g.setPaint( createPressedGradient1( base, e2x, e2y, e2w, e2h ));
        g.fill( ellip );

        final float e3x = x + 3f;
        final float e3y = y + 3f;
        final float e3w = width - 6f;
        final float e3h = height - 6f;
        ellip.setFrame( e3x, e3y, e3w, e3h );
        g.setPaint( createPressedGradient2( base, e3x, e3y, e3w, e3h ));
        g.fill( ellip );
    }

    private void paintDisabled( Graphics2D g, Color base, int x, int y, int width, int height ) {
        final float e1x = x + 2f;
        final float e1y = y + 2f;
        final float e1w = width - 4f;
        final float e1h = height - 4f;
        ellip.setFrame( e1x, e1y, e1w, e1h );
        g.setPaint( createDisabledGradient1( base, e1x, e1y, e1w, e1h ));
        g.fill( ellip );

        final float e2x = x + 3f;
        final float e2y = y + 3f;
        final float e2w = width - 6f;
        final float e2h = height - 6f;
        ellip.setFrame( e2x, e2y, e2w, e2h );
        g.setPaint( createDisabledGradient2( base, e2x, e2y, e2w, e2h ));
        g.fill( ellip );
    }

    private void paintEnabled( Graphics2D g, Color base, int x, int y, int width, int height ) {
        final float e1x = x + 2f;
        final float e1y = y + 3f;
        final float e1w = width - 4f;
        final float e1h = height - 4f;
        ellip.setFrame( e1x, e1y, e1w, e1h );
        final Color c1 = NimbusHelper.adjustColor( NimbusHelper.getBlueGreyColor( base ),
                -0.003968239f, 0.0014736876f, -0.25490198f, -156 );
        g.setColor( c1 );
        g.fill( ellip );

        final float e2x = x + 2f;
        final float e2y = y + 2f;
        final float e2w = width - 4f;
        final float e2h = height - 4f;
        ellip.setFrame( e2x, e2y, e2w, e2h );
        g.setPaint( createEnabledGradient1( base, e2x, e2y, e2w, e2h ));
        g.fill( ellip );

        final float e3x = x + 3f;
        final float e3y = y + 3f;
        final float e3w = width - 6f;
        final float e3h = height - 6f;
        ellip.setFrame( e3x, e3y, e3w, e3h );
        g.setPaint( createEnabledGradient2( base, e3x, e3y, e3w, e3h ));
        g.fill( ellip );
    }
}
