package com.imapcloud.nest.utils;

import cn.hutool.core.io.FileTypeUtil;
import cn.hutool.json.JSONUtil;
import com.geoai.common.core.util.BizIdUtils;
import com.imapcloud.nest.v2.common.exception.BusinessException;
import com.imapcloud.nest.v2.common.properties.GeoaiUosProperties;
import com.imapcloud.nest.v2.common.properties.MinioConfig;
import com.imapcloud.nest.v2.manager.dataobj.in.CommonFileInDO;
import io.minio.*;
import io.minio.http.Method;
import io.minio.messages.Item;
import io.minio.messages.Tags;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * MinIo文件服务器工具类
 *
 * @author: zhengxd
 * @create: 2020/10/21
 **/
@Service
@Slf4j
public class MinIoUnit {

    @Resource
    private GeoaiUosProperties geoaiUosProperties;

    private static MinioClient client;

    /**
     * 排序
     */
    public final static boolean SORT = true;
    /**
     * 不排序
     */
    public final static boolean NOT_SORT = false;
    /**
     * 默认过期时间(分钟)
     */
    private final static Integer DEFAULT_EXPIRY = 60;

    private static String chunkBucKet;
    private static String url;

    private static String outUrl;

//    /**
//     * 桶占位符
//     */
//    private static final String BUCKET_PARAM = "${bucket}";
//    /**
//     * bucket权限-只读
//     */
//    private static final String READ_ONLY = "{\"Version\":\"2012-10-17\",\"Statement\":[{\"Effect\":\"Allow\",\"Principal\":{\"AWS\":[\"*\"]},\"Action\":[\"s3:GetBucketLocation\",\"s3:ListBucket\"],\"Resource\":[\"arn:aws:s3:::" + BUCKET_PARAM + "\"]},{\"Effect\":\"Allow\",\"Principal\":{\"AWS\":[\"*\"]},\"Action\":[\"s3:GetObject\"],\"Resource\":[\"arn:aws:s3:::" + BUCKET_PARAM + "/*\"]}]}";
//    /**
//     * bucket权限-只读
//     */
//    private static final String WRITE_ONLY = "{\"Version\":\"2012-10-17\",\"Statement\":[{\"Effect\":\"Allow\",\"Principal\":{\"AWS\":[\"*\"]},\"Action\":[\"s3:GetBucketLocation\",\"s3:ListBucketMultipartUploads\"],\"Resource\":[\"arn:aws:s3:::" + BUCKET_PARAM + "\"]},{\"Effect\":\"Allow\",\"Principal\":{\"AWS\":[\"*\"]},\"Action\":[\"s3:AbortMultipartUpload\",\"s3:DeleteObject\",\"s3:ListMultipartUploadParts\",\"s3:PutObject\"],\"Resource\":[\"arn:aws:s3:::" + BUCKET_PARAM + "/*\"]}]}";
//    /**
//     * bucket权限-读写
//     */
//    private static final String READ_WRITE = "{\"Version\":\"2012-10-17\",\"Statement\":[{\"Effect\":\"Allow\",\"Principal\":{\"AWS\":[\"*\"]},\"Action\":[\"s3:GetBucketLocation\",\"s3:ListBucket\",\"s3:ListBucketMultipartUploads\"],\"Resource\":[\"arn:aws:s3:::" + BUCKET_PARAM + "\"]},{\"Effect\":\"Allow\",\"Principal\":{\"AWS\":[\"*\"]},\"Action\":[\"s3:DeleteObject\",\"s3:GetObject\",\"s3:ListMultipartUploadParts\",\"s3:PutObject\",\"s3:AbortMultipartUpload\"],\"Resource\":[\"arn:aws:s3:::" + BUCKET_PARAM + "/*\"]}]}";


    /**
     * 初始化minio配置
     *
     * @return: void
     * @date : 2020/8/16 20:56
     */
    @PostConstruct
    public void init() {
        try {
            MinioConfig minio = geoaiUosProperties.getMinio();
            OkHttpClient okHttpClient = new OkHttpClient().newBuilder()
                    .connectTimeout(1, TimeUnit.MINUTES)
                    .writeTimeout(5, TimeUnit.MINUTES)
                    .readTimeout(5, TimeUnit.MINUTES)
                    // 重试三次
                    .addInterceptor(new OkhttpInterceptor(3))
                    .protocols(Arrays.asList(Protocol.HTTP_1_1))
                    .build();
            client = MinioClient.builder().endpoint(minio.getUrl()).credentials(minio.getAccessKey(), minio.getSecretKey())
                    .httpClient(okHttpClient).build();
            chunkBucKet = minio.getBucketName();
            url = minio.getUrl();
            outUrl = minio.getOutUrl();
            if (!isBucketExist(chunkBucKet)) {
                createBucket(chunkBucKet);
            }
        } catch (Exception e) {
            log.error("初始化minio配置异常: ", e);
        }
    }

