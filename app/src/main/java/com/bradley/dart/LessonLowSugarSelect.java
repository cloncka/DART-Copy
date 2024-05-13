package com.bradley.dart;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

public class LessonLowSugarSelect extends AppCompatActivity {

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.lesson_low_numbers);


        final ImageButton gohome = findViewById(R.id.btnreturnlessonssugarselect);

        gohome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        final int[] position = {0};

          final ImageView x = findViewById(R.id.imageViewlesson1);



          x.setOnClickListener(new View.OnClickListener() {
              @Override
              public void onClick(View v) {
                  position[0]++;
                  if(position[0] > 12){

                      position[0] = 0;
                  }

                  if(position[0] == 0){
                      x.setImageDrawable(getResources().getDrawable(R.drawable.one_start));
                  }

                  if(position[0] == 1){
                      x.setImageDrawable(getResources().getDrawable(R.drawable.one_two));
                  }

                  if(position[0] == 2){
                      x.setImageDrawable(getResources().getDrawable(R.drawable.one_three));
                  }
                  if(position[0] == 3){
                      x.setImageDrawable(getResources().getDrawable(R.drawable.one_four));
                  }
                  if(position[0] == 4){
                      x.setImageDrawable(getResources().getDrawable(R.drawable.one_five));
                  }
                  if(position[0] == 5){
                      x.setImageDrawable(getResources().getDrawable(R.drawable.one_six));
                  }
                  if(position[0] == 6){
                      x.setImageDrawable(getResources().getDrawable(R.drawable.one_seven));
                  }
                  if(position[0] == 7){
                      x.setImageDrawable(getResources().getDrawable(R.drawable.one_eight));
                  }
                  if(position[0] == 8){
                      x.setImageDrawable(getResources().getDrawable(R.drawable.one_nine));
                  }
                  if(position[0] == 9){
                      x.setImageDrawable(getResources().getDrawable(R.drawable.one_ten));
                  }
                  if(position[0] == 10){
                      x.setImageDrawable(getResources().getDrawable(R.drawable.one_eleven));
                  }
                  if(position[0] == 11){
                      x.setImageDrawable(getResources().getDrawable(R.drawable.one_twelve));
                  }
                  if(position[0] == 12){
                      x.setImageDrawable(getResources().getDrawable(R.drawable.one_thirteen));
                  }








              }
          });





    }
}
