package com.coding404.demo.aws.service;

import com.coding404.demo.aws.config.AwsConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.AwsCredentials;
import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.lexmodelbuilding.model.PutBotAliasResponse;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class S3Service {

//    // 어세스키
//    @Value("${aws_access_key_id}")
//    private String aws_access_key_id;
//    // 시크릿키
//    @Value("${aws_secret_access_key}")
//    private String aws_secret_access_key;

    @Autowired
    private S3Client s3;

    @Value("${aws_bucket_name}")
    private String aws_bucket_name;

    public void getBucketList() {

        // 1st - 외부파일로 사용하는 방법
        // 자격증명객체
//        ProfileCredentialsProvider credentialsProvider = ProfileCredentialsProvider.create();
//        // s3클라이언트
//        S3Client s3 = S3Client.builder()
//                .region(region)
//                .credentialsProvider(credentialsProvider)
//                .build();

        // 2nd - 어플리케이션에 직접 작성하는 방법
//        AwsBasicCredentials credentials = AwsBasicCredentials.create(aws_access_key_id, aws_secret_access_key);
//        StaticCredentialsProvider credentialsProvider = StaticCredentialsProvider.create(credentials);
//
//        // s3클라이언트
//        S3Client s3 = S3Client.builder()
//                .region(region)
//                .credentialsProvider(credentialsProvider)
//                .build();

        /////////////////////////////////////////////////////////////
        // 자격증명객체를 빈으로 관리


        // s3기능 사용
        // List buckets
        ListBucketsRequest listBucketsRequest = ListBucketsRequest.builder().build();
        ListBucketsResponse listBucketsResponse = s3.listBuckets(listBucketsRequest);
        listBucketsResponse.buckets().stream().forEach(x -> System.out.println(x.name()));


    }

    // S3파일 업로드
    public void putS3Object(String originName, byte[] originData) {
        try {
            Map<String, String> metadata = new HashMap<>();
            metadata.put("x-amz-meta-myVal", "test");
            PutObjectRequest putOb = PutObjectRequest.builder()
                    .bucket(aws_bucket_name) // 버킷명
                    .key(originName) // 파일명
                    .metadata(metadata)
                    .build();

            // s3.putObject(putOb, RequestBody.fromFile(new File(objectPath))); // 로컬파일 업로드 시
            PutObjectResponse response =  s3.putObject(putOb, RequestBody.fromBytes(originData));
            System.out.println("Successfully placed " + originName + " into bucket " + aws_bucket_name);
            System.out.println("성공 실패 여부 : " + response.sdkHttpResponse().statusCode()); // 성공, 실패 여부

            //////////////////////////////////////////////////////////
            // 데이터베이스에 어떤 데이터를 넣어 놓을 것인지

        } catch (S3Exception e) {
            System.err.println(e.getMessage());
            // System.exit(1);
        }
    }

    public void listBucketObjects() {

        try {
            ListObjectsRequest listObjects = ListObjectsRequest
                    .builder()
                    .bucket(aws_bucket_name)
                    .build();

            ListObjectsResponse res = s3.listObjects(listObjects);
            List<S3Object> objects = res.contents();
            for (S3Object myValue : objects) {
                System.out.print("\n The name of the key is " + myValue.key());
                System.out.print("\n The object is " + (myValue.size() / 1024)+ " KBs");
                System.out.print("\n The owner is " + myValue.owner());
            }

        } catch (S3Exception e) {
            System.err.println(e.awsErrorDetails().errorMessage());
            // System.exit(1);
        }
    }


    public void deleteBucketObjects(String originName, String delName) {

        // Upload three sample objects to the specfied Amazon S3 bucket.
        ArrayList<ObjectIdentifier> keys = new ArrayList<>();
        PutObjectRequest putOb;
        ObjectIdentifier objectId;

        for (int i = 0; i < 3; i++) {
            String keyName = "delete object example " + i;
            objectId = ObjectIdentifier.builder()
                    .key(originName)
                    .build();

            putOb = PutObjectRequest.builder()
                    .bucket(aws_bucket_name)
                    .key(originName)
                    .build();

            s3.putObject(putOb, RequestBody.fromString(originName));
            keys.add(objectId);
        }

        System.out.println(keys.size() + " objects successfully created.");

        // Delete multiple objects in one request.
        Delete del = Delete.builder()
                .objects(keys)
                .build();

        try {
            DeleteObjectsRequest multiObjectDeleteRequest = DeleteObjectsRequest.builder()
                    .bucket(aws_bucket_name)
                    .delete(del)
                    .build();

            s3.deleteObjects(multiObjectDeleteRequest);
            System.out.println("Multiple objects are deleted!");

        } catch (S3Exception e) {
            System.err.println(e.awsErrorDetails().errorMessage());
            // System.exit(1);
        }
    }
}
