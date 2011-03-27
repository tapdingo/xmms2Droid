package com.xmms2droid.xmmsMsgHandling;

import java.util.HashMap;

public class ServerVolumeMessage extends ServerMsg {
	
	private final HashMap<String, Integer> m_volInfo;

	public ServerVolumeMessage(SrvMsgTypes msgType, HashMap<String, Integer> volInfo) {
		super(msgType);
		// TODO Auto-generated constructor stub
		m_volInfo = volInfo;
	}
	
	public HashMap<String, Integer> getVolumeInformation()
	{
		return m_volInfo;
	}

}
