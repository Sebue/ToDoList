package pl.edu.us.sebue.todolist;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.icu.util.Calendar;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import pl.edu.us.sebue.todolist.model.db.DataModel;

public class TaskActivity extends AppCompatActivity {
    Context context;
    DataModel task;
    EditText titleText;
    EditText descText;
    CheckBox doneCheck;
    Spinner priority;
    TextView dateView;
    ImageView reference;

    byte [] referenceByte;

    static Date date;
    boolean isReminderSet;

    FloatingActionButton fabDelete;
    FloatingActionButton fabSave;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task);
        context = this;
        date = new Date();
        isReminderSet = false;
        priority = (Spinner) findViewById(R.id.prioritySpinner);
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, getPriorityForSpinner());
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        priority.setAdapter(spinnerAdapter);
        priority.setSelection(3);
        titleText = (EditText) findViewById(R.id.title);
        descText = (EditText) findViewById(R.id.description);
        doneCheck = (CheckBox) findViewById(R.id.doneCheckBox);
        dateView = (TextView) findViewById(R.id.dateView);
        reference = (ImageView) findViewById(R.id.reference);
        reference.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                if(referenceByte == null) {
                    pickImage();
                }
                else{
                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setTitle("Edit image");
                    ImageView image = new ImageView(context);
                    Bitmap selectedImage = BitmapFactory.decodeByteArray(referenceByte, 0, referenceByte.length);
                    image.setImageBitmap(selectedImage);
                    builder.setView(image);
                    builder.setPositiveButton("Change", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int id) {
                            pickImage();
                        }
                    });
                    builder.setNegativeButton("Delete", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int id) {
                            referenceByte = null;
                            reference.setImageResource(android.R.drawable.ic_menu_camera);
                        }
                    });

                    builder.create();
                    builder.show();
                }
            }
        });
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            task = (DataModel) extras.get(String.valueOf(R.string.extrasTask));
            if(task != null) {
                titleText.setText(task.getTitle());
                descText.setText(task.getDescription());
                doneCheck.setChecked(task.isCompleted());
                priority.setSelection(spinnerAdapter.getPosition(task.getPriority().toString()));
                if(task.getDate() != null)
                    dateView.setText(DateFormat.format("dd/MM/yyyy HH:mm", task.getDate()));
                setImage(task);
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
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
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
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int reqCode, int resultCode, Intent data) {
        super.onActivityResult(reqCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            try {
                Uri imageUri = data.getData();
                InputStream imageStream = getContentResolver().openInputStream(imageUri);
                Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
                ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
                selectedImage.compress(Bitmap.CompressFormat.JPEG, 20, byteStream);
                referenceByte = byteStream.toByteArray();
                reference.setImageBitmap(selectedImage);
            } catch (FileNotFoundException e) {
                Toast.makeText(this, "File is not available", Toast.LENGTH_SHORT).show();
            }

        }
    }

    protected void pickImage(){
        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
        photoPickerIntent.setType("image/*");
        startActivityForResult(photoPickerIntent, 1);
    }

    protected DataModel getPopulatedTask(){
        if(task == null) {
            task = new DataModel(titleText.getText().toString(), DataModel.Priority.valueOf(priority.getSelectedItem().toString()));
        }
        else{
            task.setTitle(titleText.getText().toString());
            task.setPriority(DataModel.Priority.valueOf(priority.getSelectedItem().toString()));
        }
        task.setReference(referenceByte);
        task.setCompleted(doneCheck.isChecked());
        task.setDescription(descText.getText().toString());
        if(isReminderSet){
            task.setDate(date);
        }

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

    protected void setImage(DataModel task){
        if(task.getReference() != null){
            byte [] image = task.getReference();
            Bitmap selectedImage = BitmapFactory.decodeByteArray(image, 0, image.length);
            reference.setImageBitmap(selectedImage);
            referenceByte = image;
        }
    }

    public void showDatePickerDialog(View v) {
        DatePickerDialog newFragment = new DatePickerDialog(this);
        newFragment.show();
        newFragment.setOnDateSetListener(new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                date.setYear(year - 1900);
                date.setMonth(month);
                date.setDate(dayOfMonth);
                isReminderSet = true;
                dateView.setText(DateFormat.format("dd/MM/yyyy HH:mm", date));
            }
        });

        TimePickerFragment newFragmentTime = new TimePickerFragment();
        newFragmentTime.show(getSupportFragmentManager(), "TimePicker");
    }

    private static class TimePickerFragment extends DialogFragment
            implements TimePickerDialog.OnTimeSetListener {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            final Calendar c = Calendar.getInstance();
            int hour = c.get(Calendar.HOUR_OF_DAY);
            int minute = c.get(Calendar.MINUTE);

            return new TimePickerDialog(getActivity(), this, hour, minute,
                    DateFormat.is24HourFormat(getActivity()));
        }

        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            date.setHours(hourOfDay);
            date.setMinutes(minute);
        }
    }
}