    @AllArgsConstructor
    public static class OkhttpInterceptor implements Interceptor {

        private int maxRetry;
        @Override
        public Response intercept(Chain chain) throws IOException {
            return retry(chain, 1);
        }

        Response retry(Chain chain, int num) {
            Request request = chain.request();
            Response response = null;
            try {
                response = chain.proceed(request);
            } catch (Exception e) {
                log.info("#MinIoUnit.OkhttpInterceptor.retry# num={}, chain={}, error={}", num, JSONUtil.toJsonStr(chain), e.getMessage());
                if (maxRetry > num) {
                    return retry(chain, num + 1);
                }
            }
            return response;
        }
    }

//    /**
//     * 文件上传
//     *
//     * @param bucketName 桶名称
//     * @param file       文件
//     * @param fileName   文件名称
//     * @param path       文件在桶下的路径
//     * @return
//     * @deprecated 2.2.3，将在后续版本删除
//     */
//    @Deprecated
//    @SneakyThrows(Exception.class)
//    public static String upload(String bucketName, MultipartFile file, String fileName, String path) {
//        final InputStream is = file.getInputStream();
//        try {
//            client.putObject(PutObjectArgs.builder().bucket(bucketName).object(path + fileName).stream(is, is.available(), -1).contentType("image/png").build());
//        } finally {
//            is.close();
//        }
//        return path + fileName;
//
//    }

    /**
     * @deprecated 2.2.3，将在2023/06/30后删除
     */
    @Deprecated
    @SneakyThrows(Exception.class)
    public static String uploadZip(String bucketName, InputStream is, String fileName, String path) {
//        final InputStream is = file.getInputStream();
        try {
            client.putObject(PutObjectArgs.builder().bucket(bucketName).object(path + fileName).stream(is, is.available(), -1).contentType("application/x-zip-compressed").build());
        } finally {
            is.close();
        }
        return path + fileName;

    }

//    /**
//     * 文件上传
//     * @deprecated 2.2.3，将在2023/06/30后删除
//     * @param bucketName 桶名称
//     * @param is         文件流
//     * @param fileName   文件名称
//     * @param path       文件在桶下的路径
//     */
//    @Deprecated
//    @SneakyThrows(Exception.class)
//    public static String upload(InputStream is, String bucketName, String fileName, String path) {
//        try {
//            PutObjectArgs putObjectArgs = PutObjectArgs.builder()
//                    .bucket(bucketName)
//                    .object(path + fileName)
//                    .stream(is, is.available(), -1)
//                    .contentType("image/png")
//                    .build();
//            client.putObject(putObjectArgs);
//        } finally {
//            is.close();
//        }
//        return path + fileName;
//
//    }

//    /**
//     * @deprecated 2.2.3，将在后续版本删除
//     */
//    @Deprecated
//    @SneakyThrows(Exception.class)
//    public static String upload(InputStream is, String bucketName, String fileName, String path, String contenType) {
//        try {
//            PutObjectArgs putObjectArgs = PutObjectArgs.builder()
//                    .bucket(bucketName)
//                    .object(path + fileName)
//                    .stream(is, is.available(), -1)
//                    .contentType(contenType)
//                    .build();
//            client.putObject(putObjectArgs);
//        } finally {
//            is.close();
//        }
//        return path + fileName;
//
//    }

//    /**
//     * 下载文件，拿到inputStream
//     * @deprecated 2.2.3，后续请勿使用该接口
//     */
//    @Deprecated
//    @SneakyThrows(Exception.class)
//    public static InputStream download(String bucketName, String fileName) {
//        InputStream is = client.getObject(GetObjectArgs.builder().bucket(bucketName).object(fileName).build());
//        return is;
//    }

    /**
     * 存储桶是否存在
     *
     * @param bucketName 存储桶名称
     * @return true/false
     */
    @SneakyThrows
    private static boolean isBucketExist(String bucketName) {
        return client.bucketExists(BucketExistsArgs.builder().bucket(bucketName).build());
    }

