package name.auh.bus.common;

import lombok.Data;

@Data
public class Ret<T> {

    /**
     * 第一位表示错误类型  第二-4位标识 业务类型  最后3位代表具体业务的具体错误
     * 业务码  000000 代表成功     1xxxxxx 以1开头代表请求参数有问题  2xxxxxx 以2开头代表业务处理异常
     */
    private String code;

    /**
     * 返回的消息
     */
    private String msg;

    /**
     * 业务数据
     */
    private T data;

    public static Ret buildSuc() {
        Ret ret = new Ret<>();
        ret.setCode("000000");
        return ret;
    }

    public static <T> Ret<T> buildSuc(T data, String... msg) {
        Ret ret = new Ret<>();
        ret.setCode("000000");
        if (msg != null && msg.length == 1) {
            ret.setMsg(msg[0]);
        }
        ret.setData(data);
        return ret;
    }

    public static Ret buildFailParam(String msg, Object... data) {
        Ret ret = new Ret<>();
        ret.setCode("100000");
        ret.setMsg(msg);
        if (data != null && data.length == 1) {
            ret.setData(data[0]);
        }
        return ret;
    }

    public static Ret buildFail(String code, String msg, Object... data) {
        Ret ret = new Ret<>();
        ret.setCode(code);
        ret.setMsg(msg);
        if (data != null && data.length == 1) {
            ret.setData(data[0]);
        }
        return ret;
    }

}
