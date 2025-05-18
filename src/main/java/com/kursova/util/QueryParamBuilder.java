package com.kursova.util;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

public class QueryParamBuilder {
    private final StringBuilder sb = new StringBuilder();

    public QueryParamBuilder add(String key, Object value) {
        if (value instanceof String s && s.isBlank()) return this;
        if (value != null) {
            if (sb.length() > 0) sb.append("&");
            sb.append(key).append("=").append(URLEncoder.encode(value.toString(), StandardCharsets.UTF_8));
        }
        return this;
    }


    public String build() {
        return sb.toString();
    }
}
