package cn.posinda.base;

import com.google.common.base.Preconditions;

import java.util.HashMap;

public class WebResult extends HashMap<String, Object> {

    private static final long serialVersionUID = -6229913830733192685L;

    private static final int OK = 200;

    static final int ERROR = 500;

    WebResult() {
        super();
    }

    public static WebResult error(String msg) {
        WebResult webResult = new WebResult();
        webResult.put("code", ERROR);
        webResult.put("msg", msg == null ? "出现异常,请稍后重试.." : msg);
        return webResult;
    }

    public static WebResult success() {
        WebResult webResult = new WebResult();
        webResult.put("code", OK);
        webResult.put("msg", "请求成功");
        return webResult;
    }
    
    @Override
    public WebResult put(String key, Object value) {
        Preconditions.checkNotNull(key);
        super.put(key, value);
        return this;
    }

}
