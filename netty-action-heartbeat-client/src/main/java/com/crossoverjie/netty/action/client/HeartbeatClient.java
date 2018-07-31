package com.crossoverjie.netty.action.client;

import com.alibaba.fastjson.JSON;
import com.crossoverjie.netty.action.client.init.CustomerHandleInitializer;
import com.crossoverjie.netty.action.client.listener.ConnectionListener;
import com.crossoverjie.netty.action.common.pojo.CustomProtocol;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

/**
 * Function:
 *
 * @author crossoverJie
 *         Date: 22/05/2018 14:19
 * @since JDK 1.8
 */
@Component
public class HeartbeatClient {

    private final static Logger LOGGER = LoggerFactory.getLogger(HeartbeatClient.class);

    @Value("${netty.server.port}")
    private int nettyPort;

    @Value("${netty.server.host}")
    private String host;

    private SocketChannel channel;

    private ConnectionListener listener;

    @PostConstruct
    public void start() throws InterruptedException {

        EventLoopGroup group = new NioEventLoopGroup(2);
        Bootstrap bootstrap = new Bootstrap();
        createBootStrap(bootstrap, group);
    }

    public void createBootStrap(Bootstrap bootstrap, EventLoopGroup group) throws InterruptedException {
        bootstrap.group(group)
                .channel(NioSocketChannel.class)
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 5000)
                .handler(new CustomerHandleInitializer(this));
        ChannelFuture future = bootstrap.connect(host, nettyPort).sync();
        if (future.isSuccess()) {
            LOGGER.info("启动 Netty 成功");
            channel = (SocketChannel) future.channel();
        }else{
            LOGGER.info("启动失败");
        }
    }

    /**
     * 发送消息
     *
     * @param customProtocol
     */
    public void sendMsg(CustomProtocol customProtocol) {
        ChannelFuture future = channel.writeAndFlush(customProtocol);
        future.addListener((ChannelFutureListener) channelFuture ->
                LOGGER.info("客户端手动发消息成功={}", JSON.toJSONString(customProtocol)));

    }

    @PreDestroy
    public void destroy(){
        this.channel.close();
    }
}
