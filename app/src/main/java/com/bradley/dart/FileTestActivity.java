package com.bradley.dart;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.bradley.dart.utils.FileIO;
import com.bradley.dart.utils.Village;

import java.util.ArrayList;

public class FileTestActivity extends AppCompatActivity {
    private ArrayList<Village> villages;

//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_file_test);
//        Button btnSave = findViewById(R.id.btnSave);
//        Button btnLoad = (Button) findViewById(R.id.btnLoad);
//
//        btnLoad.setOnClickListener(view -> {
////            FileIO.getInstance(getApplicationContext(), R.raw.privatekey, R.raw.publickey).loadVillagesAsync(new FileIO.OnVillagesLoadedListener() {
////                @Override
////                public void onSuccess(ArrayList<Village> villages) {
////                    setVillages(villages);
////                }
////
////                @Override
////                public void onFailure(Exception e) {
////                    e.printStackTrace();
////                }
////            });
//            DartApplication app = (DartApplication) getApplication();
//            ArrayList<Village> villages = app.getVillages();
//            Toast toast = Toast.makeText(this, "" + villages.get(0).getResidents().get(0).getAddress(), Toast.LENGTH_SHORT);
//            toast.show();
//            System.out.println(app.getVillages().get(0).getResidents().get(0).getEntries().get(0).getRecordedOn());
//            FileIO.getInstance(getApplicationContext(), R.raw.privatekey, R.raw.publickey).loadVillagesAsync(new FileIO.OnVillagesLoadedListener() {
//                @Override
//                public void onSuccess(ArrayList<Village> villages) {
//                    setVillages(villages);
//                }
//
//                @Override
//                public void onFailure(Exception e) {
//                    e.printStackTrace();
//                }
//            });

            /*DartApplication app = (DartApplication) getApplication();
            ArrayList<Village> villages = app.getVillages();
            Toast toast = Toast.makeText(this, "" + villages.get(0).getResidents().get(0).getAddress(), Toast.LENGTH_SHORT);
            toast.show();
            System.out.println(app.getVillages().get(0).getResidents().get(0).getEntries().get(0).getRecordedOn());
            FileIO.getInstance(getApplicationContext(), R.raw.privatekey, R.raw.publickey).loadVillagesAsync(new FileIO.OnVillagesLoadedListener() {
                @Override
                public void onSuccess(ArrayList<Village> villages) {
                    setVillages(villages);
                }

                @Override
                public void onFailure(Exception e) {
                    e.printStackTrace();
                }
            });
//            System.out.println(((DartApplication) getApplicationContext()).getVillages());
        });

        btnSave.setOnClickListener(view -> {
            FileIO.getInstance(getApplicationContext(), R.raw.privatekey, R.raw.publickey).saveVillagesAsync(villages, new FileIO.OnSaveVillagesListener() {
                @Override
                public void onSuccess(Void result) {
                    System.out.println("Villages saved successfully");
                }

                @Override
                public void onFailure(Exception e) {
                    e.printStackTrace();
                }
            });*/

//        });
//    }



    protected void setVillages(ArrayList<Village> villages) {
        this.villages = villages;
        System.out.println(villages);
    }
}