package com.example.administrator.puke;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

public class PukeGroup extends ViewGroup {
    int[] pukeNum = new int[13];
    public PukeGroup(Context context) {
        super(context);
        setGroupNum(new int[]{1,2,3,4,5,6,7,8,9,10,11,12,13});
    }

    public PukeGroup(Context context, AttributeSet attrs) {
        super(context, attrs);
        setGroupNum(new int[]{1,2,3,4,5,6,7,8,9,10,11,12,13});
    }

    public PukeGroup(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setGroupNum(new int[]{1,2,3,4,5,6,7,8,9,10,11,12,13});
    }
    protected  void onLayout(boolean changed, int l, int t, int r, int b) {
        int widthAll=12*30+100;
        int viewCount=getChildCount();
        for(int i=0;i<viewCount;i++){
                View childView=getChildAt(i);
                childView.layout(0,0,65,105);
        }
    }
    public void setGroupNum(int num[]){
        int i=0;
        for(;i<num.length;i++){
            pukeNum[i] = num [i];
        }
    }
}
