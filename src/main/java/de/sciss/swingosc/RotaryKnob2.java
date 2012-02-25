/*
 *  RotaryKnob2.java
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

import javax.swing.BoundedRangeModel;
import javax.swing.InputMap;
import javax.swing.KeyStroke;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.AWTEventMulticaster;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

public class RotaryKnob2 extends RotaryKnob implements ChangeListener {
    private	ActionListener al = null;

    public RotaryKnob2( int min, int max, int value ) {
        super( min, max, value );
        init();
    }

    private void init() {
   		addChangeListener( this );
   		final InputMap imap = getInputMap();
   		imap.put( KeyStroke.getKeyStroke( KeyEvent.VK_LEFT,  0 ), "none" );
   		imap.put( KeyStroke.getKeyStroke( KeyEvent.VK_UP,    0 ), "none" );
   		imap.put( KeyStroke.getKeyStroke( KeyEvent.VK_RIGHT, 0 ), "none" );
   		imap.put( KeyStroke.getKeyStroke( KeyEvent.VK_DOWN,  0 ), "none" );
   	}

    public void setValueNoAction( int n ) {
   		removeChangeListener( this );
   		super.setValue( n );
   		addChangeListener( this );
   	}

    public synchronized void addActionListener( ActionListener l ) {
   		al = AWTEventMulticaster.add( al, l );
   	}

   	public synchronized void removeActionListener( ActionListener l ) {
   		al = AWTEventMulticaster.remove( al, l );
   	}

   	public void stateChanged( ChangeEvent e ) {
   		final ActionListener l = al;
   		if( l != null ) {
   			l.actionPerformed( new ActionEvent( e.getSource(), ActionEvent.ACTION_PERFORMED, null ));
   		}
   	}
}