/**
 *
 * @project Distributed Movie Ticket Booking System
 * @author Mayur Jodhani
 * @version 1.0.0
 * @since 2023-01-24
 */
package shared;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface Customer extends Remote {
    /**
     * When a customer invokes this method from his/her area through the server associated with this customer
     * (determined by the unique customerID prefix) attempts to book the numberOfTickets tickets for a particular movie show
     * for the customer and change the capacity left for that movie show. Also, an appropriate message is displayed to
     * the customer whether the booking was successful or not and both the server and the client stores this information in their logs.
     * @param customerID
     * @param movieID
     * @param movieName
     * @param numberOfTickets
     * @return
     * @throws RemoteException
     */
    public String bookMovieTickets (String customerID, String movieID, String movieName, int numberOfTickets) throws RemoteException;

    /**
     * When a customer invokes this method from his/her area through the server associated with this customer,
     * that area’s server gets all the movie tickets for a particular show booked by the customer and display them on the console.
     * Here, bookings from all the area’s theatre such as Atwater, Outremont and Verdun should be displayed.
     * @param customerID
     * @return
     * @throws RemoteException
     */
    public String getBookingSchedule (String customerID) throws RemoteException;

    /**
     * When a customer invokes this method from his/her area’s theatre through the server associated with this customer
     * (determined by the unique customerID prefix) searches the hash map to find the movieID with the movieName and cancel
     * the numberOfTickets tickets for that movie show. Upon success or failure, it returns a message to the customer and
     * the logs are updated with this information. It is required to check that the movie ticket can only be canceled
     * if it was booked by the same customer who sends cancel request
     * @param customerID
     * @param movieID
     * @param movieName
     * @param numberOfTickets
     * @return
     * @throws RemoteException
     */
    public String cancelMovieTickets (String customerID,String movieID,String movieName,int numberOfTickets) throws RemoteException;

}