    /**
     * 创建存储桶
     *
     * @param bucketName 存储桶名称
     * @return true/false
     */
    @SneakyThrows
    private static boolean createBucket(String bucketName) {
        client.makeBucket(MakeBucketArgs.builder().bucket(bucketName).build());
        return true;
    }

    /**
     * 获取访问对象的外链地址
     * @param bucketName 存储桶名称
     * @param objectName 对象名称
     * @param expiry     过期时间(分钟) 最大为7天 超过7天则默认最大值
     * @return viewUrl
     * @deprecated 2.2.3，仅为兼容CPS保留，将在2023/06/30后续版本删除
     */
    @Deprecated
    @SneakyThrows
    public static String getObjectUrl(String bucketName, String objectName, Integer expiry) {
        if (bucketName == null) {
            bucketName = chunkBucKet;
        }
        expiry = expiryHandle(expiry);
        return client.getPresignedObjectUrl(
                GetPresignedObjectUrlArgs.builder()
                        .method(Method.GET)
                        .bucket(bucketName)
                        .object(objectName)
                        .expiry(expiry)
                        .build()
        );
    }

    /**
     * 创建上传文件对象的外链
     *
     * @param bucketName 存储桶名称
     * @param objectName 欲上传文件对象的名称
     * @param expiry     过期时间(分钟) 最大为7天 超过7天则默认最大值
     * @return uploadUrl
     * @deprecated 2.2.3，仅为兼容CPS保留，将在2023/06/30后续版本删除
     */
    @Deprecated
    @SneakyThrows
    public static String createUploadUrl(String bucketName, String objectName, Integer expiry) {
        if (null == bucketName) {
            bucketName = chunkBucKet;
        }
        expiry = expiryHandle(expiry);
        String objectUrl = client.getPresignedObjectUrl(
                GetPresignedObjectUrlArgs.builder()
                        .method(Method.PUT)
                        .bucket(bucketName)
                        .object(objectName)
                        .expiry(expiry)
                        .build()
        );
        //内外网地址替换
        if (objectUrl.contains(url)) {
            objectUrl = objectUrl.replace(url, outUrl);
        }
        return objectUrl;
    }

//    /**
//     * 创建上传文件对象的外链
//     *
//     * @param bucketName 存储桶名称
//     * @param objectName 欲上传文件对象的名称
//     * @return uploadUrl
//     */
//    public static String createUploadUrl(String bucketName, String objectName) {
//        return createUploadUrl(bucketName, objectName, DEFAULT_EXPIRY);
//    }

    /**
     * 批量创建分片上传外链
     *
     * @param bucketName 存储桶名称
     * @param objectMD5  欲上传分片文件主文件的MD5
     * @param chunkCount 分片数量
     * @return uploadChunkUrls
     * @deprecated 2.2.3，仅为兼容CPS保留，将在2023/06/30后续版本删除
     */
    @Deprecated
    public static List<String> createUploadChunkUrlList(String bucketName, String objectMD5, Integer chunkCount) {
        if (null == bucketName) {
            bucketName = chunkBucKet;
        }
        if (null == objectMD5) {
            return null;
        }
        objectMD5 += "/";
        if (null == chunkCount || 0 == chunkCount) {
            return null;
        }
        List<String> urlList = new ArrayList<>(chunkCount);
        for (int i = 1; i <= chunkCount; i++) {
            String objectName = objectMD5 + i + ".chunk";
            urlList.add(createUploadUrl(bucketName, objectName, DEFAULT_EXPIRY));
        }
        return urlList;
    }

    /**
     * 创建指定序号的分片文件上传外链
     *
     * @param bucketName 存储桶名称
     * @param objectMD5  欲上传分片文件主文件的MD5
     * @param partNumber 分片序号
     * @return uploadChunkUrl
     * @deprecated 2.2.3，仅为兼容CPS保留，将在2023/06/30后续版本删除
     */
    @Deprecated
    public static String createUploadChunkUrl(String bucketName, String objectMD5, Integer partNumber) {
        if (null == bucketName) {
            bucketName = chunkBucKet;
        }
        if (null == objectMD5) {
            return null;
        }
        objectMD5 += "/" + partNumber + ".chunk";
        return createUploadUrl(bucketName, objectMD5, DEFAULT_EXPIRY);
    }

