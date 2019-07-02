package com.android.grice.cfengbanner.banner;

/**
 * <pre>
 *     Created by Gracie on 2019/5/8
 *     e-mail : 13574845807@163.com
 *     desc   :
 *     version: 1.0
 * </pre>
 */
public enum CFengBannerType {
    NORMAL(1),CARD(2),GALLARY(3);

    private Integer mCurrentType;

    CFengBannerType(int index) {
        mCurrentType = index;
    }

    public Integer getValue() {
        return mCurrentType;
    }
}
