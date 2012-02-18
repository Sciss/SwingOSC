/*
 *	JSCStaticTextBase
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

JSCStaticTextBase : JSCView {
	var <string, <object, <>setBoth = true;
	
	// ----------------- public class methods -----------------
	
	*paletteExample { arg parent, bounds;
		this.new(parent, bounds).string_( "The lazy brown fox" );
	}

	// ----------------- public instance methods -----------------

	font { ^this.getProperty( \font )}
	
	font_ { arg argFont;
//		font = argFont;
		this.setProperty( \font, argFont );
	}
	
	string_ { arg argString;
		string = argString.asString;
		this.setProperty(\string, string)
	}
	align_ { arg align;
		this.setProperty(\align, align)
	}
	
	stringColor {
		^this.getProperty(\stringColor, Color.new)
	}
	stringColor_ { arg color;
		this.setProperty(\stringColor, color)
	}

	object_ { arg obj;
		object = obj;
		if( setBoth, { this.string = object.asString( 80 )});
	}
	
	// ----------------- private instance methods -----------------

	properties {
		^super.properties ++ #[ \string, \font, \stringColor ];
	}

	prSendProperty { arg key, value;

		key	= key.asSymbol;

		// fix keys
		case { key === \stringColor }
		{
			key = \foreground;
		}
		{ key === \align }
		{
			key = \horizontalAlignment;
			case { value === \left }
			{
				value = 2;
			}
			{ value === \center }
			{
				value = 0;
			}
			{ value === \right }
			{
				value = 4;
			}
			// undocumented cocoa feature : -1 = left, 0 = center, 1 = right
			{ value.isKindOf( SimpleNumber )}
			{
				value = switch( value.sign, -1, 2, 0, 0, 1, 4 );
			};
		}
		{ key === \string }
		{
			key = \text;
//			value = value.asSwingArg;
// funktioniert nicht, weil BoundedRangeModel offensichtlich nochmal durch setText veraendert wird
//			server.sendBundle( nil,
//				[ '/set', this.id, \text, value ],	// make sure the text beginning is shown
//				[ "/methodr", [ '/method', this.id, \getHorizontalVisibility ], \setValue, 0 ]
//			);
//			^nil;
		};
		^super.prSendProperty( key, value );
	}
}
