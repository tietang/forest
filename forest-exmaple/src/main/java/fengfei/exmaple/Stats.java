package fengfei.exmaple;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

import com.google.gson.GsonBuilder;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

public class Stats {

    protected static Map<String, AtomicLong> counterMap = new ConcurrentHashMap<String, AtomicLong>();
    private static Object lock = new Object();

    public static Map<String, AtomicLong> getCounterMap() {
        return counterMap;
    }

    protected static void render(
        String body,
        HttpExchange exchange,
        Integer code,
        String contentType) throws IOException {
        // InputStream input = exchange.getRequestBody();
        OutputStream output = exchange.getResponseBody();
        exchange.getResponseHeaders().set("Content-Type", contentType);
        exchange.getResponseHeaders().set("X-Ostrich-Version", ".0.1snapshot");
        byte[] data = body.getBytes();
        exchange.sendResponseHeaders(code, data.length);
        output.write(data);
        output.flush();
        output.close();
        exchange.close();
    }

    public static void startHttpServer(int port) {
        try {
            Stats.incr("Total_Requests");
            HttpServer httpServer = HttpServer.create(new InetSocketAddress(port), 20);
            httpServer.start();
            httpServer.createContext("/stats", new HttpHandler() {

                @Override
                public void handle(HttpExchange exchange) throws IOException {
                    render(Stats.toJson(), exchange, 200, "application/json");
                }

            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String toJson() {
        Map<String, Long> counter = new HashMap<String, Long>();
        Set<Entry<String, AtomicLong>> sets = getCounterMap().entrySet();
        for (Entry<String, AtomicLong> entry : sets) {
            counter.put(entry.getKey(), entry.getValue().get());
        }

        Map<String, Map> ss = new HashMap<String, Map>();
        ss.put("counters", counter);
        return new GsonBuilder().create().toJson(ss);
    }

    public static Long incr(String name) {
        AtomicLong c = counterMap.get(name);
        if (c == null) {
            synchronized (lock) {
                c = counterMap.get(name);
                if (c == null) {
                    c = new AtomicLong();
                    counterMap.put(name, c);
                }
            }
        }
        return c.incrementAndGet();
    }

    public static Long incr(String name, Integer value) {
        AtomicLong c = counterMap.get(name);
        if (c == null) {
            synchronized (lock) {
                c = counterMap.get(name);
                if (c == null) {
                    c = new AtomicLong();
                    counterMap.put(name, c);
                }
            }
        }
        return c.incrementAndGet();
    }

}
