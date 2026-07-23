package com.example.quanxiankongzhi.kb.controller;
import com.example.quanxiankongzhi.auth.annotation.RequirePermission;
import com.example.quanxiankongzhi.common.result.Result;
import com.example.quanxiankongzhi.kb.dto.KnowledgeBaseDTO;
import com.example.quanxiankongzhi.kb.dto.KnowledgeBaseVo;
import com.example.quanxiankongzhi.kb.service.KnowledgeBaseService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
@RestController
@RequestMapping("/kb")
@RequiredArgsConstructor
public class KnowledgeBaseController {
    private final KnowledgeBaseService knowledgeBaseService;
    /**
     * 创建知识库
     */
    @RequirePermission("kb:add")
    @PostMapping("/create")
    public Result<KnowledgeBaseVo> create(
            @RequestBody KnowledgeBaseDTO dto,
            @RequestAttribute("userId") Long userId
    ){
        KnowledgeBaseVo vo = knowledgeBaseService.create(dto, userId);
        return Result.success(vo);
    }
    /**
     * 查询知识库列表
     */
    @GetMapping("/list")
    public Result<List<KnowledgeBaseVo>> list(@RequestAttribute("userId") Long userId){
        List<KnowledgeBaseVo> list = knowledgeBaseService.list(userId);
        return Result.success(list);
    }
    /**
     * 修改知识库
     */
    @RequirePermission("kb:edit")
    @PutMapping("/update/{id}")
    public Result<KnowledgeBaseVo> update(
            @PathVariable Long id,
            @RequestBody KnowledgeBaseDTO dto,
            @RequestAttribute("userId") Long userId
    ){
        KnowledgeBaseVo vo = knowledgeBaseService.update(id, dto, userId);
        return Result.success(vo);
    }
    /**
     * 删除知识库
     */
    @RequirePermission("kb:delete")
    @DeleteMapping("/delete/{id}")
    public Result<Void> delete(@PathVariable Long id,@RequestAttribute("userId") Long userId){
        knowledgeBaseService.delete(id, userId);
        return Result.success();
    }
}
