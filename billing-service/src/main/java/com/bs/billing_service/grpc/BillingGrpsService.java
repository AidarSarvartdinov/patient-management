package com.bs.billing_service.grpc;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import billing.BillingResponse;
import billing.BillingServiceGrpc.BillingServiceImplBase;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;

@GrpcService
public class BillingGrpsService extends BillingServiceImplBase {
    private static final Logger log = LoggerFactory.getLogger(BillingGrpsService.class);

    @Override
    public void createBillingAccount(billing.BillingRequest billingRequest,
            StreamObserver<BillingResponse> responseObserver) {

        log.info("createBillingAccount request received: {}", billingRequest.toString());

        // business logic

        BillingResponse response = BillingResponse.newBuilder()
                .setAccountId("1234")
                .setStatus("ACTIVE")
                .build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }
}
