/*
 *	JSCSlider2D
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

JSCSlider2D : JSCSliderBase {

	var acResp;	// OSCpathResponder for action listening
	var clpse;

	// ----------------- public instance methods -----------------

	step_ { arg stepSize;
		super.step_( stepSize );
		this.x_( this.x );
		this.y_( this.y );
	}

	x { ^this.getProperty( \x )}

	x_ { arg val;
		this.setProperty( \x, this.prSnap( val ));
	}
	
	activex_ { arg val;
		this.setPropertyWithAction( \x, this.prSnap( val ));
	}
	
	y { ^this.getProperty( \y )}

	y_ { arg val;
		this.setProperty( \y, this.prSnap( val ));
	}
	
	activey_ { arg val;
		this.setPropertyWithAction( \y, this.prSnap( val ));
	}
	
	setXY { arg x, y;
		x = this.prSnap( x );
		y = this.prSnap( y );
		properties.put( \x, x );
		properties.put( \y, y );
		server.sendMsg( '/set', this.id, \knobX, x, \knobY, y );
	}
	
	setXYActive { arg x, y;
		this.setXY( x, y );
		this.doAction;
	}

	pixelStepX { ^(this.prBoundsReadOnly.width - 17).max( 1 ).reciprocal }
	pixelStepY { ^(this.prBoundsReadOnly.height - 17).max( 1 ).reciprocal }

	incrementY { arg zoom = 1; ^this.y = this.y + (this.pixelStepY * zoom) }
	decrementY { arg zoom = 1; ^this.y = this.y - (this.pixelStepY * zoom) }
	incrementX { arg zoom = 1; ^this.x = this.x + (this.pixelStepX * zoom) }
	decrementX { arg zoom = 1; ^this.x = this.x - (this.pixelStepX * zoom) }

	defaultKeyDownAction { arg char, modifiers, unicode,keycode;
		// standard keydown
		if (char == $r, { this.setXYActive( 1.0.rand, 1.0.rand ); ^this });
		if (char == $n, { this.setXYActive( 0.0, 0.0 ); ^this });
		if (char == $x, { this.setXYActive( 1.0, 1.0 ); ^this });
		if (char == $c, { this.setXYActive( 0.5, 0.5 ); ^this });
		if (unicode == 0xF700, { this.incrementY( this.getScale( modifiers )); this.doAction; ^this });
		if (unicode == 0xF703, { this.incrementX( this.getScale( modifiers )); this.doAction; ^this });
		if (unicode == 0xF701, { this.decrementY( this.getScale( modifiers )); this.doAction; ^this });
		if (unicode == 0xF702, { this.decrementX( this.getScale( modifiers )); this.doAction; ^this });
		^nil		// bubble if it's an invalid key
	}

	defaultGetDrag { ^Point( this.x, this.y )}
	defaultCanReceiveDrag { ^currentDrag.isKindOf( Point )}
	defaultReceiveDrag { this.setXYActive( currentDrag.x, currentDrag.y )}

	// ----------------- private instance methods -----------------

	properties {
		^super.properties ++ #[ \x, \y ];
	}
	
	prNeedsTransferHandler { ^true }

	prClose { arg preMsg, postMsg;
		acResp.remove;
		clpse.cancel;
		^super.prClose( preMsg ++
			[[ '/method', "ac" ++ this.id, \remove ],
			 [ '/free', "ac" ++ this.id ],
			 [ '/method', this.id, \dispose ]], postMsg );
	}

	prInitView {
		properties.put( \x, 0.0 );
		properties.put( \y, 0.0 );
		properties.put( \step, 0.0 );
		clpse	= Collapse({ this.doAction });
		acResp	= OSCpathResponder( server.addr, [ '/action', this.id ], { arg time, resp, msg;
			var newX, newY;
		
			newX = msg[4];
			newY = msg[6];
			if( (newX != this.x) || (newY != this.y), {
				// don't call valueAction coz we'd create a loop
				properties.put( \x, newX );
				properties.put( \y, newY );
				clpse.instantaneous;
			});
		}).add;
		^this.prSCViewNew([
			[ '/local', this.id,
				'[', '/new', "de.sciss.swingosc.Slider2D", ']',
				"ac" ++ this.id,
				'[', '/new', "de.sciss.swingosc.ActionResponder", this.id, '[', '/array', \knobX, \knobY, ']', ']' ]
		]);
	}

	prSendProperty { arg key, value;

		key	= key.asSymbol;

		// fix keys
		case { key === \x }
		{
			key = \knobX;
		}
		{ key === \y }
		{
			key = \knobY;
		}
		{ key === \step }
		{
			key = \stepSize;
		};
		^super.prSendProperty( key, value );
	}
}

JSC2DSlider : JSCSlider2D {
	*new { arg parent, bounds, id;
		this.deprecated( thisMethod, Meta_JSCSlider2D.findRespondingMethodFor( \new ));
		^JSCSlider2D.new( parent, bounds, id );
	}
}