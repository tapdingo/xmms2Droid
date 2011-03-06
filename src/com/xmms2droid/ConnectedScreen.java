package com.xmms2droid;

import java.nio.ByteBuffer;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TabHost;
import android.app.TabActivity;

public class ConnectedScreen extends TabActivity {
	
	private Button m_stopButton = null;
	private Button m_startButton = null;
	private Button m_pauseButton = null;
	private xmmsMsgWriter m_msgWriter = new xmmsMsgWriter();
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        setContentView(R.layout.connected);
        m_stopButton = (Button) findViewById(R.id.stopButton);
        m_stopButton.setOnClickListener(stopListener);
        m_startButton = (Button) findViewById(R.id.playButton);
        m_startButton.setOnClickListener(startListener);
        m_pauseButton = (Button) findViewById(R.id.pauseButton);
        m_pauseButton.setOnClickListener(pauseListener);
        
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
			((XMMS2DroidApp) getApplication()).netModule.send(startMsg);
			
		}
	};
    
    private View.OnClickListener stopListener = new View.OnClickListener() {	
		@Override
		public void onClick(View arg0) {
			ByteBuffer stopMsg = m_msgWriter.generateStopMsg();
			((XMMS2DroidApp) getApplication()).netModule.send(stopMsg);			
		}
	};
	
	private View.OnClickListener pauseListener = new View.OnClickListener() {
		
		@Override
		public void onClick(View arg0) {
			ByteBuffer pauseMsg = m_msgWriter.generatePauseMsg();
			((XMMS2DroidApp) getApplication()).netModule.send(pauseMsg);
			
		}
	};
	
	private View.OnClickListener setVolListener = new View.OnClickListener() {
		@Override
		public void onClick(View arg0) {
			ByteBuffer volReqMsg = m_msgWriter.generateVolReqMsg();
			((XMMS2DroidApp) getApplication()).netModule.send(volReqMsg);
			
			
		}
	};

}
