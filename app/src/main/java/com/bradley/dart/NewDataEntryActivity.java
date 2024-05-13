package com.bradley.dart;


import androidx.appcompat.app.AppCompatActivity;

import java.util.Calendar;
import java.util.TimeZone;
import java.text.SimpleDateFormat;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.view.Menu;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Toast;

import com.bradley.dart.utils.DataEntry;
import com.bradley.dart.utils.FileIO;
import com.bradley.dart.utils.Resident;
import com.bradley.dart.utils.Village;

import java.util.ArrayList;
import java.util.UUID;

public class NewDataEntryActivity extends AppCompatActivity {

    EditText firstName;
    EditText lastName;
    EditText userID;
    EditText address;
    EditText birthday;
    EditText clinic;
    ImageButton back;
    ImageButton saveResident;
    Spinner villageDropdown;
    private ImageButton pickDateBtn;
    private TextView selectedDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_data_entry);

        DartApplication app = (DartApplication) getApplication();
        app.getVillages(new FileIO.OnVillagesLoadedListener() {
            @Override
            public void onSuccess(ArrayList<Village> result) {
                onVillagesLoaded(result);
            }

            @Override
            public void onFailure(Exception e) {
                e.printStackTrace();
            }
        });
    }

    public void onVillagesLoaded(ArrayList<Village> villages){
        villageDropdown = findViewById(R.id.villageSpinner);

        ArrayList<String> villageNames = new ArrayList<>();

        for (int i = 0; i < villages.size(); i++){
            villageNames.add(villages.get(i).getName());
        }

        ArrayAdapter ad = new ArrayAdapter(this, android.R.layout.simple_spinner_item, villageNames);

        ad.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        villageDropdown.setAdapter(ad);

        firstName = findViewById(R.id.firstNameEditText);
        lastName = findViewById(R.id.lastNameEditText);
        userID = findViewById(R.id.userIDEditText);
        address = findViewById(R.id.addressEditText);
        birthday = findViewById(R.id.dobEditText);
        clinic = findViewById(R.id.communityEditText);

        back = findViewById(R.id.btnBack);
        saveResident = findViewById(R.id.btnSave);

        pickDateBtn = findViewById(R.id.idBtnPickDate);
        selectedDate = findViewById(R.id.idSelectedDate);

        pickDateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Calendar calendar = Calendar.getInstance();
                int year = calendar.get(Calendar.YEAR);
                int month = calendar.get(Calendar.MONTH);
                int day = calendar.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog datePickerDialog = new DatePickerDialog(
                        NewDataEntryActivity.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year,
                                                  int monthOfYear, int dayOfMonth) {
                                Calendar selectedCalendar = Calendar.getInstance();
                                selectedCalendar.set(year, monthOfYear, dayOfMonth);

                                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.0'Z'");
                                sdf.setTimeZone(TimeZone.getTimeZone("UTC"));

                                selectedDate.setText(sdf.format(selectedCalendar.getTime()));
                            }
                        },
                        year, month, day);
                datePickerDialog.show();
            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(NewDataEntryActivity.this, PatientDashboard.class));
                finish();
            }
        });

        saveResident.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onSubmit(villages, villageDropdown);
                startActivity(new Intent(NewDataEntryActivity.this, PatientDashboard.class));
                finish();
            }
        });
    }


    public void onSubmit(ArrayList<Village> villages, Spinner dropdown) {
        String text = dropdown.getSelectedItem().toString();

        String firstNameText = firstName.getText().toString().trim();
        String lastNameText = lastName.getText().toString().trim();
        String addressText = address.getText().toString().trim();
        String birthdayText = selectedDate.getText().toString().trim();

        if (firstNameText.isEmpty() || lastNameText.isEmpty() || addressText.isEmpty() || birthdayText.isEmpty()){
            Toast.makeText(this, "All fields are required", Toast.LENGTH_SHORT).show();
            return;
        }

        Resident resident = new Resident(firstNameText + " " + lastNameText,
                addressText,
                birthdayText,
                new ArrayList<DataEntry>());

        DartApplication app = (DartApplication) getApplication();
        for (int i = 0; i < villages.size(); i++){
            if (villages.get(i).getName() == text){
                Village selectedVillage = villages.get(i);
                app.addResident(selectedVillage, resident);
            }
        }
    }
}