/*
 *  WindowListenerWrapper.java
 *  (de.sciss.gui package)
 *
 *  Copyright (c) 2004-2008 Hanns Holger Rutz. All rights reserved.
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
 *
 *
 *  Changelog:
 *		30-Aug-06	created
 *		19-Mar-08	fixed severe design mistake
 */

package de.sciss.gui;

import java.awt.Window;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import de.sciss.app.AbstractWindow;

/**
 *  @author		Hanns Holger Rutz
 *  @version	0.70, 19-Mar-08
 */
public class WindowListenerWrapper
implements WindowListener
{
//	private static	Map							mapListeners = null;
	private final	AbstractWindow.Listener		l;
	private final	AbstractWindow				w;

	private WindowListenerWrapper( AbstractWindow.Listener l, AbstractWindow w )
	{
		this.l	= l;
		this.w	= w;
	}
	
	public static void add( AbstractWindow.Listener l, AbstractWindow w )
	{
		final WindowListenerWrapper wlw = new WindowListenerWrapper( l, w );
		((Window) w.getWindow()).addWindowListener( wlw );
//		if( mapListeners == null ) mapListeners = new HashMap();
//		mapListeners.put( w, wlw );
	}
	
	public static void remove( AbstractWindow.Listener l, AbstractWindow w )
	{
		final Window w2 = (Window) w.getWindow();
//		final WindowListenerWrapper wlw = (WindowListenerWrapper) mapListeners.remove( w );
//		w2.removeWindowListener( wlw );
		
		final WindowListener[] coll = w2.getWindowListeners();
		WindowListenerWrapper wlw;
		for( int i = 0; i < coll.length; i++ ) {
			if( coll[ i ] instanceof WindowListenerWrapper ) {
				wlw = (WindowListenerWrapper) coll[ i ];
				if( wlw.l == l ) {
					w2.removeWindowListener( wlw );
					return;
				}
			}
		}
		throw new IllegalArgumentException( "Listener was not registered " + l );
	}

	public void windowOpened( WindowEvent e )
	{
		l.windowOpened( wrap( e ));
	}
	
	public void windowClosing( WindowEvent e )
	{
		l.windowClosing( wrap( e ));
	}
	
	public void windowClosed( WindowEvent e )
	{
		l.windowClosed( wrap( e ));
	}
	
	public void windowIconified( WindowEvent e )
	{
		l.windowIconified( wrap( e ));
	}
	
	public void windowDeiconified( WindowEvent e )
	{
		l.windowDeiconified( wrap( e ));
	}
	
	public void windowActivated( WindowEvent e )
	{
		l.windowActivated( wrap( e ));
	}
	
	public void windowDeactivated( WindowEvent e )
	{
//System.out.println( "windowDeactivated : " + ((javax.swing.JFrame) e.getWindow()).getTitle() + " __ " + l .getClass().getName() );
		l.windowDeactivated( wrap( e ));
	}
	
	private AbstractWindow.Event wrap( WindowEvent e )
	{
		return AbstractWindow.Event.convert( w, e );
	}
}