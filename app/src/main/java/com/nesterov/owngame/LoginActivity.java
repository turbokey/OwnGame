package com.nesterov.owngame;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

public class LoginActivity extends AppCompatActivity {

    EditText name;
    CheckBox serverCB;
    Button startBtn;
    SeekBar playersNum;
    TextView seekBarValue;
    LinearLayout playersNumOptions;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        playersNumOptions = findViewById(R.id.playersNumOptions);
        seekBarValue = findViewById(R.id.seekBarValue);
        playersNum = findViewById(R.id.playersNum);
        playersNum.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener(){

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress,
                                          boolean fromUser) {
                // TODO Auto-generated method stub
                seekBarValue.setText(String.valueOf(progress));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                // TODO Auto-generated method stub
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                // TODO Auto-generated method stub
            }
        });

        name = findViewById(R.id.name);
        serverCB = findViewById(R.id.serverCB);
        serverCB.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked)
                    playersNumOptions.setVisibility(View.VISIBLE);
                else
                    playersNumOptions.setVisibility(View.GONE);
            }
        });
        startBtn = findViewById(R.id.startBtn);
        startBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!name.getText().toString().isEmpty()) {
                    Intent myIntent = new Intent(LoginActivity.this, MainActivity.class);
                    myIntent.putExtra("isServer",serverCB.isChecked());
                    myIntent.putExtra("playersNum",playersNum.getProgress());
                    myIntent.putExtra("name",name.getText().toString());
                    startActivity(myIntent);
                }
            }
        });
    }
}
