package com.tbossgroup.utils;

import java.util.List;
import java.util.Map;

import cn.isif.alibs.utils.ALog;
import cn.isif.ifok.CallBack;
import cn.isif.ifok.IfOk;
import cn.isif.ifok.OkConfig;
import cn.isif.ifok.Params;
import cn.isif.ifok.Part;
import okhttp3.Call;
import okhttp3.Headers;

public class IfOkNet {
    private static IfOkNet ifOkNet;

    private IfOkNet() {
        OkConfig.Builder builder = new OkConfig.Builder().setTimeout(1000 * 60);
        IfOk.getInstance().init(builder.build());
    }

    public static IfOkNet getInstance() {
        if (ifOkNet == null) {
            synchronized (IfOkNet.class) {
                if (ifOkNet == null) {
                    ifOkNet = new IfOkNet();
                }
            }
        }
        return ifOkNet;
    }

    /**
     * post请求
     *  @param url 请求的url
     * @param params 携带的参数
     * @param callBack 回调
     */
    public Call post(String url, Params params, ServiceCallBack<WrapUptoken> callBack) {
        ALog.d(url);
        if (params == null) {
            Params.Builder builder = new Params.Builder();
            builder.json();
            params = builder.build();
        }

        Header headers = HeaderUtil.getHeader();
        if (headers != null) {
            for (Map.Entry<String, String> entry : headers.entrySet()) {
                params.addHeader(entry.getKey(), entry.getValue());
            }
        }
        try {
            //打印请求参数
            List<Part> ps = params.getParams();
            StringBuffer stringBuffer = new StringBuffer();
            for (Part part : ps) {
                stringBuffer.append("\"" + part._key + "\":" + "\"" + part._value + "\",");
            }
            ALog.d(stringBuffer.toString());
            ALog.d("------------------------------------------------------------------");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return IfOk.getInstance().post(url, params, callBack);
    }

    /**
     * 不带header请求
     * @param url
     * @param params
     * @param callBack
     * @return
     */
    public Call postNoHeader(String url, Params params, CallBack callBack) {
        ALog.d(url);
        if (params == null) {
            Params.Builder builder = new Params.Builder();
            builder.json();
            params = builder.build();
        }
        try {
            //打印请求参数
            List<Part> ps = params.getParams();
            StringBuffer stringBuffer = new StringBuffer();
            for (Part part : ps) {
                stringBuffer.append("\"" + part._key + "\":" + "\"" + part._value + "\",");
            }
            ALog.d(stringBuffer.toString());
            ALog.d("------------------------------------------------------------------");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return IfOk.getInstance().post(url, params, callBack);
    }

    /**
     * 下载处理
     *
     * @param url 下载地址
     * @param destFileDir 文件存储目录
     * @param callback 回调
     */
    public void download(final String url, final String destFileDir, final CallBack callback) {
        IfOk.getInstance().download(url, destFileDir, callback);
    }

    public IfOkNet setHeader(Header header) {
        Headers.Builder headers = new Headers.Builder();
        if (header != null) {
            for (Map.Entry<String, String> entry : header.entrySet()) {
                headers.add(entry.getKey(), entry.getValue());
            }
        }
        //        int cacheSize = 10 * 1024 * 1024; // 10 MiB
        //        Cache cache = new Cache(cacheDirectory, cacheSize);
        OkConfig.Builder builder = new OkConfig.Builder().setTimeout(1000 * 60)
                //                .setCache(cache)
                .setCommenHeaders(headers.build());
        IfOk.getInstance().init(builder.build());
        return ifOkNet;
    }
}
