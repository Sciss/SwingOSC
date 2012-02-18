/*
 *	JSCAbstractUserView
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

JSCAbstractUserView : JSCView {
	var <drawFunc;
	var <clearOnRefresh = true;
	var <>refreshOnFocus = true;
	var <>lazyRefresh   = true;

	var penID			= nil;
	var pendingDraw	= false;
	var routRefresh, condRefresh;

	// ----------------- public instance methods -----------------

	refresh {
		pendingDraw = false;
		if( drawFunc.notNil, { this.protRefresh });
	}
		
	clearDrawing {
		server.sendMsg( '/method', this.id, \clearDrawing );
	}
	
	clearOnRefresh_{ arg bool;
		clearOnRefresh = bool;
		this.setProperty( \clearOnRefresh, bool );
	}

	drawFunc_ { arg func;
		if( drawFunc.isNil, {
			if( func.notNil, {
				penID	= server.nextNodeID;
				server.sendBundle( nil,
					[ '/local', penID, '[', '/new', "de.sciss.swingosc.Pen", '[', '/ref', this.id, ']', relativeOrigin.not, ']' ],
					[ '/method', this.id, \setPen, '[', '/ref', penID, ']' ]
				);
			}, {
				^this;
			});
		}, {
			if( func.isNil, {
				server.sendBundle( nil,
					[ '/method', this.id, \setPen, '[', '/ref', \null, ']' ],
					[ '/method', penID, \dispose ],
					[ '/free', penID ]
				);
				penID = nil;
				drawFunc = nil;
				pendingDraw = false;
				^this;
			});
		});
		drawFunc = func;
		if( this.prAllVisible, {
			pendingDraw = false;
			this.protRefresh;
		}, {
			pendingDraw = true;
		});
	}
	
	focusVisible { ^this.getProperty( \focusVisible, true )}
	focusVisible_ { arg visible; this.setProperty( \focusVisible, visible )}
	
	focusColor_ { arg colr;
		if( (colr.alpha > 0) != this.focusVisible, {
			this.focusVisible = colr.alpha > 0;
		});
		^super.focusColor_( colr );
	}

	// ----------------- private instance methods -----------------

//	draw {
//		this.refresh;
//	}

	prSCViewNew { arg preMsg, postMsg;
		condRefresh = Condition.new;
		routRefresh = Routine({
			inf.do({
				condRefresh.wait;
				condRefresh.test = false;
				if( drawFunc.notNil, {
					try {
						JPen.protRefresh( drawFunc, this, server, penID, this.id )
					} { arg error;
						error.reportError;
					};
				});
				0.01.wait;
			});
		}).play( AppClock );
		^super.prSCViewNew( preMsg, postMsg );
	}

	prFocusChange {
		// the user may wish to paint differently according to the focus
		if( refreshOnFocus, { this.protDraw });
	}
	
	prVisibilityChange {
		if( pendingDraw, { this.protDraw });
	}

	prBoundsUpdated {
		this.protDraw;
	}

	prClose { arg preMsg, postMsg;
//		routRefresh.cancel;
		routRefresh.stop;
		this.drawFunc_( nil );
		^super.prClose( preMsg, postMsg );
	}

	protRefresh {
		if( lazyRefresh, {
//			routRefresh.instantaneous;
			condRefresh.test = true; condRefresh.signal;
		}, {
			JPen.protRefresh( drawFunc, this, server, penID, this.id );
		});
	}

	protDraw {
		if( drawFunc.notNil and: { this.prAllVisible }, {
//			// cmpID == nil --> don't repaint, because this
//			// will be done already by JSCWindow, and hence
//			// would slow down refresh unnecessarily (???)
//			JPen.protRefresh( drawFunc, this, server, penID, nil );
//			JPen.protRefresh( drawFunc, this, server, penID, this.id );
			this.protRefresh;
		});
	}
}
