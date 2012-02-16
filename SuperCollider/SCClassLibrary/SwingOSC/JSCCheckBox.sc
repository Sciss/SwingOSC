/*
 *	JSCCheckBox
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
JSCCheckBox : JSCView {
	var acResp;	// OSCpathResponder for action listening

	// ----------------- public class methods -----------------

	*paletteExample { arg parent, bounds;
		^this.new( parent, bounds ).value_( true );
	}
	
	// ----------------- public instance methods -----------------

	value_ { arg val;
		this.setProperty( \value, val );
	}
	
	value { ^this.getProperty( \value ); }
	
	valueAction_ { arg val;
		this.setPropertyWithAction( \value, val );
	}	

	font_ { arg font;
		this.setProperty( \font, font );
	}
	
	font { ^this.getProperty( \font ); }

	string_ { arg string;
		this.setProperty( \string, string );
	}

	string { ^this.getProperty( \string ); }
	
	defaultGetDrag { 
		^this.value;
	}
	
	defaultCanReceiveDrag {
		^currentDrag.isNumber or: { currentDrag.isKindOf( Function ) or: { currentDrag.isKindOf( Boolean )}};
	}
	
	defaultReceiveDrag {
		case
		{ currentDrag.isNumber }
		{
			this.valueAction = currentDrag != 0;
		}
		{ currentDrag.isKindOf( Boolean )}
		{
			this.valueAction = currentDrag;
		}
		{ currentDrag.isKindOf( Function )}
		{
			this.action = currentDrag;
		};
	}

	// ----------------- private instance methods -----------------

	properties {
		^super.properties ++ #[ \value, \font, \string ];
	}
	
	prNeedsTransferHandler {
		^true;
	}

	prClose { arg preMsg, postMsg;
		acResp.remove;
		^super.prClose( preMsg ++
			[[ '/method', "ac" ++ this.id, \remove ],
			 [ '/free', "ac" ++ this.id ]], postMsg );
	}

	prInitView {
		properties.put( \value, false );
		acResp = OSCpathResponder( server.addr, [ '/action', this.id ], { arg time, resp, msg;
			// don't call valueAction coz we'd create a loop
			properties.put( \value, msg[4] != 0 );
			{ this.doAction; }.defer;
		}).add;
		^this.prSCViewNew([
			[ '/local', this.id, '[', '/new', "de.sciss.swingosc.CheckBox", ']',
				"ac" ++ this.id,
				'[', '/new', "de.sciss.swingosc.ActionResponder", this.id, \selected, ']' ]
		]);
	}

	prSendProperty { arg key, value;
		key	= key.asSymbol;

		// fix keys
		case { key === \value }
		{
			key = \selected;
		}
		{ key === \string }
		{
			key = \text;
		};
		^super.prSendProperty( key, value );
	}
}
