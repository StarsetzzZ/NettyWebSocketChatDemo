package webSocketServer.handler;

import com.alibaba.fastjson.JSON;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import pojo.Message;
import util.BidMap;
import util.DBUtil;

import java.util.ArrayList;



public class WsTextFrameHandler extends SimpleChannelInboundHandler<TextWebSocketFrame> {
    static BidMap<String,ChannelHandlerContext> onlineMap;//在线用户名单,仅包含登录的用户
    static {
        onlineMap = new BidMap<>();
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, TextWebSocketFrame frame){
        Message in = JSON.toJavaObject(JSON.parseObject(frame.text()), Message.class);
        Message out;
        switch (in.getCode()) {
            case "201"://登录
                if (!onlineMap.containsKey(in.getUserName()) && DBUtil.authenticate(in.getUserName(),in.getPassword()) ) {
                    ctx.writeAndFlush(new TextWebSocketFrame(JSON.toJSONString(new Message("101"))));
                    onlineMap.put(in.getUserName(),ctx);
                    out = new Message();
                    out.setFrom(in.getUserName());
                    out.setCode("301");
                    out.broadCastToHandlerContexts(onlineMap);
                }else
                    ctx.writeAndFlush(new TextWebSocketFrame(JSON.toJSONString(new Message("102"))));
                break;
            case "202"://注册
                if (DBUtil.createUser(in.getUserName(),in.getPassword()) == 1)
                    ctx.writeAndFlush(new TextWebSocketFrame(JSON.toJSONString(new Message("103"))));
                else
                    ctx.writeAndFlush(new TextWebSocketFrame(JSON.toJSONString(new Message("104"))));
                break;
            case "303"://群聊
                in.setFrom(onlineMap.getKey(ctx));
                in.broadCastToHandlerContexts(onlineMap);
                break;
            case "304"://私聊
                in.setFrom(onlineMap.getKey(ctx));
                if (onlineMap.containsKey(in.getTo())) {
                    onlineMap.get(in.getTo()).writeAndFlush(new TextWebSocketFrame(JSON.toJSONString(in)));
                }
                break;
            case "305"://请求在线列表
                out = new Message();
                out.setCode("305");
                out.setOnlineList(new ArrayList<>(onlineMap.keySet()));
                String s = JSON.toJSONString(out);
                ctx.writeAndFlush(new TextWebSocketFrame(s));
                break;
        }
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx){
        System.out.println(ctx.channel().remoteAddress()+" : connected");
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx){
        System.out.println(ctx.channel().remoteAddress()+" : closed");
        Message out = new Message();
        out.setFrom(onlineMap.getKey(ctx));
        out.setCode("302");
        onlineMap.remove(onlineMap.getKey(ctx));
        out.broadCastToHandlerContexts(onlineMap);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause){
        System.out.println(ctx.channel().remoteAddress()+" : exceptionCaught,closed");
        ctx.close();
    }
}
