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
import java.util.List;

import ru.nikitars.mytasks.ColorThemeChooseHelper;
import ru.nikitars.mytasks.ListActivity;
import ru.nikitars.mytasks.R;
import ru.nikitars.mytasks.db.DBHelper;
import ru.nikitars.mytasks.db.models.MyList;

public class ListOfListsAdapter extends RecyclerView.Adapter<ListOfListsAdapter.ListViewHolder> {

    private List<MyList> lists = new ArrayList<>();

    @Override
    public ListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_of_lists_item_template, parent, false);
        return new ListViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ListViewHolder holder, int position) {
        holder.bind(lists.get(position));
    }

    @Override
    public int getItemCount() {
        return lists.size();
    }

    class ListViewHolder extends RecyclerView.ViewHolder implements ru.nikitars.mytasks.adapters.ListViewHolder {
        private ImageView iconIV;
        private TextView titleTV;
        private TextView counterTV;

        public ListViewHolder(View itemView) {
            super(itemView);
            iconIV = itemView.findViewById(R.id.listOfListsItemImage);
            titleTV = itemView.findViewById(R.id.listOfListsItemTitle);
            counterTV = itemView.findViewById(R.id.listOfListsItemCounter);
        }
        public void bind(MyList myList) {
            iconIV.setColorFilter(ContextCompat.getColor(itemView.getContext(), ColorThemeChooseHelper.getColorIDForListOfLists(myList.getThemeType(), myList.getThemeID())), android.graphics.PorterDuff.Mode.MULTIPLY);
            titleTV.setText(myList.getListName());
            int counter = new DBHelper(itemView.getContext()).getMyListCount(myList.getId());
            String countText = "";
            if (counter > 0) countText += counter;
            counterTV.setText(countText);
            itemView.setOnClickListener(v -> {
                Intent intent = new Intent(v.getContext(), ListActivity.class);
                Bundle bundle = new Bundle();
                bundle.putInt("id", myList.getId());
                bundle.putString("listName", myList.getListName());
                bundle.putInt("themeType", myList.getThemeType());
                bundle.putInt("themeID", myList.getThemeID());
                bundle.putString("listOrder", myList.getListOrder());
                intent.putExtras(bundle);
                ContextCompat.startActivity(v.getContext(), intent, null);
            });
        }
    }

    public MyList getItem(int id){
        return lists.get(id);
    }

    public void setItems(Collection<MyList> myLists) {
        lists.addAll(myLists);
        notifyDataSetChanged();
    }

    public void clearItems() {
        lists.clear();
        notifyDataSetChanged();
    }
}
