package com.bit.communityProperty.activity.deviceManagement.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bit.communityProperty.R;
import com.bit.communityProperty.activity.deviceManagement.bean.CarBrakeBean;
import com.bit.communityProperty.activity.deviceManagement.bean.CarBrakeDetailBean;
import com.bit.communityProperty.activity.deviceManagement.bean.DeviceInfoBean;
import com.bit.communityProperty.activity.deviceManagement.bean.DoorControlDetailBean;
import com.bit.communityProperty.activity.deviceManagement.bean.ElevatorDetailBean;
import com.bit.communityProperty.adapter.ListBaseAdapter;
import com.bit.communityProperty.utils.TimeUtils;
import com.bit.communityProperty.view.CircleImageView;

/**
 * Created by kezhangzhao on 2018/2/20.
 */

public class DeviceInfoAdapter extends ListBaseAdapter {
    private Context mContext;
    private DeviceAdapter.ViewHolder viewHolder = null;

    public DeviceInfoAdapter(Context context) {
        this.mContext = context;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(mContext).inflate(R.layout.activity_device_info_item, parent, false));
    }

    @Override
    public int getItemCount() {
        return mDataList.size();
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ViewHolder viewHolder = (ViewHolder) holder;
        if (mDataList.get(position) instanceof CarBrakeDetailBean.RecordsBean) {
            CarBrakeDetailBean.RecordsBean bean = (CarBrakeDetailBean.RecordsBean) mDataList.get(position);
            switch (bean.getChargeType()) {
                case 1:
                    viewHolder.tvStatus.setText("月卡");
                    break;
                case 2:
                    viewHolder.tvStatus.setText("临时车");
                    break;
                case 3:
                    viewHolder.tvStatus.setText("免费车");
                    break;
                case 4:
                    viewHolder.tvStatus.setText("储值卡");
                    break;
            }
            viewHolder.tvName.setText(bean.getCarNo());
            viewHolder.tvId.setText(bean.getInTime());
            viewHolder.ivIcon.setImageResource(R.mipmap.ic_sbgl_che);
        } else if (mDataList.get(position) instanceof ElevatorDetailBean.RecordsBean) {
            ElevatorDetailBean.RecordsBean bean = (ElevatorDetailBean.RecordsBean) mDataList.get(position);
            viewHolder.tvName.setText(bean.getUserName());
            viewHolder.tvId.setText(bean.getTime());
            viewHolder.tvStatus.setText(bean.getUserCommand());
        }else if (mDataList.get(position) instanceof DoorControlDetailBean.RecordsBean){
            DoorControlDetailBean.RecordsBean bean = (DoorControlDetailBean.RecordsBean) mDataList.get(position);
            viewHolder.tvName.setText(bean.getUserName());
            viewHolder.tvId.setText(TimeUtils.stampToDate(bean.getTime()));
            viewHolder.tvStatus.setText(bean.getUserCommand());
        }
    }

    /**
     * adapter的ViewHolder
     */
    class ViewHolder extends RecyclerView.ViewHolder {

        CircleImageView ivIcon;//图标
        TextView tvName;//名字
        TextView tvId;//时间
        TextView tvStatus;//状态

        public ViewHolder(View itemView) {
            super(itemView);
            ivIcon = itemView.findViewById(R.id.iv_icon);
            tvName = itemView.findViewById(R.id.tv_name);
            tvStatus = itemView.findViewById(R.id.tv_status);
            tvId = itemView.findViewById(R.id.tv_id);
        }
    }
}
