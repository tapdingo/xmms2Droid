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

enum IPCCommands {
	START,
	STOP,
	PAUSE,
	VOLGET, 
	VOLSET, 
	UNKNOWN
}

public class IPCCommand {
	
	private static HashMap<IPCCommands, Integer> m_commands;
	private static HashMap<Integer, IPCCommands> m_ids;
	private static boolean initialized = false;
	
	//TODO Until the Command Enum is complete..
	private static final int shiftFac = 29;
	
	static public int getCommandId(IPCCommands cmdName)
	{
		if (!initialized)
		{
			init();
		}
		if (m_commands.containsKey(cmdName))
		{
			return m_commands.get(cmdName);
		}
		return -1;
	}
	
	static public IPCCommands getCommand(int cmd)
	{
		if (!initialized)
		{
			init();
		}
		if (m_ids.containsKey(cmd))
		{
			return m_ids.get(cmd);
		}
		return IPCCommands.UNKNOWN;
		
	}
	
	static private void init()
	{
		IPCCommands[] commands = IPCCommands.values();
		
		m_commands = new HashMap<IPCCommands, Integer>();
		m_ids = new HashMap<Integer, IPCCommands>();
		
		for (int i = 0; i < commands.length; i++)
		{
			m_commands.put(commands[i], i + shiftFac);
			m_ids.put(i + shiftFac, commands[i]);
		}
		initialized = true;
		
	}

}
