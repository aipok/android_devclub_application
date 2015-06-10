package com.divapps.aipok.devclub;

import android.app.Application;
import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

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
    private static RequestQueue queue;

    /**
     * Getter for application request pool.
     */
    public static RequestQueue getApplicationQueue() {
        return queue;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        appContext = getApplicationContext();
        queue = Volley.newRequestQueue(getApplicationContext());

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
