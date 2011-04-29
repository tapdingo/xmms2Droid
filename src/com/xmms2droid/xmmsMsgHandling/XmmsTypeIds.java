package com.xmms2droid.xmmsMsgHandling;

enum XmmsTypes {
	NONE,
	ERROR,
	INT32,
	STRING,
	COLL,
	BIN,
	LIST,
	DICT,
	END
}

public class XmmsTypeIds {
	
	public static int getTypeId(XmmsTypes type)
	{
		return type.ordinal();
	}
	
	public static XmmsTypes getCommand(int id)
	{
		return XmmsTypes.values()[id];
	}
}
