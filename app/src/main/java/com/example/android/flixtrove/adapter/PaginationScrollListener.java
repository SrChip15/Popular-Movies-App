package com.example.android.flixtrove.adapter;

import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;

public abstract class PaginationScrollListener extends RecyclerView.OnScrollListener {
	private final GridLayoutManager layoutManager;

	protected PaginationScrollListener(GridLayoutManager layoutManager) {
		this.layoutManager = layoutManager;
	}

	@Override
	public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
		super.onScrolled(recyclerView, dx, dy);

		int visibleItemCount = layoutManager.getChildCount();
		int totalItemCount = layoutManager.getItemCount();
		int firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition();

		// Pagination logic
		if (!isLoading() && !isLastPage()) {
			if ((visibleItemCount + firstVisibleItemPosition) >= totalItemCount
					&& firstVisibleItemPosition >= 0) {
				loadMoreItems();
			}
		}
	}

	// TODO - Enable upward pagination also

	protected abstract void loadMoreItems();

	@SuppressWarnings("unused")
	public abstract int getTotalPageCount();

	public abstract boolean isLastPage();

	public abstract boolean isLoading();
}
