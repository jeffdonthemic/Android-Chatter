package com.jeffdouglas.chatter;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.SimpleCursorAdapter;

public class StatusList extends ListActivity {
	
	public static final int INSERT_ID = Menu.FIRST;
	public static final int REFRESH_ID = 2;
	public static final int PROJECTS_ID = 3;
	
	private static final int STATUS_UPDATE = 0;
	private static final int PROJECT_LIST = 1;
	private static final int PROJECT_UPDATE = 2;
	
	private StatusDBAdapter mDbHelper;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        mDbHelper = new StatusDBAdapter(this);
        mDbHelper.open();
        fillData();
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
    	boolean result = super.onCreateOptionsMenu(menu);
        menu.add(0, INSERT_ID, 0, R.string.menu_update_status).setIcon(R.drawable.menu_icon_update);
        menu.add(0, REFRESH_ID, 0, R.string.menu_refresh).setIcon(R.drawable.menu_icon_refresh);
        //menu.add(0, PROJECTS_ID, 0, R.string.menu_projects).setIcon(R.drawable.menu_icon_projects);
        return result;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
    	switch (item.getItemId()) {
        case INSERT_ID:
            updateStatus();
            return true;
        case REFRESH_ID:
        	refresh();
        	return true;
        case PROJECTS_ID:
        	showProjects();
        	return true;
        }
        return super.onOptionsItemSelected(item);
    }
    
    private void updateStatus() {
        Intent i = new Intent(this, StatusUpdate.class);
        startActivityForResult(i, STATUS_UPDATE);
    }
    
    private void refresh() {
    	
    	// delete all current records
    	mDbHelper.deleteAll();
    	ArrayList<HashMap> records = RestClient.fetchRecords("http://chatter-android.appspot.com/newsFeed");
    	for (HashMap<String, String> hm : records) {
    		mDbHelper.createStatusUpdate(hm.get("title"), hm.get("body"), hm.get("id"), hm.get("feedid"), new Date());
    	}
        fillData();
        
    }
    
    private void showProjects() {

    	final CharSequence[] items = {"ACME Project", "EMEA Implementation", "Salesfore.com Rollout"};

    	AlertDialog.Builder builder = new AlertDialog.Builder(this);
    	builder.setTitle("Choose a project");
    	builder.setItems(items, new DialogInterface.OnClickListener() {
    	    public void onClick(DialogInterface dialog, int item) {
    	    	Intent intent = new Intent(StatusList.this,ProjectList.class);
                startActivity(intent);
    	    }
    	});
    	AlertDialog alert = builder.create();
    	alert.show();
    	
    }
    
    private void fillData() {
        // Get all of the notes from the database and create the item list
        Cursor c = mDbHelper.fetchAllUpdates();
        startManagingCursor(c);

        String[] from = new String[] { StatusDBAdapter.KEY_TITLE, StatusDBAdapter.KEY_BODY, StatusDBAdapter.KEY_IMAGE, StatusDBAdapter.KEY_CREATEDDATE };
        int[] to = new int[] { R.id.title1, R.id.status1, R.id.avatar, R.id.date1 };
        
        // Now create an array adapter and set it to display using our row
        SimpleCursorAdapter statusUpdates =
            new SimpleCursorAdapter(this, R.layout.status_row, c, from, to);
        setListAdapter(statusUpdates);
    }
    
}