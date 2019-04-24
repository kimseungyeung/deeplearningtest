package com.example.myapplication.Activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.myapplication.Data.NoPayData;
import com.example.myapplication.R;

public class DataViewActivity extends AppCompatActivity implements View.OnClickListener {
    TextView tv_utext;
    EditText edt_cname,edt_sstate,edt_stname,edt_sreport,edt_reward,edt_research;
    ImageButton btn_preview;
    NoPayData noPayData;
    String[]statelist={"미이행","지연제출","대상","이행"};
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.data_view_activity);
        noPayData =(NoPayData) getIntent().getParcelableExtra("nopay");
        component();

    }
    public void component(){
        tv_utext=(TextView)findViewById(R.id.tv_utext);
        edt_cname=(EditText)findViewById(R.id.edt_cname);
        edt_sstate=(EditText)findViewById(R.id.edt_sstate);
        edt_stname=(EditText)findViewById(R.id.edt_stname);
        edt_sreport=(EditText)findViewById(R.id.edt_sreport);
        edt_reward=(EditText)findViewById(R.id.edt_reward);
        edt_research=(EditText)findViewById(R.id.edt_research);
        btn_preview=(ImageButton)findViewById(R.id.imbtn_preview);
        btn_preview.setOnClickListener(this);
        tv_utext.setText(noPayData.getCustomername());
        edt_cname.setText(noPayData.getCustomername());
        edt_sstate.setText(noPayData.getStatename());
        edt_stname.setText(noPayData.getStatetext());
        edt_sreport.setText(statelist[noPayData.getStatetype()]);
        edt_reward.setText(noPayData.getRewardman());
        edt_research.setText(noPayData.getResearchman());
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.imbtn_preview:
                onBackPressed();
                break;
        }

    }
}
