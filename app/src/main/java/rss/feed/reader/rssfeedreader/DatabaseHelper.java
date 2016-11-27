/* **************************************************
Author: Vlad Zat

Created: 2016/11/12
Modified: 2016/11/27
************************************************** */


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
    private static final int DATABASE_VERSION = 25;
    private static final String KEY_ID = "_id";

    // Articles Table
    private static final String TABLE_ARTICLE = "Articles";
    private static final String KEY_ARTICLE_TITLE = "title";
    private static final String KEY_ARTICLE_DESCRIPTION = "description";
    private static final String KEY_ARTICLE_LINK = "link";
    private static final String KEY_ARTICLE_DATE = "date";
    private static final String KEY_SAVED_DIRECTORY_ID = "savedDirectoryID";

    // Directories Table
    private static final String TABLE_DIRECTORY = "Directories";
    private static final String KEY_DIRECTORY_ID = "directoryID";
    private static final String KEY_DIRECTORY_NAME = "directoryName";
    private static final String KEY_DIRECTORY_TYPE = "directoryType";

    // Feeds Table
    private static final String TABLE_FEED = "Feeds";
//    private static final String KEY_FEED_ID = "feedID";
    private static final String KEY_FEED_NAME = "feedName";
    private static final String KEY_FEED_URL = "feedURL";

    // FeedDirectories Table
//    private static final String TABLE_FEED_DIRECTORY = "FeedDirectories";

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
                                                                KEY_SAVED_DIRECTORY_ID + " integer," +
                                                                "FOREIGN KEY(" + KEY_DIRECTORY_ID + ") REFERENCES " + TABLE_DIRECTORY + "(" + KEY_ID + ")," +
                                                                "FOREIGN KEY(" + KEY_SAVED_DIRECTORY_ID + ") REFERENCES " + TABLE_DIRECTORY + "(" + KEY_ID + "));";

    private static final String CREATE_TABLE_DIRECTORY =        "create table " + TABLE_DIRECTORY + "(" +
                                                                KEY_ID + " integer primary key autoincrement, " +
                                                                KEY_DIRECTORY_NAME + " text, " +
                                                                KEY_DIRECTORY_TYPE + " text);";

    private static final String CREATE_TABLE_FEED =             "create table " + TABLE_FEED + "(" +
                                                                KEY_ID + " integer primary key autoincrement, " +
                                                                KEY_FEED_NAME + " text, " +
                                                                KEY_FEED_URL + " text, " +
                                                                KEY_DIRECTORY_ID + " integer, " +
                                                                "FOREIGN KEY(" + KEY_DIRECTORY_ID + ") REFERENCES " + TABLE_DIRECTORY + "(" + KEY_ID + "));";

