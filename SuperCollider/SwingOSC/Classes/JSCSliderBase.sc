/*
 *	JSCSliderBase
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

JSCSliderBase : JSCView {

	var <>shift_scale = 100.0, <>ctrl_scale = 10.0, <>alt_scale = 0.1;

	// ----------------- public instance methods -----------------

	getScale { arg modifiers;
		^case
		{ (modifiers & 0x020000) != 0 } { shift_scale }
		{ (modifiers & 0x040000) != 0 } { ctrl_scale }
		{ (modifiers & 0x080000) != 0 } { alt_scale }
		{ 1 };
	}

	knobColor {
		^this.getProperty(\knobColor, Color.new)
	}
	
	knobColor_ { arg color;
		this.setProperty(\knobColor, color)
	}
	
	step_ { arg stepSize;
		this.setPropertyWithAction(\step, stepSize);
	}
	step {
		^this.getProperty(\step)
	}
	
	// ----------------- private instance methods -----------------

	properties {
		^super.properties ++ #[ \knobColor, \step ];
	}

	prSnap { arg val;
		if( this.step <= 0.0, {
			^val.clip( 0.0, 1.0 );
		}, {
			^(val.clip( 0.0, 1.0 ) / this.step).round * this.step;
		});
	}
}
