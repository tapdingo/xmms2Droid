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

package com.xmms2droid;

import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.util.HashMap;

public class DictParser {
	
	public static HashMap<String, Integer> parseDict(ByteBuffer buf)
	{
		buf.flip();
		
		//Currently we only use the implicit lengths
		//Should be fixed for a more robust approach
		parseHeader(buf);
		
		//TypeBla
		//TODO Make a list of response types
		buf.getInt();
		//LenBla
		int len = buf.getInt();
		
		HashMap<String, Integer> volumes = new HashMap<String, Integer>();
		
		for (int i = 0; i < len; i++)
		{
			String key = getString(buf);
			
			//TypeIdBla
			buf.getInt();
			
			int val = buf.getInt();	
			volumes.put(key, val);
		}
		return volumes;
	}
	
	private static String getString(ByteBuffer buf)
	{
		String recString = "";
		
		int len = buf.getInt();
		byte [] strTmp = new byte[len];
		
		buf.get(strTmp);
		
		if (strTmp[len - 1] == 0)
		{
			try {
				recString = new String(strTmp, 0, len-1, "utf-8");
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		else
		{
			try {
				recString = new String(strTmp, 0, len, "utf-8");
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
		
		return recString;
		
	}
	private static int parseHeader(ByteBuffer buf)
	{
		//Currently we're not interested in header values of the response...
		buf.getInt(); //Object
		buf.getInt(); //Command
		buf.getInt(); //Cookie
		int payloadLen = buf.getInt();
		return payloadLen;
	}

}
