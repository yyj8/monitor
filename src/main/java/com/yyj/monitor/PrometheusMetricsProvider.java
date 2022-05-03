package com.yyj.monitor;

import io.prometheus.client.Gauge;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;

import java.net.InetSocketAddress;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class PrometheusMetricsProvider implements StatsProvider {
    final ConcurrentMap<String, Gauge> gauges = new ConcurrentHashMap<String, Gauge>();

    public void start() {
        gauges.putIfAbsent("request_count", MyGauge.requestCount);
        InetSocketAddress httpEndpoint = InetSocketAddress.createUnresolved("localhost", 7000);
        Server server = new Server(httpEndpoint);
        ServletContextHandler context = new ServletContextHandler();
        context.setContextPath("/");
        server.setHandler(context);

        context.addServlet(new ServletHolder(new PrometheusServlet(this)), "/metrics");

        new Thread(new Runnable() {

            public void run() {
                try {
                    while (true) {
                        Thread.sleep(10000L);
                        Gauge requestCount = gauges.get("request_count");
                        requestCount.inc();
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
        try {
            server.start();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    public static void main(String[] args) {
        new PrometheusMetricsProvider().start();
    }
}
