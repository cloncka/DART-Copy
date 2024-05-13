package com.bradley.dart;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
//some lessons are small enough to store all images in the class at once, making loading faster.
//if this is a problem, the logic here can be chagned to be like in lessonfoodchoice.
public class Lessonsleep extends AppCompatActivity {

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.lesson_sleep);


        final ImageButton gohome = findViewById(R.id.btnreturnmainlessons);

        gohome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();}
        });

        final int[] position = {0};

        final ImageView x = findViewById(R.id.imageViewlesson4);


        Drawable[] listofimages = new Drawable[10];

        listofimages[0] = getResources().getDrawable(R.drawable.four_one);
        listofimages[1] = getResources().getDrawable(R.drawable.four_two);
        listofimages[2] = getResources().getDrawable(R.drawable.four_three);
        listofimages[3] = getResources().getDrawable(R.drawable.four_four);
        listofimages[4] = getResources().getDrawable(R.drawable.four_five);
        listofimages[5] = getResources().getDrawable(R.drawable.four_six);
        listofimages[6] = getResources().getDrawable(R.drawable.four_seven);





        x.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {



                position[0]++;
                if(position[0] > 6){

                    position[0] = 0;
                }
                x.setImageDrawable(listofimages[position[0]]);
            }


        });




    }
}
