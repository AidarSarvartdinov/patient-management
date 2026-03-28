package com.pm.scheduling_service.infrastructure.client;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import com.pm.scheduling_service.domain.exception.PaymentInitiationFailedException;
import com.pm.scheduling_service.domain.port.PaymentGateway;
import com.pm.scheduling_service.infrastructure.client.dto.CreatePaymentRequest;
import com.pm.scheduling_service.infrastructure.client.dto.CreatePaymentResponse;
import com.pm.scheduling_service.infrastructure.exception.PaymentServiceUnavailableException;
import com.pm.scheduling_service.infrastructure.security.JwtProvider;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class BillingServiceClient implements PaymentGateway {
    private final RestClient restClient;
    private final JwtProvider jwtProvider;

    public BillingServiceClient(RestClient.Builder restClientBuilder,
            @Value("${BILLING_SERVICE_URL}") String billingUrl, JwtProvider jwtProvider) {
        this.restClient = restClientBuilder.baseUrl(billingUrl).build();
        this.jwtProvider = jwtProvider;
    }

    @Override
    public String initiatePayment(UUID patientId, UUID slotId, long price, String currency) {
        log.info("Initiating payment with patientId: {}, slotId: {}", patientId, slotId);

        try {
            ResponseEntity<CreatePaymentResponse> responseEntity = restClient.post()
                    .uri("/payments").contentType(MediaType.APPLICATION_JSON)
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtProvider.getTokenString())
                    .body(new CreatePaymentRequest(patientId, slotId, price, currency)).retrieve()
                    .toEntity(CreatePaymentResponse.class);

            // TODO: retry if bad response
            CreatePaymentResponse response = responseEntity.getBody();
            return response.sessionUrl();
        } catch (RestClientException e) {
            if (e instanceof HttpClientErrorException clientError && clientError.getStatusCode().is4xxClientError()) {
                throw new PaymentInitiationFailedException(
                        "Payment initiation failed for patient: " + patientId + ", slot: " + slotId + ". "
                                + clientError.getResponseBodyAsString());
            }

            throw new PaymentServiceUnavailableException("Billing service is unavailable: " + e.getMessage());
        }
    }

}
