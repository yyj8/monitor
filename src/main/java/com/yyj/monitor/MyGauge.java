package com.yyj.monitor;

import io.prometheus.client.Gauge;

public class MyGauge {
    static final Gauge requestCount = Gauge.build()
            .name("request_count")
            .help("requests count.")
            .register();

    void processRequest() {
        requestCount.inc();
    }
}
