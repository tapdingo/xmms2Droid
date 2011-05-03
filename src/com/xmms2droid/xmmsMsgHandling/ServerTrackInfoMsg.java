package com.xmms2droid.xmmsMsgHandling;

import java.util.HashMap;

public class ServerTrackInfoMsg extends ServerMsg {
	private final HashMap<String, HashMap<String, Object>> m_trackInfo;
	private final Boolean m_playListInfo;

	public ServerTrackInfoMsg(SrvMsgTypes msgType,HashMap<String, HashMap<String, Object>> trackInfo, Boolean playlist) {
		super(msgType);
		m_trackInfo = trackInfo;
		m_playListInfo = playlist;
	}
	
	public HashMap<String, HashMap<String, Object>> getTrackInfo()
	{
		return m_trackInfo;
	}

	public Boolean getPlayListInfo() {
		return m_playListInfo;
	}

}
