package client;

import server.theatre.TheatreMetaData;

import java.net.InetAddress;
import java.util.HashMap;

/**
 * @author mkjodhani
 * @project
 * @since 11/03/23
 */
public class CentralRepository {
    public static HashMap<String,Integer> locationPorts = new HashMap<>();
    static {
        locationPorts.put("atw",5051);
        locationPorts.put("ver",5052);
        locationPorts.put("out",5053);
    }
}
