package com.appi.countdowntimerexample;

import androidx.appcompat.app.AppCompatActivity;


import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private EditText mEditTextInput ;
    private TextView mTextViewCountDown;
    private Button  mButtonSet ;
    private Button mButtonStartPause;
    private Button mButtonReset;
    private CountDownTimer mCountDownTimer;

    private boolean mTimerRunning;

    private  long mStartTimeInMillis ;
    private long mTimeLeftInMillis ;
    private long mENDTime;



    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mEditTextInput = findViewById(R.id.edit_text_input);
        mTextViewCountDown = findViewById(R.id.text_view_countdown);


        mButtonSet = findViewById(R.id.button_set);
        mButtonStartPause = findViewById(R.id.button_start_pause);
        mButtonReset = findViewById(R.id.button_reset);

        mButtonSet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                 String input = mEditTextInput.getText().toString();
                 if(input.length()== 0){
                     Toast.makeText(MainActivity.this, " Field can't b empty", Toast.LENGTH_SHORT).show();
                     return;
                 }

                 long millisInput = Long.parseLong(input)* 60000 ;
                 if(millisInput == 0 ){
                     Toast.makeText(MainActivity.this, "please enter a positive number", Toast.LENGTH_SHORT).show();
                     return;
                 }
                 setTime(millisInput);
                 mEditTextInput.setText("");
            }
        });
        mButtonStartPause.setOnClickListener(new View.OnClickListener() {
            public void onClick(View V) {
                if (mTimerRunning) {
                    pauseTimer();

                } else {
                    startTimer();
                }


            }




        });

        mButtonReset.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
                resetTimer();

            }
        });


    }

     private void setTime(long milliseconds){
        mStartTimeInMillis = milliseconds ;
        resetTimer();
        closeKeyboard();
     }

    private void startTimer() {
        mENDTime= System.currentTimeMillis()+ mTimeLeftInMillis ;


        mCountDownTimer = new CountDownTimer(mTimeLeftInMillis, 1000) {

            public void onTick(long millisUntilFinished) {
                mTimeLeftInMillis = millisUntilFinished;
                updateCountDownText();


            }


            public void onFinish() {
                mTimerRunning = false;
               updateWatchingInterface();
            }
        }.start();
        mTimerRunning = true;
        updateWatchingInterface();
    }
    private void pauseTimer() {
        mCountDownTimer.cancel();
        mTimerRunning = false;
        updateWatchingInterface();
    }




        private void resetTimer(){
            mTimeLeftInMillis = mStartTimeInMillis;
            updateCountDownText();
            updateWatchingInterface();

        }
        private void updateCountDownText(){
            int hours = (int) (mTimeLeftInMillis/1000)/3600;
            int minutes = (int) ((mTimeLeftInMillis / 1000) %3600)/ 60;
            int seconds = (int) (mTimeLeftInMillis / 1000) % 60;
            String timeLeftFormatted ;
             if(hours>0){
                 timeLeftFormatted = String.format(Locale.getDefault(), " %d:%02d : %02d ", hours, minutes, seconds);
             } else {
                 timeLeftFormatted = String.format(Locale.getDefault(), "%02d : %02d " , minutes, seconds);

             }



            mTextViewCountDown.setText(timeLeftFormatted);

        }

        private void updateWatchingInterface(){
           if(mTimerRunning){
               mEditTextInput.setVisibility(View.INVISIBLE);
               mButtonSet.setVisibility(View.INVISIBLE);
               mButtonReset.setVisibility(View.INVISIBLE);
               mButtonStartPause.setText("Pause");

           }else {
               mEditTextInput.setVisibility(View.VISIBLE);
               mButtonSet.setVisibility(View.VISIBLE);
               mButtonStartPause.setText("Start");

               if (mTimeLeftInMillis < 1000){
                   mButtonStartPause.setVisibility(View.INVISIBLE);

               }
               else {
                   mButtonStartPause.setVisibility(View.VISIBLE);
               }

               if(mTimeLeftInMillis < mStartTimeInMillis){
                   mButtonReset.setVisibility(View.VISIBLE);
               }
               else{
                   mButtonReset.setVisibility(View.INVISIBLE);
               }
           }
        }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putLong("millisLeft", mTimeLeftInMillis);
        outState.putBoolean("timerRunning", mTimerRunning);
        outState.putLong("endTime", mENDTime);
    }

    private void closeKeyboard(){
        View view = this.getCurrentFocus();
        if (view != null ){
            InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(),0);

        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        SharedPreferences preferences = getSharedPreferences("prefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();

        editor.putLong("startTimeInMillis", mStartTimeInMillis);
        editor.putLong("millisLeft", mTimeLeftInMillis);
        editor.putBoolean("timerRunning", mTimerRunning);
        editor.putLong("endTime",mENDTime);

        editor.apply();
        if (mCountDownTimer!=null){
            mCountDownTimer.cancel();

        }


    }

    @Override
    protected void onStart() {
        super.onStart();
        SharedPreferences preferences = getSharedPreferences("prefs",MODE_PRIVATE);

        mStartTimeInMillis = preferences.getLong("startTimeInMillis",600000);
        mTimeLeftInMillis = preferences.getLong("millisLeft", mStartTimeInMillis);
        mTimerRunning = preferences.getBoolean("timerRunning", false);

        updateCountDownText();
        updateWatchingInterface();

        if(mTimerRunning){
            mENDTime = preferences.getLong("endTime",0);
            mTimeLeftInMillis = mENDTime- System.currentTimeMillis();
            if(mTimeLeftInMillis<0){
                mTimeLeftInMillis = 0 ;
                mTimerRunning = false ;
                updateCountDownText();
                updateWatchingInterface();
            } else {
                startTimer();
            }
        }
    }
}
