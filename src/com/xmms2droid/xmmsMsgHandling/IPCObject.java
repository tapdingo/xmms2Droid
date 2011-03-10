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
	OUTPUT
}

public class IPCObject {
	private static HashMap<IPCObjects, Integer> m_objects;
	private static boolean initialized = false;
		
	public static int getObjectId(IPCObjects obj)
	{
		if (!initialized)
		{
			m_objects = new HashMap<IPCObjects, Integer>();
			m_objects.put(IPCObjects.MAIN, 0);
			m_objects.put(IPCObjects.PLAYLIST, 1);
			m_objects.put(IPCObjects.CONFIG, 2);
			m_objects.put(IPCObjects.OUTPUT, 3);
			initialized = true;
		}
		if (m_objects.containsKey(obj))
		{
			return m_objects.get(obj);
		}
		return -1;
	}
}