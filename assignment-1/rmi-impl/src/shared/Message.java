/**
 * The program implements an application that
 * input from users and issues cheques.
 *
 * @author  Mayur Jodhani
 * @version 1.0
 * @since   2023-01-24
 */
package shared;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Message {
    public static final String SUCCESS = "SUCCESS";
    public static final String ERROR = "ERROR";
    private static final Pattern pattern = Pattern.compile("^(\\w+)(:::)(((\\w*)(\\W*))*)$");
    public static String getSuccessMessage(String message){
        return String.format("%s:::%s",SUCCESS,message);
    }
    public static String getErrorMessage(String message){
        return String.format("%s:::%s",ERROR,message);
    }

    public static String getMessage(String wrappedMessage){
        if(wrappedMessage == null){
            return "";
        }
        Matcher matcher = pattern.matcher(wrappedMessage);
        if(matcher.find()){
            return matcher.group(3);
        }
        else{
            return wrappedMessage;
        }
    }
    public static String getMessageType(String wrappedMessage){
        Matcher matcher = pattern.matcher(wrappedMessage);
        return matcher.group(1);
    }
}
