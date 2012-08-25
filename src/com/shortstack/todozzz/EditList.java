package com.shortstack.todozzz;

import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import com.shortstack.R;
import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.sql.SQLException;

/**
 * Created with IntelliJ IDEA.
 * User: root
 * Date: 8/25/12
 * Time: 11:30 AM
 * To change this template use File | Settings | File Templates.
 */
public class EditList extends Activity {
    private EditText mTitleText;
    private EditText mBodyText;
    private Spinner mPriorityText;
    private Long mRowId;
    private ListAdapter mDbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mDbHelper = new ListAdapter(this);
        try {
            mDbHelper.open();
        } catch (SQLException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }

        setContentView(R.layout.list_edit);
        setTitle(R.string.edit_item);

        mTitleText = (EditText) findViewById(R.id.title);
        mBodyText = (EditText) findViewById(R.id.body);
        mPriorityText = (Spinner) findViewById(R.id.priority);

        Button confirmButton = (Button) findViewById(R.id.confirm);

        mRowId = (savedInstanceState == null) ? null :
                (Long) savedInstanceState.getSerializable(ListAdapter.KEY_ROWID);
        if (mRowId == null) {
            Bundle extras = getIntent().getExtras();
            mRowId = extras != null ? extras.getLong(ListAdapter.KEY_ROWID)
                    : null;
        }

        try {
            populateFields();
        } catch (SQLException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }

        confirmButton.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
                setResult(RESULT_OK);
                finish();
            }

        });
    }

    private void populateFields() throws SQLException {
        if (mRowId != null) {
            Cursor note = mDbHelper.fetchListItem(mRowId);
            startManagingCursor(note);
            mTitleText.setText(note.getString(
                    note.getColumnIndexOrThrow(ListAdapter.KEY_TITLE)));
            mBodyText.setText(note.getString(
                    note.getColumnIndexOrThrow(ListAdapter.KEY_BODY)));
            String priority = note.getString(
                    note.getColumnIndexOrThrow(ListAdapter.KEY_PRIORITY));
            Log.v("priority",priority);
            if (priority.equals("High")) {
                mPriorityText.setSelection(0);
            } else if (priority.equals("Medium")) {
                mPriorityText.setSelection(1);
            } else {
                mPriorityText.setSelection(2);
            }
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        saveState();
        outState.putSerializable(ListAdapter.KEY_ROWID, mRowId);
    }

    @Override
    protected void onPause() {
        super.onPause();
        saveState();
    }

    @Override
    protected void onResume() {
        super.onResume();
        try {
            populateFields();
        } catch (SQLException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }

    private void saveState() {
        String title = mTitleText.getText().toString();
        String body = mBodyText.getText().toString();
        String priority = mPriorityText.getSelectedItem().toString();

        if (mRowId == null) {
            long id = mDbHelper.createListItem(title, body, priority);
            if (id > 0) {
                mRowId = id;
            }
        } else {
            mDbHelper.updateListItem(mRowId, title, body, priority);
        }
    }
}
