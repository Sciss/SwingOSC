package de.sciss.swingosc;

import javax.swing.border.Border;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;

public class NimbusFocusBorder implements Border {
    public void paintBorder( Component c, Graphics g, int x, int y, int width, int height ) {
        final Graphics2D g2 = (Graphics2D) g;
        if( c.hasFocus() )       { paintBorderFocused(  c, g2, x, y, width, height );}
        else if( c.isEnabled() ) { paintBorderEnabled(  c, g2, x, y, width, height );}
        else                     { paintBorderDisabled( c, g2, x, y, width, height );}
    }

    public Insets getBorderInsets( Component c ) {
        return new Insets( 2, 2, 2, 2 );
    }

    public boolean isBorderOpaque() {
        return false;
    }

    private void paintBorderFocused( Component c, Graphics2D g, int x, int y, int width, int height ) {

    }

    private void paintBorderEnabled( Component c, Graphics2D g, int x, int y, int width, int height ) {

    }

    private void paintBorderDisabled( Component c, Graphics2D g, int x, int y, int width, int height ) {

    }
}