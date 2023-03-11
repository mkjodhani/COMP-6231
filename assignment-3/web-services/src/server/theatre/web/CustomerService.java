package server.theatre.web;

import server.theatre.Theatre;
import server.theatre.movie.Movie;
import server.theatre.query.Actions;
import server.theatre.query.Query;
import shared.Commands;
import shared.Customer;
import shared.Message;
import shared.Role;

import javax.jws.WebService;
import java.io.IOException;
import java.util.ArrayList;

/**
 * @author mkjodhani
 * @project
 * @since 11/03/23
 */

@WebService(endpointInterface = "shared.Customer")
public class CustomerService implements Customer {
    @Override
    public String exchangeTickets(String customerID, String oldMovieName, String movieID, String newMovieID, String newMovieName, int numberOfTickets) {
        Message response = null;
        // whether the cx has booked tickets for oldMovieName and movieID or not
        boolean isTicketExists = false;
        // whether the tickets are available to book for newMovieName and newMovieID or not
        boolean isTicketsAvailable = false;
        // calculate isTicketExists value
        if(Theatre.isLocalServer(movieID)){
            isTicketExists = Movie.getTotalTicketsFromCustomerID(customerID,oldMovieName,movieID) >= numberOfTickets;
        }
        else {
            try {
                Message remoteResponse = Message.generateMessageFromString(Query.executeCommandByServerPrefix(Commands.getCustomerSeatsByMovieSlotCommand(customerID,movieID,oldMovieName), Role.getLocationPrefix(movieID)));
                isTicketExists = Integer.parseInt(remoteResponse.extractMessage()) >= numberOfTickets;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        // calculate isTicketExists value
        if(Theatre.isLocalServer(newMovieID)){
            isTicketsAvailable = Movie.getAvailableTicketsForSlot(newMovieName,newMovieID) >= numberOfTickets;
        }
        else {
            try {
                Message remoteResponse = Message.generateMessageFromString(Query.executeCommandByServerPrefix(Commands.getAvailableSeatsByMovieCommand(newMovieID,newMovieName), Role.getLocationPrefix(newMovieID)));
                isTicketsAvailable = Integer.parseInt(remoteResponse.extractMessage()) >= numberOfTickets;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        try{
            if(!isTicketExists){
                response = Message.getErrorMessage(numberOfTickets + String.format(" tickets are not booked at %s(%s) so it can not be exchanged to %s(%s).",movieID,oldMovieName,newMovieID,newMovieName));
            }
            else if(!isTicketsAvailable){
                response = Message.getErrorMessage(numberOfTickets + String.format(" tickets are not available for booking at %s(%s).",newMovieID,newMovieName));
            }
            else{
                Message remoteResponse;
                remoteResponse = Message.generateMessageFromString(Theatre.getCustomer().bookMovieTickets(customerID,newMovieID,newMovieName,numberOfTickets));
                if(remoteResponse.getType().equals(Message.SUCCESS)){
                    remoteResponse = Message.generateMessageFromString(Theatre.getCustomer().cancelMovieTickets(customerID,movieID,oldMovieName,numberOfTickets));
                    if(remoteResponse.getType().equals(Message.SUCCESS)){
                        response = Message.getErrorMessage(numberOfTickets + String.format(" tickets are exchanged from %s(%s) to %s(%s).",movieID,oldMovieName,newMovieID,newMovieName));
                    }
                    else{
                        Theatre.getCustomer().cancelMovieTickets(customerID,newMovieID,newMovieName,numberOfTickets);
                        response = Message.getErrorMessage("Something went wrong!");
                    }
                }
                else{
                    response = Message.getErrorMessage("Something went wrong!");
                }
            }
        }
        catch (Exception e){
            e.printStackTrace();
            response = Message.getErrorMessage(e.getMessage());
        }
        return response.getMessage();
    }

    @Override
    public String bookMovieTickets(String customerID, String movieID, String movieName, int numberOfTickets) {
        Message response;
        if(Theatre.isLocalServer(movieID)){
            response = Message.generateMessageFromString(Actions.addMovieTicketsByLocal(customerID,movieID,movieName,numberOfTickets));
        }
        else{
            try{
                response = Message.generateMessageFromString(Query.executeCommandByServerPrefix(Commands.addMovieTicketsCommand(customerID, movieID, movieName, numberOfTickets), Role.getLocationPrefix(movieID)));
            }
            catch (Exception e){
                response = Message.getErrorMessage("Something went wrong, please try again later.");
            }
        }
        return response.getMessage();
    }

    @Override
    public String getBookingSchedule(String customerID) {
        ArrayList<String> response = new ArrayList<>();
        Message message;
        try{
            String localResponse = Message.generateMessageFromString(Actions.getBookingSchedule(Commands.getBookingScheduleCommand(customerID))).extractMessage();
            if(localResponse.equals("")){
                response.add(localResponse);
            }
            for(String prefix : Theatre.locationPorts.keySet()){
                if(prefix.toLowerCase().equals(Theatre.getMetaData().getPrefix().toLowerCase())){
                    continue;
                }
                else{
                    // SEND COMMAND TO REMOTE SEVER BASED ON PREFIX AND GET RESPONSE
                    Message message1 = Message.generateMessageFromString(Query.executeCommandByServerPrefix(Commands.getBookingScheduleCommand(customerID), Role.getLocationPrefix(prefix)));
                    String remoteResponse =  message1.extractMessage();
                    if(!remoteResponse.equals("")){
                        response.add(remoteResponse);
                    }
                }
            }
            message = Message.getSuccessMessage(String.join(Commands.DELIMITER,response));
        }catch (Exception e){
            e.printStackTrace();
            message =  Message.getErrorMessage(e.getLocalizedMessage());
        }
        return message.getMessage();
    }

    @Override
    public String cancelMovieTickets(String customerID, String movieID, String movieName, int numberOfTickets) {
        Message response;
        if(Theatre.isLocalServer(movieID)){
            response = Message.generateMessageFromString(Actions.cancelMovieTicketsByLocal(customerID,movieID,movieName,numberOfTickets));
        }
        else{
            try{
                response = Message.generateMessageFromString(Query.executeCommandByServerPrefix(Commands.cancelMovieTicketsCommand(customerID, movieID, movieName, numberOfTickets), Role.getLocationPrefix(movieID)));
            }
            catch (Exception e){
                response = Message.getErrorMessage("Something went wrong, please try again later.");
            }
        }
        return response.getMessage();
    }
}
