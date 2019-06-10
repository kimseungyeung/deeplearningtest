package com.running.myapplication.Adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.running.myapplication.Data.MenuData;
import com.running.myapplication.R;

import java.util.ArrayList;

public class MenuAdapter extends RecyclerView.Adapter<MenuAdapter.Holder> {
    private ItemClick itemClick;
    private int itemlayout;
    private int itemsublayout;
    private int selectidx=0;
    private ArrayList<MenuData> mlist;

    private MenuAdapter mm;
    public Context context;

    public void onClick(View view,int position){
        int d =0;
    }
    public  MenuAdapter(ArrayList<MenuData>mdaalist, int ilayout, int islayout, Context ctx){
        this.mlist=mdaalist;
        this.itemlayout=ilayout;
        this.itemsublayout =islayout;
        mm=this;
        this.context =ctx;

    }
    public interface ItemClick {
        public void onClick(View view,int position);
    }
    //아이템 클릭시 실행 함수 등록 함수
    public void setItemClick(ItemClick itemClick) {
        this.itemClick = itemClick;
    }
    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(itemlayout,viewGroup,false);
        return new Holder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull Holder holder, final int position) {
        MenuData md= mlist.get(position);
        holder.iv_menu_icon.setImageResource(mlist.get(position).getMenuicon());
        holder.tv_menu_text.setText(mlist.get(position).getMenuname());
        holder.ll_menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                itemClick.onClick(v,position);

            }
        });

    }


    public int getSelectidx() {
        return this.selectidx;
    }

    @Override
    public int getItemCount() {
        return mlist.size();
    }



    @Override
    public int getItemViewType(int position) {
        return super.getItemViewType(position);
    }

    //홀더를 셋팅
    public static class Holder extends RecyclerView.ViewHolder{
        public TextView tv_menu_text;
        public LinearLayout ll_menu;
        public ImageView iv_menu_icon;

        public Holder(@NonNull View itemView) {
            super(itemView);
            iv_menu_icon = (ImageView) itemView.findViewById(R.id.iv_menu_icon);
            tv_menu_text = (TextView) itemView.findViewById(R.id.tv_menu_text);
            ll_menu = (LinearLayout) itemView.findViewById(R.id.ll_menu);

        }


        }

    }
