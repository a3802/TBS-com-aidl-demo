package com.tbossgroup.utils;

/**
 * Created by ISIF on 2018/3/28.
 */

import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import com.qiniu.android.http.ResponseInfo;
import com.qiniu.android.storage.Configuration;
import com.qiniu.android.storage.UpCancellationSignal;
import com.qiniu.android.storage.UpCompletionHandler;
import com.qiniu.android.storage.UpProgressHandler;
import com.qiniu.android.storage.UploadManager;
import com.qiniu.android.storage.UploadOptions;
import org.json.JSONObject;
import java.io.File;

/**
 * QiNiuHandler.Builder builder = new QiNiuHandler.Builder();
 * builder.setUpFile(new File("file path")).setUpToken("uptToken").setUpListener(new QiNiuUpListener() {
 *
 * @Override public void onUpStart() {
 *
 * }
 * @Override public void progress(int result) {
 *
 * }
 * @Override public void onUpComplete(String key, ResponseInfo respInfo, JSONObject jsonData) {
 *
 * }
 * });
 * QiNiuHandler qiNiuHandler = builder.build();//构建骑牛对象
 * qiNiuHandler.upload();//开始上传
 * qiNiuHandler.cancel();//如果有需要 调用次函数取消上传
 *
 * Created by le on 2018/3/28.
 */

public class QiNiuHandler {
    static UploadManager uploadManager;
    static Object lock = new Object();
    Handler mHandler = new Handler(Looper.getMainLooper());
    QiNiuUpListener qiNiuUpListener;
    String uploadToken;
    File upFile;
    boolean isCancel = false;

    private QiNiuHandler(String uploadToken, File upFile, QiNiuUpListener qiNiuUpListener) {
        init();
        this.uploadToken = uploadToken;
        this.upFile = upFile;
        this.qiNiuUpListener = qiNiuUpListener;
    }

    void init() {
        // 重用uploadManager。一般地，只需要创建一个uploadManager对象
        if (uploadManager == null) {
            synchronized (lock) {
                if (uploadManager == null) {
                    Configuration config = new Configuration.Builder()
                            .chunkSize(512 * 1024)        // 分片上传时，每片的大小。 默认256K
                            .putThreshhold(1024 * 1024)   // 启用分片上传阀值。默认512K
                            .connectTimeout(10)           // 链接超时。默认10秒
                            .responseTimeout(60)          // 服务器响应超时。默认60秒
                            .build();
                    uploadManager = new UploadManager(config);
                }
            }
        }
    }

    public void upload() {
        //检查上传准备：必须设置上传token、必须设置file、必须设置回调
        checkUploadReady();
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                qiNiuUpListener.onUpStart();
            }
        });

        UploadOptions uploadOptions = new UploadOptions(null, null, false,
                new UpProgressHandler() {
                    @Override
                    public void progress(String key, final double percent) {
                        mHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                qiNiuUpListener.progress((int) percent);
                            }
                        });
                    }
                }, new UpCancellationSignal() {
            @Override
            public boolean isCancelled() {
                return isCancel;
            }
        });

        this.uploadManager.put(upFile, null, uploadToken, new UpCompletionHandler() {
            @Override
            public void complete(final String key, final ResponseInfo info, final JSONObject response) {
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        qiNiuUpListener.onUpComplete(key, info, response);
                    }
                });
            }
        }, uploadOptions);
    }

    void checkUploadReady() {
        if (TextUtils.isEmpty(uploadToken)) {
            throw new IllegalArgumentException("缺少上传Token");
        }
        if (null == upFile || !upFile.exists() || !upFile.canRead()) {
            throw new IllegalArgumentException("文件无效");
        }
        if (null == qiNiuUpListener) {
            throw new IllegalArgumentException("QiNiuUpListener 不能为空");
        }
    }

    public void cancel() {
        this.isCancel = true;
    }

    public interface QiNiuUpListener {
        void onUpStart();

        void progress(int result);

        void onUpComplete(String key, ResponseInfo respInfo, JSONObject jsonData);
    }

    public static final class Builder {
        QiNiuUpListener qiNiuUpListener;
        String uploadToken;
        File upFile;

        public Builder() {

        }

        public Builder setUpToken(String token) {
            this.uploadToken = token;
            return this;
        }

        public Builder setUpFile(File file) {
            this.upFile = file;
            return this;
        }

        public Builder setUpListener(QiNiuUpListener listener) {
            this.qiNiuUpListener = listener;
            return this;
        }

        public QiNiuHandler build() {
            return new QiNiuHandler(uploadToken, upFile, qiNiuUpListener);
        }
    }
}
