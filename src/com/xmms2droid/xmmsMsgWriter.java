package com.xmms2droid;

import java.nio.ByteBuffer;

public class xmmsMsgWriter {
	
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
				1,
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
