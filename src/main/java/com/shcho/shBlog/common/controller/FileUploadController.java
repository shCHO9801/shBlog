package com.shcho.shBlog.common.controller;

import com.shcho.shBlog.auth.CustomUserDetails;
import com.shcho.shBlog.common.dto.FileUploadResponseDto;
import com.shcho.shBlog.common.service.S3Service;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/files")
public class FileUploadController {

    private final S3Service s3Service;

    @PostMapping("/upload")
    public ResponseEntity<FileUploadResponseDto> uploadFile(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestParam("file") MultipartFile file,
            @RequestParam("type") String type
    ) {
        String username = userDetails.getUsername();
        return ResponseEntity.ok(s3Service.uploadByType(file, type, username));
    }
}
