/**
 *
 * @project Distributed Movie Ticket Booking System
 * @author Mayur Jodhani
 * @version 1.0.0
 * @since 2023-01-24
 */
package server.theatre.rmi;

import server.theatre.Theatre;
import server.theatre.movie.Movie;
import server.theatre.query.Actions;
import server.theatre.query.Query;
import shared.*;

import java.io.IOException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class AdminImpl extends UnicastRemoteObject implements Admin {

    public AdminImpl() throws RemoteException {
        super();
    }

    @Override
    public String addMovieSlots(String movieID, String movieName, int bookingCapacity) throws RemoteException {
        String  result = "";
        try {
            if(Theatre.isLocalServer(movieID)){
                result = Actions.addMovieSlotByLocal(movieID,movieName,bookingCapacity);
            }
            else{
                // SEND COMMAND TO REMOTE SEVER BASED ON PREFIX AND GET RESPONSE
                result = Query.executeCommandByServerPrefix(Commands.getAddMovieSlotsQuery(movieID,movieName,bookingCapacity), Role.getLocationPrefix(movieID));
            }
        } catch (Exception e) {
            result = Message.getErrorMessage(e.getMessage());
        }
        return result;
    }

    @Override
    public String removeMovieSlots(String movieID, String movieName) throws RemoteException {
        String  result = "";
        try {
            if(Theatre.isLocalServer(movieID)){
                result = Actions.removeMovieSlotByLocal(movieID,movieName);
            }
            else {
                // SEND COMMAND TO REMOTE SEVER BASED ON PREFIX AND GET RESPONSE
                result = Query.executeCommandByServerPrefix(Commands.removeMovieSlots(movieID,movieName),Role.getLocationPrefix(movieID));
                // TODO PROCESS THE RESPONSE AND BIND TO RMI
            }
        } catch (Exception e) {
            result = Message.getErrorMessage(e.getMessage());
        }
        return result;
    }

    @Override
    public String listMovieShowsAvailability(String movieName) throws RemoteException {
        final String[] result = {""};
        try{
            Movie movie = Theatre.getMovies().get(movieName);
            if(movie == null){
//                return Message.getErrorMessage(("No movie found by name of " + movieName));
            }else{
                result[0] = Commands.generateCommandFromParams(movie.getSeats());
            }
            Thread[] threads = new Thread[Theatre.locationPorts.keySet().size()-1];
            int index = 0;
            for(String prefix : Theatre.locationPorts.keySet()){
                if(prefix.toLowerCase().equals(Theatre.getMetaData().getPrefix().toLowerCase())){
                    continue;
                }
                else{
                    // SEND COMMAND TO REMOTE SEVER BASED ON PREFIX AND GET RESPONSE
                    threads[index++] = new Thread(){
                        @Override
                        public void run() {
                            super.run();
                            String remoteResponse = null;
                            try {
                                remoteResponse = Query.executeCommandByServerPrefix(Commands.listMovieShowsAvailability(movieName), Role.getLocationPrefix(prefix));
                                if(!remoteResponse.equals("")){
                                    result[0] += Commands.DELIMITER+remoteResponse;
                                }} catch (IOException e) {
                                throw new RuntimeException(e);
                            }
                        }
                    };
                    threads[index - 1].start();
                }
            }
            for(Thread t: threads){
                t.join();
            }
            result[0] = Message.getSuccessMessage(result[0]);
        }catch (Exception e){
            result[0] = Message.getErrorMessage(e.getLocalizedMessage());
        }
        return result[0];
    }
}
