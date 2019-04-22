package com.example.myapplication;

import android.content.Context;
import android.graphics.Point;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class TestMenuActivity  extends AppCompatActivity implements View.OnClickListener {

    Context context;
    DrawerLayout drawer;
    Button  btn_left_menu,btn_inmenu,btn_nopay,btn_pay,btn_closeing,btn_calendar;
    RecyclerView leftmenu;
    ImageView iv_profile_picture;
    TextView tv_nickname;
    String[] menulist = {"메뉴1", "메뉴2", "메뉴3", "메뉴4", "메뉴5"};
    ArrayList<MenuData> md;
    ArrayList<NoPayData> nd;
    int nopaycount=0;
    PopupWindow pwindo;
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
        leftmenu.setAdapter(mm);
        leftmenu.setLayoutManager(new LinearLayoutManager(this));
        drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
        nd= new ArrayList<>();
        NoPayData ndd = new NoPayData(30,0,"보고서반송","13-10-7777 김승영","두개골절","김보현","김현수",false);
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


        //  LayoutInflater 객체와 시킴
        LayoutInflater inflater = (LayoutInflater) this
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View layout = inflater.inflate(R.layout.nopayment_activity,
                (ViewGroup) findViewById(R.id.ll_popup));

         pwindo = new PopupWindow(layout,mWidthPixels-100,
                 mHeightPixels-550,true);
        pwindo.setOutsideTouchable(true);
        pwindo.setAnimationStyle(0);
        Button btn_view = (Button) layout.findViewById(R.id.btn_view);
        Button btn_update =(Button)layout.findViewById(R.id.btn_update);
        RecyclerView ll =(RecyclerView)layout.findViewById(R.id.recycle_list);
        NopayAdapter nadater =new NopayAdapter(nd,R.layout.item_popup1,this);
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
        View view =(View)btn_nopay;
        //View view = getWindow().getDecorView() ;

        pwindo.showAsDropDown(layout,Gravity.CENTER,0,0);





    } catch (Exception e) {
        Toast.makeText(getApplicationContext(),e.getMessage().toString(),Toast.LENGTH_LONG).show();
        e.printStackTrace();
    }


}
    public class NopayAdapter extends RecyclerView.Adapter<NopayAdapter.Holder> {

        private int itemlayout;
        private int selectidx=0;
        private ArrayList<NoPayData> noPayDataArrayList;
        public Context context;
        Integer[] iconlist = {R.drawable.shapeovared, R.drawable.shapeovayellow, R.drawable.shapeovablue, R.drawable.shapeovagreen};
        String[]statelist={"미이행","지연제출","대상","이행"};
        public  NopayAdapter(ArrayList<NoPayData>ndaalist, int ilayout, Context ctx){
            this.noPayDataArrayList=ndaalist;
            this.itemlayout=ilayout;
            this.context =ctx;

        }


        @NonNull
        @Override
        public NopayAdapter.Holder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(itemlayout,viewGroup,false);
            return new NopayAdapter.Holder(view);
        }

        @Override
        public void onBindViewHolder(Holder holder, final int position) {
            NoPayData md= noPayDataArrayList.get(position);
           holder.iv_stype.setImageDrawable(getResources().getDrawable(iconlist[md.getStatetype()]));
           holder.tv_stype.setText("[중간보고 : "+statelist[md.getStatetype()]+"]");
           holder.tv_snum.setText(String.valueOf(md.getStatenum()));
           holder.tv_sname.setText(md.getStatename());
           holder.tv_cname.setText(md.getCustomername());
           holder.tv_st.setText(md.getStatetext());
           holder.tv_rm.setText("보상: "+md.getRewardman()+" / "+"조사자: "+md.getResearchman());

        }




        @Override
        public int getItemCount() {
            return noPayDataArrayList.size();
        }



        @Override
        public int getItemViewType(int position) {
            return super.getItemViewType(position);
        }

        //홀더를 셋팅
        public  class Holder extends RecyclerView.ViewHolder{
         ImageView iv_stype;
         TextView tv_snum,tv_stype,tv_sname,tv_cname,tv_st,tv_rm;

            public Holder(@NonNull View itemView) {
                super(itemView);
                iv_stype=(ImageView)itemView.findViewById(R.id.iv_stype);
               tv_snum=(TextView)itemView.findViewById(R.id.tv_snum);
                tv_stype =(TextView)itemView.findViewById(R.id.tv_stype);
               tv_sname=(TextView)itemView.findViewById(R.id.tv_sname);
               tv_cname=(TextView)itemView.findViewById(R.id.tv_cname);
               tv_st=(TextView)itemView.findViewById(R.id.tv_st);
               tv_rm=(TextView)itemView.findViewById(R.id.tv_rm);

            }


        }

    }
}
