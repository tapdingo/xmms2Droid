package com.xmms2droid;

import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.util.HashMap;

public class DictParser {
	
	public static HashMap<String, Integer> parseDict(ByteBuffer buf)
	{
		buf.flip();
		
		//Currently we only use the implicit lengths
		//Should be fixed for a more robust approach
		parseHeader(buf);
		
		//TypeBla
		//TODO Make a list of response types
		buf.getInt();
		//LenBla
		int len = buf.getInt();
		
		HashMap<String, Integer> volumes = new HashMap<String, Integer>();
		
		for (int i = 0; i < len; i++)
		{
			String key = getString(buf);
			
			//TypeIdBla
			buf.getInt();
			
			int val = buf.getInt();	
			volumes.put(key, val);
		}
		return volumes;
	}
	
	private static String getString(ByteBuffer buf)
	{
		String recString = "";
		
		int len = buf.getInt();
		byte [] strTmp = new byte[len];
		
		buf.get(strTmp);
		
		if (strTmp[len - 1] == 0)
		{
			try {
				recString = new String(strTmp, 0, len-1, "utf-8");
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		else
		{
			try {
				recString = new String(strTmp, 0, len, "utf-8");
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
		
		return recString;
		
	}
	private static int parseHeader(ByteBuffer buf)
	{
		//Currently we're not interested in header values of the response...
		buf.getInt(); //Object
		buf.getInt(); //Command
		buf.getInt(); //Cookie
		int payloadLen = buf.getInt();
		return payloadLen;
	}

}
