package pl.edu.us.sebue.todolist;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import pl.edu.us.sebue.todolist.controller.RowAdapter;
import pl.edu.us.sebue.todolist.model.db.AlarmReminder;
import pl.edu.us.sebue.todolist.model.db.DataModel;
import pl.edu.us.sebue.todolist.model.db.DbAdapter;

public class MainActivity extends AppCompatActivity {
    private static final String DEBUG_TAG = "MAIN_ACTIVITY";
    Context context;
    ListView listView;
    DbAdapter dbAdapter;
    RowAdapter rowAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        context = this;
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        dbAdapter = new DbAdapter(this);
        dbAdapter.open();
        listView = (ListView) findViewById(R.id.listView);
        rowAdapter = new RowAdapter(this, R.layout.task_row, dbAdapter.getAllTodos());
        listView.setAdapter(rowAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(MainActivity.this, TaskActivity.class);
                DataModel task = rowAdapter.getItem(position);
                intent.putExtra(String.valueOf(R.string.extrasTask), task);
                startActivity(intent);
            }
        });

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, TaskActivity.class);
                startActivity(intent);
                //TODO add new task
//                DataModel data = new DataModel("Yolo", DataModel.Priority.None);
//                data.setCompleted(true);
//                long tro = dbAdapter.insertTask(data);
//                Toast.makeText(context, "Title: " + dbAdapter.getTodo(tro).getTitle(), Toast.LENGTH_SHORT).show();
//                rowAdapter.refresh(dbAdapter.getAllTodos());
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.dropTask) {
            dropAllTasks();
            return true;
        }
        else if (id == R.id.dropCompletedTasks) {
            dbAdapter.deleteAllDoneTasks();
            rowAdapter.refresh(dbAdapter.getAllTodos());
            Toast.makeText(context, "Removed all done tasks", Toast.LENGTH_SHORT).show();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void dropAllTasks(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.dropTasksAlert)
                .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dbAdapter.clearDatabase();
                        rowAdapter.refresh(dbAdapter.getAllTodos());
                    }
                })
                .setNegativeButton("NO", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                    }
                });
        builder.create();
        builder.show();
    }

    public void createNotifications(DataModel task) {
        Intent alarmIntent = new Intent(this, AlarmReminder.class);
        alarmIntent.putExtra("Title", task.getTitle());
        alarmIntent.putExtra("Text", "Priority: " + task.getPriority().toString() + ". " + task.getDescription());
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, alarmIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        alarmManager.set(AlarmManager.RTC_WAKEUP, task.getDate().getTime() - (1000 * 60 * 15), pendingIntent);
    }

    @Override
    public void onResume(){
        super.onResume();
        Bundle extras = getIntent().getExtras();
        try {
            if (extras != null && dbAdapter != null) {
                DataModel task = (DataModel) extras.get(String.valueOf(R.string.extrasTask));
                if (task != null) {
                    if (task.getId() < 0l) {
                        dbAdapter.insertTask(task);
                        if(task.getDate() != null && !task.isCompleted()) {
                            createNotifications(task);
                            Toast.makeText(context, "Notification has been set for task: " + task.getTitle(), Toast.LENGTH_SHORT).show();
                        }
                        else {
                            Toast.makeText(context, "Added task: " + task.getTitle(), Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        dbAdapter.updateTask(task);
                        Toast.makeText(context, "Updated task: " + task.getTitle(), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    long id = (long) extras.get(String.valueOf(R.string.extrasDeleteId));
                    dbAdapter.deleteTodo(id);
                    Toast.makeText(context, "Task is deleted", Toast.LENGTH_SHORT).show();
                }
            }
        }
        catch(Exception ex){
            Log.e(DEBUG_TAG, "Error in onResume, message: " + ex.getMessage());
        }
        if(getIntent().getExtras() != null){
            getIntent().getExtras().clear();
        }
        rowAdapter.refresh(dbAdapter.getAllTodos());
    }
}
