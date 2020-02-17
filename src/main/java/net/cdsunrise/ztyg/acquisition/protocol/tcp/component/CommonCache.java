package net.cdsunrise.ztyg.acquisition.protocol.tcp.component;

import java.util.concurrent.ConcurrentHashMap;

/**
 * 自定义缓存类
 * @author Binke Zhang
 * @date 2019/9/19 11:49
 */
public class CommonCache<K,V> extends ConcurrentHashMap<K,V>{
    private CommonCache(){}

    private static class SingletonClassInstance{
        private static final CommonCache instance = new CommonCache();
    }

    public static CommonCache getInstance(){
        return SingletonClassInstance.instance;
    }

}
