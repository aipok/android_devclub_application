package com.divapps.aipok.devclub.network;

import android.text.TextUtils;
import android.util.Log;
import android.util.Xml;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.divapps.aipok.devclub.BuildConfig;
import com.divapps.aipok.devclub.models.ItemModel;
import com.divapps.aipok.devclub.models.FeedsResponseModel;
import com.divapps.aipok.devclub.network.additional.NoRetryPolicy;

import org.xml.sax.SAXException;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;

public class FeedsRequest extends Request<FeedsResponseModel> {

	public static final String TAG = FeedsRequest.class.getSimpleName();

	private final Map<String, String> headers;
	private final Listener<FeedsResponseModel> listener;
	private FeedsResponseModel responseObject;

	public static final String URL = "http://feeds.feedburner.com/tedtalks_video";
	public static final String EMPTY = "";

	private static final String ITEM = "item";
	private static final String TITLE = "title";
	private static final String DESCRIPTION = "description";
	private static final String PUBLICATION_DATE = "pubDate";

	String N = EMPTY;
	ItemModel tempItem;
	
	public FeedsRequest(Listener<FeedsResponseModel> responseListener, ErrorListener errorListener) {
		this(Method.GET, URL, null, responseListener, errorListener);
	}
	
	public FeedsRequest(int method, String url, Map<String, String> headers, Listener<FeedsResponseModel> responseListener, ErrorListener errorListener) {
		super(method, url, errorListener);
		this.listener = responseListener;
		this.headers = headers;
		setRetryPolicy(new NoRetryPolicy());
	}

	@Override
    public Map<String, String> getHeaders() throws AuthFailureError {
        return headers != null ? headers : super.getHeaders();
    }
	
	@Override
	protected Response<FeedsResponseModel> parseNetworkResponse(NetworkResponse response) {
		try {
			if(responseObject != null)
				return Response.success(responseObject, HttpHeaderParser.parseCacheHeaders(response));
			else
				return Response.error(new VolleyError(getClass().getSimpleName() + "request was not loaded properly and response object is null"));
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
		parseFeeds(data);
	}

	private void parseFeeds(byte[] data) throws XmlPullParserException, IOException {
		XmlPullParser xpp = Xml.newPullParser();
		xpp.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
		xpp.setInput(new ByteArrayInputStream(data), null);

		String tmp;

		while (xpp.getEventType() != XmlPullParser.END_DOCUMENT) {
			switch (xpp.getEventType()) {
				// start of the document
				case XmlPullParser.START_DOCUMENT:
					if(BuildConfig.DEBUG)
						Log.d(TAG, "START_DOCUMENT");
					break;
				// start of the tag
				case XmlPullParser.START_TAG:
					N = xpp.getName();
					if(BuildConfig.DEBUG)
						Log.d(TAG, "START_TAG: name = " + xpp.getName() + ", depth = " + xpp.getDepth() + ", attrCount = " + xpp.getAttributeCount());
					tmp = EMPTY;
					if(ITEM.equals(N))
						tempItem = new ItemModel();

					break;
				// end of the tag
				case XmlPullParser.END_TAG:
					if(BuildConfig.DEBUG)
						Log.d(TAG, "END_TAG: name = " + xpp.getName());
					if(ITEM.equals(xpp.getName())){
						responseObject.items.add(tempItem);
						tempItem = null;
					}
					N = EMPTY;
					break;
				// content of the tag
				case XmlPullParser.TEXT:
					tmp = xpp.getText();
					if (BuildConfig.DEBUG && !TextUtils.isEmpty(tmp))
						Log.d(TAG, "text = " + tmp);
					if(tempItem != null) {
						if (TITLE.equals(N))
							tempItem.title = tmp;
						else if (DESCRIPTION.equals(N))
							tempItem.description = tmp;
						else if (PUBLICATION_DATE.equals(N))
							tempItem.publicationDate = tmp;
					}else{
						if (TITLE.equals(N))
							responseObject.title = tmp;
						else if (DESCRIPTION.equals(N))
							responseObject.description = tmp;
					}
					break;
				default:
					break;
			}
			// next tag
			xpp.next();
		}
	}
	
	@Override
	protected void deliverResponse(FeedsResponseModel response) {
		listener.onResponse(response);
	}
}
