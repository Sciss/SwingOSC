/*
 *	JSCRangeSlider
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

JSCRangeSlider : JSCSliderBase {

	var acResp;	// OSCpathResponder for action listening
	var clpse;
	var orientation;	// 0 for horiz, 1 for vert

	// ----------------- public class methods -----------------

	*paletteExample { arg parent, bounds;
		var v;
		v = this.new( parent, bounds );
		v.setSpan( 0.2, 0.7 );
		^v;
	}

	// ----------------- public instance methods -----------------

	step_ { arg stepSize;
		super.step_( stepSize );
		this.setSpan( this.lo, this.hi );
	}
	
	lo { ^this.getProperty( \lo )}

	lo_ { arg val;
		this.setProperty( \lo, this.prSnap( val ));
	}
	
	activeLo_ { arg val;
		this.setPropertyWithAction( \lo, this.prSnap( val ));
	}
	
	hi { ^this.getProperty( \hi )}

	hi_ { arg val;
		this.setProperty( \hi, this.prSnap( val ));
	}
	
	activeHi_ { arg val;
		this.setPropertyWithAction( \hi, this.prSnap( val ));
	}
	
	range { ^(this.hi - this.lo).abs }

	range_ { arg val;
		this.hi_( this.prSnap( this.lo + val ));
	}
	
	activeRange_ { arg val;
		this.range_( val );
		this.doAction;
	}
	
	setSpan { arg lo, hi;
		lo = this.prSnap( lo );
		hi = this.prSnap( hi );
		properties.put( \lo, lo );
		properties.put( \hi, hi );
		server.sendMsg( '/set', this.id, \knobPos, min( lo, hi ), \knobExtent, abs( hi - lo ));
	}
	
	setSpanActive { arg lo, hi;
		this.setSpan( lo, hi );
		this.doAction;
	}

	setDeviation { arg deviation, average;
		var lo = (1 - deviation) * average;
		this.setSpan( lo, lo + deviation );
	}

	pixelStep { 
		var b = this.prBoundsReadOnly;
		^(if( orientation == 0, { b.width }, { b.height }) - 2).max( 1 ).reciprocal;
	}

	increment { arg zoom = 1;
		var inc, val; 
		inc = this.pixelStep * zoom;
		val = this.hi + inc;
		if( val > 1, {
			inc = 1 - this.hi;
			val = 1;
		});
		this.setSpanActive( this.lo + inc, val );
	}
	
	decrement { arg zoom = 1;
		var inc, val; 
		inc = this.pixelStep * zoom;
		val = this.lo - inc;
		if( val < 0, {
			inc = this.lo;
			val = 0;
		});
		this.setSpanActive( val, this.hi - inc );
	}

	bounds_ { arg rect;
		var result;
		result = super.bounds_( rect );
		if( if( rect.width > rect.height, 0, 1 ) != orientation, {
			orientation = 1 - orientation;
			server.sendMsg( '/set', this.id, \orientation, orientation );
		});
		^result;
	}

	defaultKeyDownAction { arg char, modifiers, unicode;
		var a, b;
		// standard keydown
		if (char == $r, { 
			a = 1.0.rand;
			b = 1.0.rand;
			this.setSpanActive( min( a, b ), max( a, b ));
			^this;
		});
		if (char == $n, { this.setSpanActive( 0.0, 0.0 ); ^this });
		if (char == $x, { this.setSpanActive( 1.0, 1.0 ); ^this });
		if (char == $c, { this.setSpanActive( 0.5, 0.5 ); ^this });
		if (char == $a, { this.setSpanActive( 0.0, 1.0 ); ^this });
		if (unicode == 0xF700, { this.increment( this.getScale( modifiers )); ^this });
		if (unicode == 0xF703, { this.increment( this.getScale( modifiers )); ^this });
		if (unicode == 0xF701, { this.decrement( this.getScale( modifiers )); ^this });
		if (unicode == 0xF702, { this.decrement( this.getScale( modifiers )); ^this });
		^nil;		// bubble if it's an invalid key
	}

	defaultGetDrag { ^Point( this.lo, this.hi )}	
	defaultCanReceiveDrag {	 ^currentDrag.isKindOf( Point )}
	
	defaultReceiveDrag {
		// changed to x,y instead of lo, hi
		this.setSpanActive( currentDrag.x, currentDrag.y );
	}

	// ----------------- private instance methods -----------------

	properties {
		^super.properties ++ #[ \lo, \hi ];
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
		var b;
		properties.put( \lo, 0.0 );
		properties.put( \hi, 1.0 );
		properties.put( \step, 0.0 );
		b			= this.prBoundsReadOnly;
		orientation	= if( b.width > b.height, 0, 1 );
		clpse		= Collapse({ this.doAction });
		acResp		= OSCpathResponder( server.addr, [ '/action', this.id ], { arg time, resp, msg;
			var newLo, newHi;
		
			newLo	= msg[4];
			newHi 	= newLo + msg[6];
			if( (newLo != this.lo) || (newHi != this.hi), {
				// don't call valueAction coz we'd create a loop
				properties.put( \lo, newLo );
				properties.put( \hi, newHi );
				clpse.instantaneous;
			});
		}).add;
		^this.prSCViewNew([
			[ '/set', '[', '/local', this.id, '[', '/new', "de.sciss.swingosc.RangeSlider", orientation, ']', ']',
				\knobColor ] ++ Color.blue.asSwingArg,
			[ '/local', "ac" ++ this.id,
				'[', '/new', "de.sciss.swingosc.ActionResponder", this.id, '[', '/array', \knobPos, \knobExtent, ']', ']' ]
		]);
	}

	prSendProperty { arg key, value;
		key	= key.asSymbol;

		// fix keys
		case { key === \lo }
		{
			server.sendMsg( '/set', this.id, \knobPos, min( value, this.hi ), \knobExtent, abs( value - this.hi ));
			^nil;		
		}
		{ key === \hi }
		{
			server.sendMsg( '/set', this.id, \knobPos, min( value, this.lo ), \knobExtent, abs( value - this.lo ));
			^nil;		
		}
		{ key === \step }
		{
			key = \stepSize;
		};
		^super.prSendProperty( key, value );
	}
}
