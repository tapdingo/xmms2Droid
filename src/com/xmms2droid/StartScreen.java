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

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Button;

public class StartScreen extends Activity {
	
	private EditText m_srvIp = null;
	private EditText m_srvPort = null;
	private Button m_conButton = null;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        m_srvIp = (EditText) findViewById(R.id.srvIp);
        m_srvPort = (EditText) findViewById(R.id.srvPort);
        m_conButton = (Button) findViewById(R.id.conButton);
        
        m_conButton.setOnClickListener(conButtonListener);
    }
    
    private View.OnClickListener conButtonListener = new View.OnClickListener() {
		
		public void onClick(View arg0) {
			m_conButton.setEnabled(false);
			m_conButton.setClickable(false);
			
			int tgtPort = Integer.parseInt(m_srvPort.getText().toString());
			m_conButton.setText("Connecting to: " + m_srvIp.getText().toString());
			try {
				if (!((XMMS2DroidApp) getApplication()).netModule.connect(
						m_srvIp.getText().toString(),
						tgtPort))
				{
					m_conButton.setText("FAILED");
				}
				else
				{	
					Intent newIntent = new Intent(StartScreen.this, ConnectedScreen.class);
					startActivity(newIntent);
					m_conButton.setText("CONNECTED");
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			m_conButton.setEnabled(true);	
		}
	};
}