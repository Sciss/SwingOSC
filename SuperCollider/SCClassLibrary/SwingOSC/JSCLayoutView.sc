/*
 *	JSCLayoutView
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

// abstract class!
JSCLayoutView : JSCContainerView {
	// ----------------- public instance methods -----------------

	spacing { ^this.getProperty( \spacing, 0 )}
	
	spacing_ { arg distance; this.setProperty( \spacing, distance )}

	// ----------------- quasi-interface methods : crucial-lib support -----------------

	asFlowView {}

	// ----------------- private instance methods -----------------

	properties { ^super.properties ++ #[ \spacing ]}

	prSendProperty { arg key, value;
		var bndl;

		key	= key.asSymbol;

		switch( key,
			\spacing, {
				server.sendBundle( nil, [ '/methodr', '[', '/method', this.id, \getLayout, ']', \setSpacing, value ],
									 [ '/method', this.id, \revalidate ]);
				^nil;
			}
		);
		^super.prSendProperty( key, value );
	}

	prSCViewNew { arg preMsg, postMsg;
		properties.put( \spacing, 4 );
		^super.prSCViewNew( preMsg, postMsg );
	}
}

JSCHLayoutView : JSCLayoutView {
	// ----------------- private instance methods -----------------

	prInitView {
		^this.prSCViewNew([
			[ '/local', this.id, '[', '/new', "de.sciss.swingosc.Panel", '[', '/new', "de.sciss.swingosc.ColliderAxisLayout", 0, 4, ']', ']' ]
		]);
	}
}

JSCVLayoutView : JSCLayoutView {
	// ----------------- private instance methods -----------------

	prInitView {
		^this.prSCViewNew([
			[ '/local', this.id, '[', '/new', "de.sciss.swingosc.Panel", '[', '/new', "de.sciss.swingosc.ColliderAxisLayout", 1, 4, ']', ']' ]
		]);
	}
}
