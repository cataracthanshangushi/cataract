package com.taitan.system.service.impl;

import com.taitan.system.pojo.vo.FileInfoVO;
import com.taitan.system.service.PictureService;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;

@Service
@RequiredArgsConstructor
public class PictureServiceImpl implements PictureService {
    @SneakyThrows
    @Override
    public FileInfoVO uploadPicture(MultipartFile file) {
        String filename = file.getOriginalFilename();
        file.transferTo(new File("E:\\picture\\"+filename));
        return null;
    }
}
