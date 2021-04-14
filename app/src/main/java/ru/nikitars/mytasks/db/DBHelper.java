package ru.nikitars.mytasks.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import java.sql.Timestamp;
import java.util.Date;
import java.util.LinkedList;

import ru.nikitars.mytasks.db.models.MyList;
import ru.nikitars.mytasks.db.models.MyTask;

public class DBHelper extends SQLiteOpenHelper {
    public DBHelper(@Nullable Context context){
        super(context, "myTasksDB", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE MyList(" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "listName TEXT," +
                "themeType INTEGER," +
                "themeID INTEGER," +
                "listOrder TEXT" +
                ");");

        db.execSQL("CREATE TABLE MyTask(" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "listID INTEGER," +
                "isInMyDay INTEGER," +
                "isImportant INTEGER," +
                "isDone INTEGER," +
                "createDate INTEGER," +
                "toDoDate INTEGER," +
                "remindDate INTEGER," +
                "completeTime INTEGER," +
                "taskTitle TEXT," +
                "taskExplanation TEXT" +
                //"," +
                //"FOREIGN KEY(listID) REFERENCES MyList(id)" +
                ");");
    }

    public LinkedList<MyList> getLists(){
        LinkedList<MyList> myLists = new LinkedList<>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM MyList" , new String[]{});
        int idIdx = cursor.getColumnIndex("id");
        int listNameIdx = cursor.getColumnIndex("listName");
        int themeTypeIdx = cursor.getColumnIndex("themeType");
        int themeIDIdx = cursor.getColumnIndex("themeID");
        int listOrderIdx = cursor.getColumnIndex("listOrder");
        while(cursor.moveToNext()){
            myLists.add(new MyList(cursor.getInt(idIdx),
                                    cursor.getString(listNameIdx),
                                    cursor.getInt(themeTypeIdx),
                                    cursor.getInt(themeIDIdx),
                                    cursor.getString(listOrderIdx)));
        }
        return myLists;
    }

    public int createNewList(String listName){
        SQLiteDatabase db = getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT id FROM MyList WHERE listName = ?" , new String[]{listName});
        if (cursor.getCount() > 0){
            listName += "*";
        }
        db.beginTransaction();
        int id = -1;
        try{
            ContentValues values =  new ContentValues();
            values.put("listName", listName);
            values.put("themeType", 0);
            values.put("themeID", 6);
            values.put("listOrder", "");
            id = (int)db.insertOrThrow("MyList", null, values);
            db.setTransactionSuccessful();
        }
        finally {
            db.endTransaction();
        }
        return id;
    }

    public void createNewListTestNulls(String listName){
        SQLiteDatabase db = getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT id FROM MyList WHERE listName = ?" , new String[]{listName});
        if (cursor.getCount() > 0){
            listName += "*";
        }
        db.beginTransaction();
        try{
            Integer str = null;
            ContentValues values =  new ContentValues();
            values.put("listName", listName);
            values.put("themeType", str);
            values.put("themeID", str);
            values.put("listOrder", str);
            db.insertOrThrow("MyList", null, values);
            db.setTransactionSuccessful();
        }
        finally {
            db.endTransaction();
        }
    }

    public void renameList(int listID, String newName){
        SQLiteDatabase db = getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT id FROM MyList WHERE listName = ?" , new String[]{newName});
        if (cursor.getCount() > 0){
            cursor.moveToFirst();
            int id = cursor.getInt(cursor.getColumnIndex("id"));
            if (id == listID) return;
            newName += "*";
        }
        ContentValues values = new ContentValues();
        values.put("listName", newName);
        db.update("MyList", values, "id = ?", new String[]{listID + ""});
    }
    public void renameList(String lastName, String newName){
        if (lastName.equals(newName)) return;
        SQLiteDatabase db = getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT id FROM MyList WHERE listName = ?" , new String[]{lastName});
        if (cursor.getCount() > 0){
            cursor.moveToFirst();
            int id = cursor.getInt(cursor.getColumnIndex("id"));
            renameList(id, newName);
        }
    }
    //public void changeListTheme(int listID, int temeType, int themeID) {
    //    SQLiteDatabase db = getWritableDatabase();
    //    ContentValues values = new ContentValues();
    //    values.put("themeType", temeType);
    //    values.put("themeID", themeID);
    //    db.update("MyList", values, "id = ?", new String[]{listID + ""});
    //}

    public int getMyListID(String listName){
        int id = -1;
        SQLiteDatabase db = getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT id FROM MyList WHERE listName = ?" , new String[]{listName});
        if (cursor.getCount() > 0){
            cursor.moveToFirst();
            id = cursor.getInt(cursor.getColumnIndex("id"));
        }
        return id;
    }

    public MyList getMyList(int listID){
        MyList myList = null;
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM MyList WHERE id = ?" , new String[]{listID+""});
        if (cursor.getCount() > 0){
            cursor.moveToFirst();
            myList = new MyList(cursor.getInt(cursor.getColumnIndex("id")),
                                cursor.getString(cursor.getColumnIndex("listName")),
                                cursor.getInt(cursor.getColumnIndex("themeType")),
                                cursor.getInt(cursor.getColumnIndex("themeID")),
                                cursor.getString(cursor.getColumnIndex("listOrder")));
        }
        return myList;
    }
    public MyList getMyList(String listName){
        int id = getMyListID(listName);
        if (id >= 0) return getMyList(id);
        else return null;
    }

    public void deleteMyList(int listID){
        SQLiteDatabase db = getWritableDatabase();
        db.delete("MyList", "id = ?", new String[]{listID + ""});
    }
    public void deleteMyList(String listName){
        int id = getMyListID(listName);
        if (id >= 0) deleteMyList(id);
    }

    public void changeListTheme(int listID, int newThemeType, int newThemeID){
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("themeType", newThemeType);
        values.put("themeID", newThemeID);
        db.update("MyList", values, "id = ?", new String[]{listID + ""});
    }
    public void changeListTheme(String listName, int newThemeType, int newThemeID){
        int id = getMyListID(listName);
        if (id >= 0) changeListTheme(id, newThemeType, newThemeID);
    }

    public void createBasicLists(){
        int listID = getMyListID("MyDayList111");
        if (listID <= 0){
            createNewList("MyDayList111");
        }
        listID = getMyListID("PlannedList111");
        if (listID <= 0){
            createNewList("PlannedList111");
        }
        listID = getMyListID("ImportantList111");
        if (listID <= 0){
            createNewList("ImportantList111");
        }
        listID = getMyListID("TasksList111");
        if (listID <= 0){
            createNewList("TasksList111");
        }


    }

    public int createMyTask(int listID, boolean isInMyDay, boolean isImportant, int toDoDate, int remindTime, int completeTime, String title){
        MyTask myTask = new MyTask(0, listID, isInMyDay, isImportant, false, (int)(new Date().getTime() / 1000), toDoDate, remindTime, completeTime, title, "");
        SQLiteDatabase db = getWritableDatabase();
        db.beginTransaction();
        int newID = -1;
        try{
            ContentValues values =  new ContentValues();
            values.put("listID", myTask.getListID());
            values.put("isInMyDay", (myTask.isInMyDay())?1:0 );
            values.put("isImportant", (myTask.isImportant())?1:0);
            values.put("isDone", (myTask.isDone())?1:0);
            values.put("createDate", myTask.getCreateDate());
            values.put("toDoDate", myTask.getToDoDate());
            values.put("remindDate", myTask.getRemindDate());
            values.put("completeTime", myTask.getCompleteTime());
            values.put("taskTitle", myTask.getTaskTitle());
            values.put("taskExplanation", myTask.getTaskExplanation());

            newID = (int)db.insertOrThrow("MyTask", null, values);
            db.setTransactionSuccessful();
        }
        finally {
            db.endTransaction();
        }
        //if (newID > 0) {
        //    MyList myList = getMyList(listID);
        //    myList.addTaskToOrderList(newID);
        //    ContentValues values = new ContentValues();
        //    values.put("listOrder", myList.getListOrder());
        //    db.update("MyList", values, "id = ?", new String[]{listID + ""});
        //}
        return newID;
    }
    public MyTask getMyTask(int taskID){
        MyTask myTask = null;
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM MyTask WHERE id = ?" , new String[]{taskID+""});
        if (cursor.getCount() > 0){
            cursor.moveToFirst();
            myTask = new MyTask(cursor.getInt(cursor.getColumnIndex("id")),
                                cursor.getInt(cursor.getColumnIndex("listID")),
                                cursor.getInt(cursor.getColumnIndex("isInMyDay")) > 0,
                                cursor.getInt(cursor.getColumnIndex("isImportant")) > 0,
                                cursor.getInt(cursor.getColumnIndex("isDone")) > 0,
                                cursor.getInt(cursor.getColumnIndex("createDate")),
                                cursor.getInt(cursor.getColumnIndex("toDoDate")),
                                cursor.getInt(cursor.getColumnIndex("remindDate")),
                                cursor.getInt(cursor.getColumnIndex("completeTime")),
                                cursor.getString(cursor.getColumnIndex("taskTitle")),
                                cursor.getString(cursor.getColumnIndex("taskExplanation")));
        }
        return myTask;
    }
    public void restoreMyTask(MyTask myTask){
        SQLiteDatabase db = getWritableDatabase();
        db.beginTransaction();
        int newID = -1;
        try{
            ContentValues values =  new ContentValues();
            values.put("id", myTask.getId());
            values.put("listID", myTask.getListID());
            values.put("isInMyDay", (myTask.isInMyDay())?1:0 );
            values.put("isImportant", (myTask.isImportant())?1:0);
            values.put("isDone", (myTask.isDone())?1:0);
            values.put("createDate", myTask.getCreateDate());
            values.put("toDoDate", myTask.getToDoDate());
            values.put("remindDate", myTask.getRemindDate());
            values.put("completeTime", myTask.getCompleteTime());
            values.put("taskTitle", myTask.getTaskTitle());
            values.put("taskExplanation", myTask.getTaskExplanation());

            newID = (int)db.insertOrThrow("MyTask", null, values);
            db.setTransactionSuccessful();
        }
        finally {
            db.endTransaction();
        }
        //if (newID > 0) {
        //    MyList myList = getMyList(listID);
        //    myList.addTaskToOrderList(newID);
        //    ContentValues values = new ContentValues();
        //    values.put("listOrder", myList.getListOrder());
        //    db.update("MyList", values, "id = ?", new String[]{listID + ""});
        //}
    }
    public LinkedList<MyTask> getMyTaskList(int listID){
        LinkedList<MyTask> myTasks = new LinkedList<MyTask>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM MyTask WHERE listID = ?" , new String[]{listID+""});
        while (cursor.moveToNext()){
            myTasks.add(new MyTask(cursor.getInt(cursor.getColumnIndex("id")),
                    cursor.getInt(cursor.getColumnIndex("listID")),
                    cursor.getInt(cursor.getColumnIndex("isInMyDay")) > 0,
                    cursor.getInt(cursor.getColumnIndex("isImportant")) > 0,
                    cursor.getInt(cursor.getColumnIndex("isDone")) > 0,
                    cursor.getInt(cursor.getColumnIndex("createDate")),
                    cursor.getInt(cursor.getColumnIndex("toDoDate")),
                    cursor.getInt(cursor.getColumnIndex("remindDate")),
                    cursor.getInt(cursor.getColumnIndex("completeTime")),
                    cursor.getString(cursor.getColumnIndex("taskTitle")),
                    cursor.getString(cursor.getColumnIndex("taskExplanation"))));
        }
        return myTasks;
    }
    public void deleteMyTask(int taskId){
        SQLiteDatabase db = getReadableDatabase();
        db.delete("MyTask", "id = ?", new String[]{taskId + ""});
    }
    public void setMyTaskDone(int taskId, boolean isDone){
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("isDone", (isDone)?1:0);
        db.update("MyTask", values, "id = ?", new String[]{taskId + ""});
    }
    public void setMyTaskImportant(int taskId, boolean isImportant){
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("isImportant", (isImportant)?1:0);
        db.update("MyTask", values, "id = ?", new String[]{taskId + ""});
    }
    public void setMyTaskInMyDay(int taskId, boolean isInMyDay){
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("isInMyDay", (isInMyDay)?1:0);
        db.update("MyTask", values, "id = ?", new String[]{taskId + ""});
    }
    public void setMyTaskToDoDate(int taskId, int toDoDate){
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("toDoDate", toDoDate);
        db.update("MyTask", values, "id = ?", new String[]{taskId + ""});
    }
    public void setMyTaskRemindDate(int taskId, int remindDate){
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("remindDate", remindDate);
        db.update("MyTask", values, "id = ?", new String[]{taskId + ""});
    }
    public void setMyTaskCompleteTime(int taskId, int completeTime){
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("completeTime", completeTime);
        db.update("MyTask", values, "id = ?", new String[]{taskId + ""});
    }
    public void setMyTaskTitle(int taskId, String title){
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("taskTitle", title);
        db.update("MyTask", values, "id = ?", new String[]{taskId + ""});
    }
    public void setMyTaskExplanation(int taskId, String explanation){
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("taskExplanation", explanation);
        db.update("MyTask", values, "id = ?", new String[]{taskId + ""});
    }

    public int getMyListCount(int listID){
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT id FROM MyTask WHERE listID = ? AND isDone = 0" , new String[]{listID+""});
        return cursor.getCount();
    }

    public int getMyDayTasksNumber(){
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT id FROM MyTask WHERE isInMyDay = 1 AND isDone = 0" , new String[]{});
        return cursor.getCount();
    }
    public LinkedList<MyTask> getMyDayTasks(){
        LinkedList<MyTask> myTasks = new LinkedList<>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM MyTask WHERE isInMyDay = 1" , new String[]{});
        while (cursor.moveToNext()){
            myTasks.add(new MyTask(cursor.getInt(cursor.getColumnIndex("id")),
                    cursor.getInt(cursor.getColumnIndex("listID")),
                    cursor.getInt(cursor.getColumnIndex("isInMyDay")) > 0,
                    cursor.getInt(cursor.getColumnIndex("isImportant")) > 0,
                    cursor.getInt(cursor.getColumnIndex("isDone")) > 0,
                    cursor.getInt(cursor.getColumnIndex("createDate")),
                    cursor.getInt(cursor.getColumnIndex("toDoDate")),
                    cursor.getInt(cursor.getColumnIndex("remindDate")),
                    cursor.getInt(cursor.getColumnIndex("completeTime")),
                    cursor.getString(cursor.getColumnIndex("taskTitle")),
                    cursor.getString(cursor.getColumnIndex("taskExplanation"))));
        }
        return myTasks;
    }
    public void clearMyDayList(){
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("isInMyDay", 0);
        db.update("MyTask", values, "isInMyDay = 1", new String[]{});
    }

    public int getImportantTasksNumber(){
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT id FROM MyTask WHERE isImportant = 1 AND isDone = 0" , new String[]{});
        return cursor.getCount();
    }
    public LinkedList<MyTask> getImportantTasks(){
        LinkedList<MyTask> myTasks = new LinkedList<>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM MyTask WHERE isImportant = 1" , new String[]{});
        while (cursor.moveToNext()){
            myTasks.add(new MyTask(cursor.getInt(cursor.getColumnIndex("id")),
                    cursor.getInt(cursor.getColumnIndex("listID")),
                    cursor.getInt(cursor.getColumnIndex("isInMyDay")) > 0,
                    cursor.getInt(cursor.getColumnIndex("isImportant")) > 0,
                    cursor.getInt(cursor.getColumnIndex("isDone")) > 0,
                    cursor.getInt(cursor.getColumnIndex("createDate")),
                    cursor.getInt(cursor.getColumnIndex("toDoDate")),
                    cursor.getInt(cursor.getColumnIndex("remindDate")),
                    cursor.getInt(cursor.getColumnIndex("completeTime")),
                    cursor.getString(cursor.getColumnIndex("taskTitle")),
                    cursor.getString(cursor.getColumnIndex("taskExplanation"))));
        }
        return myTasks;
    }

    public int getPlannedTasksNumber(){
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT id FROM MyTask WHERE toDoDate > 0 AND isDone = 0" , new String[]{});
        return cursor.getCount();
    }
    public LinkedList<MyTask> getPlannedTasks(){
        LinkedList<MyTask> myTasks = new LinkedList<>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM MyTask WHERE toDoDate > 0" , new String[]{});
        while (cursor.moveToNext()){
            myTasks.add(new MyTask(cursor.getInt(cursor.getColumnIndex("id")),
                    cursor.getInt(cursor.getColumnIndex("listID")),
                    cursor.getInt(cursor.getColumnIndex("isInMyDay")) > 0,
                    cursor.getInt(cursor.getColumnIndex("isImportant")) > 0,
                    cursor.getInt(cursor.getColumnIndex("isDone")) > 0,
                    cursor.getInt(cursor.getColumnIndex("createDate")),
                    cursor.getInt(cursor.getColumnIndex("toDoDate")),
                    cursor.getInt(cursor.getColumnIndex("remindDate")),
                    cursor.getInt(cursor.getColumnIndex("completeTime")),
                    cursor.getString(cursor.getColumnIndex("taskTitle")),
                    cursor.getString(cursor.getColumnIndex("taskExplanation"))));
        }
        return myTasks;
    }

    public int getUnbindedTasksNumber(){
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT id FROM MyTask WHERE listID = 0 AND isDone = 0" , new String[]{});
        return cursor.getCount();
    }
    public LinkedList<MyTask> getUnbindedTasks(){
        LinkedList<MyTask> myTasks = new LinkedList<>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM MyTask WHERE listID = 0" , new String[]{});
        while (cursor.moveToNext()){
            myTasks.add(new MyTask(cursor.getInt(cursor.getColumnIndex("id")),
                    cursor.getInt(cursor.getColumnIndex("listID")),
                    cursor.getInt(cursor.getColumnIndex("isInMyDay")) > 0,
                    cursor.getInt(cursor.getColumnIndex("isImportant")) > 0,
                    cursor.getInt(cursor.getColumnIndex("isDone")) > 0,
                    cursor.getInt(cursor.getColumnIndex("createDate")),
                    cursor.getInt(cursor.getColumnIndex("toDoDate")),
                    cursor.getInt(cursor.getColumnIndex("remindDate")),
                    cursor.getInt(cursor.getColumnIndex("completeTime")),
                    cursor.getString(cursor.getColumnIndex("taskTitle")),
                    cursor.getString(cursor.getColumnIndex("taskExplanation"))));
        }
        return myTasks;
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }


}
