package edu.fsu.cs.mobile.ontime;

import android.app.ListActivity;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import static edu.fsu.cs.mobile.ontime.LoginActivity.PREFS_NAME;

public class FriendInvites extends ListActivity {
    ArrayList<String> listItems = new ArrayList<String>();
    ArrayAdapter<String> listAdapter;
    ArrayList<String> friendList = new ArrayList<>();
    ArrayList<String> myFriendList = new ArrayList<>();
    int positionToSend = -1;

    private DatabaseReference mDatabase;
    private DatabaseReference mDatabase2;
    private DatabaseReference mDatabase3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_friend_invites);

        setTitle("Friend Requests");

        listAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, listItems);
        setListAdapter(listAdapter);

        mDatabase = FirebaseDatabase.getInstance().getReference("requests/" + getSharedPreferences(PREFS_NAME, 0).getString("username",""));

        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {

                listItems.clear();

                for(DataSnapshot next : snapshot.getChildren())
                {
                    String myRequests = next.getValue(String.class);
                    listItems.add(myRequests);
                }

                listAdapter.notifyDataSetChanged();
            }
            @Override
            public void onCancelled(DatabaseError firebaseError) {
                System.out.println("The read failed: " + firebaseError.getMessage());
            }
        });

        listAdapter.notifyDataSetChanged();
        registerForContextMenu(getListView());
    }


    public void onCreateContextMenu(ContextMenu menu, View view, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, view, menuInfo);
        AdapterView.AdapterContextMenuInfo info =  (AdapterView.AdapterContextMenuInfo) menuInfo;
        positionToSend = info.position;
        menu.setHeaderTitle("Request From " + listItems.get(positionToSend));

        mDatabase2 = FirebaseDatabase.getInstance().getReference("friends/" + getSharedPreferences(PREFS_NAME, 0).getString("username",""));

        mDatabase2.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {

                myFriendList.clear();

                for(DataSnapshot next : snapshot.getChildren())
                {
                    String friendName = next.getValue(String.class);
                    myFriendList.add(friendName);
                }
            }
            @Override
            public void onCancelled(DatabaseError firebaseError) {
                System.out.println("The read failed: " + firebaseError.getMessage());
            }
        });


        mDatabase3 = FirebaseDatabase.getInstance().getReference("friends/" + listItems.get(positionToSend));

        mDatabase3.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {

                friendList.clear();

                for(DataSnapshot next : snapshot.getChildren())
                {
                    String friendName = next.getValue(String.class);
                    friendList.add(friendName);
                }
            }
            @Override
            public void onCancelled(DatabaseError firebaseError) {
                System.out.println("The read failed: " + firebaseError.getMessage());
            }
        });


        menu.add("Accept");
        menu.add("Dismiss");
    }

    public boolean onContextItemSelected(MenuItem item){
        super.onContextItemSelected(item);

        if(item.getTitle().equals("Accept"))
        {
            myFriendList.add(listItems.get(positionToSend));
            mDatabase2.setValue(myFriendList);
            friendList.add(getSharedPreferences(PREFS_NAME, 0).getString("username",""));
            mDatabase3.setValue(friendList);
        }

        listItems.remove(positionToSend);
        mDatabase.setValue(listItems);

        return true;
    }
}
