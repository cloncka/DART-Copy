package com.bradley.dart;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.bradley.dart.utils.DataEntry;
import com.bradley.dart.utils.FileIO;
import com.bradley.dart.utils.Resident;
import com.bradley.dart.utils.Village;

import java.util.ArrayList;
//Since this is the first class,
// I'll explain everything in a bit more detail

//all activities must be declared in the manifest first to be accessible within the app.
//afterward, at the start of the class/activity, you have the declaration below
//the class must extend the activity class to do anything to the screen.
//Appcompactactivity is simply another version of the activity class for low resource applications.
public class AllEntries extends AppCompatActivity  {

    //on create tells the app what to do when the class is opened.
    //The bundle it takes in is used for passing data from other classes
    protected void onCreate(Bundle savedInstanceState) {
        //super.oncreate ALWAYS must be the first line in the oncreate function.
        //savedInstance grabs the overall app context so everything from the previous page is saved.
        super.onCreate(savedInstanceState);
        //this gets the Dartapplication class and stores it in an object.
        //The methods from the DartApplication class can be called through this.
        DartApplication app = (DartApplication) getApplication();


        //this iff/else  gets all villages from the app and loads them in.
        //this is to ensure the app's data is loaded before anything else loads
        app.getVillages(new FileIO.OnVillagesLoadedListener() {
            @Override
            public void onSuccess(ArrayList<Village> result) {
                System.out.println(result);
                //if the villages are retrieved successfully, start another function.
                showpatients(result);


            }

            @Override
            public void onFailure(Exception e) {
                e.printStackTrace();
            }
        });


    }

    private void showpatients(ArrayList<Village> result) {
        //this sets the content on the screen to the items in the layout listed below.
        //you can go to the layout in res/layout to see the layout without any info.
        setContentView(R.layout.activity_patients_entries_all);

        //these lines grab the buttons on the page and stores them as objects
        final ImageButton exit = findViewById(R.id.back_button_entries);

        final ImageButton addentry = findViewById(R.id.add_entry);

        //this grabs the current intent in the app.
        //When passing data from one class to another, to avoid global variables,
        //items need to be placed into an intent as an extra.
        // This retrieves the intent so those items can be read by this class.
        Intent current = getIntent();

        //this grabs all extra data passed through the intent and stores it in
        // a "Bundle", which acts as a collection of different variables.
        // "things" is the name of the bundle that was passed to the intent.
        Bundle Extrasstuff = current.getBundleExtra("things");

        //this takes the bundle and grabs an int variable named "villageposition"
        //from it. If one isn't present, it defaults to 0, or the first village in the array.
        current.getIntExtra("villagePosition", 0);

        //Alternatively, the bundles can be read like this. This code creates
        //a new village object and takes result, or the array of villages,
        //and gets the item in the position of the variable seen above.
        //to see more on extras and sending data, please see the bottom of this class.
        Village villa = result.get(Extrasstuff.getInt("villagePosition", 0));

        //this grabs the list of residents from the current village.
        ArrayList<Resident> people = villa.getResidents();

        //this creates a dummy variable for a person to be used below
        Resident person = villa.getResidents().get(0);

        //this for loop loops through the people in the village, and
        //stores the person that has an id that matches the extra that was
        //passed to this class. If no such person exists, it defaults to the
        //first person in the village.
        for(int i = 0; i < people.size(); i++){
            if(people.get(i).getPatientID().equals(Extrasstuff.getString("residentPosition"))){
                person = people.get(i);
                break;
            }

        }
        //this stores the name of the person grabbed above.
        String name = person.getName();

        //this strores the textview object currently on screen.
        TextView displayname = findViewById(R.id.PatientnameDisplayEntries);

        //this sets the content of the above textview to the name from the last line.
        displayname.setText((CharSequence) name);

        //this creates an arraylist of all data entries for the current person.
        ArrayList<DataEntry> entries = person.getEntries();

        //this stores the data for each entry.
        String dates[] = new String[entries.size()];
        String Glucose[] = new String[entries.size()];

        //this fills the arrays above with each data's entry
        //each i corresponds to 1 entry
        for(int i = 0; i < entries.size(); i++){
            dates[i] = entries.get(i).getRecordedOn();
            float sugar = (entries.get(i).getBloodSugar());
            Glucose[i] = String.valueOf(sugar);

        }




        //this grabs the list view on screen.
        final ListView Entries = findViewById(R.id.ListViewInsideEntries2);

        //this creates an instance of the EntryViewAdapter class
        EntryViewAdapter lviewAdapter = new EntryViewAdapter(this, dates, Glucose);

        //this takes the list view above and set's its contents to the
        //adapter. Please see the adapter for more details.
        Entries.setAdapter(lviewAdapter);

        //this would handle the logic for clickling on an entry in the listview,
        //but nothing happens here.
        Entries.setOnItemClickListener(

                new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> arg0, View view,
                                            int position, long id) {


                    }

                }
        );

        //this handles the logic for the exit button.
        //onclicklistener does something when a button is pressed.
        //in this case, it finishes, or closes the current class
        //it then sets the screen to how it was before this class as well.
        exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


        //for some reason, android studio sometimes requires variables to be final
        //to be used in onclick functions. This assigns the current resident to a final variable.
        Resident finalPerson = person;

        //this handles the functionality of adding a patient.
        addentry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //This intent declares how we want the screen to change. In this case,
                //we want the class to change from this one to the NewGlucoseEntry class
                //as the user hit the add an entry button.
                Intent newEntry = new Intent(AllEntries.this, NewGlucoseEntry.class);

                //we create a bundle to store a variety of variables.
                Bundle extra = new Bundle();

                //we then grab the current patient's village and put it in the bundle.
                extra.putInt("villagePosition", Extrasstuff.getInt("villagePosition", 0));

                //we also take the id of the resident and put it in the bundle.
                extra.putString("residentPosition", finalPerson.getPatientID());

                //from here, we take the intent and put the bundle into the intent.
                //this will allow these variables to be unpacked in the NewGlucoseEntry
                //activity in a similar way to how the extras were unpacked above.
                newEntry.putExtras(extra);

                //this starts the intent, loading the new activity and it's oncreate function.
                startActivity(newEntry);

                //we call finish here to close the current activity.
                //by finishing here, we end this activity when adding a new entry.
                //by re-opening this activity when we leave the new entry activity,
                //we can update the screen immediately when a new patient is added.
                //you only need to do this if something needs to update very quickly after being added.
                finish();
            }
        });
    }
}
