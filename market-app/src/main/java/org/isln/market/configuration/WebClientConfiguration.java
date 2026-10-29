package org.isln.market.configuration;

import org.isln.market.ApiClient;
import org.isln.market.client.PaymentApi;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfiguration {
    @Bean
    public PaymentApi getPaymentClient(@Value("${payment.api.path}") String paymentApiPath) {
        ApiClient apiClient = new ApiClient(WebClient.create(paymentApiPath));
        apiClient.setBasePath(paymentApiPath);
        return new PaymentApi(apiClient);
    }
}
