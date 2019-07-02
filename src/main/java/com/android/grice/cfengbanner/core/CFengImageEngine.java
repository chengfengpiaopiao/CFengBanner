package com.android.grice.cfengbanner.core;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapShader;
import android.graphics.BlurMaskFilter;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.graphics.Shader;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v7.graphics.Palette;
import android.widget.ImageView;
import android.widget.Toast;


import com.android.grice.cfengbanner.utils.DeviceUtils;

import org.reactivestreams.Publisher;

import java.io.InputStream;

import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.FlowableEmitter;
import io.reactivex.FlowableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

/**
 * <pre>
 *     Created by Gracie on 2019/5/10
 *     e-mail : 13574845807@163.com
 *     desc   :
 *     version: 1.0
 * </pre>
 */
public class CFengImageEngine {

    private static CFengImageEngine mCFengImageEngine;
    private static Context mContext;
    private static Bitmap mSourceBitmap;
    private static Paint mPaint;

    private CFengImageEngine() {
    }

    public static synchronized CFengImageEngine getInstance(Context context) {
        mContext = context;
        if (mCFengImageEngine == null) {
            synchronized (CFengImageEngine.class) {
                if (mCFengImageEngine == null) {
                    mCFengImageEngine = new CFengImageEngine();
                }
            }
        }
        return mCFengImageEngine;
    }

    public Bitmap readBitMap(Context context, int resId) {
        BitmapFactory.Options opt = new BitmapFactory.Options();
        opt.inPreferredConfig = Bitmap.Config.RGB_565;
        opt.inPurgeable = true;
        opt.inInputShareable = true;
        //获取资源图片
        InputStream is = context.getResources().openRawResource(resId);
        return BitmapFactory.decodeStream(is, null, opt);
    }

    public static Bitmap getShadowBitmap(Bitmap srcBitmap) {
        Paint shadowPaint = new Paint();
        BlurMaskFilter blurMaskFilter = new BlurMaskFilter(20, BlurMaskFilter.Blur.NORMAL);
        shadowPaint.setMaskFilter(blurMaskFilter);
        int[] offsetXY = new int[10];
        Bitmap shadowBitmap = srcBitmap.extractAlpha(shadowPaint, offsetXY);
        Bitmap canvasBgBitmap = Bitmap.createBitmap(shadowBitmap.getWidth(), shadowBitmap.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas();
        canvas.setBitmap(canvasBgBitmap);
        canvas.drawBitmap(shadowBitmap, 0, 0, shadowPaint);
        canvas.drawBitmap(srcBitmap, -offsetXY[0], -offsetXY[1], null);
        shadowBitmap.recycle();
        return canvasBgBitmap;
    }

    //圆角工厂
    public RoundedBitmapDrawable rectRoundBitmap(Bitmap bitmap, float radius) {
        RoundedBitmapDrawable roundImg = RoundedBitmapDrawableFactory.create(mContext.getResources(), bitmap);
        roundImg.setAntiAlias(true);
        roundImg.setCornerRadius(radius);
        return roundImg;
    }

    public void createCircleImage(final ImageView targetView,final int width,final int height, final Bitmap bitmap, final float radius, float shadowRadius,int shadowColor,final boolean isUsePalette,float imgDesplayScale) {
        final int mShadowRadius = (int) shadowRadius;
        final int mRadius = (int) radius;
        final int imageWidth = width - mShadowRadius * 2;
        final int imageHeight = height - mShadowRadius * 2;
        final Bitmap reSizeImage = reSizeImage(bitmap, imageWidth, imageHeight,imgDesplayScale);
        if (reSizeImage == null) {
            throw new NullPointerException("Bitmap can't be null");
        }
        final Flowable<Integer> platterFlow = Flowable.create(new FlowableOnSubscribe<Integer>() {
            @Override
            public void subscribe(final FlowableEmitter<Integer> emitter) throws Exception {
                Palette.from(reSizeImage).generate(new Palette.PaletteAsyncListener() {
                    @Override
                    public void onGenerated(Palette palette) {
                        int vibrantColor = palette.getDarkVibrantColor(Color.WHITE);
                        emitter.onNext(vibrantColor);
                    }
                });
            }
        }, BackpressureStrategy.BUFFER);
        Flowable.just(shadowColor).flatMap(new Function<Integer, Publisher<Integer>>() {
            @Override
            public Publisher<Integer> apply(Integer integer) throws Exception {
                if(isUsePalette){
                    return platterFlow;
                }
                return Flowable.just(integer);
            }
        }) .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Integer>() {
                    @Override
                    public void accept(Integer mShadowColor) throws Exception {
                        BitmapShader bitmapShader = new BitmapShader(reSizeImage, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
                        Bitmap targetBitmap = Bitmap.createBitmap(imageWidth, imageHeight, Bitmap.Config.ARGB_8888);
                        Canvas targetCanvas = new Canvas(targetBitmap);

                        mPaint = new Paint();
                        mPaint.setAntiAlias(true);
                        mPaint.setShader(bitmapShader);

                        RectF rect = new RectF(0, 0, imageWidth, imageHeight);
                        targetCanvas.drawRoundRect(rect, mRadius, mRadius, mPaint);

                        mPaint.setShader(null);
                        mPaint.setColor(mShadowColor);
                        mPaint.setShadowLayer(mShadowRadius, 1, 1, mShadowColor);
                        Bitmap target = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
                        Canvas canvas = new Canvas(target);

                        RectF rectF = new RectF(mShadowRadius, mShadowRadius, width - mShadowRadius, height - mShadowRadius);
                        canvas.drawRoundRect(rectF, mRadius, mRadius, mPaint);
                        mPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_OVER));
                        mPaint.setShadowLayer(0, 0, 0, 0xffffff);
                        canvas.drawBitmap(targetBitmap, mShadowRadius, mShadowRadius, mPaint);
                        targetView.setImageBitmap(target);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        Toast.makeText(mContext,throwable.getMessage(),Toast.LENGTH_LONG).show();
                    }
                });
    }

    /**
     * 重设Bitmap的宽高
     *
     * @param bitmap
     * @param newWidth
     * @param newHeight
     * @return
     */
    private static Bitmap reSizeImage(Bitmap bitmap, int newWidth, int newHeight,float imgDesplayScale) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        // 计算出缩放比
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        // 矩阵缩放bitmap
        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleHeight);
        return Bitmap.createBitmap(bitmap, 0, 0, width, height, matrix, false);
    }
}
