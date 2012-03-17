/*
 *	JSCButton
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
JSCButton : JSCView {
	var <states;
	
	var acResp;	// OSCpathResponder for action listening

	// ----------------- public class methods -----------------

	*paletteExample { arg parent, bounds;
		var v;
		v = this.new( parent, bounds );
		v.states = [
			[ "Push" ], [ "Pop", Color.white, Color.new255( 50, 80, 180 )]
		];
		^v;
	}
	
	// ----------------- public instance methods -----------------

	value { ^this.getProperty( \value )}
	
	value_ { arg val;
		this.setProperty( \value, this.prFixValue( val ));
	}
	
	valueAction_ { arg val;
		this.setPropertyWithAction( \value, this.prFixValue( val ));
	}	

	doAction { arg modifiers;
		action.value( this, modifiers );
	}
	
	font { ^this.getProperty( \font )}

	font_ { arg argFont;
//		font = argFont;
		this.setProperty( \font, argFont );
	}

	states_ { arg array;
		states = array.deepCopy;
		this.setProperty( \states, states );
	}
	
	string { ^states[ this.value ][ 0 ]}
	string_ { arg val; this.states = [[ val ]]}
	
	defaultKeyDownAction { arg char, modifiers, unicode;
// JJJ handled automatically by javax.swing.AbstractButton
//		if (char == $ , { this.valueAction = this.value + 1; ^this });
		if (char == $\r, { this.valueAction = this.value + 1; ^this });
		if (char == $\n, { this.valueAction = this.value + 1; ^this });
		if (char == 3.asAscii, { this.valueAction = this.value + 1; ^this });
		^nil;		// bubble if it's an invalid key
	}

	defaultGetDrag { ^this.value }

	defaultCanReceiveDrag {
		^currentDrag.isNumber or: { currentDrag.isKindOf( Function )};
	}
	
	defaultReceiveDrag {
		if( currentDrag.isNumber, {
			this.valueAction = currentDrag;
		}, {
			this.action = currentDrag;
		});
	}

	// ----------------- private instance methods -----------------

	properties {
		^super.properties ++ #[ \value, \font, \states ];
	}
	
	prFixValue { arg val;
		val = val.asInteger;
		// clip() would be better but SCButton resets to zero always
		if( (val < 0) || (val >= states.size), {
			val = 0;
		});
		^val;
	}
	
	prNeedsTransferHandler { ^true }

	prClose { arg preMsg, postMsg;
		acResp.remove;
		^super.prClose( preMsg ++
			[[ '/method', "ac" ++ this.id, \remove ],
			 [ '/free', "ac" ++ this.id ]], postMsg );
	}

	prInitView {
		properties.put( \value, 0 );
		acResp = OSCpathResponder( server.addr, [ '/action', this.id ], { arg time, resp, msg;
			var value, modifiers;
			value	= msg[4];
			modifiers	= msg[6];
			modifiers		= ((modifiers & 3) << 17) |
						  ((modifiers & 4) << 18) |
						  ((modifiers & 8) << 16) | fakeModifiers;
			// don't call valueAction coz we'd create a loop
			properties.put( \value, msg[4] );
			{ this.doAction( modifiers )}.defer;
		}).add;
		^this.prSCViewNew([
			[ '/local', this.id, '[', '/new', "de.sciss.swingosc.Button", ']', // "de.sciss.gui.MultiStateButton"
				"ac" ++ this.id,
				'[', '/new', "de.sciss.swingosc.ActionResponder", this.id, '[', '/array', \selectedIndex, \lastModifiers, ']', ']' ]
		]);
	}

	prSendProperty { arg key, value;
		var bndl, msg;

		key	= key.asSymbol;

		// fix keys
		case { key === \value }
		{
			key = \selectedIndex;
		}
		{ key === \states }
		{
			bndl = [[ '/method', this.id, \removeAllItems ]];
			value.do({ arg state;
				bndl = bndl.add([ '/method', this.id, \addItem ] ++ state[0].asSwingArg ++ state[1].asSwingArg ++
					state[2].asSwingArg );
			});
			server.listSendBundle( nil, bndl );
			^nil;
		};
		^super.prSendProperty( key, value );
	}
}

