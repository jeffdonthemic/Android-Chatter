package com.jeffdouglas.chatter;

import java.util.Date;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class StatusDBAdapter {
	
    private static final String DATABASE_NAME = "data";
    private static final String DATABASE_TABLE = "statusUpdates";
    private static final int DATABASE_VERSION = 1;
	
    public static final String KEY_ROWID = "_id";
    public static final String KEY_ID = "id";
    public static final String KEY_FEEDID = "feedid";
    public static final String KEY_TITLE = "title";
    public static final String KEY_BODY = "body";
    public static final String KEY_IMAGE = "image";
    public static final String KEY_CREATEDDATE = "createdDate";
    
    private static final String TAG = "StatusDbAdapter";
    private DatabaseHelper mDbHelper;
    private SQLiteDatabase mDb;

    /**
     * Database creation sql statement
     */
    private static final String DATABASE_CREATE =
        "create table statusUpdates (_id integer primary key autoincrement, "
        + "id text not null, feedid text not null, title text, body text not null, image text, createdDate date not null);";

    private final Context mCtx;

    private static class DatabaseHelper extends SQLiteOpenHelper {

        DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
        	Log.i("---- jeff ----", "creating status database");
            db.execSQL(DATABASE_CREATE);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            Log.w(TAG, "Upgrading database from version " + oldVersion + " to "
                    + newVersion + ", which will destroy all old data");
            db.execSQL("DROP TABLE IF EXISTS statusUpdates");
            onCreate(db);
        }
    }

    /**
     * Constructor - takes the context to allow the database to be
     * opened/created
     * 
     * @param ctx the Context within which to work
     */
    public StatusDBAdapter(Context ctx) {
        this.mCtx = ctx;
    }

    /**
     * Open the data database. If it cannot be opened, try to create a new
     * instance of the database. If it cannot be created, throw an exception to
     * signal the failure
     * 
     * @return this (self reference, allowing this to be chained in an
     *         initialization call)
     * @throws SQLException if the database could be neither opened or created
     */
    public StatusDBAdapter open() throws SQLException {
        mDbHelper = new DatabaseHelper(mCtx);
        mDb = mDbHelper.getWritableDatabase();
        return this;
    }

    public void close() {
        mDbHelper.close();
    }
    
    public void deleteAll() {
    	mDb.delete(DATABASE_TABLE, null, null);
    }

    /**
     * Create a new status using the title and body provided. If the status is
     * successfully created return the new rowId for that status, otherwise return
     * a -1 to indicate failure.
     * 
     * @param title the title of the status
     * @param body the body of the note
     * @return rowId or -1 if failed
     */
    public long createStatusUpdate(String title, String body, String id, String feedid, Date createdDate) {
    	Log.i("---- jeff ----", "inserting update... " + title + " - " + body);
        ContentValues initialValues = new ContentValues();
        initialValues.put(KEY_TITLE, title);
        initialValues.put(KEY_BODY, body);
        initialValues.put(KEY_ID, id);
        initialValues.put(KEY_FEEDID, feedid);
        initialValues.put(KEY_IMAGE, "@drawable/avatar");
        initialValues.put(KEY_CREATEDDATE, createdDate.toString());
        // insert the new record
        return mDb.insert(DATABASE_TABLE, null, initialValues);
    }

    /**
     * Return a Cursor over the list of all notes in the database
     * 
     * @return Cursor over all notes
     */
    public Cursor fetchAllUpdates() {
        return mDb.query(DATABASE_TABLE, new String[] {KEY_ROWID, KEY_TITLE,
                KEY_BODY, KEY_ID, KEY_FEEDID, KEY_IMAGE, KEY_CREATEDDATE}, null, null, null, null, KEY_CREATEDDATE + " asc");
    }

    /**
     * Return a Cursor positioned at the note that matches the given rowId
     * 
     * @param rowId id of note to retrieve
     * @return Cursor positioned to matching note, if found
     * @throws SQLException if note could not be found/retrieved
     */
    public Cursor fetchStatusUpdate(long rowId) throws SQLException {

        Cursor mCursor = mDb.query(true, DATABASE_TABLE, new String[] {KEY_ROWID,
        		KEY_TITLE, KEY_BODY, KEY_ID, KEY_FEEDID, KEY_IMAGE, KEY_CREATEDDATE}, 
        		KEY_ROWID + "=" + rowId, null, null, null, null, null);
        if (mCursor != null) {
            mCursor.moveToFirst();
        }
        return mCursor;

    }

}
