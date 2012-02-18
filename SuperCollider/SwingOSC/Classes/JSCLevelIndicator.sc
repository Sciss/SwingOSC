/*
 *	JSCLevelIndicator
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

JSCLevelIndicator : JSCView {
	var <value		= 0.0;
	var <warning		= 0.0;
	var <critical		= 0.0;
	var <drawsPeak	= false;
	var <peakLevel	= 0.0;
	var <style		= 0;
	var <numSteps		= 0;
	var <image		= nil;
	var <numTicks		= 0;
	var <numMajorTicks	= 0;
	
	var orientation;

	// ----------------- public class methods -----------------

	*paletteExample { arg parent, bounds;
		^this.new( parent, bounds ).value_( 0.7 );
	}

	// ----------------- public instance methods -----------------

	value_ { arg val;
		val = val.clip( 0, 1 );
		if( value != val, {
			value = val;
			server.sendMsg( \set, this.id, \level, val );
		});
	}

	valueAction_ { arg val;
		this.value_( val );
		this.doAction;
	}

	warning_ { arg val;
		val = val.clip( 0, 1 );
		if( warning != val, {
			warning = val;
			server.sendMsg( \set, this.id, \warning, val );
		});
	}

	critical_ { arg val;
		val = val.clip( 0, 1 );
		if( critical != val, {
			critical = val;
			server.sendMsg( \set, this.id, \critical, val );
		});
	}

	style_ { arg val;
		// XXX not yet implemented
		style = val;
	}

	numSteps_ { arg val;
		val = val.max( 0 );
		// XXX not yet implemented
		numSteps = val;
	}

	image_ { arg val;
		// XXX not yet implemented
		image = val;
	}

	numTicks_ { arg ticks;
		ticks = ticks.max( 0 );
		if( numTicks != ticks, {
			numTicks = ticks;
			server.sendMsg( \set, this.id, \minorTicks, ticks );
		});
	}

	numMajorTicks_ { arg ticks;
		ticks = ticks.max( 0 );
		if( numMajorTicks != ticks, {
			numMajorTicks = ticks;
			server.sendMsg( \set, this.id, \majorTicks, ticks );
		});
	}

	drawsPeak_ { arg bool;
		if( drawsPeak != bool, {
			drawsPeak = bool;
			server.sendMsg( \set, this.id, \peakVisible, bool );
		});
	}

	peakLevel_ { arg val;
		val = val.clip( 0, 1 );
		if( peakLevel != val, {
			peakLevel = val;
			server.sendMsg( \set, this.id, \peak, val );
		});
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

	// ----------------- private instance methods -----------------

	prInitView {
		var b;
		jinsets = Insets.new;	// none
		if( scBounds.isNil, {
			orientation = 0;
		}, {
			b			= this.prBoundsReadOnly;
			orientation	= if( b.width > b.height, 0, 1 );
		});
		^this.prSCViewNew([
			[ '/local', this.id,
				'[', '/new', "de.sciss.swingosc.LevelIndicator", orientation, ']' ]
		]);
	}
}