package com.shcho.shBlog.common.dto;

public record FileUploadResponseDto (
        String fileUrl
){
    public static FileUploadResponseDto from(String fileUrl){
        return new FileUploadResponseDto(fileUrl);
    }
}
