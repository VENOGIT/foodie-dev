package com.imooc.controller;

import com.imooc.pojo.Items;
import com.imooc.pojo.ItemsImg;
import com.imooc.pojo.ItemsParam;
import com.imooc.pojo.ItemsSpec;
import com.imooc.pojo.vo.CommentLevelCountsVO;
import com.imooc.pojo.vo.ItemInfoVO;
import com.imooc.service.ItemService;
import com.imooc.utils.IMOOCJSONResult;
import com.imooc.utils.PagedGridResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author :Administrator
 * @path :ItemsController
 * @date :2023-06-27 22:13:34
 * @describe :class
 */
@Api(value = "商品接口", tags = {"商品信息展示的相关接口"})
@RestController
@RequestMapping(value = "items")
public class ItemsController extends BaseController {
    @Autowired
    private ItemService itemService;

    @ApiOperation(value = "查询商品详情", notes = "查询商品详情.", httpMethod = "GET")
    @GetMapping(value = "/info/{itemId}")
    public IMOOCJSONResult subCat(@ApiParam(name = "itemId", value = "商品id", required = true) @PathVariable String itemId) {
        if (StringUtils.isBlank(itemId)) {
            return IMOOCJSONResult.errorMsg(null);
        }
        Items item = itemService.queryItemById(itemId);
        List<ItemsImg> itemImgList = itemService.queryItemImgList(itemId);
        ItemsParam itemParam = itemService.queryItemParam(itemId);
        List<ItemsSpec> itemsSpecs = itemService.queryItemSpecList(itemId);
        ItemInfoVO vo = new ItemInfoVO();
        vo.setItem(item);
        vo.setItemImgList(itemImgList);
        vo.setItemParams(itemParam);
        vo.setItemSpecList(itemsSpecs);
        // 3 请求成功，用户名没要 重复
        return IMOOCJSONResult.ok(vo);
    }


    @ApiOperation(value = "查询商品评价等级", notes = "查询商品评价等级.", httpMethod = "GET")
    @GetMapping(value = "/commentLevel")
    public IMOOCJSONResult commentLevel(
            @ApiParam(name = "itemId", value = "商品id", required = true) @RequestParam String itemId) {
        if (StringUtils.isBlank(itemId)) {
            return IMOOCJSONResult.errorMsg(null);
        }
        CommentLevelCountsVO item = itemService.queryCommentCounts(itemId);
        // 3 请求成功，用户名没要 重复
        return IMOOCJSONResult.ok(item);
    }

    @ApiOperation(value = "查询商品评论", notes = "查询商品评论.", httpMethod = "GET")
    @GetMapping(value = "/comments")
    public IMOOCJSONResult comments(
            @ApiParam(name = "itemId", value = "商品id", required = true) @RequestParam String itemId,
            @ApiParam(name = "level", value = "评价等级") @RequestParam Integer level,
            @ApiParam(name = "page", value = "查询下一页的第几页") @RequestParam Integer page,
            @ApiParam(name = "pageSize", value = "分页的每一页显示的条数") @RequestParam Integer pageSize) {
        if (StringUtils.isBlank(itemId)) {
            return IMOOCJSONResult.errorMsg(null);
        }

        if (page == null) {
            page = 1;
        }

        if (pageSize == null) {
            pageSize = COMMENT_PAGE_SIZE;
        }

        PagedGridResult item = itemService.queryPageComments(itemId, level, page, pageSize);
        // 3 请求成功，用户名没要 重复
        return IMOOCJSONResult.ok(item);
    }
}
