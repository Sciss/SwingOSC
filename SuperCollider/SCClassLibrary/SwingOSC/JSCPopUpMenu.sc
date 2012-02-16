/*
 *	JSCPopUpMenu
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
JSCPopUpMenu : JSCView {
	var <items;
	
	var acResp;	// OSCpathResponder for action listening
	var <>allowsReselection = false;

	// ----------------- public class methods -----------------

	*paletteExample { arg parent, bounds;
		var v;
		v = this.new(parent, bounds);
		v.items = #[ "linear", "exponential", "sine", "welch", "squared", "cubed" ];
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
	
	font { ^this.getProperty( \font )}
	
	font_ { arg argFont;
//		font = argFont;
		this.setProperty( \font, argFont );
	}
	
	items_ { arg array;
		items = array.copy;
		this.setProperty( \items, items );
	}
	
	item { ^items[ this.value ]}

	stringColor {
		^this.getProperty( \stringColor, Color.new );
	}
	
	stringColor_ { arg color;
		this.setProperty( \stringColor, color );
	}
	
	defaultKeyDownAction { arg char, modifiers, unicode;
// JJJ used by lnf
//		if (char == $ , { this.valueAction = this.value + 1; ^this });
//		if (char == $\r, { this.valueAction = this.value + 1; ^this });
//		if (char == $\n, { this.valueAction = this.value + 1; ^this });
		if (char == 3.asAscii, { this.valueAction = this.value + 1; ^this });
//		if (unicode == 16rF700, { this.valueAction = this.value - 1; ^this });
		if (unicode == 16rF703, { this.valueAction = this.value + 1; ^this });
//		if (unicode == 16rF701, { this.valueAction = this.value + 1; ^this });
		if (unicode == 16rF702, { this.valueAction = this.value - 1; ^this });
		^nil		// bubble if it's an invalid key
	}
	
	defaultGetDrag { ^this.value }
	defaultCanReceiveDrag { ^currentDrag.isNumber }

	defaultReceiveDrag {
		this.valueAction = currentDrag;
	}

	// ----------------- private instance methods -----------------

	properties {
		^super.properties ++ #[ \value, \font, \items, \stringColor ];
	}

	prFixValue { arg val;
		^val.clip( 0, items.size - 1 );
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
			var newVal;
			
			newVal = msg[4];
			if( allowsReselection or: { newVal != this.value }, {
				// don't call valueAction coz we'd create a loop
				properties.put( \value, newVal );
				{ this.doAction; }.defer;
			});
		}).add;
		^this.prSCViewNew([
			[ '/local', this.id, '[', '/new', "de.sciss.swingosc.PopUpView", ']',
				"ac" ++ this.id,
				'[', '/new', "de.sciss.swingosc.ActionResponder", this.id, \selectedIndex, ']' ]
		]);
	}

	prSendProperty { arg key, value;

		key	= key.asSymbol;

		// fix keys
		case
//		{ key === \value }
//		{
//			key = \selectedIndex;
//		}
		{ key === \items }
		{
			this.prSetItems( value ); // .performUnaryOp( \asString );
			^nil;
		};
		^super.prSendProperty( key, value );
	}

// XXX at the moment...
//	prBoundsToJava { arg rect;
//		var pb;
//		
//		pb = parent.bounds;
//		if( rect.height < 26, {
//			rect			= Rect( rect.left - pb.left, rect.top - ((26 - rect.height) >> 1) - pb.top,
//							   rect.width, 26 );
//		}, {
//			rect	= rect.moveBy( pb.left.neg, pb.top.neg );
//		});
//		^rect;
//	}

// what shall we do ...
//	prBoundsFromJava { arg rect;
//		^rect;
//	}

	prSetItems { arg items;
		var sizes, dataSize, startIdx, itemArgs, bndl, selectedIdx;

		itemArgs	= items.collect({ arg it; it.asString.asSwingArg }); // necessary to escape plain bracket strings!
		selectedIdx = this.value;
		if( selectedIdx >= items.size, { selectedIdx = -1 });
		sizes	= itemArgs.collect({ arg itemArg; itemArg.sum( _.oscEncSize )});
		if( (sizes.sum + 55) <= server.options.oscBufSize, {
			server.listSendMsg([ '/method', this.id, \setListData, '[', '/array' ] ++ itemArgs.flatten ++ [ ']', selectedIdx ]);
		}, {	// need to split it up
			startIdx = 0;
			dataSize	= 147; // 45;
			bndl		= Array( 3 );
			bndl.add([ '/method', this.id, \beginDataUpdate ]);
			sizes.do({ arg size, idx;
				if( (dataSize + size) > server.options.oscBufSize, {
					bndl.add([ '/method', this.id, \addData, '[', '/array', ] ++
						itemArgs.copyRange( startIdx, idx - 1 ).flatten ++ [ ']' ]);
					server.listSendBundle( nil, bndl );
					dataSize	= 111 + size; // 45;
					startIdx	= idx;
					bndl		= Array( 2 );
				}, {
					dataSize = dataSize + size;
				});
			});
			bndl.add([ '/method', this.id, \addData, '[', '/array', ] ++
					itemArgs.copyRange( startIdx, items.size - 1 ).flatten ++ [ ']' ]);
			bndl.add([ '/method', this.id, \endDataUpdate, selectedIdx ]);
			server.listSendBundle( nil, bndl );
		});
	}
}
