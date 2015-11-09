package com.osu.way2go;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by jhansi_lak on 11/9/2015.
 */
public class FriendsListDialogAdapter extends BaseAdapter {
    private ArrayList<String> friendsList;
    LayoutInflater inflater;
    Context mContext;

    TextView friend;
    CheckBox selectFriend;

    public FriendsListDialogAdapter(ArrayList<String> friends, Context context){
        friendsList = friends;
        inflater = LayoutInflater.from(context);
        mContext = context;
    }
    @Override
    public int getCount() {
        return friendsList.size();
    }

    @Override
    public Object getItem(int position) {
        return friendsList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(convertView == null){
            convertView = inflater.inflate(R.layout.select_friend, null);
            friend = (TextView)convertView.findViewById(R.id.friend);
            selectFriend = (CheckBox) convertView.findViewById(R.id.selectFriend);

            friend.setText(friendsList.get(position));
        }

        return convertView;
    }
}