    /**
     * 获取对象文件名称列表
     *
     * @param bucketName 存储桶名称
     * @param prefix     对象名称前缀
     * @param sort       是否排序(升序)
     * @return objectNames
     * @deprecated 2.2.3，仅为兼容CPS保留，将在2023/06/30后续版本删除
     */
    @Deprecated
    @SneakyThrows
    private static List<String> listObjectNames(String bucketName, String prefix, Boolean sort) {
        ListObjectsArgs listObjectsArgs;
        if (null == prefix) {
            listObjectsArgs = ListObjectsArgs.builder()
                    .bucket(bucketName)
                    .recursive(true)
                    .build();
        } else {
            listObjectsArgs = ListObjectsArgs.builder()
                    .bucket(bucketName)
                    .prefix(prefix)
                    .recursive(true)
                    .build();
        }
        Iterable<Result<Item>> chunks = client.listObjects(listObjectsArgs);
        List<String> chunkPaths = new ArrayList<>();
        for (Result<Item> item : chunks) {
            chunkPaths.add(item.get().objectName());
        }
        if (sort) {
            return chunkPaths.stream().distinct().collect(Collectors.toList());
        }
        return chunkPaths;
    }

    /**
     * 获取对象文件名称列表
     *
     * @param bucketName 存储桶名称
     * @param prefix     对象名称前缀
     * @return objectNames
     * @deprecated 2.2.3，仅为兼容CPS保留，将在2023/06/30后续版本删除
     */
    @Deprecated
    private static List<String> listObjectNames(String bucketName, String prefix) {
        if (null == bucketName) {
            bucketName = chunkBucKet;
        }
        return listObjectNames(bucketName, prefix, NOT_SORT);
    }

//    /**
//     * 获取分片文件名称列表
//     *
//     * @param bucketName 存储桶名称
//     * @param ObjectMd5  对象Md5
//     * @return objectChunkNames
//     */
//    public static List<String> listChunkObjectNames(String bucketName, String ObjectMd5) {
//        if (null == bucketName) {
//            bucketName = chunkBucKet;
//        }
//        if (null == ObjectMd5) {
//            return null;
//        }
//        return listObjectNames(bucketName, ObjectMd5, SORT);
//    }

    /**
     * 获取分片名称地址HashMap key=分片序号 value=分片文件地址
     *
     * @param bucketName 存储桶名称
     * @param ObjectMd5  对象Md5
     * @return objectChunkNameMap
     * @deprecated 2.2.3，仅为兼容CPS保留，将在2023/06/30后续版本删除
     */
    @Deprecated
    public static Map<Integer, String> mapChunkObjectNames(String bucketName, String ObjectMd5) {
        if (null == bucketName) {
            bucketName = chunkBucKet;
        }
        if (null == ObjectMd5) {
            return null;
        }
        List<String> chunkPaths = listObjectNames(bucketName, ObjectMd5);
        if (null == chunkPaths || chunkPaths.size() == 0) {
            return null;
        }
        Map<Integer, String> chunkMap = new HashMap<>(chunkPaths.size());
        for (String chunkName : chunkPaths) {
            Integer partNumber = Integer.parseInt(chunkName.substring(chunkName.lastIndexOf("/") + 1, chunkName.lastIndexOf(".")));
            chunkMap.put(partNumber, chunkName);
        }
        return chunkMap;
    }

//    /**
//     * 合并分片文件成对象文件
//     *
//     * @param chunkBucKetName   分片文件所在存储桶名称
//     * @param composeBucketName 合并后的对象文件存储的存储桶名称
//     * @param chunkNames        分片文件名称集合
//     * @param objectName        合并后的对象文件名称
//     * @return true/false
//     */
//    @SneakyThrows
//    public static boolean composeObject(String chunkBucKetName, String composeBucketName, List<String> chunkNames, String objectName) {
//        if (null == chunkBucKetName) {
//            chunkBucKetName = chunkBucKet;
//        }
//        if (null == composeBucketName) {
//            composeBucketName = chunkBucKet;
//        }
//        List<ComposeSource> sourceObjectList = new ArrayList<>(chunkNames.size());
//        for (String chunk : chunkNames) {
//            sourceObjectList.add(
//                    ComposeSource.builder()
//                            .bucket(chunkBucKetName)
//                            .object(chunk)
//                            .build()
//            );
//        }
//        client.composeObject(
//                ComposeObjectArgs.builder()
//                        .bucket(composeBucketName)
//                        .object(objectName)
//                        .sources(sourceObjectList)
//                        .build()
//        );
//        return true;
//    }

//    /**
//     * 合并分片文件成对象文件
//     *
//     * @param bucketName 存储桶名称
//     * @param chunkNames 分片文件名称集合
//     * @param objectName 合并后的对象文件名称
//     * @return true/false
//     */
//    public static boolean composeObject(String bucketName, List<String> chunkNames, String objectName) {
//        return composeObject(chunkBucKet, bucketName, chunkNames, objectName);
//    }

