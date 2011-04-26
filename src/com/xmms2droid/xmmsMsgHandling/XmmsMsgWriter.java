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
		
	public ByteBuffer generateSimpleRequest(int objectID, int commandID, int cookie)
	{
		ByteBuffer request = allocateMinimalPacket();
		writeHeader(
				request,
				objectID,
				commandID,
				cookie,
				4);
		request.putInt(0);
		request.flip();
		return request;
	}
	
	public ByteBuffer generatePlayMsg()
	{
		ByteBuffer playMsg = generateSimpleRequest(
				IPCObject.getObjectId(IPCObjects.OUTPUT),
				IPCCommandWrapper.getCommandID(PlayBackIPCCommands.START),
				Xmms2Cookies.PLAY_COOKIE);
		return playMsg;
	}
	
	//\todo FIXME
	public ByteBuffer generateStatusReqMsg()
	{
		ByteBuffer statusReqMsg = allocateMinimalPacket();
		writeHeader(
				statusReqMsg, 
				IPCObject.getObjectId(IPCObjects.OUTPUT),
				IPCCommandWrapper.getCommandID(PlayBackIPCCommands.OUTPUT_STATUS),
				Xmms2Cookies.PLAYBACKSTATE_COOKIE, 
				4);
		statusReqMsg.putInt(0);
		statusReqMsg.flip();
		return statusReqMsg;
	}
	
	public ByteBuffer generateStopMsg()
	{
		ByteBuffer stopMsg = generateSimpleRequest(
				IPCObject.getObjectId(IPCObjects.OUTPUT),
				IPCCommandWrapper.getCommandID(PlayBackIPCCommands.STOP),
				Xmms2Cookies.STOP_COOKIE);
		return stopMsg;
	}
		
	public ByteBuffer generatePauseMsg()
	{
		ByteBuffer pauseMsg = generateSimpleRequest(
				IPCObject.getObjectId(IPCObjects.OUTPUT),
				IPCCommandWrapper.getCommandID(PlayBackIPCCommands.PAUSE),
				Xmms2Cookies.PAUSE_COOKIE);
		return pauseMsg;
	}
	
	//\TODO UPDATE ME
	public ByteBuffer generateVolReqMsg()
	{
		ByteBuffer volReqMsg = generateSimpleRequest(
				IPCObject.getObjectId(IPCObjects.OUTPUT),
				IPCCommandWrapper.getCommandID(PlayBackIPCCommands.VOLGET),
				Xmms2Cookies.GETVOL_COOKIE);
		volReqMsg.flip();
		return volReqMsg;
	}
	
	//\TODO UPDATE ME
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
				PlayBackIPCCommands.VOLSET.ordinal() + 32,
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
	
	private ByteBuffer allocateMinimalPacket()
	{
		return ByteBuffer.allocate(20);
	}
	
	//\TODO UPDATE ME
	public ByteBuffer generateHelloMsg()
	{
		//This is the len without the channel string
		final String clientName = "xmmsdroid";
		final int totalLen = clientName.length() + 1 + 8; //Don't forget the \0.
		ByteBuffer helloMsg = ByteBuffer.allocate(20);
		
		writeHeader(helloMsg,
				4,
				32,
				0,
				4);
		
		//Version number
		//\TODO get the correct version number
		helloMsg.putInt(0);
		/*helloMsg.putLong(0);
		helloMsg.putLong(0);
		helloMsg.putLong(0);
		helloMsg.putLong(0);
		helloMsg.putLong(0);
		helloMsg.putLong(0);
		helloMsg.putLong(0);
		helloMsg.putLong(0);
		helloMsg.putLong(0);
		helloMsg.putLong(0);
		helloMsg.putLong(0);
		helloMsg.putLong(0);
		helloMsg.putLong(0);
		helloMsg.putLong(0);
		helloMsg.putLong(0);
		helloMsg.putLong(0);*/
		//helloMsg.putInt(18);
		//putString(helloMsg, clientName);

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
	
	//\TODO Replace Magic Number
	public ByteBuffer generateTrackReqMsg()
	{
		ByteBuffer trackReqMsg = allocateMinimalPacket();
		writeHeader(
				trackReqMsg,
				IPCObject.getObjectId(IPCObjects.OUTPUT),
				39,
				Xmms2Cookies.TRACKREQ_COOKIE,
				0);	
		trackReqMsg.flip();
		return trackReqMsg;
	}
	
	//\TODO UPDATE ME
	public ByteBuffer generateTrackInfoReqMsg(int id)
	{
		ByteBuffer trackInfoReq = ByteBuffer.allocate(20);
		writeHeader(
				trackInfoReq,
				IPCObject.getObjectId(IPCObjects.MEDIALIB),
				0,
				Xmms2Cookies.TRACKINFOREQ_COOKIE,
				4);
		trackInfoReq.putInt(id);
		trackInfoReq.flip();
		return trackInfoReq;
	}

	//\TODO Replace Magic Number
	public ByteBuffer generateListChangeMsg(int i) 
	{
		ByteBuffer listChangeReq = ByteBuffer.allocate(20);
		writeHeader(
				listChangeReq,
				IPCObject.getObjectId(IPCObjects.PLAYLIST),
				8,
				Xmms2Cookies.LISTCHANGE_REL_COOKIE,
				4);
		listChangeReq.putInt(i);
		listChangeReq.flip();
		return listChangeReq;
	}

	//\TODO UPDATE ME
	public ByteBuffer generateTickleMsg()
	{
		ByteBuffer tickleMsg = allocateMinimalPacket();
		writeHeader(
				tickleMsg,
				IPCObject.getObjectId(IPCObjects.OUTPUT),
				0,
				Xmms2Cookies.TICKLE_COOKIE,
				0);	
		tickleMsg.flip();
		return tickleMsg;
	}
	
	//\TODO UPDATE ME
	public ByteBuffer generateReqPlaybackUpdateMsg()
	{
		ByteBuffer reqPlaybackUpdateMsg = allocateMinimalPacket();
		writeHeader(
				reqPlaybackUpdateMsg,
				6,
				32,
				11,
				0);	
		reqPlaybackUpdateMsg.flip();
		return reqPlaybackUpdateMsg;
	}
	
	

}
