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
		int len = buf.getInt();
		//Weird structure here... 2 Keys than a value...
		HashMap<String, HashMap<String, Object>> vals = new HashMap<String, HashMap<String, Object>>();
		
		//We get three values - 2 Keys 1 Value - in every pass, so i+= 3
		for (int i = 0; i < len; i+= 3)
		{
			//FirstType is ALWAYS a String *cough*
			buf.getInt();
			String outerKey = getString(buf);
			//SecondType is ALWAYS a String *cough*
			buf.getInt();
			String innerKey = getString(buf);
			
			int valType = buf.getInt();
			
			switch(valType)
			{
			case 3:
				String strVal = getString(buf);
				addValueToMap(vals, innerKey, outerKey, strVal);
				break;
			case 2:
				int intVal = buf.getInt();
				addValueToMap(vals, innerKey, outerKey, intVal);
				break;
			}
		}
		return vals;
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
