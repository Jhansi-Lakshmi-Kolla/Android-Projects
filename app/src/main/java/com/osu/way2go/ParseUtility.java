package com.osu.way2go;

import android.util.Log;

import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by jhansi_lak on 11/18/2015.
 */
public class ParseUtility {

    private static final String TAG = "ParseUtility";
    static ParseUser currentUser = ParseUser.getCurrentUser();;

    public ParseUtility(){
        currentUser = ParseUser.getCurrentUser();
    }

    public static String getUserName(){
        return currentUser.get("FName").toString()+ " " + currentUser.get("LName").toString();
    }

    public static String getUserEmail(){
        return currentUser.getEmail();
    }

    public static List<String> getInvites() throws ParseException {
        List<Object> invites = new ArrayList<>();
        List<String> invitesNames = new ArrayList<>();
        ParseQuery<ParseObject> q = ParseQuery.getQuery("Invite");
        q.whereEqualTo("Username", currentUser.getUsername());
        List<ParseObject> li = q.find();
        //invites.addAll(li.get(0).getString("Invites"));
        for(ParseObject o : li){
            invites.addAll(o.getList("Invites"));
        }


        for(Object oo : invites)
            invitesNames.add(oo.toString());
        return invitesNames;
    }

    public static List<String> getallUsers() throws ParseException {
        final List<String> allusers = new ArrayList<>();
        ParseQuery<ParseUser> query = ParseUser.getQuery();
        List<ParseUser> r = query.find();
        //query.whereNotEqualTo("username", ParseUser.getCurrentUser().getUsername());
        for(ParseUser p : r)
        {
            if(!currentUser.getUsername().equals(p.getUsername()))
            {
                Log.i(TAG, "adding inside getAllusers " + p.getUsername());
                allusers.add(p.getUsername());
            }
        }
        return allusers;
    }

    public static List<String> getConnectedList() throws ParseException {
        List<Object> connectedList = new ArrayList<>();
        List<String> connectedNames = new ArrayList<>();
        ParseQuery<ParseObject> q = ParseQuery.getQuery("Invite");
        q.whereEqualTo("Username", currentUser.getUsername());
        List<ParseObject> li = q.find();
        //invites.addAll(li.get(0).getString("Invites"));
        for(ParseObject o : li){
            connectedList.addAll(o.getList("Friends"));
        }


        for(Object oo : connectedList)
            connectedNames.add(oo.toString());
        return connectedNames;
    }

    public static List<String> getBlockedList() throws ParseException {
        List<Object> blockedList = new ArrayList<>();
        List<String> blockedNames = new ArrayList<>();
        ParseQuery<ParseObject> q = ParseQuery.getQuery("Invite");
        q.whereEqualTo("Username", currentUser.getUsername());
        List<ParseObject> li = q.find();
        //invites.addAll(li.get(0).getString("Invites"));
        for(ParseObject o : li){
            blockedList.addAll(o.getList("Blocked"));
        }


        for(Object oo : blockedList)
            blockedNames.add(oo.toString());
        return blockedNames;
    }
    public static List<String> getDirectedList(){
        return null;
    }

    public static void putInvites(List<String> invites){
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Invite");


        for(String user : invites){
            query.whereEqualTo("Username",user);
            try {
                List<ParseObject> results = query.find();
                for(ParseObject p : results){
                    Log.i(TAG, "putting invites in " + p.getString("Username"));
                    final ParseUser u = ParseUser.getCurrentUser();
                    p.addUnique("Invites", u.getUsername());
                    p.save();
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }


    }

    public static void putInFriendsList(String inviter){
        ParseQuery<ParseObject> pq = ParseQuery.getQuery("Invite");
        pq.whereEqualTo("Username", ParseUser.getCurrentUser().getUsername());
        try {
            List<ParseObject> results = pq.find();
            for(ParseObject p : results){
                Log.i(TAG, "putting invites in " + p.getString("Username"));
                //final ParseUser u = ParseUser.getCurrentUser();
                p.addUnique("Friends", inviter);
                p.save();
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }

        ParseQuery<ParseObject> qq = ParseQuery.getQuery("Invite");
        //qq.whereEqualTo("Username", ParseUser.getCurrentUser().getUsername());
        qq.whereEqualTo("Username", inviter);
        try {
            List<ParseObject> results = qq.find();
            for(ParseObject p : results){
                Log.i(TAG, "putting invites in " + p.getString("Username"));
                //final ParseUser u = ParseUser.getCurrentUser();
                p.addUnique("Friends", ParseUser.getCurrentUser().getUsername());
                p.save();
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }


    }

    public static void removeinInvitesList(String inviter){

        ParseQuery<ParseObject> pq = ParseQuery.getQuery("Invite");
        pq.whereEqualTo("Username", ParseUser.getCurrentUser().getUsername());
        try {
            List<ParseObject> results = pq.find();
            for(ParseObject p : results){
                Log.i(TAG, "putting invites in " + p.getString("Username"));
                p.removeAll("Invites", Arrays.asList(inviter));
                p.save();
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }


    public static boolean isValidEmailID(String emailIDEntered) throws ParseException {
        ParseQuery<ParseUser> query = ParseUser.getQuery();
        List<ParseUser> r = query.find();
        boolean valid = false;
        for(ParseUser p : r)
        {
            if(p.getUsername().equals(emailIDEntered))
            {
                valid = true;
            }
        }
        return valid;
    }

    public static void updatePassword(String emailIDEntered) {

    }

    public static String getPassword(String emailIDEntered) throws ParseException {
       String password = "";
        ParseQuery<ParseObject> q = ParseQuery.getQuery("Invite");
        q.whereEqualTo("Username", emailIDEntered);
        List<ParseObject> li = q.find();
        for(ParseObject o : li){
            password += o.getString("Pass");
        }
        Log.i(TAG, "password is :" + password);
        return password;
    }
}
