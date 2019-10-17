package com.example.btrwatch;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.wearable.activity.WearableActivity;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import android.widget.Toast;

import androidx.core.view.GestureDetectorCompat;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import static android.view.GestureDetector.*;



public class MainActivity extends WearableActivity implements
        OnGestureListener,GestureDetector.OnDoubleTapListener
{
    private CountDownTimer mCountDownTimer;
    public static final String SHARED_PREFS = "sharedPrefs";
    public static final String Sec_Refresh = null;
    private int mTimeLeft = 0;
    private TextView mTextView;
    private ProgressBar mProgressBar;
    public Integer mSecRefresh;

    private GestureDetectorCompat GestureDetect;

    public void geturl()
    {
        mTextView =  findViewById(R.id.text);





        OkHttpClient client = new OkHttpClient();

        String url = "https://www.bitrue.com/api/v1/trades?symbol=VETUSDT&limit=1";

        Request request = new Request.Builder()
                .url(url)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    final String myResponse = response.body().string();
                    final Pattern pattern  = Pattern.compile("price\":\"(.*?)\"");
                    Matcher m = pattern.matcher(myResponse);
                    final String num;
                    if (m.find()) {
                        num = m.group(1);
                    }
                    else {
                        num = "";
                    }

                    MainActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mTextView.setText("$"+new DecimalFormat("0.#######").format(Float.parseFloat(num)));
                        }
                    });
                }
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mTextView = (TextView) findViewById(R.id.text);
        mProgressBar = (ProgressBar) findViewById(R.id.progressBar3);


//        new CountDownTimer(30000, 10000) {
//
//            public void onTick(long millisUntilFinished) {
//                //mTextField.setText("seconds remaining: " + millisUntilFinished / 1000);
//                Log.i("Timer","********** seconds remaining: " + millisUntilFinished / 1000 );
//            }
//
//            public void onFinish() {
//                Log.i("Timer","********** counter end" );
////                mTextField.setText("done!");
//            }
//        }.start();
//        Log.i("Timer","********** counter start" );






        mTextView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                SaveData();
                Toast.makeText(getApplicationContext(), String.valueOf((mSecRefresh) /1000 + " Sec Refresh"), Toast.LENGTH_SHORT).show();

                if(mCountDownTimer != null){
                    mCountDownTimer.cancel();
                }
                updateCountDown();

                return false;
            }
        });


        GestureDetect = new GestureDetectorCompat(this,this);
        GestureDetect.setOnDoubleTapListener(this);
        // Enables Always-on
        setAmbientEnabled();

        LoadData();
        Toast.makeText(getApplicationContext(), String.valueOf((mSecRefresh) /1000 + " Sec Refresh"), Toast.LENGTH_SHORT).show();
        geturl();

        if(mCountDownTimer != null){
            mCountDownTimer.cancel();
        }
        updateCountDown();
    }

    public void updateCountDown() {

        mCountDownTimer = new CountDownTimer(mSecRefresh  , 1000) {

            @Override
            public void onTick(long l) {
            //    Log.i("Timer","Time left: " + l );
                mTimeLeft = (int)l;
                updateValueProgressBar(mSecRefresh - mTimeLeft);
            }

            @Override
            public void onFinish() {
             //   Log.i("Timer","-------------------------------");
                geturl();
                updateMaxProgressBar(mSecRefresh);
                updateValueProgressBar(0);
                mCountDownTimer.cancel();
                mCountDownTimer.start();
            }
        };
        updateMaxProgressBar(mSecRefresh);
        updateValueProgressBar(0);
        mCountDownTimer.start();

    }


    public void SaveData() {
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS,MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        if (mSecRefresh < 120000)
            {
                mSecRefresh = mSecRefresh + 10000;
                editor.putInt(Sec_Refresh, mSecRefresh);
            }
        else
            {

                mSecRefresh = 10000;
                editor.putInt(Sec_Refresh, mSecRefresh);

            }
        editor.apply();
    }
    public void LoadData() {
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS,MODE_PRIVATE);
        mSecRefresh =  sharedPreferences.getInt(Sec_Refresh, 10000);

    }
    public void updateMaxProgressBar(int max)
    {
        mProgressBar.setMax(max);
    }

    public void updateValueProgressBar(int value)
    {
        mProgressBar.setProgress(value);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        GestureDetect.onTouchEvent(event);
        return super.onTouchEvent(event);
    }

    @Override
    public boolean onSingleTapConfirmed(MotionEvent motionEvent) {
        return false;
    }

    @Override
    public boolean onDoubleTap(MotionEvent motionEvent) {
        return false;
    }

    @Override
    public boolean onDoubleTapEvent(MotionEvent motionEvent) {
        return false;
    }

    @Override
    public boolean onDown(MotionEvent motionEvent) {
        return false;
    }

    @Override
    public void onShowPress(MotionEvent motionEvent) {

    }

    @Override
    public boolean onSingleTapUp(MotionEvent motionEvent) {
        return false;
    }

    @Override
    public boolean onScroll(MotionEvent motionEvent, MotionEvent motionEvent1, float v, float v1) {
        return false;
    }

    @Override
    public void onLongPress(MotionEvent motionEvent) {

    }

    @Override
    public boolean onFling(MotionEvent motionEvent, MotionEvent motionEvent1, float v, float v1) {
        return false;
    }
}
