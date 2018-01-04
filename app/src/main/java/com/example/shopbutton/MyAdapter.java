package com.example.shopbutton;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

/**
 * Created by Administrator on 2017/9/21/021.
 */

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> {

    private List<GoodsBean> goodsBeanList;

    public MyAdapter(List<GoodsBean> goodsBeanList) {
        this.goodsBeanList = goodsBeanList;
    }

    static class ViewHolder extends RecyclerView.ViewHolder{
        TextView textView;
        ShopButtonView shopButtonView;
        public ViewHolder(View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.tv_text);
            shopButtonView = itemView.findViewById(R.id.tv_shop);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.chird_item,parent,false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final GoodsBean goodsBean = goodsBeanList.get(position);
        holder.textView.setText(goodsBean.getCount()+"");
        holder.shopButtonView.setmCount(goodsBean.getCount());
        //holder.shopButtonView.setMaxCount(goodsBean.getMaxCount());
        holder.shopButtonView.setOnCountChangeListener(new ShopButtonView.OnCountChangeListener() {
            @Override
            public void onCountChanged(int mCount) {
                goodsBean.setCount(mCount);
            }
        });
    }

    @Override
    public int getItemCount() {
        return goodsBeanList.size();
    }
}
