package com.visionbagel.utils;

import com.aliyun.oss.*;
import com.aliyun.oss.common.auth.CredentialsProvider;
import com.aliyun.oss.common.auth.DefaultCredentialProvider;
import com.aliyun.oss.common.comm.SignVersion;
import com.aliyun.oss.model.*;
import com.visionbagel.payload.SingleFileBody;
import org.apache.commons.io.FileUtils;

import java.io.*;
import java.util.List;
import java.util.UUID;

public class StorageForOSS {
    private final String bucketName = "visionbagel";

    public String upload(SingleFileBody data) throws IOException {
        String suffix = MediaTools.getImageExtensionName(data.file).toLowerCase();
        String objectName = String.join(".", UUID.randomUUID().toString(), suffix);
        OSS ossClient = this.getClient();

        try {
            // 创建PutObjectRequest对象。
            PutObjectRequest putObjectRequest = new PutObjectRequest(bucketName, objectName, data.file);
            // PutObjectRequest putObjectRequest = new PutObjectRequest(bucketName, objectName, data.file);

            // 如果需要上传时设置存储类型和访问权限，请参考以下示例代码。
            // ObjectMetadata metadata = new ObjectMetadata();
            // metadata.setHeader(OSSHeaders.OSS_STORAGE_CLASS, StorageClass.Standard.toString());
            // metadata.setObjectAcl(CannedAccessControlList.Private);
            // putObjectRequest.setMetadata(metadata);

            // 上传字符串。
            ObjectMetadata meta = new ObjectMetadata();
            meta.setContentType(String.format("image/%s", suffix));
            putObjectRequest.setMetadata(meta);
            ossClient.putObject(putObjectRequest);
        } catch (OSSException oe) {
            System.out.println("Caught an OSSException, which means your request made it to OSS, "
                + "but was rejected with an error response for some reason.");
            System.out.println("Error Message:" + oe.getErrorMessage());
            System.out.println("Error Code:" + oe.getErrorCode());
            System.out.println("Request ID:" + oe.getRequestId());
            System.out.println("Host ID:" + oe.getHostId());
        } catch (ClientException ce) {
            System.out.println("Caught an ClientException, which means the client encountered "
                + "a serious internal problem while trying to communicate with OSS, "
                + "such as not being able to access the network.");
            System.out.println("Error Message:" + ce.getMessage());
        } finally {
            ossClient.shutdown();
        }

        return objectName;
    }

    public int delete(String objectName) {
        OSS ossClient = this.getClient();
        DeleteObjectsRequest deleteObjectsRequest = new DeleteObjectsRequest(bucketName).withKeys(List.of(objectName));
        DeleteObjectsResult deleteObjectsResult = ossClient.deleteObjects(deleteObjectsRequest);
        return deleteObjectsResult.getResponse().getStatusCode();
    }

    public String updateBase64(String content) throws IOException {
        InputStream inputStream = MediaTools.base64ToImageInputStream(content);

        String extName = MediaTools.getImageExtensionName(
            MediaTools.base64ToImageInputStream(content)
        );
        File tmp = new File(String.format("/tmp/%s.%s", UUID.randomUUID(), extName));
        FileUtils.writeByteArrayToFile(tmp, inputStream.readAllBytes());

        SingleFileBody singleFileBody = new SingleFileBody();
        singleFileBody.file = tmp;
        String name = this.upload(singleFileBody);
        inputStream.close();
        if(tmp.delete()) {
            return name;
        } else {
            return null;
        }
    }

    private OSS getClient() {
        // Endpoint以华东1（杭州）为例，其它Region请按实际情况填写。
        String endpoint = "https://oss-cn-beijing.aliyuncs.com";
        // 从环境变量中获取访问凭证。运行本代码示例之前，请确保已设置环境变量OSS_ACCESS_KEY_ID和OSS_ACCESS_KEY_SECRET。
        // 填写Bucket名称，例如examplebucket。
        String bucketName = "visionbagel";
        // 填写Object完整路径，完整路径中不能包含Bucket名称，例如exampledir/exampleobject.txt。
        // 填写Bucket所在地域。以华东1（杭州）为例，Region填写为cn-hangzhou。
        String region = "cn-beijing";

        String accessKeyId = FileTools.getResource("alioss/accessKeyId.txt");
        String accessKeySecret = FileTools.getResource("alioss/accessKeySecret.txt");

        assert accessKeyId != null;
        assert accessKeySecret != null;
        CredentialsProvider credentialsProvider = new DefaultCredentialProvider(accessKeyId, accessKeySecret);

        // 创建OSSClient实例。
        ClientBuilderConfiguration clientBuilderConfiguration = new ClientBuilderConfiguration();
        clientBuilderConfiguration.setSignatureVersion(SignVersion.V4);
        return OSSClientBuilder.create()
            .endpoint(endpoint)
            .credentialsProvider(credentialsProvider)
            .clientConfiguration(clientBuilderConfiguration)
            .region(region)
            .build();
    }
}
