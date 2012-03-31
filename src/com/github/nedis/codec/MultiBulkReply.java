package com.github.nedis.codec;

import com.github.nedis.RedisException;
import org.jboss.netty.buffer.ChannelBuffer;

/**
 * User: roger
 * Date: 12-3-31 下午2:08
 */
public class MultiBulkReply extends Reply<Object[]> {
    public static final char MARKER = '*';
    private int size;
    private int num;
    public MultiBulkReply(Object... value) {
        super(MARKER, value);
    }

    public void read(RedisDecoder rd, ChannelBuffer is) {
        if (size == -1) {
            byte star = is.readByte();
            if (star == MARKER) {
                size = 0;
            } else {
                throw new AssertionError("Unexpected character in stream: " + star);
            }
        }
        if (size == 0) {
            // If the read fails, we need to skip the star
            size = -1;
            // Read the size, if this is successful we won't read the star again
            size = RedisDecoder.readInteger(is);
            value = new Object[size];
            rd.checkpoint();
        }
        for (int i = num; i < size; i++) {
            int read = is.readByte();
            if (read == BulkReply.MARKER) {
                value[i] = RedisDecoder.readBytes(is);
            } else if (read == IntegerReply.MARKER) {
                value[i] = RedisDecoder.readInteger(is);
            } else {
                throw new RedisException("Unexpected character in stream: " + read);
            }
            num = i + 1;
            rd.checkpoint();
        }
    }
}
