package com.example.quanxiankongzhi.kb.service.impl;
import com.example.quanxiankongzhi.kb.dto.DocumentVo;
import com.example.quanxiankongzhi.kb.entity.Document;
import com.example.quanxiankongzhi.kb.mapper.DocumentMapper;
import com.example.quanxiankongzhi.kb.service.DocumentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import org.apache.tika.Tika;
import com.example.quanxiankongzhi.kb.service.ChunkingService;
import com.example.quanxiankongzhi.kb.service.FullTextSearchService;
@Slf4j
@Service
@RequiredArgsConstructor
public class DocumentServiceImpl implements DocumentService{
    private final ChunkingService chunkingService;
    private final DocumentMapper documentMapper;
    private final FullTextSearchService fullTextSearchService;
    @Value("${file.upload-dir:./uploads}")
    private String uploadDir;
    @Override
    public DocumentVo upload(MultipartFile file, Long kbId) throws IOException{
        // ① 获取原始文件名
        String originalName = file.getOriginalFilename();
        // ② 提取文件类型（后缀）
        String fileType = "";
        if (originalName != null && originalName.contains(".")){
            fileType = originalName.substring(originalName.lastIndexOf(".") + 1);
        }
        // ③ 生成唯一文件名（防止重名覆盖）
        String storedName = UUID.randomUUID().toString() + "." + fileType;
        // ④ 确保上传目录存在
        Path uploadPath = Paths.get(uploadDir);
        if (!Files.exists(uploadPath)){
            Files.createDirectories(uploadPath);
        }
        // ⑤ 保存文件到磁盘
        Path filePath = uploadPath.resolve(storedName);
        Files.copy(file.getInputStream(), filePath);
        // ⑥ 提取文件内容
        String content = "";
        try{
            Tika tika = new Tika();
            content = tika.parseToString(filePath.toFile());
            log.info("文本提取成功，文件: {}, 字符数: {}", originalName, content.length());
        }catch (Exception e){
            log.warn("文本提取失败: {}, 原因: {}", originalName, e.getMessage());
        }
        // ⑥ 写数据库记录
        Document doc = new Document();
        doc.setKbId(kbId);
        doc.setName(originalName);
        doc.setFileType(fileType);
        doc.setFileSize(file.getSize());
        doc.setFilePath(filePath.toString());
        doc.setStatus(1);
        doc.setCreateTime(LocalDateTime.now());
        doc.setUpdateTime(LocalDateTime.now());
        doc.setContent(content);
        documentMapper.insert(doc);
        // ⑦ 文本分块
        if (content != null && !content.isEmpty()) {
            chunkingService.chunkAndSave(content, doc.getId(), kbId);
        }
        return toVo(doc);

    }
    @Override
    public List<DocumentVo> listByKbId(Long kbId){
        List<Document> docs = documentMapper.selectList(
                new LambdaQueryWrapper<Document>()
                        .eq(Document::getKbId, kbId)
        );
        return docs.stream()
                .map(this::toVo)
                .collect(Collectors.toList());
    }
    @Override
    public void delete(Long id){
        Document doc = documentMapper.selectById(id);
        if (doc == null){
            throw new RuntimeException("文档不存在");
        }
        // ① 删除磁盘文件
        try{
            Files.deleteIfExists(Paths.get(doc.getFilePath()));
        }catch (IOException e){
            log.warn("删除文件失败: {}", doc.getFilePath());
        }
        // ② 删除数据库记录
        documentMapper.deleteById(id);
        fullTextSearchService.deleteByDocId(id);
    }
    private DocumentVo toVo(Document doc){
        DocumentVo vo = new DocumentVo();
        vo.setId(doc.getId());
        vo.setKbId(doc.getKbId());
        vo.setName(doc.getName());
        vo.setFileType(doc.getFileType());
        vo.setFileSize(doc.getFileSize());
        vo.setFilePath(doc.getFilePath());
        vo.setStatus(doc.getStatus());
        vo.setCreateTime(doc.getCreateTime());
        vo.setUpdateTime(doc.getUpdateTime());
        return vo;
    }
}
