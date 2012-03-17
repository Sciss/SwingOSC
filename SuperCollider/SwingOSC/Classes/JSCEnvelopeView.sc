/*
 *	JSCEnvelopeView
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

JSCEnvelopeView : JSCAbstractMultiSliderView {
	var allConnections, selection;
	var items;
	var connectionsUsed = false;
	var idx = 0;	// the one that corresponds to select, x_ and y_
	
	var acResp;
	var vlResp;
	var clpse;
	
	var <horizontalEditMode = \free;

// rather useless behaviour in SCEnvelopeView (using shift+click you can ignore it),
// so keep it out for now
//	var <fixedSelection = false;
	
	// ----------------- public class methods -----------------

	*paletteExample { arg parent, bounds;
		^this.new( parent, bounds )
			.value_([ (0..7)/7, Array.fill( 8, { arg i; (i * pi / 8).sin })])
			.thumbSize_( 4 ).selectionColor_( Color.red )
			.horizontalEditMode_( \relay ).lockBounds_( true );
	}

	// ----------------- public instance methods -----------------

	value_ { arg val;
		var oldSize, xvals, yvals, curves, valClip;
		
		oldSize	= size;
		xvals	= val[ 0 ];
		yvals	= val[ 1 ];
		curves	= val[ 2 ];
		if( xvals.size != yvals.size, {
			Error( "JSCEnvelopeView got mismatched times/levels arrays" ).throw;
		});
		size 	= xvals.size;
		case
		{ oldSize < size }
		{
			if( allConnections.notNil, {
				allConnections = allConnections.growClear( size );
			});
			if( items.notNil, {
				items = items.growClear( size );
			});
			selection = selection.growClear( size ).collect({ arg sel; if( sel.isNil, false, sel )});
		}
		{ oldSize > size }
		{
			if( allConnections.notNil, {
				allConnections = allConnections.copyFromStart( size - 1 );
			});
			if( items.notNil, {
				items = items.copyFromStart( size - 1 );
			});
			selection = selection.copyFromStart( size - 1 );
		};

		xvals =  xvals.collect(_.clip( 0.0, 1.0 ));
		yvals =  yvals.collect(_.clip( 0.0, 1.0 ));
		if( curves.isNil, {
			valClip = [ xvals, yvals ];
		}, {
			valClip = [ xvals, yvals, curves.asArray.clipExtend( size )];
		});
		this.setProperty( \value, valClip );
	}
	
	setString { arg index, astring;
		if( items.isNil, {
			items = Array.newClear( size );
		});
		if( index < 0, {
			items.fill( astring );
		}, { if( index < size, {
			items[ index ] = astring;
		})});
		// items = items.add( astring );
//		this.setProperty( \string, [ index, astring ]);
		server.listSendMsg([ '/method', this.id, \setLabel, index ] ++ astring.asSwingArg );
	}

	strings_ { arg astrings;
		astrings.do({ arg str,i;
//			this.string_( i, str );
			this.setString( i, str );
		});
	}
	
	strings {
		^items.copy;	// nil.copy allowed
	}
	
//	items_ { arg items; ^this.strings_( items )}
	
	value {
//		var ax, ay, axy;
//		ax = Array.newClear( this.size );
//		ay = Array.newClear( this.size );
//		axy = Array.with( ax, ay );
//		^this.getProperty( \value, axy );
		^properties[ \value ].deepCopy;
	}
	
	selection {
		^selection.copy;
	}
	
	connections {
		var result;
		if( allConnections.isNil, { ^nil });
		
		result = Array( allConnections.size );
		allConnections.do({ arg cons; result.add( cons.copy )});
		^result;
	}
	
	setThumbHeight { arg index, height;
//		this.setProperty( \thumbHeight, [ index, height ]);
		server.sendMsg( '/method', this.id, \setThumbHeight, index, height );
	}
	
	thumbHeight_ { arg height; this.setThumbHeight( -1, height )}
	
	setThumbWidth { arg index, width;
//		this.setProperty( \thumbWidth, [ index, width ]);
		server.sendMsg( '/method', this.id, \setThumbWidth, index, width );
	}

	thumbWidth_ { arg width; this.setThumbWidth( -1, width )}

	setThumbSize { arg index, size;
//		this.setProperty(\thumbSize, [index, size]);
		server.sendMsg( '/method', this.id, \setThumbSize, index, size );
	}
	
	thumbSize_ { arg size; this.setThumbSize( -1, size )}

	setFillColor { arg index, color;
//		this.setProperty(\fillColor, [index, color]);
		server.listSendMsg([ '/method', this.id, \setFillColor, index ] ++ color.asSwingArg );
	}

	fillColor_ { arg color; this.setFillColor( -1, color )}

	colors_ { arg strokec, fillc;
		this.strokeColor_( strokec );
		this.fillColor_( fillc );
	}

	curves_ { arg curves;
		var value;
		if( curves.isArray, {
			value = properties[ \value ];
//			curves.asArray.clipExtend( size ).postln;
			value = [ value[ 0 ], value[ 1 ], curves.asArray.clipExtend( size )];
			this.setProperty( \value, value );
		}, {
			this.setCurve( -1, curves );
		});
	}
	
	setEnv { arg env, minValue, maxValue, minTime, maxTime;
		var times, levels;
		var spec;

		times		= [ 0.0 ] ++ env.times.integrate;
		maxTime		= maxTime ? times.last;
		minTime		= minTime ? 0.0;
		levels		= env.levels;
		minValue		= minValue ? levels.minItem;
		maxValue		= maxValue ? levels.maxItem;
		
		levels		= levels.linlin( minValue, maxValue, 0, 1 );
		times		= times.linlin( minTime, maxTime, 0, 1 );
		
		this.value_([ times.asFloat, levels.asFloat, env.curves ]);
	}

	editEnv { arg env, minValue, maxValue, duration;
		var vals, levels, times, viewDur;
		vals		= this.value;
		times	= vals[ 0 ].differentiate.copyToEnd( 1 );
		viewDur	= vals[ 0 ].last - vals[ 0 ].first;
		if( viewDur > 0, {
			times = times / viewDur * (duration ? 1.0);
		});
		levels	= vals[ 1 ].linlin( 0, 1, minValue, maxValue );
		env.times_( times );
		env.levels_( levels );
		env.curves_( vals[ 2 ] ? \lin );
	}

	asEnv { arg minValue, maxValue, duration;
		var env;
		env = Env.new;
		this.editEnv( env, minValue, maxValue, duration );
		^env;
	}
	
	curve_ { arg curve = \lin; this.setCurve( -1, curve )}
	
	setCurve { arg index, curve = \lin;
		var shape, value, curves;
		value  = properties[ \value ];
		curves = value[ 2 ];
		if( index == -1, {
			if( curves.notNil, {
				curves.fill( curve );
			}, {
				properties[ \value ] = value ++ [(curve ! value[1].size)];
			});
		}, { if( index < size, {
			if( curves.notNil, {
				curves[ index ] = curve;
			}, {
				properties[ \value ] = value ++ [(\lin ! value[1].size).put( index, curve )];
			});
		})});
		if( curve.isNumber, {
			shape = 5;
			curve = curve.asFloat;
		}, {
			shape = Env.shapeNames[ curve ] ? 0;
			curve = 0.0;
		});
		server.sendMsg( '/method', this.id, \setShape, index, shape, curve );
	}
	
	lockBounds_ { arg val; this.setProperty( \lockBounds, val )}
	
	horizontalEditMode_ { arg val; 
		horizontalEditMode = val;
		this.setProperty( \horizontalEditMode, val );
	}
	
	keepHorizontalOrder { ^horizontalEditMode === \clamp }
	keepHorizontalOrder_ { arg bool;
		this.horizontalEditMode = if( bool, \clamp, \free );
	}
	
	elasticSelection { ^true }
	elasticSelection_ { arg bool;
		if( bool.not, {
			"JSCEnvelopeView:elasticSelection_(false) -- not yet implemented".warn;
		});
	}
	
	connect { arg from, aconnections;
		var bndl, target, targetCons, fromCons;

		if( (from < 0) || (from >= size), { ^this });

		bndl			= Array( aconnections.size + 1 ); // max. number of messages needed
		fromCons		= Array( aconnections.size );

		if( connectionsUsed.not, {
			bndl.add([ '/set', this.id, \connectionsUsed, true ]);
			connectionsUsed	= true;
			allConnections	= Array.newClear( size );
		});

		aconnections.do({ arg target;
			target = target.asInteger;
			if( (target >= 0) && (target < size) && (target != from), {
				fromCons.add( target );
				targetCons = allConnections[ target ];
				if( targetCons.isNil or: { targetCons.includes( from ).not }, {
					targetCons = targetCons ++ [ from ];
					allConnections[ target ] = targetCons;
					// don't draw connections twice, so simply set only connections on the server whose target idx is greater than from idx
					bndl.add([ '/method', this.id, \setConnections, target ] ++ targetCons.reject({ arg idx; idx < target }).asSwingArg );
				});
			});
		});
		allConnections[ from ] = fromCons;
		bndl.add([ '/method', this.id, \setConnections, target ] ++ fromCons.reject({ arg idx; idx < from }).asSwingArg );
		server.listSendBundle( nil, bndl );
	}

	select { arg index; // this means no refresh;
		var vals;
//		this.setProperty(\setIndex, index);
		idx = index;
		if( (idx >= 0) && (idx < size), {
			vals = properties[ \value ];
			properties.put( \x, vals[ 0 ][ index ]);
			properties.put( \y, vals[ 1 ][ index ]);
		});
		server.sendMsg( '/set', this.id, \index, index );
	}
	
	selectIndex { arg index; // this means that the view will be refreshed
//		this.setProperty( \selectedIndex, index );
		properties.put( \selectedIndex, index );
		if( (idx >= 0) && (idx < size), {
			selection[ index ] = true;
		});
		server.sendMsg( '/method', this.id, \setSelected, index, true );
	}
	
	deselectIndex { arg index; // this means that the view will be refreshed
//		properties.put( \selectedIndex, index );
		if( (idx >= 0) && (idx < size), {
			selection[ index ] = false;
		});
		server.sendMsg( '/method', this.id, \setSelected, index, false );
	}
	
	x { ^this.getProperty( \x )}  // returns selected x
	y { ^this.getProperty( \y )}

	x_ { arg ax;
		ax = ax.round( this.step ).clip( 0.0, 1.0 );
		if( idx == -1, {
			properties[ \value ][ 0 ].fill( ax );
		}, { if( idx < size, {
			properties[ \value ][ 0 ][ idx ] = ax;
		})});
		this.setProperty( \x, ax );
	}

	y_ { arg ay;
		ay = ay.round( this.step ).clip( 0.0, 1.0 );
		if( idx == -1, {
			properties[ \value ][ 1 ].fill( ay );
		}, { if( idx < size, {
			properties[ \value ][ 1 ][ idx ] = ay;
		})});
		this.setProperty( \y, ay )
	}

	index { ^this.getProperty( \selectedIndex )}

//	lastIndex { ^this.getProperty( \lastIndex )}

	setEditable { arg index, boolean;
//		this.setProperty(\editable, [index,boolean]);
		server.sendMsg( '/method', this.id, \setReadOnly, index, boolean.not );
	}

	editable_{ arg boolean; this.setEditable( -1, boolean )}	
	selectionColor_ { arg acolor; this.setProperty( \selectionColor, acolor )}
	
// currently broken in cocoa
/*
	addValue { arg xval, yval;
		var arr, arrx, arry, aindx;
		// XXX could use custom server method!!
		aindx = this.lastIndex;
//		aindx.postln;
		if( xval.isNil && yval.isNil, {
			arr = this.value;
			arrx = arr @ 0;
			arry = arr @ 1;
			xval = arrx[ aindx ] + 0.05;
			yval = arry[ aindx ];
		});
		if( aindx < (arrx.size - 1), {
			arrx = arrx.insert( aindx + 1, xval );
			arry = arry.insert( aindx + 1, yval );
		}, {
			arrx = arrx.add( xval );
			arry = arry.add( yval );
		});		
		this.value_([ arrx, arry ]);
	}
*/

