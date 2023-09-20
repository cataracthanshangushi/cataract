package com.taitan.system.service;

import com.taitan.system.pojo.vo.FileInfoVO;
import org.springframework.web.multipart.MultipartFile;

public interface PictureService {
    FileInfoVO uploadPicture(MultipartFile file);
}
