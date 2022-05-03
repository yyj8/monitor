package com.yyj.monitor;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;

import java.net.InetSocketAddress;

public class PrometheusMetricsProvider implements StatsProvider {
    public void start() {
        InetSocketAddress httpEndpoint = InetSocketAddress.createUnresolved("localhost", 7000);
        Server server = new Server(httpEndpoint);
        ServletContextHandler context = new ServletContextHandler();
        context.setContextPath("/");
        server.setHandler(context);

        context.addServlet(new ServletHolder(new PrometheusServlet(this)), "/metrics");

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
