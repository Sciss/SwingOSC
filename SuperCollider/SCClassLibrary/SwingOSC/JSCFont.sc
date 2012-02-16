/*
 *	JSCFont
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

JSCFont {
	classvar <>verbose = false;

	classvar <>default;
	
	classvar defaultSansFace, defaultSerifFace, defaultMonoFace;
//	classvar names;
	
	var <>name, <>size, <>style;
		
	*initClass {
//		StartUp.add({ this.prCreateDefaults });
//	}
//	
//	*prCreateDefaults {
		switch( thisProcess.platform.name,
		\osx, {
			default			= JSCFont( "LucidaGrande", 11 );
			defaultSansFace	= "LucidaGrande";
			defaultSerifFace	= "Times";
			defaultMonoFace	= "Menlo";
		},
		\linux, {
			default			= JSCFont( "Bitstream Vera Sans", 12 );
			defaultSansFace	= "Bitstream Vera Sans";
			defaultSerifFace	= "Bitstream Vera Serif";
			defaultMonoFace	= "Bitstream Vera Sans Mono";
		}, 
		\windows, {
			default			= JSCFont( "Tahoma", 11 );
			defaultSansFace	= "Tahoma";
			defaultSerifFace	= "Serif";
			defaultMonoFace	= "Monospaced";
		}, {
			default			= JSCFont( "SansSerif", 12 );
			defaultSansFace	= "SansSerif";
			defaultSerifFace	= "Serif";
			defaultMonoFace	= "Monospaced";
		});
	}
		
	*new { arg name, size, style = 0;
		^super.newCopyArgs( name, size, style );
	}
	
	*availableFonts { arg server;
		var servers, result;
		servers 	= Archive.global[ \swingOSCFontNames ];
		server	= server ?? { SwingOSC.default };
		result	= servers !? { servers[ server.name ]};
		if( result.notNil, { ^result });
		"JSCFont.availableFonts : font cache not yet available".warn;
		^[ "Dialog", "DialogInput", "Monospaced", "SansSerif", "Serif" ];
	}
	
	*deleteCache {
		Archive.global.put( \swingOSCFontNames, nil );
	}

	// called by SwingOSC upon startup inside a Routine	
	*prMakeFontsAvailable { arg server;
		var servers, result;
		servers = Archive.global[ \swingOSCFontNames ];
		if( servers.isNil, {
			servers = IdentityDictionary.new;
			Archive.global[ \swingOSCFontNames ] = servers;
		});
		if( servers.includesKey( server.name ), { ^this });
		result = this.prQueryFontNames( server );
		servers[ server.name ] = result;
	}

	*antiAliasing_ { arg flag = false;
		if( verbose, { "JSCFont.antiAliasing : has no effect".error; });
	}
	
	*smoothing_ { arg flag = false;
		if( verbose, { "JSCFont.smoothing : has no effect".error; });
	}

	*defaultSansFace {
		^defaultSansFace;
	}
	
	*defaultSerifFace {
		^defaultSerifFace;
	}
	
	*defaultMonoFace {
		^defaultMonoFace;
	}

	*monospace { arg size, bold = false, italic = false;
		^this.new( this.defaultMonoFace, size, if( bold, 1, 0 ) | if( italic, 2, 0 ));
	}

	*serif { arg size, bold = false, italic = false;
		^this.new( this.defaultSerifFace, size, if( bold, 1, 0 ) | if( italic, 2, 0 ));
	}

	*sansSerif { arg size, bold = false, italic = false;
		^this.new( this.defaultSansFace, size, if( bold, 1, 0 ) | if( italic, 2, 0 ));
	}

	*prQueryFontNames { arg server;
		var qid, fonts, numFonts, reply, off, chunkSize, fontNames, success = true;
		
		if( verbose, { "JSCFont.availableFonts : querying...".postln });
		server	= server ?? SwingOSC.default;
		server.sendMsg( '/method', '[', '/local', \fnt, '[', '/new', 'java.util.ArrayList', ']', ']', \addAll,
			'[', '/method', 'java.util.Arrays', \asList,
				'[', '/methodr', '[', '/method', 'java.awt.GraphicsEnvironment', \getLocalGraphicsEnvironment, ']', \getAvailableFontFamilyNames, ']',
			']' );
		qid		= UniqueID.next;
		reply	= server.sendMsgSync([ '/query', qid, '[', '/method', \fnt, \size, ']' ], [ '/info', qid ]);
		if( reply.notNil, {
			numFonts	= reply[ 2 ];
		}, {
			"JSCFont.availableFonts : timeout".error;
			numFonts 	= 0;
			success	= false;
		});
		off		= 0;
		fontNames	= Array( numFonts );
		while({ (off < numFonts) && success }, {
			// 128 queries is about 4.5 KB sending and probably < 8 KB receiving
			// (worst case: all font names have a length of 64 chars)
			chunkSize	= min( 128, numFonts - off );
			reply	= server.sendMsgSync([ '/query' ] ++ Array.fill( chunkSize, { arg i; [ qid, '[', '/method', \fnt, \get, off + i, ']' ]}).flatten,
									  [ '/info', qid ]);
			if( reply.notNil, {
				chunkSize.do({ arg i; fontNames.add( reply[ (i << 1) + 2 ].asString )});
				off = off + chunkSize;
			}, {
				"JSCFont.availableFonts : timeout".error;
				success	= false; // leave loop
			});
		});
		server.sendMsg( '/free', \fnt );
		if( verbose, { "JSCFont.availableFonts : query done.".postln });
		^if( success, fontNames );
	}

	setDefault {
		default = this;
// ??? should we do this ??? cocoa doesn't
		SwingOSC.set.do({ arg server; server.listSendMsg([ '/local', \font ] ++ this.asSwingArg )});
	}

	asSwingArg {
		^([ '[', '/new', 'java.awt.Font', this.name, this.style, this.size, ']' ]);
	}
	
	storeArgs { ^[ name, size, style ] }

	boldVariant {
		^this.class.new( name, size, style | 1 );
	}
	
	bold { ^((style & 1) != 0) }
	bold_ { arg bool; style = (style & 1.bitNot) | if( bool, 1, 0 ) }

	italic { ^((style & 2) != 0) }
	italic_ { arg bool; style = (style & 2.bitNot) | if( bool, 2, 0 ) }

// don't start supporting unnecessary stuff!	
//	hasPointSize { ^false } // Java 2D uses fixed 72 dpi

	// support in combination with CocoaDocument
  	asSCFont {
    		var cocoa, scfont;
		cocoa = GUI.get( \cocoa );
    		^if( cocoa.notNil, {
      		scfont = cocoa.font.new( name, size );
      		if( this.bold, { scfont = scfont.boldVariant });
      		scfont;
    		});
  	}
}

JFont : JSCFont {
	*new { arg name, size, style = 0;
		this.deprecated( thisMethod, Meta_JSCFont.findRespondingMethodFor( \new ));
		^JSCFont.new( name, size, style );
	}

	*availableFonts { arg server;
		this.deprecated( thisMethod, Meta_JSCFont.findRespondingMethodFor( \availableFonts ));
		^JSCFont.availableFonts( server );
	}
	
	*default {
		this.deprecated( thisMethod, Meta_JSCFont.findRespondingMethodFor( \default ));
		^JSCFont.default;
	}	

	*default_ { arg value;
		this.deprecated( thisMethod, Meta_JSCFont.findRespondingMethodFor( \default_ ));
		^JSCFont.default_( value );
	}	

	*deleteCache {
		this.deprecated( thisMethod, Meta_JSCFont.findRespondingMethodFor( \deleteCache ));
		^JSCFont.deleteCache;
	}

	*defaultSansFace {
		this.deprecated( thisMethod, Meta_JSCFont.findRespondingMethodFor( \defaultSansFace ));
		^JSCFont.defaultSansFace;
	}
	
	*defaultSerifFace {
		this.deprecated( thisMethod, Meta_JSCFont.findRespondingMethodFor( \defaultSerifFace ));
		^JSCFont.defaultSerifFace;
	}
	
	*defaultMonoFace {
		this.deprecated( thisMethod, Meta_JSCFont.findRespondingMethodFor( \defaultMonoFace ));
		^JSCFont.defaultMonoFace;
	}

	*monospace { arg size, bold = false, italic = false;
		this.deprecated( thisMethod, Meta_JSCFont.findRespondingMethodFor( \monospace ));
		^JSCFont.monospace( size, bold, italic );
	}

	*serif { arg size, bold = false, italic = false;
		this.deprecated( thisMethod, Meta_JSCFont.findRespondingMethodFor( \serif ));
		^JSCFont.serif( size, bold, italic );
	}

	*sansSerif { arg size, bold = false, italic = false;
		this.deprecated( thisMethod, Meta_JSCFont.findRespondingMethodFor( \sansSerif ));
		^JSCFont.sansSerif( size, bold, italic );
	}
}