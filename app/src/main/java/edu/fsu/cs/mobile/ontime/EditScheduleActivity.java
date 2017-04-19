package edu.fsu.cs.mobile.ontime;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import static edu.fsu.cs.mobile.ontime.LoginActivity.PREFS_NAME;

public class EditScheduleActivity extends ListActivity {

    ArrayList<Classes> classList = new ArrayList<Classes>();

    ArrayList<String> classListItems = new ArrayList<String>();

    ArrayAdapter<String> listAdapter;

    EditText courseName, courseId, buildingName, roomNo;
    Spinner shours, sminutes, sampm, ehours, eminutes, eampm;
    CheckBox mon, tues, wed, thurs, fri;

    int i, j;

    int positionToDelete = -1;
    int removalIndex = -1;

    private DatabaseReference mDatabase;
    private Classes course;


    int realStartMinute = 0;
    int realStartHour = 0;
    int realEndMinute = 0;
    int realEndHour = 0;

    String building;
    String courseIdText;
    String courseCodeText;
    String room;


    String[] tokenized = new String[5];


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_schedule);

        setTitle("Edit Schedule");

        listAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, classListItems);
        setListAdapter(listAdapter);

        courseName = (EditText) findViewById(R.id.courseET);
        courseId = (EditText) findViewById(R.id.courseIdET);
        buildingName = (EditText) findViewById(R.id.buildingET);
        roomNo = (EditText) findViewById(R.id.roomNumET);

        shours = (Spinner) findViewById(R.id.sHoursSpinner);
        sminutes = (Spinner) findViewById(R.id.sMinutesSpinner);
        sampm = (Spinner) findViewById(R.id.sAmpmSpinner);
        ehours = (Spinner) findViewById(R.id.eHoursSpinner);
        eminutes = (Spinner) findViewById(R.id.eMinutesSpinner);
        eampm = (Spinner) findViewById(R.id.eAMPMSpinner);


        mon = (CheckBox) findViewById(R.id.mCB);
        tues = (CheckBox) findViewById(R.id.tCB);
        wed = (CheckBox) findViewById(R.id.wCB);
        thurs = (CheckBox) findViewById(R.id.thCB);
        fri = (CheckBox) findViewById(R.id.fCB);



        mDatabase = FirebaseDatabase.getInstance().getReference("schedule/" + getSharedPreferences(PREFS_NAME, 0).getString("username", ""));

        i = 0;

        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                classList.clear();
                classListItems.clear();

                for(DataSnapshot next : dataSnapshot.getChildren())
                {
                    classList.add(next.getValue(Classes.class));
                    classListItems.add(next.getValue(Classes.class).getCourse() + " " + intToDay(next.getValue(Classes.class).getDays()) + " " + militaryToNormal(next.getValue(Classes.class).getStartTime()) + " - " + militaryToNormal(next.getValue(Classes.class).getEndTime()) + " " + next.getValue(Classes.class).getBuilding() + "" + next.getValue(Classes.class).getRoom());
                }

                listAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        registerForContextMenu(getListView());
    }


    public void onCreateContextMenu(ContextMenu menu, View view, ContextMenu.ContextMenuInfo menuInfo)
    {
        super.onCreateContextMenu(menu, view, menuInfo);
        AdapterView.AdapterContextMenuInfo info =  (AdapterView.AdapterContextMenuInfo) menuInfo;
        positionToDelete = info.position;
        menu.add(0, view.getId(), 0, "Delete Break " + getListView().getItemAtPosition(positionToDelete) + "?");
    }

    @Override
    public boolean onContextItemSelected(MenuItem item)
    {
        tokenized = getTokens(classListItems.get(positionToDelete));

        for(j = 0; j < classList.size(); j++)
        {

            if(classList.get(j).getCourse().equals(tokenized[0]) &&
                    intToDay(classList.get(j).getDays()).equals(tokenized[1]) &&
                    militaryToNormal(classList.get(j).getStartTime()).equals(tokenized[2]) &&
                    militaryToNormal(classList.get(j).getEndTime()).equals(tokenized[3]) &&
                    (classList.get(j).getBuilding() + classList.get(j).getRoom()).equals(tokenized[4]))
            {
                removalIndex = j;
            }
        }

        classList.remove(removalIndex);
        mDatabase.setValue(classList);

        return true;
    }

    public String[] getTokens(String line)
    {
        String[] tokens = new String[5];
        tokens[0] = "";
        tokens[1] = "";
        tokens[2] = "";
        tokens[3] = "";
        tokens[4] = "";

        int k;
        for(k = 0; k < line.length() && line.charAt(k) != ' '; k++)
        {
            tokens[0] = tokens[0] + line.charAt(k);
        }
        k++;

        for(; k < line.length() && line.charAt(k) != ' '; k++)
        {
            tokens[1] = tokens[1] + line.charAt(k);
        }
        k++;

        for(; k < line.length() && line.charAt(k) != '-'; k++)
        {
            tokens[2] = tokens[2] + line.charAt(k);
        }
        tokens[2] = tokens[2].trim();
        k = k + 2;

        for(; k < line.length() && line.charAt(k) != ' '; k++)
        {
            tokens[3] = tokens[3] + line.charAt(k);
        }
        k++;

        tokens[3] = tokens[3] + ' ';

        for(; k < line.length() && line.charAt(k) != ' '; k++)
        {
            tokens[3] = tokens[3] + line.charAt(k);
        }
        k++;

        for(; k < line.length(); k++)
        {
            tokens[4] = tokens[4] + line.charAt(k);
        }

        return tokens;
    }

    public String militaryToNormal(String military)
    {
        String normal = "";
        String ammpm = "";
        int colonLocation = military.indexOf(":");
        int hour = Integer.parseInt(military.substring(0, colonLocation));
        int minutes = Integer.parseInt(military.substring(colonLocation + 1, military.length()));

        if(hour < 12)
        {
            if(hour == 0)
            {
                hour = 12;
            }
            ammpm = "AM";
        }
        else
        {
            if(hour > 12)
            {
                hour = hour - 12;
            }
            ammpm = "PM";
        }


        normal = hour + ":";

        if(minutes < 10)
        {
            normal = normal + "0";
        }

        normal = normal + minutes + " " + ammpm;

        return normal;
    }


    public String intToDay(String daysString)
    {
        String solution = "";

        if(daysString.contains("0"))
        {
            solution = solution + "SU";
        }
        if(daysString.contains("1"))
        {
            solution = solution + "M";
        }
        if(daysString.contains("2"))
        {
            solution = solution + "T";
        }
        if(daysString.contains("3"))
        {
            solution = solution + "W";
        }
        if(daysString.contains("4"))
        {
            solution = solution + "TR";
        }
        if(daysString.contains("5"))
        {
            solution = solution + "F";
        }
        if(daysString.contains("6"))
        {
            solution = solution + "SA";
        }

        return solution;
    }

    public void SkipClass(View v){
        Intent intent = new Intent(this, EditLunchActivity.class);
        startActivity(intent);
    }

    public void AddClass(View v) {

        courseIdText = courseId.getText().toString().trim();
        courseCodeText = courseName.getText().toString().trim();

        String course = courseName.getText().toString().trim() + courseId.getText().toString().trim();

        building = buildingName.getText().toString().trim();

        room = roomNo.getText().toString().trim();

        String days = "";
        if (mon.isChecked()) days = "1";
        if (tues.isChecked()) days = days + "2";
        if (wed.isChecked()) days = days + "3";
        if (thurs.isChecked()) days = days + "4";
        if (fri.isChecked()) days = days + "5";

        String start = "";
        int sh = Integer.parseInt(shours.getSelectedItem().toString().trim());
        if (sampm.getSelectedItem().toString().trim().equals("PM")) {
            if (sh == 12) {
                sh = 12;
            } else {
                sh = sh + 12;
            }
        } else {
            if (sh == 12) {
                sh = 0;
            }
        }


        realStartHour = sh;
        realStartMinute = Integer.parseInt(sminutes.getSelectedItem().toString().trim());

        start = start + sh + ":" + sminutes.getSelectedItem().toString().trim();

        String end = "";
        int eh = Integer.parseInt(ehours.getSelectedItem().toString().trim());
        if (eampm.getSelectedItem().toString().trim().equals("PM")) {
            if (eh == 12) {
                eh = 12;
            } else {
                eh = eh + 12;
            }
        } else {
            if (eh == 12) {
                eh = 0;
            }
        }

        realEndHour = eh;
        realEndMinute = Integer.parseInt(eminutes.getSelectedItem().toString().trim());

        end = end + eh + ":" + eminutes.getSelectedItem().toString().trim();

        final Classes newClass = new Classes(building, course, days, end, room, start);


        i = 0;

        if(errorChecked())
        {
            classList.add(newClass);
            mDatabase.setValue(classList);

            clearEverything();
        }
    }

    public boolean errorChecked()
    {
        int sh = realStartHour;
        int eh = realEndHour;
        int sm = realStartMinute;
        int em = realEndMinute;

        if(courseCodeText.equals(""))
        {
            Toast.makeText(this,
                    "Add course Code.", Toast.LENGTH_LONG).show();
            return false;
        }
        if(courseIdText.equals(""))
        {
            Toast.makeText(this,
                    "Add course ID.", Toast.LENGTH_LONG).show();
            return false;
        }
        if(building.equals(""))
        {
            Toast.makeText(this,
                    "Add building.", Toast.LENGTH_LONG).show();
            return false;
        }
        if(room.equals(""))
        {
            Toast.makeText(this,
                    "Add Room Number.", Toast.LENGTH_LONG).show();
            return false;
        }

        if(sh > eh)
        {Toast.makeText(this,
                "Start time is after the end time.", Toast.LENGTH_LONG).show();
            return false;
        }
        if((sh == eh) && (sm > em))
        {
            Toast.makeText(this,
                    "Start time is after the end time", Toast.LENGTH_LONG).show();
            return false;
        }
        if((sh == eh) && (sm == em))
        {
            Toast.makeText(this,
                    "Start time is the same as end time", Toast.LENGTH_LONG).show();
            return false;
        }

        if(mon.isChecked())
        {
            return true;
        }
        else if(tues.isChecked())
        {
            return true;
        }
        else if(wed.isChecked())
        {
            return true;
        }
        else if(thurs.isChecked())
        {
            return true;
        }
        else if(fri.isChecked())
        {
            return true;
        }
        else
        {
            Toast.makeText(this,
                    "Please choose a day of the week.", Toast.LENGTH_LONG).show();
            return false;
        }
    }


    public void clearEverything()
    {
        shours.setSelection(0);
        sminutes.setSelection(0);
        sampm.setSelection(0);
        ehours.setSelection(0);
        eminutes.setSelection(0);
        eampm.setSelection(0);
        mon.setChecked(false);
        tues.setChecked(false);
        wed.setChecked(false);
        thurs.setChecked(false);
        fri.setChecked(false);
        courseName.setText("");
        courseId.setText("");
        buildingName.setText("");
        roomNo.setText("");
    }
}