package enums;

public enum ViddeoStatusEnum {

    SUCCESS(1), //发布成功
    FORBID(2);  //禁止播放，管理员操作

    public final int value;

    ViddeoStatusEnum(int value){
        this.value = value;
    }


    public int getValue() {
        return value;
    }
}
