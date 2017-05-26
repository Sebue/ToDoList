package pl.edu.us.sebue.todolist;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.icu.util.Calendar;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.util.Date;

import pl.edu.us.sebue.todolist.model.db.DataModel;

public class TaskActivity extends AppCompatActivity {
    DataModel task;
    EditText titleText;
    EditText descText;
    CheckBox doneCheck;
    Spinner priority;
    TextView dateView;

    static Date date;

    FloatingActionButton fabDelete;
    FloatingActionButton fabSave;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task);
        priority = (Spinner) findViewById(R.id.prioritySpinner);
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, getPriorityForSpinner());
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        priority.setAdapter(spinnerAdapter);
        priority.setSelection(3);
        titleText = (EditText) findViewById(R.id.title);
        descText = (EditText) findViewById(R.id.description);
        doneCheck = (CheckBox) findViewById(R.id.doneCheckBox);
        dateView = (TextView) findViewById(R.id.dateView);
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            task = (DataModel) extras.get(String.valueOf(R.string.extrasTask));
            if(task != null) {
                titleText.setText(task.getTitle());
                descText.setText(task.getDescription());
                doneCheck.setChecked(task.isCompleted());
                priority.setSelection(spinnerAdapter.getPosition(task.getPriority().toString()));
            }
        }

        fabDelete = (FloatingActionButton) findViewById(R.id.fabDelete);
        fabDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(TaskActivity.this, MainActivity.class);
                if(task != null) {
                    intent.putExtra(String.valueOf(R.string.extrasDeleteId), task.getId());
                }
                startActivity(intent);
            }
        });

        fabSave = (FloatingActionButton) findViewById(R.id.fabSave);
        fabSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isMandatoryPopulated()) {
                    Intent intent = new Intent(TaskActivity.this, MainActivity.class);
                    intent.putExtra(String.valueOf(R.string.extrasTask), getPopulatedTask());
                    startActivity(intent);
                }
            }
        });
    }

    protected DataModel getPopulatedTask(){
        if(task == null) {
            task = new DataModel(titleText.getText().toString(), DataModel.Priority.valueOf(priority.getSelectedItem().toString()));
        }
        else{
            task.setTitle(titleText.getText().toString());
            task.setPriority(DataModel.Priority.valueOf(priority.getSelectedItem().toString()));
        }
        task.setCompleted(doneCheck.isChecked());
        task.setDescription(descText.getText().toString());

        return task;
    }

    protected boolean isMandatoryPopulated(){
        if(titleText.getText().toString().isEmpty()){
            Toast.makeText(this, "Missing title. Fill mandatory attribute.", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    protected String [] getPriorityForSpinner(){
        DataModel.Priority [] priorities = DataModel.Priority.values();
        String [] results = new String[priorities.length];
        for(int i = 0; i< results.length; i++){
            results[i] = priorities[i].toString();
        }
        return results;
    }

    @Override
    protected void onPause(){
        super.onPause();
        finishActivity(0);
    }

    public void showDatePickerDialog(View v) {
        date = new Date();
        DialogFragment newFragment = new DatePickerFragment();
        newFragment.show(getSupportFragmentManager(), "datePicker");

        DialogFragment newFragmentTime = new TimePickerFragment();
        newFragmentTime.show(getSupportFragmentManager(), "timePicker");

        dateView.setText(date.toString());
    }

    private static class DatePickerFragment extends DialogFragment
            implements DatePickerDialog.OnDateSetListener {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current date as the default date in the picker
            final Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);

            // Create a new instance of DatePickerDialog and return it
            return new DatePickerDialog(getActivity(), this, year, month, day);
        }

        public void onDateSet(DatePicker view, int year, int month, int day) {
            date.setYear(year);
            date.setMonth(month);
            date.setDate(day);
        }
    }

    private static class TimePickerFragment extends DialogFragment
            implements TimePickerDialog.OnTimeSetListener {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current time as the default values for the picker
            final Calendar c = Calendar.getInstance();
            int hour = c.get(Calendar.HOUR_OF_DAY);
            int minute = c.get(Calendar.MINUTE);

            // Create a new instance of TimePickerDialog and return it
            return new TimePickerDialog(getActivity(), this, hour, minute,
                    DateFormat.is24HourFormat(getActivity()));
        }

        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            date.setHours(hourOfDay);
            date.setMinutes(minute);
        }
    }
}
