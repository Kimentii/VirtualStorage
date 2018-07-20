package com.kimentii.virtualstorage;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = MainActivity.class.getSimpleName();

    private TextView mLogTextView;
    private Button mStartButton;

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
        linearLayout.addView(menuView);

        DrawingView drawingView = new DrawingView(this);
        linearLayout.addView(drawingView);
        setContentView(linearLayout);
    }
}
