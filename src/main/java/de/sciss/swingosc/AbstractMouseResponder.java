/*
 *  AbstractMouseResponder.java
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

import java.lang.reflect.InvocationTargetException;

public abstract class AbstractMouseResponder
extends AbstractResponder
{
	private final Frame		f;
	protected boolean		acceptsMouseOver;
	
	protected AbstractMouseResponder( Object objectID, int numReplyArgs, Object frameID  )
	throws IllegalAccessException, NoSuchMethodException, InvocationTargetException
	{
		super( objectID, numReplyArgs );

		final Object o = frameID == null ? null : client.getObject( frameID );
		if( (o != null) && (o instanceof Frame) ) {
			f = (Frame) o;
			f.registerMouseResponder( this );
			acceptsMouseOver = f.getAcceptMouseOver();
		} else {
			f = null;
			acceptsMouseOver = true;
		}
	}

	public void setAcceptMouseOver( boolean onOff ) {
		acceptsMouseOver = onOff;
	}
	
	public boolean getAcceptMouseOver()
	{
		return acceptsMouseOver;
	}

	public void remove()
	throws IllegalAccessException, InvocationTargetException
	{
		if( f != null ) f.unregisterMouseResponder( this );
		super.remove();
	}
}
