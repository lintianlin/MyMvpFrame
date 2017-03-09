package com.jackma.mymvpframe.ui.base;

import android.widget.BaseAdapter;

import java.util.ArrayList;

public abstract class BasicAdapter<T> extends BaseAdapter {
    protected ArrayList<T> list;

    //使用成员变量生成构造方法：alt+shift+s->o
    public BasicAdapter(ArrayList<T> list) {
        super();
        this.list = list;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

}