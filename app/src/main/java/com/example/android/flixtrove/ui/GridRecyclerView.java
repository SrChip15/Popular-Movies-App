package com.example.android.flixtrove.ui;

import android.content.Context;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;

public class GridRecyclerView extends RecyclerView {
	private int mScrollPosition;

	public GridRecyclerView(Context context) {
		super(context);
	}

	public GridRecyclerView(Context context, @Nullable AttributeSet attrs) {
		super(context, attrs);
	}

	public GridRecyclerView(Context context, @Nullable AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	@Override
	protected Parcelable onSaveInstanceState() {
		Parcelable superState = super.onSaveInstanceState();

		LayoutManager layoutManager = getLayoutManager();

		if (layoutManager != null && layoutManager instanceof GridLayoutManager) {
			mScrollPosition = ((GridLayoutManager) layoutManager).findFirstVisibleItemPosition();
		}
		return null;


	}
}
