package com.pm.scheduling_service.infrastructure.client;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import com.pm.scheduling_service.domain.port.PaymentGateway;
import com.pm.scheduling_service.infrastructure.client.dto.CreatePaymentRequest;
import com.pm.scheduling_service.infrastructure.client.dto.CreatePaymentResponse;

@Component
public class BillingServiceClient implements PaymentGateway {
    private final RestClient restClient;

    public BillingServiceClient(RestClient.Builder restClientBuilder, @Value("${BILLING_SERVICE_URL}") String billingUrl) {
        this.restClient = restClientBuilder.baseUrl(billingUrl).build();
    }

    @Override
    public String initiatePayment(UUID patientId, UUID slotId, long price, String currency) {
        ResponseEntity<CreatePaymentResponse> responseEntity = restClient.post()
                .uri("/payments").contentType(MediaType.APPLICATION_JSON)
                .body(new CreatePaymentRequest(patientId, slotId, price, currency)).retrieve()
                .toEntity(CreatePaymentResponse.class);

        // TODO: retry if bad response
        CreatePaymentResponse response = responseEntity.getBody();
        return response.sessionUrl();
    }
    
}
