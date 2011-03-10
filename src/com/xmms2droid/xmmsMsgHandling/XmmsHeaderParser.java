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
import java.nio.IntBuffer;

public class XmmsHeaderParser {
	
	private final static int OBJECT_POS = 0;
	private final static int COMMAND_POS = 1;
	private final static int COOKIE_POS = 2;
	private final static int PAYLOAD_LEN_POS = 3;
	
	static public int getObject (ByteBuffer header)
	{
		header.flip();
		return header.asIntBuffer().get(OBJECT_POS);	
	}
	
	static public int getCommand(ByteBuffer header)
	{
		header.flip();
		return header.asIntBuffer().get(COMMAND_POS);	
	}
	
	static public int getCookie(ByteBuffer header)
	{
		header.flip();
		return header.asIntBuffer().get(COOKIE_POS);
	}
	
	static public int getPayloadLen(ByteBuffer header)
	{
		header.flip();
		return header.asIntBuffer().get(PAYLOAD_LEN_POS);
	}
}
