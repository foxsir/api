package com.visionbagel.utils;

import jakarta.enterprise.context.ApplicationScoped;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@ApplicationScoped
public class FileTools {
    public static String getResource(String name) {
        try (var appId = FileTools.class.getClassLoader().getResourceAsStream(name)) {
            assert appId != null;
            return new String(appId.readAllBytes(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            return null;
        }
    }
}
