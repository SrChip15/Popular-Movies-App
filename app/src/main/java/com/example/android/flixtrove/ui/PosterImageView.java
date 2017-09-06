package com.example.android.flixtrove.ui;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatImageView;
import android.util.AttributeSet;

public class PosterImageView extends AppCompatImageView {
	private static final float ASPECT_RATIO = 1.5f;

	public PosterImageView(Context context) {
		super(context);
	}

	public PosterImageView(Context context, @Nullable AttributeSet attrs) {
		super(context, attrs);
	}

	public PosterImageView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		int width = MeasureSpec.getSize(widthMeasureSpec);
		int height = Math.round(width * ASPECT_RATIO);
		setMeasuredDimension(width, height);
	}
}
