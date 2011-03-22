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

enum IPCSignals {
	PLAYLIST_CHANGED,
	CONFIGVALUE_CHANGED,
	PLAYBACK_STATUS,
	OUTPUT_VOLUME_CHANGED,
	OUTPUT_PLAYTIME,
	OUTPUT_CURRENTID,
	OUTPUT_OPEN_FAIL,
	PLAYLIST_CURRENT_POS,
	PLAYLIST_LOADED,
	MEDIALIB_ENTRY_ADDED,
	MEDIALIB_ENTRY_UPDATE,
	COLLECTION_CHANGED,
	QUIT,
	MEDIAINFO_READER_STATUS,
	MEDIAINFO_READER_UNINDEXED,
	END, 
	UNKNOWN
}

public class IPCSignal {
	private static HashMap<IPCSignals, Integer> m_signals;
	private static HashMap<Integer, IPCSignals> m_ids;
	private static boolean initialized = false;
	
	static public int getSignalId(IPCSignals sigName)
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
	
	static public IPCSignals getSignal(int cmd)
	{
		if (!initialized)
		{
			init();
		}
		if (m_ids.containsKey(cmd))
		{
			return m_ids.get(cmd);
		}
		return IPCSignals.UNKNOWN;
		
	}
	
	static private void init()
	{
		IPCSignals[] signals = IPCSignals.values();
		
		m_signals = new HashMap<IPCSignals, Integer>();
		m_ids = new HashMap<Integer, IPCSignals>();
		
		for (int i = 0; i < signals.length; i++)
		{
			m_signals.put(signals[i], i);
			m_ids.put(i, signals[i]);
		}
		initialized = true;
	}

}
