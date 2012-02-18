/*
 *	JSCTextView
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

JSCTextView : JSCView {
	classvar <>verbose = false;

	var <stringColor, <font, <editable = true;
	var <autohidesScrollers = false, <hasHorizontalScroller = false, <hasVerticalScroller = false;
	var <usesTabToFocusNextView = true, <enterInterpretsSelection = true;
//	var <textBounds;
	var <linkAction, <linkEnteredAction, <linkExitedAction;

	var txResp, hyResp;
	
	var <string = "";
	var selStart = 0, selStop = 0;
	
	var cnID;

//	mouseDown { arg clickPos;
////		this.focus(true);
//		mouseDownAction.value( this, clickPos );	
//	}	
	
//	string {
//		^this.getProperty( \string );
//	}

	// ----------------- public instance methods -----------------

	doLinkAction { arg url, description;
		linkAction.value( this, url, description );
	}
	
	string_ { arg str;
		^this.setString( str, -1 );
	}
		
	selectedString {
		^string.copyRange( selStart, selStop - 1 );  // stupid inclusive ending
	}
	
	selectedString_ { arg str;
		this.setString( str, selStart, selStop - selStart );
		this.select( selStart, str.size );
//		this.setProperty( \selectedString, str );
		// XXX
	}
	
	caret { ^selStart }
	selectionStart { ^selStart }
	selectionSize { ^(selStop - selStart) }
	
//	lineWrap_ { arg onOff;
//		server.sendMsg( '/set', this.id, \lineWrap, onOff );
//	}
	
	stringColor_ { arg color;
		stringColor = color;
		this.setStringColor( color, -1, 0 );
	}
	
	setStringColor { arg color, rangeStart = -1, rangeSize = 0;
		server.listSendMsg([ '/method', this.id, \setForeground, rangeStart, rangeSize ] ++ color.asSwingArg );
	}
	
	font_ { arg afont;
		font = afont;
		this.setFont( font, -1, 0 );
	}
	
	setFont { arg font, rangeStart = -1, rangeSize = 0;
		server.listSendMsg([ '/method', this.id, \setFont, rangeStart, rangeSize ] ++ font.asSwingArg );
	}
	
	tabs_ { arg tabs;
		this.setTabs( tabs, -1, 0 );
	}
	
	/**
	 *	@param	tabs		array of either positions (SimpleNumber) in pixels
	 *					or of two-element arrays [ position, align ]
	 *					where align is any of \left, \right, \center, \decimal, \bar
	 */
	setTabs { arg tabs, rangeStart = -1, rangeSize = 0;
		var pos, align, leader;
		tabs = tabs.collect({ arg t; #pos, align, leader = t.asArray; [ pos,
			(([ \left, \right, \center, nil, \decimal, \bar ].indexOf( align ) ? 0) << 8 ) |
			// note: leaders are currently not working!
			([ \none, \dots, \hyphens, \underline, \thickline, \equals ].indexOf( leader ) ? 0)]}).flatten;
		server.listSendMsg([ '/method', this.id, \setTabs, rangeStart, rangeSize ] ++ tabs.asSwingArg );
	}
	
 	leftIndent_ { arg indent;
		this.setLeftIndent( indent, -1, 0 );
	}
	
	/**
	 *	@param	indent	paragraph left indentation in pixels
	 */
 	setLeftIndent { arg indent, rangeStart = -1, rangeSize = 0;
		server.sendMsg( '/method', this.id, \setLeftIndent, rangeStart, rangeSize, indent );
	}
	
 	rightIndent_ { arg indent;
		this.setRightIndent( indent, -1, 0 );
	}
	
	/**
	 *	@param	indent	paragraph right indentation in pixels
	 */
 	setRightIndent { arg indent, rangeStart = -1, rangeSize = 0;
		server.sendMsg( '/method', this.id, \setRightIndent, rangeStart, rangeSize, indent );
	}
	
 	spaceAbove_ { arg space;
		this.setSpaceAbove( space, -1, 0 );
	}
	
	/**
	 *	@param	space	paragraph's top margin in pixels
	 */
 	setSpaceAbove { arg space, rangeStart = -1, rangeSize = 0;
		server.sendMsg( '/method', this.id, \setSpaceAbove, rangeStart, rangeSize, space );
	}
	
 	spaceBelow_ { arg space;
		this.setSpaceBelow( space, -1, 0 );
	}
	
	/**
	 *	@param	space	paragraph's bottom margin in pixels
	 */
 	setSpaceBelow { arg space, rangeStart = -1, rangeSize = 0;
		server.sendMsg( '/method', this.id, \setSpaceBelow, rangeStart, rangeSize, space );
	}
	
 	lineSpacing_ { arg spacing;
		this.setLineSpacing( spacing, -1, 0 );
	}
	
	/**
	 *	@param	spacing	paragraph's line spacing factor
	 */
 	setLineSpacing { arg spacing, rangeStart = -1, rangeSize = 0;
		server.sendMsg( '/method', this.id, \setLineSpacing, rangeStart, rangeSize, spacing );
	}
	
 	align_ { arg mode;
		this.setAlign( mode, -1, 0 );
	}
	
	/**
	 *	Sets paragraphs' alignment. The naming was chosen to
	 *	correspond with the align_ method in JSCTextField.
	 *
	 *	@param	mode		alignment mode (Symbol), one of \left, \center, \right, and \justified
	 */
 	setAlign { arg mode, rangeStart = -1, rangeSize = 0;
		server.sendMsg( '/method', this.id, \setAlignment, rangeStart, rangeSize,
			[ \left, \center, \right, \justified ].indexOf( mode ) ? 0 );
	}
	
	setString { arg string, rangeStart = 0, rangeSize = 0;
		var bndl, off, len, bndlSize;
	
//		string		= string.asString;
		
		// server.options.oscBufSize - sizeof([ '/method', 1234, \setString, 0, 1, "" ])
		if( string.size <= (server.options.oscBufSize - 44), {
			server.sendMsg( '/method', this.id, \setString, rangeStart, rangeSize, string );
		}, {
			bndl	= Array( 3 );
			off	= 0;
			// [ #bundle, [ '/method', 1234, \beginDataUpdate ],
			//            [ '/method', 1234, \addData, "GA" ],
			//            [ '/method', 1234, \endDataUpdate, 0, 1 ]
			bndlSize = 136;
			bndl.add([ '/method', this.id, \beginDataUpdate ]);
			while({ off < string.size }, {
				len = min( string.size - off, server.options.oscBufSize - bndlSize );
				bndl.add([ '/method', this.id, \addData, string.copyRange( off, off + len - 1 )]);
				off = off + len;
				if( off < string.size, {
					server.listSendBundle( nil, bndl );
					bndl = Array( 2 );
					bndlSize = 100; // wie oben, jedoch ohne \beginDataUpdate
				});
			});
			bndl.add([ '/method', this.id, \endDataUpdate, rangeStart, rangeSize ]);
			server.listSendBundle( nil, bndl );
		});
	}
	
	// e.g. "<HTML><BODY><H1>Heading</H1><P>Paragraph</P></BODY></HTML>"
	htmlString_ { arg content;
		var kitID, docID;
		kitID = server.nextNodeID;
		docID = server.nextNodeID;
		server.sendBundle( nil,
			[ '/local', kitID, '[', '/new', "javax.swing.text.html.HTMLEditorKit", ']',
			            docID, '[', '/method', kitID, \createDefaultDocument, ']' ],
//			[ '/method', this.id, \setContentType, "text/html", ']',
			[ '/set', this.id, \editorKit, '[', '/ref', kitID, ']',
			                   \document, '[', '/ref', docID, ']' ],
			[ '/method', kitID, \insertHTML, '[', '/ref', docID, ']', 0,
				content,
//				"<html><body><b>This is bold</b><i>this is italics</i></html></body>",
				0, 0,
				'[', '/field', "javax.swing.text.html.HTML$Tag", "BODY", ']' ]
		);
	}

