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
