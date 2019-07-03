package com.captcha.sdk.enums;

import lombok.Getter;

@Getter
public enum SwitchEnum {
    OFF("关", 0),
    ON("开", 1);

    private String desc;
    private Integer value;

    SwitchEnum(String desc,Integer value){
        this.desc = desc;
        this.value = value;
    }
}
