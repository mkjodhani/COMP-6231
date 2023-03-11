/**
 *
 * @project Distributed Movie Ticket Booking System
 * @author Mayur Jodhani
 * @version 1.0.0
 * @since 2023-01-24
 */
package server.theatre.query;

import server.theatre.Theatre;
import server.theatre.movie.Movie;
import server.theatre.movie.Slot;
import shared.Commands;
import shared.Input;
import shared.Message;
import shared.Role;

import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Actions {
    static private Logger LOGGER = Logger.getLogger(Movie.class.getName());
    public static String addMovieSlot(String command) {
        String result = "";
        try {
            String[] args = command.split(Commands.DELIMITER);
            String movieName = args[2], movieID = args[1];
            int bookingCapacity = Integer.parseInt(args[3]);
            result = addMovieSlotByLocal(movieID, movieName, bookingCapacity);
            LOGGER.log(Level.SEVERE,String.format("Add movie slot using UDP command:%s",command));
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE,e.getMessage());
            result = e.getMessage();
        }
        return result;
    }

    public static String removeMovieSlot(String command) {
        String result = "";
        try {
            String[] args = command.split(Commands.DELIMITER);
            String movieName = args[2], movieID = args[1];
            result = removeMovieSlotByLocal(movieID, movieName);
            LOGGER.log(Level.INFO,String.format("Remove movie slot using UDP command : %s",command));
        } catch (Exception e) {
            e.printStackTrace();
            LOGGER.log(Level.SEVERE,e.getMessage());
            result = e.getMessage();
        }
        return result;
    }

    public static String checkAvailability(String command) {
        String result = "";
        try {
            String[] args = command.split(Commands.DELIMITER);
            // CMD MOVIE_NAME
            String movieName = args[1];
            Movie movie = Theatre.getMovies().get(movieName);
            if (movie == null) {
//                return Message.getErrorMessage(("No movie found by name of " + movieName));
            } else {
                result += Commands.generateCommandFromParams(movie.getSeats());
            }
            LOGGER.log(Level.SEVERE,String.format("Check availability using UDP command:%s",command));
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE,e.getMessage());
            result = e.getMessage();
        }
        return result;
    }

    public static final String getBookingSchedule(String command) {
        String[] args = Commands.generateParamsFromCommand(command);
        String customerID = args[1];
        ArrayList<String> response = new ArrayList<>();
        for (Movie movie : Theatre.getMovies().values()) {
            String ticket = movie.getCustomerTickets(customerID);
            if(ticket != ""){
                response.add(ticket);
            }
        }
        LOGGER.log(Level.INFO,String.format("Get booking schedule using UDP command:%s",command));
        return String.join(Commands.DELIMITER, response);
    }
    public static final String addMovieTickets(String command) {
        String[] args = command.split(Commands.DELIMITER);
        String customerID = args[1], movieID = args[2], movieName = args[3];
        int numberOfTickets = Integer.parseInt(args[4]);
        LOGGER.log(Level.INFO,String.format("Add movie tickets using UDP command:%s",command));
        ArrayList<String> tickets = new ArrayList<>();
        for(String serverPrefix : Theatre.locationPorts.keySet()){
            if(serverPrefix.toLowerCase().equals(Role.getLocationPrefix(customerID))){
                continue;
            }
            else if(serverPrefix.toLowerCase().equals(Role.getLocationPrefix(movieID)))
            {
                String localResponse = Actions.getBookingSchedule(Commands.getBookingScheduleCommand(customerID));
                for(String movieTicket: localResponse.split(Commands.DELIMITER)){
                    tickets.add(movieTicket);
                }
            }
            else{
                // SEND COMMAND TO REMOTE SEVER BASED ON PREFIX AND GET RESPONSE
                try {
                    String remoteResponse = Query.executeCommandByServerPrefix(Commands.getBookingScheduleCommand(customerID),serverPrefix);
                    for(String movieTicket: remoteResponse.split(Commands.DELIMITER)){
                        tickets.add(movieTicket);
                    }
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }
        if(tickets.size() > 3){
            return Message.getErrorMessage("You have already booked tickets three times for the next week.Please try to book a ticket later.");
        }
        return addMovieTicketsByLocal(customerID, movieID, movieName, numberOfTickets);
    }
    public static final String cancelMovieTickets(String command) {
        String[] args = command.split(Commands.DELIMITER);
        String customerID = args[1], movieID = args[2], movieName = args[3];
        int numberOfTickets = Integer.parseInt(args[4]);
        LOGGER.log(Level.SEVERE,String.format("Add movie tickets using UDP command:%s",command));
        return cancelMovieTicketsByLocal(customerID, movieID, movieName, numberOfTickets);
    }
    public static final String addMovieTicketsByLocal(String customerID, String movieID, String movieName, int numberOfTickets) {
        Movie movie = Theatre.getMovies().get(movieName);
        if(movie == null){
            return Message.getErrorMessage("Movie is not listed for " + movieName + " at " + Theatre.getMetaData().getLocation() + ".");
        }
        else{
            Slot selectedSlot = movie.getSlots().get(movieID);
            if(selectedSlot == null){
                return Message.getErrorMessage("Movie slot is not listed for " + movieID + "("+movieName+") at " + Theatre.getMetaData().getLocation() + ".");
            }
            else{
                if(selectedSlot.addTicket(customerID,numberOfTickets)){
                    return Message.getSuccessMessage(numberOfTickets+ " tickets are booked for " + movieName + " at " + Theatre.getMetaData().getLocation() + ".");
                }
                else{
                    return Message.getErrorMessage(String.format("%d tickets are not available for %s(%s) at %s.", numberOfTickets, movieName,selectedSlot.getSlotId(), Theatre.getMetaData().getLocation()));
                }
            }
        }
    }
    public static final String cancelMovieTicketsByLocal(String customerID, String movieID, String movieName, int numberOfTickets) {
        Movie movie = Theatre.getMovies().get(movieName);
        if(movie == null){
            return Message.getSuccessMessage("Movie is not listed for " + movieName + " at " + Theatre.getMetaData().getLocation() + ".");
        }
        else{
            Slot selectedSlot = movie.getSlots().get(movieID);
            if(selectedSlot == null){
                return Message.getSuccessMessage("Movie is not listed for " + movieName + " at " + Theatre.getMetaData().getLocation() + ".");
            }
            else{
                if(selectedSlot.cancelTickets(customerID,numberOfTickets)){
                    return Message.getSuccessMessage(numberOfTickets+ " tickets are cancelled  for " + movieName + " at " + Theatre.getMetaData().getLocation() + ".");
                }
                else{
                    return Message.getErrorMessage(numberOfTickets+ " tickets are not booked for " + movieName + " at " + Theatre.getMetaData().getLocation() + ".");
                }
            }
        }
    }
    public static String addMovieSlotByLocal(String movieID, String movieName, int bookingCapacity) throws Exception {
        String  result = "";
        Movie movie = Theatre.getMovies().get(movieName);
        if(movie == null){
            return Message.getErrorMessage(String.format("There is no movie named %s.",movieName));
//            movie = Movie.addMovie(movieName);
//            Theatre.getMovies().put(movieName,movie);
//            throw new Error(String.format("There is no movie named %s",movieName));
        }
        movie.addSlot(movieID,bookingCapacity);
        result = Message.getSuccessMessage(String.format("Movie slot for %s for %s is successfully created with %d capacity.",movieName,movieID,bookingCapacity));
        return result;
    }
    public static String removeMovieSlotByLocal(String movieID, String movieName) {
        String  result = "";
        Movie movie = Theatre.getMovies().get(movieName);
        if(movie == null){
            return Message.getErrorMessage("No movie found by name of " + movieName);
        }
        else{
            Slot deletedSlot = movie.removeSlot(movieID);
            if(deletedSlot == null){
                return Message.getErrorMessage("No slot found by ID of " + movieID);
            }
            else{
                String nextSlotID = Input.getNextAvailableSlotID(movie.getSlots().keySet(),movieID);
                if(nextSlotID == null) {
                    movie.getSlots().put(deletedSlot.getSlotId(),deletedSlot);
                    result = Message.getErrorMessage("No slots available to transfer the booked seats of " + movieID + "for " + movieName + ".");
                    LOGGER.log(Level.INFO,"Reverting the deletion operation for slot(" + deletedSlot.getSlotId()+ ").");
                }
                else{
                    Slot nextSlot = movie.getSlots().get(nextSlotID);
                    nextSlot.transferSeats(deletedSlot.getBookedSeats());
                    result = Message.getSuccessMessage(String.format("Movie slot for %s for %s is successfully deleted.Also the next slot(%s) is extended to %d tickets.",movieName,movieID,nextSlotID,deletedSlot.getBookedTickets()));
                }
            }
        }
        LOGGER.log(Level.INFO,Message.getMessage(result));
        return result;
    }

}
