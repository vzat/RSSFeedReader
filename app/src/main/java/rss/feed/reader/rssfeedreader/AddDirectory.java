package rss.feed.reader.rssfeedreader;

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
    Button addDirectory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_directory);

        setResult(-1);

        directoryName = (EditText) findViewById(R.id.directoryName);
        radioButton = 1;
        addDirectory = (Button) findViewById(R.id.add);
        addDirectory.setOnClickListener(this);
    }

    public void radioClick(View v) {
        if (((RadioButton)v).isChecked()) {
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
                if (radioButton == 0)
                    db.insertDirectory(content, "Saved");
                else if (radioButton == 1)
                    db.insertDirectory(content, "Feed");
                setResult(radioButton);
                finish();
            }
        }
    }
}
