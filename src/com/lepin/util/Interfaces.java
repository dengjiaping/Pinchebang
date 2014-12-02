package com.lepin.util;

import android.content.Intent;
import android.util.SparseArray;
import android.view.View;

public class Interfaces {

	public interface ActivityResult {
		void onActivityResult(int requestCode, int resultCode, Intent data);
	}

	public interface SetCurrentAddrress {
		void setAddress(String address);
	}
/**
 * 用于各种adapter里面的ViewHolder,建议以后所有涉及到adapter 的时候都使用这个ViewHolder
 * @author zhiqiang
 *
 */
	public static class ViewHolder {

		@SuppressWarnings("unchecked")
		public static <T extends View> T get(View view, int id) {
			SparseArray<View> viewHolder = (SparseArray<View>) view.getTag();
			if (viewHolder == null) {
				viewHolder = new SparseArray<View>();
				view.setTag(viewHolder);
			}
			View childView = viewHolder.get(id);
			if (childView == null) {
				childView = view.findViewById(id);
				viewHolder.put(id, childView);
			}
			return (T) childView;
		}

	}
}
