package com.github.nedis.codec;

import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.Channels;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelHandler;

/**
 * User: roger
 * Date: 12-3-15 11:11
 */
public class RedisClientHandler extends SimpleChannelHandler {

    @Override
    public void writeRequested(ChannelHandlerContext ctx, MessageEvent e) throws java.lang.Exception {
        Command command = (Command)e.getMessage();
        Channels.write(ctx, e.getFuture(), command.buffer());
    }

    @Override
    public void messageReceived(ChannelHandlerContext ctx, MessageEvent e) throws java.lang.Exception {

    }

}
