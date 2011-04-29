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
	
	private static final int HEADER_LEN = 16;
	private static final int PROTOCOL_VERSION = 18;
	private static final String CLIENT_NAME = "XMMS2DROID";
		
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
	
	public ByteBuffer generateStatusReqMsg()
	{
		ByteBuffer statusReqMsg = generateSimpleRequest(
				IPCObject.getObjectId(IPCObjects.OUTPUT),
				IPCCommandWrapper.getCommandID(PlayBackIPCCommands.OUTPUT_STATUS),
				Xmms2Cookies.PLAYBACKSTATE_COOKIE);
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
		return volReqMsg;
	}
	
	public ByteBuffer generateVolumeMsg(int newVol, String channel)
	{	
		//This is the len without the channel string: 
		// 8 Byte List Information
		// 8 Byte String Information
		// 8 Byte Volume Information
		final int lenRaw = 24;
		final int totalLen = channel.length() + 1 + lenRaw; //Don't forget the \0
		ByteBuffer volReqMsg = ByteBuffer.allocate(totalLen + HEADER_LEN);
		
		writeHeader(volReqMsg,
				IPCObject.getObjectId(IPCObjects.OUTPUT),
				IPCCommandWrapper.getCommandID(PlayBackIPCCommands.VOLSET),
				Xmms2Cookies.SETVOL_COOKIE,
				totalLen);
		
		putListHead(volReqMsg, 2); //2 Arguments: Channel, volume
		putString(volReqMsg, channel);
		putInt32(volReqMsg, newVol);
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
	
	public ByteBuffer generateHelloMsg()
	{
		//This is the len without the channel string: 
		// 8 Byte List Information
		// 8 Byte Protocol Information
		// 8 Byte String Information
		final int lenRaw = 24;
		final int totalLen = CLIENT_NAME.length() + 1 + lenRaw; //Don't forget the \0
		ByteBuffer helloMsg = ByteBuffer.allocate(totalLen + HEADER_LEN);
		
		writeHeader(helloMsg,
				IPCObject.getObjectId(IPCObjects.MAIN),
				IPCCommandWrapper.getCommandID(MainIPCCommands.HELLO),
				Xmms2Cookies.HELLO_COOKIE,
				totalLen);
		
		putListHead(helloMsg, 2); //2 Arguments: Protocol Version, ClientName
		putInt32(helloMsg, PROTOCOL_VERSION);
		putString(helloMsg, CLIENT_NAME);
		helloMsg.flip();
		return helloMsg;
	}
	
	private void putString(ByteBuffer buf, String msg)
	{
		
		final int len = msg.length() + 1; //String + 0 Byte
		buf.putInt(XmmsTypeIds.getTypeId(XmmsTypes.STRING));
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
	
	private void putInt32(ByteBuffer buf, Integer val)
	{
		buf.putInt(XmmsTypeIds.getTypeId(XmmsTypes.INT32));
		buf.putInt(val);
	}
	
	private void putListHead(ByteBuffer buf, Integer len)
	{
		buf.putInt(XmmsTypeIds.getTypeId(XmmsTypes.LIST));
		buf.putInt(len);
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
		ByteBuffer listChangeReq = ByteBuffer.allocate(32);
		writeHeader(
				listChangeReq,
				IPCObject.getObjectId(IPCObjects.PLAYLIST),
				IPCCommandWrapper.getCommandID(PlayListIPCCommands.SET_POS_REL),
				Xmms2Cookies.LISTCHANGE_REL_COOKIE,
				16);
		putListHead(listChangeReq, 1);
		putInt32(listChangeReq, i);
		listChangeReq.flip();
		return listChangeReq;
	}

	public ByteBuffer generateTickleMsg()
	{
		ByteBuffer tickleMsg = generateSimpleRequest(
				IPCObject.getObjectId(IPCObjects.OUTPUT),
				IPCCommandWrapper.getCommandID(PlayBackIPCCommands.DECODER_KILL),
				Xmms2Cookies.TICKLE_COOKIE);
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
