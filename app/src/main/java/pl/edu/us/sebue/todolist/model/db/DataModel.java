package pl.edu.us.sebue.todolist.model.db;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by Sebue on 25.05.2017.
 */

public class DataModel implements Serializable{
    public static final String ID = "ID";
    public static final String TITLE = "TITLE";
    public static final String IS_COMPLETED = "IS_COMPETED";
    public static final String PRIORITY = "PRIORITY";

    public static final String REFERENCE = "REFERENCE";
    public static final String DESCRIPTION = "DESCRIPTION";
    public static final String DATE = "DATE";

    public DataModel(String title, Priority priority){
        this.title = title;
        this.priority = priority;
        this.isCompleted = false;
        this.id = -1l;
    }

    String title;
    long id;
    boolean isCompleted;
    Priority priority;

    Date date;
    String description;
    byte[] reference;

    public enum Priority{
        Critical,
        Major,
        Minor,
        None
    }

    public boolean isCompleted() {
        return isCompleted;
    }

    public void setCompleted(boolean completed) {
        isCompleted = completed;
    }

    public Priority getPriority() {
        return priority;
    }

    public void setPriority(Priority priority) {
        this.priority = priority;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public byte[] getReference() {
        return reference;
    }

    public void setReference(byte[] reference) {
        this.reference = reference;
    }


    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

}
