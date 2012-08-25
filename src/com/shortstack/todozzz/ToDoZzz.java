package com.shortstack.todozzz;

import android.app.ListActivity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.*;
import com.shortstack.R;

import java.sql.SQLException;

/**
 * Created with IntelliJ IDEA.
 * User: root
 * Date: 8/25/12
 * Time: 10:45 AM
 * To change this template use File | Settings | File Templates.
 */
public class ToDoZzz extends ListActivity {
    private static final int ACTIVITY_CREATE=0;
    private static final int ACTIVITY_EDIT=1;

    private static final int INSERT_ID = Menu.FIRST;
    private static final int DELETE_ID = Menu.FIRST + 1;

    private ListAdapter mDbHelper;

    /*VARIABLES FOR TIMER*/
    private TextView tempTextView;
    private Handler mHandler = new Handler();
    private long startTime;
    private long stopTime;
    private long elapsedTime;
    private final int REFRESH_RATE = 100;
    private String hours,minutes,seconds,milliseconds;
    private long secs,mins,hrs;
    private boolean stopped = false;
    /*END VARIABLES FOR TIMER*/

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.list);
        mDbHelper = new ListAdapter(this);
        try {
            mDbHelper.open();
        } catch (SQLException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        fillData();
        registerForContextMenu(getListView());

        checkScreenDensity();

        Button startButton = (Button)findViewById(R.id.startButton);
        startButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                startClick(v);
            }
        });

        Button stopButton = (Button)findViewById(R.id.stopButton);
        stopButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                stopClick(v);
            }
        });

        Button resetButton = (Button)findViewById(R.id.resetButton);
        resetButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                resetClick(v);
            }
        });

    }

    private void fillData() {
        Cursor notesCursor = mDbHelper.fetchAllListItems();
        startManagingCursor(notesCursor);

        // Create an array to specify the fields we want to display in the list (only TITLE)
        String[] from = new String[]{ListAdapter.KEY_TITLE};

        // and an array of the fields we want to bind those fields to (in this case just text1)
        int[] to = new int[]{R.id.text1};

        // Now create a simple cursor adapter and set it to display
        SimpleCursorAdapter notes =
                new SimpleCursorAdapter(this, R.layout.list_row, notesCursor, from, to);
        setListAdapter(notes);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        menu.add(0, INSERT_ID, 0, R.string.menu_insert);
        return true;
    }

    @Override
    public boolean onMenuItemSelected(int featureId, MenuItem item) {
        switch(item.getItemId()) {
            case INSERT_ID:
                createNote();
                return true;
        }

        return super.onMenuItemSelected(featureId, item);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,
                                    ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        menu.add(0, DELETE_ID, 0, R.string.menu_delete);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case DELETE_ID:
                AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
                mDbHelper.deleteListItem(info.id);
                fillData();
                return true;
        }
        return super.onContextItemSelected(item);
    }

    private void createNote() {
        Intent i = new Intent(this, EditList.class);
        startActivityForResult(i, ACTIVITY_CREATE);
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        Intent i = new Intent(this, EditList.class);
        i.putExtra(ListAdapter.KEY_ROWID, id);
        startActivityForResult(i, ACTIVITY_EDIT);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        fillData();
    }


    private void checkScreenDensity(){
        tempTextView = (TextView)findViewById(R.id.backgroundText);
        switch (getResources().getDisplayMetrics().densityDpi) {
            case DisplayMetrics.DENSITY_LOW:
                tempTextView.setVisibility(View.GONE);
                break;
            case DisplayMetrics.DENSITY_MEDIUM:
                tempTextView.setVisibility(View.GONE);
                break;
            case DisplayMetrics.DENSITY_HIGH:
                tempTextView.setVisibility(View.VISIBLE);
                break;
        }
    }

    public void startClick (View view){
        showStopButton();
        if(stopped){
            startTime = System.currentTimeMillis() - elapsedTime;
        }
        else{
            startTime = System.currentTimeMillis();
        }
        mHandler.removeCallbacks(startTimer);
        mHandler.postDelayed(startTimer, 0);
    }

    public void stopClick (View view){
        hideStopButton();
        mHandler.removeCallbacks(startTimer);
        stopped = true;
    }

    public void resetClick (View view){
        stopped = false;
        ((TextView)findViewById(R.id.timer)).setText("00:00:00");
    }

    private Runnable startTimer = new Runnable() {
        public void run() {
            elapsedTime = System.currentTimeMillis() - startTime;
            updateTimer(elapsedTime);
            mHandler.postDelayed(this,REFRESH_RATE);
        }
    };

    private void updateTimer (float time){
        secs = (long)(time/1000);
        mins = (long)((time/1000)/60);
        hrs = (long)(((time/1000)/60)/60);

        /* Convert the seconds to String
           * and format to ensure it has
           * a leading zero when required
           */
        secs = secs % 60;
        seconds=String.valueOf(secs);
        if(secs == 0){
            seconds = "00";
        }
        if(secs <10 && secs > 0){
            seconds = "0"+seconds;
        }

        /* Convert the minutes to String and format the String */

        mins = mins % 60;
        minutes=String.valueOf(mins);
        if(mins == 0){
            minutes = "00";
        }
        if(mins <10 && mins > 0){
            minutes = "0"+minutes;
        }

        /* Convert the hours to String and format the String */

        hours=String.valueOf(hrs);
        if(hrs == 0){
            hours = "00";
        }
        if(hrs <10 && hrs > 0){
            hours = "0"+hours;
        }

        /* Although we are not using milliseconds on the timer in this example
           * I included the code in the event that you wanted to include it on your own
           */
        milliseconds = String.valueOf((long)time);
        if(milliseconds.length()==2){
            milliseconds = "0"+milliseconds;
        }
        if(milliseconds.length()<=1){
            milliseconds = "00";
        }
        milliseconds = milliseconds.substring(milliseconds.length()-3, milliseconds.length()-2);

        /* Setting the timer text to the elapsed time */
        ((TextView)findViewById(R.id.timer)).setText(hours + ":" + minutes + ":" + seconds);
    }

    private void showStopButton(){
        ((Button)findViewById(R.id.startButton)).setVisibility(View.GONE);
        ((Button)findViewById(R.id.resetButton)).setVisibility(View.GONE);
        ((Button)findViewById(R.id.stopButton)).setVisibility(View.VISIBLE);
    }

    private void hideStopButton(){
        ((Button)findViewById(R.id.startButton)).setVisibility(View.VISIBLE);
        ((Button)findViewById(R.id.resetButton)).setVisibility(View.VISIBLE);
        ((Button)findViewById(R.id.stopButton)).setVisibility(View.GONE);
    }


}
