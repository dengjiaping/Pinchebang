package com.lepin.inject;

import android.app.Activity;
import android.content.Context;
import android.view.View;

public class ViewFinder {

	private View view;
	private Activity activity;

	public ViewFinder(View view) {
		this.view = view;
	}

	public ViewFinder(Activity activity) {
		this.activity = activity;
	}

	public View findViewById(int id) {
		return activity == null ? view.findViewById(id) : activity.findViewById(id);
	}

	public View findViewById(int id, int pid) {
		View pView = null;
		if (pid > 0) {
			pView = this.findViewById(pid);
		}

		View view = null;
		if (pView != null) {
			view = pView.findViewById(id);
		} else {
			view = this.findViewById(id);
		}
		return view;
	}

	public Context getContext() {
		if (view != null) return view.getContext();
		if (activity != null) return activity;
		return null;
	}
}
