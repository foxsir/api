package com.visionbagel.utils;

import com.aliyun.core.utils.IOUtils;
import jakarta.enterprise.context.ApplicationScoped;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;
import java.io.*;
import java.util.Iterator;
import org.apache.commons.codec.binary.Base64;

@ApplicationScoped
public class MediaTools {
    public static String getImageExtensionName(InputStream file) throws IOException {
        String name = "";
        Iterator<ImageReader> imageReaders = ImageIO.getImageReaders(MediaTools.getImageInputStream(file));
        while (imageReaders.hasNext()) {
            ImageReader reader = imageReaders.next();
            name = reader.getFormatName();
        }

        return name;
    }

    public static String getImageExtensionName(File file) throws IOException {
        Iterator<ImageReader> imageReaders = ImageIO.getImageReaders(MediaTools.getImageInputStream(file));
        String name = "";
        while (imageReaders.hasNext()) {
            ImageReader reader = imageReaders.next();
            name = reader.getFormatName();
        }

        return name;
    }

    public static InputStream base64ToImageInputStream(String content) {
        String base64 = content.substring(content.indexOf(",")+1);

        // Note preferred way of declaring an array variable
        byte[] data = Base64.decodeBase64(base64);
        return new ByteArrayInputStream(data);
    }

    private static ImageInputStream getImageInputStream(InputStream file) throws IOException {
        return ImageIO.createImageInputStream(file);
    }

    private static ImageInputStream getImageInputStream(File file) throws IOException {
        return ImageIO.createImageInputStream(file);
    }
}
