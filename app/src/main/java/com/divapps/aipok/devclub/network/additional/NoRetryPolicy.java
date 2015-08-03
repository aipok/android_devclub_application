package com.divapps.aipok.devclub.network.additional;

import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;

public class NoRetryPolicy implements RetryPolicy {
	/** The current timeout in milliseconds. */
    private int mCurrentTimeoutMs;

    /**
     * Constructs a new retry policy with retry count 0.
     * @param initialTimeoutMs The initial timeout for the policy.
     */
    public NoRetryPolicy(int initialTimeoutMs) {
        mCurrentTimeoutMs = initialTimeoutMs;
    }
	
	@Override
	public int getCurrentTimeout() {
		return mCurrentTimeoutMs;
	}

	@Override
	public int getCurrentRetryCount() {
		return 0;
	}

	@Override
	public void retry(VolleyError error) throws VolleyError {
		throw error;
	}

}
