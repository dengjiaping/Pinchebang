package com.lepin.loadimage;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.widget.ImageView;

import com.lepin.util.HttpUtil;

public class ImageGetFromHttp {
	private Bitmap bitmap = null;

	public synchronized Bitmap downloadBitmap(final String url, final ImageView imageView,
			final Context mContext) {
		try {
			new Thread() {
				public void run() {
					try {
//						bitmap = getUrlData(url, mContext);
						bitmap = HttpUtil.downloadImage_conn(url);
						if (null != bitmap) {
							BitmapDisplayer bd = new BitmapDisplayer(bitmap, imageView);
							Activity a = (Activity) imageView.getContext();
							a.runOnUiThread(bd);
						}
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}.start();
			return bitmap;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return bitmap;
	}

	class BitmapDisplayer implements Runnable {
		Bitmap mBitmap;
		ImageView mImageView;

		public BitmapDisplayer(Bitmap b, ImageView p) {
			mBitmap = b;
			mImageView = p;
		}

		public void run() {
			if (bitmap != null) mImageView.setImageBitmap(bitmap);
		}
	}

	// public Bitmap getUrlData(String imgURL, Context mContext) throws
	// Exception {
	// ByteArrayOutputStream bos = null; // 内存操作流
	// try {
	// URL url = new URL(imgURL);
	// bos = new ByteArrayOutputStream();
	// byte buffer[] = new byte[1024];
	// HttpURLConnection conn = (HttpURLConnection) url.openConnection();
	// conn.setDoInput(true);
	// conn.setDoOutput(true);
	// conn.setRequestMethod("POST");
	// conn.setUseCaches(false);
	// List<Cookie> cookies = HttpUtil.bcs.getCookies();
	// if (cookies != null) {
	// for (int i = 0; i < cookies.size(); i++) {
	// String key = String.valueOf(cookies.get(i).getName());
	// String value = String.valueOf(cookies.get(i).getValue());
	// Util.printLog(key + "=" + value);
	// conn.addRequestProperty("Cookie", key + "=" + value);
	// }
	// }
	// conn.connect();
	// OutputStream osOutputStream = conn.getOutputStream();
	// DataOutputStream dos = new DataOutputStream(osOutputStream);
	// dos.close();
	// InputStream input = conn.getInputStream();
	// int len = -1;
	// while ((len = input.read(buffer)) != -1) {
	// bos.write(buffer, 0, len);
	// }
	// byte[] data = bos.toByteArray();
	// bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
	// // bitmap = HttpUtil.downloadImage(imgURL);
	// } catch (Exception e) {
	// throw e;
	// } finally {
	// if (bos != null) {
	// bos.close();
	// }
	// }
	// return bitmap;
	// }

	/*
	 * An InputStream that skips the exact number of bytes provided, unless it
	 * reaches EOF.
	 */
	static class FlushedInputStream extends FilterInputStream {
		public FlushedInputStream(InputStream inputStream) {
			super(inputStream);
		}

		@Override
		public long skip(long n) throws IOException {
			long totalBytesSkipped = 0L;
			while (totalBytesSkipped < n) {
				long bytesSkipped = in.skip(n - totalBytesSkipped);
				if (bytesSkipped == 0L) {
					int b = read();
					if (b < 0) {
						break; // we reached EOF
					} else {
						bytesSkipped = 1; // we read one byte
					}
				}
				totalBytesSkipped += bytesSkipped;
			}
			return totalBytesSkipped;
		}
	}

}