// don't know how this is supposed to work (the parent tag etc.)
//	insertHTML { arg htmlString, pos = 0, tag = "BODY";
//		server.sendMsg( '/methodr', '[', '/method', this.id, \getEditorKit, ']',
//			\insertHTML, '[', '/method', this.id, \getDocument, ']', pos, htmlString, 0, 0,
//				'[', '/field', "javax.swing.text.html.HTML$Tag", tag, ']' );
//	}
	
	editable_ { arg bool;
		editable = bool;
		server.sendMsg( '/set', this.id, \editable, bool );
	}
	
	linkAction_ { arg func;
		if( func.notNil && hyResp.isNil, { this.prCreateLinkResponder });
		linkAction = func;
	}

	linkEnteredAction_ { arg func;
		if( func.notNil && hyResp.isNil, { this.prCreateLinkResponder });
		linkEnteredAction = func;
	}

	linkExitedAction_ { arg func;
		if( func.notNil && hyResp.isNil, { this.prCreateLinkResponder });
		linkExitedAction = func;
	}
	
	usesTabToFocusNextView_ { arg bool;
		usesTabToFocusNextView = bool;
		this.setProperty( \usesTabToFocusNextView, bool );
	}
	
	enterInterpretsSelection_ { arg bool;
		enterInterpretsSelection = bool;
//		this.setProperty( \enterExecutesSelection, bool );
	}
	
	autohidesScrollers_ { arg bool;
		autohidesScrollers = bool;
		this.prUpdateScrollers;
	}
	
	hasHorizontalScroller_{ arg bool;
		hasHorizontalScroller = bool;
		this.prUpdateScrollers;
	}
	
	hasVerticalScroller_{ arg bool;
		hasVerticalScroller = bool;
		this.prUpdateScrollers;
	}
	
