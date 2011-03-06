package com.xmms2droid;

import java.nio.ByteBuffer;
import java.util.HashMap;

import android.os.Bundle;
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
	private xmmsMsgWriter m_msgWriter = new xmmsMsgWriter();
	
	private TextView m_leftVol = null;
	private TextView m_rightVol = null;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        m_app = (XMMS2DroidApp) getApplication();
        
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
    }
    
 private View.OnClickListener startListener = new View.OnClickListener() {
		@Override
		public void onClick(View arg0) {
			ByteBuffer startMsg = m_msgWriter.generatePlayMsg();
			m_app.netModule.send(startMsg);
			
		}
	};
    
    private View.OnClickListener stopListener = new View.OnClickListener() {	
		@Override
		public void onClick(View arg0) {
			ByteBuffer stopMsg = m_msgWriter.generateStopMsg();
			m_app.netModule.send(stopMsg);			
		}
	};
	
	private View.OnClickListener pauseListener = new View.OnClickListener() {
		
		@Override
		public void onClick(View arg0) {
			ByteBuffer pauseMsg = m_msgWriter.generatePauseMsg();
			((XMMS2DroidApp) getApplication()).netModule.send(pauseMsg);
			
		}
	};
	
	private View.OnClickListener incVolListener = new View.OnClickListener() {
		@Override
		public void onClick(View arg0) {
			
			//TODO Request the current volume at startup instead of on demand
			ByteBuffer volReqMsg = m_msgWriter.generateVolReqMsg();
			m_app.netModule.send(volReqMsg);
						
			ByteBuffer resp = ByteBuffer.allocate(1024);
			int bytesRead = m_app.netModule.read(resp);
			HashMap<String, Integer> volumes = DictParser.parseDict(resp);
			
			m_leftVol.setText(String.valueOf(volumes.get("left")));
			m_rightVol.setText(String.valueOf(volumes.get("right")));
			
			Log.d("CON_SCREEN", "READ COMPLETE");
		}
	};

}
