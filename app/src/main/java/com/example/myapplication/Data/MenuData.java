package com.example.myapplication.Data;

public class MenuData {
    private String menuname = "";//메뉴이름
    private int menuicon = 0; //메뉴 아이콘
    private boolean menutype = true; //메뉴타입 true:일반메뉴 false:타이틀메뉴

    public MenuData(String menun, int menui, boolean menut) {
        this.menuname = menun;
        this.menuicon = menui;
        this.menutype = menut;
    }

    public int getMenuicon() {
        return menuicon;
    }

    public String getMenuname() {
        return menuname;
    }

    public boolean getMenuType() {
        return menutype;
    }
}
