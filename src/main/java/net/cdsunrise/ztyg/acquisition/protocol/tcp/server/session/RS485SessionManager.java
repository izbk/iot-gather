package net.cdsunrise.ztyg.acquisition.protocol.tcp.server.session;


/**
 * RS485 session管理器
 */
public class RS485SessionManager extends BaseSessionManager<RS485Session> {

    private static volatile RS485SessionManager instance;
    private RS485SessionManager(){}

    static {
        instance = new RS485SessionManager();
    }

    public static RS485SessionManager getInstance() {
        return instance;
    }
}