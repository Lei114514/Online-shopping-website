package com.onlineshop.controller;

import com.onlineshop.model.Product;
import com.onlineshop.model.Category;
import com.onlineshop.model.User;
import com.onlineshop.service.ProductService;
import com.onlineshop.service.UserService;
import com.onlineshop.repository.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.UUID;

/**
 * 商家商品管理控制器
 * 處理商家對自己商品的增刪改查操作
 */
@Controller
@RequestMapping("/merchant/products")
public class MerchantProductController {
    
    @Autowired
    private ProductService productService;
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private CategoryRepository categoryRepository;
    
    @Autowired
    private ResourceLoader resourceLoader;
    
    @Value("${upload.path:src/main/resources/static/images/products}")
    private String uploadPath;
    
    /**
     * 獲取當前登錄用戶
     */
    private User getCurrentUser(Authentication authentication) {
        String username = authentication.getName();
        return userService.getUserByUsername(username);
    }
    
    /**
     * 顯示商家商品列表
     */
    @GetMapping
    public String listProducts(Model model, Authentication authentication) {
        try {
            User currentUser = getCurrentUser(authentication);
            List<Product> products = productService.getProductsByMerchant(currentUser.getId());
            
            model.addAttribute("products", products);
            model.addAttribute("currentUser", currentUser);
            
            return "merchant/product-list";
        } catch (Exception e) {
            model.addAttribute("errorMessage", "無法載入商品列表: " + e.getMessage());
            model.addAttribute("products", java.util.Collections.emptyList());
            return "merchant/product-list";
        }
    }
    
    /**
     * 顯示新增商品表單
     */
    @GetMapping("/new")
    public String showCreateForm(Model model) {
        model.addAttribute("product", new Product());
        model.addAttribute("categories", categoryRepository.findAll());
        return "merchant/product-form";
    }
    
