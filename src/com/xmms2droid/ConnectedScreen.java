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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.xmms2droid.xmmsMsgHandling.PlayListInfoMsg;
import com.xmms2droid.xmmsMsgHandling.ServerMsg;
import com.xmms2droid.xmmsMsgHandling.ServerStateMsg;
import com.xmms2droid.xmmsMsgHandling.ServerTrackIdMsg;
import com.xmms2droid.xmmsMsgHandling.ServerTrackInfoMsg;
import com.xmms2droid.xmmsMsgHandling.ServerVolumeMsg;
import com.xmms2droid.xmmsMsgHandling.XmmsMsgParser;
import com.xmms2droid.xmmsMsgHandling.XmmsMsgWriter;

public class ConnectedScreen extends Activity {
	
	private XMMS2DroidApp m_app = null;
	private Button m_stopButton = null;
	private Button m_playPauseButton = null;
	private Button m_nextButton = null;
	private Button m_prevButton = null;

	private SeekBar m_volumeBar = null;
	
	private XmmsMsgWriter m_msgWriter = new XmmsMsgWriter();
	
	private NetModule m_netModule = null;
	private boolean m_paused = true;

	private Drawable m_playDrawable = null;
	private Drawable m_pauseDrawable = null;
	
	private int m_volume = 0;
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
        m_playPauseButton = (Button) findViewById(R.id.playPauseButton);
        m_playPauseButton.setOnClickListener(playPauseListener);
        
        m_volumeBar = (SeekBar) findViewById(R.id.volumeBar);
        m_volumeBar.setOnSeekBarChangeListener(changeVolumeListener);

        m_nextButton = (Button) findViewById(R.id.next);
        m_nextButton.setOnClickListener(nextListener);
        m_prevButton = (Button) findViewById(R.id.prev);
        m_prevButton.setOnClickListener(prevListener);
        
        m_volumeView = (TextView) findViewById(R.id.volume);
        m_artistView = (TextView) findViewById(R.id.artist);
        m_titleView = (TextView) findViewById(R.id.track);
        
        m_playListView = (ListView) findViewById(R.id.playlist);
        m_playListView.setClickable(true);
        m_playListView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);

        m_playDrawable = getResources().getDrawable(R.drawable.play);
        m_pauseDrawable = getResources().getDrawable(R.drawable.pause);
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
    
    private View.OnClickListener playPauseListener = new View.OnClickListener() {
		@Override
		public void onClick(View arg0) {
			ByteBuffer playPauseMsg = (m_paused ? m_msgWriter.generatePlayMsg() : m_msgWriter.generatePauseMsg());
			m_netModule.send(playPauseMsg);
			m_paused = !m_paused;
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
	
	private SeekBar.OnSeekBarChangeListener changeVolumeListener = new SeekBar.OnSeekBarChangeListener() {

		boolean suppressSendProgressChanges = false;
		
		@Override
		public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
			m_volume = progress;
			if ( !suppressSendProgressChanges ) setVolume();
			m_volumeView.setText(Integer.toString(m_volume));
		}

		@Override
		public void onStartTrackingTouch(SeekBar seekBar) {
			suppressSendProgressChanges = true;
		}

		@Override
		public void onStopTrackingTouch(SeekBar seekBar) {
			suppressSendProgressChanges = false;
			setVolume();
		}
		
		private void setVolume() {
			ByteBuffer setVolMsgLeft = m_msgWriter.generateVolumeMsg(m_volume, "left");
			m_netModule.send(setVolMsgLeft);
			ByteBuffer setVolMsgRight= m_msgWriter.generateVolumeMsg(m_volume, "right");
			m_netModule.send(setVolMsgRight);
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
		if ( volumes == null ) {
			Log.w(XMMS2DroidApp.TAG,"Could not get volume information");
			return;
		}
		if ( volumes.containsKey("master") ) 		m_volume = volumes.get("master");
		else if ( volumes.containsKey("front") ) 	m_volume = volumes.get("front");
		else if ( volumes.containsKey("pcm") ) 		m_volume = volumes.get("pcm");
		else if ( volumes.containsKey("left") ) 	m_volume = volumes.get("left");
		else if ( volumes.size() > 0 ) {
			for( Entry<String,Integer> volume : volumes.entrySet() ) {
				m_volume = volume.getValue();
				break;
			}
		}
		else {
			Log.w(XMMS2DroidApp.TAG,"Got empty volume information :-(");
			return;
		}
		runOnUiThread(updateVolumeDisplay);
	}
	
	private void handleTrackInfoMsg(ServerTrackInfoMsg msg)
	{
		HashMap<String, Object> artistMap = msg.getTrackInfo().get("artist");
		String pluginKey = "plugin/id3v2";
		String artist = "unknown";
		for( Entry<String,Object> artistEntry : artistMap.entrySet() ) {
			if ( artistEntry.getKey().startsWith("plugin/") ) {
				artist = (String) artistEntry.getValue();
				pluginKey = artistEntry.getKey(); 
			}
		}
		final String song = (String) msg.getTrackInfo().get("title").get(pluginKey);

		if ( !msg.getPlayListInfo() ) {
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
		String playState = msg.getState();
		m_paused = !(playState.equalsIgnoreCase("playing"));
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
			m_volumeBar.setProgress(m_volume);
		}
	};
	
	private Runnable updatePlaybackStateDisplay = new Runnable()
	{

		@Override
		public void run() {
			m_playPauseButton.setBackgroundDrawable(m_paused ? m_playDrawable : m_pauseDrawable );
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
	
	private Runnable updatePlayListDisplay = new Runnable() {
		@Override
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
		@Override
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
