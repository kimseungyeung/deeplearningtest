package com.example.myapplication;

public class NoPayData {
    private int statenum=0;
   private int statetype =0;
   private String statename="";
   private String customername="";
   private String statetext="";
   private String rewardman="";
   private String researchman="";
   private boolean checkview=false;
    public NoPayData(int snum ,int stype,String sname,String cname,String st,String rm,String rsm,boolean check){
        this.statenum =snum;
        this.statetype=stype;
        this.customername=cname;
        this.statename=sname;
        this.statetext=st;
        this.rewardman=rm;
        this.researchman=rsm;
        this.checkview=check;
    }

    public int getStatenum() {
        return this.statenum;
    }

    public void setStatenum(int statenum) {
        this.statenum = statenum;
    }

    public int getStatetype() {
        return this.statetype;
    }

    public String getCustomername() {
        return this.customername;
    }

    public String getResearchman() {
        return this.researchman;
    }

    public String getRewardman() {
        return this.rewardman;
    }

    public String getStatename() {
        return this.statename;
    }

    public String getStatetext() {
        return this.statetext;
    }

    public void setStatetype(int statetype) {
        this.statetype = statetype;
    }

    public void setStatename(String statename) {
        this.statename = statename;
    }

    public void setStatetext(String statetext) {
        this.statetext = statetext;
    }

    public void setCustomername(String customername) {
        this.customername = customername;
    }

    public void setResearchman(String researchman) {
        this.researchman = researchman;
    }

    public void setRewardman(String rewardman) {
        this.rewardman = rewardman;
    }

    public void setCheckview(boolean checkview) {
        this.checkview = checkview;
    }
    public boolean getCheckview(){
        return this.checkview;
    }
}
