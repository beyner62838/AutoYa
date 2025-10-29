package com.example.back_AutoYa.config;

import com.example.back_AutoYa.Trace.BookingPaymentTraceFilter;
import com.example.back_AutoYa.service.TraceLogService;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TraceabilityConfig {

    @Bean
    public FilterRegistrationBean<BookingPaymentTraceFilter> bookingPaymentTraceFilter(TraceLogService traceLogService) {
        FilterRegistrationBean<BookingPaymentTraceFilter> registrationBean = new FilterRegistrationBean<>();

        registrationBean.setFilter(new BookingPaymentTraceFilter(traceLogService));

        // 👇 Importante: intercepta las rutas con y sin context-path
        registrationBean.addUrlPatterns(
                "/api/reservations/*",
                "/api/payment/*",
                "/api/cars/*",
                "/autoya/api/reservations/*",
                "/autoya/api/payment/*",
                "/autoya/api/cars/*"
        );

        registrationBean.setOrder(1);
        return registrationBean;
    }
}
