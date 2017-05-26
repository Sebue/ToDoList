package pl.edu.us.sebue.todolist.model.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by Sebue on 25.05.2017.
 */

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DEBUG_TAG = "ToDoDatabase";
    private static final int DB_VERSION = 1;
    public static final String DB_TODO_TABLE = "task";
    private static final String ID_OPTIONS = "INTEGER PRIMARY KEY AUTOINCREMENT";
    private static final String TITLE_OPTIONS = "TEXT NOT NULL";
    private static final String IS_COMPLETED_OPTIONS = "INTEGER NOT NULL";
    private static final String TEXT_OPTIONAL_OPTIONS = "TEXT";

    private static final String DB_CREATE_TODO_TABLE =
            "CREATE TABLE " + DB_TODO_TABLE + "( "
                    + DataModel.ID + " " + ID_OPTIONS + ", "
                    + DataModel.TITLE + " " + TITLE_OPTIONS + ", "
                    + DataModel.PRIORITY + " " + TITLE_OPTIONS + ", "
                    + DataModel.IS_COMPLETED + " " + IS_COMPLETED_OPTIONS + ", "
                    + DataModel.REFERENCE + " " + TEXT_OPTIONAL_OPTIONS + ", "
                    + DataModel.DESCRIPTION + " " + TEXT_OPTIONAL_OPTIONS + ", "
                    + DataModel.DATE + " " + TEXT_OPTIONAL_OPTIONS
                    + ");";
    private static final String DROP_TODO_TABLE = "DROP TABLE IF EXISTS " + DB_TODO_TABLE;

    public DatabaseHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(DB_CREATE_TODO_TABLE);

        Log.d(DEBUG_TAG, "Database creating...");
        Log.d(DEBUG_TAG, "Table " + DB_TODO_TABLE + " ver." + DB_VERSION + " created");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(DROP_TODO_TABLE);

        Log.d(DEBUG_TAG, "Database updating...");
        Log.d(DEBUG_TAG, "Table " + DB_TODO_TABLE + " updated from ver." + oldVersion + " to ver." + newVersion);
        Log.d(DEBUG_TAG, "All data is lost.");

        onCreate(db);
    }
}
