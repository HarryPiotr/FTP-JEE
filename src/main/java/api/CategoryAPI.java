package api;

import java.util.List;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
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

@Path("/category")
public class CategoryAPI {

    HiberManager dbm = new HiberManager();

    @GET
    @Path("/read")
    @Produces(MediaType.APPLICATION_JSON)
    public String getAllCategories() {

        List<CategoryEntity> categorylist = dbm.getAllCategories();
        JSONArray response = new JSONArray();

        for (CategoryEntity c : categorylist) {
            JSONObject u = new JSONObject(c);
            response.put(u);
        }

        return response.toString(3);
    }

    @GET
    @Path("/read/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public String getSpecificCategory(@PathParam("id") Integer category_id) {

        CategoryEntity categorylist = dbm.findCategory(category_id);
        JSONObject response = new JSONObject(categorylist);

        return response.toString(3);
    }

    @GET
    @Path("/count")
    @Produces(MediaType.TEXT_PLAIN)
    public String getCategoryCount() {

        Integer response = dbm.countAllCategories();
        return response.toString();
    }

    @GET
    @Path("/help")
    @Produces(MediaType.TEXT_PLAIN)
    public String getHelp() {
        String response = "";
        response += "Rest API dotyczące Kategorii\n";
        response += "Dostępne funkcjonalności:\n\n";
        response += "GET /rest/category/read\n\n";
        response += "GET /rest/category/read/{category_id}\n\n";
        response += "GET /rest/category/count\n\n";
        response += "GET /rest/category/help\n\n";
        response += "POST /rest/category/save\n";
        response += "   Header:\n";
        response += "       Content-Type:application/json\n";
        response += "       username:{login}\n";
        response += "       password:{hasło}\n";
        response += "   Body:\n";
        response += "       JSON - wymagane pola:\n";
        response += "           String name\n\n";
        response += "DELETE /rest/category/delete/{category_id}\n";
        response += "   Header:\n";
        response += "       username:{login}\n";
        response += "       password:{hasło}\n";
        return response;
    }

    @POST
    @Path("/save")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response addCategory(CategoryEntity categoryToAdd, @HeaderParam("username") String user_username, @HeaderParam("password") String user_password) {

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
                dbm.saveCategory(categoryToAdd);
                return Response.status(200).entity("Pomyślnie dodano: " + categoryToAdd.getName() + "(" + categoryToAdd.getId() + ")").build();
            } catch (Throwable e) {
                return Response.status(400).entity("Nie udało się dodać dokumentacji").build();
            }
        } else {
            return Response.status(401).entity("Błąd uwierzytelniania").build();
        }
    }

    @DELETE
    @Path("/delete/{id}")
    @Produces(MediaType.TEXT_PLAIN)
    public Response deleteCategory(@PathParam("id") Integer category_id, @HeaderParam("username") String user_username, @HeaderParam("password") String user_password) {

        UserEntity user_client;
        //Sprawdzamy, czy użytkownik uwierzytelnia się do PU
        try {
            user_client = dbm.findUser(user_username);

        } catch (Throwable e) {
            return Response.status(400).entity("Nie znaleziono użytkownika lub usługa niedostępna").build();
        }
        
        if (user_client != null && user_client.getPassword().equals(user_password)) {
            if(!user_client.getIsPrivileged()) return Response.status(401).entity("Użytkownik nie ma uprawnień do usuwania kategorii").build();
            //Próbujemy usunąć zasób
            try {
                CategoryEntity categoryToRemove = dbm.findCategory(category_id);
                dbm.removeCategory(categoryToRemove);
                return Response.status(200).entity("Pomyślnie usunięto kategorię").build();
            } catch (Throwable e) {
                return Response.status(400).entity("Nie udało się usunąć kategorii").build();
            }
        } else {
            return Response.status(401).entity("Błąd uwierzytelniania").build();
        }
    }
}
