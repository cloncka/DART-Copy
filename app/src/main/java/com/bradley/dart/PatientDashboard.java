package com.bradley.dart;


import static org.bouncycastle.oer.its.EndEntityType.app;


import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import com.bradley.dart.utils.DataEntry;
import com.bradley.dart.utils.FileIO;
import com.bradley.dart.utils.Resident;
import com.bradley.dart.utils.Village;
import com.google.android.material.navigation.NavigationBarView;
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
//the main class for displaying patient data


public class PatientDashboard extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemSelectedListener {

    @Override

    protected void onCreate(Bundle savedInstanceState){

        super.onCreate(savedInstanceState);

        DartApplication app = (DartApplication) getApplication();
        app.getVillages(new FileIO.OnVillagesLoadedListener() {
            @Override
            public void onSuccess(ArrayList<Village> result) {
                System.out.println(result);

                spincreate(result);


            }

            @Override
            public void onFailure(Exception e) {
                e.printStackTrace();
            }
        });




    }
//create the dropdown spinner for picking a village
    private void spincreate(ArrayList<Village> result) {
        setContentView(R.layout.patient_dashboard);
        //only the back button and the new village buttons are enable here
        //so that you can't do something like add a patient when there are no villages
        //as the spinner won't finish creating if there are no villages.

        final ImageButton exit = findViewById(R.id.btnreturnHome);

        exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        final ImageButton Villageadder = findViewById(R.id.btnaddVillage);
        Villageadder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(PatientDashboard.this, NewVillageEntry.class));
                finish();
            }
        });


        //create the spinner, which acts as a dropdown list.
        final Spinner villages = findViewById(R.id.spinner);

        String village_names[] = new String[result.size()];


        for(int i = 0; i < result.size(); i++){
            village_names[i] = (result.get(i).getName());

        }
        //adapters are needed to add arrarys to certain types of views, such as
        //spinners. Here, the adapter adds the array of village names to the spinner.
        ArrayAdapter ad
                = new ArrayAdapter(
                this,
                android.R.layout.simple_spinner_item,
                village_names);
        // Specify the layout to use when the list of choices appears.
        ad.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner.
        villages.setAdapter(ad);
        villages.setOnItemSelectedListener(this);
        //create the logic for when a village is selected (see bottom of page)
    }

//create the rest of the page. the parameters are the list of villages, the current village,
    //and any string used to search for patients.
    private void dashboard(ArrayList<Village> village_list, int placement, String search) {
        //get the current village and store it.
        int current_village = placement;

        //store the buttons and the search box so they can be defined below.
        final ImageButton export = findViewById(R.id.btnexportData);

        final ImageButton add = findViewById(R.id.btnaddPatient);


        final EditText textinput = findViewById(R.id.textInputEditText);

        final ImageButton go = findViewById(R.id.gosearch);

        Village currentvilla = village_list.get(current_village);
        //get the list of people from the current village.
        ArrayList<Resident> people = currentvilla.getResidents();



        //create arrays to hold the data for each resident
        ArrayList<String> patient = new ArrayList<String>();
        ArrayList<String> IDs = new ArrayList<String>();
        ArrayList<String> dates = new ArrayList<String>();




        //grab the data for each resident and store the data in arrays.
        for(int i = 0; i < people.size(); i++){
            boolean include = true;
            Resident person = people.get(i);
            for(int j = 0; j < search.length(); j++){
                if(search.charAt(j) != person.getName().charAt(j)){
                    include = false;

                }
            }
            //make sure the patient matches the search box criteria before storing them.
            if(include) {
                patient.add(person.getName());
                IDs.add(person.getPatientID());
                Date day = person.getBirthdayDate();
                dates.add(day.toString());
            }

        }




        String listofnames[] = patient.toArray(new String[patient.size()]);
        String listofIDs[] =  IDs.toArray(new String[patient.size()]);
        String listofDates[] =  dates.toArray(new String[patient.size()]);

        //grab the linear layout on screen and set its adapter to the patientviewadapter class.
        final ListView names = findViewById(R.id.listViewPatients);
        PatientViewAdapter lviewAdapter = new PatientViewAdapter(this, listofnames, listofIDs, listofDates);


        names.setAdapter(lviewAdapter);

        //do something when an item in the linearlayout is clicked.
        names.setOnItemClickListener(

                new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> arg0, View view,
                                            int position, long id) {

                       //create the intent to show the entries of the patient
                        Intent entryViewer = new Intent(PatientDashboard.this, AllEntries.class);

                        //grab the id of the current patient.
                        //id from OnItemClick is the id of the item textview in the layout,
                        //not the id of the patient.
                        //We have to get the patient this way instead of just grabbing a position
                        //because the search box can change positions of patients in the layout.
                        String ID = listofIDs[position];

                        ArrayList<Resident> people = currentvilla.getResidents();

                        Resident resident = people.get(position);
                        for(int i = 0; i < people.size(); i++){
                            if(people.get(i).getPatientID().equals(ID)){
                                resident = people.get(i);
                                break;
                            }

                        }

                        //pass the extras to the intent and start the new activity.
                        Bundle extra = new Bundle();
                        extra.putInt("villagePosition", placement);
                        extra.putString("residentPosition", resident.getPatientID());
                        entryViewer.putExtra("things",extra);
                        startActivity(entryViewer);
                    }

                }
        );

        export.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //this is the same screen change as others, replace this with the correct screen once
                //more is implemented
                Intent Exportal = new Intent(PatientDashboard.this, ExportPatientDashboard.class);
                startActivity(Exportal);
            }
        });
        //handle adding a new patient
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(PatientDashboard.this, NewDataEntryActivity.class));
                finish();
            }
        });

        //if anything is entered into the search bar, restart this function with that.
        go.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String text = textinput.getText().toString();
                if(text != search){
                    dashboard(village_list, placement, text);

                }


            }
        });




    }

    @Override
    public void onClick(View v) {

    }

//handles the logic for spinner item selection
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

        DartApplication app = (DartApplication) getApplication();

        app.getVillages(new FileIO.OnVillagesLoadedListener() {
            @Override
            public void onSuccess(ArrayList<Village> result) {
                //once the village is successfully obtained, create the rest of the page
                //with the dashboard function above. Additionally,
                //re-call that function with the new village if the item in the spinner is changed.
                //This defaults to the first village when the class is loaded.
                System.out.println(result);
                dashboard(result, position, "");

            }

            @Override
            public void onFailure(Exception e) {
                e.printStackTrace();
            }
        });



    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
