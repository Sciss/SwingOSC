package de.sciss.swingosc;

import java.awt.Color;
import java.awt.Graphics2D;

public class NimbusRadioThumb {
    public void paint( int state, Color c, Graphics2D g, int x, int y, int width, int height ) {
        final Color base = NimbusHelper.mixColorWithAlpha( NimbusHelper.getBaseColor(), c );
        if( (state & NimbusHelper.STATE_ENABLED) != 0 ) {
            final boolean focused = (state & NimbusHelper.STATE_FOCUSED) != 0;
            if( (state & NimbusHelper.STATE_PRESSED) != 0 ) {
                if( focused ) {
                    paintFocusedPressed( g, base, x, y, width, height );
                } else {
                    paintPressed( g, base, x, y, width, height );
                }
            } else if( (state & NimbusHelper.STATE_OVER) != 0 ) {
                if( focused ) {
                    paintFocusedOver( g, base, x, y, width, height );
                } else {
                    paintOver( g, base, x, y, width, height );
                }
            } else {
                if( focused ) {
                    paintFocused( g, base, x, y, width, height );
                } else {
                    paintEnabled( g, base, x, y, width, height );
                }
            }
        } else {
            paintDisabled( g, base, x, y, width, height );
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

    private void paintEnabled( Graphics2D g, Color base, int x, int y, int width, int height ) {
        g.setColor( Color.black );
        g.fillOval( x, y, width, height );
    }

    private void paintDisabled( Graphics2D g, Color base, int x, int y, int width, int height ) {

    }
}