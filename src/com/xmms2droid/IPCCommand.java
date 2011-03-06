package com.xmms2droid;

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
