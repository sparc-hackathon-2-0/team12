package com.shortstack.todozzz;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.sql.SQLException;

/**
 * Created with IntelliJ IDEA.
 * User: root
 * Date: 8/25/12
 * Time: 10:56 AM
 * To change this template use File | Settings | File Templates.
 */
public class ListAdapter {

    public static final String KEY_TITLE = "title";
    public static final String KEY_BODY = "body";
    public static final String KEY_PRIORITY = "priority";
    public static final String KEY_ROWID = "_id";

    private static final String TAG = "ListAdapter";
    private DatabaseHelper mDbHelper;
    private SQLiteDatabase mDb;

    /**
     * Database creation sql statement
     */
    private static final String DATABASE_CREATE =
            "create table list (_id integer primary key autoincrement, "
                    + "title text not null, body text not null, priority text not null);";

    private static final String DATABASE_NAME = "todozzz";
    private static final String DATABASE_TABLE = "list";
    private static final int DATABASE_VERSION = 2;

    private final Context mCtx;

    private static class DatabaseHelper extends SQLiteOpenHelper {

        DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {

            db.execSQL(DATABASE_CREATE);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            Log.w(TAG, "Upgrading database from version " + oldVersion + " to "
                    + newVersion + ", which will destroy all old data");
            db.execSQL("DROP TABLE IF EXISTS list");
            onCreate(db);
        }
    }

    /**
     * Constructor - takes the context to allow the database to be
     * opened/created
     *
     * @param ctx the Context within which to work
     */
    public ListAdapter(Context ctx) {
        this.mCtx = ctx;
    }

    /**
     * Open the list database. If it cannot be opened, try to create a new
     * instance of the database. If it cannot be created, throw an exception to
     * signal the failure
     *
     * @return this (self reference, allowing this to be chained in an
     *         initialization call)
     * @throws SQLException if the database could be neither opened or created
     */
    public ListAdapter open() throws SQLException {
        mDbHelper = new DatabaseHelper(mCtx);
        mDb = mDbHelper.getWritableDatabase();
        return this;
    }

    public void close() {
        mDbHelper.close();
    }


    /**
     * Create a new list item using the title and body provided. If the ListItem is
     * successfully created return the new rowId for that list item, otherwise return
     * a -1 to indicate failure.
     *
     * @param title the title of the list item
     * @param body the body of the list item
     * @return rowId or -1 if failed
     */
    public long createListItem(String title, String body, String priority) {
        ContentValues initialValues = new ContentValues();
        initialValues.put(KEY_TITLE, title);
        initialValues.put(KEY_BODY, body);
        initialValues.put(KEY_PRIORITY, priority);

        return mDb.insert(DATABASE_TABLE, null, initialValues);
    }

    /**
     * Delete the ListItem with the given rowId
     *
     * @param rowId id of ListItem to delete
     * @return true if deleted, false otherwise
     */
    public boolean deleteListItem(long rowId) {

        return mDb.delete(DATABASE_TABLE, KEY_ROWID + "=" + rowId, null) > 0;
    }

    /**
     * Return a Cursor over the list of all list items in the database
     *
     * @return Cursor over all list items
     */
    public Cursor fetchAllListItems() {

        return mDb.query(DATABASE_TABLE, new String[] {KEY_ROWID, KEY_TITLE,
                KEY_BODY, KEY_PRIORITY}, null, null, null, null, KEY_PRIORITY);
    }

    /**
     * Return a Cursor positioned at the ListItem that matches the given rowId
     *
     * @param rowId id of ListItem to retrieve
     * @return Cursor positioned to matching ListItem, if found
     * @throws SQLException if ListItem could not be found/retrieved
     */
    public Cursor fetchListItem(long rowId) throws SQLException {

        Cursor mCursor =

                mDb.query(true, DATABASE_TABLE, new String[] {KEY_ROWID,
                        KEY_TITLE, KEY_BODY, KEY_PRIORITY}, KEY_ROWID + "=" + rowId, null,
                        null, null, null, null);
        if (mCursor != null) {
            mCursor.moveToFirst();
        }
        return mCursor;

    }

    /**
     * Update the ListItem using the details provided. The ListItem to be updated is
     * specified using the rowId, and it is altered to use the title and body
     * values passed in
     *
     * @param rowId id of ListItem to update
     * @param title value to set ListItem title to
     * @param body value to set ListItem body to
     * @return true if the ListItem was successfully updated, false otherwise
     */
    public boolean updateListItem(long rowId, String title, String body, String priority) {
        ContentValues args = new ContentValues();
        args.put(KEY_TITLE, title);
        args.put(KEY_BODY, body);
        args.put(KEY_PRIORITY, priority);

        return mDb.update(DATABASE_TABLE, args, KEY_ROWID + "=" + rowId, null) > 0;
    }
}
