package org.example.patientservice.grpc;


import billing.BillingRequest;
import billing.BillingResponse;
import billing.BillingServiceGrpc;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class BillingServiceGrpcClient {
    private final BillingServiceGrpc.BillingServiceBlockingStub blockingStub;
    private static final Logger log=LoggerFactory.getLogger(BillingServiceGrpcClient.class);

    //localhost:9001/BillingService/CreatePatientAccount
    //aws.grpc:123123/BillingService/CreatePatientAccount
    public BillingServiceGrpcClient(@Value("${billing.service.address:localhost}") String serviceAddress,
                                    @Value("${billing.service.grpc.port}")int serverPort){
        log.info("Connecting to the billing Service GRPC service at: {}:{}",serviceAddress,serverPort);
        ManagedChannel channel = ManagedChannelBuilder.forAddress(serviceAddress,serverPort).usePlaintext().build();
        blockingStub=BillingServiceGrpc.newBlockingStub(channel);
    }

    public BillingResponse createBillingAccount(String patientId,String name,String email){
        BillingRequest request=BillingRequest.newBuilder().setPatientId(patientId).setName(name).setEmail(email).build();

        BillingResponse response=blockingStub.createBillingAccount(request);

        log.info("Received response from billing service via GRPC: {}",response);
        return response;
    }
}
