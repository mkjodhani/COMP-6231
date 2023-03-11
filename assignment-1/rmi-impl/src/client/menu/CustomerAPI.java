/**
 *
 * @project Distributed Movie Ticket Booking System
 * @author Mayur Jodhani
 * @version 1.0.0
 * @since 2023-01-24
 */
package client.menu;

import shared.*;

import java.rmi.RemoteException;
import java.rmi.registry.Registry;
import java.util.logging.Level;

public class CustomerAPI {
    private Customer customer;
    private String customerId;
    private java.util.logging.Logger LOGGER;
    private static final String menu = "1. Book Ticket\n" +
            "2. Get Booking Schedule\n" +
            "3. Cancel Ticket\n" +
            "4. Exit";

    /**
     * initiate the RMI connection between server and client based the associated customer ID and start LOGGER
     * @param customerId
     */
    public CustomerAPI(String customerId) {
        this.customerId = customerId;
        String location = Role.getLocationPrefix(customerId);
        Registry registry;
        try{
            registry = Input.getRegistryByPort(1099);
            customer = (Customer) registry.lookup(Input.getRemoteObjectIdByPrefix(location,"Customer"));
            LOGGER = Logger.getLogger(customerId, false);
            LOGGER.log(Level.FINE,"Logging in...");
        }catch (Exception e){
            System.out.println("Something went wrong!");
        }
    }

    /**
     * initiate the RMI connection between server and client based the associated customer ID with given LOGGER
     * @param customerId
     * @param logger
     */
    public CustomerAPI(String customerId, java.util.logging.Logger logger) {
        this.customerId = customerId;
        String location = Role.getLocationPrefix(customerId);
        Registry registry;
        try{
            registry = Input.getRegistryByPort(1099);
            customer = (Customer) registry.lookup(Input.getRemoteObjectIdByPrefix(location,"Customer"));
            LOGGER = logger;
            LOGGER.log(Level.FINE,"Logging in...");
        }catch (Exception e){
            System.out.println("Something went wrong!");
        }
    }

    public String bookTicketUsingInputs(String movieID, String movieName, int numberOfTickets){
        String result = "";
        try {
            result += Message.getMessage(customer.bookMovieTickets(this.customerId,movieID,movieName,numberOfTickets));
        } catch (RemoteException e) {
            result = e.getMessage();
        }
        return result;
    }
    public void bookTicket(){
        String movieID, movieName;
        int numberOfTickets;
        movieID = Input.getMovieID();
        movieName = Input.getString("Enter movie name :").toLowerCase();
        numberOfTickets = Input.getInteger("Enter number of tickets :");
        LOGGER.log(Level.INFO,String.format("movieID:%s, movieName:%s, numberOfTickets:%d",movieID, movieName, numberOfTickets));
        String result = bookTicketUsingInputs(movieID,movieName,numberOfTickets);
        System.out.println(result);
        LOGGER.log(Level.INFO,result);
    }
    public void  getBookingSchedule(){
        try {
            LOGGER.log(Level.INFO,"Fetching booking schedule...");
            String result = customer.getBookingSchedule(this.customerId);
            LOGGER.log(Level.INFO,result);
            if(result.equals("")){
                System.out.println(String.format("No schedule available for customer %s.",this.customerId));
            }
            else{
                String[] tickets = Commands.generateParamsFromCommand(Message.getMessage(result));
                for(String ticket : tickets){
                    if(!ticket.equals("")){
                        String[] metaData = ticket.split(" ");
                        String movieName = metaData[0];
                        String movieID = metaData[1];
                        int numberOfTickets = Integer.parseInt(metaData[2]);
                        printTicket(movieName,movieID,numberOfTickets);
                    }
                }
            }
        } catch (RemoteException e) {
            System.out.println("Something went wrong!");
        }
    }
    public void cancelBookingTicket(){
        try {
            String movieID, movieName;
            int numberOfTickets;
            movieID = Input.getMovieID();
            movieName = Input.getString("Enter movie name :").toLowerCase();
            numberOfTickets = Input.getInteger("Enter number of tickets :");
            LOGGER.log(Level.INFO,String.format("Cancel booking tickets movieID:%s, movieName:%s, numberOfTickets:%d",movieID, movieName, numberOfTickets));
            String result = customer.cancelMovieTickets(this.customerId,movieID,movieName,numberOfTickets);
            LOGGER.log(Level.INFO,result);
            System.out.println(result);
        } catch (RemoteException e) {
            // TODO : ADD LOG
            System.out.println("Something went wrong");
        }
    }
    public void start(){
        while (true){
            int option = Input.getIntegerInRange(menu,1,4);
            switch (option){
                case 1:
                    bookTicket();
                    break;
                case 2:
                    getBookingSchedule();
                    break;
                case 3:
                    cancelBookingTicket();
                    break;
                case 4:
                    LOGGER.log(Level.FINE,"Logging out...");
                    return;
            }
        }
    }
    public static void printTicket(String movieName,String movieID, int numberOfTickets){
        String ticket = "";
        ticket += Menu.getHorizontalLine();
        ticket += Menu.getRightPaddingString(String.format("Movie : %s",movieName));
        ticket += Menu.getRightPaddingString(String.format("Movie ID : %s",movieID));
        ticket += Menu.getRightPaddingString(String.format("Total Tickets : %d",numberOfTickets));
        ticket += Menu.getHorizontalLine();
        System.out.println(ticket);
    }
}
