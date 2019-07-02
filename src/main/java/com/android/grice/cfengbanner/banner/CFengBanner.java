package com.android.grice.cfengbanner.banner;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.android.grice.cfengbanner.CFengBannerPagerAdapter;
import com.android.grice.cfengbanner.R;
import com.android.grice.cfengbanner.call.ICfengCallBack;
import com.android.grice.cfengbanner.transformer.CardTransformer;
import com.android.grice.cfengbanner.transformer.GalleryTransformer;
import com.android.grice.cfengbanner.utils.DeviceUtils;
import com.android.grice.cfengbanner.utils.ElementFindUtils;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.target.Target;

import org.reactivestreams.Publisher;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.FlowableEmitter;
import io.reactivex.FlowableOnSubscribe;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.functions.Predicate;

/**
 * <pre>
 *     Created by Gracie on 2019/5/8
 *     e-mail : 13574845807@163.com
 *     desc   :
 *     version: 1.0
 * </pre>
 */
@SuppressWarnings("unchecked")
public class CFengBanner extends RelativeLayout {

    private Context mContext;

    private int mBannerLayoutId;
    //自定义Item布局
    private int mBannerItemLayoutId;
    //自定义底部索引布局
    private int mBottomIndexContainerId;
    //顶部索引间距
    private float mIndexMargin;

    //是否自动滚动
    private boolean isAutoScroller;
    //自动滚动时长
    private int autoScrolletTime;
    //底部索引布局
    private View mBottomIndexView;
    //初始页索引
    private int cuurentPosition = 0;
    //选中shape
    private int selectedIndexShape;
    //非选中Shaoe
    private int unSelectedIndexShape;
    //底部索引集合
    private ArrayList<ImageView> mBtmContainerViews;

    //页面间距
    private float mViewPagerMargin;
    //Banner之间的间距
    private float mBannerMargin;
    //非选中Page的缩放比
    private float mUnCheckedPageScale;
    //是否允许过渡滑动
    private boolean isAllowOverScroller;

    //是否使用系统提供的加载图片框架
    private Boolean isUseSystemProvierUrl;
    //图片加载回调
    private ICfengCallBack mICfengCallBack;

    //图片圆角
    private float mImageRadius;
    //阴影半径
    private float mShadeRadius;
    //阴影颜色
    private int mShadeColor;
    //使用是否Palette
    private boolean isUsePalette;
    //图片大小
    private float imgDesplayWidth;
    private float imgDesplayHeight;
    //图片缩放尺寸
    private float imgDesplayScale;

    //定时器
    private Disposable subscribe;
    //定时器暂停
    private boolean isSuspendTimer = false;

    private CFengBannerType mCFengBannerType = CFengBannerType.NORMAL;

    private View mViewContainer;
    private ViewPager mTargetViewPager;
    //ViewPager状态
    private int currentPosition;
    private int mViewPagerId;

    public CFengBanner(Context context) {
        this(context, null);
    }

