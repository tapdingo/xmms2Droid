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

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

import android.util.Log;

public class NetModule {
	private SocketChannel m_sockChannel = null;
	private String m_tag = "NETMODULE";
	
	public Boolean connect(String tgtIp, int tgtPort) throws Exception
	{
		m_sockChannel = SocketChannel.open();
		m_sockChannel.connect(new InetSocketAddress(tgtIp,tgtPort));
		
		if (!m_sockChannel.isConnected())
		{
			return false;
		}
		else 
		{
			return true;
		}
	}
	
	public void send(ByteBuffer msg)
	{
		try {
			m_sockChannel.write(msg);
		} catch (IOException e) {
			Log.d(m_tag, "Error writing to socket!");
			e.printStackTrace();
		}
	}
	
	public Boolean disconnect() throws Exception
	{
		m_sockChannel.close();
		return true;
	}
	
	public int read(ByteBuffer tgt)
	{
		int readBytes = 0;
		try {
			readBytes = m_sockChannel.read(tgt);
		} catch (IOException e) {
			Log.d(m_tag, "ERROR READING FROM SOCKET");
			e.printStackTrace();
		}
		return readBytes;
	}

}
