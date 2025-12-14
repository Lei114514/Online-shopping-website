package com.onlineshop.controller;

import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.concurrent.TimeUnit;

/**
 * 圖片控制器
 * 負責從本地文件系統提供產品圖片
 */
@Controller
@RequestMapping("/images/products")
public class ImageController {

    // 圖片存儲的基礎路徑
    private static final String IMAGE_DIRECTORY = "static/images/products/";

    /**
     * 獲取產品圖片
     * @param filename 圖片檔案名稱
     * @return 圖片資源
     */
    @GetMapping("/{filename:.+}")
    public ResponseEntity<Resource> getProductImage(@PathVariable String filename) {
        try {
            // 從classpath 讀取圖片資源
            Resource resource = new ClassPathResource(IMAGE_DIRECTORY + filename);
            
            if (!resource.exists() || !resource.isReadable()) {
                // 返回 404 而不是錯誤，這樣不會破壞頁面
                return ResponseEntity.notFound().build();
            }

            // 確定檔案的 MIME 類型
            String contentType = determineContentType(filename);

            // 設置緩存控制
            CacheControl cacheControl = CacheControl.maxAge(1, TimeUnit.HOURS)
                    .cachePublic();

            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(contentType))
                    .cacheControl(cacheControl)
                    .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + filename + "\"")
                    .header(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN, "*")
                    .body(resource);
                    
        } catch (Exception e) {
            // 記錄錯誤但不拋出異常，返回 404
            System.err.println("Error loading image: " + filename + " - " + e.getMessage());
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