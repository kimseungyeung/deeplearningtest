package com.example.myapplication.Activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.os.Bundle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.example.myapplication.Adapter.MenuAdapter;
import com.example.myapplication.Adapter.NopayAdapter;
import com.example.myapplication.Data.MenuData;
import com.example.myapplication.Data.NoPayData;
import com.example.myapplication.R;

//import net.alhazmy13.wordcloud.WordCloud;
//import net.alhazmy13.wordcloud.WordCloudView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    Context context;
    DrawerLayout drawer;
    Button btn_inmenu,btn_nopay,btn_pay,btn_closeing,btn_calendar;
    ImageButton imbtn_left_menu;
    RecyclerView leftmenu;
    ImageView iv_profile_picture;
    TextView tv_nickname;
    String[] menulist = {"차트", "차트2", "deeplearning", "메뉴4", "메뉴5"};
    ArrayList<MenuData> md;
    ArrayList<NoPayData> nd;
    int nopaycount=0;
    PopupWindow pwindo;
    int selectidx=0;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_pager);
        component();
    }
    public void component(){
        context=this;
        imbtn_left_menu = (ImageButton) findViewById(R.id.imbtn_left_menu);
        imbtn_left_menu.setOnClickListener(this);
        drawer = (DrawerLayout) findViewById(R.id.drawer);
        leftmenu = (RecyclerView) findViewById(R.id.left_menu);
        iv_profile_picture = (ImageView) findViewById(R.id.iv_profile_picture);
        tv_nickname = (TextView) findViewById(R.id.tv_nickname_left);
        btn_inmenu = (Button) findViewById(R.id.btn_inmenu);
        btn_pay=(Button)findViewById(R.id.btn_payment);
        btn_nopay=(Button)findViewById(R.id.btn_nopayment);
        btn_closeing=(Button)findViewById(R.id.btn_closeing);
        btn_calendar=(Button)findViewById(R.id.btn_calendar);
        btn_pay.setOnClickListener(this);
        btn_nopay.setOnClickListener(this);
        btn_closeing.setOnClickListener(this);
        btn_calendar.setOnClickListener(this);
        btn_inmenu.setOnClickListener(this);
        if(md==null) {
            md = new ArrayList<>();
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
        }
        MenuAdapter mm = new MenuAdapter(md,R.layout.item_left_menu,R.layout.item_left_sub_menu,this);
        mm.setItemClick(new MenuAdapter.ItemClick() {
            @Override
            public void onClick(View view, int position) {
                switch (position){
                    case 0:
                        Intent i = new Intent(context,ChartActivity.class);
                        startActivity(i);
                        break;
                    case 1:
                        Intent i2 = new Intent(context,Chart2Activity.class);
                        startActivity(i2);
                        break;
                    case 2:
                        Intent i3 = new Intent(context,DeepLearningActivity.class);
                        startActivity(i3);
                        break;
                    case 3:
                        break;
                    case 4:
                        break;
                    case 5:
                        break;
                }
            }
        });
        leftmenu.setAdapter(mm);
        leftmenu.setLayoutManager(new LinearLayoutManager(this));
        drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
        nd= new ArrayList<>();
        NoPayData ndd = new NoPayData(30,0,"보고서반송","13-10-7777 김승영","두개골절","김보현","김현수",true);
        NoPayData nd2 = new NoPayData(16,1,"종결보고서 제출","13-10-574 박석원","요추.간판의","최수열","박창휘",false);
        NoPayData nd3 = new NoPayData(59,3,"중간보고서 제출","13-11-2355 김규종","폐혈성 쇼크","김시현","김무열",false);
        NoPayData nd4 = new NoPayData(41,1,"고객면담","13-16-3455 최현성","경추간판전위","김화연","김무안",false);
        NoPayData nd5 = new NoPayData(24,2,"보고서반송","13-13-4355 이제성","간암","김철수","박영희",false);
        NoPayData nd6 = new NoPayData(31,1,"종결보고서 제출","13-14-2355 이현우","폐암","김화연","최현희",false);
        NoPayData nd7 = new NoPayData(13,3,"고객면담","13-13-4255 박현철","심근경색","김지연","박찬수",false);

        nd.add(ndd);
        nd.add(nd2);
        nd.add(nd3);
        nd.add(nd4);
        nd.add(nd5);
        nd.add(nd6);
        nd.add(nd7);
        if(nopaycount==0) {
            for (int i = 0; i < nd.size(); i++) {
                if (nd.get(i).getCheckview() == false) {
                    nopaycount++;
                }
            }
        }
//        List<WordCloud> dd=new ArrayList<WordCloud>();
//        WordCloud d = new WordCloud("안녕하세요",1);
//        WordCloud d2 = new WordCloud("어서오세요",1);
//        WordCloud d3 = new WordCloud("반갑습니다",1);
//        WordCloud d4 = new WordCloud("또오세요",1);
//        WordCloud d5 = new WordCloud("안녕히가세요",1);
//        WordCloud d6 = new WordCloud("즐거운시간되세요",1);
//        dd.add(d);
//        dd.add(d2);
//        dd.add(d3);
//        dd.add(d4);
//        dd.add(d5);
//        dd.add(d6);
//        WordCloudView wv = (WordCloudView)findViewById(R.id.testword);
//        wv.setDataSet(dd);
//        wv.setsca
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.imbtn_left_menu:
                drawer.openDrawer(GravityCompat.END);
                break;
            case R.id.btn_inmenu:
                drawer.closeDrawer(Gravity.RIGHT);
                break;

            case R.id.btn_nopayment:

                    popup1();

                break;
            case R.id.btn_payment:

//                PopupWindow popup = new PopupWindow(v);
//                LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//                //팝업으로 띄울 커스텀뷰를 설정하고
//                View view = inflater.inflate(R.layout.test_popup_window, null);
//                popup.setContentView(view);
//                //팝업의 크기 설정
//                popup.setWindowLayoutMode(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
//                //팝업 뷰 터치 되도록
//                popup.setTouchable(true);
//                //팝업 뷰 포커스도 주고
//                popup.setFocusable(true);
//                //팝업 뷰 이외에도 터치되게 (터치시 팝업 닫기 위한 코드)
//                popup.setOutsideTouchable(true);
//                popup.setBackgroundDrawable(new BitmapDrawable());
//                //인자로 넘겨준 v 아래로 보여주기
//                popup.showAsDropDown(v);
                break;
            case R.id.btn_closeing:
                break;
            case R.id.btn_calendar:
                break;
        }
    }
