package com.jeffdouglas.chatter;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class ProjectUpdate extends Activity {
	
	private ProjectDBAdapter mDbHelper;
	private EditText mBodyText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.project_update);
        
        mDbHelper = new ProjectDBAdapter(this);
        mDbHelper.open();

        Button shareButton = (Button) findViewById(R.id.button_share);
        mBodyText = (EditText) findViewById(R.id.body);

        shareButton.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
            
            	// add the status to the database
            	//mDbHelper.createProjectUpdate("Salesforce.com Rollow -- Jeff Douglas", mBodyText.getText().toString(), "0000", "0000", new Date());
            	
            	try { 
            		// Construct data 
            		String data = URLEncoder.encode("status", "UTF-8") + "=" + URLEncoder.encode(mBodyText.getText().toString(), "UTF-8"); 
            		// Send data 
            		URL url = new URL("https://chatter-android.appspot.com/projectUpdate"); 
            		URLConnection conn = url.openConnection(); 
            		conn.setDoOutput(true); 
            		OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream()); 
            		wr.write(data); 
            		wr.flush(); 
            		
            		BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream())); 
            		String line; 
            		while ((line = rd.readLine()) != null) { 
            			Log.i("---- JEFF ---", "Results: "+line);
            		} 
            		wr.close(); 
            		rd.close(); 
            		
            	} catch (Exception e) { 
            		Log.i("---- JEFF ---", e.getMessage());
            	}
            	
            	Log.i("---- JEFF ---", "Deleting all records...");
            	mDbHelper.deleteAll();
            	ArrayList<HashMap> records = RestClient.fetchRecords("http://chatter-android.appspot.com/projectFeed");
            	for (HashMap<String, String> hm : records) {
            		Log.i("---- JEFF ---", "Inserting record..."+hm.get("body"));
            		mDbHelper.createProjectUpdate(hm.get("title"), hm.get("body"), hm.get("id"), hm.get("feedid"), new Date());
            	}

                Intent mIntent = new Intent();
                setResult(RESULT_OK, mIntent);
                finish();
                
            }

        });
    }
	
}
