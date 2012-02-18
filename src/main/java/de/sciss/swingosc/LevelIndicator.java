/*
 *  LevelIndicator.java
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

import javax.swing.JComponent;
import javax.swing.SwingConstants;
import java.awt.Color;
import java.awt.Graphics;

public class LevelIndicator extends JComponent {
    private float level = 0f;
    private float warn = 0f;
    private float critical = 0f;
    private float peak = 0f;
    private int cidx = 0;
    private boolean horiz = true;
    private boolean peakVisible = false;
    private int minorTicks = 0;
    private int majorTicks = 0;

    private static Color[] colrs = new Color[] {
        Color.green, Color.yellow, Color.red
    };

    private static Color colrMinorTicks = Color.black; // darkGray;
    private static Color colrMajorTicks = Color.black; // darkGray;
    private static Color colrPeak       = Color.blue;

    public LevelIndicator( int orient ) {
        horiz = orient == SwingConstants.HORIZONTAL;
        if( !horiz && orient != SwingConstants.VERTICAL ) throw new IllegalArgumentException( "orientation " + orient );
    }

    public void setOrientation( int orient ) {
        final boolean old = horiz;
        horiz = orient == SwingConstants.HORIZONTAL;
        if( !horiz && orient != SwingConstants.VERTICAL ) throw new IllegalArgumentException( "orientation " + orient );
        if( old != horiz ) repaint();
    }

    public int getOrientation() { return horiz ? SwingConstants.HORIZONTAL : SwingConstants.VERTICAL; }

    public void setLevel( float value ) {
        if( level != value ) {
            level = value;
            adjustColor();
            repaint();
        }
    }

    public float getLevel() { return level; }

    public void setPeak( float value ) {
        if( peak != value ) {
            peak = value;
            if( peakVisible ) {
                adjustColor();
                repaint();
            }
        }
    }

    public float getPeak() { return peak; }

    public void setPeakVisible( boolean b ) {
        if( peakVisible != b ) {
            peakVisible = b;
            adjustColor();
            repaint();
        }
    }

    public boolean getPeakVisible() { return peakVisible; }

    public void setWarning( float value ) {
        if( warn != value ) {
            warn = value;
            checkColor();
        }
    }

    public float getWarning() { return warn; }

    public void setCritical( float value ) {
        if( critical != value ) {
            critical = value;
            checkColor();
        }
    }

    public float getCritical() { return critical; }

    public void setMinorTicks( int num ) {
        if( minorTicks != num ) {
            minorTicks = num;
            repaint();
        }
    }

    public int getMinorTicks() { return minorTicks; }

    public void setMajorTicks( int num ) {
        if( majorTicks != num ) {
            majorTicks = num;
            repaint();
        }
    }

    public int getMajorTicks() { return majorTicks; }

    private void checkColor() {
        final int cold = cidx;
        adjustColor();
        if( cold != cidx ) repaint();
    }

    private void adjustColor() {
        final float ref = peakVisible ? peak : level;
        cidx = critical <= warn ? (ref <= warn ? 1 : 0) :
                        (ref < warn ? 0 : ((ref < critical) ? 1 : 2));
    }

    private void paintTicks( Graphics g, Color colr, int x, int y, int h, int w, int num, int tickExt, int ext, int thick ) {
        if( num <= 0 ) return;

        g.setColor( colr );
        if( horiz ) {
            final float scale = (float) (w - thick) / Math.max( 1, num - 1 );
            final int yp = h - tickExt;
            for( int i = 0; i < num; i++ ) {
                final int xp = (int) (i * scale + 0.5f) + x;
                g.fillRect( xp, yp, thick, ext );
            }
        } else {
            final float scale = (float) (h - thick) / Math.max( 1, num - 1 );
            final int xp = w - tickExt;
            for( int i = 0; i < num; i++ ) {
                final int yp = (h - thick - (int) (i * scale + 0.5f)) + y;
//                g.drawLine( xp, yp, xp + extM1, yp );
                g.fillRect( xp, yp, ext, thick );
            }
        }
    }

    public void paintComponent( Graphics g ) {
        super.paintComponent( g );

        final int x = 0;
        final int y = 0;
        final int w = getWidth();
        final int h = getHeight();
        final int tickExt = majorTicks > 0 ? 7 : (minorTicks > 0 ? 4 : 0);
        g.setColor( Color.lightGray );
        if( horiz ) {
            g.fillRect( x, y, w, h - tickExt );
        } else {
            g.fillRect( x, y, w - tickExt, h );
        }

        paintTicks( g, colrMinorTicks, x, y, h, w, minorTicks, tickExt, 4, 1 );
        paintTicks( g, colrMajorTicks, x, y, h, w, majorTicks, tickExt, 7, 3 );

        if( level > 0f ) {
            g.setColor( colrs[ cidx ]);
            if( horiz ) {
                final int ext = (int) (level * w + 0.5f);
                g.fillRect( x, y, ext, h - tickExt );

            } else {
                final int ext = (int) (level * h + 0.5f);
                g.fillRect( x, y + h - ext, w - tickExt, ext );
            }
        }

        if( peakVisible ) {
            g.setColor( colrPeak );
            if( horiz ) {
                final int pos = (int) (peak * (w - 3) + 0.5f);
                g.fillRect( x + pos, y, 3, h - tickExt );

            } else {
                final int pos = (int) (peak * (h - 3) + 0.5f);
                g.fillRect( x, y + h - pos - 3, w - tickExt, 3 );
            }
        }
    }
}
