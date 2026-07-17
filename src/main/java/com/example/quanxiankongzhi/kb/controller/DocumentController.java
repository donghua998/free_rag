package com.example.quanxiankongzhi.kb.controller;
import com.example.quanxiankongzhi.auth.annotation.RequirePermission;
import com.example.quanxiankongzhi.common.result.Result;
import com.example.quanxiankongzhi.kb.dto.DocumentVo;
import com.example.quanxiankongzhi.kb.service.DocumentService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.io.IOException;
@RestController
@RequestMapping("/kb/document")
@RequiredArgsConstructor
public class DocumentController {
    private final DocumentService documentService;
    /**
     * 上传文档
     */
    @RequirePermission("kb:doc:upload")
    @PostMapping("/upload")
    public Result<DocumentVo> upload(
            @RequestParam("file") MultipartFile file,
            @RequestParam("kbId") Long kbId
    )throws IOException{
        DocumentVo vo = documentService.upload(file, kbId);
        return Result.success(vo);
    }
    /**
     * 查询某知识库下的文档列表
     */
    @GetMapping("/list")
    public Result<List<DocumentVo>> list(@RequestParam("kbId") Long kbId){
        List<DocumentVo> list = documentService.listByKbId(kbId);
        return Result.success(list);
    }
    /**
     * 删除文档
     */
    @RequirePermission("kb:doc:delete")
    @DeleteMapping("/delete/{id}")
    public Result<Void> delete(@PathVariable Long id){
        documentService.delete(id);
        return Result.success();
    }
}
