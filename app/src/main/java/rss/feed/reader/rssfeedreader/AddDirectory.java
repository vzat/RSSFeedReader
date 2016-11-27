/* **************************************************
Author: Vlad Zat

Created: 2016/11/12
Modified: 2016/11/20
************************************************** */

package rss.feed.reader.rssfeedreader;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;

public class AddDirectory extends AppCompatActivity implements View.OnClickListener {
    EditText directoryName;
    int radioButton;
    int requestCode;
    Button addDirectory;
    Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_directory);

        setResult(-1);

        setTitle("Add Directory");

        intent = getIntent();
        requestCode = intent.getIntExtra("requestCode", 1);
        directoryName = (EditText) findViewById(R.id.directoryName);
        radioButton = 1;
        addDirectory = (Button) findViewById(R.id.add);
        addDirectory.setOnClickListener(this);

        if (requestCode == 2) {
            setTitle("Edit Directory");
            addDirectory.setText("Edit Directory");

            directoryName.setText(intent.getStringExtra("directoryName"));
            directoryName.setSelection(intent.getStringExtra("directoryName").length());
            RadioButton savedRadio = (RadioButton) findViewById(R.id.saved);
            RadioButton feedRadio = (RadioButton) findViewById(R.id.feed);
            savedRadio.setVisibility(View.GONE);
            feedRadio.setVisibility(View.GONE);

            if ("Saved".equals(intent.getStringExtra("directoryType"))) {
                radioButton = 0;
            } else {
                radioButton = 1;
            }
        }
    }

    public void radioClick(View v) {
        if (((RadioButton)v).isChecked() && requestCode == 1) {
            if (v.getId() == R.id.saved)
                radioButton = 0;
            else if (v.getId() == R.id.feed)
                radioButton = 1;
        }
    }

    public void onClick(View v) {
        if (v.getId() == R.id.add) {
            String content = directoryName.getText().toString().trim();
            if (content.length() > 0) {
                DatabaseHelper db = DatabaseHelper.getInstance(this);

                // Get the directoryType from the Radio Button pressed
                String directoryType;
                if (radioButton == 0)
                    directoryType = "Saved";
                else
                    directoryType = "Feed";

                // Either insert or update directory
                if (requestCode == 1) {
                    db.insertDirectory(content, directoryType);
                } else if (requestCode == 2){
                    db.updateDirectory(intent.getIntExtra("directoryID", -1), content, directoryType);
                }

                setResult(radioButton);
                finish();
            }
        }
    }
}
