package com.visionbagel.resources;

import com.aliyun.oss.*;
import com.aliyun.oss.common.comm.SignVersion;
import com.aliyun.oss.model.ObjectMetadata;
import com.visionbagel.payload.ResultOfData;
import com.visionbagel.payload.SingleFileBody;
import com.visionbagel.utils.FileTools;
import com.visionbagel.utils.MediaTools;
import com.visionbagel.utils.StorageForOSS;
import jakarta.activation.MimetypesFileTypeMap;
import jakarta.annotation.security.RolesAllowed;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;

import com.aliyun.oss.common.auth.*;
import com.aliyun.oss.model.PutObjectRequest;
import com.aliyun.oss.model.PutObjectResult;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.jboss.resteasy.annotations.providers.multipart.MultipartForm;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Path("/storage")
@RolesAllowed({"User"})
public class StorageResource {
    private static final Logger log = LoggerFactory.getLogger(StorageResource.class);

    @ConfigProperty(name = "alioss.domain")
    public String ossDomain;

    @POST()
    @Transactional
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    public Response upload(@Valid @MultipartForm SingleFileBody data) throws IOException {

//        // Endpoint以华东1（杭州）为例，其它Region请按实际情况填写。
//        String endpoint = "https://oss-cn-beijing.aliyuncs.com";
//        // 从环境变量中获取访问凭证。运行本代码示例之前，请确保已设置环境变量OSS_ACCESS_KEY_ID和OSS_ACCESS_KEY_SECRET。
//        // 填写Bucket名称，例如examplebucket。
//        String bucketName = "visionbagel";
//        // 填写Object完整路径，完整路径中不能包含Bucket名称，例如exampledir/exampleobject.txt。
//        // 填写Bucket所在地域。以华东1（杭州）为例，Region填写为cn-hangzhou。
//        String region = "cn-beijing";
//
//        String accessKeyId = FileTools.getResource("alioss/accessKeyId.txt");
//        String accessKeySecret = FileTools.getResource("alioss/accessKeySecret.txt");
//
//        assert accessKeyId != null;
//        assert accessKeySecret != null;
//        CredentialsProvider credentialsProvider = new DefaultCredentialProvider(accessKeyId, accessKeySecret);
//
//        // 创建OSSClient实例。
//        ClientBuilderConfiguration clientBuilderConfiguration = new ClientBuilderConfiguration();
//        clientBuilderConfiguration.setSignatureVersion(SignVersion.V4);
//        OSS ossClient = OSSClientBuilder.create()
//                .endpoint(endpoint)
//                .credentialsProvider(credentialsProvider)
//                .clientConfiguration(clientBuilderConfiguration)
//                .region(region)
//                .build();
//
//        String suffix = MediaTools.getImageExtensionName(data.file).toLowerCase();
//        String objectName = String.join(".", UUID.randomUUID().toString(), suffix);
//
//        try {
//            // 创建PutObjectRequest对象。
////            PutObjectRequest putObjectRequest = new PutObjectRequest(bucketName, objectName, new ByteArrayInputStream(content.getBytes()));
//            PutObjectRequest putObjectRequest = new PutObjectRequest(bucketName, objectName, data.file);
//
//            // 如果需要上传时设置存储类型和访问权限，请参考以下示例代码。
//            // ObjectMetadata metadata = new ObjectMetadata();
//            // metadata.setHeader(OSSHeaders.OSS_STORAGE_CLASS, StorageClass.Standard.toString());
//            // metadata.setObjectAcl(CannedAccessControlList.Private);
//            // putObjectRequest.setMetadata(metadata);
//
//            // 上传字符串。
//            ObjectMetadata meta = new ObjectMetadata();
//            meta.setContentType(String.format("image/%s", suffix));
//            putObjectRequest.setMetadata(meta);
//            ossClient.putObject(putObjectRequest);
//        } catch (OSSException oe) {
//            System.out.println("Caught an OSSException, which means your request made it to OSS, "
//                    + "but was rejected with an error response for some reason.");
//            System.out.println("Error Message:" + oe.getErrorMessage());
//            System.out.println("Error Code:" + oe.getErrorCode());
//            System.out.println("Request ID:" + oe.getRequestId());
//            System.out.println("Host ID:" + oe.getHostId());
//        } catch (ClientException ce) {
//            System.out.println("Caught an ClientException, which means the client encountered "
//                    + "a serious internal problem while trying to communicate with OSS, "
//                    + "such as not being able to access the network.");
//            System.out.println("Error Message:" + ce.getMessage());
//        } finally {
//            ossClient.shutdown();
//        }

        StorageForOSS storageForOSS = new StorageForOSS();
        String objectName = storageForOSS.upload(data);

        String url = String.join("/", ossDomain, objectName);
        return Response
                .status(Response.Status.OK.getStatusCode())
                .entity(new ResultOfData<>(Map.of("url", url)))
                .build();
    }
}
