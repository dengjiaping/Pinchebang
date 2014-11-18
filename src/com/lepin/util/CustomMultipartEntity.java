/**
 * Project Name:PinCheBang
 * File Name:UploadUtil.java
 * Package Name:com.lepin.util
 * Date:2014年9月11日上午10:15:38
 * Copyright (c) 2014, chenzhou1025@126.com All Rights Reserved.
 *
*/
/**
 * Date:2014年9月11日上午10:15:38
 * Copyright (c) 2014, wxh All Rights Reserved.
 */

package com.lepin.util;
/**
 * ClassName:UploadUtil <br/>
 * Function: TODO ADD FUNCTION. <br/>
 * Reason:	 TODO ADD REASON. <br/>
 * Date:     2014年9月11日 上午10:15:38 <br/>
 * @author   {author wangxiaohong}
 * @version  
 * @since    JDK 1.6
 * @see 	 
 */
/**
 * @description TODO
 */
import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.Charset;

import org.apache.http.HttpResponse;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;

import android.app.ProgressDialog;
import android.os.AsyncTask;
 
  
 
public class CustomMultipartEntity extends MultipartEntity {
 
    private final ProgressListener listener;
 
    public CustomMultipartEntity(final ProgressListener listener) {
 
        super();
 
        this.listener = listener;
 
    }
 
    public CustomMultipartEntity(final HttpMultipartMode mode, final ProgressListener listener)     {
 
        super(mode);
 
        this.listener = listener;
 
    }
 
    public CustomMultipartEntity(HttpMultipartMode mode, final String boundary,
 
            final Charset charset, final ProgressListener listener) {
 
        super(mode, boundary, charset);
 
        this.listener = listener;
 
    }
 
    @Override
 
    public void writeTo(final OutputStream outstream) throws IOException {
 
        super.writeTo(new CountingOutputStream(outstream, this.listener));
 
    }
 
    public static interface ProgressListener {
 
        void transferred(long num);
 
    }
 
  
 
    public static class CountingOutputStream extends FilterOutputStream {
 
        private final ProgressListener listener;
 
        private long transferred;
 
        public CountingOutputStream(final OutputStream out, final ProgressListener listener) {
 
            super(out);
 
            this.listener = listener;
 
            this.transferred = 0;
 
        }
 
        public void write(byte[] b, int off, int len) throws IOException {
 
//            out.write(b, off, len);
// 
//            this.transferred += len;
// 
//            this.listener.transferred(this.transferred);
            
            int BUFFER_SIZE = 10000;
            int chunkSize;
            int currentOffset = 0;
            while (len>currentOffset) {
            chunkSize = len - currentOffset;
            if (chunkSize > BUFFER_SIZE) {
            chunkSize = BUFFER_SIZE;
            }
            out.write(b, currentOffset, chunkSize);
            currentOffset += chunkSize;
            this.transferred += chunkSize;
            //Log.i("CustomOutputStream WRITE","" + off + "|" + len + "|" + len + "|" + currentOffset + "|" + chunkSize + "|" + this.transferred);
            this.listener.transferred(this.transferred);
            }
        }
 
        public void write(int b) throws IOException {
 
            out.write(b);
 
            this.transferred++;
 
            this.listener.transferred(this.transferred);
 
        }
 
    }
 
}
 

