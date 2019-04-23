package com.example.myapplication;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class NopayAdapter extends RecyclerView.Adapter<NopayAdapter.Holder> {
    private ItemClick itemClick;
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
    public interface ItemClick {
        public void onClick(View view,int position,NoPayData np);
    }
    //아이템 클릭시 실행 함수 등록 함수
    public void setItemClick(ItemClick itemClick) {
        this.itemClick = itemClick;
    }
    @NonNull
    @Override
    public NopayAdapter.Holder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(itemlayout,viewGroup,false);
        return new NopayAdapter.Holder(view);
    }

    @Override
    public void onBindViewHolder(Holder holder, final int position) {
        final NoPayData md= noPayDataArrayList.get(position);
        holder.iv_stype.setImageDrawable(context.getResources().getDrawable(iconlist[md.getStatetype()]));
        holder.tv_stype.setText("[중간보고 : "+statelist[md.getStatetype()]+"]");
        holder.tv_snum.setText(String.valueOf(md.getStatenum()));
        holder.tv_sname.setText(md.getStatename());
        holder.tv_cname.setText(md.getCustomername());
        holder.tv_st.setText(md.getStatetext());
        holder.tv_rm.setText("보상: "+md.getRewardman()+" / "+"조사자: "+md.getResearchman());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(itemClick != null){
                    itemClick.onClick(v,position,md);
                }

            }
        });
    }

    public void adddata(NoPayData np){
        this.noPayDataArrayList.add(np);
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