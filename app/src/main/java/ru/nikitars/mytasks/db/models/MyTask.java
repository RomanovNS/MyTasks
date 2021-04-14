package ru.nikitars.mytasks.db.models;

public class MyTask {

    private int id;
    private int listID;
    private boolean isInMyDay;
    private boolean isImportant;
    private boolean isDone;
    private int createDate;
    private int toDoDate;
    private int remindDate;
    private int completeTime;
    private String taskTitle;
    private String taskExplanation;

    public MyTask(int id, int listID, boolean isInMyDay, boolean isImportant, boolean isDone, int createDate, int toDoDate, int remindDate, int completeTime, String taskTitle, String taskExplanation) {
        this.id = id;
        this.listID = listID;
        this.isInMyDay = isInMyDay;
        this.isImportant = isImportant;
        this.isDone = isDone;
        this.createDate = createDate;
        this.toDoDate = toDoDate;
        this.remindDate = remindDate;
        this.completeTime = completeTime;
        this.taskTitle = taskTitle;
        this.taskExplanation = taskExplanation;
    }

    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }

    public int getListID() {
        return listID;
    }
    public void setListID(int listID) {
        this.listID = listID;
    }

    public boolean isInMyDay() {
        return isInMyDay;
    }
    public void setInMyDay(boolean inMyDay) {
        isInMyDay = inMyDay;
    }

    public boolean isImportant() {
        return isImportant;
    }
    public void setImportant(boolean important) {
        isImportant = important;
    }

    public boolean isDone() {
        return isDone;
    }
    public void setDone(boolean done) {
        isDone = done;
    }

    public int getCreateDate() {
        return createDate;
    }
    public void setCreateDate(int createDate) {
        this.createDate = createDate;
    }

    public int getToDoDate() {
        return toDoDate;
    }
    public void setToDoDate(int toDoDate) {
        this.toDoDate = toDoDate;
    }

    public int getRemindDate() {
        return remindDate;
    }
    public void setRemindDate(int remindDate) {
        this.remindDate = remindDate;
    }

    public int getCompleteTime() {
        return completeTime;
    }
    public void setCompleteTime(int completeTime) {
        this.completeTime = completeTime;
    }

    public String getTaskTitle() {
        return taskTitle;
    }
    public void setTaskTitle(String taskTitle) {
        this.taskTitle = taskTitle;
    }

    public String getTaskExplanation() {
        return taskExplanation;
    }
    public void setTaskExplanation(String taskExplanation) {
        this.taskExplanation = taskExplanation;
    }
}
