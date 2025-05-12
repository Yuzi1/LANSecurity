package Netty.utils;

import Netty.Message.Message;
import Netty.Message.MessageType;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.List;

public class PacketDecoder extends ByteToMessageDecoder {
    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        if (in.readableBytes() < 1) {
            return;
        }

        // 标记当前读取位置
        in.markReaderIndex();

        // 读取消息类型
        byte typeValue = in.readByte();
        MessageType type = MessageType.fromValue(typeValue);

        // 读取数据
        byte[] data = new byte[in.readableBytes()];
        in.readBytes(data);

        // 反序列化对象
        Object obj = deserialize(data);

        // 生成消息对象
        Message msg = new Message(type, obj);
        out.add(msg);
    }

    private Object deserialize(byte[] data) throws IOException, ClassNotFoundException {
        ByteArrayInputStream bais = new ByteArrayInputStream(data);
        ObjectInputStream ois = new ObjectInputStream(bais);
        return ois.readObject();
    }
}
