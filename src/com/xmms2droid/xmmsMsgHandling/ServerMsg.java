package com.xmms2droid.xmmsMsgHandling;

public class ServerMsg {
	
	private final SrvMsgTypes m_msgType;
	
	public ServerMsg(SrvMsgTypes msgType) {
		// TODO Auto-generated constructor stub
		m_msgType = msgType;
	}
	
	public SrvMsgTypes getMsgType()
	{
		return m_msgType;
	}

}
