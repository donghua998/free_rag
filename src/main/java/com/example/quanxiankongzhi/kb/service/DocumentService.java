package com.example.quanxiankongzhi.kb.service;
import com.example.quanxiankongzhi.kb.dto.DocumentVo;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;

import java.util.List;
public interface DocumentService {
    DocumentVo upload(MultipartFile file, Long kbId) throws IOException;
    List<DocumentVo> listByKbId(Long kbId);
    void delete(Long id);
}