    /**
     * 将分钟数转换为秒数
     *
     * @param expiry 过期时间(分钟数)
     * @return expiry
     */
    private static int expiryHandle(Integer expiry) {
        expiry = expiry * 60;
        if (expiry > 604800) {
            return 604800;
        }
        return expiry;
    }

//    public static String replaceUrl(String uploadUrl) {
//        return uploadUrl.replaceAll(url, "");
//
//    }

//    /**
//     * 删除文件夹及文件
//     *
//     * @param bucketName bucket名称
//     * @param objectName 文件或文件夹名称
//     * @since tarzan LIU
//     */
//    private void deleteObject(String bucketName, String objectName) {
//        try {
//            if (StringUtils.isNotBlank(objectName)) {
//                if (objectName.endsWith(".") || objectName.endsWith("/")) {
//                    Iterable<Result<Item>> list = client.listObjects(bucketName, objectName);
//                    list.forEach(e -> {
//                        try {
//                            client.removeObject(bucketName, e.get().objectName());
//                        } catch (Exception el) {
//                            el.printStackTrace();
//                        }
//                    });
//                }
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }

//    /**
//     * 删除文件夹及文件
//     *
//     * @param bucketName     bucket名称
//     * @param objectNameList 文件或文件夹名称
//     */
//    private static void rmObjects(String bucketName, List<String> objectNameList) {
//        try {
//            if (CollectionUtil.isEmpty(objectNameList)) {
//                return;
//            }
//            List<DeleteObject> deleteObjectList = new LinkedList<>();
//            for (String objectName : objectNameList) {
//                objectName = replacePath(objectName);
//                if (objectName.startsWith("/")) {
//                    objectName = objectName.substring(1);
//                }
//                deleteObjectList.add(new DeleteObject(objectName));
//            }
//            Iterable<Result<DeleteError>> removeObjects = client.removeObjects(RemoveObjectsArgs.builder()
//                    .bucket(StringUtil.isEmpty(bucketName) ? chunkBucKet : bucketName)
//                    .objects(deleteObjectList).build());
//            log.info("#MinIoUtil.rmObjects# removeObjects={}", JSONUtil.toJsonStr(removeObjects));
//        } catch (Exception e) {
//            log.error("rmObjects---删除文件出错", e);
//        }
//    }

//    /**
//     * @deprecated 2.2.3，请使用{@link FileManager#deleteFiles(List)}替代
//     */
//    public static void rmObjects(List<String> objectNameList) {
//        rmObjects(chunkBucKet, objectNameList);
//    }

    /**
     * 获取文件类型
     *
     * @param is 是
     * @return {@link String}
     */
    public static String getFileType(InputStream is) {

        try {
            return FileTypeUtil.getType(is);
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                log.error("#MinIoUnit.getFileType# close", e);
            }
        }
    }

//    /**
//     * 获取文件名称 雪花ID_源文件名称
//     *
//     * @param originalFilename 原始文件名
//     * @return {@link String}
//     */
//    private static String getFileName(String originalFilename) {
//        return String.format("%s_%s", BizIdUtils.snowflakeIdStr(), originalFilename);
//    }

    /**
     * 替换路径 ，取消映射
     *
     * @param filePath
     * @return
     */
    private static String replacePath(String filePath) {
        String dataPath = "/data/origin", originalPath = "/origin", returnPath = filePath;
        if (org.springframework.util.StringUtils.isEmpty(filePath)) {
            return filePath;
        }
        if (filePath.startsWith(dataPath)) {
            returnPath = filePath.replaceAll(dataPath, "");
        } else {
            returnPath = filePath.replaceAll(originalPath, "");
        }
        return returnPath;
    }

