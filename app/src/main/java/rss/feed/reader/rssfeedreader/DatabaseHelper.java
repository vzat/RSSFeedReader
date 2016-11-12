package rss.feed.reader.rssfeedreader;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static DatabaseHelper instance = null;

    // Database Name, Version and common Column Names
    private static final String DATABASE_NAME = "RSSDatabase";
    private static final int DATABASE_VERSION = 1;
    private static final String KEY_ID = "_id";

    // Articles Table
    private static final String TABLE_ARTICLE = "Articles";
    private static final String KEY_ARTICLE_TITLE = "title";
    private static final String KEY_ARTICLE_DESCRIPTION = "description";
    private static final String KEY_ARTICLE_LINK = "link";
    private static final String KEY_ARTICLE_DATE = "date";

    // Directories Table
    private static final String TABLE_DIRECTORY = "Directories";
    private static final String KEY_DIRECTORY_ID = "directoryID";
    private static final String KEY_DIRECTORY_NAME = "directoryName";
    private static final String KEY_DIRECTORY_TYPE = "directoryType";

    // Feeds Table
    private static final String TABLE_FEED = "Feeds";
    private static final String KEY_FEED_ID = "feedID";
    private static final String KEY_FEED_NAME = "feedName";
    private static final String KEY_FEED_URL = "feedURL";

    // FeedDirectories Table
    private static final String TABLE_FEED_DIRECTORY = "FeedDirectories";

    // Filters Table
    private static final String TABLE_FILTER = "Filters";
    private static final String KEY_FILTER_NAME = "filterName";


    // Create Tables
    private static final String CREATE_TABLE_ARTICLE =          "create table " + TABLE_ARTICLE + "(" +
                                                                KEY_ID + " integer primary key autoincrement, " +
                                                                KEY_ARTICLE_TITLE + " text, " +
                                                                KEY_ARTICLE_DESCRIPTION + " text, " +
                                                                KEY_ARTICLE_LINK + " text, " +
                                                                KEY_ARTICLE_DATE + " text" +
                                                                KEY_DIRECTORY_ID + " integer," +
                                                                "FOREIGN KEY(" + KEY_DIRECTORY_ID + ") REFERENCES " + TABLE_DIRECTORY + "(" + KEY_ID + ");";

    private static final String CREATE_TABLE_DIRECTORY =        "create table " + TABLE_DIRECTORY + "(" +
                                                                KEY_ID + " integer primary key autoincrement, " +
                                                                KEY_DIRECTORY_NAME + " text, " +
                                                                KEY_ARTICLE_DESCRIPTION + " text);";

    private static final String CREATE_TABLE_FEED =             "create table " + TABLE_FEED + "(" +
                                                                KEY_ID + " integer primary key autoincrement, " +
                                                                KEY_FEED_NAME + " text, " +
                                                                KEY_FEED_URL + " text);";

    private static final String CREATE_TABLE_FEED_DIRECTORY =   "create table " + TABLE_FEED_DIRECTORY + "(" +
                                                                KEY_ID + " integer primary key autoincrement, " +
                                                                KEY_FEED_ID + " integer, " +
                                                                KEY_DIRECTORY_ID + " integer, " +
                                                                "FOREIGN KEY(" + KEY_FEED_ID + ") REFERENCES " + TABLE_FEED + "(" + KEY_ID + ")," +
                                                                "FOREIGN KEY(" + KEY_DIRECTORY_ID + ") REFERENCES " + TABLE_DIRECTORY + "(" + KEY_ID + ");";


    private static final String CREATE_TABLE_FILTER =           "create table " + TABLE_FILTER + "(" +
                                                                KEY_ID + " integer primary key autoincrement, " +
                                                                KEY_FILTER_NAME + " text, " +
                                                                KEY_DIRECTORY_ID + " integer, " +
                                                                "FOREIGN KEY(" + KEY_DIRECTORY_ID + ") REFERENCES " + TABLE_DIRECTORY + "(" + KEY_ID + ");";

    private Context context;

    private DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }

    public static synchronized DatabaseHelper getInstance(Context context) {
        if (instance == null) {
            instance = new DatabaseHelper(context.getApplicationContext());
        }
        return instance;
    }

    public void onCreate(SQLiteDatabase db) {
        try {
            db.execSQL(CREATE_TABLE_DIRECTORY);
            db.execSQL(CREATE_TABLE_FEED);
            db.execSQL(CREATE_TABLE_FEED_DIRECTORY);
            db.execSQL(CREATE_TABLE_ARTICLE);
            db.execSQL(CREATE_TABLE_FILTER);
        } catch(SQLiteException e) {
            Log.e("Cannot Create Tables ", e.toString());
        }
    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        try {
            db.execSQL("drop table if exists " + TABLE_FILTER);
            db.execSQL("drop table if exists " + TABLE_ARTICLE);
            db.execSQL("drop table if exists " + TABLE_FEED_DIRECTORY);
            db.execSQL("drop table if exists " + TABLE_FEED);
            db.execSQL("drop table if exists " + TABLE_DIRECTORY);

            onCreate(db);
        } catch(SQLiteException e) {
            Log.e("Cannot Drop Tables ", e.toString());
        }
    }

    public long insertArticles(ArrayList<Article> articles) {
        SQLiteDatabase db = instance.getWritableDatabase();
        long totalRows = 0;

        for (int i = 0 ; i < articles.size() ; i++) {
            ContentValues values = new ContentValues();
            values.put(KEY_ARTICLE_TITLE, articles.get(i).getTitle());
            values.put(KEY_ARTICLE_DESCRIPTION, articles.get(i).getDescription());
            values.put(KEY_ARTICLE_LINK, articles.get(i).getLink());
            values.put(KEY_ARTICLE_DATE, articles.get(i).getDate());

            // Sum the total number of rows inserted
            long columnsInserted = db.insert(TABLE_ARTICLE, null, values);
            if (columnsInserted != -1)
                totalRows += columnsInserted;
        }
        db.close();

        return totalRows;
    }

    public Cursor getAllArticles() {
        SQLiteDatabase db = instance.getReadableDatabase();
        try {
            return db.query(TABLE_ARTICLE,
                            new String[] {
                                    KEY_ID,
                                    KEY_ARTICLE_TITLE,
                                    KEY_ARTICLE_DESCRIPTION,
                                    KEY_ARTICLE_LINK,
                                    KEY_ARTICLE_DATE
                            },
                            null,
                            null,
                            null,
                            null,
                            null);
        } finally {
            db.close();
        }
    }
}
