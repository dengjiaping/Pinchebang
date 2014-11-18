package com.lepin.loadimage;

import java.util.Collections;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.widget.ImageView;

import com.lepin.activity.R;
import com.lepin.util.HttpUtil;
import com.lepin.util.Util;

public class ImageLoader {

	MemoryCache memoryCache = new MemoryCache();
	FileCache fileCache;
	private Map<ImageView, String> imageViews = Collections
			.synchronizedMap(new WeakHashMap<ImageView, String>());
	ExecutorService executorService;
	private Context mContext;

	private static class ImageLoaderHolder {
		private static final ImageLoader LOADER = new ImageLoader();
	}

	private ImageLoader() {
	}

	public static ImageLoader getInstance(Context mContext) {
		return ImageLoaderHolder.LOADER.setContext(mContext);
	}

	public ImageLoader setContext(Context context) {
		mContext = context;
		fileCache = new FileCache(context);
		executorService = Executors.newFixedThreadPool(5);
		return this;
	}

	final int stub_id = R.drawable.pcb_home_photo;

	/**
	 * 
	 * @param url
	 *            图片url
	 * @param imageView
	 *            要展示图片的控件
	 * @param isSave2Sdcard
	 *            是否存储在sd
	 * @param isSave2Memory
	 *            是否缓存到内存中
	 */
	public void DisplayImage(String url, ImageView imageView, boolean isSave2Sdcard,
			boolean isSave2Memory) {
		Util.printLog("图片路径：" + url);
		imageViews.put(imageView, url);
		if (isSave2Memory) {
			Bitmap bitmap = memoryCache.get(url);// 从内存中取
			if (bitmap != null)
				imageView.setImageBitmap(bitmap);
			else {
				queuePhoto(url, imageView, isSave2Sdcard);
				// 如果还没有设置图片，就设置一张默认的
			}
		} else {
			queuePhoto(url, imageView, isSave2Sdcard);
		}
	}

	private void queuePhoto(String url, ImageView imageView, boolean isSave) {
		PhotoToLoad p = new PhotoToLoad(url, imageView);
		executorService.submit(new PhotosLoader(p, isSave));
	}

	// /**
	// * 从ＳＤ卡取图片，没用再从网络下载
	// *
	// * @param url
	// * @param imageView
	// * @return
	// */
	// private Bitmap getBitmap(String url, ImageView imageView, boolean isSave)
	// {
	// File f = fileCache.getFile(url);
	// // from SD cache
	// if (isSave) {
	// Bitmap b = decodeFile(f, imageView);
	// if (b != null) return b;
	// }
	// if (!Util.getInstance().isNetworkAvailable(imageView.getContext()))
	// return null;
	// // download image
	// try {
	// Bitmap bitmap = null;
	// URL imageUrl = new URL(url);
	// HttpURLConnection conn = (HttpURLConnection) imageUrl.openConnection();
	// conn.setConnectTimeout(30000);
	// conn.setReadTimeout(30000);
	// conn.setInstanceFollowRedirects(true);
	// InputStream is = conn.getInputStream();
	// OutputStream os = new FileOutputStream(f);
	// Util.getInstance().copyStream(is, os);// 是否存到sd卡
	// os.close();
	// bitmap = decodeFile(f, imageView);
	// is.close();
	// if (!isSave) f.delete();// 删除图片
	// return bitmap;
	// } catch (Throwable ex) {
	// ex.printStackTrace();
	// if (ex instanceof OutOfMemoryError) memoryCache.clear();
	// return null;
	// }
	// }

	// Task for the queue
	private class PhotoToLoad {
		public String url;
		public ImageView imageView;

		public PhotoToLoad(String u, ImageView i) {
			url = u;
			imageView = i;
		}
	}

	class PhotosLoader implements Runnable {
		PhotoToLoad photoToLoad;
		boolean isSave2Sdcard = false;

		PhotosLoader(PhotoToLoad photoToLoad, boolean is) {
			this.photoToLoad = photoToLoad;
			this.isSave2Sdcard = is;
		}

		@Override
		public void run() {
			if (imageViewReused(photoToLoad)) return;
			Bitmap bmp = HttpUtil.getBitmap(photoToLoad.url, fileCache.getFile(photoToLoad.url),
					photoToLoad.imageView, isSave2Sdcard, memoryCache);
			// Bitmap bmp = getBitmap(photoToLoad.url, photoToLoad.imageView,
			// isSave2Sdcard);
			if (bmp == null) return;// sd卡没有图片，也没有下载成功
			memoryCache.put(photoToLoad.url, bmp);
			BitmapDisplayer bd = new BitmapDisplayer(bmp, photoToLoad);
			Activity a = (Activity) photoToLoad.imageView.getContext();
			a.runOnUiThread(bd);
		}
	}

	/**
	 * 检查要下载的图片的ＵＲＬ存在不
	 * 
	 * @param photoToLoad
	 * @return
	 */
	boolean imageViewReused(PhotoToLoad photoToLoad) {
		String url = imageViews.get(photoToLoad.imageView);
		if (url == null || !url.equals(photoToLoad.url)) return true;
		return false;
	}

	// Used to display bitmap in the UI thread
	class BitmapDisplayer implements Runnable {
		Bitmap bitmap;
		PhotoToLoad photoToLoad;

		public BitmapDisplayer(Bitmap b, PhotoToLoad p) {
			bitmap = b;
			photoToLoad = p;
		}

		public void run() {
			if (imageViewReused(photoToLoad)) return;
			if (bitmap != null)
				photoToLoad.imageView.setImageBitmap(bitmap);
			else photoToLoad.imageView.setImageResource(stub_id);
		}
	}

	public void clearCache() {
		memoryCache.clear();
		fileCache.clear();
	}

}
