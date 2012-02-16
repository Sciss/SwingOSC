/*
 *	JSCScrollView
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

JSCScrollView : JSCContainerView {
	var <autohidesScrollers = true, <hasHorizontalScroller = true, <hasVerticalScroller = true;
	var <autoScrolls = true;
	var vpID, chResp;

	var viewX = 0, viewY = 0, viewW = 0, viewH = 0;
	
	var <hasBorder = false;

	// ----------------- public instance methods -----------------

	hasBorder_ { arg bool = true;
		if( hasBorder != bool, {
			hasBorder = bool;
			this.setProperty( \border, bool );
		});
	}

	autohidesScrollers_ { arg bool;
		var hPolicy, vPolicy;
		autohidesScrollers = bool;
		hPolicy = JSCScrollView.protCalcPolicy( bool, hasHorizontalScroller ) + 30;
		vPolicy = JSCScrollView.protCalcPolicy( bool, hasVerticalScroller ) + 20;

		server.sendMsg( '/set', this.id, \horizontalScrollBarPolicy, hPolicy, \verticalScrollBarPolicy, vPolicy );
	}
	
	hasHorizontalScroller_ { arg bool;
		var policy;
		hasHorizontalScroller = bool;
		policy = JSCScrollView.protCalcPolicy( autohidesScrollers, bool ) + 30;
		server.sendMsg( '/set', this.id, \horizontalScrollBarPolicy, policy );
	}
	
	hasVerticalScroller_ { arg bool;
		var policy;
		hasVerticalScroller = bool;
		policy = JSCScrollView.protCalcPolicy( autohidesScrollers, bool ) + 20;
		server.sendMsg( '/set', this.id, \verticalScrollBarPolicy, policy );
	}

	visibleOrigin_ { arg point;
		viewX	= point.x;
		viewY	= point.y;
		server.sendMsg( '/method', this.id, \setViewPosition, point.x, point.y );
		this.doAction;
	}
	
	visibleOrigin {
		^Point( viewX, viewY );
	}
	
	autoScrolls_ { arg bool;
		"JSCScrollView.autoScrolls_ : not yet implemented".warn;
		autoScrolls = bool;
//		server.sendMsg( '/set', this.id, \autoScrolls, bool );
	}
	
	innerBounds {
		^Rect( 0, 0, viewW, viewH );
	}

	// ----------------- private class methods -----------------

	*protCalcPolicy { arg auto, has;
//		autohidesScrollers			1	0	1	0
//		hasHorizontalScroller		1	1	0	0
//		--------------------------------------------
//		horizontalScrollBarPolicy	0	2	1	1	+ 30
		^(has.not.binaryValue | ((auto.not && has).binaryValue << 1));
	}
	
	// ----------------- private instance methods -----------------

	prGetRefTopLeft {
		^Point( 0, 0 );
	}

	prVisibleChild { arg pre, post, child;
		var vpID;
		if( this.prAllVisible, {
			vpID = this.prViewPortID;
			if( this.id != vpID, {
				post.add([ '/method', vpID, \validate ]);
			});
			post.add([ '/method', this.id, \revalidate ]);
			pendingValidation = false;
		}, {
			pendingValidation = true;
		});
	}

	prClose { arg preMsg, postMsg;
		chResp.remove;
		^super.prClose( preMsg ++
			[[ '/method', "ch" ++ this.id, \remove ],
		      [ '/free', "ch" ++ this.id ]], postMsg );
	}

	prInitView {
		chResp	= OSCpathResponder( server.addr, [ '/change', this.id ], { arg time, resp, msg;
			var newVal;
		
			// [ /change, 1001, performed, viewX, ..., viewY, ..., innerWidth, ..., innerHeight, ... ]

			viewW = msg[8];
			viewH = msg[10];
			if( viewX != msg[4] or: { viewY != msg[6] }, {
				viewX = msg[4];
				viewY = msg[6];
				{ this.doAction }.defer;
			});
		}).add;
		vpID = "vp" ++ this.id;
		^this.prSCViewNew([
			[ '/local', vpID, '[', '/new', "de.sciss.swingosc.ContentPane", 0, ']',
			  this.id, '[', '/new', "de.sciss.swingosc.ScrollPane", '[', '/ref', vpID, ']', ']',
 			  "ch" ++ this.id, '[', '/new', "de.sciss.swingosc.ChangeResponder", this.id,
 			  	'[', '/array', \viewX, \viewY, \innerWidth, \innerHeight, ']', ']' ]]);
	}

	prInit { arg ... args;
		var result;
		result = super.prInit( *args );
		vpID = "vp" ++ this.id;
		server.sendMsg( '/local', vpID, '[', '/methodr', '[', '/method', this.id, \getViewport, ']', \getView, ']' );
	}

	prViewPortID { ^vpID }
}
