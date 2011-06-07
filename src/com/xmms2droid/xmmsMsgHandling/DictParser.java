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

import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.util.HashMap;

class DictEntry {
	public String key;
	public Object value;
}

public class DictParser {

	public static HashMap<String, Integer> parseDict(ByteBuffer buf)
	{	
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
	
	public static HashMap<String, HashMap<String, Object>> parseTrackInfo(ByteBuffer buf)
	{
		//This has been modified in new server versions, much more consistent now...
		HashMap<String, HashMap<String, Object>> vals = new HashMap<String, HashMap<String, Object>>();

		int len = buf.getInt();
		for (int i = 0; i < len; i++)
		{
			String outerKey = getString(buf);
			int innerType = buf.getInt();
			
			switch (innerType)
			{
			case 7:
				DictEntry curEntry = parseInnerDict(buf);
				addValueToMap(vals, curEntry.key, outerKey, curEntry.value);
				break;
			}
		}
		return vals;
	}
	
	private static DictEntry parseInnerDict(ByteBuffer buf)
	{
		DictEntry parsedVal = new DictEntry();
		
		final int innerLen = buf.getInt();
		Object value = 0;
		
		for (int i = 0; i < innerLen; i++)
		{
			final String innerKey = getString(buf);
			final int innerType = buf.getInt();
			switch(innerType)
			{
			
			//Only Integer and Strings for now in inner Dictionaries...
			case 2:
				value = buf.getInt();
				break;
			case 3:
				value = getString(buf);
				break;
				
			}
			parsedVal.key = innerKey;
			parsedVal.value = value;
		}	
		return parsedVal;
	}
	
	private static void addValueToMap(HashMap<String, HashMap<String, Object>> valMap,String innerKey, String outerKey, Object val)
	{
		if (!valMap.containsKey(outerKey))
		{
			HashMap<String, Object> innerMap = new HashMap<String, Object>();
			valMap.put(outerKey, innerMap);
		}
		HashMap<String, Object> innerMap = valMap.get(outerKey);
		innerMap.put(innerKey, val);
	}
	
	private static String getString(ByteBuffer buf)
	{
		String recString = "";
		
		int len = buf.getInt();
		
		// Simple catch to prevent crashes here...
		if (len > 256)
		{
			return "ERROR";
		}
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

}
