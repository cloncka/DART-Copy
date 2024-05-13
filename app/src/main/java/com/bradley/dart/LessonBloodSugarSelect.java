package com.bradley.dart;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
//this page handles the lesson selection process.
//it has 2 buttons which are used to select the lesson to dispaly
//the lessons are intended to be visual aids for health workers.
public class LessonBloodSugarSelect extends AppCompatActivity {


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.bad_sugar_select);


        final ImageButton gohome = findViewById(R.id.btnreturnlessonselect1);

        gohome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


     final ImageButton low = findViewById(R.id.imageButtonlowsugar);

     low.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {
             Intent learning = new Intent(LessonBloodSugarSelect.this, LessonLowSugarSelect.class);
             startActivity(learning);
         }
     });

        final ImageButton high = findViewById(R.id.Buttonhighsugar);

        high.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent learning2 = new Intent(LessonBloodSugarSelect.this, LessonHighSugarSelect.class);
                startActivity(learning2);
            }
        });

    }
}
