package org.ka1amoor.api;

import com.sun.net.httpserver.HttpServer;
import org.ka1amoor.App;

import java.io.OutputStream;
import java.net.InetSocketAddress;

public class LoadEngineServer {

    public static void main(String[] args) throws Exception {

        HttpServer server = HttpServer.create(
                new InetSocketAddress(8085), 0);

        server.createContext("/load-tests/run", exchange -> {

            if (!"POST".equals(exchange.getRequestMethod())) {
                exchange.sendResponseHeaders(405, -1);
                return;
            }

            try {
                System.out.println("API: start load test");

                new Thread(() -> {
                    try {
                        App.main(new String[0]);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }).start();

                String resp = "LOAD_TEST_STARTED";
                exchange.sendResponseHeaders(200, resp.length());
                try (OutputStream os = exchange.getResponseBody()) {
                    os.write(resp.getBytes());
                }

            } catch (Exception e) {
                String resp = "ERROR: " + e.getMessage();
                exchange.sendResponseHeaders(500, resp.length());
                try (OutputStream os = exchange.getResponseBody()) {
                    os.write(resp.getBytes());
                }
            }
        });

        server.start();
        System.out.println("Load engine API started on :8085");
    }
}
