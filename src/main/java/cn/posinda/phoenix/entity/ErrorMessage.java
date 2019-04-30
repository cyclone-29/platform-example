package cn.posinda.phoenix.entity;

import cn.posinda.phoenix.base.PhoenixDataEntity;
import lombok.Getter;
import lombok.Setter;

/**
 * 错误信息统计
 */
@Getter
@Setter
public class ErrorMessage extends PhoenixDataEntity {

    private static final long serialVersionUID = 4229384918705767526L;

    private String occursInstant;

    private String message;

    private String url;
}
