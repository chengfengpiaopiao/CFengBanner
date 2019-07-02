package com.android.grice.cfengbanner.call;

import android.view.View;

/**
 * <pre>
 *     Created by Gracie on 2018/8/10
 *     e-mail : 13574845807@163.com
 *     desc   :
 *     version: 1.0
 * </pre>
 */


public interface ICfengCallBack<T> {

    public void onClick(View currentView,Integer position,T data);
    public void onLoadError(View currentView,Integer position,T data);
    public void onLoadUrl(View currentView,Integer position,T data);
}