public void popup1(){
    try {
        WindowManager w = getWindowManager();
        Display d = w.getDefaultDisplay();



            Point realSize = new Point();
            Display.class.getMethod("getRealSize", Point.class).invoke(d, realSize);
            int mWidthPixels = realSize.x;
            int mHeightPixels = realSize.y;

        DisplayMetrics dm = getApplicationContext().getResources().getDisplayMetrics();

        int width = dm.widthPixels;

        int height = dm.heightPixels;


        //  LayoutInflater 객체와 시킴
        LayoutInflater inflater = (LayoutInflater) this
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View layout = inflater.inflate(R.layout.nopayment_activity,
                (ViewGroup) findViewById(R.id.ll_popup));

         pwindo = new PopupWindow(layout, mWidthPixels-100,
                 mHeightPixels-550,false);
        pwindo.setOutsideTouchable(true);
        pwindo.setAnimationStyle(-1);
        Button btn_view = (Button) layout.findViewById(R.id.btn_view);
        Button btn_update =(Button)layout.findViewById(R.id.btn_update);
        RecyclerView ll =(RecyclerView)layout.findViewById(R.id.recycle_list);
        final NopayAdapter nadater =new NopayAdapter(nd,R.layout.item_popup1,this);
        nadater.setItemClick(new NopayAdapter.ItemClick() {
                                 @Override
                                 public void onClick(View view, int position, NoPayData np) {
                                     switch (view.getId()){
                                         case R.id.ll_text:
                                             Toast.makeText(getApplicationContext(),np.getStatetext(),Toast.LENGTH_LONG).show();
                                             Intent i = new Intent(context,DataViewActivity.class);
                                             i.putExtra("nopay",np);
                                             startActivity(i);
                                             break;
                                         case R.id.iv_stype:
                                             Toast.makeText(getApplicationContext(),String.valueOf(position),Toast.LENGTH_LONG).show();
//                                             selectidx=position;
                                             nadater.clickicon(position);
                                             break;
                                     }


                                 }
                             });
                ll.setAdapter(nadater);
        ll.setLayoutManager(new LinearLayoutManager(this));

        btn_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(),"확대",Toast.LENGTH_LONG).show();
                pwindo.dismiss();
            }
        });
        btn_update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(),"update",Toast.LENGTH_LONG).show();
                pwindo.dismiss();
            }
        });

        //View view = getWindow().getDecorView() ;
        pwindo.showAtLocation(layout,Gravity.CENTER,0,200);
        pwindo.showAsDropDown(layout);






    } catch (Exception e) {
        Toast.makeText(getApplicationContext(),e.getMessage().toString(),Toast.LENGTH_LONG).show();
        e.printStackTrace();
    }


}

    @Override
    public void onBackPressed() {

        if(drawer.isDrawerOpen(GravityCompat.END)){
            drawer.closeDrawer(Gravity.RIGHT);
        }else{
            super.onBackPressed();
        }
    }
}
