package com.lepin.inject;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import android.app.Activity;
import android.view.View;

public class ViewInjectUtil {
//	private static ConcurrentHashMap<Class<? extends Annotation>, ViewCustomEventListener> annotationType_viewCustomEventListener_map;

	public static void inject(Activity activity) {
		doInject(activity, new ViewFinder(activity));

	}

	public static void inject(Object handler, View view) {
		doInject(handler, new ViewFinder(view));
	}

	public static void doInject(Object handle, ViewFinder finder) {
		Class<?> handleType = handle.getClass();

		Contextview mContextview = handleType.getAnnotation(Contextview.class);
		try {
			if (mContextview != null) {
				Method method = handleType.getMethod("setContentView", int.class);
				method.invoke(handle, mContextview.value());
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		Field[] files = handleType.getDeclaredFields();
		if (files != null && files.length > 0) {
			for (Field field : files) {
				ViewInject viewInject = field.getAnnotation(ViewInject.class);
				if (viewInject != null) {
					View view = finder.findViewById(viewInject.id(), viewInject.parentId());
					if (view != null) {
						try {
							field.setAccessible(true);
							field.set(handle, view);
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				}
			}
		}

	}
}
