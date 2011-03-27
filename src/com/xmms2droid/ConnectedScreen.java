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

import com.xmms2droid.xmmsMsgHandling.ServerMsg;
import com.xmms2droid.xmmsMsgHandling.ServerVolumeMessage;
import com.xmms2droid.xmmsMsgHandling.XmmsMsgParser;
import com.xmms2droid.xmmsMsgHandling.XmmsMsgWriter;
import android.os.Bundle;
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
	private Button m_decVolButton = null;
	private XmmsMsgWriter m_msgWriter = new XmmsMsgWriter();
	
	private NetModule m_netModule = null;
	
	private int m_volume = 0;
	private TextView m_volumeView = null;
	private TextView m_playState = null;
	
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
        m_decVolButton = (Button) findViewById(R.id.decVol);
        m_decVolButton.setOnClickListener(decVolListener);
        
        m_volumeView = (TextView) findViewById(R.id.volume);
        m_playState = (TextView) findViewById(R.id.playStatus);
        
        TabHost.TabSpec spec = getTabHost().newTabSpec("tag1");
        spec.setContent(R.id.controls);
        spec.setIndicator("Controls");
        getTabHost().addTab(spec);
        
        spec = getTabHost().newTabSpec("tag2");
        spec.setContent(R.id.playlist);
        spec.setIndicator("Playlist");
        getTabHost().addTab(spec);
        
        getTabHost().setCurrentTab(0);
        
        new Thread(readerTask).start();
        updateVolume();
        updatePlaybackStatus();
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
		}
	};
	
	private View.OnClickListener incVolListener = new View.OnClickListener() {
		@Override
		public void onClick(View arg0) {

		}
	};
	
	private View.OnClickListener decVolListener = new View.OnClickListener() {
		@Override
		public void onClick(View arg0) {
			ByteBuffer decVolMsg = m_msgWriter.generateVolumeMsg(m_volume - 10);
			m_netModule.send(decVolMsg);
		}
	};
	
	private void handleMessage(ServerMsg msg)
	{
		switch (msg.getMsgType())
		{
		case VOLUME_MSG:
			handleVolumeMsg((ServerVolumeMessage) msg);
			break;
		
		}
	}
	
	private void updatePlaybackStatus()
	{
		ByteBuffer reqStatusMsg = m_msgWriter.generateStatusReqMsg();
		m_netModule.send(reqStatusMsg);
	}
	
	private void handleVolumeMsg(ServerVolumeMessage msg)
	{
		HashMap<String, Integer> volumes = msg.getVolumeInformation();
		m_volume = volumes.get("left");
		runOnUiThread(updateVolumeDisplay);
	}
	
	private void updateVolume()
	{
		ByteBuffer volReqMsg = m_msgWriter.generateVolReqMsg();
		m_app.netModule.send(volReqMsg);
	}
	
	private Runnable updateVolumeDisplay = new Runnable()
	{
		@Override
		public void run() {
			m_volumeView.setText(String.valueOf(m_volume));
		}
	};
	
	private Runnable readerTask = new Runnable() {
		
		private ReadHandler m_readHandler = null;
		@Override
		public void run() {
			m_readHandler = new ReadHandler(m_netModule);
			
			while(true)
			{
				if (m_readHandler.readMsg())
				{
					ByteBuffer recHeader = m_readHandler.getHeader();
					ByteBuffer recMsg = m_readHandler.getMsg();
					ServerMsg parsed = XmmsMsgParser.parseMsg(recHeader, recMsg);
					handleMessage(parsed);
					m_readHandler.clear();
				}
			}
		}
	};

}
