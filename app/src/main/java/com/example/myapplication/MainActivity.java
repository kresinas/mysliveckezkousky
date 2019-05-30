package com.example.myapplication;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.support.annotation.NonNull;
import android.text.method.ScrollingMovementMethod;
import android.view.MenuItem;
import android.widget.TextView;

import java.util.Map;

public class MainActivity extends AppCompatActivity {
    public static final String CURRENT_POSITION = "currentPosition";
    public static final String MYSLIVECKEZKOUSKY = "mysliveckezkousky";
    public static final int INT = 1;
    private TextView mTextMessage;
    private Integer currentPosition = INT;
    private Map<Integer, Question> questionMap;
    private String text;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {

            switch (item.getItemId()) {
                case R.id.navigation_next:
                    if (currentPosition > INT){
                        currentPosition --;
                    }
                    show();
                    return true;
                case R.id.navigation_first:
                    currentPosition = INT;
                    show();
                    return true;
                case R.id.navigation_random:
                    currentPosition = randomWithRange(INT, questionMap.size());
                    show();
                    return true;
                case R.id.navigation_prev:
                    if (currentPosition < questionMap.size()) {
                        currentPosition++;
                    }
                    show();
                    return true;
            }
            return false;
        }
    };

    private void show() {
        updateText();
        mTextMessage.setText(text);
        mTextMessage.scrollTo(0,0);
        SharedPreferences.Editor prefsEditor = getSharedPreferences(MYSLIVECKEZKOUSKY, Context.MODE_PRIVATE).edit();
        prefsEditor.putInt(CURRENT_POSITION, this.currentPosition).commit();
    }

    private void updateText() {
        text = questionMap.get(currentPosition).toStringFormated();
    }

    int randomWithRange(int min, int max){
        int range = (max - min) + 1;
        return (int)(Math.random() * range) + min;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        BottomNavigationView navView = findViewById(R.id.nav_view);
        mTextMessage = findViewById(R.id.message);
        mTextMessage.setMovementMethod(new ScrollingMovementMethod());
        navView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        questionMap = QuestionsFactory.getQuestions();
        try {
            SharedPreferences prefs = getSharedPreferences(MYSLIVECKEZKOUSKY, Context.MODE_PRIVATE);
            this.currentPosition = prefs.getInt(CURRENT_POSITION, INT);
        } catch (Exception e){
            e.printStackTrace();
        }
        if(this.currentPosition != INT) {
            updateText();
            mTextMessage.setText(text);
        }
    }
}
