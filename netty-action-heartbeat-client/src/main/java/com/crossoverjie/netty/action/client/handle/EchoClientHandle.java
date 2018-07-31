package com.crossoverjie.netty.action.client.handle;

import com.crossoverjie.netty.action.client.HeartbeatClient;
import com.crossoverjie.netty.action.client.util.SpringBeanFactory;
import com.crossoverjie.netty.action.common.pojo.CustomProtocol;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.EventLoop;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.util.CharsetUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.concurrent.TimeUnit;

/**
 * Function:
 *
 * @author crossoverJie
 *         Date: 16/02/2018 18:09
 * @since JDK 1.8
 */
public class EchoClientHandle extends SimpleChannelInboundHandler<ByteBuf> {

    private final static Logger LOGGER = LoggerFactory.getLogger(EchoClientHandle.class);

    @Autowired
    private HeartbeatClient client;

    public EchoClientHandle(HeartbeatClient client){
        this.client = client;
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {

        if (evt instanceof IdleStateEvent){
            IdleStateEvent idleStateEvent = (IdleStateEvent) evt ;

            if (idleStateEvent.state() == IdleState.WRITER_IDLE) {
                LOGGER.info("已经 10 秒没有发送信息！");
                //向服务端发送消息
                CustomProtocol heartBeat = SpringBeanFactory.getBean("heartBeat", CustomProtocol.class);
                ctx.writeAndFlush(heartBeat).addListener(ChannelFutureListener.CLOSE_ON_FAILURE);
                LOGGER.info("发送心跳信息！" + heartBeat.toString());
            } else if(idleStateEvent.state() == IdleState.READER_IDLE){
                //20秒内没收到信息就重新连接

            }
        }

        super.userEventTriggered(ctx, evt);
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {

        //客户端和服务端建立连接时调用
        LOGGER.info("已经建立了连接。。");
    }

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, ByteBuf in) throws Exception {

        //从服务端收到消息时被调用
        LOGGER.info("客户端收到消息={}",in.toString(CharsetUtil.UTF_8)) ;

    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        //异常时断开连接
        cause.printStackTrace() ;
        //ctx.close() ;
    }

    @Override
    public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {

        final EventLoop loop = ctx.channel().eventLoop();
        if(!ctx.channel().isActive()){
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
