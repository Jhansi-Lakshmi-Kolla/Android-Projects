package com.osu.way2go;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;
import java.util.zip.Inflater;

/**
 * Created by jhansi_lak on 11/16/2015.
 */
public class InvitesAdapter extends BaseAdapter {
    private static final String TAG = "InvitesAdapter";
    Context mContext;
    List<String> invitesList;
    LayoutInflater inflater;
    MapsActivity mapsActivity;
    boolean isInviteList;

    public  InvitesAdapter(Context context,MapsActivity mapsActivity, List<String> invitesList, boolean isInviteList){
        this.mContext = context;
        this.mapsActivity = mapsActivity;
        this.invitesList = invitesList;
        inflater = LayoutInflater.from(mContext);
        this.isInviteList = isInviteList;
    }
    @Override
    public int getCount() {
        return invitesList.size();
    }

    @Override
    public Object getItem(int position) {
        return invitesList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View rootView;
        rootView = inflater.inflate(R.layout.invite_list_item,null);
        final TextView inviter = (TextView) rootView.findViewById(R.id.inviter);
        inviter.setText(invitesList.get(position));
        ImageButton yes = (ImageButton) rootView.findViewById(R.id.yes);
        ImageButton no = (ImageButton) rootView.findViewById(R.id.no);

        if(!isInviteList){
            yes.setVisibility(View.GONE);
            no.setVisibility(View.GONE);
        }

        yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG, "clicked yes");
                ParseUtility.putInFriendsList(inviter.getText().toString());
                ParseUtility.removeinInvitesList(inviter.getText().toString());
                invitesList.remove(position);
                notifyDataSetChanged();
            }
        });

        no.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG, "clicked no");
                ParseUtility.removeinInvitesList(inviter.getText().toString());
                invitesList.remove(position);
                notifyDataSetChanged();
            }
        });
        return rootView;
    }
}
