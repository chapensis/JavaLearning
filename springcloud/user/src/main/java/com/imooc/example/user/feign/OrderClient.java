package com.imooc.example.user.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * 利用Feign进行服务调用，不需要再自己手动写post请求
 * value是服务的名字
 * path是访问服务的路径
 */
@FeignClient(value = "order", path = "/api/order")
public interface OrderClient {

    @GetMapping("")
    String getMyOrders();
}
