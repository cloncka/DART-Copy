package com.bradley.dart;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
public class LessonHighSugarSelect extends AppCompatActivity {

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.lesson_high_numbers);


        final ImageButton gohome = findViewById(R.id.btnreturnlessonssugarselect2);

        gohome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();}
        });

        final int[] position = {0};

        final ImageView x = findViewById(R.id.imageViewlesson2);

        Drawable listofimages[] = new Drawable[10];

        listofimages[0] = getResources().getDrawable(R.drawable.two_one);
        listofimages[1] = getResources().getDrawable(R.drawable.two_two);
        listofimages[2] = getResources().getDrawable(R.drawable.two_three);
        listofimages[3] = getResources().getDrawable(R.drawable.two_four);
        listofimages[4] = getResources().getDrawable(R.drawable.two_five);
        listofimages[5] = getResources().getDrawable(R.drawable.two_six);
        listofimages[6] = getResources().getDrawable(R.drawable.two_seven);
        listofimages[7] = getResources().getDrawable(R.drawable.two_eight);
        listofimages[8] = getResources().getDrawable(R.drawable.two_nine);
        listofimages[9] = getResources().getDrawable(R.drawable.two_ten);




        x.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                position[0]++;
                if(position[0] > 9){

                    position[0] = 0;
                }
                x.setImageDrawable(listofimages[position[0]]);
            }
        });




    }
}
