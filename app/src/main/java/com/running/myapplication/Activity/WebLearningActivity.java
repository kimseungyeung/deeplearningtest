package com.running.myapplication.Activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.EditText;

import com.running.myapplication.R;

public class WebLearningActivity extends AppCompatActivity implements View.OnClickListener {
    EditText edt_address;
    WebView wv_data;
    Button btn_search,btn_setting;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.weblearning_activity);
    }
    public void component(){
        edt_address = (EditText)findViewById(R.id.edt_address);
        wv_data = (WebView)findViewById(R.id.wv_data);
        btn_search=(Button)findViewById(R.id.btn_search);
        btn_setting =(Button)findViewById(R.id.btn_setting);
        btn_setting.setOnClickListener(this);
        btn_search.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_search:
                String address =edt_address.getText().toString();
                wv_data.loadUrl(address);
                break;
            case R.id.btn_setting:
                break;
        }
    }
}
