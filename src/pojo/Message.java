package pojo;

import com.alibaba.fastjson.JSON;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import util.BidMap;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * @author Starset
 * @version 1.0
 * @date 2021/12/7 21:27
 */
public class Message implements Serializable {
    private static final long serialVersionUID = -3660835499823221424L;
    String code;
    String from;
    String to;
    String content;

    String userName;
    String password;

    ArrayList<String> onlineList;

    public Message(String code, String from, String to, String content) {
        this.code = code;
        this.from = from;
        this.to = to;
        this.content = content;
    }

    public Message(String userName, String password) {
        this.userName = userName;
        this.password = password;
    }

    public Message() {
    }

    public Message(String code, ArrayList<String> onlineList) {
        this.code = code;
        this.onlineList = onlineList;
    }

    public Message(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public String getContent() {
        return content;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public ArrayList<String> getOnlineList() {
        return onlineList;
    }

    public void setOnlineList(ArrayList<String> onlineList) {
        this.onlineList = onlineList;
    }

    public static void sendLoginFailed(ChannelHandlerContext context){
        context.writeAndFlush(new Message("101"));
    }
    public static void sendLoginSuccess(ChannelHandlerContext context){
        context.writeAndFlush(new Message("102"));
    }
    public static void sendRegisterFailed(ChannelHandlerContext context){
        context.writeAndFlush(new Message("103"));
    }
    public static void sendRegisterSuccess(ChannelHandlerContext context){
        context.writeAndFlush(new Message("104"));
    }

    public void broadCastToChannels(BidMap<String,Channel> map){
        for (String s : map.keySet()) {
            if(!s.equals(from)) map.get(s).pipeline().context("handler").writeAndFlush(this);
        }
    }

    public void broadCastToHandlerContexts(BidMap<String, ChannelHandlerContext> map){
        for (String s : map.keySet()) {
            if(!s.equals(from)) map.get(s).writeAndFlush(new TextWebSocketFrame(JSON.toJSONString(this)));
        }
    }
}
