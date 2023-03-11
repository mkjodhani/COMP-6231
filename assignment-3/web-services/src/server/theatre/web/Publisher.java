package server.theatre.web;

import javax.xml.ws.Endpoint;

/**
 * @author mkjodhani
 * @version 2.0
 * @project
 * @since 11/03/23
 */
public class Publisher {
    private int port;

    public Publisher(int port) {
        this.port = port;
        System.out.println(port);
    }

    public void publish(){
        String localhost = String.format("http://localhost:%d/ws",port);
        Endpoint.publish(localhost+"/admin", new AdminService());
        Endpoint.publish(localhost+"/customer", new CustomerService());
    }
}
