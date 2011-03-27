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
import java.util.HashMap;


public class XmmsMsgParser {
	
	public static ServerMsg parseMsg(ByteBuffer header, ByteBuffer msg)
	{
		int headerObject = XmmsHeaderParser.getObject(header);
		int headerSignal= XmmsHeaderParser.getCommand(header);
		
		IPCObjects object = IPCObject.getObject(headerObject);
		IPCSignals signal = IPCSignal.getSignal(headerSignal);
		
		switch(object)
		{
		case OUTPUT:
			return parseOutputMsg(msg, signal);
		case MAIN:
			return parseMainMsg(msg, signal);
		case CONFIG:
			return parseConfigMsg(msg, signal);
		}
		return new ServerMsg(SrvMsgTypes.UNKNOWN);
	}
	
	public static ServerMsg parseVolMsg(ByteBuffer msg)
	{
		msg.flip();
		XmmsCollTypes type = XmmsCollType.getCollType(msg.getInt());
		HashMap<String, Integer> volumes = DictParser.parseDict(msg);
		ServerVolumeMessage retMessage = new ServerVolumeMessage(SrvMsgTypes.VOLUME_MSG, volumes);
		return retMessage;

	}
	
	public static ServerMsg parseOutputMsg(ByteBuffer msg, IPCSignals signal)
	{
		//TODO replace this with something, that actually works...
		//Check the commands that have to be used...
		if (43 == msg.limit())
		{
			return parseVolMsg(msg);
		}
		return new ServerMsg(SrvMsgTypes.UNKNOWN);
	}
	
	public static ServerMsg parseConfigMsg(ByteBuffer msg, IPCSignals cmd)
	{
		return new ServerMsg(SrvMsgTypes.UNKNOWN);
	}
	
	public static ServerMsg parseMainMsg(ByteBuffer msg, IPCSignals cmd)
	{
		return new ServerMsg(SrvMsgTypes.UNKNOWN);
	}

}