//    /**
//     * @deprecated 2.2.3，使用新接口{@link FileManager#getInputSteam(java.lang.String)}替代
//     */
//    @Deprecated
//    public static InputStream getObject(String fileName) {
//        try {
//            fileName = replacePath(fileName);
//            ObjectStat objectStat = client.statObject(StatObjectArgs.builder().bucket(chunkBucKet).object(fileName).build());
//            return client.getObject(GetObjectArgs.builder()
//                    .bucket(objectStat.bucketName())
//                    .object(objectStat.name())
//                    .length(objectStat.length())
//                    .build());
//        } catch (Exception e) {
//            log.error("MinIoUnit.getObject",e);
////            log.info("#MinIoUnit.getObject# fileName={}, msg={}", fileName, e.getMessage());
//            return null;
//        }
//    }

//    /**
//     * @deprecated 2.2.3，使用FileManager接口替代，将在后续版本删除
//     */
//    @Deprecated
//    public static InputStream getObjectFile(String fileName) {
//        try {
//            return new FileInputStream(fileName);
//        } catch (Exception e) {
//            log.error("#MinIoUnit.getObjectFile# fileName={}, msg={}", fileName, e.getMessage());
//        }
//        return null;
//    }

    /**
     * @deprecated 2.2.3, 将在后续版本移除，本接口请勿使用，
     * 使用新接口{@link com.imapcloud.nest.v2.manager.rest.UploadManager#uploadFile(CommonFileInDO)}替代
     * 仅仅为录屏文件迁移定时任务保留，等流媒体管理版本上线后，该接口可以删除
     */
    @Deprecated
    public static boolean putObject(String fileName, InputStream inputStream, String contentType) {

        try {
            byte[] bytes = IOUtils.toByteArray(inputStream);
            inputStream = new ByteArrayInputStream(bytes);
            log.info("#MinIoUnit.putObjectAuto# fileName={}, contentType={}", fileName, contentType);
            PutObjectArgs putObjectArgs = PutObjectArgs.builder()
                    .bucket(chunkBucKet)
                    .object(fileName)
                    .stream(inputStream, inputStream.available(), 10 * 1024 * 1024)
                    .contentType(contentType)
                    .build();
            log.info("#MinIoUnit.putObjectAuto# putObjectArgs={}", putObjectArgs);
            ObjectWriteResponse objectWriteResponse = client.putObject(putObjectArgs);
            log.info("#MinIoUnit.putObjectAuto# putObjectArgs={}, objectWriteResponse={}", putObjectArgs, objectWriteResponse);
            return true;
        } catch (Exception e) {
            log.error("#MinIoUnit.putObjectAuto# fileName={}", fileName, e);
        } finally {
            try {
                if (inputStream != null) {
                    inputStream.close();
                }
            } catch (IOException e) {
                log.error("#MinIoUnit.putObjectAuto# close fileName={}", fileName, e);
            }
        }
        return false;
    }

//    /**
//     * @deprecated 2.2.3, 将在后续版本移除，本接口请勿使用，
//     * 使用新接口{@link com.imapcloud.nest.v2.manager.rest.UploadManager#uploadFile(CommonFileInDO)}替代
//     */
//    @Deprecated
//    public static boolean putObject(String fileName, InputStream inputStream) {
//
//        try {
//            PutObjectArgs putObjectArgs = PutObjectArgs.builder()
//                    .bucket(chunkBucKet)
//                    .object(fileName)
//                    .stream(inputStream, inputStream.available(), -1)
//                    .contentType("image/png")
//                    .build();
//            log.info("#MinIoUnit.putObject# putObjectArgs={}", putObjectArgs);
//            ObjectWriteResponse objectWriteResponse = client.putObject(putObjectArgs);
//            log.info("#MinIoUnit.putObject# putObjectArgs={}, objectWriteResponse={}", putObjectArgs, objectWriteResponse);
//            return true;
//        } catch (Exception e) {
//            log.error("#MinIoUnit.putObject# fileName={}", fileName, e);
//        } finally {
//            try {
//                if (inputStream != null) {
//                    inputStream.close();
//                }
//            } catch (IOException e) {
//                log.error("#MinIoUnit.putObject# close fileName={}", fileName, e);
//            }
//        }
//        return false;
//    }

