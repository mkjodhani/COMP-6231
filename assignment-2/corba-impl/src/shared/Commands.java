/**
 *
 * @project Distributed Movie Ticket Booking System
 * @author Mayur Jodhani
 * @version 1.0.0
 * @since 2023-01-24
 */
package shared;

public class Commands {
    public static final String AKG_INIT_BETA_SERVER = "AKG_INIT_BETA_SERVER";
    public static final String ADD_MOVIE_SLOT = "ADD_MOVIE_SLOT";
    public static final String REMOVE_MOVIE_SLOT = "REMOVE_MOVIE_SLOT";
    public static final String LIST_MOVIE_AVAILABILITY = "LIST_MOVIE_AVAILABILITY";
    public static final String BOOK_MOVIE_TICKET = "BOOK_MOVIE_TICKET";
    public static final String GET_CUSTOMER_SCHEDULE = "GET_CUSTOMER_SCHEDULE";
    public static final String CANCEL_MOVIE_TICKET = "CANCEL_MOVIE_TICKET";
    public static final String GET_AVAILABLE_SEATS_BY_SLOT_ID = "GET_AVAILABLE_SEATS_BY_SLOT_ID";
    public static final String GET_CX_BOOKED_SEATS_FOR_SLOT_ID = "GET_CX_BOOKED_SEATS_FOR_SLOT_ID";
    public static final String DELIMITER = ",";

    public static String generateCommandFromParams(String[] args){
        return String.join(DELIMITER,args);
    }
    public static String[] generateParamsFromCommand(String command){
        return command.split(DELIMITER);
    }
    public static final String getAddMovieSlotsQuery(String movieID, String movieName, int bookingCapacity)  {
        String[] args = new String[]{ADD_MOVIE_SLOT,movieID,movieName,String.valueOf(bookingCapacity)};
        return Commands.generateCommandFromParams(args);
    }
    public static final String removeMovieSlots(String movieID, String movieName){
        String[] args = new String[]{REMOVE_MOVIE_SLOT,movieID,movieName};
        return Commands.generateCommandFromParams(args);
    }
    public static final String listMovieShowsAvailability(String movieName) {
        String[] args = new String[]{LIST_MOVIE_AVAILABILITY,movieName};
        return Commands.generateCommandFromParams(args);
    }
    public static final String getBookingScheduleCommand(String customerId) {
        String[] args = new String[]{GET_CUSTOMER_SCHEDULE,customerId};
        return Commands.generateCommandFromParams(args);
    }
    public static final String addMovieTicketsCommand(String customerID, String movieID, String movieName, int numberOfTickets) {
        String[] args = new String[]{BOOK_MOVIE_TICKET,customerID,movieID,movieName,String.valueOf(numberOfTickets)};
        return Commands.generateCommandFromParams(args);
    }
    public static final String cancelMovieTicketsCommand(String customerID, String movieID, String movieName, int numberOfTickets) {
        String[] args = new String[]{CANCEL_MOVIE_TICKET,customerID,movieID,movieName,String.valueOf(numberOfTickets)};
        return Commands.generateCommandFromParams(args);
    }
    public static final String getAvailableSeatsByMovieCommand(String movieID, String movieName) {
        String[] args = new String[]{GET_AVAILABLE_SEATS_BY_SLOT_ID,movieID,movieName};
        return Commands.generateCommandFromParams(args);
    }
    public static final String getCustomerSeatsByMovieSlotCommand(String customerID,String movieID, String movieName) {
        String[] args = new String[]{GET_CX_BOOKED_SEATS_FOR_SLOT_ID,customerID,movieID,movieName};
        return Commands.generateCommandFromParams(args);
    }
}
