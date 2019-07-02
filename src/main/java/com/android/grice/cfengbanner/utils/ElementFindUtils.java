package com.android.grice.cfengbanner.utils;

import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

/**
 * <pre>
 *     Created by Gracie on 2019/5/9
 *     e-mail : 13574845807@163.com
 *     desc   :
 *     version: 1.0
 * </pre>
 */
public class ElementFindUtils {
    /**
     * 从当前页面中查找所有的Spinner控件
     * @param group
     * @return
     */
    public static<T> T findTargetView(T targetView, ViewGroup group) {
        if (group != null) {
            for (int i = 0, j = group.getChildCount(); i < j; i++) {
                View child = group.getChildAt(i);
                if(child instanceof ImageView && targetView instanceof ImageView){
                    return (T) child;
                }
                if(child instanceof ViewGroup && targetView instanceof ViewGroup){
                    return (T) child;
                }
            }
        }
        return null;
    }
}
