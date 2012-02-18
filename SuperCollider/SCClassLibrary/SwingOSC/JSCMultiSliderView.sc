/*
 *	JSCMultiSliderView
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

JSCMultiSliderView : JSCAbstractMultiSliderView { 

	var acResp;	// OSCpathResponder for action listening
	var vlResp;	// OSCpathResponder for value update listening
	var clpse;

	var <gap;
	var <editable = true;
	var <elasticMode = 0;
	var <steady = false, <precision = 0.05;
		
	// ----------------- public class methods -----------------

	*paletteExample { arg parent, bounds;
		^this.new( parent, bounds ).elasticMode_( true )
			.isFilled_( true )
			.value_( Array.fill( 8, { arg i; (i * pi / 8).sin }));
	}

	// ----------------- public instance methods -----------------

	elasticMode_{ arg mode;
		elasticMode = mode;
		this.setProperty( \elasticResizeMode, mode );
	}

	value { // returns array
		^this.getProperty( \value, Array.newClear( this.size ));
	}
	
	value_ { arg val;
		size = val.size;
		this.setProperty( \value, val.copy );
	}

	valueAction_ { arg val;
		size = val.size;	
		this.setPropertyWithAction( \value, val.copy );
	}
	
	reference { // returns array
		^this.getProperty( \referenceValues, Array.newClear( this.size ));
	}
	
	reference_ { arg val;
		// this.size = val.size;
		this.setProperty( \referenceValues, val );
	}
	
	index { // returns selected index
		^this.getProperty( \x );
	}
	
	index_ { arg inx;
		this.setProperty( \x, inx );
	}
	
	fillColor_ { arg acolor; this.setProperty( \fillColor, acolor )}

	colors_ { arg strokec, fillc;
		this.strokeColor_( strokec );
		this.fillColor_( fillc );
	}
	
	isFilled_ { arg abool;
		this.setProperty( \isFilled, abool );
	}
	
	xOffset_ { arg aval;
		this.setProperty( \xOffset, aval );
	}
	
	gap_ { arg inx;
		gap = inx;
		this.setProperty( \xOffset, inx );
	}
	
	startIndex_ { arg val; this.setProperty( \startIndex, val )}
	
	showIndex_ { arg abool; this.setProperty( \showIndex, abool )}
	
	// = thumb width
	indexThumbSize_ { arg val; this.setProperty( \thumbWidth, val )}

	// = thumb height
	valueThumbSize_ { arg val; this.setProperty( \thumbHeight, val )}

	indexIsHorizontal_ { arg val; this.setProperty( \isHorizontal, val )}
	
	thumbSize_ { arg val;
		properties.put( \thumbWidth, val );
		properties.put( \thumbHeight, val );
		server.sendMsg( '/set', this.id, \thumbSize, val );
	}
	
	readOnly_ { arg val;
		editable = val.not;
		this.setProperty( \readOnly, val );
	}
	
	editable_ { arg val;
		editable = val;
		this.setProperty( \readOnly, editable.not );
	}
	
	defaultReceiveDrag {
		if( currentDrag[ 0 ].isSequenceableCollection, { 
			this.value_( currentDrag[ 0 ]);
			this.reference_( currentDrag[ 1 ]);
		}, {
			this.value_( currentDrag );
		});
	}
	
	defaultGetDrag {
		var setsize, vals, rvals, outval;
		rvals = this.reference;
		vals = this.value;
		if( this.selectionSize > 1, {
			vals = vals.copyRange( this.index, this.selectionSize + this.index );
		});
		if( rvals.isNil, { 
			^vals; 
		}, {
			if( this.selectionSize > 1, {
				rvals = rvals.copyRange( this.index, this.selectionSize + this.index );
			});
			outval = outval.add( vals );
			outval = outval.add( rvals );
		});
		^outval;
	}
		
	defaultKeyDownAction { arg key, modifiers, unicode;
		//modifiers.postln; 16rF702
		if (unicode == 16rF703, { this.index = this.index + 1; ^this });
		if (unicode == 16rF702, { this.index = this.index - 1; ^this });
		if (unicode == 16rF700, { this.gap = this.gap + 1; ^this });
		if (unicode == 16rF701, { this.gap = this.gap - 1; ^this });
		^nil		// bubble if it's an invalid key
	}

	steady_ { arg bool;
		if( steady != bool, {
			steady = bool;
			server.sendMsg( \set, this.id, \steady, bool );
		});
	}

	precision_ { arg factor;
		if( precision != factor, {
			precision = factor;
			server.sendMsg( \set, this.id, \precision, factor );
		});
	}
	
	// ----------------- private instance methods -----------------

	properties {
		^super.properties ++ #[ \elasticResizeMode, \fillColor, \thumbWidth, \thumbHeight, \xOffset, \showIndex, \startIndex, \referenceValues, \isFilled, \readOnly ]; // JJJ not \thumbSize, but \thumbHeight, added \readOnly
	}
		
	prClose { arg preMsg, postMsg;
		vlResp.remove;
		acResp.remove;
		clpse.cancel;
		^super.prClose( preMsg ++
			[[ '/method', "ac" ++ this.id, \remove ],
			 [ '/free', "ac" ++ this.id ]], postMsg );
	}

	prInitView {
		var initVal;
		initVal	= 0 ! 8;
		properties.put( \value, initVal );
		properties.put( \x, 0 );
		properties.put( \y, 0.0 );
		properties.put( \step, 0.0 );
		clpse	= Collapse({ this.doAction });
		vlResp	= OSCpathResponder( server.addr, [ '/values', this.id ], { arg time, resp, msg;
			var dirtyIndex, dirtySize, vals, selectedIndex;

			vals			= properties[ \value ];
			dirtyIndex	= min( msg[ 2 ], vals.size );
			dirtySize		= min( msg[ 3 ], vals.size - dirtyIndex );
			
			dirtySize.do({ arg i;
				vals[ dirtyIndex + i ] = msg[ 4 + i ];
			});
			selectedIndex	= this.getProperty( \x, -1 );
//("selectedIndex = "++selectedIndex++"; vals = "++vals).inform;
			if( (selectedIndex >= dirtyIndex) and: { selectedIndex < (dirtyIndex + dirtySize) }, {
				properties.put( \y, vals[ selectedIndex ]);
			});
			if( dirtySize > 0, { clpse.instantaneous });
		}).add;
		acResp = OSCpathResponder( server.addr, [ '/action', this.id ], { arg time, resp, msg;
			var dirtyIndex, dirtySize, vals, selectedIndex;

			selectedIndex	= msg[ 4 ];
			properties.put( \x, selectedIndex );
			properties.put( \selectionSize, msg[ 6 ]);
			dirtyIndex	= msg[ 8 ];
			dirtySize		= msg[ 10 ];

			if( dirtySize == 0, {
				vals = properties[ \value ];
				properties.put( \y, vals[ selectedIndex ]);
				clpse.instantaneous;
			});
		}).add;
		^this.prSCViewNew([
			[ '/local', this.id, '[', '/new', "de.sciss.swingosc.MultiSlider", ']',
				"ac" ++ this.id,
				'[', '/new', "de.sciss.swingosc.ActionMessenger",  // ActionResponder
					this.id, '[', '/array', \selectedIndex, \selectionSize, \dirtyIndex, \dirtySize, ']',
					\sendValuesAndClear, '[', '/array', this.id, ']', ']' ],
			[ '/set', this.id, \values ] ++ initVal.asSwingArg
		]);
	}

	prSendProperty { arg key, value;
		key	= key.asSymbol;

		// fix keys
		switch( key,
		\value, {
			key = \values;
			this.prFixValues;
		},
		\x, {
			key = \selectedIndex;
		},
		\isFilled, {
			key = \filled;
		},
		\isHorizontal, {
			key 		= \orientation;
			value	= if( value, 0, 1 );
		},
		\step, {
			key = \stepSize;
			this.prFixValues;
		});
		^super.prSendProperty( key, value );
	}
	
	prFixValues {
		var val, step;
		
		val	= properties[ \value ];
		step	= this.step;
		if( step > 0, {
			val.size.do({ arg i; val[ i ] = val[ i ].round( step ).clip( 0.0, 1.0 )});
		}, {
			val.size.do({ arg i; val[ i ] = val[ i ].clip( 0.0, 1.0 )});
		});
	}
}
