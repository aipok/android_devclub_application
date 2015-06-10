package com.divapps.aipok.devclub.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.divapps.aipok.devclub.R;

public class LoadingView extends LinearLayout {

	private String textToShow;
	private int textColor;
	
	private OnClickListener clickListener = new OnClickListener() {
		@Override
		public void onClick(View v) { /* BLOCK ANY USER INTERACTION WHILE LOADING */ }
	};
	
	private OnTouchListener touchListener = new OnTouchListener() {
		@Override
		public boolean onTouch(View v, MotionEvent event) {
			/* BLOCK ANY USER INTERACTION WHILE LOADING */
			return true;
		}
	};
	
	public LoadingView(Context context)
	{
		super(context);
	}
	
	public LoadingView(Context context, AttributeSet attrs)
	{
		super(context, attrs);
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		inflater.inflate(R.layout.loading_view, this, true);
		
		if(!this.isInEditMode()){
			TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.LoadingView, 0, 0);
			try {
				textToShow = a.getNonResourceString(R.styleable.LoadingView_setText);
				if (textToShow == null) {
					textToShow = getResources().getString(
							a.getResourceId(R.styleable.LoadingView_setText,
									R.string.loading_title));
				}
			} catch(Exception ignored){ }
			textColor = a.getColor(R.styleable.LoadingView_setTextColor, getResources().getColor(R.color.black));
			a.recycle();
		}
		
		TextView tv = (TextView) findViewById(R.id.loading_text);
		if(tv != null){
			if(!TextUtils.isEmpty(textToShow))
				tv.setText(textToShow);
			tv.setTextColor(textColor);
		}
	}
	
	public void show(){
		if(getVisibility() == View.GONE || getVisibility() == View.INVISIBLE)
			setVisibility(View.VISIBLE);
	}
	
	public void hide(){
		if(getVisibility() == View.VISIBLE)
			setVisibility(View.GONE);
	}

	@Override
	protected void onAttachedToWindow() {
		super.onAttachedToWindow();
		setOnClickListener(clickListener);
		setOnTouchListener(touchListener);
	}

	@Override
	protected void onDetachedFromWindow() {
		setOnClickListener(null);
		setOnTouchListener(null);
		super.onDetachedFromWindow();
	}
	
	
}
