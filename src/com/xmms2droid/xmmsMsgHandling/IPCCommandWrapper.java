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
import android.util.Log; 


enum MainIPCCommands {
	 HELLO,
	 QUIT,
	 PLUGIN_LIST,
	 STATS
}

enum PlayListIPCCommands {
	SHUFFLE,
	SET_POS,
	SET_POS_REL,
	ADD_URL,
	ADD_ID,
	ADD_IDLIST,
	ADD_COLL,
	REMOVE_ENTRY,
	MOVE_ENTRY,
	CLEAR,
	SORT,
	LIST,
	CURRENT_POS,
	CURRENT_ACTIVE,
	INSERT_URL,
	INSERT_ID,
	INSERT_COLL,
	LOAD,
	RADD,
	RINSERT
}


enum PlayBackIPCCommands {
	START,
	STOP,
	PAUSE,
	DECODER_KILL,
	CPLAYTIME,
	SEEKMS,
	SEEKMS_REL,
	SEEKSAMPLES,
	SEEKSAMPLES_REL,
	OUTPUT_STATUS,
	CURRENTID,
	VOLSET,
	VOLGET,
	INFO,
	UNKNOWN
}

public class IPCCommandWrapper {
	
	private static final int shiftFac = 32;
	
	public static int getCommandID(java.lang.Enum cmd)
	{
		Log.d("Wrapper", "Command: " + cmd.ordinal() + shiftFac);
		return cmd.ordinal() + shiftFac;
	}
	
	public static PlayBackIPCCommands getCommand(int id)
	{
		int ordinal = id - shiftFac;
		return PlayBackIPCCommands.values()[ordinal];
	}
}