package com.bradley.dart;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

public class EditPatientDataActivity extends AppCompatActivity {

    //this activity ended up out of scope, it originally was meant to
    //allow a user to enter text into textviews and would save the inputs
    //to the current user
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_patient_data);
    }
}
