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
    private static final int DATABASE_VERSION = 14;
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

    // DirectoryTypes Table
    private static final String TABLE_DIRECTORY_TYPES = "DirectoryTypes";

    // Create Tables
    private static final String CREATE_TABLE_ARTICLE =          "create table " + TABLE_ARTICLE + "(" +
                                                                KEY_ID + " integer primary key autoincrement, " +
                                                                KEY_ARTICLE_TITLE + " text, " +
                                                                KEY_ARTICLE_DESCRIPTION + " text, " +
                                                                KEY_ARTICLE_LINK + " text, " +
                                                                KEY_ARTICLE_DATE + " text, " +
                                                                KEY_DIRECTORY_ID + " integer," +
                                                                "FOREIGN KEY(" + KEY_DIRECTORY_ID + ") REFERENCES " + TABLE_DIRECTORY + "(" + KEY_ID + "));";

    private static final String CREATE_TABLE_DIRECTORY =        "create table " + TABLE_DIRECTORY + "(" +
                                                                KEY_ID + " integer primary key autoincrement, " +
                                                                KEY_DIRECTORY_NAME + " text, " +
                                                                KEY_DIRECTORY_TYPE + " text);";

    private static final String CREATE_TABLE_FEED =             "create table " + TABLE_FEED + "(" +
                                                                KEY_ID + " integer primary key autoincrement, " +
                                                                KEY_FEED_NAME + " text, " +
                                                                KEY_FEED_URL + " text);";

    private static final String CREATE_TABLE_FEED_DIRECTORY =   "create table " + TABLE_FEED_DIRECTORY + "(" +
                                                                KEY_FEED_ID + " integer, " +
                                                                KEY_DIRECTORY_ID + " integer, " +
                                                                "FOREIGN KEY(" + KEY_FEED_ID + ") REFERENCES " + TABLE_FEED + "(" + KEY_ID + "), " +
                                                                "FOREIGN KEY(" + KEY_DIRECTORY_ID + ") REFERENCES " + TABLE_DIRECTORY + "(" + KEY_ID + ")" +
                                                                "PRIMARY KEY(" + KEY_FEED_ID + "," + KEY_DIRECTORY_ID + "));";


    private static final String CREATE_TABLE_FILTER =           "create table " + TABLE_FILTER + "(" +
                                                                KEY_ID + " integer primary key autoincrement, " +
                                                                KEY_FILTER_NAME + " text, " +
                                                                KEY_DIRECTORY_ID + " integer, " +
                                                                "FOREIGN KEY(" + KEY_DIRECTORY_ID + ") REFERENCES " + TABLE_DIRECTORY + "(" + KEY_ID + "));";

    private static final String CREATE_TABLE_DIRECTORY_TYPES =  "create table " + TABLE_DIRECTORY_TYPES + "(" +
                                                                KEY_ID + " integer primary key autoincrement, " +
                                                                KEY_DIRECTORY_TYPE + " text);";

    private Context context;
    private SQLiteDatabase rDB = null, wDB = null;

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

    public static synchronized DatabaseHelper getInstance() {
        return instance;
    }

    public void onCreate(SQLiteDatabase db) {
        try {
            db.execSQL(CREATE_TABLE_DIRECTORY);
            db.execSQL(CREATE_TABLE_FEED);
            db.execSQL(CREATE_TABLE_FEED_DIRECTORY);
            db.execSQL(CREATE_TABLE_ARTICLE);
            db.execSQL(CREATE_TABLE_FILTER);
            db.execSQL(CREATE_TABLE_DIRECTORY_TYPES);

            // Set the Directory Types
            ContentValues typesValues1 = new ContentValues();
            typesValues1.put(KEY_DIRECTORY_TYPE, "Saved Directories");
            db.insert(TABLE_DIRECTORY_TYPES, null, typesValues1);

            ContentValues typesValues2 = new ContentValues();
            typesValues2.put(KEY_DIRECTORY_TYPE, "Feed Directories");
            db.insert(TABLE_DIRECTORY_TYPES, null, typesValues2);

            // Temp directories
//            for (int i = 0 ; i < 10 ; i++) {
//                ContentValues savedValues = new ContentValues();
//                savedValues.put(KEY_DIRECTORY_NAME, "savedDirectory" + i);
//                savedValues.put(KEY_DIRECTORY_TYPE, "Saved");
//                db.insert(TABLE_DIRECTORY, null, savedValues);
//
//                ContentValues feedValues = new ContentValues();
//                feedValues.put(KEY_DIRECTORY_NAME, "feedDirectory" + i);
//                feedValues.put(KEY_DIRECTORY_TYPE, "Feed");
//                db.insert(TABLE_DIRECTORY, null, feedValues);
//            }

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
            db.execSQL("drop table if exists " + TABLE_DIRECTORY_TYPES);

            onCreate(db);
        } catch(SQLiteException e) {
            Log.e("Cannot Drop Tables ", e.toString());
        }
    }

    private synchronized SQLiteDatabase openReadableDB() {
        if (rDB == null) {
            rDB = instance.getReadableDatabase();
        }
        return rDB;
    }

    private synchronized SQLiteDatabase openWritableDB() {
        if (wDB == null) {
            wDB = instance.getWritableDatabase();
        }
        return wDB;
    }

    public void closeDBs() {
        if (rDB != null)
            rDB.close();
        if (wDB != null)
            wDB.close();
        if (instance != null)
            instance.close();
    }

    public long insertArticles(ArrayList<Article> articles) {
        SQLiteDatabase db = openReadableDB();
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

        return totalRows;
    }

    // query(String table, String[] columns, String selection, String[] selectionArgs, String groupBy, String having, String orderBy)
    public Cursor getAllArticles() {
        SQLiteDatabase db = openReadableDB();
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
                        KEY_ARTICLE_DATE);
    }

    public int deleteArticlesFromDirectory() {
        SQLiteDatabase db = openWritableDB();
        return db.delete(TABLE_ARTICLE, null, null);
    }

    public Cursor getAllDirectories(String directoryType) {
        SQLiteDatabase db = openReadableDB();
        return db.query(TABLE_DIRECTORY,
                        new String[] {
                                KEY_ID,
                                KEY_DIRECTORY_NAME
                        },
                        "upper(" + KEY_DIRECTORY_TYPE + ") LIKE upper('%" + directoryType + "%')",
                        null,
                        null,
                        null,
                        null);
    }

    public Cursor getDirectoryTypes() {
        SQLiteDatabase db = openReadableDB();
        return db.query(TABLE_DIRECTORY_TYPES,
                        new String[] {
                                KEY_ID,
                                KEY_DIRECTORY_TYPE
                        },
                        null,
                        null,
                        null,
                        null,
                        KEY_DIRECTORY_TYPE + " DESC");
    }

    public long insertDirectory(String directoryName, String directoryType) {
        SQLiteDatabase db = openWritableDB();

        ContentValues values = new ContentValues();
        values.put(KEY_DIRECTORY_NAME, directoryName);
        values.put(KEY_DIRECTORY_TYPE, directoryType);

        return db.insert(TABLE_DIRECTORY, null, values);
    }
}