package com.bit.communityProperty.fragment.main.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.sdk.android.oss.ClientException;
import com.alibaba.sdk.android.oss.OSS;
import com.bit.communityProperty.R;
import com.bit.communityProperty.adapter.ListBaseAdapter;
import com.bit.communityProperty.fragment.main.bean.MainNewsBean;
import com.bit.communityProperty.utils.GlideUtils;
import com.bit.communityProperty.utils.OssManager;
import com.bit.communityProperty.utils.TimeUtils;

/**
 * Created by DELL60 on 2018/2/5.
 */

public class MainNewsAdapter extends ListBaseAdapter<MainNewsBean.RecordsBean> {

    private Context mContext;
    public MainNewsAdapter(Context context) {
        this.mContext = context;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(mContext).inflate(R.layout.item_main_news, parent, false));
    }

    @Override
    public int getItemCount() {
        return mDataList.size();
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ViewHolder viewHolder = (ViewHolder) holder;
        viewHolder.tvTitle.setText(mDataList.get(position).getTitle());
        viewHolder.tvDate.setText(TimeUtils.stampToDateWithHm(mDataList.get(position).getPublishAt()));
        viewHolder.tvContent.setText(mDataList.get(position).getBody());
        String url = OssManager.getInstance().getUrl(mDataList.get(position).getThumbnail());
        if(url!=null){
            if(url.length()>0){
                GlideUtils.loadImage(mContext,url,viewHolder.ivImg);
            }
        }
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        TextView tvTitle;
        TextView tvContent;
        TextView tvDate;
        ImageView ivImg;

        public ViewHolder(View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tv_title);
            tvContent = itemView.findViewById(R.id.tv_content);
            tvDate = itemView.findViewById(R.id.tv_date);
            ivImg = itemView.findViewById(R.id.iv_img);
        }
    }
}
