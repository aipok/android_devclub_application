package com.divapps.aipok.devclub;

import android.app.Application;
import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;
import com.divapps.aipok.devclub.network.cache.LruBitmapCache;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLSession;

/**
 * Created by Vitali Nartov on 10.06.2015.
 * Application class and data holder
 */
public class App extends Application {

    public static Context appContext;
    /**
     * Application request pool for Volley requests.
     */
    private RequestQueue queue;

    /**
     * Image loader application instance.
     */
    private ImageLoader imageLoader;
    public static ImageLoader getLoader(){
        return app().imageLoader;
    }

    private static App app;
    public static App app(){
        return app;
    }

    private static String deviceType;
    public static boolean isPhone(){
        return "phone".equals(deviceType);
    }

    public static boolean isTablet(){
        return isTablet600() || isTablet720();
    }

    public static boolean isTablet600(){
        return "sw600dp".equals(deviceType);
    }

    public static boolean isTablet720(){
        return "sw720dp".equals(deviceType);
    }

    /**
     * Getter for application request pool.
     */
    public static RequestQueue getApplicationQueue() {
        return app().queue;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        app = this;
        deviceType = getApplicationContext().getResources().getString(R.string.device_type);
        appContext = getApplicationContext();
        queue = Volley.newRequestQueue(getApplicationContext());
        imageLoader = new ImageLoader(queue, new LruBitmapCache(LruBitmapCache.getCacheSize(appContext)));

        setDefaultVerifier();
    }

    private void setDefaultVerifier() {
        HostnameVerifier allHostsValid = new HostnameVerifier() {
            @Override
            public boolean verify(String hostname, SSLSession session) {
                return true;
            }
        };
        // Install the all-trusting host verifier
        HttpsURLConnection.setDefaultHostnameVerifier(allHostsValid);
    }

    public static void addRequestToQueueWithTag(RequestQueue rq, Request<?> request, String tag){
        request.setTag(tag);
        rq.add(request);
    }

    @SuppressWarnings("UnusedDeclaration")
    public static void addRequestToQueueWithTag(RequestQueue rq, Request<?> request, boolean clearPreviousRequestWithTag){
        final String tag = request.getClass().getSimpleName();
        if(clearPreviousRequestWithTag) rq.cancelAll(new RequestQueue.RequestFilter() {
            @Override
            public boolean apply(Request<?> request) {
                return tag.equals(request.getTag());
            }
        });
        request.setTag(tag);
        rq.add(request);
    }

    @SuppressWarnings("UnusedDeclaration")
    public static void clearRequestByTag(RequestQueue rq, final String tag){
        rq.cancelAll(new RequestQueue.RequestFilter() {
            @Override
            public boolean apply(Request<?> request) {
                return tag.equals(request.getTag());
            }
        });
    }
}
