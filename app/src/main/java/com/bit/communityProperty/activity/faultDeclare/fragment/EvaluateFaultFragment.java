package com.bit.communityProperty.activity.faultDeclare.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bit.communityProperty.R;
import com.bit.communityProperty.activity.faultDeclare.FaultDetailsSBActivity;
import com.bit.communityProperty.activity.faultManager.FaultDetailsActivity;
import com.bit.communityProperty.activity.faultManager.adapter.FaultManagerCommonAdapter;
import com.bit.communityProperty.activity.faultManager.bean.FaultManagementBean;
import com.bit.communityProperty.base.BaseEntity;
import com.bit.communityProperty.config.AppConfig;
import com.bit.communityProperty.net.Api;
import com.bit.communityProperty.net.RetrofitManage;
import com.bit.communityProperty.utils.GsonUtils;
import com.bit.communityProperty.utils.LogManager;
import com.bit.communityProperty.utils.SPUtil;
import com.github.jdsjlzx.interfaces.OnItemClickListener;
import com.github.jdsjlzx.interfaces.OnLoadMoreListener;
import com.github.jdsjlzx.interfaces.OnRefreshListener;
import com.github.jdsjlzx.recyclerview.LRecyclerView;
import com.github.jdsjlzx.recyclerview.LRecyclerViewAdapter;
import com.github.jdsjlzx.recyclerview.ProgressStyle;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

/**
 * 故障申报——待评价fragment
 * 其中：全部、待受理、待检修的fragment是跟故障管理的fragment公用的。
 * Created by kezhangzhao on 2018/3/10.
 */

public class EvaluateFaultFragment extends Fragment {

    private static final String ARG_SECTION_NUMBER = "section_number";
    private int pageIndex;//当前列表数据的页数
    private List<FaultManagementBean.RecordsBean> FaultManagementBeanList = new ArrayList<>();//数据列表
    private Context mContext;
    private LRecyclerView mRecyclerView;
    private FaultManagerCommonAdapter adapter;//普通物业人员的adapter
    private LRecyclerViewAdapter mLRecyclerViewAdapter;//上下拉的recyclerView的adapter
    private int TYPE;//(0：故障申报进来的，1：是故障管理进来的)
    /**
     * 服务器端一共多少条数据
     */
    private static int TOTAL_COUNTER = 0;

    /**
     * 每一页展示多少条数据
     */
    private static final int REQUEST_COUNT = 30;

    /**
     * 已经获取到多少条数据了
     */
    private static int mCurrentCounter = 0;

    public EvaluateFaultFragment() {
    }

    @Override
    public Context getContext() {
        return mContext;
    }

    public void setContext(Context context) {
        this.mContext = context;
    }

    private void initView(Context context,int type) {
        this.mContext = context;
        this.TYPE = type;
//        adapter = new FaultManagerCommonAdapter(context);
//        mLRecyclerViewAdapter = new LRecyclerViewAdapter(adapter);
    }


    public static EvaluateFaultFragment newInstance(int type, int sectionNumber, Context context) {
        EvaluateFaultFragment fragment = new EvaluateFaultFragment();
        fragment.initView(context,type);
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_fault_management, container, false);
        mRecyclerView = rootView.findViewById(R.id.lv_fault_management);
        adapter = new FaultManagerCommonAdapter(mContext);
        mLRecyclerViewAdapter = new LRecyclerViewAdapter(adapter);
        mRecyclerView.setAdapter(mLRecyclerViewAdapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        // 设置RecyclerView刷新加载样式
        mRecyclerView.setRefreshProgressStyle(ProgressStyle.LineSpinFadeLoader);
        mRecyclerView.setArrowImageView(R.drawable.ic_pulltorefresh_arrow);
        mRecyclerView.setLoadingMoreProgressStyle(ProgressStyle.BallSpinFadeLoader);
        // 刷新数据
        mRecyclerView.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh() {
                mCurrentCounter = 0;
                pageIndex = 1;
                mLRecyclerViewAdapter.removeFooterView();
                adapter.clear();
                mLRecyclerViewAdapter.notifyDataSetChanged();
                //网络请求获取列表数据
                FaultManagementBeanList.clear();
                //网络请求获取列表数据
                getFaultList("5a82adf3b06c97e0cd6c0f3d",null,null,"4",pageIndex,REQUEST_COUNT);
            }
        });

        //是否禁用自动加载更多功能
        mRecyclerView.setLoadMoreEnabled(true);

        // 加载更多
        mRecyclerView.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore() {

                if (mCurrentCounter < TOTAL_COUNTER) {
                    pageIndex++;
                    //网络请求获取列表数据
                    getFaultList("5a82adf3b06c97e0cd6c0f3d",null,null,"4",pageIndex,REQUEST_COUNT);
                } else {
                    mRecyclerView.setNoMore(true);
                }
            }
        });

        // RecyclewView的item点击事件监听
