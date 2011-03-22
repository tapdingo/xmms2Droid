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
	
	public static void parseMsg(ByteBuffer header, ByteBuffer msg)
	{
		int headerObject = XmmsHeaderParser.getObject(header);
		int headerSignal= XmmsHeaderParser.getCommand(header);
		
		IPCObjects object = IPCObject.getObject(headerObject);
		IPCSignals signal = IPCSignal.getSignal(headerSignal);
		
		switch(object)
		{
		case OUTPUT:
			parseOutputMsg(msg, signal);
			break;
		case MAIN:
			parseMainMsg(msg, signal);
			break;
		case CONFIG:
			parseConfigMsg(msg, signal);
			break;
		}
	}
	
	public static void parseVolMsg(ByteBuffer msg)
	{
		msg.flip();
		XmmsCollTypes type = XmmsCollType.getCollType(msg.getInt());
		if (XmmsCollTypes.XMMSV_TYPE_DICT == XmmsCollType.getCollType(msg.getInt()))
		{
			HashMap<String, Integer> volumes = DictParser.parseDict(msg);
			int leftVolume = volumes.get("left");
			int rightVolume = volumes.get("right");
		}
	}
	
	public static void parseOutputMsg(ByteBuffer msg, IPCSignals signal)
	{
		//TODO replace this with something, that actually works...
		//Check the commands that have to be used...
		if (43 == msg.limit())
		{
			parseVolMsg(msg);
		}
		
	}
	
	public static void parseConfigMsg(ByteBuffer msg, IPCSignals cmd)
	{
	}
	
	public static void parseMainMsg(ByteBuffer msg, IPCSignals cmd)
	{
	}

}
