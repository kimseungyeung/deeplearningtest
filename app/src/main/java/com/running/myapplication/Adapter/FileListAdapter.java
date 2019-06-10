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

import com.running.myapplication.Data.NoPayData;
import com.running.myapplication.R;

import java.io.File;
import java.util.ArrayList;

public class FileListAdapter extends RecyclerView.Adapter<FileListAdapter.Holder> {
    private ItemClick itemClick;
    private int selectidx=-1;
    private ArrayList<File> filelist;
    public Context context;
    String s;


    public FileListAdapter(ArrayList<File>flist, Context ctx){
        this.filelist=flist;
        this.context =ctx;

    }
    public interface ItemClick {
        public void onClick(View view, int position, File fdata);

    }

    //아이템 클릭시 실행 함수 등록 함수
    public void setItemClick(ItemClick itemClick) {
        this.itemClick = itemClick;
    }

    @NonNull
    @Override
    public FileListAdapter.Holder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_filelist,viewGroup,false);
        return new FileListAdapter.Holder(view);
    }

    @Override
    public void onBindViewHolder(final Holder holder, final int position) {
        if(selectidx==position){
            holder.ll_main.setBackgroundColor(context.getResources().getColor(R.color.grey));
        }else{
            holder.ll_main.setBackgroundColor(context.getResources().getColor(R.color.White));
        }
        final String fname= filelist.get(position).getName();
        final String fsize= Long.toString(filelist.get(position).length()/1024)+"kb";
        holder.tv_fname.setText(fname);
        holder.tv_fsize.setText(fsize);
        holder.ll_main.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(itemClick != null){
                    itemClick.onClick(v,position,filelist.get(position));
                }
            }
        });



    }
    public void clickidx(int position){

        if(position!=-1&&position!=selectidx){
            selectidx=position;
        }else{
            selectidx=-1;
        }
        notifyDataSetChanged();
    }
    public void adddata(File np){
        this.filelist.add(np);
    }
    public String getfilename(){

        if(selectidx!=-1) {
           s = filelist.get(selectidx).getName();
       }else{
            s="";
        }
        return s;
    }

    @Override
    public int getItemCount() {
        return filelist.size();
    }



    @Override
    public int getItemViewType(int position) {
        return super.getItemViewType(position);
    }

    //홀더를 셋팅
    public  class Holder extends RecyclerView.ViewHolder{
        LinearLayout ll_main;
        TextView tv_fname,tv_fsize;

        public Holder(@NonNull View itemView) {
            super(itemView);
            ll_main=(LinearLayout)itemView.findViewById(R.id.ll_main);
            tv_fname=(TextView)itemView.findViewById(R.id.tv_fname);
            tv_fsize=(TextView)itemView.findViewById(R.id.tv_fsize);

        }


    }

}