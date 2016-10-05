package cn.bingod.segmentedseekbar;

import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;

import cn.bingod.widget.segmentedseekbar.SegmentedSeekBar;

public class MainActivity extends AppCompatActivity {

    SegmentedSeekBar ssb;
    EditText etWeight;
    EditText etCount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ssb = (SegmentedSeekBar) findViewById(R.id.ssb);
        etWeight = (EditText) findViewById(R.id.et_weight);
        etCount = (EditText) findViewById(R.id.et_count);

        etWeight.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String text = s.toString();
                if(!TextUtils.isEmpty(text))
                    ssb.setLineWeight(Integer.parseInt(text));
            }
        });

        etCount.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String text = s.toString();
                if(!TextUtils.isEmpty(text))
                    ssb.setSegmentCount(Integer.parseInt(text));
            }
        });
    }

    public void changeLineColor(View view){
        ssb.setLineColor(Color.BLUE);
    }

    public void clickButton(View view){
        Intent intent = new Intent(this, WithTextActivity.class);
        startActivity(intent);
    }
}