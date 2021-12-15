package webSocketServer;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.stream.ChunkedWriteHandler;
import util.DBUtil;
import webSocketServer.handler.WsTextFrameHandler;


public class WebSocketServer {

    void start(){
        NioEventLoopGroup boss = new NioEventLoopGroup();
        NioEventLoopGroup worker = new NioEventLoopGroup();
        try{
            ServerBootstrap b = new ServerBootstrap();
            b.group(boss,worker);
            b.channel(NioServerSocketChannel.class);
            b.childHandler(new ChannelInitializer<SocketChannel>() {
                protected void initChannel(SocketChannel sc) {
                    sc.pipeline().addLast("http-codec",new HttpServerCodec());
                    sc.pipeline().addLast("http-chunked", new ChunkedWriteHandler());
                    sc.pipeline().addLast("aggregator", new HttpObjectAggregator(64 * 1024));
                    sc.pipeline().addLast("wsProtocolHandler", new WebSocketServerProtocolHandler("/ws"));
                    sc.pipeline().addLast("wsText-handler", new WsTextFrameHandler());
                }
            });
            b.option(ChannelOption.SO_BACKLOG,128);
            b.childOption(ChannelOption.SO_KEEPALIVE,true);

            ChannelFuture future = b.bind(8889).sync();
            System.out.println("WebSocket服务器启动成功!");
            future.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            boss.shutdownGracefully();
            worker.shutdownGracefully();
        }
    }

    public static void main(String[] args) {
        if(DBUtil.initConnection("","root","")){
            new WebSocketServer().start();
        }
    }
}
