package com.taitan.system.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.taitan.system.common.result.PageResult;
import com.taitan.system.common.result.Result;
import com.taitan.system.pojo.form.DictForm;
import com.taitan.system.pojo.form.DictTypeForm;
import com.taitan.system.pojo.query.DictPageQuery;
import com.taitan.system.pojo.query.DictTypePageQuery;
import com.taitan.system.pojo.vo.DictPageVO;
import com.taitan.system.pojo.vo.DictTypePageVO;
import com.taitan.system.pojo.vo.Option;
import com.taitan.system.service.SysDictService;
import com.taitan.system.service.SysDictTypeService;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "06.字典接口")
@RestController
@RequestMapping("/api/v1/dict")
@RequiredArgsConstructor
public class SysDictController {

    private final SysDictService dictService;

    private final SysDictTypeService dictTypeService;

    @Operation(summary = "字典分页列表", security = {@SecurityRequirement(name = "Authorization")})
    @GetMapping("/page")
    public PageResult<DictPageVO> getDictPage(
            @ParameterObject DictPageQuery queryParams
    ) {
        Page<DictPageVO> result = dictService.getDictPage(queryParams);
        return PageResult.success(result);
    }

    @Operation(summary = "字典数据表单数据", security = {@SecurityRequirement(name = "Authorization")})
    @GetMapping("/{id}/form")
    public Result<DictForm> getDictForm(
            @Parameter(description ="字典ID") @PathVariable Long id
    ) {
        DictForm formData = dictService.getDictForm(id);
        return Result.success(formData);
    }

    @Operation(summary = "新增字典", security = {@SecurityRequirement(name = "Authorization")})
    @PostMapping
    public Result saveDict(
            @RequestBody DictForm DictForm
    ) {
        boolean result = dictService.saveDict(DictForm);
        return Result.judge(result);
    }

    @Operation(summary = "修改字典", security = {@SecurityRequirement(name = "Authorization")})
    @PutMapping("/{id}")
    public Result updateDict(
            @PathVariable Long id,
            @RequestBody DictForm DictForm
    ) {
        boolean status = dictService.updateDict(id, DictForm);
        return Result.judge(status);
    }

    @Operation(summary = "删除字典", security = {@SecurityRequirement(name = "Authorization")})
    @DeleteMapping("/{ids}")
    public Result deleteDict(
            @Parameter(description ="字典ID，多个以英文逗号(,)拼接") @PathVariable String ids
    ) {
        boolean result = dictService.deleteDict(ids);
        return Result.judge(result);
    }


    @Operation(summary = "字典下拉列表", security = {@SecurityRequirement(name = "Authorization")})
    @GetMapping("/options")
    public Result<List<Option>> listDictOptions(
            @Parameter(description ="字典类型编码") @RequestParam String typeCode
    ) {
        List<Option> list = dictService.listDictOptions(typeCode);
        return Result.success(list);
    }


    /*----------------------------------------------------*/
    @Operation(summary = "字典类型分页列表", security = {@SecurityRequirement(name = "Authorization")})
    @GetMapping("/types/page")
    public PageResult<DictTypePageVO> getDictTypePage(
            @ParameterObject DictTypePageQuery queryParams
    ) {
        Page<DictTypePageVO> result = dictTypeService.getDictTypePage(queryParams);
        return PageResult.success(result);
    }

    @Operation(summary = "字典类型表单数据", security = {@SecurityRequirement(name = "Authorization")})
    @GetMapping("/types/{id}/form")
    public Result<DictTypeForm> getDictTypeForm(
            @Parameter(description ="字典ID") @PathVariable Long id
    ) {
        DictTypeForm dictTypeForm = dictTypeService.getDictTypeForm(id);
        return Result.success(dictTypeForm);
    }

    @Operation(summary = "新增字典类型", security = {@SecurityRequirement(name = "Authorization")})
    @PostMapping("/types")
    public Result saveDictType(@RequestBody DictTypeForm dictTypeForm) {
        boolean result = dictTypeService.saveDictType(dictTypeForm);
        return Result.judge(result);
    }

    @Operation(summary = "修改字典类型", security = {@SecurityRequirement(name = "Authorization")})
    @PutMapping("/types/{id}")
    public Result updateDictType(@PathVariable Long id, @RequestBody DictTypeForm dictTypeForm) {
        boolean status = dictTypeService.updateDictType(id, dictTypeForm);
        return Result.judge(status);
    }

    @Operation(summary = "删除字典类型", security = {@SecurityRequirement(name = "Authorization")})
    @DeleteMapping("/types/{ids}")
    public Result deleteDictTypes(
            @Parameter(description ="字典类型ID，多个以英文逗号(,)分割") @PathVariable String ids
    ) {
        boolean result = dictTypeService.deleteDictTypes(ids);
        return Result.judge(result);
    }

}
