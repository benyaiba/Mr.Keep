package com.yaiba.keep.data;

import android.app.Application;
/**
 * Created by benyaiba on 2017/06/09.
 */

public class ListViewData extends Application{

    private int firstVisiblePosition; // listView第一个可见的item的位置，即在数据集合中的位置position
    private int firstVisiblePositionTop; // listView第一可见的item距离父布局的top

    public int getFirstVisiblePosition(){
        return this.firstVisiblePosition;
    }
    public void setFirstVisiblePosition(int c){
        this.firstVisiblePosition= c;
    }

    public int getFirstVisiblePositionTop(){
        return this.firstVisiblePositionTop;
    }
    public void setFirstVisiblePositionTop(int c){
        this.firstVisiblePositionTop= c;
    }

    @Override
    public void onCreate(){
        firstVisiblePosition = 0;
        firstVisiblePositionTop = 0;
        super.onCreate();
    }

}
