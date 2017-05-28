package pl.edu.us.sebue.todolist.model.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import static android.R.attr.id;
import static pl.edu.us.sebue.todolist.model.db.DatabaseHelper.DB_TODO_TABLE;


/**
 * Created by Sebue on 25.05.2017.
 */

public class DbAdapter {
    private static final int DB_VERSION = 1;
    private static final String DB_NAME = "toDoDatabase.db";
    private static final String DEBUG_TAG = "DbAdapter";

    private SQLiteDatabase db;
    private Context context;
    private DatabaseHelper dbHelper;

    public DbAdapter(Context context) {
        this.context = context;
    }

    public DbAdapter open(){
        dbHelper = new DatabaseHelper(context, DB_NAME, null, DB_VERSION);

        try {
            db = dbHelper.getWritableDatabase();
        } catch (SQLException e) {
            db = dbHelper.getReadableDatabase();
        }
        return this;
    }

    public void clearDatabase(){
        dbHelper.onUpgrade(db, DB_VERSION, DB_VERSION);
    }

    public void close() {
        dbHelper.close();
    }

    public long insertTask(DataModel task){
        ContentValues newTaskValues = new ContentValues();
        newTaskValues.put(DataModel.TITLE, task.getTitle());
        newTaskValues.put(DataModel.PRIORITY, String.valueOf(task.getPriority()));
        newTaskValues.put(DataModel.IS_COMPLETED, task.isCompleted());
        newTaskValues.put(DataModel.DATE, String.valueOf(android.text.format.DateFormat.format("dd/MM/yyyy HH:mm", task.getDate())));
        newTaskValues.put(DataModel.REFERENCE, task.getReference());
        newTaskValues.put(DataModel.DESCRIPTION, task.getDescription());

        Log.d(DEBUG_TAG, "Insert task: " + task.getTitle());
        return db.insert(DB_TODO_TABLE, null, newTaskValues);
    }

    public boolean updateTask(DataModel task){
        ContentValues updateTaskValues = new ContentValues();
        updateTaskValues.put(DataModel.TITLE, task.getTitle());
        updateTaskValues.put(DataModel.PRIORITY, String.valueOf(task.getPriority()));
        updateTaskValues.put(DataModel.IS_COMPLETED, task.isCompleted());
        updateTaskValues.put(DataModel.DATE, String.valueOf(android.text.format.DateFormat.format("dd/MM/yyyy HH:mm", task.getDate())));
        updateTaskValues.put(DataModel.REFERENCE, task.getReference());
        updateTaskValues.put(DataModel.DESCRIPTION, task.getDescription());

        return db.update(DB_TODO_TABLE, updateTaskValues, DataModel.ID + "=" + task.getId(), null) > 0;
    }

    public boolean deleteTodo(long id){
        String where = DataModel.ID + "=" + id;
        return db.delete(DB_TODO_TABLE, where, null) > 0;
    }

    public boolean deleteAllDoneTasks(){
        String where = DataModel.IS_COMPLETED + "=" + "1";
        return db.delete(DB_TODO_TABLE, where, null) > 0;
    }


    public List<DataModel> getAllTodos() {
        return getAllTodos(DataModel.ID);
    }

    public List<DataModel> getAllTodos(String column) {
        String[] columns = {DataModel.ID, DataModel.TITLE, DataModel.PRIORITY, DataModel.IS_COMPLETED, DataModel.REFERENCE, DataModel.DESCRIPTION, DataModel.DATE};
        Cursor cursor = db.query(DB_TODO_TABLE, columns, null, null, null, null, column);
        List<DataModel> tasks = new ArrayList<>();
        if(cursor != null && cursor.moveToFirst()){
            do{
                DataModel task = getDataModel(cursor);
                tasks.add(task);
            } while(cursor.moveToNext());
        }

        return tasks;
    }

    private DataModel getDataModel(Cursor cursor) {
        String title = cursor.getString(1);
        String priority = cursor.getString(2);
        DataModel task = new DataModel(title, DataModel.Priority.valueOf(priority));
        task.setId(cursor.getLong(0));
        task.setCompleted(cursor.getInt(3) > 0 ? true : false);
        task.setReference(cursor.getBlob(4));
        task.setDescription(cursor.getString(5));
        DateFormat format = DateFormat.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        try {
            task.setDate(sdf.parse(cursor.getString(6)));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Log.d(DEBUG_TAG, "DataModel - Task: ID - " + id + "; Title: " + title);
        return task;
    }

    public DataModel getTodo(long id) {
        Log.d(DEBUG_TAG, "Getting task with ID: " + id + " from tasks count: " + getTasksCount());
        String[] columns = {DataModel.ID, DataModel.TITLE, DataModel.PRIORITY, DataModel.IS_COMPLETED, DataModel.REFERENCE, DataModel.DESCRIPTION, DataModel.DATE};
        String where = DataModel.ID + "=" + id;
        Cursor cursor = db.query(DB_TODO_TABLE, columns, where, null, null, null, null);
        DataModel task = null;
        if(cursor != null && cursor.moveToFirst()) {
            task = getDataModel(cursor);
        }
        else{
            Log.d(DEBUG_TAG, "Task not founded for id: " + id);
        }
        return task;
    }

    public int getTasksCount(){
        return db.rawQuery("select * from " + DatabaseHelper.DB_TODO_TABLE, null).getCount();
    }
}
