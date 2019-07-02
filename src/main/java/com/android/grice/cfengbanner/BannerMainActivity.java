package com.android.grice.cfengbanner;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.grice.cfengbanner.banner.CFengBanner;
import com.android.grice.cfengbanner.banner.CFengBannerType;
import com.android.grice.cfengbanner.call.ICfengAbsCall;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.target.Target;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;


/**
 * <pre>
 *     Created by Gracie on 2019/5/8
 *     e-mail : 13574845807@163.com
 *     desc   :
 *     version: 1.0
 * </pre>
 */
public class BannerMainActivity extends Activity {


    @BindView(R.id.view_pager)
    CFengBanner mCFengBanner;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_banner_main);
        ButterKnife.bind(this);
        ArrayList<Integer> bannerImgLists = new ArrayList<>();
        bannerImgLists.add(R.drawable.banner_lunbo2);
        bannerImgLists.add(R.drawable.banner_lunbo);
        //bannerImgLists.add(R.drawable.vds_home_banner);

        ArrayList<String> urlList = new ArrayList<>();
        urlList.add("https://vdsapi.sla5.com/img/banner/vds_banner_4.png");
        urlList.add("https://vdsapi.sla5.com/img/banner/vds_banner_3.png");
        mCFengBanner.setCFengBannerType(CFengBannerType.GALLARY).registerICfengCallBack(new ICfengAbsCall<Object>() {
            @Override
            public void onClick(View currentView, Integer position, Object data) {
                Toast.makeText(BannerMainActivity.this,"点击第" + position + "个item",Toast.LENGTH_LONG).show();
            }

            @Override
            public void onLoadUrl(final View currentView, Integer position, Object srcData) {
                String data = (String) srcData;
                Glide.with(BannerMainActivity.this)
                        .load(data)
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .listener(new RequestListener<String, GlideDrawable>() {
                            @Override
                            public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                                return false;
                            }

                            @Override
                            public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                                return false;
                            }
                        })
                        .into(new SimpleTarget<GlideDrawable>() {
                            @Override
                            public void onResourceReady(GlideDrawable resource,
                                                        GlideAnimation<? super GlideDrawable> glideAnimation) {
                                if((ImageView) currentView != null && resource != null){
                                    ((ImageView) currentView).setImageDrawable(resource);
                                }
                            }
                        });
            }
        }).startBanner(urlList);

    }
}
