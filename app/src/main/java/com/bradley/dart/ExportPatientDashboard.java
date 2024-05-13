package com.bradley.dart;

import static com.bradley.dart.utils.FileIO.getDefaultCharSet;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Spinner;

import androidx.appcompat.app.AppCompatActivity;

import com.bradley.dart.utils.DataEntry;
import com.bradley.dart.utils.EmailSender;
import com.bradley.dart.utils.FileIO;
import com.bradley.dart.utils.Resident;
import com.bradley.dart.utils.Village;
import com.bradley.dart.utils.pgp.PgpEncryptionUtil;
import com.google.gson.Gson;

import org.bouncycastle.bcpg.CompressionAlgorithmTags;
import org.bouncycastle.bcpg.SymmetricKeyAlgorithmTags;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Date;

//this dashboard works very similarly to the patient daashboard
public class ExportPatientDashboard extends AppCompatActivity implements AdapterView.OnItemSelectedListener  {
    @Override

    protected void onCreate(Bundle savedInstanceState){

        super.onCreate(savedInstanceState);
        //notably, we only get new data here instead of all data

        DartApplication app = (DartApplication) getApplication();
        app.getNewData(new FileIO.OnVillagesLoadedListener() {
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




    private void spincreate(ArrayList<Village> result) {

        setContentView(R.layout.activity_export_patient_data);

        final ImageButton exit = findViewById(R.id.back_button);

        exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


        final Spinner villages = findViewById(R.id.spinner2);

        String village_names[] = new String[result.size()];


        for(int i = 0; i < result.size(); i++){
            village_names[i] = (result.get(i).getName());

        }

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

    }

    private void export(ArrayList<Village> result, int placement) {


        final ImageButton export = findViewById(R.id.btnExportData);

        Village currentvilla = result.get(placement);

        ArrayList<Resident> people = currentvilla.getResidents();

        String patient[] = new String[people.size()];
        String IDs[] = new String[people.size()];
        String dates[] = new String[people.size()];

        for(int i = 0; i < people.size(); i++){
            Resident person = people.get(i);
            patient[i] = person.getName();
            IDs[i] = person.getPatientID();
            Date day= person.getBirthdayDate();
            dates[i] = day.toString();

        }



        final ListView names = findViewById(R.id.listViewPatients);
        PatientViewAdapter lviewAdapter = new PatientViewAdapter(this, patient, IDs, dates);

        System.out.println("adapter => "+lviewAdapter.getCount());

        names.setAdapter(lviewAdapter);

        names.setOnItemClickListener(

                new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> arg0, View view,
                                            int position, long id) {

                        Intent entryviewer = new Intent(ExportPatientDashboard.this, PatientEntryList.class);
                        Resident person = people.get(position);
                        Bundle extras = new Bundle();
                        extras.putInt("villagepos", placement);
                        extras.putInt("personpos",position);
                        entryviewer.putExtra("stuffs", extras);
                        startActivity(entryviewer);

                    }

                }
        );


//send the data in an email
        //it also wipes the newdata village to remove all recent changes from it.
        //this resets the export page.

        export.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                Thread thread = new Thread(new Runnable() {

                    @Override
                    public void run() {
                        try {
                        /*
                        FOR NOW, JUST GET ALL DATA INTO PGP FORMAT
                        IN THE FUTURE, WE'LL WANT TO ONLY SEND THE NEWLY ADDED DATA
                         */
                            DartApplication app = (DartApplication) getApplication();
                            app.getNewData(new FileIO.OnVillagesLoadedListener() {
                                @Override
                                public void onSuccess(ArrayList<Village> result) {
                                    try {
                                        System.out.println("VILLAGES RECEIVED");
                                        ArrayList<Village> villages = result;
                                        Gson gson = new Gson();
                                        final String villagesJson = gson.toJson(villages);

                                        PgpEncryptionUtil pgpEncryptionUtil = new PgpEncryptionUtil(
                                                CompressionAlgorithmTags.BZIP2,
                                                SymmetricKeyAlgorithmTags.AES_128,
                                                true,
                                                true
                                        );

                                        OutputStream outStream = new ByteArrayOutputStream();
                                        ByteArrayInputStream inStream = new ByteArrayInputStream(villagesJson.getBytes(getDefaultCharSet()));
                                        InputStream keyInStream = FileIO.instance.getPublicKey();
                                        pgpEncryptionUtil.encrypt(outStream, inStream, inStream.available(), keyInStream);

                                    /*
                                    SEND OUT THE EMAIL CONTENT
                                     */
                                        EmailSender emailSender = new EmailSender();
                                        emailSender.sendEmail(getApplicationContext(), new ByteArrayInputStream(outStream.toString().getBytes()), app.getUserId());
                                        app.saveNewData(new ArrayList<Village>()) ;
                                        finish();
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }

                                @Override
                                public void onFailure(Exception e) {
                                    e.printStackTrace();
                                }
                            });
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });

                thread.start();

            }
        });


    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        DartApplication app = (DartApplication) getApplication();

        app.getNewData(new FileIO.OnVillagesLoadedListener() {
            @Override
            public void onSuccess(ArrayList<Village> result) {
                System.out.println(result);
                export(result, position);

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
