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

import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;


public class XmmsMsgWriter {
	
	public ByteBuffer generatePlayMsg()
	{
		ByteBuffer playMsg = allocateHeader();
		writeHeader(
				playMsg,
				IPCObject.getObjectId(IPCObjects.OUTPUT),
				IPCCommand.getCommandId(IPCCommands.START),
				Xmms2Cookies.PLAY_COOKIE,
				0);	
		playMsg.flip();
		return playMsg;
	}
	
	public ByteBuffer generateStatusReqMsg()
	{
		ByteBuffer statusReqMsg = allocateHeader();
		writeHeader(
				statusReqMsg, 
				IPCObject.getObjectId(IPCObjects.OUTPUT),
				IPCCommand.getCommandId(IPCCommands.OUTPUT_STATUS),
				Xmms2Cookies.PLAYBACKSTATE_COOKIE, 
				0);
		statusReqMsg.flip();
		return statusReqMsg;
	}
	
	public ByteBuffer generateStopMsg()
	{
		ByteBuffer stopMsg = allocateHeader();
		writeHeader(
				stopMsg,
				IPCObject.getObjectId(IPCObjects.OUTPUT),
				IPCCommand.getCommandId(IPCCommands.STOP),
				Xmms2Cookies.STOP_COOKIE,
				0);	
		stopMsg.flip();
		return stopMsg;
	}
		
	public ByteBuffer generatePauseMsg()
	{
		ByteBuffer pauseMsg = allocateHeader();
		writeHeader(
				pauseMsg,
				IPCObject.getObjectId(IPCObjects.OUTPUT),
				IPCCommand.getCommandId(IPCCommands.PAUSE),
				Xmms2Cookies.PAUSE_COOKIE,
				0);	
		pauseMsg.flip();
		return pauseMsg;
	}
	
	public ByteBuffer generateVolReqMsg()
	{
		ByteBuffer volReqMsg = allocateHeader();
		writeHeader(
				volReqMsg,
				IPCObject.getObjectId(IPCObjects.OUTPUT),
				IPCCommand.getCommandId(IPCCommands.VOLGET),
				Xmms2Cookies.GETVOL_COOKIE,
				0);	
		
		volReqMsg.flip();
		return volReqMsg;
	}
	
	public ByteBuffer generateVolumeMsg(int newVol, String channel)
	{
		//This is the len without the channel string
		final int lenRaw = 8;
		final int totalLen = channel.length() + 1 + lenRaw; //Don't forget the \0
		//30 Bytes is the longest possible payload length for the RIGHT channel
		//To come: handle all channels...
		ByteBuffer volReqMsg = ByteBuffer.allocate(30);
		
		writeHeader(volReqMsg,
				IPCObject.getObjectId(IPCObjects.OUTPUT),
				IPCCommand.getCommandId(IPCCommands.VOLSET),
				Xmms2Cookies.SETVOL_COOKIE,
				totalLen);
		putString(volReqMsg, channel);
		volReqMsg.putInt(newVol);
		volReqMsg.flip();
		return volReqMsg;
	}
	
	private void writeHeader(ByteBuffer tgt, int obj, int cmd, int cookie, int payload)
	{
		tgt.putInt(obj);
		tgt.putInt(cmd);
		tgt.putInt(cookie);
		tgt.putInt(payload);
	}
	
	private ByteBuffer allocateHeader()
	{
		return ByteBuffer.allocate(16);
	}
	
	public ByteBuffer generateHelloMsg()
	{
		//This is the len without the channel string
		final String clientName = "xmms2droid";
		final int totalLen = clientName.length() + 1 + 4; //Don't forget the \0
		//30 Bytes is the longest possible payload length for the RIGHT channel
		//To come: handle all channels...
		ByteBuffer helloMsg = ByteBuffer.allocate(100);
		
		writeHeader(helloMsg,
				IPCObject.getObjectId(IPCObjects.MAIN),
				IPCCommand.getCommandId(IPCCommands.CURRENTID),
				Xmms2Cookies.SETVOL_COOKIE,
				totalLen);
		
		//Version number
		//\TODO get the correct version number
		helloMsg.putInt(1);
		putString(helloMsg, clientName);

		helloMsg.flip();
		return helloMsg;
	}
	
	private void putString(ByteBuffer buf, String msg)
	{
		final int len = msg.length() + 1;
		buf.putInt(len);
		byte[] bytes = null;
		try {
			bytes = msg.getBytes("utf-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		buf.put(bytes);
		buf.put((byte)0);
	}

}