    public CFengBanner(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CFengBanner(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initAttrs(context, attrs);
    }

    private void initAttrs(Context context, AttributeSet attrs) {
        mContext = context;
        DeviceUtils.setContext(mContext);
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.CFengBanner);
        mBannerLayoutId = typedArray.getResourceId(R.styleable.CFengBanner_cf_banner_default_banner_view, R.layout.user_banner_layout);
        mBannerItemLayoutId = typedArray.getResourceId(R.styleable.CFengBanner_cf_banner_item_default, R.layout.banner_item);
        mBottomIndexContainerId = typedArray.getResourceId(R.styleable.CFengBanner_cf_banner_index_container, R.layout.index_default_circle_dot);
        mIndexMargin = typedArray.getFloat(R.styleable.CFengBanner_cf_index_margin, 0);
        selectedIndexShape = typedArray.getResourceId(R.styleable.CFengBanner_cf_index_select, R.drawable.cf_index_select);
        unSelectedIndexShape = typedArray.getResourceId(R.styleable.CFengBanner_cf_index_unselect, R.drawable.cf_index_unselected);
        isAutoScroller = typedArray.getBoolean(R.styleable.CFengBanner_cf_auto_scroller, false);
        autoScrolletTime = typedArray.getInt(R.styleable.CFengBanner_cf_auto_time, 3000);
        mViewPagerMargin = typedArray.getDimension(R.styleable.CFengBanner_cf_view_page_margin, 0f);
        mBannerMargin = typedArray.getDimension(R.styleable.CFengBanner_cf_banner_page_margin, 0f);
        mUnCheckedPageScale = typedArray.getFloat(R.styleable.CFengBanner_cf_un_checked_page_scale, 1);
        isAllowOverScroller = typedArray.getBoolean(R.styleable.CFengBanner_cf_allow_over_scroller, false);
        isUseSystemProvierUrl = typedArray.getBoolean(R.styleable.CFengBanner_cf_system_provider_load_url, false);
        //图片处理
        imgDesplayWidth = typedArray.getDimension(R.styleable.CFengBanner_cf_banner_img_width,0f);
        imgDesplayHeight = typedArray.getDimension(R.styleable.CFengBanner_cf_banner_img_height,0f);
        imgDesplayScale = typedArray.getFloat(R.styleable.CFengBanner_cf_banner_img_scale,1f);
        mImageRadius = typedArray.getDimension(R.styleable.CFengBanner_cf_img_radius, 0f);
        mShadeRadius = typedArray.getDimension(R.styleable.CFengBanner_cf_img_shade_radius, 0f);
        mShadeColor = typedArray.getColor(R.styleable.CFengBanner_cf_img_shade_color, Color.WHITE);
        isUsePalette = typedArray.getBoolean(R.styleable.CFengBanner_cf_use_palette, true);
        mViewContainer = LayoutInflater.from(context).inflate(mBannerLayoutId, null);
        mTargetViewPager = ElementFindUtils.findTargetView(new ViewPager(mContext), (ViewGroup) mViewContainer);
        if (mTargetViewPager == null) {
            mViewContainer = LayoutInflater.from(context).inflate(R.layout.user_banner_layout, null);
            mTargetViewPager = ElementFindUtils.findTargetView(new ViewPager(mContext), (ViewGroup) mViewContainer);
        }
        mViewPagerId = mTargetViewPager.getId();
        if (mViewPagerId < 0) {
            mViewPagerId = R.id.cf_view_pager;
            mTargetViewPager.setId(mViewPagerId);
            mTargetViewPager.setOverScrollMode(isAllowOverScroller ? View.OVER_SCROLL_ALWAYS : View.OVER_SCROLL_NEVER);
        }
        typedArray.recycle();
        addView(mViewContainer);
    }

