package com.taitan.system.controller;

import com.taitan.system.common.result.Result;
import com.taitan.system.pojo.vo.FileInfoVO;
import com.taitan.system.service.PictureService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Tag(name = "07.文件接口")
@RestController
@RequestMapping("/api/v1/picture")
@RequiredArgsConstructor
public class PictureController {

    private final PictureService pictureService;

    @PostMapping
    @Operation(summary = "图片上传", security = {@SecurityRequirement(name = "Authorization")})
    public Result<FileInfoVO> uploadPicture(
            @Parameter(description ="图片文件对象") @RequestParam(value = "file") MultipartFile file
    ) {
        FileInfoVO fileInfoVO = pictureService.uploadPicture(file);
        return Result.success(fileInfoVO);
    }

}
