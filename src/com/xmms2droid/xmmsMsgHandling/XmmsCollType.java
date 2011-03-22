package com.xmms2droid.xmmsMsgHandling;

import java.util.HashMap;

enum XmmsCollTypes {
	XMMSV_TYPE_NONE,
	XMMSV_TYPE_ERROR,
	XMMSV_TYPE_INT32,
	XMMSV_TYPE_STRING,
	XMMSV_TYPE_COLL,
	XMMSV_TYPE_BIN,
	XMMSV_TYPE_LIST,
	XMMSV_TYPE_DICT,
	XMMSV_TYPE_END
}

public class XmmsCollType {
	private static HashMap<XmmsCollTypes, Integer> m_signals;
	private static HashMap<Integer, XmmsCollTypes> m_ids;
	private static boolean initialized = false;
	
	static public int getCollTypeId(XmmsCollTypes sigName)
	{
		if (!initialized)
		{
			init();
		}
		if (m_signals.containsKey(sigName))
		{
			return m_signals.get(sigName);
		}
		return -1;
	}
	
	static public XmmsCollTypes getCollType(int cmd)
	{
		if (!initialized)
		{
			init();
		}
		if (m_ids.containsKey(cmd))
		{
			return m_ids.get(cmd);
		}
		return XmmsCollTypes.XMMSV_TYPE_NONE;
	}
	
	static private void init()
	{
		XmmsCollTypes[] signals = XmmsCollTypes.values();
		
		m_signals = new HashMap<XmmsCollTypes, Integer>();
		m_ids = new HashMap<Integer, XmmsCollTypes>();
		
		for (int i = 0; i < signals.length; i++)
		{
			m_signals.put(signals[i], i);
			m_ids.put(i, signals[i]);
		}
		initialized = true;
	}

}
