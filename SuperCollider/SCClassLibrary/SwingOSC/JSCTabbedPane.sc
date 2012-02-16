/*
 *	JSCTabbedPane
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

JSCTabbedPane : JSCContainerView {
	var tabs;		// List -> (IdentityDictionary properties)
	var acResp;	// OSCpathResponder for change listening
	
	// ------------- public general methods -------------

	font_ { arg font;
		this.setProperty( \font, font );
	}
	
	font { ^this.getProperty( \font ); }
	
	tabPlacement_ { arg type;
		this.setProperty( \placement, type );
	}
	
	tabPlacement { ^this.getProperty( \placement ); }
	
	numTabs { ^tabs.size }
	
	value_ { arg index;
		this.setProperty( \value, index );
	}

	valueAction_ { arg index;
		this.setPropertyWithAction( \value, index );
	}	
	
	value { ^this.getProperty( \value ); }

	// ------------- public per tab methods -------------

	setTitleAt { arg index, title;
		this.prSetTabProperty( index, title, \title, \setTitleAt );
	}
	
	getTitleAt { arg index;
		^this.prGetTabProperty( index, \title );
	}

	setEnabledAt { arg index, enabled;
		this.prSetTabProperty( index, enabled, \enabled, \setEnabledAt );
	}

	getEnabledAt { arg index;
		^this.prGetTabProperty( index, \enabled );
	}

	setBackgroundAt { arg index, color;
		this.prSetTabProperty( index, color, \background, \setBackgroundAt );
	}

	getBackgroundAt { arg index;
		^this.prGetTabProperty( index, \background );
	}

	setForegroundAt { arg index, color;
		this.prSetTabProperty( index, color, \foreground, \setForegroundAt );
	}

	getForegroundAt { arg index;
		^this.prGetTabProperty( index, \foreground );
	}

	setToolTipAt { arg index, text;
		this.prSetTabProperty( index, text, \tooltip, \setToolTipTextAt );
	}

	getToolTipAt { arg index;
		^this.prGetTabProperty( index, \tooltip );
	}

	// ------------- private methods -------------
	
	add { arg child;
		var tab;

		tab = IdentityDictionary.new;
		tab.put( \enabled, true );
		tab.put( \component, child );
		tabs.add( tab );
		if( this.value.isNil, { properties.put( \value, 0 )});
		^super.add( child );
	}

	prSetTabProperty { arg index, value, key, javaSelector;
		var tab;
		if( index == -1, {
			tabs.size.do({ arg index; this.prSetTabProperty( index, value, key, javaSelector )});
			^this;
		});
		tab = tabs[ index ];
		if( tab.notNil, {
			tab.put( key, value );
			server.listSendMsg([ '/method', this.id, javaSelector, index ] ++ value.asSwingArg );
		}, {
			this.prMethodError( thisMethod, "Illegal tab index " ++ index ++ " (" ++ key ++ ")" );
		});
	}
	
	prGetTabProperty { arg index, key;
		var tab;	
		tab = tabs[ index ];
		if( tab.notNil, {
			^tab[ key ];
		}, {
			this.prMethodError( thisMethod, "Illegal tab index " ++ index ++ " (" ++ key ++ ")" );
			^nil;
		});
	}

	prRemoveChild { arg child;
		block { arg break;
			tabs.do({ arg tab, index;
				if( tab[ \component ] === child, {
					tabs.removeAt( index );
					break.value;
				});
			});
			this.prMethodError( thisMethod, "Child was not a registered tab : " ++ child );
		};
		^super.prRemoveChild( child );
	}

	prInitView {
		tabs = List.new;
		properties.put( \opaque, false );
		acResp = OSCpathResponder( server.addr, [ '/action', this.id ], { arg time, resp, msg;
			// don't call valueAction coz we'd create a loop
			properties.put( \value, msg[ 4 ]);
			{ this.doAction; }.defer;
		}).add;
		^this.prSCViewNew([
			[ '/local', this.id, '[', '/new', "de.sciss.swingosc.TabbedPane", ']',
				"ac" ++ this.id,
				'[', '/new', "de.sciss.swingosc.ActionResponder", this.id, \selectedIndex, ']' ]
		]);
	}

	prSendProperty { arg key, value;
		key	= key.asSymbol;

		// fix keys
		case
		{ key === \value }
		{
			key 		= \selectedIndexNoAction;
		}
		{ key === \placement }
		{
			key 		= \tabPlacement;
			value	= [ \top, \left, \bottom, \right ].indexOf( value ) + 1;
		};
		^super.prSendProperty( key, value );
	}

	prMethodError { arg methodName, message;
		(this.class.name ++ "." ++ methodName ++ " failed : " ++ message).error;
	}

	prClose { arg preMsg, postMsg;
		acResp.remove;
		^super.prClose( preMsg ++
			[[ '/method', "ac" ++ this.id, \remove ],
			 [ '/free', "ac" ++ this.id ]], postMsg );
	}

//	public void setMnemonicAt(int tabIndex, int mnemonic)
}
