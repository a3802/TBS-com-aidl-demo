package com.tbossgroup.utils;

import android.text.TextUtils;

import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.reflect.TypeToken;

import org.json.JSONObject;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import cn.isif.alibs.utils.ALog;
import cn.isif.ifok.CallBack;
import okhttp3.Request;

/**
 * Created by dell on 2016/12/22-11:38.
 */

public abstract class ServiceCallBack<T> extends CallBack {
    public static final int NET_ERROR_CODE = -1;//network error
    public static final int PARSE_ERROR_CODE = -2000;//json parse failed
    private static final Gson mGson = new Gson();
    private static final int SUCCESS = 1000;//success back
    private final String JSON_PARSE_ERROR = "Json 解析错误";
    private final String NET_ERROR = "网络异常";
    private Type payloadClass = null;

    public ServiceCallBack() {
        Type genType = getClass().getGenericSuperclass();
        if (genType instanceof ParameterizedType) {
            Type[] params = ((ParameterizedType) genType).getActualTypeArguments();
            payloadClass = params[0];
        }
    }

    @Override
    public void onStart(Request request) {

    }

    @Override
    public void onFail(Exception e) {
        e.printStackTrace();
        if (!TextUtils.isEmpty(e.getMessage()) && e.getMessage().equals("Canceled")) {

        } else {
            failed(NET_ERROR_CODE, NET_ERROR, e.getMessage());
        }
    }

    @Override
    public void onSuccess(Object o) {
        try {
            ALog.d("======response::" + o);
            if (o == null) {
                throw new Exception("response is null");
            }
            RespBean resp = mGson.fromJson(o.toString(), RespBean.class);
            int code = resp.getRspInfo().getRspCode();
            String desc = resp.getRspInfo().getRspDesc();
            ALog.d("======response::desc::" + desc);
            T payLoad = null;
            try {
                if (payloadClass != null) {
                    JSONObject jsonObject = new JSONObject(o.toString());
                    JSONObject jsonObject1 = jsonObject.getJSONObject("rspData");
                    String json = jsonObject1.toString();
                    TypeAdapter<?> adapter = mGson.getAdapter(TypeToken.get(payloadClass));
                    payLoad = (T) adapter.fromJson(json);
                }

                switch (code) {
                    case SUCCESS:
                        success(resp, new Response(payLoad == null ? o : payLoad));
                        break;
                    default:
                        failed(code, desc, o.toString());
                        break;
                }
            } catch (Exception e) {
                failed(code, desc, o.toString());
            }
        } catch (Exception e) {
            failed(PARSE_ERROR_CODE, JSON_PARSE_ERROR, e.getMessage());
            e.printStackTrace();
        }
    }

    public abstract void failed(int code, String errorInfo, String source);

    public abstract void success(RespBean resp, Response<T> payload);

    @Override
    public void updateProgress(int progress, long networkSpeed, boolean done) {

    }
}
