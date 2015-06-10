package com.divapps.aipok.devclub.network;

import android.text.TextUtils;
import android.util.Log;
import android.util.Xml;

import com.android.volley.Request;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.divapps.aipok.devclub.BuildConfig;
import com.divapps.aipok.devclub.models.FeedModel;
import com.divapps.aipok.devclub.models.FeedsResponseModel;
import com.divapps.aipok.devclub.network.additional.NoRetryPolicy;

import org.xml.sax.SAXException;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;

public class FeedsRequest extends BaseXmlRequest<FeedsResponseModel> {

    public static final String URL = "http://www.devclub.eu/feed/";
    public static final String EMPTY = "";

    private static final String ITEM = "item";
    private static final String TITLE = "title";
    private static final String DESCRIPTION = "description";
    private static final String PUBLICATION_DATE = "pubDate";

    String N = EMPTY;
    FeedModel tempItem;

    /**
     * Creates a new get request.
     *
     * @param listener Listener to receive the String response
     * @param errorListener Error listener, or null to ignore errors
     */
    public FeedsRequest(Listener<FeedsResponseModel> listener, ErrorListener errorListener) {
        super(Request.Method.GET, URL, listener, errorListener);
        if(responseObject == null) responseObject = new FeedsResponseModel();
        setRetryPolicy(new NoRetryPolicy(15000));
    }

	@Override
	protected void parseResponseValues(byte[] data) throws XPathExpressionException, ParserConfigurationException,
            SAXException, IOException, InterruptedException, ExecutionException, TimeoutException, XmlPullParserException {
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
                        tempItem = new FeedModel();

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
}
