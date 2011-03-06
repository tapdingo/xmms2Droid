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

}
