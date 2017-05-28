package pl.edu.us.sebue.todolist.controller;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import pl.edu.us.sebue.todolist.R;
import pl.edu.us.sebue.todolist.model.db.DataModel;

/**
 * Created by Sebue on 25.05.2017.
 */

public class RowAdapter extends ArrayAdapter<DataModel> {
    private Context context;
    private int layoutId;
    private List<DataModel> tasks;

    public RowAdapter(Context context, int layoutId, List<DataModel> tasks){
        super(context, layoutId, tasks);
        this.context = context;
        this.layoutId = layoutId;
        this.tasks = tasks;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        View row = convertView;
        RowHolder holder = null;

        if(row == null)
        {
            LayoutInflater inflater = ((Activity)context).getLayoutInflater();
            row = inflater.inflate(layoutId, parent, false);

            holder = new RowHolder();
            holder.title = (TextView)row.findViewById(R.id.taskTitle);
            holder.priority = (TextView)row.findViewById(R.id.taskPriority);
            holder.isFinished = (ImageView)row.findViewById(R.id.taskFinished);

            row.setTag(holder);
        }
        else
        {
            holder = (RowHolder)row.getTag();
        }

        DataModel object = tasks.get(position);
        holder.title.setText(object.getTitle());
        holder.priority.setText(object.getPriority().toString());
        switch(object.getPriority()){
            case Critical:
                row.setBackgroundColor(context.getColor(R.color.colorCritical));
                break;
            case Major:
                row.setBackgroundColor(context.getColor(R.color.colorMajor));
            case Minor:
                row.setBackgroundColor(context.getColor(R.color.colorPrimaryDark));
                break;
            case None:
                row.setBackgroundColor(context.getColor(R.color.colorBackground));
                holder.priority.setText("");
                break;
        }
        if(object.isCompleted()) {
            holder.isFinished.setImageDrawable(context.getDrawable(R.drawable.ic_done_black_256dp_1x));
        } else {
            holder.isFinished.setImageDrawable(null);
        }
        return row;
    }

    public void refresh(List<DataModel> tasks){
        this.tasks.clear();
        this.tasks.addAll(tasks);
        notifyDataSetChanged();
    }

    static class RowHolder{
        TextView title;
        TextView priority;
        ImageView isFinished;
    }
}
