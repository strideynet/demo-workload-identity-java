package org.example;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.awssdk.http.apache.ApacheHttpClient;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.sts.StsClient;
import software.amazon.awssdk.services.sts.model.GetCallerIdentityRequest;
import software.amazon.awssdk.services.sts.model.GetCallerIdentityResponse;
import software.amazon.awssdk.services.s3.model.ListObjectsV2Request;
import software.amazon.awssdk.services.s3.model.ListObjectsV2Response;
import software.amazon.awssdk.services.s3.model.S3Object;

public class App {
    private static final Logger logger = LoggerFactory.getLogger(App.class);

    public static void main(String... args) {
        logger.info("Application starts");

        // Setup clients. Note, no explicit authn config, they're relying on the
        // default provider chain.
        S3Client s3Client = S3Client.builder().httpClientBuilder(ApacheHttpClient.builder()).build();
        StsClient stsClient = StsClient.builder().httpClientBuilder(ApacheHttpClient.builder()).build();

        // Let's do a GetCallerIdentity first to show which role is being used!
        GetCallerIdentityResponse response = stsClient.getCallerIdentity(
            GetCallerIdentityRequest.builder().build()
        );
        logger.info(
            "GetCallerIdentity response. My account is {}. My ARN is {}. My unique identifier is {}",
             response.account(), 
             response.arn(), 
             response.userId()
        );

        // Now let's list the items in a bucket!
        String bucketName = "noah-workload-id-demo";
        ListObjectsV2Response bucketItems = s3Client.listObjectsV2(
            ListObjectsV2Request.builder().bucket(bucketName).build()
        );

        logger.info("Fetched {} items from bucket {}", bucketItems.keyCount(), bucketName);
        for (S3Object s3Object : bucketItems.contents()) {
            logger.info("Item: {}", s3Object.key());
        }

        logger.info("Application ends");
    }
}
