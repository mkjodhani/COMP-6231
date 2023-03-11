/**
 *
 * @project Distributed Movie Ticket Booking System
 * @author Mayur Jodhani
 * @version 1.0.0
 * @since 2023-01-24
 */
package server.theatre;

import server.theatre.movie.Movie;
import server.theatre.query.Query;
import server.theatre.web.Publisher;
import shared.Admin;
import shared.Customer;
import shared.Input;

import java.io.IOException;
import java.net.*;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Theatre implements Runnable{

    static public Logger LOGGER;
    public static final HashMap<String,TheatreMetaData> locationPorts = new HashMap<>();
    private static TheatreMetaData metaData;
    private static DatagramSocket datagramSocket;
    private static Admin admin;
    private static Customer customer;



    public static HashMap<String, Movie> movies = new HashMap<>();
    public static void main(String[] args) throws UnknownHostException {
        String location, prefix;
        int port;
        // TODO Move this to Replica Manager for fault tolerance
        locationPorts.put("atw",new TheatreMetaData(InetAddress.getLocalHost().getHostAddress(),"Atwater","atw",5051));
        locationPorts.put("ver",new TheatreMetaData(InetAddress.getLocalHost().getHostAddress(),"Verdun","ver",5052));
        locationPorts.put("out",new TheatreMetaData(InetAddress.getLocalHost().getHostAddress(),"Outremont","out",5053));

        // Set three movies by default for all the server such as Avatar, Avengers and Titanic

        try{
            if(args.length == 3){
                location = args[0].toLowerCase();
                prefix = args[1].toLowerCase();
                port = Integer.parseInt(args[2]);
            }
            else{
                location = Input.getString("Enter the location:").toLowerCase();
                prefix = Input.getString("Enter the prefix for the location:").toLowerCase();
                port = Input.getInteger("Enter the PORT number:");
            }

            Theatre theatre = new Theatre(port,location,prefix);
            Thread thread = new Thread(theatre);
            thread.start();
        }
        catch (Exception e){
            e.printStackTrace();
            System.out.println("Enter the valid information");
        }
    }
    public Theatre(int port, String location, String prefix) throws UnknownHostException, SocketException {
        datagramSocket = new DatagramSocket(port);
        metaData = new TheatreMetaData(InetAddress.getLocalHost().getHostAddress(),location,prefix,port);
        LOGGER = shared.Logger.getLogger(location,true);
    }

    @Override
    public void run() {
        LOGGER.log(Level.INFO,"Starting COBRA registry for the server.");
        Publisher publisher = new Publisher(metaData.getPort());
        publisher.publish();
        LOGGER.log(Level.INFO,"Remote objects are bound by CORBA service.");
        // to listen all the command from peer server
        setInitialState();

        LOGGER.log(Level.INFO,String.format("UDP server started to listen request for %s at %d port",this.metaData.getLocation(),this.metaData.getPort()));
//            start UDP listener
        while (true){
            try {
                byte[] bytes = new byte[1024];
                DatagramPacket receivedPacket = new DatagramPacket(bytes,bytes.length);
                this.datagramSocket.receive(receivedPacket);
                Query query = new Query(receivedPacket);
                Thread thread = new Thread(query);
                thread.start();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public static TheatreMetaData getMetaData() {
        return metaData;
    }

    public static HashMap<String, Movie> getMovies() {
        return movies;
    }

    public static boolean isLocalServer(String id){
        if(id.toLowerCase().contains(Theatre.getMetaData().getPrefix().toLowerCase())){
            return true;
        }
        return false;
    }
    private static void setInitialState(){
        String[] movieList = new String[]{"Avengers","Avatar","Titanic"};
        for(String movieName : movieList)
        {
            Movie.addMovie(movieName.toLowerCase());
        }
    }



    public static Admin getAdmin() {
        return admin;
    }

    public static void setAdmin(Admin admin) {
        Theatre.admin = admin;
    }

    public static Customer getCustomer() {
        return customer;
    }

    public static void setCustomer(Customer customer) {
        Theatre.customer = customer;
    }
}
