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

import com.xmms2droid.xmmsMsgHandling.IPCCommandWrapper;
import com.xmms2droid.xmmsMsgHandling.ServerMsg;
import com.xmms2droid.xmmsMsgHandling.ServerStateMsg;
import com.xmms2droid.xmmsMsgHandling.ServerTrackIdMsg;
import com.xmms2droid.xmmsMsgHandling.ServerTrackInfoMsg;
import com.xmms2droid.xmmsMsgHandling.ServerVolumeMsg;
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
	private Button m_nextButton = null;
	private Button m_prevButton = null;
	private XmmsMsgWriter m_msgWriter = new XmmsMsgWriter();
	
	private NetModule m_netModule = null;
	private boolean m_muted = false;
	private Button m_muteButton = null;
	
	private int m_volume = 0;
	private String m_playState = "UNKNOWN";
	private TextView m_volumeView = null;
	private TextView m_playStateView = null;
	private String m_curSong = "UNKNOWN";
	private String m_curArtist = "UNKNWON";
	private TextView m_artistView = null;
	private TextView m_titleView = null;
	
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
        
      //\TODO UPDATE ME
        m_incVolButton = (Button) findViewById(R.id.incVol);
        m_incVolButton.setOnClickListener(incVolListener);
        m_decVolButton = (Button) findViewById(R.id.decVol);
        m_decVolButton.setOnClickListener(decVolListener);
        m_muteButton = (Button) findViewById(R.id.mute);
        m_muteButton.setOnClickListener(muteListener);
        //m_nextButton = (Button) findViewById(R.id.next);
        //m_nextButton.setOnClickListener(nextListener);
        //m_prevButton = (Button) findViewById(R.id.prev);
        //m_prevButton.setOnClickListener(prevListener);
        
        m_volumeView = (TextView) findViewById(R.id.volume);
        m_playStateView = (TextView) findViewById(R.id.playStatus);
        m_artistView = (TextView) findViewById(R.id.artist);
        m_titleView = (TextView) findViewById(R.id.track);
        
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
        sayHello();
        updateVolume();
        updatePlaybackStatus();
       //updatePlayingTrack();
       //registerPlayBackUpdate();
    }
    
 private View.OnClickListener startListener = new View.OnClickListener() {
		@Override
		public void onClick(View arg0) {
			ByteBuffer startMsg = m_msgWriter.generatePlayMsg();
			m_netModule.send(startMsg);
			updatePlaybackStatus();	
		}
	};
	
	private View.OnClickListener nextListener = new View.OnClickListener() {
		
		@Override
		public void onClick(View v) {
			ByteBuffer nextMsg = m_msgWriter.generateListChangeMsg(1);
			m_netModule.send(nextMsg);
			ByteBuffer tickleMsg = m_msgWriter.generateTickleMsg();
			m_netModule.send(tickleMsg);
		}
	};
	
	private View.OnClickListener prevListener = new View.OnClickListener() {
		
		@Override
		public void onClick(View v) {
			ByteBuffer nextMsg = m_msgWriter.generateListChangeMsg(-1);
			m_netModule.send(nextMsg);
			ByteBuffer tickleMsg = m_msgWriter.generateTickleMsg();
			m_netModule.send(tickleMsg);
		}
	};
    
    private View.OnClickListener stopListener = new View.OnClickListener() {	
		@Override
		public void onClick(View arg0) {
			ByteBuffer stopMsg = m_msgWriter.generateStopMsg();
			m_netModule.send(stopMsg);
			updatePlaybackStatus();
		}
	};
	
	private View.OnClickListener pauseListener = new View.OnClickListener() {
		
		@Override
		public void onClick(View arg0) {
			ByteBuffer pauseMsg = m_msgWriter.generatePauseMsg();
			m_netModule.send(pauseMsg);
			updatePlaybackStatus();
		}
	};
	
	private View.OnClickListener incVolListener = new View.OnClickListener() {
		@Override
		public void onClick(View arg0) {
			m_muted = false;
			ByteBuffer incVolMsgLeft = m_msgWriter.generateVolumeMsg(m_volume + 10, "left");
			m_netModule.send(incVolMsgLeft);
			ByteBuffer incVolMsgRight = m_msgWriter.generateVolumeMsg(m_volume + 10, "right");
			m_netModule.send(incVolMsgRight);
			updateVolume();
			m_muteButton.setText("Mute");
		}
	};
	
	private View.OnClickListener decVolListener = new View.OnClickListener() {
		@Override
		public void onClick(View arg0) {
			m_muted = false;
			ByteBuffer decVolMsgLeft = m_msgWriter.generateVolumeMsg(m_volume - 10, "left");
			m_netModule.send(decVolMsgLeft);
			ByteBuffer decVolMsgRight= m_msgWriter.generateVolumeMsg(m_volume - 10, "right");
			m_netModule.send(decVolMsgRight);
			updateVolume();
			m_muteButton.setText("Mute");
		}
	};
	
	private View.OnClickListener muteListener = new View.OnClickListener() {
		@Override
		public void onClick(View arg0) {
			
			int newVol = 0;
			
			if (m_muted)
			{
				newVol = m_volume;
				m_muted = false;
				m_muteButton.setText("Mute");
			}
			else
			{
				newVol = 0;
				m_muted = true;
				m_muteButton.setText("Unmute");
			}
			ByteBuffer decVolMsgLeft = m_msgWriter.generateVolumeMsg(newVol, "left");
			m_netModule.send(decVolMsgLeft);
			ByteBuffer decVolMsgRight= m_msgWriter.generateVolumeMsg(newVol, "right");
			m_netModule.send(decVolMsgRight);
		}
	};
	
	private void handleMessage(ServerMsg msg)
	{
		switch (msg.getMsgType())
		{
		case VOLUME_MSG:
			handleVolumeMsg((ServerVolumeMsg) msg);
			break;
		case PLAYBACKSTATE_MSG:
			handlePlaybackStateMsg((ServerStateMsg) msg);
			break;
		case TRACKID_MSG:
			handleTrackIdMsg((ServerTrackIdMsg) msg);
			break;
		case TRACKINFO_MSG:
			handleTrackInfoMsg((ServerTrackInfoMsg) msg);
			break;
		}
	}
	
	private void sayHello()
	{
		ByteBuffer helloMsg = m_msgWriter.generateHelloMsg();
		m_app.netModule.send(helloMsg);
	}
	
	private void registerPlayBackUpdate()
	{
		ByteBuffer reqPlayUpdateMsg = m_msgWriter.generateReqPlaybackUpdateMsg();
		m_app.netModule.send(reqPlayUpdateMsg);
	}
	
	private void requestTrackInfo(int id)
	{
		ByteBuffer trackInfoMsg = m_msgWriter.generateTrackInfoReqMsg(id);
		m_netModule.send(trackInfoMsg);
	}
	
	private void updatePlaybackStatus()
	{
		ByteBuffer reqStatusMsg = m_msgWriter.generateStatusReqMsg();
		m_netModule.send(reqStatusMsg);
	}
	
	private void handleVolumeMsg(ServerVolumeMsg msg)
	{
		HashMap<String, Integer> volumes = msg.getVolumeInformation();
		m_volume = volumes.get("left");
		runOnUiThread(updateVolumeDisplay);
	}
	
	private void handleTrackInfoMsg(ServerTrackInfoMsg msg)
	{
		m_curArtist = (String) msg.getTrackInfo().get("plugin/id3v2").get("artist");
		m_curSong = (String) msg.getTrackInfo().get("plugin/id3v2").get("title");
		runOnUiThread(updateTrackDisplay);
	}
	
	private void handleTrackIdMsg(ServerTrackIdMsg msg)
	{
		requestTrackInfo(msg.getId());
	}
	
	private void handlePlaybackStateMsg(ServerStateMsg msg)
	{
		m_playState = msg.getState();
		runOnUiThread(updatePlaybackStateDisplay);
	}
	
	private void updateVolume()
	{
		ByteBuffer volReqMsg = m_msgWriter.generateVolReqMsg();
		m_app.netModule.send(volReqMsg);
	}
	
	private void updatePlayingTrack()
	{
		ByteBuffer trackReqMsg = m_msgWriter.generateTrackReqMsg();
		m_app.netModule.send(trackReqMsg);
	}
	
	private Runnable updateVolumeDisplay = new Runnable()
	{
		@Override
		public void run() {
			m_volumeView.setText(String.valueOf(m_volume));
		}
	};
	
	private Runnable updatePlaybackStateDisplay = new Runnable()
	{

		@Override
		public void run() {
			m_playStateView.setText(m_playState);
		}
	};
	
	private Runnable updateTrackDisplay = new Runnable()
	{

		@Override
		public void run() {
			m_artistView.setText(m_curArtist);
			m_titleView.setText(m_curSong);
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