//        mLRecyclerViewAdapter.setOnItemClickListener(new MyOnItemClickListener());

        //设置头部加载颜色
        mRecyclerView.setHeaderViewColor(R.color.swipe_refresh_color1, R.color.swipe_refresh_color2, R.color.swipe_refresh_color3);
        //设置底部加载颜色
        mRecyclerView.setFooterViewColor(R.color.swipe_refresh_color1, R.color.swipe_refresh_color2, R.color.swipe_refresh_color3);
        //设置底部加载文字提示
        mRecyclerView.setFooterViewHint("拼命加载中", "已经全部为你呈现了", "网络不给力啊，点击再试一次吧");

        mRecyclerView.refresh();
        mLRecyclerViewAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {//跳转到故障的详情页面
                FaultManagementBean.RecordsBean bean = FaultManagementBeanList.get(position);
                Intent intent;
                if (TYPE == 0) {//故障申报详情页面
                    intent = new Intent(mContext, FaultDetailsSBActivity.class);
                } else {//故障管理详情页面
                    intent = new Intent(mContext, FaultDetailsActivity.class);
                }
                if (bean != null)
                    intent.putExtra("FaultID", bean.getId());
                startActivity(intent);
            }
        });
//            textView.setText(getString(R.string.section_format, getArguments().getInt(ARG_SECTION_NUMBER)));
        return rootView;
    }

    /**
     * 获取故障列表
     * @param communityId 社区ID 5a82adf3b06c97e0cd6c0f3d （必传参数）
     * @param faultType 故障类型 1：住户；2：公共；
     * @param faultItem 故障种类 1：水电煤气；2：房屋结构；3：消防安防；9：其它；10：电梯；11：门禁；99：其它；
     * @param faultStatus 故障状态 （0：已取消；1：已提交；2：已受理；3：已指派；4：已完成；-1：已驳回；）
     * @param page
     * @param size
     */
    private void getFaultList(String communityId,String faultType,
                              String faultItem,String faultStatus,int page,int size) {
        Map<String, Object> map = new HashMap();
        map.put("communityId", communityId);
        if (TYPE==0)
            map.put("userId", SPUtil.get(mContext, AppConfig.id, ""));//故障申报需要添加这个参数用户ID，因为只能看自己的申报单
        if (!TextUtils.isEmpty(faultType))
            map.put("faultType", faultType);
        if (!TextUtils.isEmpty(faultItem))
            map.put("faultItem", faultItem);
        if (!TextUtils.isEmpty(faultStatus))
            map.put("faultStatus", faultStatus);
        map.put("page", page);
        map.put("size", size);
        RetrofitManage.getInstance().subscribe(Api.getInstance().getFaultlList(map), new Observer<BaseEntity<FaultManagementBean>>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(BaseEntity<FaultManagementBean> baseEntity) {
                LogManager.printErrorLog("backinfo", GsonUtils.getInstance().toJson(baseEntity));
                if (baseEntity.getData() != null) {
                    TOTAL_COUNTER = baseEntity.getData().getTotal();
                    if (baseEntity.getData().getRecords() != null) {
                        FaultManagementBeanList.addAll(baseEntity.getData().getRecords());
                        addItems(baseEntity.getData().getRecords());
                    }
                }
                notifyDataSetChanged();
            }

            @Override
            public void onError(Throwable e) {
                mRecyclerView.refreshComplete(REQUEST_COUNT);
            }

            @Override
            public void onComplete() {
                mRecyclerView.refreshComplete(REQUEST_COUNT);
            }
        });
    }


    /**
     * 添加数据到列表
     *
     * @param list
     */
    private void addItems(List<FaultManagementBean.RecordsBean> list) {
        mCurrentCounter += list.size();
        adapter.addAll(list);
    }

    /**
     * 更新adapter数据
     */
    private void notifyDataSetChanged() {
        mLRecyclerViewAdapter.notifyDataSetChanged();
    }

}
