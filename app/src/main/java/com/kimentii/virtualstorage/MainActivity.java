package com.kimentii.virtualstorage;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.HashMap;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = MainActivity.class.getSimpleName();
    public static final int ACTION_LOG = 1;
    public static final int ACTION_CLEAR_LOG = 99;

    private TextView mLogTextView;
    private Button mStartButton;
    private Spinner mRobotsNumSpinner;
    private DrawingView mDrawingView;
    private ScrollView mLogScrollView;
    private boolean isDrawing;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        Log.d(TAG, "onCreate: ");

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

        GlobalConfigurations.getInstance(getApplicationContext());

        LinearLayout linearLayout = new LinearLayout(this);
        linearLayout.setOrientation(LinearLayout.HORIZONTAL);

        LayoutInflater layoutInflater = getLayoutInflater();
        View menuView = layoutInflater.inflate(R.layout.menu, linearLayout, false);
        mLogTextView = menuView.findViewById(R.id.tv_log);
        mStartButton = menuView.findViewById(R.id.button_start);
        mRobotsNumSpinner = menuView.findViewById(R.id.spinner_robots_num);
        mLogScrollView = menuView.findViewById(R.id.sv_log);
        linearLayout.addView(menuView);

        Handler handler = new LogHandler();
        mDrawingView = new DrawingView(this, handler, 2);
        linearLayout.addView(mDrawingView);
        mRobotsNumSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String[] choose = getResources().getStringArray(R.array.robots_num);
                mLogTextView.setText("");
                mDrawingView.setRobotsNum(Integer.valueOf(choose[i]));
                mDrawingView.invalidate();
//                Log.d(TAG, "onItemSelected: " + i);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
//                Log.d(TAG, "onNothingSelected: ");

            }
        });
        mStartButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                if (!isDrawing) {
                    startDrawing();
                    isDrawing = true;
                } else {
                    stopDrawing();
                    isDrawing = false;
                }
            }
        });
        setContentView(linearLayout);
    }

    @Override
    protected void onPause() {
        super.onPause();
//        Log.d(TAG, "onPause");
        if (isDrawing) {
            stopDrawing();
            isDrawing = false;
        }
        mLogTextView.setText("");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mDrawingView.destroy();
    }

    private void startDrawing() {
        mStartButton.setText(R.string.action_stop);
        mRobotsNumSpinner.setEnabled(false);
        mDrawingView.startDrawing();
    }

    private void stopDrawing() {
        mStartButton.setText(R.string.action_start);
        mRobotsNumSpinner.setEnabled(true);
        mDrawingView.stopDrawing();
        mLogTextView.setText("");
    }

    class LogHandler extends Handler {

        @Override
        public void handleMessage(Message msg) {
            if (msg.what == ACTION_LOG) {
                String record = (String) msg.obj;
                if (record != null) {
                    mLogTextView.append(record + "\n");
                    mLogScrollView.fullScroll(View.FOCUS_DOWN);
                }
            } else if (msg.what == ACTION_CLEAR_LOG) {
                mLogTextView.setText("");
            }
        }
    }
}
