package Netty.Server;

import Netty.Message.Message;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

public class NettyServerHandler extends SimpleChannelInboundHandler<Message> {
    @Override
    protected void messageReceived(ChannelHandlerContext ctx, Message msg) {
        // 处理从客户端接收的消息
        switch (msg.getType()) {
            case PACKET_INFO:
                System.out.println("收到数据包信息：" + msg.getData());
                break;
            case FEATURE_DATA:
                System.out.println("收到特征数据：" + msg.getData());
                break;
            default:
                System.out.println("收到未知类型消息");
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }
}
