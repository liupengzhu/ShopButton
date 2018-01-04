package com.example.shopbutton;

/**
 * Created by Administrator on 2017/9/21/021.
 */

public class GoodsBean {
    private int count;
    private int maxCount;

    public GoodsBean(int count, int maxCount) {
        this.count = count;
        this.maxCount = maxCount;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public int getMaxCount() {
        return maxCount;
    }

    public void setMaxCount(int maxCount) {
        this.maxCount = maxCount;
    }
}
