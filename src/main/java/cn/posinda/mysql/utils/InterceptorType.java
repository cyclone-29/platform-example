package cn.posinda.mysql.utils;

import cn.posinda.phoenix.tools.ArgumentValidation;
import scala.MatchError;

public enum InterceptorType {

    ALL_REPLACE_BY_STAR(0, "全部内容替换为*"), REPLACE_BY_START_LEAVING_FOUR(1, "头部尾部各保留2个字符,其余部分以*代替"),
    STRING_ENCODING(2, "字符串编码");

    private int index;

    private String value;

    InterceptorType(int index, String value) {
        this.index = index;
        this.value = value;
    }

    @SuppressWarnings("unused")
    public static InterceptorType fromIndex(int index) {
        switch (index) {
            case 0:
                return ALL_REPLACE_BY_STAR;
            case 1:
                return REPLACE_BY_START_LEAVING_FOUR;
            case 2:
                return STRING_ENCODING;
            default:
                throw new MatchError("参数错误,匹配异常");
        }
    }

    @SuppressWarnings("unused")
    public static InterceptorType fromValue(String value) {
        ArgumentValidation.checkNotEmpty(new String[]{value});
        switch (value) {
            case "全部替换为*":
                return ALL_REPLACE_BY_STAR;
            case "头部尾部各保留2个字符,其余部分以*代替":
                return REPLACE_BY_START_LEAVING_FOUR;
            case "字符串编码":
                return STRING_ENCODING;
            default:
                throw new MatchError("参数错误，匹配异常");

        }
    }

    @Override
    public String toString() {
        return this.index + ("->").concat(this.value);
    }

    public String value() {
        return this.value;
    }

    public int index() {
        return this.index;
    }

}
