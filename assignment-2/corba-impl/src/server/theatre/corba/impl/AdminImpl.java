/**
 *
 * @project Distributed Movie Ticket Booking System
 * @author Mayur Jodhani
 * @version 1.0.0
 * @since 2023-01-24
 */
package server.theatre.corba.impl;
import org.omg.CORBA.AdminPOA;
import org.omg.CORBA.ORB;
import server.theatre.Theatre;
import server.theatre.movie.Movie;
import server.theatre.query.Actions;
import server.theatre.query.Query;
import shared.Commands;
import shared.Message;
import shared.Role;

import java.io.IOException;

public class AdminImpl extends AdminPOA {
    private ORB orb;

    public void setOrb(ORB orb) {
        this.orb = orb;
    }

    @Override
    public String addMovieSlots(String movieID, String movieName, int bookingCapacity) {
        Message response;
        try {
            if(Theatre.isLocalServer(movieID)){
                response = Message.generateMessageFromString(Actions.addMovieSlotByLocal(movieID,movieName,bookingCapacity));
            }
            else{
                // SEND COMMAND TO REMOTE SEVER BASED ON PREFIX AND GET RESPONSE
                response = Message.generateMessageFromString(Query.executeCommandByServerPrefix(Commands.getAddMovieSlotsQuery(movieID,movieName,bookingCapacity), Role.getLocationPrefix(movieID)));
            }
        } catch (Exception e) {
            response = Message.getErrorMessage(e.getMessage());
        }
        return response.getMessage();
    }

    @Override
    public String removeMovieSlots(String movieID, String movieName) {
        Message response;
        try {
            if(Theatre.isLocalServer(movieID)){
                response = Message.generateMessageFromString(Actions.removeMovieSlotByLocal(movieID,movieName));
            }
            else {
                // SEND COMMAND TO REMOTE SEVER BASED ON PREFIX AND GET RESPONSE
                response = Message.generateMessageFromString(Query.executeCommandByServerPrefix(Commands.removeMovieSlots(movieID,movieName),Role.getLocationPrefix(movieID)));
            }
        } catch (Exception e) {
            response = Message.getErrorMessage(e.getMessage());
        }
        return response.getMessage();
    }

    @Override
    public String listMovieShowsAvailability(String movieName) {
        final String[] result = {""};
        Message response;
        Thread[] threads = new Thread[Theatre.locationPorts.keySet().size()-1];
        try{
            Movie movie = Theatre.getMovies().get(movieName);
            if(movie == null){
//                return Message.getErrorMessage(("No movie found by name of " + movieName));
            }else{
                result[0] = Commands.generateCommandFromParams(movie.getSeats());
            }
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
                                Message remoteMessage = Message.generateMessageFromString(Query.executeCommandByServerPrefix(Commands.listMovieShowsAvailability(movieName), Role.getLocationPrefix(prefix)));
                                remoteResponse = remoteMessage.extractMessage();
                                if(!remoteResponse.equals("")){
                                    result[0] += Commands.DELIMITER + remoteResponse;
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
            response = Message.getSuccessMessage(result[0]);
        }catch (Exception e){
            response = Message.getErrorMessage(e.getLocalizedMessage());
        }
        return response.getMessage();
    }
}
