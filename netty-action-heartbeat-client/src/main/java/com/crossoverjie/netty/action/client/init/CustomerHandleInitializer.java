package com.crossoverjie.netty.action.client.init;

import com.crossoverjie.netty.action.client.HeartbeatClient;
import com.crossoverjie.netty.action.client.encode.HeartbeatEncode;
import com.crossoverjie.netty.action.client.handle.EchoClientHandle;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.handler.timeout.IdleStateHandler;

/**
 * Function:
 *
 * @author crossoverJie
 *         Date: 23/02/2018 22:47
 * @since JDK 1.8
 */
public class CustomerHandleInitializer extends ChannelInitializer<Channel> {

    private HeartbeatClient client;
    private IdleStateHandler idleStateHandler;
    private HeartbeatEncode heartbeatEncode;
    private EchoClientHandle echoClientHandle;

    public CustomerHandleInitializer(HeartbeatClient client){
        this.client = client;
        this.idleStateHandler = new IdleStateHandler(0, 10, 0);
        this.heartbeatEncode = new HeartbeatEncode();
        this.echoClientHandle = new EchoClientHandle(this.client);
    }

    @Override
    protected void initChannel(Channel ch) throws Exception {
        ch.pipeline()
                //10 秒没发送消息 将IdleStateHandler 添加到 ChannelPipeline 中
                .addLast(this.idleStateHandler)
                .addLast(this.heartbeatEncode)
                .addLast(this.echoClientHandle);
    }
}
