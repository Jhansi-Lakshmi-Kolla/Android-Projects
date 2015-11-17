package com.osu.way2go;

/**
 * Created by jhansi_lak on 10/28/2015.
 */


import android.app.Dialog;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.ParseException;

import java.util.List;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> {
    private static  final String TAG = "MyAdapter";
    Context mContext;
    MapsActivity mapsActivity;

    private static final int TYPE_HEADER = 0;
    private static final int TYPE_ITEM = 1;

    private String mNavTitles[];
    private int mIcons[];

    private String name;
    private int profile;
    private String email;




    public static class ViewHolder extends RecyclerView.ViewHolder {
        int Holderid;

        TextView textView;
        ImageView imageView;
        ListView hiddenList;
        ImageView profile;
        TextView Name;
        TextView email;


        public ViewHolder(View itemView, int ViewType) {
            super(itemView);
            if (ViewType == TYPE_ITEM) {
                textView = (TextView) itemView.findViewById(R.id.rowText);
                imageView = (ImageView) itemView.findViewById(R.id.rowIcon);
                hiddenList = (ListView) itemView.findViewById(R.id.expandableList);
                Holderid = 1;
            } else {


                Name = (TextView) itemView.findViewById(R.id.name);
                email = (TextView) itemView.findViewById(R.id.email);
                profile = (ImageView) itemView.findViewById(R.id.circleView);
                Holderid = 0;
            }
        }


    }


    MyAdapter(String Titles[], int Icons[], String Name, String Email, int Profile, Context context) {
        mContext = context;
        mapsActivity = (MapsActivity) context;
        mNavTitles = Titles;
        mIcons = Icons;
        name = Name;
        email = Email;
        profile = Profile;
    }




    @Override
    public MyAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        if (viewType == TYPE_ITEM) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.navigation_item_row, parent, false);

            ViewHolder vhItem = new ViewHolder(v, viewType);
            return vhItem;

        } else if (viewType == TYPE_HEADER) {

            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.header, parent, false);
            ViewHolder vhHeader = new ViewHolder(v, viewType);
            return vhHeader;
        }
        return null;

    }


    @Override
    public void onBindViewHolder(final MyAdapter.ViewHolder holder, int position) {
        if (holder.Holderid == 1) {
            holder.textView.setText(mNavTitles[position - 1]);
            holder.imageView.setImageResource(mIcons[position - 1]);

            holder.textView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(((TextView)v).getText().toString().equals("Invite Friends")){
                        Toast.makeText(mContext, "inviting friends", Toast.LENGTH_SHORT).show();

                        final Dialog addFriendsDialog = new Dialog((mContext));
                        addFriendsDialog.setContentView(R.layout.add_friends_layout);
                        addFriendsDialog.setTitle("Select Friends");
                        ListView addFriendsList = (ListView) addFriendsDialog.findViewById(R.id.addFriends);
                        List<String> allUsers = null;
                        try {
                            allUsers = mapsActivity.getallUsers();
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }

                        final FriendsListDialogAdapter addFriendsAdapter = new FriendsListDialogAdapter(allUsers, mContext);
                        addFriendsList.setAdapter(addFriendsAdapter);

                        addFriendsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                            }
                        });
                        Button inviteAll = (Button) addFriendsDialog.findViewById(R.id.inviteAll);
                        Button invite = (Button) addFriendsDialog.findViewById(R.id.invite);

                        final List<String> finalAllUsers = allUsers;
                        inviteAll.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                //invite everyone
                                mapsActivity.putInvites(finalAllUsers);
                                addFriendsDialog.dismiss();
                            }
                        });

                        invite.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                //invite selected ones
                                mapsActivity.putInvites(addFriendsAdapter.getSelectedFriendsList());
                                for(String s: addFriendsAdapter.getSelectedFriendsList()){
                                    Log.i(TAG, "selected friend : " + s);

                                }
                                addFriendsDialog.dismiss();
                            }
                        });

                        addFriendsDialog.show();
                    }else if(((TextView)v).getText().toString().equals("Invites")){
                        List<String> invites = null;
                        try {
                            invites = mapsActivity.getInvites();
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        Log.i(TAG, "Clicked on invites. showing listview");
                        if(invites != null && !invites.isEmpty()){
                            InvitesAdapter adapter = new InvitesAdapter(mContext, mapsActivity,invites);
                            holder.hiddenList.setAdapter(adapter);
                        }


                    }else if(((TextView)v).getText().toString().equals("Connected")){
                        List<String> connectedList = mapsActivity.getConnectedList();
                        Log.i(TAG, "Clicked on invites. showing listview");
                        if(connectedList!= null && !connectedList.isEmpty()){
                            for(String s: connectedList){
                                Log.i(TAG, "connected has " + s);
                            }
                            final ArrayAdapter adapter = new ArrayAdapter(mContext,
                                    android.R.layout.simple_list_item_1, connectedList);
                            holder.hiddenList.setAdapter(adapter);
                        }
                    }else if(((TextView)v).getText().toString().equals("Blocked")){
                        List<String> blockedList = mapsActivity.getBlockedList();
                        Log.i(TAG, "Clicked on invites. showing listview");
                        if(blockedList!= null && !blockedList.isEmpty()){
                            for(String s: blockedList){
                                Log.i(TAG, "blocked has " + s);
                            }
                            final ArrayAdapter adapter = new ArrayAdapter(mContext,
                                    android.R.layout.simple_list_item_1, blockedList);
                            holder.hiddenList.setAdapter(adapter);
                        }
                    }

                    if(holder.hiddenList.getVisibility() == View.INVISIBLE){
                        holder.hiddenList.setVisibility(View.VISIBLE);
                    }else if(holder.hiddenList.getVisibility() == View.VISIBLE){
                        holder.hiddenList.setVisibility(View.INVISIBLE);
                    }

                }
            });
        } else {

            holder.profile.setImageResource(profile);
            holder.Name.setText(name);
            holder.email.setText(email);
        }


    }


    @Override
    public int getItemCount() {
        return mNavTitles.length + 1;
    }



    @Override
    public int getItemViewType(int position) {
        if (isPositionHeader(position))
            return TYPE_HEADER;

        return TYPE_ITEM;
    }

    private boolean isPositionHeader(int position) {
        return position == 0;
    }

}
