package Netty.Client;

import Meta.PacketInfo;
import Netty.Message.Message;
import Netty.Message.MessageType;
import Netty.utils.PacketDecoder;
import Netty.utils.PacketEncoder;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.*;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;

public class NettyClient {
    private static NettyClient instance;
    private EventLoopGroup group;
    private Channel channel;
    private String host;
    private int port;

    private NettyClient() {
        this.host = "localhost";
        this.port = 8888;
    }

    public static synchronized NettyClient getInstance() {
        if (instance == null) {
            instance = new NettyClient();
        }
        return instance;
    }

    public void start() throws Exception {
        group = new NioEventLoopGroup();
        Bootstrap b = new Bootstrap();
        b.group(group)
                .channel(NioSocketChannel.class)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) {
                        ChannelPipeline p = ch.pipeline();
                        p.addLast(new LengthFieldBasedFrameDecoder(65535, 0, 4, 0, 4));
                        p.addLast(new LengthFieldPrepender(4));
                        p.addLast(new PacketEncoder());
                        p.addLast(new PacketDecoder());
                        p.addLast(new NettyClientHandler());
                    }
                });

        ChannelFuture f = b.connect(host, port).sync();
        channel = f.channel();
    }

    public void sendPacketInfo(PacketInfo packetInfo) {
        if (channel != null && channel.isActive()) {
            Message message = new Message(MessageType.PACKET_INFO, packetInfo);
            channel.writeAndFlush(message);
        }
    }

    public void stop() {
        if (group != null) {
            group.shutdownGracefully();
        }
    }
}
