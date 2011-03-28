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

import java.util.HashMap;

enum IPCObjects {
	MAIN, 
	PLAYLIST, 
	CONFIG,
	OUTPUT,
	MEDIALIB,
	UNKNOWN
}

public class IPCObject {
	private static HashMap<Integer, IPCObjects> m_ids;
	private static HashMap<IPCObjects, Integer> m_objects;
	private static boolean initialized = false;
	
		
	public static int getObjectId(IPCObjects obj)
	{
		if (!initialized)
		{
			init();
		}
		
		if (m_objects.containsKey(obj))
		{
			return m_objects.get(obj);
		}
		return -1;
	}
	
	public static IPCObjects getObject(int id)
	{
		if (!initialized)
		{
			init();
		}
		
		if (m_ids.containsKey(id))
		{
			return m_ids.get(id);
		}
		return IPCObjects.UNKNOWN;
	}
	
	private static void init()
	{
		//TODO Java BIMAP?
		//This will only work, if the lists are complete without holes..
		IPCObjects[] objects = IPCObjects.values();
		m_objects = new HashMap<IPCObjects, Integer>();
		m_ids = new HashMap<Integer, IPCObjects>();
		
		for (int i = 0; i < objects.length; i++)
		{
			m_objects.put(objects[i], i);
			m_ids.put(i, objects[i]);
			
		}
		initialized = true;
	}
}