//    /**
//     * @deprecated 2.2.3，使用新接口{@link FileManager#copyFile(java.lang.String, java.lang.String)}替代，将在后续版本删除
//     */
//    @Deprecated
//    public static void copy(String sourcePath, String targetPath) {
//        try {
//            sourcePath = replacePath(sourcePath);
//            CopyObjectArgs copyObjectArgs = CopyObjectArgs.builder()
//                    .source(CopySource.builder().bucket(chunkBucKet).object(sourcePath).build())
//                    .object(targetPath)
//                    .bucket(chunkBucKet)
//                    .build();
//            log.info("#MinIoUnit.copy# copyObjectArgs = {}", copyObjectArgs);
//            ObjectWriteResponse objectWriteResponse = client.copyObject(copyObjectArgs);
//            log.info("#MinIoUnit.copy# copyObjectArgs = {}, objectWriteResponse = {}", copyObjectArgs, objectWriteResponse);
//        } catch (Exception e) {
//            log.error("#MinIoUnit.copy# sourcePath={}, targetPath={}", sourcePath, targetPath, e);
//            throw new BusinessException("文件copy失败");
//        }
//    }

//    /**
//     * @deprecated 2.2.3，使用接口{@link FileManager#setFileTags(java.lang.String, java.util.Map)}替代，将在后续版本删除
//     */
//    @Deprecated
//    public static void setObjectTags(String object, Map<String, String> tags) {
//
//        try {
//            object = replacePath(object);
//            client.setObjectTags(SetObjectTagsArgs.builder().bucket(chunkBucKet).object(object).tags(tags).build());
//        } catch (Exception e) {
//            throw new BusinessException(e.getMessage());
//        }
//    }

    public static Map<String, String> getObjectTags(String bucket, String object) {

        try {
            Tags objectTags = client.getObjectTags(GetObjectTagsArgs.builder().bucket(bucket).object(object).build());
            return objectTags.get();
        } catch (Exception e) {
            throw new BusinessException(e.getMessage());
        }
    }

    public static Long getObjectSize(String bucket, String object) {
        try {
            ObjectStat objectStat = client.statObject(StatObjectArgs.builder().bucket(bucket).object(object).build());
            return objectStat.length();
        } catch (Exception e) {
            throw new BusinessException(e.getMessage());
        }
    }

    public static boolean objectExists(String object) {
        return objectExists(chunkBucKet, object);
    }

    public static boolean objectExists(String bucket, String object) {
        if (StringUtils.isBlank(bucket) || StringUtils.isBlank(object)) {
            return false;
        }
        try {
            object = replacePath(object);
            if (object.startsWith("/")) {
                object = object.substring(1);
            }
            if (object.contains("//")) {
                object = object.replace("//", "/");
            }
            Iterable<Result<Item>> listObjects = client.listObjects(ListObjectsArgs.builder()
                    .bucket(bucket).maxKeys(10)
                    .prefix(object).build());
            log.info("#MinIoUnit.objectExists# listObjects={}", listObjects);
            if (listObjects == null) {
                return false;
            }
            for (Result<Item> listObject : listObjects) {
                if (object.equals(listObject.get().objectName())) {
                    return true;
                }
            }
            return false;
        } catch (Exception e) {
            log.error("#MinIoUnit.objectExists# object={}", object, e);
            return false;
        }
    }

//    private static boolean objectMove(String sourceObject, String targetObject) {
//        try {
//            sourceObject = replacePath(sourceObject);
//            targetObject = replacePath(targetObject);
//            CopySource copySource = CopySource.builder().bucket(chunkBucKet).object(sourceObject).build();
//            CopyObjectArgs copyObjectArgs = CopyObjectArgs.builder().source(copySource)
//                    .bucket(chunkBucKet).object(targetObject).build();
//            client.copyObject(copyObjectArgs);
//            RemoveObjectArgs removeObjectArgs = RemoveObjectArgs.builder().bucket(chunkBucKet).object(sourceObject).build();
//            client.removeObject(removeObjectArgs);
//            return true;
//        } catch (Exception e) {
//            log.error("#MinIoUnit.objectMove# sourceObject={}, targetObject={}", sourceObject, targetObject, e);
//        }
//        return false;
//    }

    /**
     * @deprecated 2.2.3，将在后续版本删除，请勿使用
     */
    @Deprecated
    public static String renameFile(String fileName) {
        if (StringUtils.isBlank(fileName) || !fileName.contains(".")) {
            return BizIdUtils.snowflakeIdStr();
        }
        String[] split = fileName.split("\\.");
        return String.format("%s.%s", BizIdUtils.snowflakeIdStr(), split[split.length - 1]);
    }
}
