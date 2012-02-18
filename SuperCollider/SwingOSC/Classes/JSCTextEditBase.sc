/*
 *	JSCTextEditBase
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

JSCTextEditBase : JSCStaticTextBase {

	var <>keyString;
	var <>typingColor, <>normalColor;
	var origBd;

	// ----------------- public instance methods -----------------

	caretColor { ^this.getProperty( \caretColor )}
	caretColor_ { arg color; this.setProperty( \caretColor, color )}

	value { ^object }
	
	value_ { arg val;
		keyString = nil;
//		this.stringColor = normalColor;
		object = val;
		this.string = object.asString;
	}
	
	valueAction_ { arg val;
		var prev;
		prev = object;
		this.value = val;
		if( object != prev, { this.doAction });
	}
	
	boxColor {
		^this.getProperty( \boxColor, Color.new );
	}
	
	boxColor_ { arg color;
		this.setProperty( \boxColor, color );
	}

	setNormalBorder {
		if( origBd.notNil, {
			server.sendBundle( nil, [ '/set', this.id, \border, '[', '/ref', origBd, ']' ], [ '/free', origBd ]);
			origBd = nil;
		});
	}

	setLineBorder { arg color = Color.black, thickness = 1;
		var msg;
		msg = [ '/set', this.id, \border, '[', '/method', 'javax.swing.BorderFactory', \createLineBorder ] ++
			color.asSwingArg ++ [ thickness, ']' ];
		if( origBd.isNil, {
			origBd = "bd" ++ this.id;
			server.sendBundle( nil, [ '/local', origBd, '[', '/method', this.id, \getBorder, ']' ], msg );
		}, {
			server.listSendMsg( msg );
		});
	}
	
	borderless_ { arg bool; ^if( bool, { this.setLineBorder( thickness: 0 )}, { this.setNormalBorder })}
	
	// ----------------- private instance methods -----------------

	properties {
		^super.properties ++ #[\boxColor]
	}

	init { arg argParent, argBounds, id;
		typingColor	= Color.red;
		normalColor	= Color.black;
		parent		= argParent.asView; // actual view
// cocoa does parent.asView once more. too cryptic IMO ?
//		this.prInit( parent, argBounds.asRect, this.class.viewClass, parent.server, id );
		this.prInit( parent.asView, argBounds, this.class.viewClass, parent.server, id );
		argParent.add( this );//maybe window or viewadapter
	}

	prSendProperty { arg key, value;
		key	= key.asSymbol;

		// fix keys
		case { key === \boxColor }
		{
			key = \background;
			if( value == Color.clear, {
				value = nil;
			});
		};
		^super.prSendProperty( key, value );
	}

	prClose { arg preMsg, postMsg;
		if( origBd.notNil, {
			preMsg = preMsg.add([ '/free', origBd ]);
		});
		^super.prClose( preMsg, postMsg );
	}
	
	prNeedsTransferHandler { ^true }
}
