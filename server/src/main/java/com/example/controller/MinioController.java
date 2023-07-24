package com.example.controller;

import cn.hutool.core.util.IdUtil;
import com.example.service.MinioService;
import io.minio.http.Method;
import io.minio.messages.Part;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping(value = "/minio")
@CrossOrigin
public class MinioController {

    @Resource
    private MinioService minioService;

    /**
     * 获取上传地址
     *
     * @param fileName   文件名
     * @param totalChunk 总分片数
     */
    @GetMapping(value = "/getUploadUrl")
    public Map<String, Object> getUploadUrl(@RequestParam(name = "fileName") String fileName, @RequestParam(name = "totalChunk") Integer totalChunk) throws Exception {
        String day = LocalDateTime.now(ZoneId.of("+8")).format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String[] fileNameSplit = fileName.split("\\.");
        String objectName = String.format("test/%s/%s.%s", day, IdUtil.getSnowflakeNextIdStr(), fileNameSplit[fileNameSplit.length - 1]);
        // 获取上传ID
        String uploadId = minioService.getUploadId(objectName);
        // 组装上传地址
        Map<String, Object> uploadUrls = new HashMap<>();
        Map<String, String> extraQueryParams = new HashMap<>();
        extraQueryParams.put("uploadId", uploadId);
        for (int i = 0; i <= totalChunk; i++) {
            extraQueryParams.put("partNumber", String.valueOf(i));
            String uploadUrl = minioService.getPresignedObjectUrl(Method.PUT, objectName, 1, TimeUnit.DAYS, extraQueryParams);
            uploadUrls.put(String.format("chunk_%s", i), uploadUrl);
        }
        return Map.of(
                "objectName", objectName,
                "uploadId", uploadId,
                "uploadUrls", uploadUrls
        );
    }

    /**
     * 完成分片上传
     *
     * @param uploadId   上传ID
     * @param objectName 上传路径
     */
    @GetMapping(value = "/completeUpload")
    public String completeUpload(@RequestParam(name = "uploadId") String uploadId, @RequestParam(name = "objectName") String objectName) throws Exception {
        List<Part> parts = minioService.listParts(objectName, uploadId);
        minioService.completeMultipartUpload(objectName, uploadId, parts.toArray(new Part[]{}));
        return objectName;
    }
}
