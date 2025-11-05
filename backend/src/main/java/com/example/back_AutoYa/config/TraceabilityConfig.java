package com.example.back_AutoYa.config;

import com.example.back_AutoYa.Trace.BookingPaymentTraceFilter;
import com.example.back_AutoYa.service.TraceLogService;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TraceabilityConfig {


    public FilterRegistrationBean<BookingPaymentTraceFilter> bookingPaymentTraceFilter(TraceLogService traceLogService) {
        FilterRegistrationBean<BookingPaymentTraceFilter> registrationBean = new FilterRegistrationBean<>();

        // ✅ Usa la variable (minúscula), no la clase
        registrationBean.setFilter(new BookingPaymentTraceFilter(traceLogService));

        registrationBean.addUrlPatterns(
                "/api/reservations/**",
                "/api/payment/**"
        );

        registrationBean.setOrder(10000);
        return registrationBean;
    }
}
