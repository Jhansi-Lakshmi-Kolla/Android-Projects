package com.osu.way2go;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.RadioButton;
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

    int selectCount;
    String selectedFriend;

    TextView friend;
    CheckBox selectFriend;

    int selectedPosition = -1;

    public FriendsListDialogAdapter(List<String> friends, Context context, int selectCount){
        friendsList = friends;
        inflater = LayoutInflater.from(context);
        mContext = context;
        selectedFriendsList = new ArrayList<>();
        this.selectCount = selectCount;
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
    public View getView(final int position, View convertView, ViewGroup parent) {
        if(convertView == null){
            convertView = inflater.inflate(R.layout.select_friend, null);
            friend = (TextView)convertView.findViewById(R.id.friend);
            selectFriend = (CheckBox) convertView.findViewById(R.id.selectFriend);
            final String a = friendsList.get(position);
            friend.setText(a);

            if(position==selectedPosition){
                selectFriend.setChecked(true);
            }else{
                selectFriend.setChecked(false);
            }
            selectFriend.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    Log.i(TAG, "onCheckedChanged");
                    if(selectCount != 1){
                        if (isChecked) {
                            Log.i(TAG, "adding " + a + " to sfl in oncheck changed");
                            selectedFriendsList.add(a);
                        } else {
                            selectedFriendsList.remove(a);
                            Log.i(TAG, "removing");
                        }
                    }else{
                        if (isChecked) {
                            Log.i(TAG, "adding " + a + " to sfl in oncheck changed");
                            selectedFriend = a;
                            selectedPosition = position;
                        } else {
                            selectedPosition = -1;
                            Log.i(TAG, "removing");
                        }
                        notifyDataSetChanged();
                    }

                }
            });




            /*selectFriend.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (selectFriend.isChecked()) {
                        String s = friend.getText().toString();
                        Log.i(TAG, "adding " + s + " to sfl in onclick");
                        selectedFriendsList.add(s);
                    }
                }
            });*/
        }

        return convertView;
    }

    public List<String> getSelectedFriendsList(){
        if(selectedFriendsList.isEmpty()){
            Log.i(TAG, "empty selected list");
        }
        return this.selectedFriendsList;
    }

    public String getSelectedFriend(){
        return this.selectedFriend;
    }


}
