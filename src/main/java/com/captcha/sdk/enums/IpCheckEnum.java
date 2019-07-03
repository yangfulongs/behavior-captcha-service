package com.captcha.sdk.enums;

import lombok.Getter;

@Getter
public enum IpCheckEnum {
    WHITE_LIST("白名单", 0),
    BLACK_LIST("黑名单", 1);

    private String desc;
    private Integer value;

    IpCheckEnum(String desc, Integer value){
        this.desc = desc;
        this.value = value;
    }
}
