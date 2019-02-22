package com.reeflix;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;
import java.util.Locale;

public class ListViewAdapter extends ArrayAdapter<String> {


    private HomeActivity activity;
    private List<String> friendList;
    private List<String> searchList;

    private class ViewHolder {

        private TextView friendName;

        public ViewHolder(View v) {

            friendName = (TextView) v.findViewById(R.id.text);
        }
    }


    public ListViewAdapter(HomeActivity context, int resource, List<String> objects) {
        super(context, resource, objects);
        this.activity = context;
        this.friendList = objects;
        System.out.println("friend list"+this.friendList);
        /*this.searchList = new ArrayList<>();
        this.searchList.addAll(friendList);
        System.out.println("search list>>>>>>>"+this.searchList);*/
    }

    @Override
    public int getCount() {
        return friendList.size();
    }

    @Override
    public String getItem(int position) {
        return friendList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        LayoutInflater inflater = (LayoutInflater) activity.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        // If holder not exist then locate all view from UI file.
        if (convertView == null) {
            // inflate UI from XML file
            convertView = inflater.inflate(R.layout.item_listview, parent, false);
            // get all UI view
            holder = new ViewHolder(convertView);
            // set tag for holder
            convertView.setTag(holder);
        } else {
            // if holder created, get tag from view
            holder = (ViewHolder) convertView.getTag();
        }

        holder.friendName.setText(getItem(position));



        return convertView;
    }

    // Filter method
    public void filter(String charText) {
        System.out.println("hffbhhfhtgh"+charText);
        charText = charText.toLowerCase(Locale.getDefault());
        friendList.clear();
        if (charText.length() == 0) {
            friendList.addAll(searchList);
        } else {
            for (String s : searchList) {
                if (s.toLowerCase(Locale.getDefault()).contains(charText)) {
                    friendList.add(s);
                }
            }
        }
        notifyDataSetChanged();
    }




}
