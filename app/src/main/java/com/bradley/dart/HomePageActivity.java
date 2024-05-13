package com.bradley.dart;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;

import com.bradley.dart.utils.FileIO;
import com.bradley.dart.utils.Village;

import java.util.ArrayList;
//please see Allentries for an overview of how the code works
public class HomePageActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //set the content on the page
        setContentView(R.layout.activity_homepage);

        //make sure the app can get both recently entered and all data
        DartApplication app = (DartApplication) getApplication();
        app.getVillages(new FileIO.OnVillagesLoadedListener() {
            @Override
            public void onSuccess(ArrayList<Village> result) {
                System.out.println(result);
            }

            @Override
            public void onFailure(Exception e) {
                e.printStackTrace();
            }
        });

        app.getNewData(new FileIO.OnVillagesLoadedListener() {
            @Override
            public void onSuccess(ArrayList<Village> result) {
                System.out.println("NEW DATA");
                System.out.println(result);
            }

            @Override
            public void onFailure(Exception e) {
                e.printStackTrace();
            }
        });

        //here, the buttons are assigned the ability to go to start new classes.
        final ImageButton patientList = findViewById(R.id.btnPatientData);

        patientList.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent PatientDash = new Intent(HomePageActivity.this, PatientDashboard.class);
                startActivity(PatientDash);
            }
        });


        final ImageButton lessons = findViewById(R.id.btnEducation);

        lessons.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent learning = new Intent(HomePageActivity.this, LessonSelection.class);
                startActivity(learning);
            }
        });

        final ImageButton FAQ = findViewById(R.id.btnFAQ);

        FAQ.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent help = new Intent(HomePageActivity.this, HelpPage.class);
                startActivity(help);
            }
        });
    }
}
