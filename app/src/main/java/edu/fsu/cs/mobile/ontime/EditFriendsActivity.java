package edu.fsu.cs.mobile.ontime;

import android.app.ListActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import static edu.fsu.cs.mobile.ontime.LoginActivity.PREFS_NAME;

public class EditFriendsActivity extends ListActivity {

    ArrayList<String> listItems = new ArrayList<String>();
    ArrayList<Friend> friendList = new ArrayList<Friend>();

    ArrayAdapter<String> listAdapter;

    int positionToDelete = -1;

    private DatabaseReference mDatabase;
    private DatabaseReference mDatabase1;

    String removalKey = "";

    int numberOfFriends = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_friends);

        setTitle("Edit Friends");
        listAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, listItems);
        setListAdapter(listAdapter);

        mDatabase = FirebaseDatabase.getInstance().getReference("friends/" + getSharedPreferences(PREFS_NAME, 0).getString("username",""));

        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {

                listItems.clear();

                for(DataSnapshot next : snapshot.getChildren())
                {
                    String myFriend = next.getValue(String.class);

                    friendList.add(new Friend(next.getKey(), myFriend));

                    listItems.add(myFriend);
                    numberOfFriends++;
                }

                listAdapter.notifyDataSetChanged();
            }
            @Override
            public void onCancelled(DatabaseError firebaseError) {
                System.out.println("The read failed: " + firebaseError.getMessage());
            }
        });

        registerForContextMenu(getListView());
    }

    public void onCreateContextMenu(ContextMenu menu, View view, ContextMenu.ContextMenuInfo menuInfo)
    {
        super.onCreateContextMenu(menu, view, menuInfo);
        AdapterView.AdapterContextMenuInfo info =  (AdapterView.AdapterContextMenuInfo) menuInfo;
        positionToDelete = info.position;
        menu.add(0, view.getId(), 0, "Remove Friend " + getListView().getItemAtPosition(positionToDelete) + "?");
    }

    @Override
    public boolean onContextItemSelected(MenuItem item)
    {
        for(int i = 0; i < friendList.size(); i++)
        {
            if(friendList.get(i).getFriendName().equals(listItems.get(positionToDelete)))
            {
                removalKey = friendList.get(i).getFriendKey();
            }
        }

        mDatabase.child(removalKey).removeValue();

        return true;
    }

    public void addFriendClicked(View view)
    {
        if(listItems.contains(((TextView) findViewById(R.id.friendAdded)).getText().toString()))
        {
            return;
        }

        mDatabase1 = FirebaseDatabase.getInstance().getReference("users");

        mDatabase1.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {

                for(DataSnapshot next : snapshot.getChildren())
                {
                    if(next.getKey().equals(((TextView) findViewById(R.id.friendAdded)).getText().toString()))
                    {
                        listItems.add(((TextView) findViewById(R.id.friendAdded)).getText().toString());
                        mDatabase.setValue(listItems);
                        return;
                    }
                }

            }
            @Override
            public void onCancelled(DatabaseError firebaseError) {
                System.out.println("The read failed: " + firebaseError.getMessage());
            }
        });

    }

}
