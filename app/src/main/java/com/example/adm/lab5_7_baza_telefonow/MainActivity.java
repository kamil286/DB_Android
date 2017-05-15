package com.example.adm.lab5_7_baza_telefonow;

import android.content.ContentUris;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.ActionMode;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.SimpleCursorAdapter;
import android.support.v4.app.LoaderManager;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;


public class MainActivity extends AppCompatActivity  implements LoaderManager.LoaderCallbacks<Cursor> {

    private ListView myList;
    private SimpleCursorAdapter cursorAdapter;
    private TextView emptyTextView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        emptyTextView = (TextView) findViewById(R.id.none_smartphones);
        emptyTextView.setVisibility(View.INVISIBLE);
        emptyTextView.setText(R.string.no_smartphones);

        myList = (ListView) findViewById(R.id.list);
        myList.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);
        myList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(MainActivity.this, TelephoneEditionActivity.class);
                intent.putExtra(DBHelper.ID, id);
                startActivityForResult(intent, 0);
            }
        });
        myList.setMultiChoiceModeListener(new AbsListView.MultiChoiceModeListener() {
            @Override
            public void onItemCheckedStateChanged(ActionMode mode, int position, long id, boolean checked) {
            }

            @Override
            public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                mode.getMenuInflater().inflate(R.menu.menu_delete,menu);
                return true;
            }

            @Override
            public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                return false;
            }

            @Override
            public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.delete_menu:
                        deleteSelected();
                        mode.finish();
                        noneTelephones();
                        return true;
                }
                return false;
            }

            @Override
            public void onDestroyActionMode(ActionMode mode) {

            }
        });
        fillFields();
        noneTelephones();

    }
    private void noneTelephones()
    {
        try
        {
            String projection[] = { DBHelper.MANUFACTURER};
            Cursor cursor = getContentResolver().query( DBProvider.CONTENT_URI, projection, null, null, null);
            cursor.moveToFirst();
            int columnIndex = cursor.getColumnIndexOrThrow(DBHelper.MANUFACTURER);
            String value = cursor.getString(columnIndex);
            cursor.close();
            emptyTextView.setVisibility(View.INVISIBLE);
        }catch (Exception e)
        {
            emptyTextView.setVisibility(View.VISIBLE);
        }
    }
    private void deleteSelected() {
        long selection[] = myList.getCheckedItemIds();
        for (int i = 0; i < selection.length; ++i) {
            getContentResolver().delete(
                    ContentUris.withAppendedId(DBProvider.CONTENT_URI,
                            selection[i]), null, null);
        }
    }

    private void addValue(String p, String m) {
        ContentValues values = new ContentValues();

        values.put(
                DBHelper.MANUFACTURER,p);
        values.put(DBHelper.MODEL, m);
        values.put(DBHelper.ANDROID_VERSION, 1);
        values.put(DBHelper.WWW, "WWW");
        Uri newUri = getContentResolver().insert(
                DBProvider.CONTENT_URI, values);
    }


    private void fillFields() {
        getSupportLoaderManager().initLoader(0, null, this);
        String[] mapFrom = new String[]{DBHelper.MANUFACTURER, DBHelper.MODEL};
        int[] mapTo = new int[]{R.id.manufacturer_text, R.id.model_text};

        cursorAdapter = new SimpleCursorAdapter(this, R.layout.row_list, null, mapFrom, mapTo, 0);
        myList.setAdapter(cursorAdapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {

        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_add) {

            Intent intent = new Intent(MainActivity.this, TelephoneEditionActivity.class);
            intent.putExtra(DBHelper.ID, (long) -1);
            startActivityForResult(intent, 0);
            return true;
        }


        return super.onOptionsItemSelected(item);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String[] projection = {DBHelper.ID,
                DBHelper.MANUFACTURER,
                DBHelper.MODEL};
        CursorLoader cursorLoader = new CursorLoader(this,
                DBProvider.CONTENT_URI,
                projection, null, null, null);
        return cursorLoader;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        cursorAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

        cursorAdapter.swapCursor(null);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        getSupportLoaderManager().restartLoader(0, null, this);
        noneTelephones();
    }
}
