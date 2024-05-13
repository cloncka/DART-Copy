package com.bradley.dart;
//this is the actual code for a lesson
import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
public class LessonFoodchoice extends AppCompatActivity {

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //set the content on the screen
        setContentView(R.layout.lesson_food);

        //create the back button
        final ImageButton gohome = findViewById(R.id.btnreturnlessonmain1);

        gohome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        //create an int to store the current image
        final int[] position = {0};
        //grab the image displayed on screen.
        final ImageView x = findViewById(R.id.imageViewlesson3);


        //whenever the image is pressed, go to the next image.
        //loop back to the start if all images have been displayed
        x.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                position[0]++;
                if(position[0] > 16){

                    position[0] = 0;
                }
                if(position[0] == 0){
                    x.setImageDrawable(getResources().getDrawable(R.drawable.three_one));
                }

                if(position[0] == 1){
                    x.setImageDrawable(getResources().getDrawable(R.drawable.three_two));
                }

                if(position[0] == 2){
                    x.setImageDrawable(getResources().getDrawable(R.drawable.three_three));
                }
                if(position[0] == 3){
                    x.setImageDrawable(getResources().getDrawable(R.drawable.three_four));
                }
                if(position[0] == 4){
                    x.setImageDrawable(getResources().getDrawable(R.drawable.three_five));
                }
                if(position[0] == 5){
                    x.setImageDrawable(getResources().getDrawable(R.drawable.three_six));
                }
                if(position[0] == 6){
                    x.setImageDrawable(getResources().getDrawable(R.drawable.three_seven));
                }
                if(position[0] == 7){
                    x.setImageDrawable(getResources().getDrawable(R.drawable.three_eight));
                }
                if(position[0] == 8){
                    x.setImageDrawable(getResources().getDrawable(R.drawable.three_nine));
                }
                if(position[0] == 9){
                    x.setImageDrawable(getResources().getDrawable(R.drawable.three_ten));
                }
                if(position[0] == 10){
                    x.setImageDrawable(getResources().getDrawable(R.drawable.three_eleven));
                }
                if(position[0] == 11){
                    x.setImageDrawable(getResources().getDrawable(R.drawable.three_twelve));
                }
                if(position[0] == 12){
                    x.setImageDrawable(getResources().getDrawable(R.drawable.three_thirteen));
                }
                if(position[0] == 13){
                    x.setImageDrawable(getResources().getDrawable(R.drawable.three_fourteen));
                }
                if(position[0] == 14){
                    x.setImageDrawable(getResources().getDrawable(R.drawable.three_fifteen));
                }
                if(position[0] == 15){
                    x.setImageDrawable(getResources().getDrawable(R.drawable.three_sixteen));
                }
                if(position[0] == 16){
                    x.setImageDrawable(getResources().getDrawable(R.drawable.three_seventeen));
                }
            }
        });





    }
}
