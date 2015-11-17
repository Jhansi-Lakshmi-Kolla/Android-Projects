package com.osu.way2go;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;
import java.util.zip.Inflater;

/**
 * Created by jhansi_lak on 11/16/2015.
 */
public class InvitesAdapter extends BaseAdapter {
    Context mContext;
    List<String> invitesList;
    LayoutInflater inflater;
    MapsActivity mapsActivity;

    public  InvitesAdapter(Context context,MapsActivity mapsActivity, List<String> invitesList){
        this.mContext = context;
        this.mapsActivity = mapsActivity;
        this.invitesList = invitesList;
        inflater = LayoutInflater.from(mContext);
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
    public View getView(int position, View convertView, ViewGroup parent) {
        View rootView;
        rootView = inflater.inflate(R.layout.invite_list_item,null);
        final TextView inviter = (TextView) rootView.findViewById(R.id.inviter);
        inviter.setText(invitesList.get(position));
        ImageView yes = (ImageView) rootView.findViewById(R.id.yes);
        ImageView no = (ImageView) rootView.findViewById(R.id.no);

        yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mapsActivity.putInFriendsList(inviter.getText().toString());
                mapsActivity.removeinInvitesList(inviter.getText().toString());
            }
        });

        no.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mapsActivity.removeinInvitesList(inviter.getText().toString());
            }
        });
        return rootView;
    }
}
