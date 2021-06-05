import java.io.IOException;
import java.io.InputStreamReader;
import java.net.CookieManager;
import java.net.HttpCookie;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.zip.GZIPInputStream;


public class Get {

    static int default_connect_timeout = 2_000; // 2s
    static int default_read_timeout = 20_000; // 20s
    static String UTF8 = "UTF-8";
    /**
     * @return - 0 GET success
     * - -1 cannot open url
     * - -2 cannot close input stream
     * - -5 cannot get response
     * - -6 response check fail
     * - -7 302
     * @non-ui
     * @clear
     */
    static HttpConnectionAndCode get(final String u,
                                     final String[] parms,
                                     final String user_agent,
                                     String referer,
                                     final String cookie,
                                     final String cookie_delimiter,
                                     final String success_resp_text,
                                     final String[] accept_encodings,
                                     final Boolean redirect,
                                     String read_charset_name,
                                     Boolean quick_test
    ) {
        URL url = null;
        HttpURLConnection cnt = null;
        InputStreamReader in = null;
        String response = null;
        if (read_charset_name == null) read_charset_name = UTF8;
        Charset read_charset = StandardCharsets.UTF_8;
        try {
            read_charset = Charset.forName(read_charset_name);
        }catch (Exception ignored){}
        int resp_code = 0;
        try {
            StringBuilder u_bulider = new StringBuilder();
            u_bulider.append(u);
            if (parms != null && parms.length > 0) {
                u_bulider.append("?").append(Methods.textutil_join("&", parms));
            }
            String url_str = u_bulider.toString();
            url = new URL(url_str);
            cnt = (HttpURLConnection) url.openConnection();

            cnt.setDoInput(true);
            cnt.setRequestProperty("User-Agent", user_agent);
            if (accept_encodings != null && accept_encodings.length > 0) {
                List<String> encodings = Arrays.asList(accept_encodings);
                if (!encodings.contains("gzip")) {
                    encodings.add("gzip");
                }
                cnt.setRequestProperty("Accept-Encoding", Methods.textutil_join(", ", (String[])encodings.toArray()));
            } else {
                cnt.setRequestProperty("Accept-Encoding", "gzip");
            }
            cnt.setRequestProperty("Referer", referer);
            if (cookie != null) {
                cnt.setRequestProperty("Cookie", cookie);
            }
            cnt.setRequestMethod("GET");
            if (redirect == null) {
                cnt.setInstanceFollowRedirects(true);
            } else {
                cnt.setInstanceFollowRedirects(redirect);
            }
            if (quick_test == null) quick_test = false;
            cnt.setConnectTimeout(quick_test? 500 :default_connect_timeout);
            cnt.setReadTimeout(quick_test? 500 :default_read_timeout);
            cnt.connect();

        } catch (Exception e) {
            e.printStackTrace();
            return new HttpConnectionAndCode(-1);
        }
        try {
            resp_code = cnt.getResponseCode();
            if (redirect != null && !redirect && resp_code == 302) {
                return new HttpConnectionAndCode(-7).setC(cnt).setResp_code(resp_code);
            }
            List<String> encodings = cnt.getHeaderFields().get("content-encoding");
            if (encodings != null && encodings.contains("gzip")) {
                in = new InputStreamReader(new GZIPInputStream(cnt.getInputStream()), read_charset);
            } else {
                in = new InputStreamReader(cnt.getInputStream(), read_charset);
            }
            int resp_length = cnt.getHeaderFieldInt("Content-Length", -1);
            StringBuilder response_builder = new StringBuilder();
            char read_char;
            while ( (resp_length < 0 || resp_length-- > 0) && (read_char = (char) in.read()) != (char) -1) {
                response_builder.append(read_char);
            }
            response = response_builder.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return new HttpConnectionAndCode(-5).setC(cnt);
        }
        try {
            in.close();
        } catch (IOException e) {
            e.printStackTrace();
            return new HttpConnectionAndCode(-2).setC(cnt).setResp_code(resp_code).setComment(response);
        }

        //get cookie from server
        String set_cookie = null;
        if (cookie_delimiter != null) {
            CookieManager cookieman = new CookieManager();
            StringBuilder cookie_builder = new StringBuilder();
            //getHeaderFields() returns the header fields of response
            List<String> cookies = cnt.getHeaderFields().get("Set-Cookie");
            if (cookies != null) {
                for (String cookie_resp : cookies) {
                    cookieman.getCookieStore().add(null, HttpCookie.parse(cookie_resp).get(0));
                }
            }
            if (cookieman.getCookieStore().getCookies().size() > 0) {
                List<HttpCookie> cookieList = cookieman.getCookieStore().getCookies();
                List<String> cookieStringList = new LinkedList<>();
                for (HttpCookie httpCookie : cookieList) {
                    String str = httpCookie.getName() + "=" + httpCookie.getValue();
                    cookieStringList.add(str);
                }
                String cookie_join = Methods.textutil_join(cookie_delimiter, (String[])cookieStringList.toArray());
                cookie_builder.append(cookie_join);
            }
            set_cookie = cookie_builder.toString();
        }

        //do not disconnect, keep alive
        if (success_resp_text != null) {
            if (!response.contains(success_resp_text)) {
                //if cookie_delimiter != null but no server cookie, set_cookie = ""
                //if no response, response = ""
                return new HttpConnectionAndCode(-6).setAll(cnt, resp_code, response, Methods.getEmptyStringFromNull(set_cookie), null);
            }
        }

        //do not disconnect, keep alive
        //if cookie_delimiter != null but no server cookie, set_cookie = ""
        //if no response, response = ""
        return new HttpConnectionAndCode(0).setAll(cnt, resp_code, response, Methods.getEmptyStringFromNull(set_cookie), null);
    }
}
