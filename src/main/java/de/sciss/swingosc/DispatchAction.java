/*
 * 	DispatchAction.java
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

import java.awt.AWTEventMulticaster;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.AbstractAction;
import javax.swing.Icon;
import javax.swing.KeyStroke;

public class DispatchAction
extends AbstractAction
{
	private ActionListener em	= null;
	
	public DispatchAction()
	{
		super();
	}
	
	public DispatchAction( String text, KeyStroke accel )
	{
		super( text );
		if( accel != null ) putValue( ACCELERATOR_KEY, accel );
	}

	public DispatchAction( String text )
	{
		super( text );
	}

	public DispatchAction( String text, Icon ic )
	{
		super( text, ic );
	}
	
    public synchronized void addActionListener( ActionListener l )
    {
    	em = AWTEventMulticaster.add( em, l );
    }
    
    public synchronized void removeActionListener( ActionListener l )
    {
        em = AWTEventMulticaster.remove( em, l );
	}

  	public void actionPerformed( ActionEvent e )
	{
// 		System.out.println( "actionPerformed" );
		if( em != null ) em.actionPerformed( e );
	}
}