    private <T> void initBottomIndex(List<T> bannerInfoLists) {
        LinearLayout linearLayout = new LinearLayout(mContext);
        linearLayout.removeAllViews();
        linearLayout.setOrientation(LinearLayout.HORIZONTAL);
        linearLayout.setGravity(Gravity.CENTER);
        mBtmContainerViews = new ArrayList<>();
        for (int i = 0; i < bannerInfoLists.size(); i++) {
            ImageView imageView = new ImageView(mContext);
            imageView.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                }
            });
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            params.gravity = Gravity.CENTER;
            params.setMargins(DeviceUtils.dip2px(mIndexMargin) / 2, 0, DeviceUtils.dip2px(mIndexMargin) / 2, 0);
            imageView.setLayoutParams(params);
            mBtmContainerViews.add(imageView);
            linearLayout.addView(imageView);
        }
        if (bannerInfoLists.size() < 2) {
            linearLayout.setVisibility(GONE);
        } else {
            this.removeAllViews();
            addView(mViewContainer);
            addView(linearLayout);
            RelativeLayout.LayoutParams layoutParams =
                    new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
            layoutParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
            linearLayout.setLayoutParams(layoutParams);
        }
        changeIndexViewBg(currentPosition);
    }

    private void changeIndexViewBg(int selectPositon) {
        for (int index = 0; index < mBtmContainerViews.size(); index++) {
            if (selectPositon == index) {
                mBtmContainerViews.get(index).setImageResource(selectedIndexShape);
            } else {
                mBtmContainerViews.get(index).setImageResource(unSelectedIndexShape);
            }
        }
    }
    private ArrayList<Bitmap> bannerBitmaps = new ArrayList<>();

    private ArrayList<Flowable<String>> creteImgFlows(final List<String> bannerInfoBeans){
        bannerBitmaps = new ArrayList<>();
        ArrayList<Flowable<String>> objects = new ArrayList<>();
        for (int index = 0 ;index < bannerInfoBeans.size() ; index ++){
            final String bannerInfoBean  = bannerInfoBeans.get(index);
            if(TextUtils.isEmpty(bannerInfoBean)){
                break;
            }
            Flowable<String> stringFlowable = Flowable.create(new FlowableOnSubscribe<String>() {
                @Override
                public void subscribe(final FlowableEmitter<String> emitter) throws Exception {
                    Glide.with(mContext)
                            .load(bannerInfoBean)
                            .asBitmap()
                            .listener(new RequestListener<String, Bitmap>() {
                                @Override
                                public boolean onException(Exception e, String model, Target<Bitmap> target, boolean isFirstResource) {
                                    emitter.onComplete();
                                    return false;
                                }

                                @Override
                                public boolean onResourceReady(Bitmap resource, String model, Target<Bitmap> target, boolean isFromMemoryCache, boolean isFirstResource) {
                                    return false;
                                }
                            }).into(new SimpleTarget<Bitmap>() {
                        @Override
                        public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                            bannerBitmaps.add(resource);
                            emitter.onNext(bannerInfoBean);
                            emitter.onComplete();
                        }
                    });
                }
            }, BackpressureStrategy.BUFFER).subscribeOn(AndroidSchedulers.mainThread());

            objects.add(stringFlowable);
        }
        return objects;
    }

    public <T> void startBanner(List<T> bannerInfoLists) {
        if (bannerInfoLists == null || bannerInfoLists.size() < 1) {
            return;
        }
        initBottomIndex(bannerInfoLists);
        if (isUseSystemProvierUrl && bannerInfoLists.get(0) instanceof String) {
            setVisibility(View.GONE);
            ArrayList<String> srcList = (ArrayList<String>) bannerInfoLists;
            Flowable.just(srcList).flatMap(new Function<List<String>, Publisher<String>>() {
                @Override
                public Publisher<String> apply(List<String> strings) throws Exception {
                    ArrayList<Flowable<String>> flowables = creteImgFlows(strings);
                    return Flowable.mergeDelayError((Iterable<? extends Publisher<? extends String>>) flowables);
                }
            }).doOnComplete(new Action() {
                @Override
                public void run() throws Exception {
                }
            }).doFinally(new Action() {
                @Override
                public void run() throws Exception {
                    if (bannerBitmaps.size() > 0) {
                        setVisibility(View.VISIBLE);
                        doRealRdener(bannerBitmaps);
                    } else {
                        setVisibility(View.GONE);
                    }
                }
            }).subscribe(new Consumer<String>() {
                @Override
                public void accept(String s) throws Exception {
                }
            }, new Consumer<Throwable>() {
                @Override
                public void accept(Throwable throwable) throws Exception {
                }
            });
            return;
        }
        doRealRdener(bannerInfoLists);
    }

    private <T> void doRealRdener(List<T> bannerInfoLists) {
        CFengBannerPagerAdapter<T> cFengBannerPagerAdapter = new CFengBannerPagerAdapter<T>(bannerInfoLists, mContext) {
            @Override
            protected int getLayoutId() {
                return mBannerItemLayoutId;
            }
        };
        cFengBannerPagerAdapter.setTICfengCallBack(mICfengCallBack);
        cFengBannerPagerAdapter.setImageRadius(mImageRadius);
        cFengBannerPagerAdapter.setShadeRadius(mShadeRadius);
        cFengBannerPagerAdapter.setShadeColor(mShadeColor);
        cFengBannerPagerAdapter.setUsePalette(isUsePalette);
        cFengBannerPagerAdapter.setWidth(imgDesplayWidth);
        cFengBannerPagerAdapter.setHeight(imgDesplayHeight);
        cFengBannerPagerAdapter.setImgDesplayScale(imgDesplayScale);
        switch (mCFengBannerType) {
            case NORMAL:
                break;
            case CARD:
                mTargetViewPager.setPageTransformer(false, new CardTransformer());
                ((ViewGroup) mViewContainer).setClipChildren(false);
                ((ViewGroup) mViewContainer).setClipToPadding(false);
                mTargetViewPager.setClipChildren(false);
                mTargetViewPager.setClipToPadding(false);
                break;
            case GALLARY:
                GalleryTransformer galleryTransformer = new GalleryTransformer();
                galleryTransformer.setUnCheckedPageScale(mUnCheckedPageScale);
                mTargetViewPager.setPageTransformer(false, galleryTransformer);
                mTargetViewPager.setPageMargin((int) mBannerMargin);
                ((ViewGroup) mViewContainer).setClipChildren(false);
                ((ViewGroup) mViewContainer).setClipToPadding(false);
                mTargetViewPager.setClipChildren(false);
                mTargetViewPager.setClipToPadding(false);
                break;
        }
        RelativeLayout.LayoutParams layoutParams = (LayoutParams) mTargetViewPager.getLayoutParams();
        layoutParams.setMargins((int) mViewPagerMargin, 0, (int) mViewPagerMargin, 0);
        mTargetViewPager.setLayoutParams(layoutParams);
        mTargetViewPager.setAdapter(cFengBannerPagerAdapter);
        mTargetViewPager.setOffscreenPageLimit(2);
        cuurentPosition = Integer.MAX_VALUE/2;
        mTargetViewPager.setCurrentItem(100000);
        mTargetViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                currentPosition = position % mBtmContainerViews.size();
                changeIndexViewBg(currentPosition);
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                if (subscribe == null) {
                    return;
                }
                if (state == 1 && !subscribe.isDisposed()) {
                    isSuspendTimer = true;
                    subscribe.dispose();
                } else {
                    if (subscribe.isDisposed()) {
                        isSuspendTimer = false;
                        startCountDown();
                    }
                }
            }
        });
        startCountDown();
    }

    private void startCountDown() {
        if (!isAutoScroller) {
            return;
        }
        subscribe = Observable.interval(3, TimeUnit.SECONDS).observeOn(AndroidSchedulers.mainThread()).takeWhile(new Predicate<Long>() {
            @Override
            public boolean test(Long aLong) throws Exception {
                return !isSuspendTimer;
            }
        }).subscribe(new Consumer<Long>() {
            @Override
            public void accept(Long aLong) throws Exception {
                int nextIndex = mTargetViewPager.getCurrentItem() + 1;
                mTargetViewPager.setCurrentItem(nextIndex);
            }
        });
    }

    public CFengBanner setBannerItemLayoutId(int bannerItemLayoutId) {
        mBannerItemLayoutId = bannerItemLayoutId;
        return this;
    }

    public CFengBanner setAutoScroller(boolean autoScroller) {
        isAutoScroller = autoScroller;
        return this;
    }

    public CFengBanner setCFengBannerType(CFengBannerType CFengBannerType) {
        mCFengBannerType = CFengBannerType;
        return this;
    }

    public <T> CFengBanner registerICfengCallBack(ICfengCallBack<T> ICfengCallBack) {
        mICfengCallBack = ICfengCallBack;
        return this;
    }
}
