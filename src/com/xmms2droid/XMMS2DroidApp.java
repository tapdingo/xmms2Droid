package com.xmms2droid;

import android.app.Application;
import com.xmms2droid.NetModule;

public class XMMS2DroidApp extends Application{
	
	public NetModule netModule = new NetModule();
	
	@Override
	public void onCreate() {
		super.onCreate();
	}

}
