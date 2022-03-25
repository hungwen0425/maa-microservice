package com.atguigu.yygh.cmn.api;

import com.atguigu.yygh.cmn.service.DictService;
import com.atguigu.yygh.common.result.Result;
import com.atguigu.yygh.model.cmn.Dict;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author qy
 */
@Api(tags = "数据字典管理")
@RestController
@RequestMapping(value = "/api/cmn/dict")
public class DictApiController {

    @Autowired
    private DictService dictService;

    @ApiOperation(value = "根据dictCode获取下级节点")
    @GetMapping(value = "/findByDictCode/{dictCode}")
    public Result<List<Dict>> findByDictCode(
            @ApiParam(name = "dictCode", value = "节点编码", required = true)
            @PathVariable String dictCode) {
        List<Dict> list = dictService.findByDictCode(dictCode);
        return Result.ok(list);
    }

    @ApiOperation(value = "根据上级id获取子节点数据列表")
    @GetMapping(value = "findByParentId/{parentId}")
    public Result<List<Dict>> findByParentId(
            @ApiParam(name = "parentId", value = "上级节点id", required = true)
            @PathVariable Long parentId) {
        List<Dict> list = dictService.findByParentId(parentId);
        return Result.ok(list);
    }
}

