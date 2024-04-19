package com.github.example.okhttp;

import com.google.gson.Gson;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import okhttp3.*;
import okhttp3.internal.http.HttpMethod;
import okhttp3.logging.HttpLoggingInterceptor;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MarkerFactory;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.*;

@Data
public class OkHttp {
    /**
     * 读取超时时间，秒
     */
    private long readTimeout = 30;
    /**
     * 链接超时时间
     */
    private long connectTimeout = 30;
    /**
     * 是否支持cookie
     */
    private boolean cookie = false;
    /**
     * 是否重定向
     */
    private boolean followRedirects = false;

    /**
     * 编码
     */
    private Charset charset = StandardCharsets.UTF_8;


    private String baseURL;

    private OkHttpClient client;
    private static OkHttp instance;
    private final HashMap<String, List<Cookie>> cookieStore = new HashMap<>();


    /**
     * 实例化
     */
    private OkHttp() {
        ConnectionPool connectionPool = new ConnectionPool();
        HttpLoggingInterceptor httpLoggingInterceptor = new HttpLoggingInterceptor(new HttpLogging());
        OkHttpClient.Builder builder = new OkHttpClient.Builder()
                .connectionPool(connectionPool)
                .followRedirects(isFollowRedirects())
                .addNetworkInterceptor(httpLoggingInterceptor)
                .readTimeout(Duration.ofSeconds(getReadTimeout()))
                .connectTimeout(Duration.ofSeconds(getConnectTimeout()));
        if (isCookie()) {
            builder.cookieJar(new CookieJar() {
                @Override
                public void saveFromResponse(HttpUrl httpUrl, List<Cookie> list) {
                    cookieStore.put(httpUrl.host(), list);
                }

                @Override
                public List<Cookie> loadForRequest(HttpUrl httpUrl) {
                    List<Cookie> cookies = cookieStore.get(httpUrl.host());
                    return cookies != null ? cookies : new ArrayList<Cookie>();
                }
            });
        }
        client = builder.build();
    }

    /**
     * 使用单例模式
     *
     * @return OkHttp
     */
    public static OkHttp build() {
        if (instance == null) {
            synchronized (OkHttp.class) {
                instance = new OkHttp();
            }
        }
        return instance;
    }


    /**
     * Map<String,Object> queryMap = new LinkedHashMap<String,Object>();
     * queryMap.put("name","test");
     */
    public Response get(String pathOrUrl, Map<String, Object> query) throws IOException {
        return this.request(Method.GET, pathOrUrl, MediaTypeEnum.JSON, query, null, null);
    }


    /**
     * 发送json请求
     */
    public Response postJSON(String pathOrUrl, Object object) throws IOException {
        ArrayList<Header> headers = new ArrayList<>();
        headers.add(Header.JSON());
        return this.request(Method.POST, pathOrUrl, MediaTypeEnum.JSON, null, object, headers);
    }

    /**
     * 文件上传案例
     * File file = new File("test.yml");
     * body.put("file",file);
     * body.put("name","upload");
     */
    public Response postForm(String pathOrUrl, Map<String, Object> body) throws IOException {
        return this.request(Method.POST, pathOrUrl, MediaTypeEnum.FORM_DATA, null, body, null);
    }

    /**
     * 自定义发送请求
     */
    public Response request(Method method, String pathOrUrl, MediaTypeEnum mediaType, Map<String, Object> query, Object body, List<Header> headers) throws IOException {
        Call call = this.buildCall(method, pathOrUrl, mediaType, query, body, headers);
        return call.execute();
    }


    /**
     * 重新生成url
     */
    public String buildUrl(String path, Map<String, Object> queryMap) {
        if (StringUtils.isBlank(this.baseURL) && queryMap == null) {
            return path;
        }
        final StringBuilder url = new StringBuilder();
        //判断开始头是否包含http字符串,不区分大小
        if (StringUtils.startsWithIgnoreCase(path, "http")) {
            url.append(path);
        } else {
            url.append(this.baseURL).append(path);
        }
        if (queryMap != null && !queryMap.isEmpty()) {
            String prefix = url.toString().contains("?") ? "&" : "?";
            for (Map.Entry<String, Object> param : queryMap.entrySet()) {
                if (param.getValue() != null) {
                    if (prefix != null) {
                        url.append(prefix);
                        prefix = null;
                    } else {
                        url.append("&");
                    }
                    String value = Util.object2String(param.getValue());
                    url.append(this.URLEncoder(param.getKey())).append("=").append(this.URLEncoder(value));
                }
            }
        }
        return url.toString();
    }

    /**
     * url参数编码
     */
    protected String URLEncoder(String url) {
        try {
            return URLEncoder.encode(url, StandardCharsets.UTF_8.toString()).replace("\\+", "%20");
        } catch (UnsupportedEncodingException e) {
            return url;
        }
    }

    /**
     * 发送请求前准备，参数处理
     */
    public Call buildCall(
            Method method,
            String pathOrUrl,
            MediaTypeEnum mediaType,
            Map<String, Object> query,
            Object body,
            List<Header> headers) {
        if (headers == null) {
            headers = new ArrayList<Header>();
        }
        pathOrUrl = this.buildUrl(pathOrUrl, query);
        final Request.Builder reqBuilder = new Request.Builder().url(pathOrUrl);
        //设置header
        for (Header header : headers) {
            reqBuilder.header(header.getKey(), header.getValue());
        }
        // GET HEAD request
        if (!HttpMethod.permitsRequestBody(method.name())) {
            Request build = reqBuilder.method(method.name(), null).build();
            return this.client.newCall(build);
        }
        //other request
        RequestBody requestBody = this.buildRequestBody(mediaType.getMediaType(), body);
        Request build = reqBuilder.method(method.name(), requestBody).build();
        return this.client.newCall(build);
    }

