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

import java.nio.ByteBuffer;


public class XmmsMsgWriter {
	
	public ByteBuffer generatePlayMsg()
	{
		ByteBuffer playMsg = allocateHeader();
		writeHeader(
				playMsg,
				IPCObject.getObjectId(IPCObjects.OUTPUT),
				IPCCommand.getCommandId(IPCCommands.START),
				1,
				0);	
		playMsg.flip();
		return playMsg;
	}
	
	public ByteBuffer generateStopMsg()
	{
		ByteBuffer stopMsg = allocateHeader();
		writeHeader(
				stopMsg,
				IPCObject.getObjectId(IPCObjects.OUTPUT),
				IPCCommand.getCommandId(IPCCommands.STOP),
				10,
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
				1,
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
				127,
				0);	
		
		volReqMsg.flip();
		return volReqMsg;
	}
	
	private void writeHeader(ByteBuffer tgt, int obj, int cmd, int cookie, int payload)
	{
		//TODO HTON?
		tgt.putInt(obj);
		tgt.putInt(cmd);
		tgt.putInt(cookie);
		tgt.putInt(payload);
	}
	
	private ByteBuffer allocateHeader()
	{
		return ByteBuffer.allocate(16);
	}

}
