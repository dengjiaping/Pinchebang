package com.lepin.loadimage;

import java.io.File;
import java.security.NoSuchAlgorithmException;

import android.content.Context;

import com.lepin.util.Util;

public class FileCache {

	private File cacheDir;

	public FileCache(Context context) {
		// Find the dir to save cached images
		cacheDir = Util.getInstance().getCacheFile(context);
	}

	public File getFile(String url) {
		String filename = "";
		try {
			filename = String.valueOf(Util.getInstance().getMD5String(url));
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		File f = new File(cacheDir, filename);
		return f;
	}

	public void clear() {
		File[] files = cacheDir.listFiles();
		if (files == null) return;
		for (File f : files)
			f.delete();
	}

}