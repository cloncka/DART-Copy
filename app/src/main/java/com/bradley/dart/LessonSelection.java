package com.bradley.dart;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;

//this page works like lessonbloodsugarselect, but it has 3 buttons for 3 lessons

public class LessonSelection extends AppCompatActivity {


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.lesson_select_page);


        final ImageButton gohome = findViewById(R.id.back_button_lessons);

        gohome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


        final ImageButton Bloodsugar= findViewById(R.id.bloodsug);

        Bloodsugar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LessonSelection.this, LessonBloodSugarSelect.class));
            }
        });


        final ImageButton foodchoice = findViewById(R.id.food_choice);

        foodchoice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LessonSelection.this, LessonFoodchoice.class));
            }
        });


        final ImageButton sleep = findViewById(R.id.imagelessonsleep);


        sleep.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LessonSelection.this, Lessonsleep.class));
            }
        });



    }

}
