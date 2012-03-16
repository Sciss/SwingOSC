/*
 *	JSCWebView
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

JSCWebView : JSCView {
	classvar <>verbose = false;

	var <>onLoadFinished, <>onLoadFailed, <onLinkActivated, <enterInterpretsSelection=true;
	var <url = "";
	var <editable = false;

//	var string = "";
	var <title = "";
//	var txResp;
	var acResp, hyResp;
//	var selStart = 0, selStop = 0;
	
	*clearCache {
//		^thisMethod.notYetImplemented;
		this.prWarnNotYetImplemented( thisMethod );
	}

	url_ { arg path;
//("BEFORE : " ++ path).postln;
		if( path.contains( "SC://" ), {
	        	path = Help.findHelpFile( path.asRelativePath( "SC:/" ));
		});

		if( path.contains( "://" ).not, {
	        if( path.first.asString != "/" ) { path = String.scDir +/+ path };
	        path = "file://" ++ path;
		});
//("AFTER : " ++ path).postln;
//		path = path.replace(" ", "%20");
		url = path;
//		server.sendMsg( '/method', this.id, \read, path );
		server.sendMsg( '/method', this.id, \navigate, path );
	}

//	title { 
//		this.prWarnNotYetImplemented( thisMethod );
//		^"Unknown Title";
//	}

	forward { 
		server.sendMsg( '/method', this.id, \forward );
	}

	back { 
		server.sendMsg( '/method', this.id, \back );
	}

	reload { 
		server.sendMsg( '/method', this.id, \reload );
	}

	didLoad { onLoadFinished.value( this )}

	didFail { onLoadFailed.value( this )}

	// Get the displayed content in html form.
	html { 
		this.class.prWarnNotYetImplemented( thisMethod );
		^"";
	}

	// Set html content.
	html_ { arg htmlString;
		// XXX should protect against message overflow
		server.sendBundle( nil, [ '/set', this.id, "html" ] ++ htmlString.asSwingArg );
		
//		^this.prWarnNotYetImplemented( thisMethod );
//		var kitID, docID;
//		kitID = server.nextNodeID;
//		docID = server.nextNodeID;
//		server.sendBundle( nil,
//			[ '/local', kitID, '[', '/new', "javax.swing.text.html.HTMLEditorKit", ']',
//			            docID, '[', '/method', kitID, \createDefaultDocument, ']' ],
////			[ '/method', this.id, \setContentType, "text/html", ']',
//			[ '/set', this.id, \editorKit, '[', '/ref', kitID, ']',
//			                   \document, '[', '/ref', docID, ']' ],
//			[ '/method', kitID, \insertHTML, '[', '/ref', docID, ']', 0,
//				htmlString,
////				"<html><body><b>This is bold</b><i>this is italics</i></html></body>",
//				0, 0,
//				'[', '/field', "javax.swing.text.html.HTML$Tag", "BODY", ']' ]
//		);
	}

	editable_ { arg bool;
		if( bool, {
			^this.class.prWarnNotYetImplemented( thisMethod );
		});
//		editable = bool;
//		server.sendMsg( '/set', this.id, \editable, bool );
	}

	selectedText { 
		this.class.prWarnNotYetImplemented( thisMethod );
		^"";
//		^string.copyRange( selStart, selStop - 1 );  // stupid inclusive ending
	}

	// Try to extract plain text from html content and return it.
	plainText { 
		this.class.prWarnNotYetImplemented( thisMethod );
		^"";
	}

	onLinkActivated_ { arg func;
		if( func.notNil && hyResp.isNil, { this.prCreateLinkResponder });
		onLinkActivated = func;
		server.sendMsg( \set, this.id, "vetoEnabled", func.notNil );
	}

	linkActivated { arg linkString; onLinkActivated.value( this, linkString )}

	findText { arg string, reverse = false;
		^this.class.prWarnNotYetImplemented( thisMethod );
	}

	enterInterpretsSelection_ { arg bool;
		enterInterpretsSelection = bool;
	}

//	setFontFamily { arg generic, specific;
//		^this.prWarnNotYetImplemented( thisMethod );
//	}

	*paletteExample { arg parent, bounds;
		^this.new( parent, bounds ).url_( "http://supercollider.sourceforge.net" );
	}

	// ----------------- private class methods -----------------
	
	*prWarnNotYetImplemented { arg method;
		(this.name ++ ":" ++ method.name ++ " -- no op, it is not yet implemented").warn;
	}
	
	// ----------------- private instance methods -----------------

	prCreateLinkResponder {
		if( hyResp.notNil, {
			"JSCTextView.prCreateLinkResponder : already created!".warn;
			^nil;
		});
		hyResp = OSCpathResponder( server.addr, [ '/hyperlink', this.id ], { arg time, resp, msg; var url, descr;
			{
				url   = msg[3].asString;
				descr = msg[4].asString;
				switch( msg[2],
					\ACTIVATED, { this.linkActivated( this.prMassageURL( url ))}
//					\ENTERED,   { linkEnteredAction.value( this, url, descr )},
//					\EXITED,    { linkExitedAction.value( this, url, descr )}
				);
			}.defer;
		}).add;
		server.sendMsg( '/local', "hy" ++ this.id,
			'[', '/new', "de.sciss.swingosc.HyperlinkResponder", this.id, ']' );
	}
	
	// this is necessary for SCDoc!
	prMassageURL { arg url;
		^if( url.beginsWith( "file:" ) and: { url.copyRange( 5, 6 ) != "//" }, {
			"file://" ++ url.copyToEnd( 5 )
		}, url );
	}
	
	prInitView {
		jinsets = Insets.new;	// none
//		txResp = OSCpathResponder( server.addr, [ '/doc', this.id ], { arg time, resp, msg;
//			var state, str;
//			
//			state = msg[2];
//	
//			case
//			{ state === \insert }
//			{
////				("insert at "++msg[3]++" len "++msg[4]++" text='"++msg[5]++"'").postln;
//				str = msg[5].asString;
//if( verbose and: { msg[ 4 ] != str.size }, { ("JSCTextView discrepancy. SwingOSC sees " ++ msg[ 4 ] ++ " characters, SuperCollider sees " ++ str.size ).postln });
//				string = string.insert( msg[3], str );
//				if( action.notNil, {{ action.value( this, state, msg[3], msg[4], str )}.defer });
//			}
//			{ state === \remove }
//			{
////				("remove from "++msg[3]++" len "++msg[4]).postln;
//				string = string.keep( msg[3] ) ++ string.drop( msg[3] + msg[4] );
//				if( action.notNil, {{ action.value( this, state, msg[3], msg[4] )}.defer });
//			}
//			{ state === \caret }
//			{
////				("caret now between "++msg[3]++" and "++msg[4]).postln;
//				if( msg[3] < msg[4], {
//					selStart	= msg[3];
//					selStop	= msg[4];
//				}, {
//					selStart	= msg[4];
//					selStop	= msg[3];
//				});
//				if( action.notNil, {{ action.value( this, state, msg[3], msg[4] )}.defer });
//			};
//		}).add;
		acResp = OSCpathResponder( server.addr, [ '/action', this.id ], { arg time, resp, msg;
			var state;
			state = msg[4];
			case { state === \loaded }
			{
				{
					url   = msg[6].asString;
					title = msg[8].asString;
					this.didLoad;
				}.defer;
			}
			{ state === \failed }
			{
				{ 
					url   = msg[6].asString;
					this.didFail;
				}.defer;
			};
		}).add;
		
		^this.prSCViewNew([
			[ '/local', this.id, '[', '/method', "de.sciss.swingosc.WebView", "create", ']',
				"ac" ++ this.id,
				'[', '/new', "de.sciss.swingosc.ActionResponder", this.id, '[', '/array', \result, \URL, \title, ']', ']',
//				"tx" ++ this.id,
//				'[', '/new', "de.sciss.swingosc.DocumentResponder", this.id, ']'
			]
		]);
	}

	prClose { arg preMsg, postMsg;
//		txResp.remove;
		acResp.remove;
		hyResp.remove; // nil.remove is allowed
		^super.prClose( preMsg ++
			[[ '/method', "ac" ++ this.id, \remove ]] ++
			if( hyResp.notNil, {[[ '/method', "hy" ++ this.id, \remove ]]}), postMsg );
	}
}
