package edu.fsu.cs.mobile.ontime;

import android.app.ListActivity;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import static edu.fsu.cs.mobile.ontime.LoginActivity.PREFS_NAME;

public class EditFriendsActivity extends ListActivity {

    ArrayList<String> listItems = new ArrayList<String>();
    ArrayList<Friend> myFriendList = new ArrayList<Friend>();
    ArrayList<String> friendsList = new ArrayList<>();
    ArrayList<String> friendsList2 = new ArrayList<>();
    ArrayList<String> requestList = new ArrayList<>();
    ArrayList<String> myRequestList = new ArrayList<>();


    ArrayAdapter<String> listAdapter;

    int positionToDelete = -1;

    private DatabaseReference mDatabase;
    private DatabaseReference mDatabase1;
    private DatabaseReference mDatabase2;
    private DatabaseReference mDatabase3;
    private DatabaseReference mDatabase4;
    private DatabaseReference mDatabase5;


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

                    myFriendList.add(new Friend(next.getKey(), myFriend));

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
        menu.add(0, view.getId(), 0, "Delete Friend " + getListView().getItemAtPosition(positionToDelete) + "?");

    }

    @Override
    public boolean onContextItemSelected(MenuItem item)
    {
        for(int i = 0; i < myFriendList.size(); i++)
        {
            if(myFriendList.get(i).getFriendName().equals(listItems.get(positionToDelete)))
            {
                removalKey = myFriendList.get(i).getFriendKey();
            }
        }
        mDatabase.child(removalKey).removeValue();

        mDatabase5 = FirebaseDatabase.getInstance().getReference("friends/" + listItems.get(positionToDelete));

        mDatabase5.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot)
            {
                friendsList2.clear();

                for(DataSnapshot next : snapshot.getChildren())
                {
                    String myFriend = next.getValue(String.class);
                    friendsList2.add(myFriend);
                }
            }
            @Override
            public void onCancelled(DatabaseError firebaseError) {
                System.out.println("The read failed: " + firebaseError.getMessage());
            }
        });

        friendsList2.remove(getSharedPreferences(PREFS_NAME, 0).getString("username", ""));
        mDatabase5.setValue(friendsList2);

        return true;
    }

    public void addFriendClicked(View view)
    {
        mDatabase2 = FirebaseDatabase.getInstance().getReference("requests/" + ((TextView) findViewById(R.id.friendAdded)).getText().toString());

        mDatabase2.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {

                requestList.clear();

                for(DataSnapshot next : snapshot.getChildren())
                {
                    String myFriend = next.getValue(String.class);

                    requestList.add(myFriend);
                }

            }
            @Override
            public void onCancelled(DatabaseError firebaseError) {
                System.out.println("The read failed: " + firebaseError.getMessage());
            }
        });

        if(requestList.contains(getSharedPreferences(PREFS_NAME, 0).getString("username",""))){
            Toast.makeText(this, "Request Pending", Toast.LENGTH_SHORT).show();
            return;
        }

        mDatabase3 = FirebaseDatabase.getInstance().getReference("requests/" + getSharedPreferences(PREFS_NAME, 0).getString("username",""));

        mDatabase3.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {

                myRequestList.clear();
                for(DataSnapshot next : snapshot.getChildren())
                {
                    myRequestList.add(next.getValue(String.class));
                }

            }
            @Override
            public void onCancelled(DatabaseError firebaseError) {
                System.out.println("The read failed: " + firebaseError.getMessage());
            }
        });


        mDatabase4 = FirebaseDatabase.getInstance().getReference("friends/" + ((TextView) findViewById(R.id.friendAdded)).getText().toString());

        mDatabase4.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {

                friendsList.clear();

                for(DataSnapshot next : snapshot.getChildren())
                {
                    String myFriend = next.getValue(String.class);
                    friendsList.add(myFriend);
                }

            }
            @Override
            public void onCancelled(DatabaseError firebaseError) {
                System.out.println("The read failed: " + firebaseError.getMessage());
            }
        });

        if(listItems.contains(((TextView) findViewById(R.id.friendAdded)).getText().toString()) || getSharedPreferences(PREFS_NAME, 0).getString("username","").equals(((TextView) findViewById(R.id.friendAdded)).getText().toString()))
        {
            Toast.makeText(this, "Already Friends", Toast.LENGTH_LONG).show();
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
                        if(myRequestList.contains(((TextView) findViewById(R.id.friendAdded)).getText().toString())){
                            myRequestList.remove(((TextView) findViewById(R.id.friendAdded)).getText().toString());
                            listItems.add(((TextView) findViewById(R.id.friendAdded)).getText().toString());
                            friendsList.add(getSharedPreferences(PREFS_NAME, 0).getString("username", ""));

                            mDatabase.setValue(listItems);
                            mDatabase3.setValue(myRequestList);
                            mDatabase4.setValue(friendsList);
                            Toast.makeText(getApplicationContext(), "Friend Confirmed", Toast.LENGTH_LONG).show();
                        }
                        else {
                            requestList.add(getSharedPreferences(PREFS_NAME, 0).getString("username", ""));
                            mDatabase2.setValue(requestList);
                            Toast.makeText(getApplicationContext(), "Friend Request Sent", Toast.LENGTH_LONG).show();
                        }
                        ((TextView) findViewById(R.id.friendAdded)).setText("");
                        return;
                    }
                }
                Toast.makeText(getApplicationContext(), "Not Valid User", Toast.LENGTH_LONG).show();
            }
            @Override
            public void onCancelled(DatabaseError firebaseError) {
                System.out.println("The read failed: " + firebaseError.getMessage());
            }
        });
    }
}
