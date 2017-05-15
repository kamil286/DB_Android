package com.example.adm.lab5_7_baza_telefonow;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class TelephoneEditionActivity extends AppCompatActivity {

    private Button cancelButton, saveButton, wwwButton;
    private EditText editTextManufacturer, editTextModel, editTextVerAndroid, editTextWWW;
    private long rowId;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.telephon_edition_activity);

        editTextManufacturer = (EditText) findViewById(R.id.manufacturer_edit);
        editTextModel = (EditText) findViewById(R.id.model_edit);
        editTextVerAndroid = (EditText) findViewById(R.id.android_edit);
        editTextWWW = (EditText) findViewById(R.id.www_edit);

        cancelButton = (Button) findViewById(R.id.cancel_button);
        saveButton = (Button) findViewById(R.id.save_button);
        wwwButton = (Button) findViewById(R.id.www_button);

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setResult(RESULT_CANCELED);
                finish();
            }
        });

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveToBasicData();
            }
        });

        wwwButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pushButtonWWW();
            }
        });

        rowId = -1;
        if (savedInstanceState != null)
            rowId = savedInstanceState.getLong(DBHelper.ID);
        else {
            Bundle bundle = getIntent().getExtras();
            if (bundle != null)
                rowId = bundle.getLong(DBHelper.ID);
        }
        if (rowId != -1)
            fillFields();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putLong(DBHelper.ID, rowId);
    }

    private void saveToBasicData() {
        if (checStrings()) {
            ContentValues values = new ContentValues();
            values.put(DBHelper.MANUFACTURER, editTextManufacturer.getText().toString());
            values.put(DBHelper.MODEL, editTextModel.getText().toString());
            values.put(DBHelper.ANDROID_VERSION, editTextVerAndroid.getText().toString());
            values.put(DBHelper.WWW, editTextWWW.getText().toString());
            if (rowId == -1) {
                Uri newUri = getContentResolver().insert(DBProvider.CONTENT_URI, values);
                rowId = Integer.parseInt(newUri.getLastPathSegment());
            }
            else {
                int countOfChangedRows = getContentResolver().update(
                        ContentUris.withAppendedId( DBProvider.CONTENT_URI, rowId),
                                                    values, null, null);
            }
            setResult(RESULT_OK);
            finish();
        }
        else Toast.makeText(this,
                getString(R.string.error), Toast.LENGTH_SHORT).show();
    }

    private boolean checStrings() {
        return !(editTextManufacturer.getText().toString().equals("")
                || editTextModel.getText().toString().equals("")
                || editTextVerAndroid.getText().toString().equals("")
                || editTextWWW.getText().toString().equals(""));
    }

    private void fillFields() {
        String projection[] = { DBHelper.MANUFACTURER,
                DBHelper.MODEL,
                DBHelper.ANDROID_VERSION,
                DBHelper.WWW };
        Cursor cursor = getContentResolver().query(
                ContentUris.withAppendedId(
                        DBProvider.CONTENT_URI,
                        rowId), projection, null, null, null);
        cursor.moveToFirst();
        int columnIndex = cursor .getColumnIndexOrThrow(DBHelper.MANUFACTURER);
        String values = cursor.getString(columnIndex);
        editTextManufacturer.setText(values);
        editTextModel.setText(cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.MODEL)));
        editTextVerAndroid.setText(cursor.getString(cursor .getColumnIndexOrThrow(DBHelper.ANDROID_VERSION)));
        editTextWWW.setText(cursor.getString(cursor .getColumnIndexOrThrow(DBHelper.WWW)));
        cursor.close();
    }

    private void pushButtonWWW() {
        if (!editTextWWW.getText().toString().equals("")) {
            String address = editTextWWW.getText().toString();
            if (!address.startsWith("http://") && !address.startsWith("https://"))
                address = "http://" + address;
            Intent browserIntent = new Intent("android.intent.action.VIEW", Uri.parse(address));
            startActivity(browserIntent);
        } else {
            Toast.makeText(this, getString(R.string.error),
                    Toast.LENGTH_SHORT).show();
        }
    }
}
