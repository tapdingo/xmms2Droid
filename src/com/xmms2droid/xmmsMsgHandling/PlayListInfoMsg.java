package com.xmms2droid.xmmsMsgHandling;

import java.util.ArrayList;

public class PlayListInfoMsg extends ServerMsg {
	
	public ArrayList<Integer> ids;

	public PlayListInfoMsg(SrvMsgTypes msgType) {
		super(msgType);
	}

}
