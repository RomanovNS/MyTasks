package ru.nikitars.mytasks;

import android.app.DatePickerDialog;
import android.content.Context;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.content.ContextCompat;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import ru.nikitars.mytasks.db.DBHelper;
import ru.nikitars.mytasks.db.models.MyList;
import ru.nikitars.mytasks.db.models.MyTask;

public class TaskActivity2 extends AppCompatActivity {

    CoordinatorLayout mainLayout;
    Toolbar toolbar;
    Context context;
    int listID;

    int myDayListID;
    int importantID;
    int plannedID;
    int unbindedID;

    int taskID;
    String taskTitle;
    String taskExplanation;
    boolean isDone;
    boolean isImportant;
    boolean isInMyDay;
    int completeTime;
    int remindDate;
    int createDate;
    int toDoDate;

    RelativeLayout relativeLayout;
    ImageView taskDoneItemImage;
    TextView taskTitleTextView;
    ImageView taskImportantItemImage;

    LinearLayout taskToMyDay;
    ImageView taskToMyDayImageView;
    TextView taskToMyDayTextView;

    RelativeLayout taskToPlan;
    ImageView taskToPlanImageView;
    TextView taskToPlanTextView;
    ImageView taskToPlanIconCancel;

    RelativeLayout creationDateAndDelete;
    TextView creationDateAndDeleteTextView;
    ImageView creationDateAndDeleteImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task2);
        mainLayout = findViewById(R.id.taskActivityLayout2);
        context = this;

        relativeLayout = findViewById(R.id.relativeLayout);
        taskDoneItemImage = findViewById(R.id.taskDoneItemImage);
        taskTitleTextView = findViewById(R.id.taskTitleTextView);
        taskImportantItemImage = findViewById(R.id.taskImportantItemImage);

        taskToMyDay = findViewById(R.id.taskToMyDay);
        taskToMyDayImageView = findViewById(R.id.taskToMyDayImageView);
        taskToMyDayTextView = findViewById(R.id.taskToMyDayTextView);

        taskToPlan = findViewById(R.id.taskToPlan);
        taskToPlanImageView = findViewById(R.id.taskToPlanImageView);
        taskToPlanTextView = findViewById(R.id.taskToPlanTextView);
        taskToPlanIconCancel = findViewById(R.id.taskToPlanIconCancel);

        creationDateAndDelete = findViewById(R.id.creationDateAndDelete);
        creationDateAndDeleteTextView = findViewById(R.id.creationDateAndDeleteTextView);
        creationDateAndDeleteImageView = findViewById(R.id.creationDateAndDeleteImageView);

        DBHelper dbHelper = new DBHelper(this);
        myDayListID = dbHelper.getMyListID("MyDayList111");
        importantID = dbHelper.getMyListID("ImportantList111");
        plannedID = dbHelper.getMyListID("PlannedList111");
        unbindedID = dbHelper.getMyListID("TasksList111");

        Bundle bundle = getIntent().getExtras();
        listID = bundle.getInt("listID");
        taskID = bundle.getInt("taskID");
        taskTitle = bundle.getString("taskTitle");
        taskExplanation = bundle.getString("taskExplanation");
        isDone = bundle.getBoolean("isDone");
        isImportant = bundle.getBoolean("isImportant");
        isInMyDay = bundle.getBoolean("isInMyDay");
        completeTime = bundle.getInt("completeTime");
        remindDate = bundle.getInt("remindDate");
        createDate = bundle.getInt("createDate");
        toDoDate = bundle.getInt("toDoDate");

        MyList list = dbHelper.getMyList(listID);
        String listTitle = list.getListName();
        if (listTitle.equals("MyDayList111") || listID == myDayListID) {
            listTitle = "Мой день";
        }
        else if (listTitle.equals("ImportantList111") || listID == importantID) {
            listTitle = "Важное";
        }
        else if (listTitle.equals("PlannedList111") || listID == plannedID) {
            listTitle = "Запланированно";
        }
        else if (listTitle.equals("TasksList111") || listID == unbindedID) {
            listTitle = "Задачи";
        }

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(listTitle);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(s -> {
            finish();
        });

        if (isDone){
            taskDoneItemImage.setImageResource(R.drawable.done_icon);
        }
        else{
            taskDoneItemImage.setImageResource(R.drawable.circle_icon);
        }
        if (isImportant){
            taskImportantItemImage.setImageResource(R.drawable.important_choosed_icon);
        }
        else{
            taskImportantItemImage.setImageResource(R.drawable.important_icon);
        }
        taskDoneItemImage.setColorFilter(ContextCompat.getColor(context, R.color.gray), android.graphics.PorterDuff.Mode.MULTIPLY);
        taskTitleTextView.setText(taskTitle);
        taskImportantItemImage.setColorFilter(ContextCompat.getColor(context, R.color.gray), android.graphics.PorterDuff.Mode.MULTIPLY);

        if (isInMyDay){
            taskToMyDayImageView.setColorFilter(ContextCompat.getColor(context, R.color.royalBlue), android.graphics.PorterDuff.Mode.MULTIPLY);
            taskToMyDayTextView.setText("Добавлено в Мой день");
            taskToMyDayTextView.setTextColor(ContextCompat.getColor(context, R.color.royalBlue));
        }
        else {
            taskToMyDayImageView.setColorFilter(ContextCompat.getColor(context, R.color.gray), android.graphics.PorterDuff.Mode.MULTIPLY);
            taskToMyDayTextView.setText("Добавить в Мой день");
            taskToMyDayTextView.setTextColor(ContextCompat.getColor(context, R.color.gray));
        }

        if (toDoDate > 0){
            Date date = new Date();
            date.setTime((long)(toDoDate)*1000);
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            int day = calendar.get(Calendar.DAY_OF_MONTH);
            int month = calendar.get(Calendar.MONTH);
            int year = calendar.get(Calendar.YEAR);
            taskToPlanTextView.setText("Выполнить к " + ((day < 10)?"0"+day:day) + "." + ((month < 10)?"0"+month:month) + "." + year);
            taskToPlanImageView.setColorFilter(ContextCompat.getColor(context, R.color.royalBlue), android.graphics.PorterDuff.Mode.MULTIPLY);
            taskToPlanIconCancel.setColorFilter(ContextCompat.getColor(context, R.color.royalBlue), android.graphics.PorterDuff.Mode.MULTIPLY);
            taskToPlanTextView.setTextColor(ContextCompat.getColor(context, R.color.royalBlue));
        }
        else{
            taskToPlanTextView.setText("Задать дату выполнения");
            taskToPlanImageView.setColorFilter(ContextCompat.getColor(this, R.color.gray), android.graphics.PorterDuff.Mode.MULTIPLY);
            taskToPlanIconCancel.setColorFilter(0, android.graphics.PorterDuff.Mode.MULTIPLY);
            taskToPlanTextView.setTextColor(ContextCompat.getColor(this, R.color.gray));
        }

        if (completeTime > 0){
            Date date = new Date();
            date.setTime((long)(completeTime)*1000);
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            int day = calendar.get(Calendar.DAY_OF_MONTH);
            int month = calendar.get(Calendar.MONTH);
            int year = calendar.get(Calendar.YEAR);
            creationDateAndDeleteTextView.setText("Выполнено " + ((day < 10)?"0"+day:day) + "." + ((month < 10)?"0"+month:month) + "." + year);
        }
        else {
            Date date = new Date();
            date.setTime((long)(createDate)*1000);
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            int day = calendar.get(Calendar.DAY_OF_MONTH);
            int month = calendar.get(Calendar.MONTH);
            int year = calendar.get(Calendar.YEAR);
            creationDateAndDeleteTextView.setText("Создано " + ((day < 10)?"0"+day:day) + "." + ((month < 10)?"0"+month:month) + "." + year);
        }
        creationDateAndDeleteTextView.setTextColor(ContextCompat.getColor(this, R.color.gray));
        creationDateAndDeleteImageView.setColorFilter(ContextCompat.getColor(this, R.color.gray), android.graphics.PorterDuff.Mode.MULTIPLY);

        relativeLayout.setOnClickListener(s -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Изменить задачу")
                    .setView(R.layout.dialog_rename_task)
                    .setPositiveButton("ИЗМЕНИТЬ", null)
                    .setNegativeButton("ОТМЕНА", null);
            builder.setCancelable(false);
            AlertDialog dialog = builder.create();
            dialog.show();
            Button positiveButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
            Button negativeButton = dialog.getButton(AlertDialog.BUTTON_NEGATIVE);
            AutoCompleteTextView input = dialog.findViewById(R.id.textInputRenameTask);
            positiveButton.setTextColor(ContextCompat.getColor(this, R.color.gray));
            negativeButton.setTextColor(ContextCompat.getColor(this, R.color.gray));
            input.setBackgroundTintList(ContextCompat.getColorStateList(this, R.color.gray));
            input.setText(taskTitle);
            positiveButton.setOnClickListener(ss -> {
                String newTaskTitle = input.getText().toString();
                taskTitleTextView.setText(newTaskTitle);
                dbHelper.setMyTaskTitle(taskID, newTaskTitle);
                dialog.dismiss();
            });
            negativeButton.setOnClickListener(ss -> {
                dialog.dismiss();
            });
        });
        taskDoneItemImage.setOnClickListener(s -> {
            if (isDone){
                isDone = false;
                taskDoneItemImage.setImageResource(R.drawable.circle_icon);
                completeTime = 0;
                Date date = new Date();
                date.setTime((long)(createDate)*1000);
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(date);
                int day = calendar.get(Calendar.DAY_OF_MONTH);
                int month = calendar.get(Calendar.MONTH);
                int year = calendar.get(Calendar.YEAR);
                creationDateAndDeleteTextView.setText("Создано " + ((day < 10)?"0"+day:day) + "." + ((month < 10)?"0"+month:month) + "." + year);
            }
            else{
                isDone = true;
                taskDoneItemImage.setImageResource(R.drawable.done_icon);
                long timestamp = new Date().getTime();
                completeTime = (int)(timestamp/1000);
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(new Date());
                int day = calendar.get(Calendar.DAY_OF_MONTH);
                int month = calendar.get(Calendar.MONTH);
                int year = calendar.get(Calendar.YEAR);
                creationDateAndDeleteTextView.setText("Выполнено " + ((day < 10)?"0"+day:day) + "." + ((month < 10)?"0"+month:month) + "." + year);
            }
            dbHelper.setMyTaskDone(taskID, isDone);
            dbHelper.setMyTaskCompleteTime(taskID, completeTime);
        });
        taskImportantItemImage.setOnClickListener(s -> {
            if (isImportant){
                isImportant = false;
                taskImportantItemImage.setImageResource(R.drawable.important_icon);
            }
            else{
                isImportant = true;
                taskImportantItemImage.setImageResource(R.drawable.important_choosed_icon);
            }
            dbHelper.setMyTaskImportant(taskID, isImportant);
        });

        taskToMyDay.setOnClickListener(s -> {
            if (isInMyDay){
                isInMyDay = false;
                taskToMyDayImageView.setColorFilter(ContextCompat.getColor(this, R.color.gray), android.graphics.PorterDuff.Mode.MULTIPLY);
                taskToMyDayTextView.setTextColor(ContextCompat.getColor(this, R.color.gray));
                taskToMyDayTextView.setText("Добавить в Мой день");
            }
            else{
                isInMyDay = true;
                taskToMyDayImageView.setColorFilter(ContextCompat.getColor(this, R.color.royalBlue), android.graphics.PorterDuff.Mode.MULTIPLY);
                taskToMyDayTextView.setTextColor(ContextCompat.getColor(this, R.color.royalBlue));
                taskToMyDayTextView.setText("Добавелено в Мой день");
            }
            dbHelper.setMyTaskInMyDay(taskID, isInMyDay);
        });

        taskToPlan.setOnClickListener(s ->{
            final Calendar cal = Calendar.getInstance();
            DatePickerDialog datePickerDialog = new DatePickerDialog(context, R.style.datePickerStyle,
                    new DatePickerDialog.OnDateSetListener() {
                        @Override
                        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                            Date date = new Date();
                            monthOfYear++;
                            try {
                                date = new SimpleDateFormat("dd-MM-yyyy").parse(((dayOfMonth < 10)?"0"+dayOfMonth:dayOfMonth) + "-" + ((monthOfYear < 10)?"0"+monthOfYear:monthOfYear) + "-" + year);
                            } catch (ParseException e) {
                                e.printStackTrace();
                                date = new Date();
                            }
                            toDoDate = ((int)(date.getTime() / 1000));
                            taskToPlanTextView.setText("Выполнить к " + ((dayOfMonth < 10)?"0"+dayOfMonth:dayOfMonth) + "." + ((monthOfYear < 10)?"0"+monthOfYear:monthOfYear) + "." + year);
                            taskToPlanImageView.setColorFilter(ContextCompat.getColor(context, R.color.royalBlue), android.graphics.PorterDuff.Mode.MULTIPLY);
                            taskToPlanIconCancel.setColorFilter(ContextCompat.getColor(context, R.color.royalBlue), android.graphics.PorterDuff.Mode.MULTIPLY);
                            taskToPlanTextView.setTextColor(ContextCompat.getColor(context, R.color.royalBlue));
                            dbHelper.setMyTaskToDoDate(taskID, toDoDate);
                        }
                    },
                    cal.get(Calendar.YEAR),
                    cal.get(Calendar.MONTH),
                    cal.get(Calendar.DAY_OF_MONTH));
            datePickerDialog.show();
        });
        taskToPlanIconCancel.setOnClickListener(s -> {
            toDoDate = 0;
            taskToPlanTextView.setText("Задать дату выполнения");
            taskToPlanImageView.setColorFilter(ContextCompat.getColor(this, R.color.gray), android.graphics.PorterDuff.Mode.MULTIPLY);
            taskToPlanIconCancel.setColorFilter(0, android.graphics.PorterDuff.Mode.MULTIPLY);
            taskToPlanTextView.setTextColor(ContextCompat.getColor(this, R.color.gray));
            dbHelper.setMyTaskToDoDate(taskID, toDoDate);
        });

        creationDateAndDeleteImageView.setOnClickListener(s -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Удалить задачу")
                    .setMessage("Вы уверены?")
                    .setPositiveButton("УДАЛИТЬ", null)
                    .setNegativeButton("ОТМЕНА", null);
            builder.setCancelable(false);
            AlertDialog dialog = builder.create();
            dialog.show();
            Button positiveButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
            Button negativeButton = dialog.getButton(AlertDialog.BUTTON_NEGATIVE);
            positiveButton.setTextColor(ContextCompat.getColor(this, R.color.red));
            negativeButton.setTextColor(ContextCompat.getColor(this, R.color.gray));
            positiveButton.setOnClickListener(ss -> {
                dbHelper.deleteMyTask(taskID);
                finish();
            });
            negativeButton.setOnClickListener(ss -> {
                dialog.dismiss();
            });
        });


    }
}