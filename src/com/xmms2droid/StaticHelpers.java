package com.xmms2droid;

public class StaticHelpers {


	static final char[] HEXES = new char[] {'0','1','2','3','4','5','6','7','8','9','a','b','c','d','e','f'};
	/**
	 * Convert a byte array into a hex string
	 * @param raw
	 * @return
	 */
	public static String toHexString( byte[] raw ) {
		if (raw == null) {
			return null;
		}
		final StringBuilder hex = new StringBuilder(2 * raw.length);
		for (final byte b : raw) {
			hex.append(HEXES[(b & 0xF0) >> 4]).append(HEXES[(b & 0x0F)]);
		}
		return hex.toString();
	}
	
}
