package com.xmms2droid;

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
