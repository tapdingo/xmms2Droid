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
import java.util.HashMap;

import com.xmms2droid.xmmsMsgHandling.XmmsMsgWriter;

import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TabHost;
import android.widget.TextView;
import android.app.TabActivity;

public class ConnectedScreen extends TabActivity {
	
	private XMMS2DroidApp m_app = null;
	private Button m_stopButton = null;
	private Button m_startButton = null;
	private Button m_pauseButton = null;
	private Button m_incVolButton = null;
	private XmmsMsgWriter m_msgWriter = new XmmsMsgWriter();
	
	private NetModule m_netModule = null;
	
	private TextView m_leftVol = null;
	private TextView m_rightVol = null;
	
	private int m_leftVolume = 0;
	private int m_rightVolume = 0;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        m_app = (XMMS2DroidApp) getApplication();
        m_netModule = m_app.netModule;
        
        setContentView(R.layout.connected);
        m_stopButton = (Button) findViewById(R.id.stopButton);
        m_stopButton.setOnClickListener(stopListener);
        m_startButton = (Button) findViewById(R.id.playButton);
        m_startButton.setOnClickListener(startListener);
        m_pauseButton = (Button) findViewById(R.id.pauseButton);
        m_pauseButton.setOnClickListener(pauseListener);
        m_incVolButton = (Button) findViewById(R.id.incVol);
        m_incVolButton.setOnClickListener(incVolListener);
        
        m_leftVol = (TextView) findViewById(R.id.leftVol);
        m_rightVol = (TextView) findViewById(R.id.rightVol);
        
        TabHost.TabSpec spec = getTabHost().newTabSpec("tag1");
        spec.setContent(R.id.controls);
        spec.setIndicator("Controls");
        getTabHost().addTab(spec);
        
        spec = getTabHost().newTabSpec("tag2");
        spec.setContent(R.id.playlist);
        spec.setIndicator("Playlist");
        getTabHost().addTab(spec);
        
        getTabHost().setCurrentTab(0);
        
        updateVolume();
        
        new Thread(readerTask).start();
    }
    
 private View.OnClickListener startListener = new View.OnClickListener() {
		@Override
		public void onClick(View arg0) {
			ByteBuffer startMsg = m_msgWriter.generatePlayMsg();
			m_netModule.send(startMsg);
			
		}
	};
    
    private View.OnClickListener stopListener = new View.OnClickListener() {	
		@Override
		public void onClick(View arg0) {
			ByteBuffer stopMsg = m_msgWriter.generateStopMsg();
			m_netModule.send(stopMsg);
		}
	};
	
	private View.OnClickListener pauseListener = new View.OnClickListener() {
		
		@Override
		public void onClick(View arg0) {
			ByteBuffer pauseMsg = m_msgWriter.generatePauseMsg();
			m_netModule.send(pauseMsg);
			ByteBuffer resp = ByteBuffer.allocate(1024);		
			m_app.netModule.read(resp);
		}
	};
	
	private View.OnClickListener incVolListener = new View.OnClickListener() {
		@Override
		public void onClick(View arg0) {
			Log.d("CON_SCREEN", "READ COMPLETE");
		}
	};
	
	//TODO Move this to the rest of the parsing section...
	private void updateVolume()
	{
		ByteBuffer volReqMsg = m_msgWriter.generateVolReqMsg();
		m_app.netModule.send(volReqMsg);
		
		ByteBuffer resp = ByteBuffer.allocate(1024);
		m_app.netModule.read(resp);
		HashMap<String, Integer> volumes = DictParser.parseDict(resp);
		
		m_leftVolume = volumes.get("left");
		m_rightVolume = volumes.get("right");
		m_leftVol.setText(String.valueOf(m_leftVolume));
		m_rightVol.setText(String.valueOf(m_rightVolume));
	}
	
	private Runnable readerTask = new Runnable() {
		
		private ReadHandler m_readHandler = null;
		@Override
		public void run() {
			m_readHandler = new ReadHandler(m_netModule);
			
			while(true)
			{
				//Sleep a little while before trying to read again...
				//Maybe lower thread priority might also do the trick...
				SystemClock.sleep(200);
				
				if (m_readHandler.readMsg())
				{
					ByteBuffer msg = m_readHandler.getMsg();
					msg.reset();	
				}
			}
		}
	};

}
