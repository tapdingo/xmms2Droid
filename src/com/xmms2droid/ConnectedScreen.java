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
import java.text.ChoiceFormat;
import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import com.xmms2droid.xmmsMsgHandling.IPCCommandWrapper;
import com.xmms2droid.xmmsMsgHandling.PlayListInfoMsg;
import com.xmms2droid.xmmsMsgHandling.ServerMsg;
import com.xmms2droid.xmmsMsgHandling.ServerStateMsg;
import com.xmms2droid.xmmsMsgHandling.ServerTrackIdMsg;
import com.xmms2droid.xmmsMsgHandling.ServerTrackInfoMsg;
import com.xmms2droid.xmmsMsgHandling.ServerVolumeMsg;
import com.xmms2droid.xmmsMsgHandling.XmmsMsgParser;
import com.xmms2droid.xmmsMsgHandling.XmmsMsgWriter;
import android.os.Bundle;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TabHost;
import android.widget.TextView;
import android.app.Dialog;
import android.app.ProgressDialog;
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
	private ListView m_playListView = null;
	private ArrayList<String> m_playList = new ArrayList<String>(); 
	private ArrayAdapter<String> m_playListAdapter = null;
	private HashMap<Integer, String> m_tracks = new HashMap<Integer, String>(); //Holds information about tracks;
	private ArrayList<Integer> m_trackIds = new ArrayList<Integer>();
	
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
        m_muteButton = (Button) findViewById(R.id.mute);
        m_muteButton.setOnClickListener(muteListener);
        m_nextButton = (Button) findViewById(R.id.next);
        m_nextButton.setOnClickListener(nextListener);
        m_prevButton = (Button) findViewById(R.id.prev);
        m_prevButton.setOnClickListener(prevListener);
        
        m_volumeView = (TextView) findViewById(R.id.volume);
        m_playStateView = (TextView) findViewById(R.id.playStatus);
        m_artistView = (TextView) findViewById(R.id.artist);
        m_titleView = (TextView) findViewById(R.id.track);
        
        m_playListView = (ListView) findViewById(R.id.playlist);
        m_playListView.setClickable(true);
        m_playListView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        
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
        updatePlaylist();
        updateVolume();
        updatePlaybackStatus();
        updatePlayingTrack();
        registerPlayBackUpdate();
        registerTrackUpdate();
        
        
        m_playListAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, m_playList);
        m_playListView.setAdapter(m_playListAdapter);
    }
    
    @Override
    public Dialog onCreateDialog(int dialogId)
    {
    	ProgressDialog dialog = new ProgressDialog(this);
    	dialog.setTitle("Loading Playlist...");
    	dialog.setMessage("Updating PlaylistInformation");
    	return dialog;
    }
    
 private View.OnClickListener startListener = new View.OnClickListener() {
		public void onClick(View arg0) {
			ByteBuffer startMsg = m_msgWriter.generatePlayMsg();
			m_netModule.send(startMsg);
			updatePlaybackStatus();	
		}
	};
	
	private View.OnClickListener nextListener = new View.OnClickListener() {
		
		public void onClick(View v) {
			ByteBuffer nextMsg = m_msgWriter.generateListChangeMsg(1);
			m_netModule.send(nextMsg);
			ByteBuffer tickleMsg = m_msgWriter.generateTickleMsg();
			m_netModule.send(tickleMsg);
		}
	};
	
	private View.OnClickListener prevListener = new View.OnClickListener() {
		
		public void onClick(View v) {
			ByteBuffer nextMsg = m_msgWriter.generateListChangeMsg(-1);
			m_netModule.send(nextMsg);
			ByteBuffer tickleMsg = m_msgWriter.generateTickleMsg();
			m_netModule.send(tickleMsg);
		}
	};
    
    private View.OnClickListener stopListener = new View.OnClickListener() {	
		public void onClick(View arg0) {
			ByteBuffer stopMsg = m_msgWriter.generateStopMsg();
			m_netModule.send(stopMsg);
			updatePlaybackStatus();
		}
	};
	
	private View.OnClickListener pauseListener = new View.OnClickListener() {
		
		public void onClick(View arg0) {
			ByteBuffer pauseMsg = m_msgWriter.generatePauseMsg();
			m_netModule.send(pauseMsg);
			updatePlaybackStatus();
		}
	};
	
	private View.OnClickListener incVolListener = new View.OnClickListener() {
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
		case PLAYLIST_INFO_MSG:
			handlePlayListInfoMsg ((PlayListInfoMsg) msg);
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
	
	private void updatePlaylist()
	{
		ByteBuffer playListMsg = m_msgWriter.generatePlayListUpdateMsg("Default");
		m_app.netModule.send(playListMsg);
	}
	
	private void registerTrackUpdate()
	{
		ByteBuffer reqTrackUpdateMsg = m_msgWriter.generateReqTrackUpdateMsg();
		m_app.netModule.send(reqTrackUpdateMsg);
	}
	
	private void requestTrackInfo(int id, Boolean playlist)
	{
		if (0 == id)
		{
			return;
		}
		ByteBuffer trackInfoMsg = m_msgWriter.generateTrackInfoReqMsg(id, playlist);
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
		final String artist = (String) msg.getTrackInfo().get("artist").get("plugin/id3v2");
		final String song = (String) msg.getTrackInfo().get("title").get("plugin/id3v2");
		if (!msg.getPlayListInfo())
		{
			m_curArtist = artist;
			m_curSong = song;
			runOnUiThread(updateTrackDisplay);
		}
		else
		{
			int id = (Integer) msg.getTrackInfo().get("id").get("server");
			m_tracks.put(id, artist + " - " + song);
			runOnUiThread(updatePlayListDisplay);
		}
	}
	
	private void handlePlayListInfoMsg(PlayListInfoMsg msg)
	{
		m_trackIds = msg.ids;
		runOnUiThread(updatePlayListInformation);
		runOnUiThread(updatePlayListDisplay);
	}
	
	private void handleTrackIdMsg(ServerTrackIdMsg msg)
	{
		requestTrackInfo(msg.getId(), false);
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
		public void run() {
			m_volumeView.setText(String.valueOf(m_volume));
		}
	};
	
	private Runnable updatePlaybackStateDisplay = new Runnable()
	{

		public void run() {
			m_playStateView.setText(m_playState);
		}
	};
	
	private Runnable updateTrackDisplay = new Runnable()
	{

		public void run() {
			m_artistView.setText(m_curArtist);
			m_titleView.setText(m_curSong);
		}
	};
	
	private Runnable updatePlayListDisplay = new Runnable() {
		public void run() {
			int len = m_trackIds.size();
			m_playListAdapter.clear();
			
			for (int i = 0; i < len; i++)
			{
				int id = m_trackIds.get(i);		
				if (m_tracks.containsKey(id))
				{
					String trackInfo = m_tracks.get(id);
					m_playListAdapter.add(trackInfo);
				}
				else
				{
					m_playListAdapter.add("UNKNOWN");
					m_playListView.setSelection(5);
				}
			}
		}
	};
	
	private Runnable updatePlayListInformation = new Runnable() {
		public void run() {
			showDialog(0);
			
			for (int i = 0; i<m_trackIds.size(); i++)
			{
				if (!m_tracks.containsKey(i))
				{
					requestTrackInfo(m_trackIds.get(i), true);
				}
			}
			dismissDialog(0);
		}
	};
	
	private Runnable readerTask = new Runnable() {
		
		private ReadHandler m_readHandler = null;
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
