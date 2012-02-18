/*
 *	JSCDragView
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

JSCDragView : JSCStaticTextBase {
	var <>interpretDroppedStrings = false;
	
	// ----------------- public class methods -----------------

	*paletteExample { arg parent, bounds;
		var v;
		v = this.new(parent, bounds);
		v.object = \something;
		^v
	}
	
	// ----------------- private instance methods -----------------

	defaultGetDrag { ^object }

	prNeedsTransferHandler { ^true }

	prImportDrag {
		if( interpretDroppedStrings, { JSCView.importDrag });
	}

	prSCViewNew { arg preMsg, postMsg;
		properties.put( \canFocus, false );
//		jinsets = Insets( 3, 3, 3, 3 );
		^super.prSCViewNew( preMsg, postMsg );
	}
}

JSCDragSource : JSCDragView {
	// ----------------- private instance methods -----------------

	prInitView {
		^this.prSCViewNew([
//			[ '/set', '[', '/local', this.id, '[', '/new', "de.sciss.swingosc.Label", ']', ']',
//				\border, '[', '/method', "javax.swing.BorderFactory", \createRaisedBevelBorder, ']'
//			]
			[ '/local', this.id, '[', '/new', "de.sciss.swingosc.DragView", 1, ']',
			]
		]);
	}

	prGetDnDModifiers { ^0 } 	// no modifiers needed
}

JSCDragSink : JSCDragView {
	// ----------------- public instance methods -----------------

	defaultCanReceiveDrag { ^true }

	defaultReceiveDrag {
		this.object = currentDrag;
		this.doAction;
	}

	// ----------------- private instance methods -----------------

	prInitView {
		^this.prSCViewNew([
//			[ '/set', '[', '/local', this.id, '[', '/new', "de.sciss.swingosc.Label", ']', ']',
//				\border, '[', '/method', "javax.swing.BorderFactory", \createLoweredBevelBorder, ']'
//			]
			[ '/local', this.id, '[', '/new', "de.sciss.swingosc.DragView", 0, ']',
			]
		]);
	}

	prGetDnDModifiers { ^-1 }	// don't allow it to be drag source
}

JSCDragBoth : JSCDragView {		// in SwingOSC not subclass of JSCDragSink
	// ----------------- public instance methods -----------------

	defaultCanReceiveDrag { ^true }
	
	defaultReceiveDrag {
		this.object = currentDrag;
		this.doAction;
	}

	defaultGetDrag { ^object }

	// ----------------- private instance methods -----------------

	prInitView {
		^this.prSCViewNew([
//			[ '/set', '[', '/local', this.id, '[', '/new', "de.sciss.swingosc.Label", ']', ']',
//				\border, '[', '/method', "javax.swing.BorderFactory", \createCompoundBorder,
//					'[', '/method', "javax.swing.BorderFactory", \createRaisedBevelBorder, ']',
//					'[', '/method', "javax.swing.BorderFactory", \createLoweredBevelBorder, ']', ']'
//			]
			[ '/local', this.id, '[', '/new', "de.sciss.swingosc.DragView", 2, ']',
			]
		]);
	}

	prGetDnDModifiers { ^0 } 	// no modifiers needed
}