// what's the point about this method??
//	textBounds_{ arg rect;
//		textBounds = rect;
//		this.setProperty(\textBounds, rect);
//	}

	caretColor { ^this.getProperty( \caretColor )}
	caretColor_ { arg color; this.setProperty( \caretColor, color )}

	openURL { arg url;
//		server.sendMsg( '/method', this.id, \setPage, '[', '/new', "java.net.URL", url, ']' );
//		server.sendMsg( '/set', this.id, \page, url );
		// XXX update client send string rep.
		server.sendMsg( '/method', this.id, \readURL, '[', '/new', "java.net.URL", url, ']' );
	}

	open { arg path;
		var file;
		if( path.beginsWith( "SC://" ), {
			path = Help.findHelpFile( path.copyToEnd( 5 ));
		}, {
			path = path.absolutePath;
		});
//		server.sendMsg( '/set', this.id, \page, '[', '/methodr', '[', '/new', "java.io.File", path, ']', 'toURL', ']' );
		server.sendMsg( '/method', this.id, \read, path );
	}
	
	select { arg start, len;
		server.sendMsg( '/method', this.id, \select, start, start + len );
	}
	
	selectAll {
		server.sendMsg( '/method', this.id, \selectAll );
	}

	caret_ { arg pos;
		server.sendMsg( '/set', this.id, \caretPosition, pos );
	}

	defaultKeyDownAction { arg key, modifiers, unicode;
		// check for 'ctrl+enter' = interprete
		if( (unicode == 0x0D) and: { ((modifiers & 0x40000) != 0) && enterInterpretsSelection }, {
			if( selStop > selStart, {	// text is selected
				this.selectedString.interpretPrint;
			}, {
				this.prCurrentLine.interpretPrint;
			});
			^this;
		});
		^nil;
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
					\ACTIVATED, { linkAction.value( this, url, descr )},
					\ENTERED,   { linkEnteredAction.value( this, url, descr )},
					\EXITED,    { linkExitedAction.value( this, url, descr )}
				);
			}.defer;
		}).add;
		server.sendMsg( '/local', "hy" ++ this.id,
			'[', '/new', "de.sciss.swingosc.HyperlinkResponder", this.id, ']' );
	}
	
	prContainerID { ^cnID }

	prInitView {
		cnID = "cn" ++this.id;
//		properties.put( \value, 0 );
		
		txResp = OSCpathResponder( server.addr, [ '/doc', this.id ], { arg time, resp, msg;
			var state, str;
			
			state = msg[2];
	
			case
			{ state === \insert }
			{
//				("insert at "++msg[3]++" len "++msg[4]++" text='"++msg[5]++"'").postln;
				str = msg[5].asString;
if( verbose and: { msg[ 4 ] != str.size }, { ("JSCTextView discrepancy. SwingOSC sees " ++ msg[ 4 ] ++ " characters, SuperCollider sees " ++ str.size ).postln });
				string = string.insert( msg[3], str );
				if( action.notNil, {{ action.value( this, state, msg[3], msg[4], str )}.defer });
			}
			{ state === \remove }
			{
//				("remove from "++msg[3]++" len "++msg[4]).postln;
				string = string.keep( msg[3] ) ++ string.drop( msg[3] + msg[4] );
				if( action.notNil, {{ action.value( this, state, msg[3], msg[4] )}.defer });
			}
			{ state === \caret }
			{
//				("caret now between "++msg[3]++" and "++msg[4]).postln;
				if( msg[3] < msg[4], {
					selStart	= msg[3];
					selStop	= msg[4];
				}, {
					selStart	= msg[4];
					selStop	= msg[3];
				});
				if( action.notNil, {{ action.value( this, state, msg[3], msg[4] )}.defer });
			};
		}).add;
		
		^this.prSCViewNew([
			[ '/local', this.id, '[', '/new', "de.sciss.swingosc.TextView", ']',
				"cn" ++ this.id,				 	// bars : v=never, h=never
				'[', '/new', "javax.swing.JScrollPane", '[', '/ref', this.id, ']', 21, 31, ']',
				"tx" ++ this.id,
				'[', '/new', "de.sciss.swingosc.DocumentResponder", this.id, ']'
			]
		]);
	}

	prUpdateScrollers {
		server.sendMsg( '/set', "cn" ++ this.id,
			\horizontalScrollBarPolicy, hasHorizontalScroller.if( autohidesScrollers.if( 30, 32 ), 31 ),
			\verticalScrollBarPolicy, hasVerticalScroller.if( autohidesScrollers.if( 20, 22 ), 21 ));
	}

	prClose { arg preMsg, postMsg;
		txResp.remove;
		hyResp.remove; // nil.remove is allowed
		^super.prClose( preMsg ++ [
			[ '/method', "tx" ++ this.id, \remove ]] ++
			if( hyResp.notNil, {[[ '/method', "hy" ++ this.id, \remove ]]}), postMsg );
	}

	prCurrentLine {
		var startIdx, stopIdx;
		
		startIdx	= string.findBackwards( "\n", false, selStart - 1 ) ? 0;
		stopIdx	= string.find( "\n", false, selStart ) ?? { string.size };
		^string.copyRange( startIdx, stopIdx - 1 );
	}
}
