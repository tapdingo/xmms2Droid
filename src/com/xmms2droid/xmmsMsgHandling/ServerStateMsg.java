package com.xmms2droid.xmmsMsgHandling;

public class ServerStateMsg extends ServerMsg {
	private final int m_state;

	public ServerStateMsg(SrvMsgTypes msgType, int state) {
		super(msgType);
		m_state = state;
	}
	
	public String getState()
	{
		switch(m_state)
		{
		case 0:
			return "Stopped";
		case 1:
			return "Playing";
		case 2:
			return "Paused";
		}
		return "UNKNOWN";
	}

}
