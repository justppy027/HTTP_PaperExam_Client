import java.net.HttpURLConnection;

public class HttpConnectionAndCode {

    public HttpURLConnection c = null;
    public Object obj = null;

    public int resp_code = 0;

    public String comment = "";

    public String cookie = "";

    public int code;

    public HttpConnectionAndCode(int code_){
        code = code_;
    }

    public HttpConnectionAndCode setComment(String comment) {
        this.comment = Methods.getEmptyStringFromNull(comment);
        return this;
    }

    public HttpConnectionAndCode setCookie(String cookie) {
        this.cookie = Methods.getEmptyStringFromNull(cookie);
        return this;
    }

    public HttpConnectionAndCode setC(HttpURLConnection c) {
        this.c = c;
        return this;
    }

    public HttpConnectionAndCode setObj(Object obj) {
        this.obj = obj;
        return this;
    }

    public HttpConnectionAndCode setResp_code(int resp_code) {
        this.resp_code = resp_code;
        return this;
    }

    public HttpConnectionAndCode setCode(int code) {
        this.code = code;
        return this;
    }

    public HttpConnectionAndCode setAll(HttpURLConnection c, int resp_code, String comment, String cookie, Object obj){
        setC(c);
        setResp_code(resp_code);
        setComment(comment);
        setCookie(cookie);
        setObj(obj);
        return this;
    }
}
