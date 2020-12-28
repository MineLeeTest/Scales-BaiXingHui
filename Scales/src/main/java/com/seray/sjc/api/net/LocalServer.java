package com.seray.sjc.api.net;

import android.text.TextUtils;

import com.seray.util.FileHelp;

import java.util.Map;

import fi.iki.elonen.NanoHTTPD;


public class LocalServer extends NanoHTTPD {

    private Map<String, String> fields;

    public LocalServer() {
        super(8081);
    }

    @Override
    @Deprecated
    public Response serve(IHTTPSession session) {
        try {
            if (Method.POST.equals(session.getMethod())) {
                String uri = session.getUri();
                fields = session.getParms();
                session.parseBody(fields);
                String result = "";
                switch (uri) {
                    case "/SendQRCode":
                        receiveSendQRCode();
                        break;
                    default:
                        return new Response("0,没有匹配的方法;");
                }
                return new Response("1,OK;" + result);
            } else {
                return new Response("0,no_post;");
            }
        } catch (Exception e) {
            return new Response("0," + "客户端错误：" + e.getMessage() + ";");
        }
    }

    /**
     * 接收支付二维码图片
     */
    private void receiveSendQRCode() {
        String value = fields.get("value");
        if (!TextUtils.isEmpty(value)) {
            FileHelp.generateImage(value);
        }
    }
}
