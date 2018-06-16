package com.example.android.flixtrove.ui.adapter;

import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;

public abstract class PaginationScrollListener extends RecyclerView.OnScrollListener {
	private final GridLayoutManager layoutManager;
	@SuppressWarnings("UnusedAssignment")
	private int visibleThreshold = 4;  // number of rows below scroll position
	private int previousTotalItemCount = 0;
	private boolean loading;
	private int currentPage = 1;

	protected PaginationScrollListener(GridLayoutManager layoutManager) {
		this.layoutManager = layoutManager;
		visibleThreshold = visibleThreshold * layoutManager.getSpanCount();
	}

	@Override
	public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
		super.onScrolled(recyclerView, dx, dy);

		int lastVisibleItemPosition = layoutManager.findLastVisibleItemPosition();
		int totalItemCount = layoutManager.getItemCount();

		// Pagination logic
		if (!loading && (lastVisibleItemPosition + visibleThreshold > totalItemCount)) {
			loadMoreItems(++currentPage);
			loading = true;
		}

		if (loading && (previousTotalItemCount < totalItemCount)) {
			loading = false;
			previousTotalItemCount = totalItemCount;
		}
	}

	protected abstract void loadMoreItems(int page);
}
