package com.zencoo.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class CloudinaryService {
    private final Cloudinary cloudinary;

    public CloudinaryService(
        @Value("${cloudinary.cloud_name}") String cloudName,
        @Value("${cloudinary.api_key}") String apiKey,
        @Value("${cloudinary.api_secret}") String apiSecret
    ) {
        this.cloudinary = new Cloudinary(ObjectUtils.asMap(
            "cloud_name", cloudName,
            "api_key", apiKey,
            "api_secret", apiSecret
        ));
    }

    public void deleteImageByUrl(String url) {
        try {
            String publicId = extractPublicId(url);
            if (publicId != null) {
                cloudinary.uploader().destroy(publicId, ObjectUtils.emptyMap());
            }
        } catch (Exception e) {
            // Log error, but don't fail the whole operation
            System.err.println("Failed to delete image from Cloudinary: " + e.getMessage());
        }
    }

    // Extracts the public ID from a Cloudinary URL
    private String extractPublicId(String url) {
        if (url == null || !url.contains("/upload/")) return null;
        String[] parts = url.split("/upload/");
        if (parts.length < 2) return null;
        String path = parts[1];
        // Remove any query params
        int qIdx = path.indexOf('?');
        if (qIdx > 0) path = path.substring(0, qIdx);
        // Remove file extension
        int dotIdx = path.lastIndexOf('.');
        if (dotIdx > 0) path = path.substring(0, dotIdx);
        return path;
    }
}