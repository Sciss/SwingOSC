/*
 *	JSCViews collection 1
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

JSCAbstractMultiSliderView : JSCView { 

	var <>metaAction;
	var <size = 0;
		
	// ----------------- public instance methods -----------------

	step_ { arg stepSize; this.setPropertyWithAction( \step, stepSize )}
	
	step { ^this.getProperty( \step )}
	
	selectionSize { ^this.getProperty( \selectionSize )}

	selectionSize_ { arg aval; this.setProperty( \selectionSize, aval )}

	currentvalue { // returns value of selected index
		^this.getProperty( \y );
	}
	
	strokeColor_ { arg acolor; this.setProperty( \strokeColor, acolor )}

	currentvalue_ { arg iny; this.setProperty( \y, iny )}
	
	drawLines { arg abool; this.setProperty( \drawLines, abool )}

	drawLines_ { arg abool; this.drawLines( abool )}
	
	drawRects_ { arg abool; this.setProperty( \drawRects, abool )}

	doMetaAction { // performed on ctrl click
		metaAction.value( this );
	} 

	// ----------------- private instance methods -----------------

	properties {
		^super.properties ++ #[ \value, \strokeColor, \x, \y, \drawLines, \drawRects, \selectionSize, \step ]; // JJJ not thumbSize, thumbWidth, not absoluteX
	}
	
	defaultCanReceiveDrag {	^true }
			
	prNeedsTransferHandler { ^true }
}
