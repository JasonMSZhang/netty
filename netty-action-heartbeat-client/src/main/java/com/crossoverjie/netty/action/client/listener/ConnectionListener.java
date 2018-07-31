package com.crossoverjie.netty.action.client.listener;

import com.crossoverjie.netty.action.client.HeartbeatClient;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.EventLoop;

import java.util.concurrent.TimeUnit;

/**
 * Created by IntelliJ IDEA.
 * User: Jason
 * Date: 2018/7/23
 */
public class ConnectionListener implements ChannelFutureListener {


    private HeartbeatClient client;

    public ConnectionListener(HeartbeatClient client){
        this.client = client;
    }

    @Override
    public void operationComplete(ChannelFuture channelFuture) throws Exception {
        if(!channelFuture.isSuccess()){
            final EventLoop loop = channelFuture.channel().eventLoop();
            loop.schedule(new Runnable() {
                @Override
                public void run() {
                    try {
                        client.createBootStrap(new Bootstrap(), loop);
                    } catch (InterruptedException e) {
                        System.out.println("重连失败");
                    }
                }
            }, 10, TimeUnit.SECONDS);
        }
    }
}
