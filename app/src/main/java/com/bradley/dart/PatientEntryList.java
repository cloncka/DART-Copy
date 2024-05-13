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
//this works similarly to allentries, but only displayes recently changed data instead.

public class PatientEntryList extends AppCompatActivity {

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        DartApplication app = (DartApplication) getApplication();
        app.getNewData(new FileIO.OnVillagesLoadedListener() {
            @Override
            public void onSuccess(ArrayList<Village> result) {
                System.out.println(result);

                showpatients(result);


            }

            @Override
            public void onFailure(Exception e) {
                e.printStackTrace();
            }
        });


    }

    private void showpatients(ArrayList<Village> result) {
        setContentView(R.layout.patient_entry_list);

        final ImageButton exit = findViewById(R.id.back_button);

        Intent current = getIntent();

        Bundle Extrasstuff = current.getBundleExtra("stuffs");;

        current.getIntExtra("villagepos", 0);

        Village villa = result.get(Extrasstuff.getInt("villagepos", 0));

        Resident person = villa.getResidents().get(Extrasstuff.getInt("personpos", 0));

        String name = person.getName();

        TextView displayname = findViewById(R.id.PatientnameDisplay);

        displayname.setText((CharSequence) name);

        //String dates[] = {"11/3/2001", "11/4/2003","11/3/2201"};
        //String Glucose[] = {"3","100","70"};

        ArrayList<DataEntry> entries = person.getEntries();

        String dates[] = new String[entries.size()];
        String Glucose[] = new String[entries.size()];

        for(int i = 0; i < entries.size(); i++){
            dates[i] = entries.get(i).getRecordedOn();
            float sugar = (entries.get(i).getBloodSugar());
            Glucose[i] = String.valueOf(sugar);

        }





        final ListView Entries = findViewById(R.id.ListViewInsideEntries);
        EntryViewAdapter lviewAdapter = new EntryViewAdapter(this, dates, Glucose);


        Entries.setAdapter(lviewAdapter);

        Entries.setOnItemClickListener(

                new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> arg0, View view,
                                            int position, long id) {


                    }

                }
        );

        exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


    }
}
