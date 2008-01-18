/*
 *	JSCViews collection 4
 *	(SwingOSC classes for SuperCollider)
 *
 *	Copyright (c) 2005-2008 Hanns Holger Rutz. All rights reserved.
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
 *
 *
 *	Changelog:
 */

/**
 *	@version		0.57, 12-Jan-08
 *	@author		Hanns Holger Rutz
 */
JPeakMeterManager {
	classvar all;			// IdentityDictionary mapping guiServer name to
						//	IdentityDictionary mapping audioServer name to JPeakMeterManager
	var <guiServer, <audioServer;
	var views;			// IdentityDictionary mapping a JSCPeakMeterView to a config.
						//	A config is an IdentityDictionary itself with the following entries:
						//	- \busIndexOffset	the channel offset div(2) in the control bus
						//	- \numChannels	the number of audio channels (control bus channels div(2))
						//	- \synths			an Array of Synth instances (the metering synths)
						//	- \group			the group for the synths _if created by the manager_
	var cbus;				// current meter control bus
	var numChannels = 0;	// current number of audio channels
	var <id;				// server side manager id
	
	// ----------------- quasi-constructor -----------------

	*newFrom { arg guiServer, audioServer;
		var res, guiServerDict;
		if( all.isNil, {
			all	= IdentityDictionary.new;
		});
		guiServerDict = all.at( guiServer.name );
		if( guiServerDict.isNil, {
			guiServerDict = IdentityDictionary.new;
			all.put( guiServer.name, guiServerDict );
		});
		res = guiServerDict.at( audioServer.name );
		if( res.isNil, {
			res = this.new( guiServer, audioServer );
			guiServerDict.put( guiServer.name, res );
		});
		^res;
	}
	
	// ----------------- constructor -----------------

	*new { arg guiServer, audioServer;
		^super.new.prInit( guiServer, audioServer );
	}

	// ----------------- private instance methods -----------------

	prInit { arg argGuiServer, argAudioServer;
		views		= IdentityDictionary.new;
		guiServer		= argGuiServer;
		audioServer	= argAudioServer;
		id			= guiServer.nextNodeID;
		guiServer.sendBundle( nil, [ '/method',
			'[', '/local', id, '[', '/new', "de.sciss.swingosc.PeakMeterManager", ']', ']',
			\setServer, audioServer.addr.hostname, audioServer.addr.port, audioServer.options.protocol ]
		);
	}
	
	protRegister { arg view;
		var config, oldCBus, viewNumChannels, group, bndl;
		
		config			= IdentityDictionary.new;
		views.put( view, config );
		viewNumChannels	= view.bus.numChannels;
		config.put( \numChannels, viewNumChannels );
		config.put( \busIndexOffset, numChannels );
		this.protSetGroup( view, view.group );	// creates group if necessary
		group			= config.at( \group );
		oldCBus			= cbus;
		numChannels		= numChannels + viewNumChannels;
		cbus				= Bus.control( audioServer, numChannels << 1 );
~cbus = cbus;
~group = group;
		bndl				= Array( 3 );
		bndl.add([ '/set', view.id, \numChannels, numChannels ]);
		bndl.add([ '/method', this.id, \addAndSetBus,
			'[', '/ref', view.id, ']', group.nodeID, cbus.index ]);
		if( view.active, {
			bndl.add([ '/method', this.id, \setActive, '[', '/ref', view.id, ']', true ]);
		});
		guiServer.listSendBundle( nil, bndl );
		{
			guiServer.sync;
			oldCBus.free;
		}.fork( SwingOSC.clock );
	}
	
	protUnregister { arg view;
		var bndl, synths, group, config, viewNumChannels;
		
		config			= views.removeAt( view );
		synths			= config.at( \synths );
		bndl				= Array( synths.size + 1 );
		group			= config.at( \group );
		viewNumChannels	= config.at( \numChannels );
		numChannels		= numChannels - viewNumChannels;
		synths.do({ arg synth; bndl.add( synth.freeMsg )});
		if( group.notNil, {
			bndl.add( group.freeMsg );
		});
		if( bndl.notEmpty, {
			audioServer.listSendBundle( nil, bndl );
		});
		if( views.isEmpty, {
			this.prDispose;
		});
	}
	
	prDispose {
		all.at( guiServer.name ).removeAt( audioServer.name );
		guiServer.sendBundle( nil, [ '/method', this.id, \dispose ], [ '/free', this.id ]);
		cbus.free;
		cbus			= nil;
		guiServer		= nil;
		audioServer	= nil;
	}
	
	protSetGroup { arg view, group;
		var config, synths, bndl, newGroup, oldGroup;
		
		config	= views.at( view );
		synths	= config.at( \synths );
		oldGroup	= config.at( \group );
		bndl		= Array( synths.size + 2 );
		if( group.isNil, {
			group	= Group.basicNew( audioServer );
			// only store groups that _we_ create
			// in order to properly free them later
			config.put( \group, group );
			bndl.add( group.newMsg( audioServer.asGroup, \addToTail ));
		}, {
			config.removeAt( \group );
		});
		synths.do({ arg synth; bndl.add( synth.moveToTailMsg( group ))});
		if( oldGroup.notNil, {
			bndl.add( oldGroup.freeMsg );
		});
		if( bndl.notEmpty, {
			audioServer.listSendBundle( nil, bndl );
		});
	}
	
	protSetActive { arg view, active;
		guiServer.sendMsg( '/method', this.id, \setActive, '[', '/ref', view.id, ']', active );
	}
}

