package Netty.Message;

// 消息类型枚举
public enum MessageType {
    PACKET_INFO(1),
    FEATURE_DATA(2),
    ALERT(3),
    COMMAND(4);

    private final byte value;

    MessageType(int value) {
        this.value = (byte) value;
    }

    public byte getValue() {
        return value;
    }

    public static MessageType fromValue(byte value) {
        for (MessageType type : values()) {
            if (type.value == value) {
                return type;
            }
        }
        throw new IllegalArgumentException("Unknown message type: " + value);
    }
}