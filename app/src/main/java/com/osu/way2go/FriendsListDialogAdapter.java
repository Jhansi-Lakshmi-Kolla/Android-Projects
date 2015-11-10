package com.osu.way2go;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jhansi_lak on 11/9/2015.
 */
public class FriendsListDialogAdapter extends BaseAdapter {
    private static  final String TAG = "DialogAdapter";
    private List<String> friendsList;
    private List<String> selectedFriendsList;
    LayoutInflater inflater;
    Context mContext;

    TextView friend;
    CheckBox selectFriend;

    public FriendsListDialogAdapter(List<String> friends, Context context){
        friendsList = friends;
        inflater = LayoutInflater.from(context);
        mContext = context;
        selectedFriendsList = new ArrayList<>();
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
            final String a = friendsList.get(position);
            friend.setText(a);

            friend.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    String s = friend.getText().toString();
                    Log.i(TAG, "adding " + a);
                    selectedFriendsList.add(a);
                }
            });

            selectFriend = (CheckBox) convertView.findViewById(R.id.selectFriend);
            selectFriend.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener(){
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (selectFriend.isChecked()) {
                        String s = friend.getText().toString();
                        Log.i(TAG, "adding " + s + " to sfl in oncheck changed");
                        selectedFriendsList.add(s);
                    }
                }
            });
            selectFriend.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (selectFriend.isChecked()) {
                        String s = friend.getText().toString();
                        Log.i(TAG, "adding " + s + " to sfl in onclick");
                        selectedFriendsList.add(s);
                    }
                }
            });
        }

        return convertView;
    }

    public List<String> getSelectedFriendsList(){
        if(selectedFriendsList.isEmpty()){
            Log.i(TAG, "empty selected list");
        }
        return this.selectedFriendsList;
    }

    public void onCheckBoxClicked(View v){
        CheckBox checkBox = (CheckBox)v;
        if(checkBox.isChecked()){
            Log.i(TAG,"clicked the check box");
        }
    }
}
