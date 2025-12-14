package com.onlineshop.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.TimeUnit;

/**
 * 圖片控制器
 * 負責從本地文件系統提供產品圖片
 */
@Controller
@RequestMapping("/images/products")
public class ImageController {

    @Value("${upload.path:src/main/resources/static/images/products}")
    private String uploadPath;

    /**
     * 獲取產品圖片
     * @param filename 圖片檔案名稱
     * @return 圖片資源
     */
    @GetMapping("/{filename:.+}")
    public ResponseEntity<Resource> getProductImage(@PathVariable String filename) {
        try {
            // 構建圖片文件路徑
            Path imagePath = Paths.get(uploadPath).resolve(filename);
            File imageFile = imagePath.toFile();
            
            System.out.println("=== ImageController Debug ===");
            System.out.println("Requested filename: " + filename);
            System.out.println("Upload path: " + uploadPath);
            System.out.println("Full image path: " + imagePath.toAbsolutePath());
            System.out.println("File exists: " + imageFile.exists());
            System.out.println("File readable: " + imageFile.canRead());
            System.out.println("File size: " + (imageFile.exists() ? imageFile.length() : 0) + " bytes");
            
            if (!imageFile.exists() || !imageFile.canRead()) {
                System.err.println("Image not found or not readable: " + imagePath);
                return ResponseEntity.notFound().build();
            }

            // 創建文件系統資源
            Resource resource = new FileSystemResource(imageFile);

            // 確定檔案的MIME 類型
            String contentType = determineContentType(filename);
            System.out.println("Content type: " + contentType);

            // 設置緩存控制
            CacheControl cacheControl = CacheControl.maxAge(1, TimeUnit.HOURS)
                    .cachePublic();

            System.out.println("Successfully serving image: " + filename);
            
            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(contentType))
                    .cacheControl(cacheControl)
                    .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + filename + "\"")
                    .header(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN, "*")
                    .body(resource);    
        } catch (Exception e) {
            // 記錄錯誤但不拋出異常，返回 404
            System.err.println("Error loading image: " + filename);
            e.printStackTrace();
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * 根據檔案副檔名確定 MIME 類型
     * @param filename 檔案名稱
     * @return MIME 類型
     */
    private String determineContentType(String filename) {
        String extension = filename.substring(filename.lastIndexOf(".") + 1).toLowerCase();
        
        switch (extension) {
            case "jpg":
            case "jpeg":
                return "image/jpeg";
            case "png":
                return "image/png";
            case "gif":
                return "image/gif";
            case "webp":
                return "image/webp";
            case "svg":
                return "image/svg+xml";
            default:
                return "application/octet-stream";
        }
    }
}