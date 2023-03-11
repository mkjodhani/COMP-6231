/**
 *
 * @project Distributed Movie Ticket Booking System
 * @author Mayur Jodhani
 * @version 1.0.0
 * @since 2023-01-24
 */
package client.menu;

import shared.*;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.Registry;
import java.time.LocalDate;
import java.util.logging.Level;

public class AdminAPI {
    private Admin admin;
    private java.util.logging.Logger LOGGER;

    private String adminID;

    private static final String menu = "1. Add movie slot\n" +
            "2. Remove movie slot\n" +
            "3. List movie availability\n" +
            "4. Book movie tickets\n" +
            "5. Get booking schedule\n" +
            "6. Cancel movie tickets\n" +
            "7. Exit";

    /**
     * initiate the RMI connection between server and client
     * @param adminId
     * @throws NotBoundException
     * @throws RemoteException
     */
    public AdminAPI(String adminId) throws NotBoundException, RemoteException {
        Registry registry = Input.getRegistryByPort(1099);
        this.adminID = adminId;
        admin = (Admin) registry.lookup(Input.getRemoteObjectIdByPrefix(Role.getLocationPrefix(adminId),"Admin"));
        LOGGER = Logger.getLogger(adminId,false);
    }

    /**
     * This function will take input from the user and add slot to given movie with associated capacity.
     */
    public void addMovieSlots(){
        try {
             String movieId = Input.getMovieID();
             String movieName = Input.getString("Enter movie name :").toLowerCase();
             int capacity = Input.getInteger("Enter the capacity :");
             String response = admin.addMovieSlots(movieId,movieName,capacity);
             System.out.println(Message.getMessage(response));
             LOGGER.log(Level.INFO,Logger.getFullMessage(String.format("addMovieSlot movieId:%s, movieName:%s, capacity:%d",movieId,movieName,capacity),Message.getMessage(response)));
        }
        catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    /**
     * This function will take input from the user and remove slot to given slot ID.
     */
    public void removeMovieSlots(){
        try{
            String movieId = Input.getMovieID();
            String movieName = Input.getString("Enter movie name :").toLowerCase();
            String response = admin.removeMovieSlots(movieId,movieName);
            System.out.println(Message.getMessage(response));
            LOGGER.info(Logger.getFullMessage(String.format("removeMovieSlots movieId:%s, movieName:%s",movieId,movieName),Message.getMessage(response)));
        }
        catch (Exception e){
            System.out.println(e.getMessage());
        }
    }

    /**
     * This function will take input from the user and list movie shows availability for given movie.
     */
    public void listMovieShowsAvailability(){
        try{
            String movieName = Input.getString("Enter movie name :").toLowerCase();
            String response = admin.listMovieShowsAvailability(movieName);
            String slots = Message.getMessage(response);
            if(slots.equals("")){
                System.out.println("No slots found for " + movieName + ".");
            }
            else{
                for(String slot: slots.split(Commands.DELIMITER)){
                    printMovieAvailability(slot);
                }
            }
            LOGGER.info(Logger.getFullMessage(String.format("listMovieShowsAvailability movieName:%s",movieName),Message.getMessage(response)));
        }
        catch (Exception e){
            e.printStackTrace();
            System.out.println("Something went wrong form our side, please try again later.");
        }
    }
    public void start(){
        while (true){
            int option = Input.getIntegerInRange(menu,1,7);
            if(option == 7){
                return;
            }
            else if(option <= 3){
                switch (option){
                    case 1:
                        addMovieSlots();
                        break;
                    case 2:
                        removeMovieSlots();
                        break;
                    case 3:
                        listMovieShowsAvailability();
                        break;
                }
            }
            else{
                String customerId = Menu.getUserId().toLowerCase();
                java.util.logging.Logger LOGGER = Logger.getLogger(this.adminID,false);
                try {
                    CustomerAPI customerAPI = new CustomerAPI(customerId,LOGGER);
                    switch (option){
                        case 4:
                            customerAPI.bookTicket();
                            break;
                        case 5:
                            customerAPI.getBookingSchedule();
                            break;
                        case 6:
                            customerAPI.cancelBookingTicket();
                            break;
                    }
                } catch (Exception e) {
                    System.out.println("Something went wrong form our side, please try again later.");
                }
            }
        }
    }
    public static void printMovieAvailability(String movieAvailability){
        if(movieAvailability.equals("")){
            return;
        }
        String slotId = movieAvailability.split(" ")[0];
        int totalSeatsAvailable = Integer.parseInt(movieAvailability.split(" ")[1]);
        int day = Integer.parseInt(slotId.substring(4,6)) , month = Integer.parseInt(slotId.substring(6,8)) , year = Integer.parseInt(slotId.substring(8,10)) + 2000;
        LocalDate movieDate = LocalDate.of(year, month, day);
        String movie = "";
        movie += Menu.getHorizontalLine();
        movie += Menu.getLeftPaddingString(String.format(Role.getLocationPrefix(slotId)));
        movie += Menu.getRightPaddingString(String.format("Date : "+ movieDate));
        movie += Menu.getRightPaddingString(String.format("Time : %s", Role.getTimeBySlotID(slotId)));
        movie += Menu.getRightPaddingString(String.format("Total tickets available : %d",totalSeatsAvailable));
        movie += Menu.getHorizontalLine();
        System.out.println(movie);
    }

}
