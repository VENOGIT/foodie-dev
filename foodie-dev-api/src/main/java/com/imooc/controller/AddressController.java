package com.imooc.controller;

import com.imooc.pojo.UserAddress;
import com.imooc.pojo.bo.AddressBO;
import com.imooc.service.AddressService;
import com.imooc.utils.IMOOCJSONResult;
import com.imooc.utils.MobileEmailUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author :Administrator
 * @path :HelloController
 * @date :2023-06-11 14:14:38
 * @describe :class
 */
@Api(value = "地址相关", tags = {"地址相关的api接口"})
@RestController
@RequestMapping(value = {"address"})
public class AddressController {

    @Autowired
    private AddressService addressService;

    /**
     * 用户在确认订单页面，可以针对收货地址做如下操作：
     * 1. 查询用户的所有收货地址列表
     * 2. 新增收货地址
     * 3. 删除收货地址
     * 4. 修改收货地址
     * 5. 设置默认地址
     */

    @ApiOperation(value = "根据用户id查询收货地址列表 ", notes = "根据用户id查询收货地址列表.", httpMethod = "POST")
    @PostMapping(value = "/list")
    public IMOOCJSONResult list(
            @ApiParam(name = "list", value = "根据用户id查询收货地址列表", required = true)
            @RequestParam String userId) {

        if (StringUtils.isBlank(userId)) {
            return IMOOCJSONResult.errorMsg("");
        }

        List<UserAddress> userAddresses = addressService.queryAll(userId);

        return IMOOCJSONResult.ok(userAddresses);
    }


    @ApiOperation(value = "用户新增地址 ", notes = "用户新增地址.", httpMethod = "POST")
    @PostMapping(value = "/add")
    public IMOOCJSONResult add(
            @ApiParam(name = "add", value = "用户新增地址", required = true)
            @RequestBody AddressBO addressBO) {
        IMOOCJSONResult imoocjsonResult = checkAddress(addressBO);
        if (imoocjsonResult.getStatus() != HttpStatus.OK.value()) {
            return imoocjsonResult;
        }
        addressService.addNewUserAddress(addressBO);
        return IMOOCJSONResult.ok();
    }

    private IMOOCJSONResult checkAddress(AddressBO addressBO) {
        String receiver = addressBO.getReceiver();
        if (StringUtils.isBlank(receiver)) {
            return IMOOCJSONResult.errorMsg("收货人不能为空");
        }
        if (receiver.length() > 12) {
            return IMOOCJSONResult.errorMsg("收货人姓名不能太长");
        }

        String mobile = addressBO.getMobile();
        if (StringUtils.isBlank(mobile)) {
            return IMOOCJSONResult.errorMsg("收货人手机号不能为空");
        }
        if (mobile.length() != 11) {
            return IMOOCJSONResult.errorMsg("收货人手机号长度不正确");
        }
        boolean isMobileOk = MobileEmailUtils.checkMobileIsOk(mobile);
        if (!isMobileOk) {
            return IMOOCJSONResult.errorMsg("收货人手机号码格式不正确");
        }
        String province = addressBO.getProvince();
        String city = addressBO.getCity();
        String district = addressBO.getDistrict();
        String detail = addressBO.getDetail();
        if (StringUtils.isBlank(province) ||
                StringUtils.isBlank(city) ||
                StringUtils.isBlank(district) ||
                StringUtils.isBlank(detail)
        ) {
            return IMOOCJSONResult.errorMsg("收货地址信息不能为空");
        }
        return IMOOCJSONResult.ok();
    }

    @ApiOperation(value = "用户修改地址 ", notes = "用户修改地址.", httpMethod = "POST")
    @PostMapping(value = "/update")
    public IMOOCJSONResult update(
            @ApiParam(name = "add", value = "用户修改地址", required = true)
            @RequestBody AddressBO addressBO) {
        if (StringUtils.isBlank(addressBO.getAddressId())) {
            return IMOOCJSONResult.errorMsg("修改地址错误：addressId不能为空");
        }
        IMOOCJSONResult imoocjsonResult = checkAddress(addressBO);
        if (imoocjsonResult.getStatus() != HttpStatus.OK.value()) {
            return imoocjsonResult;
        }
        addressService.updateUserAddress(addressBO);
        return IMOOCJSONResult.ok();
    }


    @ApiOperation(value = "用户删除地址 ", notes = "用户删除地址.", httpMethod = "POST")
    @PostMapping(value = "/delete")
    public IMOOCJSONResult delete(
            @ApiParam(name = "add", value = "用户删除地址", required = true)
            @RequestParam String userId,
            @RequestParam String addressId) {
        if (StringUtils.isBlank(userId) || StringUtils.isBlank(addressId)) {
            return IMOOCJSONResult.errorMsg("");
        }

        addressService.deleteUserAddress(userId, addressId);
        return IMOOCJSONResult.ok();
    }
    @ApiOperation(value = "用户设置默认地址 ", notes = "用户设置默认地址.", httpMethod = "POST")
    @PostMapping(value = "/setDefalut")
    public IMOOCJSONResult setDefault(
            @ApiParam(name = "add", value = "用户设置默认地址", required = true)
            @RequestParam String userId,
            @RequestParam String addressId) {

        if (StringUtils.isBlank(userId) || StringUtils.isBlank(addressId)) {
            return IMOOCJSONResult.errorMsg("");
        }

        addressService.updateUserAddressToBeDefault(userId, addressId);
        return IMOOCJSONResult.ok();
    }

}
