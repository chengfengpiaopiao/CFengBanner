package com.android.grice.cfengbanner;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.android.grice.cfengbanner.call.ICfengCallBack;
import com.android.grice.cfengbanner.core.CFengImageEngine;
import com.android.grice.cfengbanner.utils.ElementFindUtils;

import java.util.List;

public abstract class CFengBannerPagerAdapter<T> extends PagerAdapter {
    private List<T> mList;
    private Context mContext;
    private ICfengCallBack<T> mTICfengCallBack;
    private ImageView mTargetImageView;

    public CFengBannerPagerAdapter(List<T> list, Context context) {
        this.mList = list;
        this.mContext = context;
    }

    @Override
    public int getCount() {
        return Integer.MAX_VALUE;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

    @Override
    public Object instantiateItem(ViewGroup container, final int position) {
        View convertView = LayoutInflater.from(mContext).inflate(getLayoutId(), container, false);
        mTargetImageView = ElementFindUtils.findTargetView(new ImageView(mContext), (ViewGroup) convertView);
        if (mTargetImageView == null) {
            mTargetImageView = new ImageView(mContext);
        }
        final int index = position % mList.size();
        if (mList.get(index) instanceof String) {
            if (mTICfengCallBack != null) {
                mTICfengCallBack.onLoadUrl(mTargetImageView, index, mList.get(index));
            }
        } else if (mList.get(index) instanceof Integer) {
            Bitmap bitmap = new BitmapDrawable(CFengImageEngine.getInstance(mContext).readBitMap(mContext, (Integer) mList.get(index))).getBitmap();
            if (mImageRadius > 0  || mShadeRadius > 0) {
                dealBitmapDisplaySize(bitmap);
                CFengImageEngine.getInstance(mContext).createCircleImage(mTargetImageView, imgDesplayWidth, imgDesplayHeight, bitmap, mImageRadius, mShadeRadius, mShadeColor,isUsePalette,imgDesplayScale);
            } else {
                mTargetImageView.setImageBitmap(bitmap);
            }
        } else if (mList.get(index) instanceof Bitmap) {
            if (mImageRadius > 0 || mShadeRadius > 0) {
                dealBitmapDisplaySize((Bitmap)(mList.get(index)));
                CFengImageEngine.getInstance(mContext).createCircleImage(mTargetImageView, imgDesplayWidth, imgDesplayHeight, (Bitmap) mList.get(index), mImageRadius, mShadeRadius, mShadeColor,isUsePalette,imgDesplayScale);
            } else {
                mTargetImageView.setImageBitmap((Bitmap) mList.get(index));
            }
        }
        mTargetImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mTICfengCallBack != null) {
                    mTICfengCallBack.onClick(mTargetImageView, index, mList.get(index));
                }
            }
        });
        container.addView(convertView);
        return convertView;
    }

    private void dealBitmapDisplaySize(Bitmap bitmap){
        if(imgDesplayWidth <= 0 && imgDesplayHeight <= 0){
            imgDesplayWidth = bitmap.getWidth();
            imgDesplayHeight =bitmap.getHeight();
        }
        if(imgDesplayScale != 1){
            imgDesplayWidth = (int) (imgDesplayWidth * imgDesplayScale);
            imgDesplayHeight = (int) (imgDesplayHeight * imgDesplayScale);
        }
        imgDesplayScale = 1;
    }

    /**
     *
     * @param TICfengCallBack 设置回调
     */
    public void setTICfengCallBack(ICfengCallBack<T> TICfengCallBack) {
        this.mTICfengCallBack = TICfengCallBack;
    }

    //图片宽度
    private int imgDesplayWidth;
    private int imgDesplayHeight;

    public void setWidth(float width) {
        this.imgDesplayWidth = (int) width;
    }

    public void setHeight(float height) {
        this.imgDesplayHeight = (int) height;
    }

    //图片圆角
    private float mImageRadius;
    //阴影半径
    private float mShadeRadius;
    //阴影颜色
    private int mShadeColor;
    private boolean isUsePalette;
    //图片缩放尺寸
    private float imgDesplayScale;

    public void setImgDesplayScale(float imgDesplayScale) {
        this.imgDesplayScale = imgDesplayScale;
    }

    public void setUsePalette(boolean usePalette) {
        isUsePalette = usePalette;
    }

    public void setImageRadius(float imageRadius) {
        mImageRadius = imageRadius;
    }

    public void setShadeRadius(float shadeRadius) {
        mShadeRadius = shadeRadius;
    }

    public void setShadeColor(int shadeColor) {
        mShadeColor = shadeColor;
    }

    protected abstract int getLayoutId();
}