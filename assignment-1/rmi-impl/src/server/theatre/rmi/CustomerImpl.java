/**
 *
 * @project Distributed Movie Ticket Booking System
 * @author Mayur Jodhani
 * @version 1.0.0
 * @since 2023-01-24
 */
package server.theatre.rmi;

import server.theatre.Theatre;
import server.theatre.query.Actions;
import server.theatre.query.Query;
import shared.Commands;
import shared.Customer;
import shared.Message;
import shared.Role;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;

public class CustomerImpl extends UnicastRemoteObject implements Customer {
    public CustomerImpl() throws RemoteException {
    }

    @Override
    public String bookMovieTickets(String customerID, String movieID, String movieName, int numberOfTickets) throws RemoteException {
        if(Theatre.isLocalServer(movieID)){
            return Actions.addMovieTicketsByLocal(customerID,movieID,movieName,numberOfTickets);
        }
        else{
            try{
                String remoteResponse = Query.executeCommandByServerPrefix(Commands.addMovieTickets(customerID, movieID, movieName, numberOfTickets), Role.getLocationPrefix(movieID));
                return remoteResponse;
            }
            catch (Exception e){
                e.printStackTrace();
                return Message.getErrorMessage("Something went wrong, please try again later.");
            }
        }
    }

    @Override
    public String getBookingSchedule(String customerID) throws RemoteException {
        ArrayList<String> response = new ArrayList<>();
        try{
            String localResponse = Actions.getBookingSchedule(Commands.getBookingScheduleCommand(customerID));
            if(localResponse != ""){
                response.add(localResponse);
            }
            for(String prefix : Theatre.locationPorts.keySet()){
                if(prefix.toLowerCase().equals(Theatre.getMetaData().getPrefix().toLowerCase())){
                    continue;
                }
                else{
                    // SEND COMMAND TO REMOTE SEVER BASED ON PREFIX AND GET RESPONSE
                    String remoteResponse = Query.executeCommandByServerPrefix(Commands.getBookingScheduleCommand(customerID), Role.getLocationPrefix(prefix));
                    if(!remoteResponse.equals("")){
                        response.add(remoteResponse);
                    }
                }
            }
        }catch (Exception e){
            e.printStackTrace();
            return Message.getErrorMessage(e.getLocalizedMessage());
        }
        return String.join(Commands.DELIMITER,response);
    }

    @Override
    public String cancelMovieTickets(String customerID, String movieID, String movieName, int numberOfTickets) throws RemoteException {
        if(Theatre.isLocalServer(movieID)){
//            TODO EXECUTE THE LOGIC TO HANDLE THE ACTION
            return Actions.cancelMovieTicketsByLocal(customerID,movieID,movieName,numberOfTickets);
        }
        else{
            try{
                String remoteResponse = Query.executeCommandByServerPrefix(Commands.cancelMovieTickets(customerID, movieID, movieName, numberOfTickets), Role.getLocationPrefix(movieID));
                return remoteResponse;
            }
            catch (Exception e){
                e.printStackTrace();
                return Message.getErrorMessage("Something went wrong, please try again later.");
            }
        }
    }
}
