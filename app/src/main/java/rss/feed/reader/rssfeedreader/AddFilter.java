/* **************************************************
Author: Vlad Zat

Created: 2016/11/22
Modified: 2016/11/25
************************************************** */

package rss.feed.reader.rssfeedreader;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class AddFilter extends AppCompatActivity implements View.OnClickListener {
    int directoryID;
    int requestCode;

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
        requestCode = this.getIntent().getIntExtra("requestCode", -1);

        // Setup Views
        filterName = (EditText) findViewById(R.id.filterName);
        addFilter = (Button) findViewById(R.id.addFilter);
        addFilter.setOnClickListener(this);

        // Edit Filter
        if (requestCode == 2) {
            addFilter.setText("Edit Filter");
            filterName.setText(getIntent().getStringExtra("filterName"));
            filterName.setSelection(getIntent().getStringExtra("filterName").length());
            setTitle("Edit Filter");
        }
    }

    public void onClick(View v) {
        if (v.getId() == R.id.addFilter) {
            String content = filterName.getText().toString().trim();
            if (content.length() > 0) {
                if (requestCode != 2) {
                    if (db.insertFilter(content, directoryID) > 0)
                        setResult(0);
                    else
                        setResult(-1);
                } else {
                    if (db.updateFilter(this.getIntent().getIntExtra("filterID", -1), content) > 0)
                        setResult(0);
                    else
                        setResult(-1);
                }
            } else {
                setResult(-1);
            }
            finish();
        }
    }
}