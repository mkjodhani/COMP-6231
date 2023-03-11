/**
 *
 * @project Distributed Movie Ticket Booking System
 * @author Mayur Jodhani
 * @version 1.0.0
 * @since 2023-01-24
 */
package client.menu;

import client.CentralRepository;
import server.theatre.Theatre;
import shared.*;

import javax.xml.namespace.QName;
import javax.xml.ws.Service;
import java.net.URL;
import java.rmi.registry.Registry;
import java.time.LocalDate;
import java.util.logging.Level;

public class CustomerAPI {
    private Customer customer;
    private String customerId;
    private java.util.logging.Logger LOGGER;
    private static final String menu = "1. Book Ticket\n" +
            "2. Get Booking Schedule\n" +
            "3. Cancel Ticket\n" +
            "4. Exchange Ticket\n" +
            "5. Exit";

    /**
     * initiate the RMI connection between server and client based the associated customer ID and start LOGGER
     * @param customerId
     */
    public CustomerAPI(String customerId) {
        this.customerId = customerId;
        String location = Role.getLocationPrefix(customerId);
        try{
            customer = getCustomerService(location);
            LOGGER = Logger.getLogger(customerId, false);
            LOGGER.log(Level.FINE,"Logging in...");
        }catch (Exception e){
            e.printStackTrace();
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
        try{
            customer = getCustomerService(location);
            LOGGER = logger;
            LOGGER.log(Level.FINE,"Logging in...");
        }catch (Exception e){
            System.out.println("Something went wrong!");
        }
    }

    public String bookTicketUsingInputs(String movieID, String movieName, int numberOfTickets){
        String result = "";
        String response = customer.bookMovieTickets(this.customerId,movieID,movieName,numberOfTickets);
        Message message =  Message.generateMessageFromString(response);
        result = message.extractMessage();
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
        LOGGER.log(Level.INFO,"Fetching booking schedule...");
        String response = customer.getBookingSchedule(this.customerId);
        Message message = Message.generateMessageFromString(response);
        LOGGER.log(Level.INFO,message.getMessage());
        if(message.extractMessage().equals("")){
            System.out.println(String.format("No schedule available for customer %s.",this.customerId));
        }
        else{
            String[] tickets = Commands.generateParamsFromCommand(message.extractMessage());
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
    }
    public void cancelBookingTicket(){
        String movieID, movieName;
        int numberOfTickets;
        movieID = Input.getMovieID();
        movieName = Input.getString("Enter movie name :").toLowerCase();
        numberOfTickets = Input.getInteger("Enter number of tickets :");
        LOGGER.log(Level.INFO,String.format("Cancel booking tickets movieID:%s, movieName:%s, numberOfTickets:%d",movieID, movieName, numberOfTickets));
        String result = customer.cancelMovieTickets(this.customerId,movieID,movieName,numberOfTickets);
        LOGGER.log(Level.INFO,result);
        System.out.println(result);
    }
    public void exchangeTickets(){
        String oldMovieName, oldMovieID, newMovieID, newMovieName;
        int numberOfTickets;
        oldMovieName = Input.getString("Enter old Movie Name:");
        oldMovieID = Input.getMovieID();
        newMovieName = Input.getString("Enter new Movie Name:");
        newMovieID = Input.getMovieID();
        numberOfTickets = Input.getInteger("Enter the number of tickets you want to exchange:");
        String response = customer.exchangeTickets(customerId,oldMovieName,oldMovieID,newMovieID,newMovieName,numberOfTickets);
        Message message =  Message.generateMessageFromString(response);
        message.show();
    }
    public void start(){
        while (true){
            int option = Input.getIntegerInRange(menu,1,5);
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
                    exchangeTickets();
                    break;
                case 5:
                    LOGGER.log(Level.FINE,"Logging out...");
                    return;
            }
        }
    }
    public static void printTicket(String movieName,String movieID, int numberOfTickets){
        String ticket = "";
        int day = Integer.parseInt(movieID.substring(4,6)) , month = Integer.parseInt(movieID.substring(6,8)) , year = Integer.parseInt(movieID.substring(8,10)) + 2000;
        LocalDate movieDate = LocalDate.of(year, month, day);
        ticket += Menu.getHorizontalLine();
        ticket += Menu.getLeftPaddingString(String.format(Role.getLocationPrefix(movieID)));
        ticket += Menu.getRightPaddingString(String.format("Movie : %s",movieName));
        ticket += Menu.getRightPaddingString(String.format("Date : "+ movieDate));
        ticket += Menu.getRightPaddingString(String.format("Time : %s", Role.getTimeBySlotID(movieID)));
        ticket += Menu.getRightPaddingString(String.format("Total Tickets : %d",numberOfTickets));
        ticket += Menu.getHorizontalLine();
        System.out.println(ticket);
    }
    public static shared.Customer getCustomerService(String prefix){
        try{
            int port = CentralRepository.locationPorts.get(prefix);
            URL url = new URL(String.format("http://localhost:%d/ws/customer?wsdl",port));
            QName qname = new QName("http://web.theatre.server/", "CustomerServiceService");
            Service service = Service.create(url, qname);
            shared.Customer customer = service.getPort(shared.Customer.class);
            return customer;
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }
}
