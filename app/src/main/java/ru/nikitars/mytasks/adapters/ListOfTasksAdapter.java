package ru.nikitars.mytasks.adapters;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import ru.nikitars.mytasks.ColorThemeChooseHelper;
import ru.nikitars.mytasks.ListActivity;
import ru.nikitars.mytasks.R;
import ru.nikitars.mytasks.TaskActivity2;
import ru.nikitars.mytasks.db.DBHelper;
import ru.nikitars.mytasks.db.models.MyList;
import ru.nikitars.mytasks.db.models.MyTask;

public class ListOfTasksAdapter extends RecyclerView.Adapter<ListOfTasksAdapter.TaskViewHolder>{

    private List<MyTask> tasks = new ArrayList<>();
    private int listID = 0;

    @Override
    public TaskViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_of_tasks_item_template, parent, false);
        return new TaskViewHolder(view);
    }

    @Override
    public void onBindViewHolder(TaskViewHolder holder, int position) {
        holder.bind(tasks.get(position));
    }

    @Override
    public int getItemCount() {
        return tasks.size();
    }

    class TaskViewHolder extends RecyclerView.ViewHolder {
        private ImageView doneIconIV;
        private TextView titleTV;
        private ImageView importantIconIV;

        public TaskViewHolder(View itemView) {
            super(itemView);
            doneIconIV = itemView.findViewById(R.id.listOfTasksDoneItemImage);
            titleTV = itemView.findViewById(R.id.listOfTasksTitleTextView);
            importantIconIV = itemView.findViewById(R.id.listOfTasksImportantItemImage);
        }
        public void bind(MyTask myTask) {
            if (myTask == null) System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!! TASK_ID==NULL");
            //смена иконок на валидные
            if (myTask.isDone()){
                doneIconIV.setImageResource(R.drawable.done_icon);
            }
            else{
                doneIconIV.setImageResource(R.drawable.circle_icon);
            }
            if (myTask.isImportant()){
                importantIconIV.setImageResource(R.drawable.important_choosed_icon);
            }
            else{
                importantIconIV.setImageResource(R.drawable.important_icon);
            }

            //получение списка чтобы знать как установить цвета
            DBHelper dbHelper = new DBHelper(itemView.getContext());
            MyList myList = dbHelper.getMyList(listID);
            if (myList == null) System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!! LIST_ID==NULL");
            int themeType = 0;
            int themeID = 0;
            if (myList != null) {
                themeType = myList.getThemeType();
                themeID = myList.getThemeID();
            }

            //установка цвето и текста
            doneIconIV.setColorFilter(ContextCompat.getColor(itemView.getContext(), ColorThemeChooseHelper.getColorIDForListOfLists(myList.getThemeType(), myList.getThemeID())), android.graphics.PorterDuff.Mode.MULTIPLY);
            titleTV.setText(myTask.getTaskTitle());
            importantIconIV.setColorFilter(ContextCompat.getColor(itemView.getContext(), ColorThemeChooseHelper.getColorIDForListOfLists(myList.getThemeType(), myList.getThemeID())), android.graphics.PorterDuff.Mode.MULTIPLY);

            doneIconIV.setOnClickListener(s -> {
                if (myTask.isDone()){
                    myTask.setDone(false);
                    doneIconIV.setImageResource(R.drawable.circle_icon);
                    myTask.setCompleteTime(0);
                }
                else{
                    myTask.setDone(true);
                    doneIconIV.setImageResource(R.drawable.done_icon);
                    long timestamp = new Date().getTime();
                    myTask.setCompleteTime((int)(timestamp/1000));
                }
                dbHelper.setMyTaskDone(myTask.getId(), myTask.isDone());
                dbHelper.setMyTaskCompleteTime(myTask.getId(), myTask.getCompleteTime());
            });
            importantIconIV.setOnClickListener(s -> {
                if (myTask.isImportant()){
                    myTask.setImportant(false);
                    importantIconIV.setImageResource(R.drawable.important_icon);
                }
                else{
                    myTask.setImportant(true);
                    importantIconIV.setImageResource(R.drawable.important_choosed_icon);
                }
                dbHelper.setMyTaskImportant(myTask.getId(), myTask.isImportant());
            });

            //кликер чтобы перейти в меню задачи
            itemView.setOnClickListener(v -> {
                Intent intent = new Intent(v.getContext(), TaskActivity2.class);
                Bundle bundle = new Bundle();
                bundle.putInt("listID", myList.getId());
                bundle.putInt("taskID", myTask.getId());
                bundle.putString("taskTitle", myTask.getTaskTitle());
                bundle.putString("taskExplanation", myTask.getTaskExplanation());
                bundle.putBoolean("isDone", myTask.isDone());
                bundle.putBoolean("isImportant", myTask.isImportant());
                bundle.putBoolean("isInMyDay", myTask.isInMyDay());
                bundle.putInt("completeTime", myTask.getCompleteTime());
                bundle.putInt("remindDate", myTask.getRemindDate());
                bundle.putInt("createDate", myTask.getCreateDate());
                bundle.putInt("toDoDate", myTask.getToDoDate());
                intent.putExtras(bundle);
                ContextCompat.startActivity(v.getContext(), intent, null);
            });
        }
    }

    public int getListID(){
        return listID;
    }
    public void setListID(int newListID){
        listID = newListID;
    }

    public MyTask getItem(int id){
        return tasks.get(id);
    }

    public void setItems(Collection<MyTask> myTasks) {
        tasks.addAll(myTasks);
        notifyDataSetChanged();
    }

    public void clearItems() {
        tasks.clear();
        notifyDataSetChanged();
    }

    public void removeItem(int position) {
        tasks.remove(position);
        notifyItemRemoved(position);
    }

    public void restoreItem(MyTask item, int position) {
        tasks.add(position, item);
        notifyItemInserted(position);
    }

    public List<MyTask> getData() {
        return tasks;
    }

}
