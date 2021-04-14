package ru.nikitars.mytasks;

import android.app.DatePickerDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import ru.nikitars.mytasks.adapters.ListOfTasksAdapter;
import ru.nikitars.mytasks.db.DBHelper;
import ru.nikitars.mytasks.db.models.MyTask;

public class ListActivity extends AppCompatActivity {

    LinkedList<MyTask> tasks;
    CoordinatorLayout mainLayout;
    Toolbar toolbar;
    FloatingActionButton fab;
    int listID;
    int themeType;
    int themeID;
    String listTitle;
    String listOrder;

    int myDayListID;
    int importantID;
    int plannedID;
    int tasksID;

    RecyclerView listOfTasksRV;
    private ListOfTasksAdapter listOfTasksAdapter;

    LinkedList<MenuItem> menuItems;

    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createNewTask();
            }
        });

        menuItems = new LinkedList<MenuItem>();

        DBHelper dbHelper = new DBHelper(this);
        myDayListID = dbHelper.getMyListID("MyDayList111");
        importantID = dbHelper.getMyListID("ImportantList111");
        plannedID = dbHelper.getMyListID("PlannedList111");
        tasksID = dbHelper.getMyListID("TasksList111");

        Bundle bundle = getIntent().getExtras();
        listID = bundle.getInt("id");
        themeType = (listID>0)?bundle.getInt("themeType"):0;
        themeID = (listID>0)?bundle.getInt("themeID"):6;
        listTitle = (listID>0)?bundle.getString("listName"):"Новый список";
        listOrder = (listID>0)?bundle.getString("listOrder"):"";

        context = this;

        mainLayout = findViewById(R.id.listActivityLayout);
        initRecyclerView();
        enableSwipeToDeleteAndUndo();
    }

    public void setThemeAndTitle(){
        mainLayout.setBackgroundColor(ContextCompat.getColor(this, ColorThemeChooseHelper.getColorIDBackground(themeType,themeID)));
        toolbar.getNavigationIcon().setTint(ContextCompat.getColor(this, ColorThemeChooseHelper.getColorIDFont(themeType,themeID)));
        toolbar.setBackgroundColor(ContextCompat.getColor(this, ColorThemeChooseHelper.getColorIDBackground(themeType,themeID)));
        toolbar.setTitleTextColor(ContextCompat.getColor(this, ColorThemeChooseHelper.getColorIDFont(themeType,themeID)));
        //toolbar.getCollapseIcon().setTint(ContextCompat.getColor(this, ColorThemeChooseHelper.getColorIDFont(themeType,themeID)));

        getWindow().setStatusBarColor(ContextCompat.getColor(this, ColorThemeChooseHelper.getColorIDBackground(themeType,themeID)));
        getWindow().getDecorView().setSystemUiVisibility(0);

        Drawable drawable = fab.getContentBackground();
        drawable = DrawableCompat.wrap(drawable);
        DrawableCompat.setTint(drawable, ContextCompat.getColor(this, ColorThemeChooseHelper.getColorIDAddButtonColor(themeType,themeID)));
        fab.setBackground(drawable);
        fab.setColorFilter(ContextCompat.getColor(this, ColorThemeChooseHelper.getColorIDAddButtonInnerPlusColor(themeType,themeID))); // цвет крестика

        DBHelper dbHelper = new DBHelper(this);
        if (listID > 0){
            if (listTitle.equals("MyDayList111") || listID == myDayListID) {
                listTitle = "Мой день";
                tasks = dbHelper.getMyDayTasks();
            }
            else if (listTitle.equals("ImportantList111") || listID == importantID) {
                listTitle = "Важное";
                tasks = dbHelper.getImportantTasks();
            }
            else if (listTitle.equals("PlannedList111") || listID == plannedID) {
                listTitle = "Запланированно";
                tasks = dbHelper.getPlannedTasks();
            }
            else if (listTitle.equals("TasksList111") || listID == tasksID) {
                listTitle = "Задачи";
                //tasks = new LinkedList<>();
                tasks = dbHelper.getUnbindedTasks();
            }
            else {
                tasks = dbHelper.getMyTaskList(listID);
            }
        }
        else{
            tasks = new LinkedList<MyTask>();
        }
        toolbar.setTitle(listTitle);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(s -> {
            finish();
        });

        listOfTasksAdapter.clearItems();
        listOfTasksAdapter.setItems(tasks);
    }

    private void initRecyclerView() {
        listOfTasksRV = findViewById(R.id.listOfTasks);
        listOfTasksRV.setLayoutManager(new LinearLayoutManager((this)));
        listOfTasksAdapter = new ListOfTasksAdapter();
        listOfTasksAdapter.setListID(listID);
        listOfTasksRV.setAdapter(listOfTasksAdapter);
    }

    @Override
    public void onResume() {
        super.onResume();
        setThemeAndTitle();
        if (listID < 0)
            createNewList();
    }

    private void createNewList(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Новый список")
                .setView(R.layout.dialog_new_list)
                .setPositiveButton("СОЗДАТЬ СПИСОК", null)
                .setNegativeButton("ОТМЕНА", null);
        builder.setCancelable(false);
        AlertDialog dialog = builder.create();
        dialog.show();
        Button positiveButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
        Button negativeButton = dialog.getButton(AlertDialog.BUTTON_NEGATIVE);
        AutoCompleteTextView input = dialog.findViewById(R.id.textInputNewListName);

        positiveButton.setTextColor(ContextCompat.getColor(this, R.color.gray));
        positiveButton.setEnabled(false);
        negativeButton.setTextColor(ContextCompat.getColor(this, R.color.black));
        input.requestFocus();
        input.setBackgroundTintList(ContextCompat.getColorStateList(this, R.color.black));
        input.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() > 0){
                    positiveButton.setTextColor(ContextCompat.getColor(context, R.color.black));
                    positiveButton.setEnabled(true);
                }
                else {
                    positiveButton.setTextColor(ContextCompat.getColor(context, R.color.gray));
                    positiveButton.setEnabled(false);
                }
            }
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }
            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        DBHelper dbHelper = new DBHelper(this);
        positiveButton.setOnClickListener(s -> {
            listTitle = input.getText().toString();
            toolbar.setTitle(listTitle);
            setSupportActionBar(toolbar);
            toolbar.setNavigationOnClickListener(ss -> { finish(); });
            listID = dbHelper.createNewList(listTitle);
            listOfTasksAdapter.setListID(listID);
            dialog.dismiss();
        });
        negativeButton.setOnClickListener(s -> {
            finish();
        });
    }

    private void createNewTask(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Добавить задачу")
                .setView(R.layout.dialog_new_task)
                .setPositiveButton("ДОБАВИТЬ", null)
                .setNegativeButton("ОТМЕНА", null);
        builder.setCancelable(false);
        AlertDialog dialog = builder.create();
        dialog.show();
        Button positiveButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
        Button negativeButton = dialog.getButton(AlertDialog.BUTTON_NEGATIVE);
        AutoCompleteTextView input = dialog.findViewById(R.id.textInputNewTask);
        RelativeLayout planBtn = dialog.findViewById(R.id.makePlanInCreationLayout);
        //RelativeLayout remindBtn = dialog.findViewById(R.id.makeReminderInCreationLayout);
        ImageView planIcon = dialog.findViewById(R.id.makePlanInCreationIcon);
        //ImageView remindIcon = dialog.findViewById(R.id.makeReminderInCreationIcon);
        ImageView cancelPlanIcon = dialog.findViewById(R.id.makePlanInCreationIconCancel);
        //ImageView cancelReminderIcon = dialog.findViewById(R.id.makeReminderInCreationIconCancel);
        TextView planText = dialog.findViewById(R.id.makePlanInCreationText);
        //TextView remindText = dialog.findViewById(R.id.makeReminderInCreationText);

        //planIcon.setColorFilter(ContextCompat.getColor(this, ColorThemeChooseHelper.getColorIDForListOfLists(themeType,themeID)), android.graphics.PorterDuff.Mode.MULTIPLY);
        //remindIcon.setColorFilter(ContextCompat.getColor(this, ColorThemeChooseHelper.getColorIDForListOfLists(themeType,themeID)), android.graphics.PorterDuff.Mode.MULTIPLY);
        //cancelPlanIcon.setColorFilter(ContextCompat.getColor(this, ColorThemeChooseHelper.getColorIDForListOfLists(themeType,themeID)), android.graphics.PorterDuff.Mode.MULTIPLY);
        //cancelReminderIcon.setColorFilter(ContextCompat.getColor(this, ColorThemeChooseHelper.getColorIDForListOfLists(themeType,themeID)), android.graphics.PorterDuff.Mode.MULTIPLY);
        //planText.setTextColor(ContextCompat.getColor(this, ColorThemeChooseHelper.getColorIDForListOfLists(themeType,themeID)));
        //remindText.setTextColor(ContextCompat.getColor(this, ColorThemeChooseHelper.getColorIDForListOfLists(themeType,themeID)));

        planIcon.setColorFilter(ContextCompat.getColor(this, R.color.gray), android.graphics.PorterDuff.Mode.MULTIPLY);
        //remindIcon.setColorFilter(ContextCompat.getColor(this, R.color.gray), android.graphics.PorterDuff.Mode.MULTIPLY);
        cancelPlanIcon.setColorFilter(0, android.graphics.PorterDuff.Mode.MULTIPLY);
        //cancelReminderIcon.setColorFilter(0, android.graphics.PorterDuff.Mode.MULTIPLY);
        planText.setTextColor(ContextCompat.getColor(this, R.color.gray));
        //remindText.setTextColor(ContextCompat.getColor(this, R.color.gray));

        AtomicInteger toDoDate = new AtomicInteger();
        AtomicInteger remindTime = new AtomicInteger();
        toDoDate.set(0);
        remindTime.set(0);

        final Calendar cal = Calendar.getInstance();
        planBtn.setOnClickListener(s -> {

            DatePickerDialog datePickerDialog = new DatePickerDialog(dialog.getContext(), R.style.datePickerStyle,
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
                            toDoDate.set((int)(date.getTime() / 1000));
                            planText.setText("Выполнить к " + ((dayOfMonth < 10)?"0"+dayOfMonth:dayOfMonth) + "." + ((monthOfYear < 10)?"0"+monthOfYear:monthOfYear) + "." + year);
                            planIcon.setColorFilter(ContextCompat.getColor(context, ColorThemeChooseHelper.getColorIDForListOfLists(themeType,themeID)), android.graphics.PorterDuff.Mode.MULTIPLY);
                            cancelPlanIcon.setColorFilter(ContextCompat.getColor(context, ColorThemeChooseHelper.getColorIDForListOfLists(themeType,themeID)), android.graphics.PorterDuff.Mode.MULTIPLY);
                            planText.setTextColor(ContextCompat.getColor(context, ColorThemeChooseHelper.getColorIDForListOfLists(themeType,themeID)));
                        }
                    },
                    cal.get(Calendar.YEAR),
                    cal.get(Calendar.MONTH),
                    cal.get(Calendar.DAY_OF_MONTH));
            datePickerDialog.show();
            //datePickerDialog.getDatePicker().color(ContextCompat.getColor(context, ColorThemeChooseHelper.getColorIDForListOfLists(themeType,themeID)));
        });
        //remindBtn.setOnClickListener(s -> {
        //});
        cancelPlanIcon.setOnClickListener(s -> {
            toDoDate.set(0);
            planText.setText("Задать дату выполнения");
            planIcon.setColorFilter(ContextCompat.getColor(this, R.color.gray), android.graphics.PorterDuff.Mode.MULTIPLY);
            cancelPlanIcon.setColorFilter(0, android.graphics.PorterDuff.Mode.MULTIPLY);
            planText.setTextColor(ContextCompat.getColor(this, R.color.gray));
        });
        //cancelReminderIcon.setOnClickListener(s -> {
        //    remindTime.set(0);
        //    remindText.setText("Напомнить");
        //    remindIcon.setColorFilter(ContextCompat.getColor(this, R.color.gray), android.graphics.PorterDuff.Mode.MULTIPLY);
        //    cancelReminderIcon.setColorFilter(0, android.graphics.PorterDuff.Mode.MULTIPLY);
        //    remindText.setTextColor(ContextCompat.getColor(this, R.color.gray));
        //});

        positiveButton.setTextColor(ContextCompat.getColor(this, R.color.gray));
        positiveButton.setEnabled(false);
        negativeButton.setTextColor(ContextCompat.getColor(this, ColorThemeChooseHelper.getColorIDForListOfLists(themeType,themeID)));
        input.requestFocus();
        input.setBackgroundTintList(ContextCompat.getColorStateList(this, ColorThemeChooseHelper.getColorIDForListOfLists(themeType,themeID)));

        input.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() > 0){
                    positiveButton.setTextColor(ContextCompat.getColor(context, ColorThemeChooseHelper.getColorIDForListOfLists(themeType,themeID)));
                    positiveButton.setEnabled(true);
                }
                else {
                    positiveButton.setTextColor(ContextCompat.getColor(context, R.color.gray));
                    positiveButton.setEnabled(false);
                }
            }
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }
            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        DBHelper dbHelper = new DBHelper(this);
        positiveButton.setOnClickListener(s -> {
            int listIDtemp = listID;
            if (listID == myDayListID || listID == importantID || listID == plannedID || listID == tasksID)
                listIDtemp = 0;
            boolean isInMyDay = listID == myDayListID;
            boolean isImportant = listID == importantID;

            String taskTitle = input.getText().toString();
            int taskID = dbHelper.createMyTask(listIDtemp, isInMyDay, isImportant, toDoDate.get(), 0, 0, taskTitle);
            if (taskID > 0){
                MyTask myTask = dbHelper.getMyTask(taskID);
                tasks.add(myTask);
            }
            setThemeAndTitle();
            dialog.dismiss();
        });
        negativeButton.setOnClickListener(s -> {
            dialog.dismiss();
        });
    }

    private void enableSwipeToDeleteAndUndo() {
        SwipeToDeleteCallback swipeToDeleteCallback = new SwipeToDeleteCallback(this) {
            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int i) {

                DBHelper dbHelper = new DBHelper(context);
                final int position = viewHolder.getAdapterPosition();
                final MyTask item = listOfTasksAdapter.getData().get(position);

                listOfTasksAdapter.removeItem(position);
                dbHelper.deleteMyTask(item.getId());

                Snackbar snackbar = Snackbar.make(mainLayout, "Задача была удалена из списка", Snackbar.LENGTH_LONG);
                snackbar.setAction("Вернуть", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dbHelper.restoreMyTask(item);
                        listOfTasksAdapter.restoreItem(item, position);
                        listOfTasksRV.scrollToPosition(position);
                    }
                });
                snackbar.setActionTextColor(Color.YELLOW);
                snackbar.show();
            }
        };

        ItemTouchHelper itemTouchhelper = new ItemTouchHelper(swipeToDeleteCallback);
        itemTouchhelper.attachToRecyclerView(listOfTasksRV);
    }

    private void renameList(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Переименовать список")
                .setView(R.layout.dialog_new_list)
                .setPositiveButton("ПЕРЕИМЕНОВАТЬ", null)
                .setNegativeButton("ОТМЕНА", null);
        builder.setCancelable(false);
        AlertDialog dialog = builder.create();
        dialog.show();
        Button positiveButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
        Button negativeButton = dialog.getButton(AlertDialog.BUTTON_NEGATIVE);
        AutoCompleteTextView input = dialog.findViewById(R.id.textInputNewListName);

        positiveButton.setTextColor(ContextCompat.getColor(this, R.color.gray));
        positiveButton.setEnabled(false);
        negativeButton.setTextColor(ContextCompat.getColor(this, R.color.black));
        input.setText(listTitle);
        input.setBackgroundTintList(ContextCompat.getColorStateList(this, R.color.black));
        input.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() > 0){
                    positiveButton.setTextColor(ContextCompat.getColor(context, R.color.black));
                    positiveButton.setEnabled(true);
                }
                else {
                    positiveButton.setTextColor(ContextCompat.getColor(context, R.color.gray));
                    positiveButton.setEnabled(false);
                }
            }
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }
            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        DBHelper dbHelper = new DBHelper(this);
        positiveButton.setOnClickListener(s -> {
            listTitle = input.getText().toString();
            toolbar.setTitle(listTitle);
            setSupportActionBar(toolbar);
            toolbar.setNavigationOnClickListener(ss -> { finish(); });
            dbHelper.renameList(listID, listTitle);
            dialog.dismiss();
        });
        negativeButton.setOnClickListener(s -> {
            dialog.dismiss();
        });
    }

    private void deleteList(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Удаление")
                .setMessage("Удалить список со всеми его задачами?")
                .setPositiveButton("УДАЛИТЬ", null)
                .setNegativeButton("ОТМЕНА", null);
        builder.setCancelable(false);
        AlertDialog dialog = builder.create();
        dialog.show();
        Button positiveButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
        Button negativeButton = dialog.getButton(AlertDialog.BUTTON_NEGATIVE);
        positiveButton.setTextColor(ContextCompat.getColor(this, R.color.red));
        negativeButton.setTextColor(ContextCompat.getColor(this, R.color.black));
        DBHelper dbHelper = new DBHelper(this);
        positiveButton.setOnClickListener(s -> {
            for (MyTask task : tasks){
                dbHelper.deleteMyTask(task.getId());
            }
            dbHelper.deleteMyList(listID);
            finish();
        });
        negativeButton.setOnClickListener(s -> {
            dialog.dismiss();
        });
    }

    private void changeTheme(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Выбор темы")
                .setView(R.layout.dialog_change_theme)
                .setPositiveButton("ВЫБРАТЬ", null)
                .setNegativeButton("ОТМЕНА", null);
        builder.setCancelable(false);
        AlertDialog dialog = builder.create();
        dialog.show();
        Button positiveButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
        Button negativeButton = dialog.getButton(AlertDialog.BUTTON_NEGATIVE);


        positiveButton.setTextColor(ContextCompat.getColor(this, ColorThemeChooseHelper.getColorIDForListOfLists(themeType,themeID)));
        negativeButton.setTextColor(ContextCompat.getColor(this, ColorThemeChooseHelper.getColorIDForListOfLists(themeType,themeID)));
        FloatingActionButton f1 = dialog.findViewById(R.id.theme1);
        FloatingActionButton f2 = dialog.findViewById(R.id.theme2);
        FloatingActionButton f3 = dialog.findViewById(R.id.theme3);
        FloatingActionButton f4 = dialog.findViewById(R.id.theme4);
        FloatingActionButton f5 = dialog.findViewById(R.id.theme5);
        FloatingActionButton f6 = dialog.findViewById(R.id.theme6);
        FloatingActionButton f7 = dialog.findViewById(R.id.theme7);
        FloatingActionButton f8 = dialog.findViewById(R.id.theme8);
        FloatingActionButton f9 = dialog.findViewById(R.id.theme9);
        FloatingActionButton f10 = dialog.findViewById(R.id.theme10);
        FloatingActionButton f11 = dialog.findViewById(R.id.theme11);
        FloatingActionButton f12 = dialog.findViewById(R.id.theme12);
        FloatingActionButton f13 = dialog.findViewById(R.id.theme13);
        FloatingActionButton f14 = dialog.findViewById(R.id.theme14);
        FloatingActionButton f15 = dialog.findViewById(R.id.theme15);
        FloatingActionButton f16 = dialog.findViewById(R.id.theme16);
        FloatingActionButton f17 = dialog.findViewById(R.id.theme17);
        FloatingActionButton f18 = dialog.findViewById(R.id.theme18);
        FloatingActionButton f19 = dialog.findViewById(R.id.theme19);
        FloatingActionButton f20 = dialog.findViewById(R.id.theme20);
        FloatingActionButton f21 = dialog.findViewById(R.id.theme21);
        FloatingActionButton f22 = dialog.findViewById(R.id.theme22);
        FloatingActionButton f23 = dialog.findViewById(R.id.theme23);
        FloatingActionButton f24 = dialog.findViewById(R.id.theme24);
        FloatingActionButton f25 = dialog.findViewById(R.id.theme25);
        FloatingActionButton f26 = dialog.findViewById(R.id.theme26);
        FloatingActionButton f27 = dialog.findViewById(R.id.theme27);
        FloatingActionButton f28 = dialog.findViewById(R.id.theme28);
        FloatingActionButton f29 = dialog.findViewById(R.id.theme29);
        FloatingActionButton f30 = dialog.findViewById(R.id.theme30);
        FloatingActionButton f31 = dialog.findViewById(R.id.theme31);
        FloatingActionButton f32 = dialog.findViewById(R.id.theme32);

        int oldTheme = themeID;
        DBHelper dbHelper = new DBHelper(context);
        f1.setOnClickListener(s ->{
            themeID = 0;
            dbHelper.changeListTheme(listID, themeType, themeID);
            setThemeAndTitle();
            positiveButton.setTextColor(ContextCompat.getColor(context, ColorThemeChooseHelper.getColorIDForListOfLists(themeType,themeID)));
            negativeButton.setTextColor(ContextCompat.getColor(context, ColorThemeChooseHelper.getColorIDForListOfLists(themeType,themeID)));
        });
        f2.setOnClickListener(s ->{
            themeID = 1;
            dbHelper.changeListTheme(listID, themeType, themeID);
            setThemeAndTitle();
            positiveButton.setTextColor(ContextCompat.getColor(context, ColorThemeChooseHelper.getColorIDForListOfLists(themeType,themeID)));
            negativeButton.setTextColor(ContextCompat.getColor(context, ColorThemeChooseHelper.getColorIDForListOfLists(themeType,themeID)));
        });
        f3.setOnClickListener(s ->{
            themeID = 2;
            dbHelper.changeListTheme(listID, themeType, themeID);
            setThemeAndTitle();
            positiveButton.setTextColor(ContextCompat.getColor(context, ColorThemeChooseHelper.getColorIDForListOfLists(themeType,themeID)));
            negativeButton.setTextColor(ContextCompat.getColor(context, ColorThemeChooseHelper.getColorIDForListOfLists(themeType,themeID)));
        });
        f4.setOnClickListener(s ->{
            themeID = 3;
            dbHelper.changeListTheme(listID, themeType, themeID);
            setThemeAndTitle();
            positiveButton.setTextColor(ContextCompat.getColor(context, ColorThemeChooseHelper.getColorIDForListOfLists(themeType,themeID)));
            negativeButton.setTextColor(ContextCompat.getColor(context, ColorThemeChooseHelper.getColorIDForListOfLists(themeType,themeID)));
        });
        f5.setOnClickListener(s ->{
            themeID = 4;
            dbHelper.changeListTheme(listID, themeType, themeID);
            setThemeAndTitle();
            positiveButton.setTextColor(ContextCompat.getColor(context, ColorThemeChooseHelper.getColorIDForListOfLists(themeType,themeID)));
            negativeButton.setTextColor(ContextCompat.getColor(context, ColorThemeChooseHelper.getColorIDForListOfLists(themeType,themeID)));
        });
        f6.setOnClickListener(s ->{
            themeID = 5;
            dbHelper.changeListTheme(listID, themeType, themeID);
            setThemeAndTitle();
            positiveButton.setTextColor(ContextCompat.getColor(context, ColorThemeChooseHelper.getColorIDForListOfLists(themeType,themeID)));
            negativeButton.setTextColor(ContextCompat.getColor(context, ColorThemeChooseHelper.getColorIDForListOfLists(themeType,themeID)));
        });
        f7.setOnClickListener(s ->{
            themeID = 6;
            dbHelper.changeListTheme(listID, themeType, themeID);
            setThemeAndTitle();
            positiveButton.setTextColor(ContextCompat.getColor(context, ColorThemeChooseHelper.getColorIDForListOfLists(themeType,themeID)));
            negativeButton.setTextColor(ContextCompat.getColor(context, ColorThemeChooseHelper.getColorIDForListOfLists(themeType,themeID)));
        });
        f8.setOnClickListener(s ->{
            themeID = 7;
            dbHelper.changeListTheme(listID, themeType, themeID);
            setThemeAndTitle();
            positiveButton.setTextColor(ContextCompat.getColor(context, ColorThemeChooseHelper.getColorIDForListOfLists(themeType,themeID)));
            negativeButton.setTextColor(ContextCompat.getColor(context, ColorThemeChooseHelper.getColorIDForListOfLists(themeType,themeID)));
        });
        f9.setOnClickListener(s ->{
            themeID = 8;
            dbHelper.changeListTheme(listID, themeType, themeID);
            setThemeAndTitle();
            positiveButton.setTextColor(ContextCompat.getColor(context, ColorThemeChooseHelper.getColorIDForListOfLists(themeType,themeID)));
            negativeButton.setTextColor(ContextCompat.getColor(context, ColorThemeChooseHelper.getColorIDForListOfLists(themeType,themeID)));
        });
        f10.setOnClickListener(s ->{
            themeID = 9;
            dbHelper.changeListTheme(listID, themeType, themeID);
            setThemeAndTitle();
            positiveButton.setTextColor(ContextCompat.getColor(context, ColorThemeChooseHelper.getColorIDForListOfLists(themeType,themeID)));
            negativeButton.setTextColor(ContextCompat.getColor(context, ColorThemeChooseHelper.getColorIDForListOfLists(themeType,themeID)));
        });
        f11.setOnClickListener(s ->{
            themeID = 10;
            dbHelper.changeListTheme(listID, themeType, themeID);
            setThemeAndTitle();
            positiveButton.setTextColor(ContextCompat.getColor(context, ColorThemeChooseHelper.getColorIDForListOfLists(themeType,themeID)));
            negativeButton.setTextColor(ContextCompat.getColor(context, ColorThemeChooseHelper.getColorIDForListOfLists(themeType,themeID)));
        });
        f12.setOnClickListener(s ->{
            themeID = 11;
            dbHelper.changeListTheme(listID, themeType, themeID);
            setThemeAndTitle();
            positiveButton.setTextColor(ContextCompat.getColor(context, ColorThemeChooseHelper.getColorIDForListOfLists(themeType,themeID)));
            negativeButton.setTextColor(ContextCompat.getColor(context, ColorThemeChooseHelper.getColorIDForListOfLists(themeType,themeID)));
        });
        f13.setOnClickListener(s ->{
            themeID = 12;
            dbHelper.changeListTheme(listID, themeType, themeID);
            setThemeAndTitle();
            positiveButton.setTextColor(ContextCompat.getColor(context, ColorThemeChooseHelper.getColorIDForListOfLists(themeType,themeID)));
            negativeButton.setTextColor(ContextCompat.getColor(context, ColorThemeChooseHelper.getColorIDForListOfLists(themeType,themeID)));
        });
        f14.setOnClickListener(s ->{
            themeID = 13;
            dbHelper.changeListTheme(listID, themeType, themeID);
            setThemeAndTitle();
            positiveButton.setTextColor(ContextCompat.getColor(context, ColorThemeChooseHelper.getColorIDForListOfLists(themeType,themeID)));
            negativeButton.setTextColor(ContextCompat.getColor(context, ColorThemeChooseHelper.getColorIDForListOfLists(themeType,themeID)));
        });
        f15.setOnClickListener(s ->{
            themeID = 14;
            dbHelper.changeListTheme(listID, themeType, themeID);
            setThemeAndTitle();
            positiveButton.setTextColor(ContextCompat.getColor(context, ColorThemeChooseHelper.getColorIDForListOfLists(themeType,themeID)));
            negativeButton.setTextColor(ContextCompat.getColor(context, ColorThemeChooseHelper.getColorIDForListOfLists(themeType,themeID)));
        });
        f16.setOnClickListener(s ->{
            themeID = 15;
            dbHelper.changeListTheme(listID, themeType, themeID);
            setThemeAndTitle();
            positiveButton.setTextColor(ContextCompat.getColor(context, ColorThemeChooseHelper.getColorIDForListOfLists(themeType,themeID)));
            negativeButton.setTextColor(ContextCompat.getColor(context, ColorThemeChooseHelper.getColorIDForListOfLists(themeType,themeID)));
        });
        f17.setOnClickListener(s ->{
            themeID = 16;
            dbHelper.changeListTheme(listID, themeType, themeID);
            setThemeAndTitle();
            positiveButton.setTextColor(ContextCompat.getColor(context, ColorThemeChooseHelper.getColorIDForListOfLists(themeType,themeID)));
            negativeButton.setTextColor(ContextCompat.getColor(context, ColorThemeChooseHelper.getColorIDForListOfLists(themeType,themeID)));
        });
        f18.setOnClickListener(s ->{
            themeID = 17;
            dbHelper.changeListTheme(listID, themeType, themeID);
            setThemeAndTitle();
            positiveButton.setTextColor(ContextCompat.getColor(context, ColorThemeChooseHelper.getColorIDForListOfLists(themeType,themeID)));
            negativeButton.setTextColor(ContextCompat.getColor(context, ColorThemeChooseHelper.getColorIDForListOfLists(themeType,themeID)));
        });
        f19.setOnClickListener(s ->{
            themeID = 18;
            dbHelper.changeListTheme(listID, themeType, themeID);
            setThemeAndTitle();
            positiveButton.setTextColor(ContextCompat.getColor(context, ColorThemeChooseHelper.getColorIDForListOfLists(themeType,themeID)));
            negativeButton.setTextColor(ContextCompat.getColor(context, ColorThemeChooseHelper.getColorIDForListOfLists(themeType,themeID)));
        });
        f20.setOnClickListener(s ->{
            themeID = 19;
            dbHelper.changeListTheme(listID, themeType, themeID);
            setThemeAndTitle();
            positiveButton.setTextColor(ContextCompat.getColor(context, ColorThemeChooseHelper.getColorIDForListOfLists(themeType,themeID)));
            negativeButton.setTextColor(ContextCompat.getColor(context, ColorThemeChooseHelper.getColorIDForListOfLists(themeType,themeID)));
        });
        f21.setOnClickListener(s ->{
            themeID = 20;
            dbHelper.changeListTheme(listID, themeType, themeID);
            setThemeAndTitle();
            positiveButton.setTextColor(ContextCompat.getColor(context, ColorThemeChooseHelper.getColorIDForListOfLists(themeType,themeID)));
            negativeButton.setTextColor(ContextCompat.getColor(context, ColorThemeChooseHelper.getColorIDForListOfLists(themeType,themeID)));
        });
        f22.setOnClickListener(s ->{
            themeID = 21;
            dbHelper.changeListTheme(listID, themeType, themeID);
            setThemeAndTitle();
            positiveButton.setTextColor(ContextCompat.getColor(context, ColorThemeChooseHelper.getColorIDForListOfLists(themeType,themeID)));
            negativeButton.setTextColor(ContextCompat.getColor(context, ColorThemeChooseHelper.getColorIDForListOfLists(themeType,themeID)));
        });
        f23.setOnClickListener(s ->{
            themeID = 22;
            dbHelper.changeListTheme(listID, themeType, themeID);
            setThemeAndTitle();
            positiveButton.setTextColor(ContextCompat.getColor(context, ColorThemeChooseHelper.getColorIDForListOfLists(themeType,themeID)));
            negativeButton.setTextColor(ContextCompat.getColor(context, ColorThemeChooseHelper.getColorIDForListOfLists(themeType,themeID)));
        });
        f24.setOnClickListener(s ->{
            themeID = 23;
            dbHelper.changeListTheme(listID, themeType, themeID);
            setThemeAndTitle();
            positiveButton.setTextColor(ContextCompat.getColor(context, ColorThemeChooseHelper.getColorIDForListOfLists(themeType,themeID)));
            negativeButton.setTextColor(ContextCompat.getColor(context, ColorThemeChooseHelper.getColorIDForListOfLists(themeType,themeID)));
        });
        f25.setOnClickListener(s ->{
            themeID = 24;
            dbHelper.changeListTheme(listID, themeType, themeID);
            setThemeAndTitle();
            positiveButton.setTextColor(ContextCompat.getColor(context, ColorThemeChooseHelper.getColorIDForListOfLists(themeType,themeID)));
            negativeButton.setTextColor(ContextCompat.getColor(context, ColorThemeChooseHelper.getColorIDForListOfLists(themeType,themeID)));
        });
        f26.setOnClickListener(s ->{
            themeID = 25;
            dbHelper.changeListTheme(listID, themeType, themeID);
            setThemeAndTitle();
            positiveButton.setTextColor(ContextCompat.getColor(context, ColorThemeChooseHelper.getColorIDForListOfLists(themeType,themeID)));
            negativeButton.setTextColor(ContextCompat.getColor(context, ColorThemeChooseHelper.getColorIDForListOfLists(themeType,themeID)));
        });
        f27.setOnClickListener(s ->{
            themeID = 26;
            dbHelper.changeListTheme(listID, themeType, themeID);
            setThemeAndTitle();
            positiveButton.setTextColor(ContextCompat.getColor(context, ColorThemeChooseHelper.getColorIDForListOfLists(themeType,themeID)));
            negativeButton.setTextColor(ContextCompat.getColor(context, ColorThemeChooseHelper.getColorIDForListOfLists(themeType,themeID)));
        });
        f28.setOnClickListener(s ->{
            themeID = 27;
            dbHelper.changeListTheme(listID, themeType, themeID);
            setThemeAndTitle();
            positiveButton.setTextColor(ContextCompat.getColor(context, ColorThemeChooseHelper.getColorIDForListOfLists(themeType,themeID)));
            negativeButton.setTextColor(ContextCompat.getColor(context, ColorThemeChooseHelper.getColorIDForListOfLists(themeType,themeID)));
        });
        f29.setOnClickListener(s ->{
            themeID = 28;
            dbHelper.changeListTheme(listID, themeType, themeID);
            setThemeAndTitle();
            positiveButton.setTextColor(ContextCompat.getColor(context, ColorThemeChooseHelper.getColorIDForListOfLists(themeType,themeID)));
            negativeButton.setTextColor(ContextCompat.getColor(context, ColorThemeChooseHelper.getColorIDForListOfLists(themeType,themeID)));
        });
        f30.setOnClickListener(s ->{
            themeID = 29;
            dbHelper.changeListTheme(listID, themeType, themeID);
            setThemeAndTitle();
            positiveButton.setTextColor(ContextCompat.getColor(context, ColorThemeChooseHelper.getColorIDForListOfLists(themeType,themeID)));
            negativeButton.setTextColor(ContextCompat.getColor(context, ColorThemeChooseHelper.getColorIDForListOfLists(themeType,themeID)));
        });
        f31.setOnClickListener(s ->{
            themeID = 30;
            dbHelper.changeListTheme(listID, themeType, themeID);
            setThemeAndTitle();
            positiveButton.setTextColor(ContextCompat.getColor(context, ColorThemeChooseHelper.getColorIDForListOfLists(themeType,themeID)));
            negativeButton.setTextColor(ContextCompat.getColor(context, ColorThemeChooseHelper.getColorIDForListOfLists(themeType,themeID)));
        });
        f32.setOnClickListener(s ->{
            themeID = 31;
            dbHelper.changeListTheme(listID, themeType, themeID);
            setThemeAndTitle();
            positiveButton.setTextColor(ContextCompat.getColor(context, ColorThemeChooseHelper.getColorIDForListOfLists(themeType,themeID)));
            negativeButton.setTextColor(ContextCompat.getColor(context, ColorThemeChooseHelper.getColorIDForListOfLists(themeType,themeID)));
        });

        positiveButton.setOnClickListener(s -> {
            dialog.dismiss();
        });
        negativeButton.setOnClickListener(s -> {
            themeID = oldTheme;
            dbHelper.changeListTheme(listID, themeType, themeID);
            setThemeAndTitle();
            dialog.dismiss();
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        if (listID == myDayListID || listID == importantID || listID == plannedID || listID == tasksID){
            getMenuInflater().inflate(R.menu.menu_fixed_list, menu);
            menu.getItem(0).getIcon().setTint(ContextCompat.getColor(this, ColorThemeChooseHelper.getColorIDFont(themeType,themeID)));
            menu.getItem(0).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    changeTheme();
                    return true;
                }
            });
            menuItems.add(menu.getItem(0));
        }
        else {
            getMenuInflater().inflate(R.menu.menu_list, menu);
            menu.getItem(0).getIcon().setTint(ContextCompat.getColor(this, ColorThemeChooseHelper.getColorIDFont(themeType,themeID)));
            menu.getItem(0).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    renameList();
                    return true;
                }
            });
            menu.getItem(1).getIcon().setTint(ContextCompat.getColor(this, ColorThemeChooseHelper.getColorIDFont(themeType,themeID)));
            menu.getItem(1).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    changeTheme();
                    return true;
                }
            });
            menu.getItem(2).getIcon().setTint(ContextCompat.getColor(this, ColorThemeChooseHelper.getColorIDFont(themeType,themeID)));
            menu.getItem(2).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    deleteList();
                    return true;
                }
            });
            menuItems.add(menu.getItem(0));
            menuItems.add(menu.getItem(1));
            menuItems.add(menu.getItem(2));
        }
        return super.onCreateOptionsMenu(menu);
    }
}