//    private static final String CREATE_TABLE_FEED_DIRECTORY =   "create table " + TABLE_FEED_DIRECTORY + "(" +
//                                                                KEY_FEED_ID + " integer, " +
//                                                                KEY_DIRECTORY_ID + " integer, " +
//                                                                "FOREIGN KEY(" + KEY_FEED_ID + ") REFERENCES " + TABLE_FEED + "(" + KEY_ID + "), " +
//                                                                "FOREIGN KEY(" + KEY_DIRECTORY_ID + ") REFERENCES " + TABLE_DIRECTORY + "(" + KEY_ID + ")" +
//                                                                "PRIMARY KEY(" + KEY_FEED_ID + "," + KEY_DIRECTORY_ID + "));";


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

    public void onCreate(SQLiteDatabase db) {
        try {
            db.execSQL(CREATE_TABLE_DIRECTORY);
            db.execSQL(CREATE_TABLE_FEED);
//            db.execSQL(CREATE_TABLE_FEED_DIRECTORY);
            db.execSQL(CREATE_TABLE_ARTICLE);
            db.execSQL(CREATE_TABLE_FILTER);
            db.execSQL(CREATE_TABLE_DIRECTORY_TYPES);

            // Set the Directory Types
            ContentValues typesValues1 = new ContentValues();
            typesValues1.put(KEY_DIRECTORY_TYPE, "Read Later");
            db.insert(TABLE_DIRECTORY_TYPES, null, typesValues1);

            ContentValues typesValues2 = new ContentValues();
            typesValues2.put(KEY_DIRECTORY_TYPE, "Feeds");
            db.insert(TABLE_DIRECTORY_TYPES, null, typesValues2);

            // Temp feeds
//            ContentValues feedsValues1 = new ContentValues();
//            feedsValues1.put(KEY_FEED_NAME, "Reddit");
//            feedsValues1.put(KEY_FEED_URL, "https://www.reddit.com/.rss");
//            feedsValues1.put(KEY_DIRECTORY_ID, 1);
//            db.insert(TABLE_FEED, null, feedsValues1);
//
//            ContentValues feedsValues2 = new ContentValues();
//            feedsValues2.put(KEY_FEED_NAME, "RTE");
//            feedsValues2.put(KEY_FEED_URL, "http://www.rte.ie/news/rss/news-headlines.xml");
//            feedsValues2.put(KEY_DIRECTORY_ID, 1);
//            db.insert(TABLE_FEED, null, feedsValues2);

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
//            db.execSQL("drop table if exists " + TABLE_FEED_DIRECTORY);
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

    public long insertArticles(ArrayList<Article> articles, int directoryID) {
        SQLiteDatabase db = openReadableDB();
        long totalRows = 0;

        for (int i = 0 ; i < articles.size() ; i++) {
            ContentValues values = new ContentValues();
            values.put(KEY_ARTICLE_TITLE, articles.get(i).getTitle());
            values.put(KEY_ARTICLE_DESCRIPTION, articles.get(i).getDescription());
            values.put(KEY_ARTICLE_LINK, articles.get(i).getLink());
            values.put(KEY_ARTICLE_DATE, articles.get(i).getDate());
            values.put(KEY_DIRECTORY_ID, directoryID);

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

    public Cursor getAllArticlesFromDirectoryFiltered(int directoryID, String directoryType) {
        SQLiteDatabase db = openReadableDB();

        // Check the type of directory
        if ("Feed".equals(directoryType)) {
            Cursor filters = db.query(  TABLE_FILTER,
                    new String[] {KEY_FILTER_NAME},
                    KEY_DIRECTORY_ID + " = " + directoryID,
                    null,
                    null,
                    null,
                    null);

            // Check if there are any filters
            if (filters != null && filters.getCount() > 0) {
                // Get only the articles that contain all the filters
                try {
                    return db.rawQuery(
                            "select articles.* " +
                            "from articles left outer join filters on (articles.directoryID = filters.directoryID) " +
                            "where  articles.directoryID = " + directoryID + " AND " +
                                    "(upper(articles.title) LIKE '%' || upper(filters.filterName) || '%' OR " +
                                    "upper(articles.description) LIKE '%' || upper(filters.filterName) || '%') " +
                            "group by articles._id " +
                            "having count(articles._id) = ( select count(*) " +
                                                            "from filters " +
                                                            "where directoryID = articles.directoryID)" +
                            "order by date desc", null);
                } finally {
                    filters.close();
                }
            } else {
                // Otherwise get all the articles
                try {
                    return getAllArticlesFromDirectory(directoryID, directoryType);
                } finally {
                    if (filters != null)
                        filters.close();
                }
            }
        }

        // If it's a saved directory then there are no filters
        return getAllArticlesFromDirectory(directoryID, directoryType);
    }

    public Cursor getAllArticlesFromDirectory(int directoryID, String directoryType) {
        SQLiteDatabase db = openReadableDB();
        String key_directory;

        if ("Feed".equals(directoryType)) {
            key_directory = KEY_DIRECTORY_ID;
        } else {
            key_directory = KEY_SAVED_DIRECTORY_ID;
        }

        return db.query(TABLE_ARTICLE,
                        new String[] {
                                KEY_ID,
                                KEY_ARTICLE_TITLE,
                                KEY_ARTICLE_DESCRIPTION,
                                KEY_ARTICLE_LINK,
                                KEY_ARTICLE_DATE
                        },
                        key_directory + " = " + directoryID,
                        null,
                        null,
                        null,
                        KEY_ARTICLE_DATE + " desc");
    }

    public int deleteArticlesFromDirectory(int directoryID) {
        SQLiteDatabase db = openWritableDB();

        ContentValues values = new ContentValues();
        values.putNull(KEY_DIRECTORY_ID);

        // Remove the link between the directory and the articles if the article is saved in a saved directory
        db.update(TABLE_ARTICLE, values, KEY_DIRECTORY_ID + " = " + directoryID + " AND " + KEY_SAVED_DIRECTORY_ID + " IS NOT NULL", null);

        // Delete all the other articles
        return db.delete(TABLE_ARTICLE, KEY_DIRECTORY_ID + " = " + directoryID, null);
    }

    public Cursor getAllDirectories(String directoryType) {
        SQLiteDatabase db = openReadableDB();
        return db.query(TABLE_DIRECTORY,
                        new String[] {
                                KEY_ID,
                                KEY_DIRECTORY_NAME,
                                KEY_DIRECTORY_TYPE
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

    public Cursor getFeedsFromDirectory(int directoryID) {
        SQLiteDatabase db = openReadableDB();
        return db.query(TABLE_FEED,
                        new String[] {
                                KEY_ID,
                                KEY_FEED_NAME,
                                KEY_FEED_URL
                        },
                        KEY_DIRECTORY_ID + " = " + directoryID,
                        null,
                        null,
                        null,
                        null);
    }

    public long insertDirectory(String directoryName, String directoryType) {
        SQLiteDatabase db = openWritableDB();

        ContentValues values = new ContentValues();
        values.put(KEY_DIRECTORY_NAME, directoryName);
        values.put(KEY_DIRECTORY_TYPE, directoryType);

        return db.insert(TABLE_DIRECTORY, null, values);
    }

    public int updateDirectory(int directoryID, String directoryName, String directoryType) {
        if (directoryID != -1) {
            SQLiteDatabase db = openWritableDB();

            ContentValues values = new ContentValues();
            values.put(KEY_DIRECTORY_NAME, directoryName);
            values.put(KEY_DIRECTORY_TYPE, directoryType);

            return db.update(TABLE_DIRECTORY, values, KEY_ID + " = " + directoryID, null);
        }
        return 0;
    }

    public int deleteDirectory(int directoryID) {
        SQLiteDatabase db = openWritableDB();

        // Delete Feeds from Directory
        db.delete(TABLE_FEED, KEY_DIRECTORY_ID + " = " + directoryID, null);

        // Delete Articles from Directory
        deleteArticlesFromDirectory(directoryID);

        // Delete Filters from Directory
        db.delete(TABLE_FILTER, KEY_DIRECTORY_ID + " = " + directoryID, null);

        // Delete Directory
        return db.delete(TABLE_DIRECTORY, KEY_ID + " = " + directoryID, null);
    }

    public long insertFeed(String name, String url, int directoryID) {
        SQLiteDatabase db = openWritableDB();

        ContentValues values = new ContentValues();
        values.put(KEY_FEED_NAME, name);
        values.put(KEY_FEED_URL, url);
        values.put(KEY_DIRECTORY_ID, directoryID);

        return db.insert(TABLE_FEED, null, values);
    }

    public int deleteFeed(int feedID) {
        SQLiteDatabase db = getWritableDatabase();

        return db.delete(TABLE_FEED, KEY_ID + " = " + feedID, null);
    }

    public int updateFeed(int feedID, String feedName, String feedURL) {
        if (feedID != -1) {
            SQLiteDatabase db = openWritableDB();

            ContentValues values = new ContentValues();
            values.put(KEY_FEED_NAME, feedName);
            values.put(KEY_FEED_URL, feedURL);

            return db.update(TABLE_FEED, values, KEY_ID + " = " + feedID, null);
        }
        return 0;
    }

    public int setSavedDirectory(int articleID, int savedDirectoryID) {
        if (articleID != -1 && savedDirectoryID != -1) {
            SQLiteDatabase db = openWritableDB();

            ContentValues values = new ContentValues();
            values.put(KEY_SAVED_DIRECTORY_ID, savedDirectoryID);

            return db.update(TABLE_ARTICLE, values, KEY_ID + " = " + articleID, null);
        } else if (articleID != -1) {
            SQLiteDatabase db = openWritableDB();

            ContentValues values = new ContentValues();
            values.putNull(KEY_SAVED_DIRECTORY_ID);

            return db.update(TABLE_ARTICLE, values, KEY_ID + " = " + articleID, null);
        }
        return 0;
    }

    public int getSavedDirectory(int articleID) {
        SQLiteDatabase db = openReadableDB();
        Cursor cursor =  db.query(TABLE_ARTICLE,
                                new String[] {
                                        KEY_SAVED_DIRECTORY_ID
                                },
                                KEY_ID + " = " + articleID,
                                null,
                                null,
                                null,
                                null);

        try {
            if (cursor != null && cursor.getCount() == 0)
                return -1;
            cursor.moveToNext();
            if (cursor.isNull(0))
                return -1;
            return cursor.getInt(0);
        } finally {
            if (cursor != null)
                cursor.close();
        }
    }

    public Cursor getFilters(int directoryID) {
        SQLiteDatabase db = openReadableDB();
        return db.query(TABLE_FILTER,
                        new String[] {
                                KEY_ID,
                                KEY_FILTER_NAME,
                        },
                        KEY_DIRECTORY_ID + " = " + directoryID,
                        null,
                        null,
                        null,
                        null);
    }

    public long insertFilter(String filterName, int directoryID) {
        SQLiteDatabase db = openWritableDB();

        ContentValues values = new ContentValues();
        values.put(KEY_FILTER_NAME, filterName);
        values.put(KEY_DIRECTORY_ID, directoryID);

        return db.insert(TABLE_FILTER, null, values);
    }

    public int deleteFilter(int filterID) {
        SQLiteDatabase db = getWritableDatabase();

        return db.delete(TABLE_FILTER, KEY_ID + " = " + filterID, null);
    }

    public int updateFilter(int filterID, String filterName) {
        if (filterID != -1) {
            SQLiteDatabase db = openWritableDB();

            ContentValues values = new ContentValues();
            values.put(KEY_FILTER_NAME, filterName);

            return db.update(TABLE_FILTER, values, KEY_ID + " = " + filterID, null);
        }
        return 0;
    }
}