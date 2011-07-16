package com.jeffdouglas.chatter;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

public class RestClient {
	
	/*
	 * To convert the InputStream to String we use the BufferedReader.readLine()
	 * method. We iterate until the BufferedReader return null which means
	 * there's no more data to read. Each line will appended to a StringBuilder
	 * and returned as String.
	 */
	private static String convertStreamToString(InputStream is) {
		BufferedReader reader = new BufferedReader(new InputStreamReader(is));
		StringBuilder sb = new StringBuilder();

		String line = null;
		try {
			while ((line = reader.readLine()) != null) {
				sb.append(line + "\n");
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				is.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		return sb.toString();
	}

	public static ArrayList<HashMap> fetchRecords(String url) {
		
		// records returned from 
		ArrayList<HashMap> records = new ArrayList<HashMap>();
		HttpClient httpclient = new DefaultHttpClient();
		// Prepare a request object
		HttpGet httpget = new HttpGet(url); 
		// Execute the request
		HttpResponse response;
		
		try {
			response = httpclient.execute(httpget);

			// Get the response entity
			HttpEntity entity = response.getEntity();
			
			// only process a valid entity
			if (entity != null) {

				// A Simple JSON Response Read
				InputStream instream = entity.getContent();
				String result = convertStreamToString(instream);

				// A Simple JSONObject Creation
				JSONObject json=new JSONObject(result);

				// A Simple JSONObject Parsing
				JSONArray nameArray=json.names();
				JSONArray valArray=json.toJSONArray(nameArray);
				for(int i=0;i<valArray.length();i++)
				{
					Log.i("key",nameArray.getString(i));
					JSONArray resultsArray= new JSONArray(valArray.getString(i));
					
					for(int k=0;k<resultsArray.length();k++) {
						Log.i("record",resultsArray.getString(k));
						JSONObject record = new JSONObject(resultsArray.getString(k));
						JSONArray recordNameArray = record.names();
						JSONArray recordValArray = record.toJSONArray(recordNameArray);
						
						// create a new map to hold the fields and data
						HashMap<String,String> row = new HashMap<String, String>();
												
						for(int j=0;j<recordValArray.length();j++) {
							Log.i("data","key: "+recordNameArray.getString(j) + " - value: "+recordValArray.getString(j));
							row.put(recordNameArray.getString(j), recordValArray.getString(j));
						}
						Log.i("row",row.toString());
						records.add(row);
					}
						
				}
				
				Log.i("records",records.toString());

				// Closing the input stream will trigger connection release
				instream.close();
			}


		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return records;
	}

}
