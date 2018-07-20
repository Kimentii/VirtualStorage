package com.kimentii.virtualstorage;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = MainActivity.class.getSimpleName();

    private TextView mLogTextView;
    private Button mStartButton;
    private Spinner mRobotsNumSpinner;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

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
        linearLayout.addView(menuView);

        final DrawingView drawingView = new DrawingView(this, 2);
        drawingView.setWillNotDraw(false);
        linearLayout.addView(drawingView);
        mRobotsNumSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String[] choose = getResources().getStringArray(R.array.robots_num);
                drawingView.setRobotsNum(Integer.valueOf(choose[i]));
                drawingView.invalidate();
                Log.d(TAG, "onItemSelected: " + i);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                Log.d(TAG, "onNothingSelected: ");

            }
        });
        setContentView(linearLayout);
    }
}
