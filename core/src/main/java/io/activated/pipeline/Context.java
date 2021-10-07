package io.activated.pipeline;

import com.scurrilous.circe.Hash;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class Context {

    private Map<String, List<String>> headers = new HashMap<>();

    public Map<String, List<String>> getHeaders() {
        return headers;
    }

    public void setHeaders(Map<String, List<String>> headers) {
        this.headers = headers;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Context)) return false;
        Context context = (Context) o;
        return Objects.equals(headers, context.headers);
    }

    @Override
    public int hashCode() {
        return Objects.hash(headers);
    }

    @Override
    public String toString() {
        return "Context{" +
                "headers=" + headers +
                '}';
    }
}
