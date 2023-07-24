package com.example.service;

import com.example.config.MinioProperties;
import com.example.config.MyMinioClient;
import io.minio.GetPresignedObjectUrlArgs;
import io.minio.MinioClient;
import io.minio.errors.*;
import io.minio.http.Method;
import io.minio.messages.Part;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

@Service
public class MinioService {

    @Resource
    private MyMinioClient myMinioClient;
    @Resource
    private MinioClient minioClient;
    @Resource
    private MinioProperties minioProperties;

    /**
     * 获取上传ID
     *
     * @param objectName 路径/文件名.后缀
     */
    public String getUploadId(String objectName) throws InsufficientDataException, IOException, NoSuchAlgorithmException, InvalidKeyException, XmlParserException, InternalException, ExecutionException, InterruptedException {
        return myMinioClient.createMultipartUploadAsync(minioProperties.getBucketName(), null, objectName, null, null).get().result().uploadId();
    }

    /**
     * 获取临时签名地址
     *
     * @param method           请求方法 PUT是上传
     * @param objectName       路径/文件名.后缀
     * @param duration         有效时长
     * @param unit             时长单位
     * @param extraQueryParams 拓展查询字段 主要防止分片签名一样
     */
    public String getPresignedObjectUrl(Method method, String objectName, int duration, TimeUnit unit, Map<String, String> extraQueryParams) throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        return minioClient.getPresignedObjectUrl(GetPresignedObjectUrlArgs.builder()
                .method(method)
                .bucket(minioProperties.getBucketName())
                .object(objectName)
                .expiry(duration, unit)
                .extraQueryParams(extraQueryParams)
                .build()
        );
    }

    /**
     * 查询分片数据
     *
     * @param objectName 路径/文件名.后缀
     * @param uploadId   上传ID
     */
    public List<Part> listParts(String objectName, String uploadId) throws InsufficientDataException, IOException, NoSuchAlgorithmException, InvalidKeyException, XmlParserException, InternalException, ExecutionException, InterruptedException {
        return myMinioClient.listPartsAsync(minioProperties.getBucketName(), null, objectName, null, null, uploadId, null, null).get().result().partList();
    }

    /**
     * 分片合并文件
     *
     * @param objectName 路径/文件名.后缀
     * @param uploadId   上传ID
     * @param parts      分片数据
     */
    public void completeMultipartUpload(String objectName, String uploadId, Part[] parts) throws InsufficientDataException, IOException, NoSuchAlgorithmException, InvalidKeyException, XmlParserException, InternalException, ExecutionException, InterruptedException {
        myMinioClient.completeMultipartUploadAsync(minioProperties.getBucketName(), null, objectName, uploadId, parts, null, null).get();
    }
}