JSCPeakMeterView : JSCControlView {
	var <bus, <group, manager;
	var <active = false;
	var <border = false, <caption = true, <captionVisible = true, <captionPosition = \left;
	var <rmsPainted = true, <holdPainted = true;
//	var acResp;	// OSCpathResponder for action listening

	// ----------------- public instance methods -----------------

	active_ { arg bool;
		active = bool;
		if( manager.notNil, {
			manager.protSetActive( this, active );
		});
	}
	
	border_ { arg bool;
		border = bool;
		this.setProperty( \border, border );
	}
	
	caption_ { arg bool;
		caption = bool;
		this.setProperty( \caption, caption );
	}

	captionVisible_ { arg bool;
		captionVisible = bool;
		this.setProperty( \captionVisible, captionVisible );
	}

	captionPosition_ { arg value;
		captionPosition = value;
		this.setProperty( \captionPosition, captionPosition );
	}
	
	rmsPainted_ { arg bool;
		rmsPainted = bool;
		this.setProperty( \rmsPainted, rmsPainted );
	}

	holdPainted_ { arg bool;
		holdPainted = bool;
		this.setProperty( \holdPainted, holdPainted );
	}

	font { ^this.getProperty( \font )}
	font_ { arg argFont;
		this.setProperty( \font, argFont );
	}

	group_ { arg g;
		group = g;
		if( manager.notNil, {
			manager.protSetGroup( this, group );
		});
	}
	
	bus_ { arg b;
		var numChannels;
		// if( (bus.server != b.server) or: { b.numChannels != bus.numChannels }, { ... });
		this.prUnregister;
		if( b.notNil and: { b.numChannels > 0 }, {
			bus			= b;
			manager		= JPeakMeterManager.newFrom( this.server, bus.server );
			manager.protRegister( this );
			numChannels	= bus.numChannels;
		}, {
			bus			= nil;
			numChannels	= 0;
			server.sendMsg( '/set', this.id, \numChannels, numChannels );
		});
		
//		server.sendMsg( '/method', this.id, \setBus, b.server.addr.hostname, b.server.addr.port, b.server.options.protocol, b.index, b.numChannels );
	}

	// ----------------- private instance methods -----------------

	prClose { arg preMsg, postMsg;
		this.prUnregister;
		^super.prClose( preMsg, postMsg );
//		acResp.remove;
//		^super.prClose([[ '/method', "ac" ++ this.id, \remove ],
//					   [ '/free', "ac" ++ this.id ]]);
	}

	prSCViewNew {
//		properties.put( \value, false );
//		acResp = OSCpathResponder( server.addr, [ '/action', this.id ], { arg time, resp, msg;
//			// don't call valueAction coz we'd create a loop
//			properties.put( \value, msg[4] != 0 );
//			{ this.doAction; }.defer;
//		}).add;
		^super.prSCViewNew([
			[ '/local', this.id, '[', '/new', "de.sciss.swingosc.PeakMeterGroup", ']' ]
		]);
	}
	
	prUnregister {
		if( manager.notNil, {
			manager.protUnregister( this );
			manager = nil;
		});
	}
	
	prSendProperty { arg key, value;
		key	= key.asSymbol;

		// fix keys
		case { key === \captionPosition }
		{
			switch( value,
			\left,   { value = 2 },
			\right,  { value = 4 },
			\center, { value = 0 }
			);
		}
		{ key === \rmsPainted }
		{
			key = \rMSPainted;
		};
		^super.prSendProperty( key, value );
	}
}