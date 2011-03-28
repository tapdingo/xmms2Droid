package com.xmms2droid.xmmsMsgHandling;

public class ServerTrackIdMsg extends ServerMsg {
	private final int m_id;

	public ServerTrackIdMsg(SrvMsgTypes msgType, int id) {
		super(msgType);
		m_id = id;
	}
	
	public int getId()
	{
		return m_id;
	}

}
