package com.android.grice.cfengbanner.call;

import android.view.View;

/**
 * <pre>
 *     Created by Gracie on 2019/5/8
 *     e-mail : 13574845807@163.com
 *     desc   :
 *     version: 1.0
 * </pre>
 */
public abstract class ICfengAbsCall<T> implements ICfengCallBack<T> {
    public void onLoadError(View currentView, Integer position, T data){};
}