    /**
     * 處理新增商品
     */
    @PostMapping
    public String createProduct(
            @ModelAttribute Product product,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) MultipartFile imageFile,
            Authentication authentication,
            RedirectAttributes redirectAttributes) {
        try {
            User currentUser = getCurrentUser(authentication);
            
            // Handle image upload
            if (imageFile != null && !imageFile.isEmpty()) {
                String imageUrl = saveProductImage(imageFile);
                product.setImageUrl(imageUrl);
            }
            
            productService.createMerchantProduct(product, categoryId, currentUser.getId());
            redirectAttributes.addFlashAttribute("successMessage", "商品創建成功！");
            return "redirect:/merchant/products";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "創建商品失敗: " + e.getMessage());
            return "redirect:/merchant/products/new";
        }
    }
    
    /**
     * 顯示編輯商品表單
     */
    @GetMapping("/{id}/edit")
    public String showEditForm(
            @PathVariable Long id,
            Model model,
            Authentication authentication,
            RedirectAttributes redirectAttributes) {
        try {
            User currentUser = getCurrentUser(authentication);
            
            // 驗證商品所有權
            if (!productService.isProductOwnedByUser(id, currentUser.getId())) {
                redirectAttributes.addFlashAttribute("errorMessage", "您沒有權限編輯此商品");
                return "redirect:/merchant/products";
            }
            
            Product product = productService.getProductById(id);
            model.addAttribute("product", product);
            model.addAttribute("categories", categoryRepository.findAll());
            
            return "merchant/product-form";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "商品不存在");
            return "redirect:/merchant/products";
        }
    }
    
    /**
     * 處理更新商品
     */
    @PostMapping("/{id}")
    public String updateProduct(
            @PathVariable Long id,
            @ModelAttribute Product product,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) MultipartFile imageFile,
            Authentication authentication,
            RedirectAttributes redirectAttributes) {
        try {
            User currentUser = getCurrentUser(authentication);
            
            System.out.println("=== 更新商品開始 ===");
            System.out.println("imageFile is null: " + (imageFile == null));
            if (imageFile != null) {
                System.out.println("imageFile isEmpty: " + imageFile.isEmpty());
                System.out.println("imageFile name: " + imageFile.getOriginalFilename());}
            
            // Handle image upload
            if (imageFile != null && !imageFile.isEmpty()) {
                try {
                    String imageUrl = saveProductImage(imageFile);
                    product.setImageUrl(imageUrl);
                    System.out.println("圖片上傳成功,URL: " + imageUrl);
                } catch (Exception e) {
                    System.err.println("圖片上傳失敗: " + e.getMessage());
                    e.printStackTrace();
                    redirectAttributes.addFlashAttribute("errorMessage", "圖片上傳失敗: " + e.getMessage());
                    return "redirect:/merchant/products/" + id + "/edit";
                }
            }
            
            productService.updateMerchantProduct(id, product, categoryId, currentUser.getId());
            
            redirectAttributes.addFlashAttribute("successMessage", "商品更新成功！");
            System.out.println("=== 更新商品完成 ===");
            return "redirect:/merchant/products";
        } catch (SecurityException e) {
            System.err.println("權限錯誤: " + e.getMessage());
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/merchant/products";
        } catch (Exception e) {
            System.err.println("更新商品失敗: " + e.getMessage());
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("errorMessage", "更新商品失敗: " + e.getMessage());
            return "redirect:/merchant/products/" + id + "/edit";
        }
    }
    
    /**
     * 刪除商品
     */
    @PostMapping("/{id}/delete")
    public String deleteProduct(
            @PathVariable Long id,
            Authentication authentication,
            RedirectAttributes redirectAttributes) {
        try {
            User currentUser = getCurrentUser(authentication);
            productService.deleteMerchantProduct(id, currentUser.getId());
            
            redirectAttributes.addFlashAttribute("successMessage", "商品刪除成功！");
        } catch (SecurityException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "刪除商品失敗: " + e.getMessage());
        }
        
        return "redirect:/merchant/products";
    }
    
    /**
     * 切換商品狀態（上架/下架）
     */
    @PostMapping("/{id}/toggle-status")
    public String toggleProductStatus(
            @PathVariable Long id,
            Authentication authentication,
            RedirectAttributes redirectAttributes) {
        try {
            User currentUser = getCurrentUser(authentication);
            // 驗證商品所有權
            if (!productService.isProductOwnedByUser(id, currentUser.getId())) {
                redirectAttributes.addFlashAttribute("errorMessage", "您沒有權限操作此商品");
                return "redirect:/merchant/products";
            }
            
            Product product = productService.getProductById(id);
            
            // 切換狀態
            if (product.getStatus() == Product.ProductStatus.ACTIVE) {
                product.setStatus(Product.ProductStatus.INACTIVE);
            } else if (product.getStatus() == Product.ProductStatus.INACTIVE) {
                product.setStatus(Product.ProductStatus.ACTIVE);
            }
            
            productService.updateProduct(id, product, null);
            redirectAttributes.addFlashAttribute("successMessage", "商品狀態更新成功！");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "操作失敗: " + e.getMessage());
        }
        
        return "redirect:/merchant/products";
    }
    
    /**
     * Save uploaded product image to static/images/products directory
     * @param file The uploaded image file
     * @return The relative URL path to access the image
     */
    private String saveProductImage(MultipartFile file) throws IOException {
        System.out.println("=== saveProductImage 開始 ===");
        
        // Validate file
        if (file.isEmpty()) {
            throw new IllegalArgumentException("檔案為空");
        }
        
        // Validate file type
        String contentType = file.getContentType();
        System.out.println("Content Type: " + contentType);
        if (contentType == null || !contentType.startsWith("image/")) {
            throw new IllegalArgumentException("檔案必須是圖片格式");
        }
        
        // Validate file size (max 5MB)
        long maxSize = 5 * 1024 * 1024; // 5MB
        System.out.println("File size: " + file.getSize() + " bytes");
        if (file.getSize() > maxSize) {
            throw new IllegalArgumentException("檔案大小不能超過 5MB");
        }
        
        // Get original filename and extension
        String originalFilename = file.getOriginalFilename();
        System.out.println("Original filename: " + originalFilename);
        if (originalFilename == null || originalFilename.isEmpty()) {
            throw new IllegalArgumentException("無效的檔案名稱");
        }
        
        String extension = "";
        int dotIndex = originalFilename.lastIndexOf('.');
        if (dotIndex > 0) {
            extension = originalFilename.substring(dotIndex);
        }
        System.out.println("File extension: " + extension);
        
        // Generate unique filename
        String filename = UUID.randomUUID().toString() + extension;
        System.out.println("Generated filename: " + filename);
        
        // Determine upload directory
        System.out.println("Upload path from config: " + uploadPath);
        Path uploadDir = Paths.get(uploadPath).toAbsolutePath();
        System.out.println("Absolute upload directory: " + uploadDir);
        System.out.println("Directory exists: " + Files.exists(uploadDir));
        System.out.println("Directory is writable: " + Files.isWritable(uploadDir.getParent()));
        
        // Create directory if it doesn't exist
        if (!Files.exists(uploadDir)) {
            System.out.println("Creating directory: " + uploadDir);
            Files.createDirectories(uploadDir);
            System.out.println("Directory created successfully");
        }
        
        // Save file
        Path filePath = uploadDir.resolve(filename);
        System.out.println("Full file path: " + filePath);
        try {
            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
            System.out.println("File copied successfully");
        } catch (IOException e) {
            System.err.println("Failed to copy file: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
        
        // Verify file was created
        boolean fileExists = Files.exists(filePath);
        long fileSize = fileExists ? Files.size(filePath) : 0;
        System.out.println("File exists after save: " + fileExists);
        System.out.println("File size after save: " + fileSize + " bytes");
        
        // 只返回文件名，不包含路徑前綴
        // 模板會添加 /images/products/ 前綴
        System.out.println("Image filename: " + filename);
        System.out.println("=== saveProductImage 完成 ===");
        
        // Return 只文件名
        return filename;
    }
}