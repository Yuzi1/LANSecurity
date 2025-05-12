package Netty.Client;

import Netty.Message.Message;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

public class NettyClientHandler extends SimpleChannelInboundHandler<Message>{
    @Override
    protected void messageReceived(ChannelHandlerContext ctx, Message msg) {
        // 处理从服务端接收的消息
        switch (msg.getType()) {
            case ALERT:
                System.out.println("收到告警信息：" + msg.getData());
                break;
            case COMMAND:
                System.out.println("收到控制命令：" + msg.getData());
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