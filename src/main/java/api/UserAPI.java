package api;

import java.util.List;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.json.*;
import jee.*;

@Path("/user")
public class UserAPI {

    HiberManager dbm = new HiberManager();

    @GET
    @Path("/read")
    @Produces(MediaType.APPLICATION_JSON)
    public String getAllUsers() {

        List<UserEntity> userlist = dbm.getAllUsers();
        JSONArray response = new JSONArray();

        for (UserEntity e : userlist) {
            JSONObject u = new JSONObject(e);
            //Nie ujawniamy haseł po API
            u.remove("password");
            response.put(u);
        }

        return response.toString(3);
    }

    @GET
    @Path("/read/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public String getSpecificUser(@PathParam("id") Integer user_id) {

        UserEntity userlist = dbm.findUser(user_id);
        JSONObject response = new JSONObject(userlist);
        //Nie ujawniamy haseł po API
        response.remove("password");

        return response.toString(3);
    }
    
    @GET
    @Path("/count")
    @Produces(MediaType.TEXT_PLAIN)
    public String getUserCount() {
        
        Integer response = dbm.countAllUsers();
        return response.toString();
    }
    
    @GET
    @Path("/help")
    @Produces(MediaType.TEXT_PLAIN)
    public String getSpecificUser() {
        String response = "";
        response += "Rest API dotyczące Użytkowników\n";
        response += "Dostępne funkcjonalności:\n\n";
        response += "GET /rest/user/read\n\n";
        response += "GET /rest/user/read/{user_id}\n\n";
        response += "GET /rest/user/count\n\n";
        response += "GET /rest/user/help\n\n";
        response += "POST /rest/user/save\n";
        response += "   Header:\n";
        response += "       Content-Type:application/json\n";
        response += "       username:{login}\n";
        response += "       password:{hasło}\n";
        response += "   Body:\n";
        response += "       JSON - wymagane pola:\n";
        response += "           String username\n";
        response += "           String password\n";
        response += "           Boolean isPrivileged\n";
        return response;
    }
    
    @POST
    @Path("/save")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response addUser(UserEntity userToAdd, @HeaderParam("username") String user_username, @HeaderParam("password") String user_password) {

        UserEntity user_client;
        //Sprawdzamy, czy użytkownik uwierzytelnia się do PU
        try {
            user_client = dbm.findUser(user_username);

        } catch (Throwable e) {
            return Response.status(400).entity("Nie znaleziono użytkownika lub usługa niedostępna").build();
        }

        if (user_client != null && user_client.getPassword().equals(user_password) && user_client.getIsPrivileged()) {
            //Próbujemy dodać dokumentację
            try {
                dbm.saveUser(userToAdd);
                return Response.status(200).entity("Pomyślnie dodano: " + userToAdd.getUsername() + "(" + userToAdd.getId() + ")").build();
            } catch (Throwable e) {
                return Response.status(400).entity("Nie udało się dodać dokumentacji").build();
            }
        }
        else return Response.status(401).entity("Błąd uwierzytelniania").build();
    }
}
