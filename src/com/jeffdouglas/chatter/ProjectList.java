package com.jeffdouglas.chatter;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import android.app.ListActivity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.SimpleCursorAdapter;
import android.util.Log;

public class ProjectList extends ListActivity {
	
	public static final int INSERT_ID = Menu.FIRST;
	public static final int REFRESH_ID = 2;
	
	private static final int PROJECT_UPDATE = 0;
	
	private ProjectDBAdapter mDbHelper;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.project_list);
        mDbHelper = new ProjectDBAdapter(this);
        mDbHelper.open();
        fillData();
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
    	boolean result = super.onCreateOptionsMenu(menu);
        menu.add(0, INSERT_ID, 0, R.string.menu_update_status).setIcon(R.drawable.menu_icon_update);
        menu.add(0, REFRESH_ID, 0, R.string.menu_refresh).setIcon(R.drawable.menu_icon_refresh);
        return result;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
    	switch (item.getItemId()) {
        case INSERT_ID:
            updateProjectStatus();
            return true;
        case REFRESH_ID:
        	refresh();
        	return true;
        }
        return super.onOptionsItemSelected(item);
    }
    
    private void updateProjectStatus() {
        Intent i = new Intent(this, ProjectUpdate.class);
        startActivityForResult(i, PROJECT_UPDATE);
    }
    
    private void refresh() {
    	Log.i("-- jeff --", "starting refresh....");
    	// delete all current records
    	mDbHelper.deleteAll();
    	ArrayList<HashMap> records = RestClient.fetchRecords("http://chatter-android.appspot.com/projectFeed");
    	for (HashMap<String, String> hm : records) {
    		mDbHelper.createProjectUpdate(hm.get("title"), hm.get("body"), hm.get("id"), hm.get("feedid"), new Date());
    	}
        fillData();
        Log.i("-- jeff --", "done refreshing data");
    }
    
    private void fillData() {
    	Log.i("-- jeff --", "filling data....");
        // Get all of the notes from the database and create the item list
        Cursor c = mDbHelper.fetchAllUpdates();
        startManagingCursor(c);

        String[] from = new String[] { ProjectDBAdapter.KEY_TITLE, ProjectDBAdapter.KEY_BODY, ProjectDBAdapter.KEY_CREATEDDATE };
        int[] to = new int[] { R.id.title1, R.id.status1, R.id.date1 };
        
        // Now create an array adapter and set it to display using our row
        SimpleCursorAdapter projectUpdates =
            new SimpleCursorAdapter(this, R.layout.project_row, c, from, to);
        setListAdapter(projectUpdates);
        Log.i("-- jeff --", "done filling data...");
    }
    
}