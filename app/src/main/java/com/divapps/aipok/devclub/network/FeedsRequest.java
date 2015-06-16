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
import com.divapps.aipok.devclub.models.FeedsResponseModel;
import com.divapps.aipok.devclub.models.ItemModel;
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
	private static final boolean LOG_ENABLED = false;

	private final Map<String, String> headers;
	private final Listener<FeedsResponseModel> listener;
	private FeedsResponseModel responseObject;

	public static final String URL = "http://feeds.feedburner.com/tedtalks_video";
	public static final String EMPTY = "";

	private static final String TAG_ITEM = "item";
	private static final String TAG_TITLE = "title";
	private static final String TAG_SUMMARY = "itunes:summary";
	private static final String TAG_PUBLICATION_DATE = "pubDate";
	private static final String TAG_DURATION = "itunes:duration";
	private static final String TAG_CONTENT = "media:content";
	private static final String TAG_IMAGE = "itunes:image";
	private static final String TAG_URL = "url";
	private static final String TAG_HREF = "href";

	String N = EMPTY;
	ItemModel tempItem;
	
	public FeedsRequest(Listener<FeedsResponseModel> responseListener, ErrorListener errorListener) {
		this(Method.GET, URL, null, responseListener, errorListener);
	}
	
	public FeedsRequest(int method, String url, Map<String, String> headers, Listener<FeedsResponseModel> responseListener, ErrorListener errorListener) {
		super(method, url, errorListener);
		if(responseObject == null) responseObject = new FeedsResponseModel();
		this.listener = responseListener;
		this.headers = headers;
		setRetryPolicy(new NoRetryPolicy(15000));
	}

	@Override
    public Map<String, String> getHeaders() throws AuthFailureError {
        return headers != null ? headers : super.getHeaders();
    }
	
	@Override
	protected Response<FeedsResponseModel> parseNetworkResponse(NetworkResponse response) {
		try {
			parseResponse(response.data);
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
					if(BuildConfig.DEBUG && LOG_ENABLED)
						Log.d(TAG, "START_DOCUMENT");
					break;
				// start of the tag
				case XmlPullParser.START_TAG:
					N = xpp.getName();
					if(BuildConfig.DEBUG && LOG_ENABLED)
						Log.d(TAG, "START_TAG: name = " + xpp.getName() + ", depth = " + xpp.getDepth() + ", attrCount = " + xpp.getAttributeCount());
					if (tempItem == null && TAG_IMAGE.equals(N)) {
						responseObject.coverImage = xpp.getAttributeValue(null, TAG_HREF);
						break;
					}
					if (TAG_ITEM.equals(N))
						tempItem = new ItemModel();
					else if (TAG_IMAGE.equals(N))
						tempItem.imageUrl = xpp.getAttributeValue(null, TAG_HREF);
					else if (TAG_CONTENT.equals(N))
						tempItem.mediaUrl = xpp.getAttributeValue(null, TAG_URL);



					break;
				// end of the tag
				case XmlPullParser.END_TAG:
					if(BuildConfig.DEBUG && LOG_ENABLED)
						Log.d(TAG, "END_TAG: name = " + xpp.getName());
					if(TAG_ITEM.equals(xpp.getName())){
						responseObject.items.add(tempItem);
						tempItem = null;
					}
					N = EMPTY;
					break;
				// content of the tag
				case XmlPullParser.TEXT:
					tmp = xpp.getText();
					if (BuildConfig.DEBUG && LOG_ENABLED && !TextUtils.isEmpty(tmp))
						Log.d(TAG, "text = " + tmp);
					if(tempItem != null) {
						if (TAG_TITLE.equals(N))
							tempItem.title = tmp;
						else if (TAG_SUMMARY.equals(N))
							tempItem.summary = tmp;
						else if (TAG_PUBLICATION_DATE.equals(N))
							tempItem.publicationDate = tmp;
						else if (TAG_DURATION.equals(N))
							tempItem.duration = tmp;
					}else{
						if (TAG_TITLE.equals(N))
							responseObject.title = tmp;
						else if (TAG_SUMMARY.equals(N))
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
