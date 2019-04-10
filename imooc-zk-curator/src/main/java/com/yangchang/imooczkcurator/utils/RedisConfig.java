package com.yangchang.imooczkcurator.utils;

import lombok.Data;

/**
 * RedisConfig
 *
 * @author yangchang
 * @date 2019/1/1
 */
@Data
public class RedisConfig {
    /**
     * add 新增配置
     * update 更新配置
     * delete 删除配置
     */
    private String type;

    /**
     * 如果是add或者update，则提供下载地址
     */
    private String url;

    /**
     * 备注
     */
    private String remark;
}
