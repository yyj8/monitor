package com.yyj.monitor;

import io.prometheus.client.Gauge;
import io.prometheus.client.exporter.common.TextFormat;

import java.io.IOException;
import java.io.Writer;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet used to export metrics in prometheus text format.
 */
public class PrometheusServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    private final transient PrometheusMetricsProvider provider;

    public PrometheusServlet(PrometheusMetricsProvider provider) {
        this.provider = provider;
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setStatus(HttpServletResponse.SC_OK);
        resp.setContentType(TextFormat.CONTENT_TYPE_004);

        Writer writer = resp.getWriter();
        try {
            //写指标
// Example:
            // # TYPE bookie_storage_entries_count gauge
            // bookie_storage_entries_count 519
            try {
                Gauge requestCount = provider.gauges.get("request_count");
                writer.append("# TYPE ").append("request_count").append(" gauge\n");
                writer.append("request_count");
                writer.append("{");
                writer.append("\"broker\"=\"1001\"");
                writer.append("}");
                writer.append(' ').append(String.valueOf(requestCount.get())).append('\n');
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            writer.flush();
        } finally {
            writer.close();
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doGet(req, resp);
    }

}
