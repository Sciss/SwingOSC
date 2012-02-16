/*
 *	JSCTopView
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

JSCTopView : JSCContainerView {	// NOT subclass of JSCCompositeView
	var window;

	// ----------------- public instance methods -----------------

	focus { arg flag = true;
		if( flag, {
			server.sendMsg( '/method', this.id, \requestFocus );
		}, {
			"JSCTopView.focus( false ) : not yet implemented".error;
		});
	}

	findWindow { ^this.prGetWindow }
	
	// only in construction mode, handled internally
	canReceiveDrag { ^currentDrag.isKindOf( Class )}

//	// bug: visible_( false ) doesn't properly refresh the view ...
//	visible_ { arg bool;
//		var bndl; //, cntID, tempID;
//		if( visible != bool, {
//			visible = bool;	// must be set before calling prVisiblityChange
//			this.prInvalidateAllVisible;
//			bndl = List.new;
//			this.prVisibilityChange( bndl );
////			cntID = if( this.prIsInsideContainer, { "cn" ++ this.id }, { this.id });
//			bndl.add([ '/set', this.id, \visible, visible ]);
////			if( visible, {
////				bndl.add([ '/methodr', '[', '/method', this.id, \getParent, ']', \repaint ]);
////			}, {
////				tempID = server.nextNodeID;
////				bndl.add([ '/local', tempID, '[', '/new', "javax.swing.JPanel", ']' ]);
////				bndl.add([ '/set', this.prGetWindow.id, \contentPane, '[', '/ref', tempID, ']' ]);
////				bndl.add([ '/method', tempID, \revalidate ]);
////				bndl.add([ '/set', this.prGetWindow.id, \contentPane, '[', '/ref', this.id, ']' ]);
////			});
//////			bndl.add([ '/methodr', '[', '/method', this.prGetWindow.id, \getRootPane, ']', \repaint ]);
////			bndl.add([ '/methodr', '[', '/method', cntID, \getParent, ']', \validate ]);
////			bndl.add([ '/methodr', '[', '/method', cntID, \getParent, ']', \repaint ]);
//			bndl.add([ '/method', this.id, \repaint ]);
//			server.listSendBundle( nil, bndl );
//		});
//	}

	// ----------------- private class methods -----------------

	*new { arg window, bounds, id;
		^super.new.prInitTopView( window, bounds, id );
	}
	
	// ----------------- public instance methods -----------------

	defaultReceiveDrag {
		var win, view;
		win = this.findWindow;
		view = currentDrag.paletteExample( win, Rect( 10, 10, 140, 24 ));
		view.keyDownAction_({ arg view, char, modifiers, unicode, keycode;
			if( keycode == 51, { view.remove });
		});
	}

	absoluteBounds { ^this.bounds }

	// ----------------- private instance methods -----------------

	prChildOrder { arg child; ^0 }
	
	init { }	// kind of overriden by prInitTopView

	prInitTopView { arg argWindow, argBounds, id;
//		parent		= argParent.asView;	// actual view
		window		= argWindow;
//		scBounds		= argBounds;
//		jBounds		= this.prBoundsToJava( scBounds );
//		jinsets		= Insets.new;
		this.prInit( nil, argBounds, this.class.viewClass, window.server, id );
//		argParent.add( this );		// maybe window or viewadapter
	}

	prSCViewNew { arg preMsg, postMsg;
		var bndl, argBounds;
		
		if( jinsets.isNil, { jinsets = Insets.new });
		
		bndl			= List.new;
		bndl.addAll( preMsg );
		jBounds		= this.prBoundsToJava( scBounds );
//		argBounds		= jBounds.asSwingArg;
//		bndl.add([ '/set', this.id, \bounds ] ++ argBounds ++ [ \font, '[', '/ref', \font, ']' ]);
//		if( this.prIsInsideContainer, {
//			bndl.add([ '/set', "cn" ++ this.id, \bounds ] ++ argBounds );
//		});
		if( this.prNeedsTransferHandler, {
			this.prCreateDnDResponder( bndl );
		});
		// NOTE: for global key actions to be working, every view
		// has to create a key responder, even if it's not using it personally ;-(
		this.prCreateKeyResponder( bndl );
		this.prCreateCompResponder( bndl );
		bndl.addAll( postMsg );
		server.listSendBundle( nil, bndl.asArray );
	}

	prInitView { ^this.prSCViewNew }

	prGetWindow { ^window }

	handleKeyDownBubbling { arg view, char, modifiers, unicode, keycode;
		keyDownAction.value( view, char, modifiers, unicode, keycode );
	}

	handleKeyUpBubbling { arg view, char, modifiers, unicode, keycode;
		keyUpAction.value( view, char, modifiers, unicode, keycode );
	}

	handleKeyModifiersChangedBubbling { arg view, modifiers;
		keyModifiersChangedAction.value( view, modifiers );
	}

	prBoundsToJava { arg rect; ^rect.copy }
	prBoundsFromJava { arg rect; ^rect.copy }

	prBoundsUpdated {
		if( window.drawFunc.notNil, { window.refresh });
	}
	
	prAllVisible {
		^(visible and: { this.prGetWindow.visible });
	}

	prAddAllTopLeft { arg rect; ^rect }

	prBoundsReadOnly {
		if( scBounds.isNil, {
			// need to revalidate bounds
			scBounds	= jinsets.addTo( jBounds );
		});
		^scBounds;
	}

//	prGetParentRefTopLeft { ^Point( 0, 0 )}
}
