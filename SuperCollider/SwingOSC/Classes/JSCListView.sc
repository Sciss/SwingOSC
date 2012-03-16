/*
 *	JSCListView
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
JSCListView : JSCView {
	var <items, <>enterKeyAction;
	var <allowsDeselection = false;
	
	var acResp;	// listens to list selection changes
	var cnID;
	
	// ----------------- public class methods -----------------

	*paletteExample { arg parent, bounds;
		var v;
		v = this.new(parent, bounds);
		v.items = #[ "linear", "exponential", "sine", "welch", "squared", "cubed" ];
		^v;
	}
	
	// ----------------- public instance methods -----------------

	item { ^items[ this.value ]}

	value { ^this.getProperty( \value )}

	value_ { arg val;
		this.setProperty( \value, this.prFixValue( val ));
	}
	
	valueAction_ { arg val;
		this.setPropertyWithAction( \value, this.prFixValue( val ));
	}
	
	allowsDeselection_ { arg bool;
		if( allowsDeselection != bool, {
			allowsDeselection = bool;
			if( allowsDeselection, {
				if( (this.value == 0) and: { items.size == 0 }, {
					this.valueAction_( nil );
				});
			}, {
				if( this.value.isNil, {
					this.valueAction_( 0 );
				});
			});
		});
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
	
	stringColor {
		^this.getProperty( \stringColor, Color.new );
	}
	
	stringColor_ { arg color;
		this.setProperty( \stringColor, color );
	}
	
	selectedStringColor {
		^this.getProperty( \selectedStringColor, Color.new );
	}
	
	selectedStringColor_ { arg color;
		this.setProperty( \selectedStringColor, color );
	}
	
	hiliteColor {
		^this.getProperty( \hiliteColor, Color.new );
	}
	
	hiliteColor_ { arg color;
		this.setProperty( \hiliteColor, color );
	}
	
	defaultKeyDownAction { arg char, modifiers, unicode;
		var index;
		if( this.value.notNil, {
			if( char == $ , { this.valueAction = this.value + 1; ^this });
			if( char == $\r, { this.enterKeyAction.value(this); ^this });
			if( char == $\n, { this.enterKeyAction.value(this); ^this });
			if( char == 3.asAscii, { this.enterKeyAction.value(this); ^this });
	// JJJ automatically handled by lnf
	//		if( unicode == 16rF700, { this.valueAction = this.value - 1; ^this });
			if( unicode == 16rF703, { this.valueAction = this.value + 1; ^this });
	//		if( unicode == 16rF701, { this.valueAction = this.value + 1; ^this });
			if( unicode == 16rF702, { this.valueAction = this.value - 1; ^this });
		});
		if( char.isKindOf( Char ) and: { char.isAlpha }, {
			char = char.toUpper;
			index = items.detectIndex({ arg item; item.asString.at(0).toUpper >= char });
			if( index.notNil, { this.valueAction = index });
			^this;
		});
		^nil;	// bubble if it's an invalid key
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
		if( allowsDeselection and: { val.isNil }, { ^nil });
		val = (val ? 0).asInteger;
		if( (val < 0) || (val >= items.size), {
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

	prContainerID { ^cnID }

	prInitView {
		cnID = "cn" ++this.id;
		properties.put( \value, 0 );
		
		acResp = OSCpathResponder( server.addr, [ '/list', this.id ], { arg time, resp, msg;
			var newVal;

			newVal = this.prFixValue( if( msg[4] >= 0, msg[4] ));
			if( newVal != this.value, {
				// don't call valueAction coz we'd create a loop
				properties.put( \value, newVal );
				{ this.doAction; }.defer;
			});
		}).add;
		^this.prSCViewNew([
			[ '/set', '[', '/local', this.id, '[', '/new', "de.sciss.swingosc.ListView", ']', ']',
				\selectionMode, 0 ],	// single selection only for compatibility
			[ '/local', "cn" ++ this.id, 	// bars : v=asNeeded, h=never
				'[', '/new', "javax.swing.JScrollPane", '[', '/ref', this.id, ']', 20, 31, ']',
				"ac" ++ this.id,
				'[', '/new', "de.sciss.swingosc.ListResponder", this.id,
					'[', '/array', \selectedIndex, ']', ']' ] // , \valueIsAdjusting
		]);
	}

	prSendProperty { arg key, value;

		key	= key.asSymbol;

		// fix keys
		case { key === \value }
		{
			value = value ? -1;
			if( value >= 0, {
				server.sendBundle( nil,
					[ '/set', this.id, \value, value ],
					[ '/method', this.id, \ensureIndexIsVisible, value ]
				);
			}, {
				server.sendMsg( '/set', this.id, \value, value );
			});
			^nil;
		}
		{ key === \stringColor }
		{
			key = \foreground;
		}
		{ key === \selectedStringColor }
		{
			key = \selectionForeground;
		}
		{ key === \hiliteColor }
		{
			key = \selectionBackground;
		}
		{ key === \items }
		{
			this.prSetItems( value ); // value.performUnaryOp( \asString );
			^nil;
//		}
//		{ key === \bounds }
//		{
//			server.listSendMsg([ '/set', "cn" ++ this.id, key ] ++ this.prBoundsToJava( value ).asSwingArg );
//			^nil;
		};
		^super.prSendProperty( key, value );
	}

	prSetItems { arg items;
		var sizes, dataSize, startIdx, itemArgs, bndl, selectedIdx;

		itemArgs	= items.collect({ arg it; it.asString.asSwingArg }); // necessary to escape plain bracket strings!
		selectedIdx = this.value ? -1;
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
