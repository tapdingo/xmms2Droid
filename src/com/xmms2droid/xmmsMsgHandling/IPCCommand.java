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
	VOLSET
}

public class IPCCommand {
	
	private static HashMap<IPCCommands, Integer> m_commands;
	private static boolean initialized = false;
	
	static public int getCommandId(IPCCommands cmdName)
	{
		if (!initialized)
		{
			m_commands = new HashMap<IPCCommands, Integer>();
			m_commands.put(IPCCommands.START, 29);
			m_commands.put(IPCCommands.STOP, 30);
			m_commands.put(IPCCommands.PAUSE, 31);
			m_commands.put(IPCCommands.VOLSET, 40);
			m_commands.put(IPCCommands.VOLGET, 41);
			initialized = true;
		}
		if (m_commands.containsKey(cmdName))
		{
			return m_commands.get(cmdName);
		}
		return -1;
	}

}
