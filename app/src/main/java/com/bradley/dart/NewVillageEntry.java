package com.bradley.dart;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;

import com.bradley.dart.utils.FileIO;
import com.bradley.dart.utils.Resident;
import com.bradley.dart.utils.Village;

import java.util.ArrayList;
//this page handles the logic for adding a new village.
//importantly, this page assumes you came from the patientdashboard.

public class NewVillageEntry extends AppCompatActivity{

    protected void onCreate(Bundle savedInstanceState){

        super.onCreate(savedInstanceState);

        setContentView(R.layout.new_village_entry);

        DartApplication app = (DartApplication) getApplication();
        app.getVillages(new FileIO.OnVillagesLoadedListener() {
            @Override
            public void onSuccess(ArrayList<Village> result) {
                System.out.println(result);


                final ImageButton Save = findViewById(R.id.btnSaveaddvillage);

                final EditText name = findViewById(R.id.promtentryaddvillage);

                final ImageButton back = findViewById(R.id.btnBackaddvillage);

                Save.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String villaname = name.getText().toString();

                        if(villaname != "") {
                            ArrayList<Resident> people = new ArrayList<Resident>();

                            int id = result.size();

                            String x = "" + id;

                            Village newvilla = new Village(villaname, people);

                            app.addVillage(newvilla);

                            startActivity(new Intent(NewVillageEntry.this, PatientDashboard.class));
                            finish();

                        }
                    }


                });


                back.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startActivity(new Intent(NewVillageEntry.this, PatientDashboard.class));
                        finish();
                    }
                });


            }

            @Override
            public void onFailure(Exception e) {
                e.printStackTrace();
            }
        });





    }

}
