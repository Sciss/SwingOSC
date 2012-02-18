/*
 *	JSCUserView
 *	(SwingOSC classes for SuperCollider)
 *
 *	Copyright (c) 2005-2012 Hanns Holger Rutz. All rights reserved.
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

JSCUserView : JSCAbstractUserView {
	var lastMouseX = 0, lastMouseY = 0;

	// ----------------- public class methods -----------------

	*paletteExample { arg parent, bounds;
		^this.new( parent, bounds ).refreshOnFocus_( false ).drawFunc_({ arg view;
			var b = view.bounds, min = min( b.width, b.height ), max = max( b.width, b.height ),
			    num = (max / min).asInteger;
			JPen.addRect( b.moveTo( 0, 0 ));
			JPen.clip;
			JPen.scale( min, min );
			num.do({ 	arg i;
				var rel = i / num;
				JPen.fillColor = Color.hsv( rel, 0.4, 0.6 );
				JPen.addWedge( (0.5 + i) @ 0.5, 0.4, rel * pi + 0.2, 1.5pi );
				JPen.fill;
			});
		});
	}

	// ----------------- public instance methods -----------------

	mousePosition {
		var b;
		^if( relativeOrigin, {
			lastMouseX @ lastMouseY;
		}, {
			b = this.prBoundsReadOnly;
			(lastMouseX - b.left) @ (lastMouseY - b.top);
		});
	}

	relativeOrigin_ { arg bool;
		relativeOrigin = bool;
		this.setProperty( \relativeOrigin, bool );
	}

	// ----------------- private instance methods -----------------

	mouseDown { arg x, y ... rest;
		lastMouseX	= x;
		lastMouseY	= y;
		^super.mouseDown( x, y, *rest );
	}
	
	mouseUp { arg x, y ... rest;
		lastMouseX	= x;
		lastMouseY	= y;
		^super.mouseUp( x, y, *rest );
	}
	
	mouseMove { arg x, y ... rest;
		lastMouseX	= x;
		lastMouseY	= y;
		^super.mouseMove( x, y, *rest );
	}
	
	mouseOver { arg x, y ... rest;
		lastMouseX	= x;
		lastMouseY	= y;
		^super.mouseOver( x, y, *rest );
	}

	prInitView {
		^this.prSCViewNew([
			[ '/local', this.id, '[', '/new', "de.sciss.swingosc.UserView", ']' ]
		]);
	}

	prSendProperty { arg key, value;

		key	= key.asSymbol;

		// fix keys
		case { key === \relativeOrigin }
		{
			if( penID.notNil, { server.sendMsg( '/set', penID, \absCoords, value.not )});
			^nil;
		};
		^super.prSendProperty( key, value );
	}
}