// see comment for <fixedSelection	
//	fixedSelection_ { arg bool;
//		fixedSelection =  bool;
//		this.setProperty(\setFixedSelection, bool);
//	}

	font { ^this.getProperty( \font )}

	font_ { arg argFont;
		this.setProperty( \font, argFont );
	}
	
	clipThumbs { ^this.getProperty( \clipThumbs )}

	clipThumbs_ { arg bool; this.setProperty( \clipThumbs, bool )}

	defaultGetDrag { ^this.value }

// currently broken in cocoa
/*
	defaultReceiveDrag {
		if( currentDrag.isString, {
			this.addValue;
//			items = items.insert( this.lastIndex + 1, currentDrag );
//			this.strings_( items );
			this.setString( this.lastIndex + 1, currentDrag );
		}, {
			this.value_( currentDrag );
		});
	}
*/
	defaultReceiveDrag { }
	
	defaultKeyDownAction { arg key, modifiers, unicode;
		var oldIdx, selIdx;

// gap is not working with envelope view!
//		if (unicode == 16rF700, { this.gap = this.gap + 1; ^this });
//		if (unicode == 16rF701, { this.gap = this.gap - 1; ^this });

		if( (unicode >= 16rF700) && (unicode <= 16rF703), {  // cursor
			selIdx	= this.index;
			oldIdx	= idx;
			if( (selIdx >= 0) and: { selIdx - 1 < this.size }, {
				case
				{ unicode == 16rF703 }	// cursor right
				{
					if( (modifiers & 524288) == 0, {	// test for alt
						this.select( selIdx );
						this.x = this.x + max( this.step, 0.015625 );
						this.select( oldIdx );
					}, { if( (selIdx + 1) < this.size, {
						this.deselectIndex( selIdx );
						this.selectIndex( selIdx + 1 );
					})});
				}
				{ unicode == 16rF702 }	// cursor left
				{
					if( (modifiers & 524288) == 0, {	// test for alt
						this.select( selIdx );
						this.x = this.x - max( this.step, 0.015625 );
						this.select( oldIdx );
					}, { if( selIdx > 0, {
						this.deselectIndex( selIdx );
						this.selectIndex( selIdx - 1 );
					})});
				}
				{ unicode == 16rF700 }	// cursor up
				{
					this.select( selIdx );
					this.y = this.y + max( this.step, 0.015625 );
					this.select( oldIdx );
				}
				{ unicode == 16rF701 }	// cursor down
				{
					this.select( selIdx );
					this.y = this.y - max( this.step, 0.015625 );
					this.select( oldIdx );
				};
			});
			^this;
		});
		^nil;		// bubble if it's an invalid key
	}

	// ----------------- private instance methods -----------------

	properties {
		^super.properties ++ #[ \font, \selectedIndex, \clipThumbs, \lockBounds, \horizontalEditMode ];  // \lastIndex
	}

	prClose { arg preMsg, postMsg;
		vlResp.remove;
		acResp.remove;
		clpse	= Collapse({ this.doAction });
		^super.prClose( preMsg ++
			[[ '/method', "ac" ++ this.id, \remove ],
			 [ '/free', "ac" ++ this.id ]], postMsg );
	}

	prInitView {
		var initVal;
//		initVal	= nil ! 8 ! 2;	// pretty stupid
		initVal	= [[],[]];
		properties.put( \value, initVal );
		properties.put( \index, -1 );
		properties.put( \x, 0.0 );
		properties.put( \y, 0.0 );
//		properties.put( \lastIndex, -1 );	// 0 in cocoa ...
		properties.put( \selectedIndex, -1 );
		properties.put( \step, 0.0 );
		properties.put( \clipThumbs, false );
		selection	= [];
		clpse	= Collapse({ this.doAction });
//		items	= Array.new;
		vlResp	= OSCpathResponder( server.addr, [ '/values', this.id ], { arg time, resp, msg;
			var dirtySize, vals, xvals, yvals, action = false;

			vals			= properties[ \value ];
			xvals		= vals[ 0 ];
			yvals		= vals[ 1 ];
			dirtySize		= msg[ 2 ];
			msg.copyToEnd( 3 ).clump( 4 ).do({ arg entry; var idx, x, y, sel;
				#idx, x, y, sel = entry;
				if( idx < xvals.size, {
					xvals[ idx ]		= x;
					yvals[ idx ]		= y;
					selection[ idx ]	= sel != 0;
					action			= true;
				});
			});
			if( action, { clpse.instantaneous });
		}).add;
		acResp = OSCpathResponder( server.addr, [ '/action', this.id ], { arg time, resp, msg;
			var lastIndex, dirtySize;

			lastIndex	= msg[ 4 ];
			if( lastIndex >= 0, { properties.put( \selectedIndex, lastIndex )}); // \lastIndex
			dirtySize	= msg[ 5 ];

//			if( dirtySize == 0, {
//				vals = properties[ \value ];
//				properties.put( \y, vals[ selectedIndex ]);
//				clpse.instantaneous;
//			});
		}).add;
		^this.prSCViewNew([
			[ '/local', this.id, '[', '/new', "de.sciss.swingosc.EnvelopeView", false, ']',
				"ac" ++ this.id,
				'[', '/new', "de.sciss.swingosc.ActionMessenger",  // ActionResponder
					this.id, '[', '/array', \lastIndex, \dirtySize, ']',
					\sendDirtyValuesAndClear, '[', '/array', this.id, ']', ']' ],
		]);
	}

	prSendProperty { arg key, value;
		var ival, shapes, curvesSC, curves;
		
		key	= key.asSymbol;

		// fix keys
		switch( key,
		\value, {
			this.prFixValues;
			if( value.size < 3, {
				// XXX should check against max bundle size
				server.listSendMsg([ '/method', this.id, \setValues ] ++ value[ 0 ].asSwingArg ++ value[ 1 ].asSwingArg );
			}, {
				curvesSC = value[ 2 ];
				if( curvesSC.isArray, {
					shapes = Array( curvesSC.size );
					curves = Array( curvesSC.size );
					curvesSC.do({ arg curve;
						if( curve.isNumber, {
							shapes.add( 5 );
							curves.add( curve.asFloat );
						}, {
							shapes.add( Env.shapeNames[ curve ] ? 0 );
							curves.add( 0.0 );
						});
					});
				}, {
					if( curvesSC.isNumber, {
						shapes = 5;
						curves = curvesSC.asFloat;
					}, {
						shapes = Env.shapeNames[ curvesSC ] ? 0;
						curves = 0.0;
					});
				});
				// XXX should check against max bundle size
				server.listSendMsg([ '/method', this.id, \setValues ] ++ value[ 0 ].asSwingArg ++ value[ 1 ].asSwingArg ++ shapes.asSwingArg ++ curves.asSwingArg );
			});
			^this;
		},
		\step, {
			key = \stepSize;
			this.prFixValues;
		},
		\horizontalEditMode, {
			ival = [ \free, \clamp, \relay ].indexOf( value );
			if( ival.isNil, { Error( "Illegal edit mode '" ++ value ++ "'" ).throw });
			value = ival;
		});
		^super.prSendProperty( key, value );
	}

	prFixValues {
		var val, step;
		
		val	= properties[ \value ];
		step	= this.step;
		if( step > 0, {
			2.do({ arg j; var xyvals = val[ j ]; xyvals.size.do({ arg i; xyvals[ i ] = xyvals[ i ].round( step ).clip( 0.0, 1.0 )})});
		}, {
			2.do({ arg j; var xyvals = val[ j ]; xyvals.size.do({ arg i; xyvals[ i ] = xyvals[ i ].clip( 0.0, 1.0 )})});
		});
	}

	prShapeNumber { arg name; ^Env.shapeNames.at( name ) ? 5 }
}