    protected RequestBody buildRequestBody(MediaType mediaType, Object body) {
        if (body == null) {
            return null;
        }
        if (body instanceof Map<?, ?>) {
            // application/json;charset=utf-8
            if (MediaTypeEnum.JSON.getMediaType().equals(mediaType)) {
                String content = Util.object2String(body);
                return RequestBody.create(mediaType, content.getBytes(charset));
            } else if (MediaTypeEnum.FORM.getMediaType().equals(mediaType)) {
                // form application/x-www-form-urlencoded;charset=UTF-8
                FormBody.Builder builder = new FormBody.Builder();
                Map<?, ?> formData = (Map<?, ?>) body;
                for (Map.Entry<?, ?> param : formData.entrySet()) {
                    builder.add(param.getKey().toString(), Util.object2String(param.getValue()));
                }
                return builder.build();
            } else if (MediaTypeEnum.FORM_DATA.getMediaType().equals(mediaType)) {
                // form multipart/form-data
                Map<?, ?> formData = (Map<?, ?>) body;
                MultipartBody.Builder builder = new MultipartBody.Builder();
                for (Map.Entry<?, ?> param : formData.entrySet()) {
                    String key = param.getKey().toString();
                    if (param.getValue() instanceof File) {
                        File file = (File) param.getValue();
                        RequestBody requestBody = RequestBody.create(MediaTypeEnum.OCTET_STREAM.getMediaType(), file);
                        builder.addFormDataPart(key, file.getName(), requestBody);
                    } else {
                        builder.addFormDataPart(key, Util.object2String(param.getValue()));
                    }
                }
                return builder.setType(mediaType).build();
            } else {
                return RequestBody.create(mediaType, body.toString());
            }
        } else if (body instanceof byte[]) {
            return RequestBody.create(mediaType, (byte[]) body);
        } else if (body instanceof File) {
            return RequestBody.create(mediaType, (File) body);
        } else if (body instanceof String) {
            return RequestBody.create(mediaType, (String) body);
        } else {
            String content = Util.object2String(body);
            return RequestBody.create(mediaType, content.getBytes(charset));
        }
    }

    /**
     * 发送请求日志类
     */
    public static class HttpLogging implements HttpLoggingInterceptor.Logger {
        private final org.slf4j.Logger logger;

        public HttpLogging() {
            this.logger = LoggerFactory.getLogger(Logger.class);
        }

        @Override
        public void log(String message) {
            logger.debug(MarkerFactory.getMarker("OKHTTP"), message);
        }
    }


    public static enum MediaTypeEnum {
        JSON(MediaType.parse("application/json;charset=utf-8")),
        //multipart/form-data
        FORM(MediaType.parse("application/x-www-form-urlencoded;charset=UTF-8")),
        FORM_DATA(MultipartBody.FORM),
        OCTET_STREAM(MediaType.parse("application/octet-stream")),
        ;
        @Getter
        private final MediaType mediaType;

        MediaTypeEnum(MediaType mediaType) {
            this.mediaType = mediaType;
        }
    }

    /**
     * 发送请求携带header
     */
    public static class Header extends MapObject {

        public static final String userAgent = "User-Agent";
        public static final String contentType = "Content-Type";

        /**
         * 初始化单个header
         */
        public Header(String key, Object value) {
            super(key, value);
        }

        /**
         * json
         */
        public static Header JSON() {
            return new Header(userAgent, MediaTypeEnum.JSON.getMediaType());
        }

        /**
         * form
         */
        public static Header FORM() {
            return new Header(userAgent, MediaTypeEnum.FORM.getMediaType());
        }


        /**
         * userAgent
         */
        public static Header UserAgent(String userAgent) {
            return new Header(userAgent, userAgent);
        }

        /**
         * 在列表中是否包含指定key值
         */
        public static boolean listHasKey(List<Header> headers, String key) {
            return headers.stream().anyMatch(header -> key.equals(header.getKey()));
        }

        /**
         * 在列表中查找是否包含key 如果没有就返回空字符串
         */
        public static String listGetKey(List<Header> headers, String key) {
            Optional<String> first = headers.stream()
                    .filter(header -> key.equals(header.getKey()))
                    .map(header -> header.getValue().toString())
                    .findFirst();
            return first.orElse("");
        }
    }


    public static class MapObject {
        @Setter
        @Getter
        private String key;
        @Setter
        private Object value;

        public MapObject(String key, Object value) {
            setKey(key);
            setValue(value);
        }

        /**
         * 获取值 将object转string
         *
         * @return String
         */
        public String getValue() {
            return Util.object2String(value);
        }
    }

    /**
     * 请求方式定义常量
     */
    public enum Method {
        GET, POST, HEAD, OPTIONS, PUT, DELETE, TRACE, CONNECT, PATCH
    }


    public static class Util {

        /**
         * 对象转json字符串
         */
        public static String object2JSION(Object obj) {
            Gson gson = new Gson();
            return gson.toJson(obj);
        }

        /**
         * 对象转字符串
         */
        public static String object2String(Object obj) {
            if (obj == null) {
                return "";
            } else if (obj instanceof Date || obj instanceof OffsetDateTime || obj instanceof LocalDate) {
                return object2JSION(obj);
            } else if (obj instanceof Collection) {
                StringBuilder b = new StringBuilder();
                for (Object o : (Collection) obj) {
                    if (b.length() > 0) {
                        b.append(",");
                    }
                    b.append(o);
                }
                return b.toString();
            } else if (obj instanceof String) {
                return (String) obj;
            } else {
                return String.valueOf(obj);
            }
        }
    }
}
