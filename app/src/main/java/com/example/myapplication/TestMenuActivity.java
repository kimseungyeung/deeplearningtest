package com.example.myapplication;

import android.app.TabActivity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class TestMenuActivity  extends TabActivity implements View.OnClickListener {
    TabHost tabHost;
    Context context;
    DrawerLayout drawer;
    Button  btn_left_menu,btn_inmenu;
    RecyclerView leftmenu;
    ImageView iv_profile_picture;
    TextView tv_nickname;
    String[] menulist = {"메뉴1", "메뉴2", "메뉴3", "메뉴4", "메뉴5"};
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_pager);
        component();
    }
    public void component(){
        btn_left_menu = (Button) findViewById(R.id.btn_left_menu);
        btn_left_menu.setOnClickListener(this);
        drawer = (DrawerLayout) findViewById(R.id.drawer);
        leftmenu = (RecyclerView) findViewById(R.id.left_menu);
        iv_profile_picture = (ImageView) findViewById(R.id.iv_profile_picture);
        tv_nickname = (TextView) findViewById(R.id.tv_nickname_left);
        btn_inmenu = (Button) findViewById(R.id.btn_inmenu);
        btn_inmenu.setOnClickListener(this);
        ArrayList<MenuData> md = new ArrayList<>();
        boolean df = true;
        for (int i = 0; i < menulist.length; i++) {
            if (i == 0 || i == 2) {
                df = false;
            } else {
                df = true;
            }
            MenuData mm = new MenuData(menulist[i], R.drawable.ic_launcher_foreground, df);
            md.add(mm);
        }

        MenuAdapter mm = new MenuAdapter(md,R.layout.item_left_menu,R.layout.item_left_sub_menu,this);
        leftmenu.setAdapter(mm);
        leftmenu.setLayoutManager(new LinearLayoutManager(this));
        drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_left_menu:
                drawer.openDrawer(GravityCompat.END);
                break;
            case R.id.btn_inmenu:
                drawer.closeDrawer(Gravity.RIGHT);
                break;
        }
    }

}
