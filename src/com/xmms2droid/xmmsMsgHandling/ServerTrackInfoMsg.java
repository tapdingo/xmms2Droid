package com.xmms2droid.xmmsMsgHandling;

import java.util.HashMap;

public class ServerTrackInfoMsg extends ServerMsg {
	private final HashMap<String, HashMap<String, Object>> m_trackInfo;

	public ServerTrackInfoMsg(SrvMsgTypes msgType,HashMap<String, HashMap<String, Object>> trackInfo) {
		super(msgType);
		m_trackInfo = trackInfo;
	}
	
	public HashMap<String, HashMap<String, Object>> getTrackInfo()
	{
		return m_trackInfo;
	}

}
