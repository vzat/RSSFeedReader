package rss.feed.reader.rssfeedreader;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class AddFilter extends AppCompatActivity implements View.OnClickListener {
    int directoryID;

    EditText filterName;
    Button addFilter;

    DatabaseHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_filter);

        setTitle("Add Filter");
        setResult(-1);
        db = DatabaseHelper.getInstance(this);

        // Get the directoryID
        directoryID = this.getIntent().getIntExtra("directoryID", 0);

        // Setup Views
        filterName = (EditText) findViewById(R.id.filterName);
        addFilter = (Button) findViewById(R.id.addFilter);
        addFilter.setOnClickListener(this);
    }

    public void onClick(View v) {
        if (v.getId() == R.id.addFilter) {
            String content = filterName.getText().toString().trim();
            if (content.length() > 0) {
                db.insertFilter(content, directoryID);
            }
            setResult(0);
            finish();
        }
    }
}