/*   Copyright 2011 Patrick Rehm (tapdingo@googlemail.com)
 * 
 *   This file is part of XMMS2Droid.
 *
 *   XMMS2Droid is free software: you can redistribute it and/or modify
 *   it under the terms of the GNU General Public License as published by
 *   the Free Software Foundation, either version 3 of the License, or
 *   (at your option) any later version.
 *
 *   XMMS2Droid is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU General Public License for more details.
 *
 *   You should have received a copy of the GNU General Public License
 *   along with XMMS2Droid.  If not, see <http://www.gnu.org/licenses/>
 */

package com.xmms2droid.xmmsMsgHandling;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;


public class XmmsMsgParser {
	
	public static ServerMsg parseMsg(ByteBuffer header, ByteBuffer msg)
	{
		int headerObject = XmmsHeaderParser.getObject(header);
		int headerSignal= XmmsHeaderParser.getCommand(header);
		int cookie = XmmsHeaderParser.getCookie(header);
		
		IPCObjects object = IPCObject.getObject(headerObject);
		IPCSignals signal = IPCSignal.getSignal(headerSignal);
		
		switch(object)
		{
		case OUTPUT:
			return parseOutputMsg(msg, signal, cookie);
		case MAIN:
			return parseMainMsg(msg, signal, cookie);
		case CONFIG:
			return parseConfigMsg(msg, signal, cookie);
		case MEDIALIB:
			return parseMediaLibMsg(msg, signal, cookie);
		case PLAYLIST:
			return parsePlayListMsg(msg, signal, cookie);
		case SIGNAL:
			return parseBroadcastMsg(msg, signal, cookie);
		}
		return new ServerMsg(SrvMsgTypes.UNKNOWN);
	}
	
	private static ServerMsg parseVolMsg(ByteBuffer msg)
	{
		msg.flip();
		
		//Type is irrelevant here...
		msg.getInt();
		HashMap<String, Integer> volumes = DictParser.parseDict(msg);
		return new ServerVolumeMsg(SrvMsgTypes.VOLUME_MSG, volumes);
	}
	
	private static ServerMsg parsePlayStateMsg(ByteBuffer msg)
	{
		msg.flip();
		
		//Len is irrelevant here...
		msg.getInt();
		int playState = msg.getInt();
		return new ServerStateMsg(SrvMsgTypes.PLAYBACKSTATE_MSG, playState);
	}
	
	
	public static ServerMsg parseOutputMsg(ByteBuffer msg, IPCSignals signal, int cookie)
	{
		//TODO replace this with something, that actually works...
		//Check the commands that have to be used...
		switch (cookie)
		{
		case Xmms2Cookies.GETVOL_COOKIE:
			return parseVolMsg(msg);
		case Xmms2Cookies.PLAYBACKSTATE_COOKIE:
			return parsePlayStateMsg(msg);
		case Xmms2Cookies.TRACKREQ_COOKIE:
			return parseTrackIdMsg(msg);
		}
		return new ServerMsg(SrvMsgTypes.UNKNOWN);
	}
	
	private static ServerMsg parseMediaLibMsg(ByteBuffer msg, IPCSignals cmd, int cookie)
	{
		switch(cookie)
		{
		case Xmms2Cookies.TRACKINFOREQ_COOKIE:
			return parseTrackInfoMsg(msg, false);
		case Xmms2Cookies.PLAYLIST_TRACKINFO_COOKIE:
			return parseTrackInfoMsg(msg, true);
		}
		return new ServerMsg(SrvMsgTypes.UNKNOWN);
	}
	
	private static ServerMsg parseConfigMsg(ByteBuffer msg, IPCSignals cmd, int cookie)
	{
		return new ServerMsg(SrvMsgTypes.UNKNOWN);
	}
	
	private static ServerMsg parseMainMsg(ByteBuffer msg, IPCSignals cmd, int cookie)
	{
		return new ServerMsg(SrvMsgTypes.UNKNOWN);
	}
	
	private static ServerMsg parseTrackIdMsg(ByteBuffer msg)
	{
		msg.flip();
		//Type is irrelevant here
		msg.getInt();
		return new ServerTrackIdMsg(SrvMsgTypes.TRACKID_MSG, msg.getInt());
	}
	
	private static ServerMsg parseTrackInfoMsg(ByteBuffer msg, Boolean playlist)
	{
		//21 Is the size of the Track Unknown Error msg...
		if (msg.capacity() == 21)
		{
			return new ServerMsg(SrvMsgTypes.UNKNOWN);
		}
		msg.flip();
		//Type is irrelevant here
		msg.getInt();
		//This msg type seems to be a little bit more complicated...
		HashMap<String, HashMap<String, Object>> trackInfo = DictParser.parseTrackInfo(msg);
		
		return new ServerTrackInfoMsg(SrvMsgTypes.TRACKINFO_MSG, trackInfo, playlist);
	}
	
	private static ServerMsg parsePlayListMsg(ByteBuffer msg, IPCSignals cmd, int cookie)
	{
		switch(cookie)
		{
		case Xmms2Cookies.TRACKREQ_COOKIE:
			return requestTrackInfo(msg);
		case Xmms2Cookies.PLAYLIST_REQUEST_COOKIE:
			return updatePlaylist(msg);
		}
		return new ServerMsg(SrvMsgTypes.UNKNOWN);
	}
	
	private static ServerMsg parseBroadcastMsg(ByteBuffer msg, IPCSignals cmd, int cookie)
	{
		switch (cookie)
		{
			case Xmms2Cookies.REGPLAYBACKUPDATE_COOKIE:
				return parsePlayStateMsg(msg);
			case Xmms2Cookies.REGTRACKUPDATE_COOKIE:
				return parseTrackIdMsg(msg);
		}
		return new ServerMsg(SrvMsgTypes.UNKNOWN);
	}
	
	private static ServerMsg requestTrackInfo(ByteBuffer msg)
	{
		msg.flip();
		//Type is irrelevant here
		msg.getInt();
		return new ServerTrackIdMsg(SrvMsgTypes.TRACKID_MSG, msg.getInt());
	}
	
	private static ServerMsg updatePlaylist(ByteBuffer msg)
	{
		ArrayList<Integer> ids = new ArrayList<Integer>();
		msg.flip();
		msg.getInt(); //TypeBla;
		final int playListLen = msg.getInt();
			
		for(int i = 0; i < playListLen; i++)
		{
			msg.getInt();
			ids.add(msg.getInt());
		}
		
		PlayListInfoMsg retMsg = new PlayListInfoMsg(SrvMsgTypes.PLAYLIST_INFO_MSG);
		retMsg.ids = ids;
		return retMsg;
	}
}
