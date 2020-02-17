package net.cdsunrise.ztyg.acquisition.protocol.rs485.session;


import io.netty.channel.Channel;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * session 管理器
 *
 * @author:
 * @date: Create in 15:41 2018/09/20
 */
public class BaseSessionManager<T extends BaseSession> {

    /**
     * 设备id-session
     */
    private Map<String, T> deviceIdMap = new ConcurrentHashMap<>();

    /**
     * channelId-session
     */
    private Map<String, T> sessionIdMap = new ConcurrentHashMap<>();

    public synchronized T put(T baseSession) {
        if (baseSession.getDeviceIdentifier() != null) {
            deviceIdMap.put(baseSession.getDeviceIdentifier(), baseSession);
        }
        return sessionIdMap.put(baseSession.getId(), baseSession);
    }

    public synchronized T removeBySessionId(String sessionId) {
        if (sessionId == null) {
            return null;
        }
        T session = sessionIdMap.remove(sessionId);
        if (session == null) {
            return null;
        }
        if (session.getDeviceIdentifier() != null) {
            this.deviceIdMap.remove(session.getDeviceIdentifier());
        }
        return session;
    }

    public synchronized T removeByChannel(Channel channel) {
        return removeBySessionId(BaseSession.buildId(channel));
    }

    public T findBySessionId(String sessionId) {
        return sessionIdMap.get(sessionId);
    }

    public T findByDeviceCode(String deviceCode) {
        return deviceIdMap.get(deviceCode);
    }

    public T findByChannel(Channel channel) {
        return findBySessionId(BaseSession.buildId(channel));
    }

    public Collection<T> values() {
        return sessionIdMap.values();
    }

}
