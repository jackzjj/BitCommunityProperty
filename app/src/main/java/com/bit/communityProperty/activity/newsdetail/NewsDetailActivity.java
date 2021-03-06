package com.bit.communityProperty.activity.newsdetail;

import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.sdk.android.oss.ClientException;
import com.alibaba.sdk.android.oss.OSS;
import com.alibaba.sdk.android.oss.OSSClient;
import com.alibaba.sdk.android.oss.common.auth.OSSCredentialProvider;
import com.alibaba.sdk.android.oss.common.auth.OSSStsTokenCredentialProvider;
import com.bit.communityProperty.R;
import com.bit.communityProperty.base.BaseActivity;
import com.bit.communityProperty.base.BaseEntity;
import com.bit.communityProperty.fragment.main.bean.MainNewsDetailBean;
import com.bit.communityProperty.net.Api;
import com.bit.communityProperty.net.RetrofitManage;
import com.bit.communityProperty.utils.GlideUtils;
import com.bit.communityProperty.utils.LogManager;
import com.bit.communityProperty.utils.OssManager;
import com.bit.communityProperty.utils.StringUtils;
import com.bit.communityProperty.utils.TimeUtils;
import com.bit.communityProperty.utils.UploadInfo;

import butterknife.BindView;
import butterknife.OnClick;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

public class NewsDetailActivity extends BaseActivity {


    @BindView(R.id.action_bar_title)
    TextView actionBarTitle;
    @BindView(R.id.btn_back)
    ImageView btnBack;
    @BindView(R.id.btn_right_action_bar)
    TextView btnRightActionBar;
    @BindView(R.id.iv_right_action_bar)
    ImageView ivRightActionBar;
    @BindView(R.id.pb_loaing_action_bar)
    ProgressBar pbLoaingActionBar;
    @BindView(R.id.action_bar)
    RelativeLayout actionBar;
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.iv_img)
    ImageView ivImg;
    @BindView(R.id.tv_content)
    TextView tvContent;
    @BindView(R.id.tv_date)
    TextView tvDate;
    @BindView(R.id.tv_danwei)
    TextView tvDanwei;

    private String id;
    private UploadInfo uploadInfo;

    @Override
    public int getLayoutId() {
        return R.layout.activity_news_detail;
    }

    @Override
    public void initViewData() {
        actionBarTitle.setText("小区新闻");
        id = getIntent().getStringExtra("id");
        getDetail();
    }

    private void getDetail() {
        RetrofitManage.getInstance().subscribe(Api.getInstance().getNoticeDetail(id), new Observer<BaseEntity<MainNewsDetailBean>>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(BaseEntity<MainNewsDetailBean> stringBaseEntity) {
                if (stringBaseEntity.isSuccess()) {
                    MainNewsDetailBean bean = stringBaseEntity.getData();
                    if (bean != null) {
                        tvTitle.setText(bean.getTitle());
                        tvDate.setText(TimeUtils.stampToDate(bean.getPublishAt()));
                        tvDanwei.setText(bean.getEditorName());
                        tvContent.setText(bean.getBody());
                        String url = OssManager.getInstance().getUrl(bean.getThumbnailUrl());
                        LogManager.i(url);
                        GlideUtils.loadImage(mContext,url,ivImg);
                    }
                }
            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onComplete() {

            }
        });
    }

    @OnClick({R.id.btn_back})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_back:
                finish();
                break;
        }
    }
}
