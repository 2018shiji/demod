package com.example.demo.netty;


import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.springframework.stereotype.Component;

/**
 * Created by DGM on 2019/10/21.
 */
@Component
public class WebClient {

    public void start(){
        EventLoopGroup workerLoop = new NioEventLoopGroup();
        Bootstrap bootstrap = new Bootstrap();
        bootstrap.group(workerLoop)
                .channel(NioSocketChannel.class)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                        ch.pipeline().addLast(new WebClientHandler());
                    }
                });
        try {
            ChannelFuture future = bootstrap.connect("127.0.0.1", 8081).sync();
            future.channel().writeAndFlush(Unpooled.copiedBuffer("777".getBytes()));
            future.channel().closeFuture().sync();

        } catch (InterruptedException e){
            e.printStackTrace();

        } finally {
            workerLoop.shutdownGracefully();

        }
    }

    public static void main(String[] args){
        new WebClient().start();
    }
}
