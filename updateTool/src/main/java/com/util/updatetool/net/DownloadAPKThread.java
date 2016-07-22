package com.util.updatetool.net;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.util.Log;

import com.util.updatetool.service.MKUpdateService;
import com.util.updatetool.utils.MKUpdateUtil;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class DownloadAPKThread extends Thread {
	private Context context;
	private int connectTimeout = 30 * 1000;
	private int readTimeout = 30 * 1000;
	private int currentDownload = 0;
	private String url;
	private String cachePath;
	
	private HttpURLConnection conn = null;

	public DownloadAPKThread(Context context, String url, String cachePath, HttpURLConnection conn) {
		super();
		this.context = context;
		this.url = url;
		this.cachePath = cachePath;
		this.conn = conn;
	}

	@Override
	public void run() {
		super.run();
		downLoadNewVerApk(url, cachePath);
	}

	private void downLoadNewVerApk(String urlPath, String cachePath) {
		FileOutputStream fos = null;
		InputStream is = null;
		try {
			if(conn == null){
				URL url = new URL(urlPath);
				conn = (HttpURLConnection) url.openConnection();
				conn.setConnectTimeout(connectTimeout);
				conn.setReadTimeout(readTimeout);
				conn.setDoInput(true);

				conn.connect();
			}
			is = conn.getInputStream();
			// 文件大小
			Integer fileSize = conn.getContentLength();
			if (fileSize.intValue() <= 0) {
				throw new RuntimeException("未能获知文件大小");
			}
			File file = new File(cachePath);
			Log.e("aaa", "cachePath" + cachePath);
			if (file.exists() && file.length() == fileSize) {
				((MKUpdateService)context).sendUpdateBroadcast();
				return;
			}else {
				String dir = cachePath.substring(0, cachePath.lastIndexOf(File.separator));
				File oldFile = new File(dir);
				if(oldFile.exists()){
					File[] oldFiles = oldFile.listFiles();
					for (int i = 0;i<oldFiles.length;i++){
						if(oldFiles[i].getAbsolutePath().endsWith(".apk")){
							oldFiles[i].delete();
						}
					}
				}else{
					oldFile.mkdirs();
				}
			}
			file.createNewFile();
			fos = new FileOutputStream(file);
			byte[] temp = new byte[65535];
			int i = 0;
			int oldPct = 0;
			while ((i = is.read(temp)) > 0) {
				fos.write(temp, 0, i);
				currentDownload += i;
				float percent = (Float.valueOf(currentDownload) / Float.valueOf(fileSize)) * 100.0f;
				if((int)percent > oldPct){
					oldPct = (int) percent;
					((MKUpdateService)context).refreshNotification(oldPct);
				}
			}
			((MKUpdateService)context).sendUpdateBroadcast();

		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (null != fos) {
					fos.close();
				}
				if (null != is) {
					is.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}

