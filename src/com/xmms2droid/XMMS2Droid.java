package com.xmms2droid;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;

public class XMMS2Droid extends Activity {

	public static SharedPreferences prefs;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

		prefs = PreferenceManager.getDefaultSharedPreferences(this);
		Intent newIntent = new Intent(XMMS2Droid.this, ( connectToServer() ? ConnectedScreen.class : StartScreen.class ) );
		startActivity(newIntent);
    }

    private boolean connectToServer() {
		try {
			String serverIp = prefs.getString(StartScreen.KEY_SERVER_IP, null);
			int serverPort = Integer.parseInt(prefs.getString(StartScreen.KEY_SERVER_PORT, "9997"));
			return ( ((XMMS2DroidApp) getApplication()).netModule.connect( serverIp, serverPort));
		} catch (Exception e) {
			Log.w(XMMS2DroidApp.TAG,Log.getStackTraceString(e));
		}
		return false;
    }
}
