package com.example.myapplication;

import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;
import android.speech.tts.TextToSpeech;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.speech.tts.UtteranceProgressListener;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.support.annotation.NonNull;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

@TargetApi(Build.VERSION_CODES.DONUT)
public class MainActivity extends AppCompatActivity implements TextToSpeech.OnInitListener {
    private TextView mTextMessage;
    private TextToSpeech myTTS;
    public static final String CURRENT_POSITION = "currentPosition";
    public static final String MYSLIVECKEZKOUSKY = "mysliveckezkousky";
    public static final int STARTNUMBER = 1;
    private int MY_DATA_CHECK_CODE = 0;
    private Integer currentPosition = STARTNUMBER;
    private Map<Integer, Question> questionMap;
    private static final Locale locale = new Locale("cs", "CZ");
    private String text;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {

            switch (item.getItemId()) {
                case R.id.navigation_next:
                    if (currentPosition > STARTNUMBER) {
                        currentPosition--;
                    }
                    show();
                    return true;
                case R.id.navigation_first:
                    currentPosition = STARTNUMBER;
                    show();
                    return true;
/*
                case R.id.navigation_random:
                    currentPosition = randomWithRange(STARTNUMBER, questionMap.size());
                    show();
                    return true;
*/
                case R.id.navigation_prev:
                    if (currentPosition < questionMap.size()) {
                        currentPosition++;
                    }
                    show();
                    return true;
                case R.id.navigation_speech:
                    speakWords(text);
                    return true;
                case R.id.navigation_speech_stop:
                    myTTS.stop();
                    return true;
            }
            return false;
        }
    };

    private void show() {

        try {
            updateText();
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mTextMessage.setText(text);
                    mTextMessage.scrollTo(0, 0);
                }
            });
            savePosition();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void savePosition() {
        try {
            SharedPreferences.Editor prefsEditor = getSharedPreferences(MYSLIVECKEZKOUSKY, Context.MODE_PRIVATE).edit();
            prefsEditor.putInt(CURRENT_POSITION, this.currentPosition).commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void updateText() {
        text = questionMap.get(currentPosition).toStringFormated();
    }

    int randomWithRange(int min, int max) {
        int range = (max - min) + 1;
        return (int) (Math.random() * range) + min;
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
            this.currentPosition = prefs.getInt(CURRENT_POSITION, STARTNUMBER);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (this.currentPosition != STARTNUMBER) {
            updateText();
            mTextMessage.setText(text);
        }
        Intent checkTTSIntent = new Intent();
        checkTTSIntent.setAction(TextToSpeech.Engine.ACTION_CHECK_TTS_DATA);
        startActivityForResult(checkTTSIntent, MY_DATA_CHECK_CODE);
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    private void speakWords(String speech) {
        List<String> strings = splitEqually(speech, TextToSpeech.getMaxSpeechInputLength() - 1);

        for (String string : strings) {
//            myTTS.speak(speech, TextToSpeech.QUEUE_FLUSH, null);
            HashMap<String, String> params = new HashMap<>();
            params.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, string);
            myTTS.speak(string, TextToSpeech.QUEUE_ADD, params);
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == MY_DATA_CHECK_CODE) {
            if (resultCode == TextToSpeech.Engine.CHECK_VOICE_DATA_PASS) {
                //the user has the necessary data - create the TTS
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.DONUT) {
                    myTTS = new TextToSpeech(this, this);
                }

                if (Build.VERSION.SDK_INT >= 15) {
                    myTTS.setOnUtteranceProgressListener(new UtteranceProgressListener() {
                        @Override
                        public void onDone(String utteranceId) {
                            onDoneSpeaking(utteranceId);
                        }

                        @Override
                        public void onError(String utteranceId) {
                        }

                        @Override
                        public void onStart(String utteranceId) {
                        }
                    });
                } else {
                    Log.d("TAG", "set utternace completed listener");
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.DONUT) {
                        myTTS.setOnUtteranceCompletedListener(new TextToSpeech.OnUtteranceCompletedListener() {
                            @Override
                            public void onUtteranceCompleted(String utteranceId) {
                                onDoneSpeaking(utteranceId);
                            }
                        });
                    }
                }
            } else {
                //no data - install it now
                Intent installTTSIntent = new Intent();
                installTTSIntent.setAction(TextToSpeech.Engine.ACTION_INSTALL_TTS_DATA);
                startActivity(installTTSIntent);
            }
        }
    }

    private void onDoneSpeaking(String utteranceId) {
        if (currentPosition < questionMap.size()) {
            currentPosition++;
        } else {
            currentPosition = STARTNUMBER;
        }
        show();
        speakWords(text);
    }

    @Override
    public void onInit(int initStatus) {
        //check for successful instantiation
        if (initStatus == TextToSpeech.SUCCESS) {
            if (myTTS.isLanguageAvailable(locale) == TextToSpeech.LANG_AVAILABLE)
                myTTS.setLanguage(locale);
        } else if (initStatus == TextToSpeech.ERROR) {
            Toast.makeText(this, "Sorry! Text To Speech failed...", Toast.LENGTH_LONG).show();
        }
    }

    public static List<String> splitEqually(String text, int size) {
        // Give the list the right capacity to start with. You could use an array
        // instead if you wanted.
        List<String> ret = new ArrayList<String>((text.length() + size - 1) / size);

        for (int start = 0; start < text.length(); start += size) {
            ret.add(text.substring(start, Math.min(text.length(), start + size)));
        }
        return ret;
    }
}
