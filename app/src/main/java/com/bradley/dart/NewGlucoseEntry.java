package com.bradley.dart;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Toast;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;
import java.util.TimeZone;
import java.util.Calendar;
import android.app.DatePickerDialog;
import android.widget.TextView;

import com.bradley.dart.utils.DataEntry;
import com.bradley.dart.utils.FileIO;
import com.bradley.dart.utils.Resident;
import com.bradley.dart.utils.Village;

import java.util.ArrayList;
import java.util.Locale;
import java.util.UUID;

public class NewGlucoseEntry extends AppCompatActivity {
    EditText glucoseEntry;
    EditText notesEntry;
    ImageButton back;
    ImageButton saveGlucoseEntry;
    private ImageButton pickDateBtn;
    private TextView selectedDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_data_entry);

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
        glucoseEntry = findViewById(R.id.glucose_edit_text);
        notesEntry = findViewById(R.id.notes_edit_text);

        back = findViewById(R.id.back_button);
        saveGlucoseEntry = findViewById(R.id.save_data_button);

        pickDateBtn = findViewById(R.id.pickDateBtn);
        selectedDate = findViewById(R.id.selectedDate);

        final Calendar calendar = Calendar.getInstance();
        pickDateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int year = calendar.get(Calendar.YEAR);
                int month = calendar.get(Calendar.MONTH);
                int day = calendar.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog datePickerDialog = new DatePickerDialog(
                        NewGlucoseEntry.this,
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
                Intent entryViewer = new Intent(NewGlucoseEntry.this, AllEntries.class);

                Intent intent = getIntent();
                Bundle ExtrasStuff = intent.getExtras();
                Bundle extra = new Bundle();
                extra.putInt("villagePosition", ExtrasStuff.getInt("villagePosition", 0));
                extra.putString("residentPosition", ExtrasStuff.getString("residentPosition"));
                entryViewer.putExtra("things",extra);
                startActivity(entryViewer);

                finish();
            }
        });

        saveGlucoseEntry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onSubmit(villages);

                Intent entryViewer = new Intent(NewGlucoseEntry.this, AllEntries.class);

                Intent intent = getIntent();
                Bundle ExtrasStuff = intent.getExtras();
                Bundle extra = new Bundle();
                extra.putInt("villagePosition", ExtrasStuff.getInt("villagePosition", 0));
                extra.putString("residentPosition", ExtrasStuff.getString("residentPosition"));
                entryViewer.putExtra("things",extra);
                startActivity(entryViewer);

                finish();
            }
        });
    }

    public void onSubmit(ArrayList<Village> villages){
        //String dataEntryText = dateEntry.getText().toString().trim();
        String dataEntryText = selectedDate.getText().toString().trim();
        String glucoseEntryText = glucoseEntry.getText().toString().trim();
        float glucoseLevel = Float.parseFloat(glucoseEntryText);
        String notesEntryText = notesEntry.getText().toString().trim();

        if (dataEntryText.isEmpty() || glucoseEntryText.isEmpty() || selectedDate.getText().toString().equals("Select Date")){
            Toast.makeText(this, "All fields are required", Toast.LENGTH_SHORT).show();
            return;
        }

        DataEntry data = new DataEntry(glucoseLevel, notesEntryText, dataEntryText);

        Intent intent = getIntent();
        Bundle ExtrasStuff = intent.getExtras();
        Village villa = villages.get(ExtrasStuff.getInt("villagePosition", 0));
        String person = ExtrasStuff.getString("residentPosition");

        DartApplication app = (DartApplication) getApplication();

        Resident resident = villa.getResidents().get(0);

        for (int i = 0; i < villa.getResidents().size(); i++){
            if (Objects.equals(villa.getResidents().get(i).getPatientID(), person)){
                resident = villa.getResidents().get(i);
                break;
            }
        }
        app.addEntry(villa, resident, data);

    }
}
