/*
 *  TextField.java
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
import java.awt.Graphics;
import java.awt.Insets;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JTextField;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.Document;

public class TextField
extends JTextField {
    private final List collChangeListeners = new ArrayList();
    private DocumentListener docToChange;

	public TextField() {
		super();
	}
	
	public TextField( String text ) {
		super( text );
	}

	public TextField( int columns ) {
		super( columns );
	}

	public TextField( String text, int columns ) {
		super( text, columns );
	}

	public TextField( Document doc, String text, int columns ) {
		super( doc, text, columns );
	}
	
	// this is here to make DocumentResponder less complex
	// (because now it can connect both Caret and Document listeners to the same object)
	// ; this just forwards the request to the Document.
	public void addDocumentListener( DocumentListener l ) {
		getDocument().addDocumentListener( l );
	}

	public void removeDocumentListener( DocumentListener l ) {
		getDocument().removeDocumentListener( l );
	}

    public void addChangeListener( ChangeListener l ) {
        final boolean init = collChangeListeners.isEmpty();
        collChangeListeners.add( l );
        if( init ) addDocToChange();
    }

    public void removeChangeListener( ChangeListener l ) {
        collChangeListeners.remove( l );
        if( collChangeListeners.isEmpty() ) removeDocToChange();
    }

    private void fireChange() {
        final ChangeEvent e = new ChangeEvent( this );
        for( int i = 0; i < collChangeListeners.size(); i++ ) {
            final ChangeListener l = (ChangeListener) collChangeListeners.get( i );
            l.stateChanged( e );
        }
    }

    private void addDocToChange() {
        assert( docToChange == null );
        docToChange = new DocumentListener() {
            public void insertUpdate( DocumentEvent e ) {
                fireChange();
            }

            public void removeUpdate( DocumentEvent e ) {
                fireChange();
            }

            public void changedUpdate( DocumentEvent e ) {}
        };
        addDocumentListener( docToChange );
    }

    private void removeDocToChange() {
        assert( docToChange != null );
        removeDocumentListener( docToChange );
        docToChange = null;
    }

	/**
	 *	Overwritten to toggle the opacity settings
	 *	when background colour is (semi)transparent
	 */
	public void setBackground( Color c ) {
		setOpaque( (c != null) && (c.getAlpha() == 0xFF) );
		super.setBackground( c );
	}

//	public void paintComponent( Graphics g )
//	{
//		final Color colrBg	= getBackground();
//
//		if( (colrBg != null) && (colrBg.getAlpha() > 0) ) {
////            final Insets in = getInsets();
//			g.setColor( colrBg );
//			g.fillRect( 2, 2, getWidth() - 4, getHeight() - 4 );
//		}
//		super.paintComponent( g );
//	}
}