package ru.nikitars.mytasks;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Pair;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import ru.nikitars.mytasks.adapters.ListOfListsAdapter;
import ru.nikitars.mytasks.db.DBHelper;
import ru.nikitars.mytasks.db.models.MyList;

public class MainActivity extends AppCompatActivity {

    LinearLayout myDayLL;
    LinearLayout importantLL;
    LinearLayout plannedLL;
    LinearLayout tasksLL;
    LinearLayout addListLL;

    RecyclerView listOfListsRV;
    private ListOfListsAdapter listOfListsAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_main);

        myDayLL = findViewById(R.id.myDay);
        importantLL = findViewById(R.id.important);
        plannedLL = findViewById(R.id.planned);
        tasksLL = findViewById(R.id.tasks);
        addListLL = findViewById(R.id.addList);

        myDayLL.setOnClickListener(s -> {
            DBHelper dbHelper = new DBHelper(this);
            MyList myList = dbHelper.getMyList("MyDayList111");
            Bundle bundle = new Bundle();
            bundle.putInt("id", myList.getId());
            bundle.putString("listName", myList.getListName());
            bundle.putInt("themeType", myList.getThemeType());
            bundle.putInt("themeID", myList.getThemeID());
            bundle.putString("listOrder", myList.getListOrder());
            Intent intent = new Intent(this, ListActivity.class);
            intent.putExtras(bundle);
            startActivity(intent);
        });
        importantLL.setOnClickListener(s -> {
            DBHelper dbHelper = new DBHelper(this);
            MyList myList = dbHelper.getMyList("ImportantList111");
            Bundle bundle = new Bundle();
            bundle.putInt("id", myList.getId());
            bundle.putString("listName", myList.getListName());
            bundle.putInt("themeType", myList.getThemeType());
            bundle.putInt("themeID", myList.getThemeID());
            bundle.putString("listOrder", myList.getListOrder());
            Intent intent = new Intent(this, ListActivity.class);
            intent.putExtras(bundle);
            startActivity(intent);
        });
        plannedLL.setOnClickListener(s -> {
            DBHelper dbHelper = new DBHelper(this);
            MyList myList = dbHelper.getMyList("PlannedList111");
            Bundle bundle = new Bundle();
            bundle.putInt("id", myList.getId());
            bundle.putString("listName", myList.getListName());
            bundle.putInt("themeType", myList.getThemeType());
            bundle.putInt("themeID", myList.getThemeID());
            bundle.putString("listOrder", myList.getListOrder());
            Intent intent = new Intent(this, ListActivity.class);
            intent.putExtras(bundle);
            startActivity(intent);
        });
        tasksLL.setOnClickListener(s -> {
            DBHelper dbHelper = new DBHelper(this);
            MyList myList = dbHelper.getMyList("TasksList111");
            Bundle bundle = new Bundle();
            bundle.putInt("id", myList.getId());
            bundle.putString("listName", myList.getListName());
            bundle.putInt("themeType", myList.getThemeType());
            bundle.putInt("themeID", myList.getThemeID());
            bundle.putString("listOrder", myList.getListOrder());
            Intent intent = new Intent(this, ListActivity.class);
            intent.putExtras(bundle);
            startActivity(intent);
        });
        addListLL.setOnClickListener(s -> {
            DBHelper dbHelper = new DBHelper(this);
            Bundle bundle = new Bundle();
            bundle.putInt("id", -1);
            Intent intent = new Intent(this, ListActivity.class);
            intent.putExtras(bundle);
            startActivity(intent);
        });

        DBHelper dbHelper = new DBHelper(this);
        dbHelper.createBasicLists();
        initRecyclerView();

    }

    private void initRecyclerView() {
        listOfListsRV = findViewById(R.id.listOfLists);
        listOfListsRV.setLayoutManager(new LinearLayoutManager((this)));
        listOfListsAdapter = new ListOfListsAdapter();
        listOfListsRV.setAdapter(listOfListsAdapter);
    }

    //private void LoadLists(){
    //    List<MyList> lists = getLists();
    //    listOfListsAdapter.setItems(lists);
    //}
    //private List<MyList> getLists(){
    //    ArrayList<MyList> lists = new ArrayList<MyList>();
    //    lists.add(new MyList(66, "ldddd", 0, 1, ""));
    //    lists.add(new MyList(66, "ldddd1", 0, 2, ""));
    //    lists.add(new MyList(66, "ldddd2", 0, 4, ""));
    //    lists.add(new MyList(66, "ldddd3", 0, 7, ""));
    //    lists.add(new MyList(66, "ldddd", 0, 1, ""));
    //    lists.add(new MyList(66, "ldddd1", 0, 2, ""));
    //    lists.add(new MyList(66, "ldddd2", 0, 4, ""));
    //    lists.add(new MyList(66, "ldddd3", 0, 7, ""));
    //    lists.add(new MyList(66, "ldddd", 0, 1, ""));
    //    lists.add(new MyList(66, "ldddd1", 0, 2, ""));
    //    lists.add(new MyList(66, "ldddd2", 0, 4, ""));
    //    lists.add(new MyList(66, "ldddd3", 0, 7, ""));
    //    return lists;
    //}

    @Override
    public void onResume() {
        super.onResume();
        TextView myDayCounter = findViewById(R.id.mainMenuMyDayCounter);
        TextView importantCounter = findViewById(R.id.mainMenuImportantCounter);
        TextView plannedCounter = findViewById(R.id.mainMenuPlannedCounter);
        TextView tasksCounter = findViewById(R.id.mainMenuTasksCounter);

        DBHelper dbHelper = new DBHelper(this);
        int myDayCount = dbHelper.getMyDayTasksNumber();
        int importantCount = dbHelper.getImportantTasksNumber();
        int plannedCount = dbHelper.getPlannedTasksNumber();
        int tasksCount = dbHelper.getUnbindedTasksNumber();

        myDayCounter.setText((myDayCount > 0)?""+myDayCount:"");
        importantCounter.setText((importantCount > 0)?""+importantCount:"");
        plannedCounter.setText((plannedCount > 0)?""+plannedCount:"");
        tasksCounter.setText((tasksCount > 0)?""+tasksCount:"");

        LinkedList<MyList> listsAll = dbHelper.getLists();
        LinkedList<MyList> lists = new LinkedList<>();
        listsAll.forEach(list -> {
            if (!(list.getListName().equals("MyDayList111") || list.getListName().equals("PlannedList111") || list.getListName().equals("ImportantList111") || list.getListName().equals("TasksList111"))){
                lists.add(list);
            }
        });
        //Добавляем кнопки списков и вешаем на них листенеры
        listOfListsAdapter.clearItems();
        listOfListsAdapter.setItems(lists);

    }
}