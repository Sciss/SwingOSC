/*
 *	JSCContainerView
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

JSCContainerView : JSCView { // abstract class
	var <children, <decorator;
	var pendingValidation = false;
			
	// ----------------- public instance methods -----------------

	removeAll {
		children.copy.do({ arg child; child.remove });
	}
	
	relativeOrigin_ { arg bool;
		relativeOrigin = bool;
//		this.setProperty(\relativeOrigin, bool);
		this.prInvalidateBounds;
	}
	
		// this is a TEMPORARY method
		// will be removed when relativeOrigin variable is permanently banished
	prRelativeOrigin { ^relativeOrigin }
	
	addFlowLayout { arg margin, gap;
		this.relativeOrigin.if
			{this.decorator_( FlowLayout( this.bounds.moveTo(0,0), margin, gap ) )}
			{this.decorator_( FlowLayout( this.bounds, margin, gap ) )};
		^this.decorator;
	}

	decorator_ { arg decor;
		if( relativeOrigin and: { decor.notNil }, {
			decor.bounds = decor.bounds.moveTo( 0, 0 );
			decor.reset;
		});
		decorator = decor;
	}

	// ----------------- quasi-interface methods : crucial-lib support -----------------

	asPageLayout { arg title, bounds;
		// though it won't go multi page
		// FlowView better ?
		^MultiPageLayout.on( this, bounds );
	}


	flow { arg func, bounds;
		var f, comp;
		f = FlowView( this, bounds /*?? { this.bounds }*/ );
		func.value( f );
		f.resizeToFit;
		^f;
	}
	
	horz { arg func, bounds;
		var comp;
		comp = JSCHLayoutView.new( this, bounds ?? { this.bounds });
		func.value( comp );
		^comp;
	}
	
	vert { arg func, bounds;
		var comp;
		comp = JSCVLayoutView.new( this, bounds ?? { this.bounds });
		func.value( comp );
		^comp;
	}
	
	comp { arg func, bounds;
		var comp;
		comp = JSCCompositeView.new( this, bounds ?? { this.bounds });
		func.value( comp );
		^comp;
	}		

	// ----------------- private instance methods -----------------

	prViewPortID { ^id }	// actually this refers to a viewport-view!! we should fuse that with the containerID concept XXX
	prChildOrder { arg child; ^-1 }

	prGetRefTopLeft {
// more efficient but too difficult to maintain
//		var refTopLeft;
//		var pTopLeft;
//		if( scBounds.isNil, {
//			// need to revalidate bounds
//			pTopLeft		= parent.prGetRefTopLeft;
//			refTopLeft	= jBounds.moveBy( pTopLeft.x, pTopLeft.y );
//			scBounds		= jinsets.addTo( refTopLeft );
//			^refTopLeft;
//		}, {
//			^(scBounds.leftTop - jinsets.leftTop);
//		});
//		^(this.bounds.leftTop - jinsets.leftTop);
		^if( relativeOrigin, {
			Point( jinsets.left.neg, jinsets.top.neg )
		}, {
			this.prBoundsReadOnly.leftTop - jinsets.leftTop
		});
	}

	prAddAllTopLeft { arg rect;
		^parent.prAddAllTopLeft( rect.moveBy( jBounds.left, jBounds.top ));
	}

	add { arg child;
		var bndl, vpID;
		
		children = children.add( child );
		if( decorator.notNil, { decorator.place( child )});

		if( child.id.notNil, {
			vpID = this.prViewPortID;
			bndl = Array( 4 );
			bndl.add([ '/method', vpID, \add, '[', '/ref', child.prContainerID, ']', this.prChildOrder( child )]);
			if( this.prAllVisible, {
				if( this.id != vpID, {
					bndl.add([ '/method', vpID, \validate ]);
				});
				bndl.add([ '/method', this.id, \revalidate ]);
				bndl.add([ '/method', child.id, \repaint ]);
				pendingValidation = false;
			}, {
				pendingValidation = true;
			});
			server.listSendBundle( nil, bndl );
		});
	}
	
	prInvalidateBounds {
		scBounds = nil;
		children.do({ arg child;
//			child.prSetScBounds( nil );
			child.prInvalidateBounds;
		});
	}

	prInvalidateAllVisible {
		allVisible = nil;
		children.do({ arg child;
			child.prInvalidateAllVisible;
		});
	}

	prVisibilityChange { arg pre, post;
		var vpID;
		if( pendingValidation, {
			if( this.prAllVisible, {
				vpID = this.prViewPortID;
				if( this.id != vpID, {
					post.add([ '/method', vpID, \validate ]);
				});
				post.add([ '/method', this.id, \revalidate ]);
				post.add([ '/method', this.id, \repaint ]);
				pendingValidation = false;
			});
		});
		children.do({ arg child;
			child.prVisibilityChange( pre, post );
		});
	}

	prRemoveChild { arg child;
		var bndl, vpID;
		
		children.remove( child );
		bndl = Array( 4 );
		vpID = this.prViewPortID;
		bndl.add([ '/method', vpID, \remove, '[', '/ref', child.prContainerID, ']' ]);
		if( this.prAllVisible, {
			if( this.id != vpID, {
				bndl.add([ '/method', vpID, \validate ]);
			});
			bndl.add([ '/method', this.id, \revalidate ]);
			bndl.add([ '/method', this.id, \repaint ]);
			pendingValidation = false;
		}, {
			pendingValidation = true;
		});
		server.listSendBundle( nil, bndl );
		// ... decorator replace all
	}
	//bounds_  ... replace all

	prMoveChild { arg bndl, child;
		var vpID;
		if( child.prAllVisible, {
			vpID = this.prViewPortID;
			if( this.id != vpID, {
				bndl.add([ '/method', vpID, \validate ]);
			});
			bndl.add([ '/method', this.id, \revalidate ]);
			bndl.add([ '/method', child.id, \repaint ]);
			pendingValidation = false;
		}, {
			pendingValidation = true;
		});
	}

	prVisibleChild {}

	prClose { arg preMsg, postMsg;
		super.prClose( preMsg, postMsg );
		children.do({ arg item; item.prClose });
	}

	prSCViewNew { arg preMsg, postMsg;
		properties.put( \canFocus, false );
		^super.prSCViewNew( preMsg, postMsg );
	}
	
	protDraw {
		children.do({ arg child; child.protDraw });
	}

	prSendProperty { arg key, value;
		switch( key,
		\background, {	// overriden to redirect to viewport
			server.listSendMsg([ '/set', this.prViewPortID, key ] ++ value.asSwingArg );
			^nil;
		});
		^super.prSendProperty( key, value );
	}
}
