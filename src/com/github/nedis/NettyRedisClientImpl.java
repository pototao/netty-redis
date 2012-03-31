package com.github.nedis;

import com.github.nedis.codec.*;
import com.sun.xml.internal.ws.api.pipe.PipelineAssembler;
import org.jboss.netty.bootstrap.ClientBootstrap;
import org.jboss.netty.channel.*;
import org.jboss.netty.channel.group.ChannelGroup;
import org.jboss.netty.channel.group.DefaultChannelGroup;
import org.jboss.netty.channel.socket.ClientSocketChannelFactory;
import org.jboss.netty.channel.socket.nio.NioClientSocketChannelFactory;
import org.jboss.netty.util.HashedWheelTimer;
import org.jboss.netty.util.Timer;

import java.net.InetSocketAddress;
import java.util.concurrent.Executors;
import java.util.logging.Logger;

/**
 * User: roger
 * Date: 12-3-15 11:03
 */
public class NettyRedisClientImpl implements RedisClient {
    private Logger logger = Logger.getLogger(getClass().getName());
    private ClientBootstrap bootstrap;
    private Channel channel;
    private Timer timer;
    private ChannelGroup channels;

    private String host;
    private int port;
    private ChannelPipeline pipeline;

    public NettyRedisClientImpl(String host) {
        this("localhost", 6379);
    }

    synchronized ChannelPipeline getPipeline() {

        return pipeline;
    }

    public NettyRedisClientImpl(String host, int port) {
        this.host = host;
        this.port = port;

        channels = new DefaultChannelGroup();
        timer    = new HashedWheelTimer();

        ClientSocketChannelFactory factory =  new  NioClientSocketChannelFactory(Executors.newCachedThreadPool(), Executors.newCachedThreadPool());
        bootstrap = new ClientBootstrap(factory);
        pipeline = Channels.pipeline(new AutoReconnectHandler(this, channels, timer), new RedisDecoder(), new RedisClientHandler());

        bootstrap.setPipeline(pipeline);

        ChannelFuture future = bootstrap.connect(new InetSocketAddress(host, port));
        channel = future.awaitUninterruptibly().getChannel();


    }

    public void reconnect() {
        logger.info("reconnect");
        bootstrap.setPipeline(pipeline);
        ChannelFuture future = bootstrap.connect(new InetSocketAddress(host, port));
        channel = future.awaitUninterruptibly().getChannel();
    }

    public void ping() {
        channel.write(new Command(CommandType.PING,  null));
    }

    public void set(String key, String value) {
        Command set = new Command(CommandType.SET, new CommandArgs().addKey(key).addValue(value));
        channel.write(set);
    }

    public void setSafeBinary(String key, byte[] value) {

    }

    public String get(String key) {
        Command cmd = new Command(CommandType.GET, new CommandArgs().addKey(key));
        channel.write(cmd);
        try {
            cmd.waitForResult();
            BulkReply bulkReply = (BulkReply)cmd.getOutput();
            return bulkReply.getString();
        }catch(InterruptedException e) {
            return null;
        }

    }

    public byte[] getSafeBinary(String key) {
        return new byte[0];
    }

    public static void main(String[] args) {
        final RedisClient redis = new NettyRedisClientImpl("localhost");
        redis.set("name", "chenxy");
        System.out.println(redis.get("name"));

    }
}
