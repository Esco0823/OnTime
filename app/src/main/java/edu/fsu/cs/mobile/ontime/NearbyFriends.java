package edu.fsu.cs.mobile.ontime;

import android.app.ListActivity;
import android.location.Location;
import android.os.Bundle;
import android.view.ContextMenu;
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

public class NearbyFriends extends ListActivity {

    ArrayList<String> listItems = new ArrayList<String>();
    ArrayList<Users> friendList = new ArrayList<Users>();

    ArrayAdapter<String> listAdapter;

    int positionToSend = -1;

    private DatabaseReference mDatabase;
    private DatabaseReference mDatabase1;
    private DatabaseReference mDatabase2;

    Users me;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_naerbyfreinds);

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
                    //if distances are fine{
                    listItems.add(myFriend);
                }

                listAdapter.notifyDataSetChanged();
            }
            @Override
            public void onCancelled(DatabaseError firebaseError) {
                System.out.println("The read failed: " + firebaseError.getMessage());
            }
        });

        mDatabase1 = FirebaseDatabase.getInstance().getReference("users");

        mDatabase1.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot snapshot) {

                for(DataSnapshot next : snapshot.getChildren())
                {
                    if(next.getKey().equals(getSharedPreferences(PREFS_NAME, 0).getString("username","")))
                    {
                        me = next.getValue(Users.class);
                    }
                }
            }
            @Override
            public void onCancelled(DatabaseError firebaseError) {
                System.out.println("The read failed: " + firebaseError.getMessage());
            }
        });

        mDatabase2 = FirebaseDatabase.getInstance().getReference("users/");

        mDatabase2.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot snapshot) {

                for(DataSnapshot next : snapshot.getChildren())
                {
                    for(int i = 0; i < getListView().getCount() - 1; i++)
                    {
                        if (next.getKey().equals(getListView().getItemAtPosition(i)))
                        {
                            friendList.add(next.getValue(Users.class));
                        }
                    }

                }
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
        positionToSend = info.position;

        Location myLocation = new Location("");
        Location theyLocation = new Location("");


        double lat = Double.parseDouble(me.getLatitude());
        double lon = Double.parseDouble(me.getLongitude());


        myLocation.setLatitude(lat);
        myLocation.setLongitude(lon);

        double theyLat = Double.parseDouble(friendList.get(positionToSend).getLatitude());
        double theyLon = Double.parseDouble(friendList.get(positionToSend).getLongitude());

        theyLocation.setLatitude(theyLat);
        theyLocation.setLongitude(theyLon);

        menu.add(0, view.getId(), 0, getListView().getItemAtPosition(positionToSend) + " is " + chop(myLocation.distanceTo(theyLocation)/(1609.344)) + " miles away");
    }


    public String chop(double location){
        String myString = location + "00";
        String chopped = "";
        boolean decimalFound = false;

        int i;
        for (i = 0; i < myString.length() && !decimalFound; i++)
        {
            if(myString.charAt(i) == '.')
            {
                decimalFound = true;
            }
            chopped = chopped + myString.charAt(i);
        }

        chopped = chopped + myString.charAt(i);
        chopped = chopped + myString.charAt(i + 1);


        return chopped;
    }

}
