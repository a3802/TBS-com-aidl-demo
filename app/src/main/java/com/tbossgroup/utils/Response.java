package com.tbossgroup.utils;

/**
 * Created by zh on 2017/4/21.
 */

public class Response<T> {
    private T body;

    public Response(T body) {
        this.body = body;
    }

    public T body() {
        return body;
    }
}

