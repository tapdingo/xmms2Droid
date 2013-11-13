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
 *   
 *   \TODO The generation of messages with arguments should be improved
 *   It's always the same, refactor it into a method...
 */

package com.xmms2droid.xmmsMsgHandling;

import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;


public class XmmsMsgWriter {
	
	private static final int HEADER_LEN = 16;
	private static final int PROTOCOL_VERSION = 18;
	private static final String CLIENT_NAME = "XMMS2DROID";
		
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
		return ByteBuffer.allocate(24);
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
	
	public ByteBuffer generateTrackReqMsg()
	{
		ByteBuffer trackReqMsg = generateSimpleRequest(
				IPCObject.getObjectId(IPCObjects.OUTPUT),
				IPCCommandWrapper.getCommandID(PlayBackIPCCommands.CURRENTID),
				Xmms2Cookies.TRACKREQ_COOKIE);
		return trackReqMsg;
	}
	
	public ByteBuffer generateTrackInfoReqMsg(int id, Boolean playlist)
	{
		//8 Byte List Info
		//8 Byte Track ID Info
		final int totalLen = 16;
		final int cookie = 
			playlist ? Xmms2Cookies.PLAYLIST_TRACKINFO_COOKIE : Xmms2Cookies.TRACKINFOREQ_COOKIE;
		ByteBuffer trackInfoReq = ByteBuffer.allocate(totalLen + HEADER_LEN);
		writeHeader(
				trackInfoReq,
				IPCObject.getObjectId(IPCObjects.MEDIALIB),
				IPCCommandWrapper.getCommandID(MediaLibIPCCommands.INFO),
				cookie,
				totalLen);
		putListHead(trackInfoReq, 1);
		putInt32(trackInfoReq, id);
		trackInfoReq.flip();
		return trackInfoReq;
	}

	/**
	 * Request the play list to move by a relative amount.
	 * @param relativeOffset
	 * @return
	 */
	public ByteBuffer generateListChangeMsg(int relativeOffset) 
	{
		ByteBuffer listChangeReq = ByteBuffer.allocate(32);
		writeHeader(
				listChangeReq,
				IPCObject.getObjectId(IPCObjects.PLAYLIST),
				IPCCommandWrapper.getCommandID(PlayListIPCCommands.SET_POS_REL),
				Xmms2Cookies.LISTCHANGE_REL_COOKIE,
				16);
		putListHead(listChangeReq, 1);
		putInt32(listChangeReq, relativeOffset);
		listChangeReq.flip();
		return listChangeReq;
	}

	/**
	 * Request the play list to move to an absolute position.
	 * @param newPosition
	 * @return
	 */
	public ByteBuffer generateListSetPositionMsg(int newPosition) 
	{
		ByteBuffer listChangeReq = ByteBuffer.allocate(32);
		writeHeader(
				listChangeReq,
				IPCObject.getObjectId(IPCObjects.PLAYLIST),
				IPCCommandWrapper.getCommandID(PlayListIPCCommands.SET_POS),
				Xmms2Cookies.LISTCHANGE_ABS_COOKIE,
				16);
		putListHead(listChangeReq, 1);
		putInt32(listChangeReq, newPosition);
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
	
	public ByteBuffer generateReqPlaybackUpdateMsg()
	{
		final int totalLen = 16;
		ByteBuffer reqPlaybackUpdateMsg = ByteBuffer.allocate(totalLen + HEADER_LEN);
		writeHeader(reqPlaybackUpdateMsg,
				IPCObject.getObjectId(IPCObjects.SIGNAL),
				33,
				Xmms2Cookies.REGPLAYBACKUPDATE_COOKIE,
				totalLen);
		
		putListHead(reqPlaybackUpdateMsg, 1);
		putInt32(reqPlaybackUpdateMsg, IPCSignal.getSignalId(IPCSignals.PLAYBACK_STATUS));
		reqPlaybackUpdateMsg.flip();
		return reqPlaybackUpdateMsg;
	}
	
	public ByteBuffer generateReqTrackUpdateMsg()
	{
		final int totalLen = 16;
		ByteBuffer reqTrackUpdateMsg = ByteBuffer.allocate(totalLen + HEADER_LEN);
		writeHeader(reqTrackUpdateMsg,
				IPCObject.getObjectId(IPCObjects.SIGNAL),
				33,
				Xmms2Cookies.REGTRACKUPDATE_COOKIE,
				totalLen);
		
		putListHead(reqTrackUpdateMsg, 1); 
		putInt32(reqTrackUpdateMsg, IPCSignal.getSignalId(IPCSignals.OUTPUT_CURRENTID));
		reqTrackUpdateMsg.flip();
		return reqTrackUpdateMsg;
	}
	
	public ByteBuffer generatePlayListUpdateMsg(String name)
	{
		final int lenRaw = 16;
		final int totalLen = name.length() + 1 + lenRaw; //Don't forget the \0
		ByteBuffer plUpdateMsg = ByteBuffer.allocate(totalLen + HEADER_LEN);
		
		writeHeader(plUpdateMsg,
				IPCObject.getObjectId(IPCObjects.PLAYLIST),
				IPCCommandWrapper.getCommandID(PlayListIPCCommands.LIST),
				Xmms2Cookies.PLAYLIST_REQUEST_COOKIE,
				totalLen);
		
		putListHead(plUpdateMsg, 1);
		putString(plUpdateMsg, name);
		plUpdateMsg.flip();
		return plUpdateMsg;	
	}
	
	public ByteBuffer generateSimpleRequest(int objectID, int commandID, int cookie)
	{
		ByteBuffer request = allocateMinimalPacket();
		writeHeader(
				request,
				objectID,
				commandID,
				cookie,
				8);
		
		// Add an empty argument list
		request.putInt(6); 
		request.putInt(0);
		request.flip();
		return request;
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
}
