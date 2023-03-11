package shared;

import javax.jws.WebMethod;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;

/**
 * @author mkjodhani
 * @version 2.0
 * @project
 * @since 11/03/23
 */
@WebService
@SOAPBinding(style = SOAPBinding.Style.RPC)
public interface Customer {
    @WebMethod
    String exchangeTickets(String customerID, String oldMovieName, String movieID, String newMovieID, String newMovieName, int numberOfTickets);
    @WebMethod
    String bookMovieTickets(String customerID, String movieID, String movieName, int numberOfTickets);
    @WebMethod
    String getBookingSchedule(String customerID);
    @WebMethod
    String cancelMovieTickets(String customerID, String movieID, String movieName, int numberOfTickets);
}
