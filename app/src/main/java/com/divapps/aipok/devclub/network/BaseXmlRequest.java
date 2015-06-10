package com.divapps.aipok.devclub.network;

import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.divapps.aipok.devclub.network.additional.NoRetryPolicy;

import org.xml.sax.SAXException;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;

public abstract class BaseXmlRequest<T> extends Request<T> {

	protected static final String TAG = BaseXmlRequest.class.getSimpleName();
	
	protected final Map<String, String> headers;
    protected final Listener<T> listener;
	protected T responseObject;
	protected long startTime;
	
	public BaseXmlRequest(int method, String url, Listener<T> responseListener, ErrorListener errorListener) {
		this(method, url, null, responseListener, errorListener);
	}
	
	public BaseXmlRequest(int method, String url, Map<String, String> headers, Listener<T> responseListener, ErrorListener errorListener) {
		super(method, url, errorListener);
		this.listener = responseListener;
		this.headers = headers;
		startTime = System.nanoTime();
		setRetryPolicy(new NoRetryPolicy());
	}

	@Override
    public Map<String, String> getHeaders() throws AuthFailureError {
        return headers != null ? headers : super.getHeaders();
    }
	
	@Override
	protected Response<T> parseNetworkResponse(NetworkResponse response) {
		try {
			long parsingStart = System.nanoTime();
			parseResponse(response.data);
            Log.d(TAG, getClass().getSimpleName() + "[" + responseObject + "] "
                    + "completed in " + String.valueOf(TimeUnit.MILLISECONDS.convert(System.nanoTime() - startTime, TimeUnit.NANOSECONDS)) + " milliseconds"
                    + " and parsed in " + String.valueOf(TimeUnit.MILLISECONDS.convert(System.nanoTime() - parsingStart, TimeUnit.NANOSECONDS)) + " milliseconds");
			if(responseObject != null)
				return Response.success(responseObject, HttpHeaderParser.parseCacheHeaders(response));
			else
				return Response.error(new VolleyError(getClass().getSimpleName() + "request was not loaded properly and response object is null"));
        } catch (UnsupportedEncodingException e) {
            return Response.error(new VolleyError(e));
        } catch (NullPointerException e) {
			return Response.error(new VolleyError(e.getClass().getSimpleName() + ": " + e.getMessage()));
		} catch (Exception e) {
			return Response.error(new VolleyError(e.getClass().getSimpleName() + ": " + e.getMessage()));
		} 
	}
	
	protected void parseResponse(byte[] data) throws ParserConfigurationException, SAXException, IOException,
            XPathExpressionException, InterruptedException, ExecutionException, TimeoutException, XmlPullParserException {
		if(data == null || data.length == 0){
			throw new NullPointerException("response is empty");
		}
		parseResponseValues(data);
	}

	protected abstract void parseResponseValues(byte[] data) throws XPathExpressionException, ParserConfigurationException,
            SAXException, IOException, InterruptedException, ExecutionException, TimeoutException, XmlPullParserException;
	
	@Override
	protected void deliverResponse(T response) {
		listener.onResponse(response);
	}
}
