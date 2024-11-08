package com.visionbagel.utils;

import jakarta.enterprise.context.ApplicationScoped;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;

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

    private static ImageInputStream getImageInputStream(InputStream file) throws IOException {
        return ImageIO.createImageInputStream(file);
    }

    private static ImageInputStream getImageInputStream(File file) throws IOException {
        return ImageIO.createImageInputStream(file);
    }
}
