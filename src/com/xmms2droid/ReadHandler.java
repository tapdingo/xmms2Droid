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

package com.xmms2droid;

import java.nio.ByteBuffer;

import android.util.Log;

import com.xmms2droid.xmmsMsgHandling.XmmsHeaderParser;

public class ReadHandler {
	
	private NetModule m_netModule = null;
	private ByteBuffer m_headBuffer = ByteBuffer.allocate(16); //16 Byte Header length
	private ByteBuffer m_msgBuffer = null; //will be allocated to actual msg size
	private int m_readBytes = 0;
	public boolean m_gotHeader = false;
	
	public ReadHandler(NetModule netMod) {
		m_netModule = netMod;
	}
	
	public Boolean readMsg()
	{
		if (!m_gotHeader)
		{
			readHeader();
		}
		else
		{
			if (readPayload())
			{
				return true;
			}
		}
		return false;
	}
	
	private void readHeader()
	{
		m_readBytes += m_netModule.read(m_headBuffer);
		
		//Header has been received completely
		if ( 0 == m_headBuffer.remaining())
		{
			m_msgBuffer = ByteBuffer.allocate(XmmsHeaderParser.getPayloadLen(m_headBuffer));
			m_headBuffer.clear();
		}
		m_gotHeader = true;
	}
	
	private Boolean readPayload()
	{
		m_readBytes += m_netModule.read(m_msgBuffer);
		Log.d("ReadHandler", "Got " + m_readBytes + "Bytes");
		if ( 0 == m_msgBuffer.remaining())
		{
			Log.d("ReadHandler", "Completed Payload!");
			m_gotHeader = false;
			return true;
		}
		return false;
	}
	
	
	public ByteBuffer getMsg()
	{
		return m_msgBuffer.duplicate();
	}
	
	public int getReadBytes()
	{
		return m_readBytes;
	}
}
