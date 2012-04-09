package com.github.nedis;


/**
 * User: roger
 * Date: 12-3-7 9:43
 */

public interface RedisClient {
    public String ping();

    public void set(String key, String value);
    
    public void setSafeBinary(String key, byte[] value);

    public String get(String key);
    
    public byte[] getSafeBinary(String key);

    public void startPipeline();

    public void endPipeline();

}
