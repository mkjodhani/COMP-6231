/**
 *
 * @project Distributed Movie Ticket Booking System
 * @author Mayur Jodhani
 * @version 1.0.0
 * @since 2023-01-24
 */
package server.theatre;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class TheatreMetaData {

    private String ipAddress, location, prefix;
    private InetAddress inetAddress;
    private int port;

    public TheatreMetaData(String ipAddress, String location, String prefix, int port) throws UnknownHostException {
        this.ipAddress = ipAddress;
        this.location = location;
        this.prefix = prefix;
        this.port = port;
        this.inetAddress = InetAddress.getByName(ipAddress);
    }

    public String getLocation() {
        return location;
    }

    public String getPrefix() {
        return prefix;
    }

    public InetAddress getInetAddress() {
        return inetAddress;
    }

    public int getPort() {
        return port;
    }
}
