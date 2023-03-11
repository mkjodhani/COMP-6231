/**
 *
 * @project Distributed Movie Ticket Booking System
 * @author Mayur Jodhani
 * @version 1.0.0
 * @since 2023-01-24
 */
package client.menu;

import org.omg.CORBA.Admin;
import org.omg.CORBA.AdminHelper;
import org.omg.CORBA.ORB;
import org.omg.CORBA.ORBPackage.InvalidName;
import org.omg.CosNaming.NamingContextExt;
import org.omg.CosNaming.NamingContextExtHelper;
import org.omg.CosNaming.NamingContextPackage.CannotProceed;
import org.omg.CosNaming.NamingContextPackage.NotFound;
import shared.*;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
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
            "7. Exchange movie tickets\n" +
            "8. Exit";

    /**
     * initiate the RMI connection between server and client
     * @param adminId
     * @throws NotBoundException
     * @throws RemoteException
     */
    public AdminAPI(String adminId) throws NotBoundException, RemoteException {
        this.adminID = adminId;
        this.admin = getAdminRemoteObjectByPrefix(Role.getLocationPrefix(adminId));
//        Registry registry = Input.getRegistryByPort(1099);
//        admin = (Admin) registry.lookup(Input.getRemoteObjectIdByPrefix(Role.getLocationPrefix(adminId),"Admin"));
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
             Message message =  Message.generateMessageFromString(response);
             message.show();
             LOGGER.log(Level.INFO,Logger.getFullMessage(String.format("addMovieSlot movieId:%s, movieName:%s, capacity:%d",movieId,movieName,capacity),message.extractMessage()));
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
            Message message =  Message.generateMessageFromString(admin.removeMovieSlots(movieId,movieName));
            message.show();
            LOGGER.info(Logger.getFullMessage(String.format("removeMovieSlots movieId:%s, movieName:%s",movieId,movieName),message.getMessage()));
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
            String slots = Message.generateMessageFromString(admin.listMovieShowsAvailability(movieName)).extractMessage();
            if(slots.equals("")){
                System.out.println("No slots found for " + movieName + ".");
            }
            else{
                for(String slot: slots.split(Commands.DELIMITER)){
                    printMovieAvailability(slot);
                }
            }
            LOGGER.info(Logger.getFullMessage(String.format("listMovieShowsAvailability movieName:%s",movieName),slots));
        }
        catch (Exception e){
            System.out.println("Something went wrong form our side, please try again later.");
        }
    }
    public void start(){
        while (true){
            int option = Input.getIntegerInRange(menu,1,8);
            if(option == 8){
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
                        case 7:
                            customerAPI.exchangeTickets();
                            break;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
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

    public static org.omg.CORBA.Admin getAdminRemoteObjectByPrefix(String prefix){
        org.omg.CORBA.Admin admin = null;
        try {
            ORB orb = ORB.init("-ORBInitialPort 1051 -ORBInitialHost localhost".split(" "), null);
            org.omg.CORBA.Object objRef =  orb.resolve_initial_references("NameService");

            // get the root naming context
            // Use NamingContextExt instead of NamingContext. This is
            // part of the Interoperable naming Service.
            NamingContextExt ncRef = NamingContextExtHelper.narrow(objRef);

            // resolve the Object Reference in Naming
            admin = AdminHelper.narrow(ncRef.resolve_str("admin"+prefix));
        } catch (InvalidName e) {
            throw new RuntimeException(e);
        } catch (org.omg.CosNaming.NamingContextPackage.InvalidName e) {
            throw new RuntimeException(e);
        } catch (CannotProceed e) {
            throw new RuntimeException(e);
        } catch (NotFound e) {
            throw new RuntimeException(e);
        }
        return admin;
    }

}
