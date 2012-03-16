/*
 *	JSCKnob
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
JSCKnob : JSCSliderBase {
	var acResp;	// OSCpathResponder for action listening
	var clpse;
	var <mode = \round;
	var <centered = false;
	var colrRange, colrTrack, colrHand;

	var <>keystep = 0.01;

	// ----------------- public class methods -----------------

	*paletteExample { arg parent, bounds;
		^this.new( parent, bounds ).value_( 0.2 );
	}

	// ----------------- public instance methods -----------------

	value { ^this.getProperty( \value )}
	
	value_ { arg val;
		this.setProperty( \value, this.prSnap( val ));
	}
	
	valueAction_ { arg val;
		this.setPropertyWithAction( \value, this.prSnap( val ));
	}
	
	mode_ { arg inputMode;
		// XXX currently not implemented
		mode = inputMode;
	}
	
	centered_ { arg bool;
		server.sendMsg( \set, this.id, \centered, bool );
		centered = bool;
	}
	
	color {
		^[ this.knobColor  ?? { Color.new255( 169,176,190 )},
		   this.rangeColor ?? { Color.new255( 51,98,140 )},
		   this.trackColor ?? { Color.new255( 233,236,242 )},
		   this.handColor  ?? { Color.black }
		];
	}
	
	color_ { arg array;
		this.knobColor  = array[ 0 ];
		this.rangeColor = array[ 1 ];
		this.trackColor = array[ 2 ];
		this.handColor  = array[ 3 ];
	}
	
	rangeColor { ^colrRange }
	rangeColor_ { arg val;
		if( colrRange != val, {
			colrRange = val;
			server.listSendMsg([ \set, this.id, \rangeColor ] ++ val.asSwingArg );
		})
	}

	trackColor { ^colrTrack  }
	trackColor_ { arg val;
		if( colrTrack != val, {
			colrTrack = val;
			server.listSendMsg([ \set, this.id, \trackColor ] ++ val.asSwingArg );
		})
	}

	handColor { ^colrHand  }
	handColor_ { arg val;
		if( colrHand != val, {
			colrHand = val;
			server.listSendMsg([ \set, this.id, \handColor ] ++ val.asSwingArg );
		})
	}
		
	defaultKeyDownAction { arg char, modifiers, unicode, keycode;
		// standard keydown
		if (char == $r, { this.valueAction = 1.0.rand; ^this });
		if (char == $n, { this.valueAction = 0.0; ^this });
		if (char == $x, { this.valueAction = 1.0; ^this });
		if (char == $c, { this.valueAction = 0.5; ^this });
		if (char == $], { this.increment( this.getScale( modifiers )); ^this });
		if (char == $[, { this.decrement( this.getScale( modifiers )); ^this });
		if (unicode == 0xF700, { this.increment( this.getScale( modifiers )); ^this });
		if (unicode == 0xF703, { this.increment( this.getScale( modifiers )); ^this });
		if (unicode == 0xF701, { this.decrement( this.getScale( modifiers )); ^this });
		if (unicode == 0xF702, { this.decrement( this.getScale( modifiers )); ^this });
		^nil		// bubble if it's an invalid key
	}

	increment { |zoom=1| ^this.valueAction = (this.value + (keystep * zoom)) }

	decrement { |zoom=1| ^this.valueAction = (this.value - (keystep * zoom)) }
	
	defaultCanReceiveDrag { ^currentDrag.isNumber }
	defaultGetDrag { ^this.value }
	defaultReceiveDrag { this.valueAction = currentDrag }

	// ----------------- private instance methods -----------------

	prNeedsTransferHandler { ^true }

	prClose { arg preMsg, postMsg;
		acResp.remove;
		clpse.cancel;
		^super.prClose( preMsg ++
			[[ '/method', "ac" ++ this.id, \remove ],
			 [ '/free', "ac" ++ this.id ]], postMsg );
	}

	prInitView {
		var b;
		properties.put( \value, 0.0 );
		properties.put( \step, 0.0 );
		jinsets	= Insets.new;	// none
		clpse	= Collapse({ this.doAction });
		acResp	= OSCpathResponder( server.addr, [ '/action', this.id ], { arg time, resp, msg;
			var newVal;
			newVal = this.prSnap( msg[4] / 0x40000000 );
			if( newVal != this.value, {
				// don't call valueAction coz we'd create a loop
				properties.put( \value, newVal );
				clpse.instantaneous;
			});
		}).add;
		^this.prSCViewNew([
			[ '/local', this.id,
				'[', '/new', "de.sciss.swingosc.RotaryKnob2", 0, 0x40000000, 0, ']',
				"ac" ++ this.id,
				'[', '/new', "de.sciss.swingosc.ActionResponder", this.id, \value, ']' ]
		]);
	}
	
	prSendProperty { arg key, value;

		key	= key.asSymbol;

		// fix keys
		case { key === \value }
		{
			key		= \valueNoAction;
			value	= value * 0x40000000;
		}
		{ key === \step }
		{
//			value = max( 1, value * 0x40000000 ).asInteger;
////			server.sendMsg( '/set', this.id, \snapToTicks, value != 0,
////							\minorTickSpacing, value, \extent, value );
//			server.sendMsg( '/set', this.id, \snapToTicks, value != 0,
//							\majorTickSpacing, value ); // stupidly, using extent won't let you move the slider to the max
			^nil;
		};
		^super.prSendProperty( key, value );
	}
}