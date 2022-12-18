package com.fareyeconnect.service;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.SdkClientException;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.fareyeconnect.config.Property;
import com.fareyeconnect.constant.AppConstant;
import com.fareyeconnect.tool.Helper;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

@ApplicationScoped
public class AwsHelper {

    @Inject
    private static Property property;

    @Helper(description = "check if file exists in s3")
    public static boolean exists(String path) {
        return exists(path, property.getS3BucketName(), property.getS3AccessKey(), property.getS3SecretKey(), AppConstant.DEFAULT_REGION);
    }

    @Helper(description = "check if file exists in s3")
    public static boolean exists(String path, String bucketName, String accessKey, String secretKey, String region) {
        BasicAWSCredentials basicAWSCredentials = new BasicAWSCredentials(accessKey, secretKey);
        AmazonS3 s3client = AmazonS3ClientBuilder.standard().withRegion(Regions.fromName(region)).withCredentials(new AWSStaticCredentialsProvider(basicAWSCredentials)).build();
        try {
            return s3client.doesObjectExist(bucketName, path);
        } catch(AmazonServiceException e) {
            throw new RuntimeException("Error occured in Amazon S3");
        } catch(SdkClientException ex) {
            throw new RuntimeException("Errors encountered in the client while making the request or handling the response");
        }
    }

    @Helper(description = "Upload to s3")
    public static String upload(InputStream inputStream, String path, String contentType, String s3BucketName, String s3AccessKey, String s3SecretKey, String cannedACL, String regionName) {
        AWSCredentials credentials = new BasicAWSCredentials(s3AccessKey, s3SecretKey);
        AmazonS3 client = AmazonS3ClientBuilder
                .standard()
                .withRegion(Regions.fromName(regionName))
                .withCredentials(new AWSStaticCredentialsProvider(credentials))
                .build();
        ObjectMetadata metadata = new ObjectMetadata();
        if(null!=contentType) {
            metadata.setContentType(contentType);
        }
        PutObjectRequest request = new PutObjectRequest(s3BucketName, path, inputStream, metadata);
        if(null!=cannedACL) {
            request.withCannedAcl(CannedAccessControlList.valueOf(cannedACL));
        }
        client.putObject(request);
        return client.getUrl(s3BucketName, path).toString();
    }

    @Helper(description = "Upload to s3")
    public static String upload(String content, String path, String contentType, String s3BucketName, String s3AccessKey, String s3SecretKey, String cannedACL, String regionName) {
        byte[] byteArray = content.getBytes(StandardCharsets.UTF_8);
        InputStream inputStream = new ByteArrayInputStream(byteArray);
        return upload(inputStream,path, contentType, s3BucketName, s3AccessKey, s3SecretKey, cannedACL, regionName);
    }

    @Helper(description = "Upload to s3")
    public static String upload(byte[] byteArray, String path, String contentType, String s3BucketName, String s3AccessKey, String s3SecretKey, String cannedACL, String regionName) {
        InputStream inputStream = new ByteArrayInputStream(byteArray);
        return upload(inputStream,path, contentType, s3BucketName, s3AccessKey, s3SecretKey, cannedACL, regionName);
    }

    @Helper(description = "Upload to s3")
    public static String upload(byte[] byteArray, String path, String contentType, String cannedAcl, String region) {
        InputStream inputStream = new ByteArrayInputStream(byteArray);
        return upload(inputStream,path, contentType, property.getS3BucketName(), property.getS3AccessKey(), property.getS3SecretKey(), cannedAcl, region);
    }

    @Helper(description = "Upload to s3")
    public static String upload(String content, String path, String contentType, String cannedAcl, String region) {
        byte[] byteArray = content.getBytes(StandardCharsets.UTF_8);
        InputStream is = new ByteArrayInputStream(byteArray);
        return upload(is,path, contentType, property.getS3BucketName(), property.getS3AccessKey(), property.getS3SecretKey(), cannedAcl, region);
    }

    @Helper(description = "Upload to s3")
    public static String upload(InputStream inputStream, String path, String contentType, String cannedAcl, String region) {
        return upload(inputStream,path, contentType, property.getS3BucketName(), property.getS3AccessKey(), property.getS3SecretKey(), cannedAcl, region);
    }
}
