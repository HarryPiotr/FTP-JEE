package api;

import java.util.List;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.json.*;
import jee.*;

@Path("/file")
public class FileAPI {

    HiberManager dbm = new HiberManager();

    private String determinePrivileges(UserEntity user) {
        String whichViewmode;
        if (user.getUsername().equalsIgnoreCase("anonim")) {
            whichViewmode = "anon";
        } else if (user.getIsPrivileged()) {
            whichViewmode = "PU";
        } else {
            whichViewmode = "SU";
        }
        return whichViewmode;
    }

    private Boolean canIModify(UserEntity user, FileEntity file) {
        Boolean verdict = false;
        if (user.getIsPrivileged()) {
            verdict = true;
        }
        if (!user.getIsPrivileged() && !user.getUsername().equals("anonim") && file.getOwner_id().equals(user.getId())) {
            verdict = true;
        }
        return verdict;
    }

    @GET
    @Path("/read")
    @Produces(MediaType.APPLICATION_JSON)
    public String getFiles(@QueryParam("category_id") Integer category_id, @HeaderParam("username") String user_username, @HeaderParam("password") String user_password) {

        UserEntity user_client;
        //Ustalamy, czy udało się zalogować
        if (user_username == null && user_password == null) {
            user_client = dbm.findUser("anonim");
        } else {
            user_client = dbm.findUser(user_username);

            if (user_client == null) {
                return "Nie znaleziono użytkownika lub błędne dane logowania";
            }
        }

        if (!(user_client.getUsername().equals("anonim")) && !(user_client.getPassword().equals(user_password))) {
            return "Nie znaleziono użytkownika lub błędne dane logowania";
        }

        //Ustalamy, jakie pliki może widzieć użytkownik
        String verdict = determinePrivileges(user_client);

        List<FileEntity> resultingFileList = null;

        //Postępujemy inaczej, jeżeli sprecyzowano kategorię
        if (category_id != null) {

            if (dbm.findCategory(category_id) == null) {
                return "Nie ma takiej kategorii";
            }

            switch (verdict) {
                case "SU":
                    resultingFileList = dbm.getSUFiles(category_id, user_client.getId());
                    break;
                case "PU":
                    resultingFileList = dbm.getPUFiles(category_id);
                    break;
                default:
                    resultingFileList = dbm.getEveryoneFiles(category_id);
                    break;
            }
            //Generujemy JSONa
            JSONArray jac = new JSONArray();
            JSONArray jaf = new JSONArray();
            JSONObject joc = new JSONObject(dbm.findCategory(category_id));
            jac.put(joc);
            joc.put("files", jaf);

            if (resultingFileList == null) {
                return jac.toString(3);
            } else {
                for (FileEntity f : resultingFileList) {
                    JSONObject jof = new JSONObject(f);
                    jof.remove("path");
                    jof.remove("category_id");
                    jof.remove("visibility");
                    jof.remove("readableTimestamp");
                    jaf.put(jof);
                }
                return jac.toString(3);
            }
        } else {

            //Generujemy JSONa
            JSONArray jac = new JSONArray();

            List<CategoryEntity> allCategories = dbm.getAllCategories();

            for (CategoryEntity cat : allCategories) {
                JSONObject joc = new JSONObject(cat);
                JSONArray jaf = new JSONArray();
                jac.put(joc);
                joc.put("files", jaf);

                List<FileEntity> thisCategoryFiles;
                switch (verdict) {
                    case "SU":
                        thisCategoryFiles = dbm.getSUFiles(cat.getId(), user_client.getId());
                        break;
                    case "PU":
                        thisCategoryFiles = dbm.getPUFiles(cat.getId());
                        break;
                    default:
                        thisCategoryFiles = dbm.getEveryoneFiles(cat.getId());
                        break;
                }
                if (thisCategoryFiles == null) {
                    continue;
                }
                for (FileEntity fil : thisCategoryFiles) {
                    JSONObject jof = new JSONObject(fil);
                    jof.remove("path");
                    jof.remove("category_id");
                    jof.remove("visibility");
                    jof.remove("readableTimestamp");
                    jaf.put(jof);
                }
            }
            return jac.toString(3);
        }
    }

    @GET
    @Path("/readall")
    @Produces(MediaType.APPLICATION_JSON)
    public String getAllFiles(@QueryParam("category_id") Integer category_id, @HeaderParam("username") String user_username, @HeaderParam("password") String user_password) {

        UserEntity user_client;
        //Ustalamy, czy udało się zalogować
        if (user_username == null && user_password == null) {
            user_client = dbm.findUser("anonim");
        } else {
            user_client = dbm.findUser(user_username);

            if (user_client == null) {
                return "Nie znaleziono użytkownika lub błędne dane logowania";
            }
        }

        if (!(user_client.getUsername().equals("anonim")) && !(user_client.getPassword().equals(user_password))) {
            return "Nie znaleziono użytkownika lub błędne dane logowania";
        }

        //Ustalamy, jakie pliki może widzieć użytkownik
        String verdict = determinePrivileges(user_client);

        List<FileEntity> resultingFileList = null;

        //Postępujemy inaczej, jeżeli sprecyzowano kategorię
        if (category_id != null) {

            if (dbm.findCategory(category_id) == null) {
                return "Nie ma takiej kategorii";
            }

            switch (verdict) {
                case "SU":
                    resultingFileList = dbm.getSUFiles(category_id, user_client.getId());
                    break;
                case "PU":
                    resultingFileList = dbm.getPUFiles(category_id);
                    break;
                default:
                    resultingFileList = dbm.getEveryoneFiles(category_id);
                    break;
            }
            //Generujemy JSONa
            JSONArray jaf = new JSONArray();

            if (resultingFileList == null) {
                return jaf.toString(3);
            }

            for (FileEntity f : resultingFileList) {
                JSONObject jof = new JSONObject(f);
                jof.remove("path");
                jof.remove("visibility");
                jof.remove("readableTimestamp");
                jaf.put(jof);
            }
            return jaf.toString(3);
        } else {
            switch (verdict) {
                case "anon":
                    resultingFileList = dbm.getEveryoneFiles();
                    break;
                case "SU":
                    resultingFileList = dbm.getSUFiles(user_client.getId());
                    break;
                case "PU":
                    resultingFileList = dbm.getPUFiles();
                    break;
            }
            //Generujemy JSONa
            JSONArray jaf = new JSONArray();

            for (FileEntity f : resultingFileList) {
                JSONObject jof = new JSONObject(f);
                jof.remove("path");
                jof.remove("visibility");
                jof.remove("readableTimestamp");
                jaf.put(jof);
            }
            return jaf.toString(3);
        }
    }

    @GET
    @Path("/count")
    @Produces(MediaType.TEXT_PLAIN)
    public Response getFileCount(@HeaderParam("username") String user_username, @HeaderParam("password") String user_password) {

        UserEntity user_client;
        //Ustalamy, czy udało się zalogować
        if (user_username == null && user_password == null) {
            user_client = dbm.findUser("anonim");
        } else {
            user_client = dbm.findUser(user_username);

            if (user_client == null) {
                return Response.status(401).entity("Nie znaleziono użytkownika lub błędne dane logowania").build();
            }
        }

        if (!(user_client.getUsername().equals("anonim")) && !(user_client.getPassword().equals(user_password))) {
            return Response.status(401).entity("Nie znaleziono użytkownika lub błędne dane logowania").build();
        }

        //Ustalamy, jakie pliki może widzieć użytkownik
        String verdict = determinePrivileges(user_client);

        Integer resultingNumber;

        switch (verdict) {
            case "SU":
                resultingNumber = dbm.getSUFilesCount(user_client.getId());
                break;
            case "PU":
                resultingNumber = dbm.getPUFilesCount();
                break;
            default:
                resultingNumber = dbm.getEveryoneFilesCount();
                break;
        }

        return Response.status(200).entity(resultingNumber).build();
    }

    @GET
    @Path("/help")
    @Produces(MediaType.TEXT_PLAIN)
    public String getSpecificUser() {
        String response = "";
        response += "Rest API dotyczące Plików\n";
        response += "Dostępne funkcjonalności:\n\n";
        response += "GET /rest/file/read\n";
        response += "   Header:\n";
        response += "       username:{login}\n";
        response += "       password:{hasło}\n";
        response += "   Query:\n";
        response += "       category_id={category_id}:\n\n";
        response += "GET /rest/file/readall\n";
        response += "   Header:\n";
        response += "       username:{login}\n";
        response += "       password:{hasło}\n";
        response += "   Query:\n";
        response += "       category_id={category_id}:\n\n";
        response += "GET /rest/file/count\n";
        response += "   Header:\n";
        response += "       username:{login}\n";
        response += "       password:{hasło}\n\n";
        response += "GET /rest/file/help\n\n";
        response += "PUT /rest/file/update/{file_id}\n";
        response += "   Header:\n";
        response += "       Content-Type:application/json\n";
        response += "       username:{login}\n";
        response += "       password:{hasło}\n";
        response += "   Body:\n";
        response += "       JSON - pola:\n";
        response += "           *Integer file_id\n";
        response += "           String name\n";
        response += "           Integer category_id\n";
        response += "           String visibility={\"everyone\",\"registered\",\"me\"}\n\n";
        response += "DELETE /rest/file/delete/{file_id}\n";
        response += "   Header:\n";
        response += "       username:{login}\n";
        response += "       password:{hasło}\n";
        return response;
    }

    @PUT
    @Path("/update")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response UpdateFile(@HeaderParam("username") String user_username, @HeaderParam("password") String user_password, FileEntity toUpdate) {

        UserEntity user_client;
        //Ustalamy, czy udało się zalogować
        if (user_username == null && user_password == null) {
            user_client = dbm.findUser("anonim");
        } else {
            user_client = dbm.findUser(user_username);

            if (user_client == null) {
                return Response.status(401).entity("Nie znaleziono użytkownika lub błędne dane logowania").build();
            }
        }
        if (!(user_client.getUsername().equals("anonim")) && !(user_client.getPassword().equals(user_password))) {
            return Response.status(401).entity("Nie znaleziono użytkownika lub błędne dane logowania").build();
        }
        FileEntity targetFile = dbm.getFile(toUpdate.getId());
        if(targetFile == null) return Response.status(400).entity("Nie ma takiego pliku").build();
        if(!(canIModify(user_client, targetFile))) return Response.status(403).entity("Nie masz uprawnień do modyfikacji tego pliku").build();
        
        FileEntity toBeUpdated = dbm.getFile(toUpdate.getId());
        if(toUpdate.getName() != null) toBeUpdated.setName(toUpdate.getName());
        if(toUpdate.getVisibility() != null) toBeUpdated.setVisibility(toUpdate.getVisibility());
        if(!(toUpdate.getCategory_id().equals(toBeUpdated.getCategory_id()))) toBeUpdated.setCategory_id(toUpdate.getCategory_id());
        
        dbm.updateFile(toBeUpdated);
        return Response.status(200).entity("Zmodyfikowano plik " + dbm.getFile(toBeUpdated.getId()).getName()).build();
        
    }

    @DELETE
    @Path("/delete/{id}")
    @Produces(MediaType.TEXT_PLAIN)
    public Response deleteFile(@PathParam("id") Integer file_id,
            @HeaderParam("username") String user_username,
            @HeaderParam("password") String user_password
    ) {

        UserEntity user_client;
        //Sprawdzamy, czy użytkownik uwierzytelnia się do PU
        try {
            user_client = dbm.findUser(user_username);

        } catch (Throwable e) {
            return Response.status(400).entity("Nie znaleziono użytkownika lub usługa niedostępna").build();
        }
        
        if (user_client != null && user_client.getPassword().equals(user_password)) {
            if(!user_client.getIsPrivileged()) return Response.status(401).entity("Użytkownik nie ma uprawnień do usuwania plików").build();
            //Próbujemy usunąć zasób
            try {
                FileEntity fileToRemove = dbm.getFile(file_id);
                dbm.removeFile(fileToRemove);
                return Response.status(200).entity("Pomyślnie usunięto plik").build();
            } catch (Throwable e) {
                return Response.status(400).entity("Nie udało się usunąć pliku").build();
            }
        } else {
            return Response.status(401).entity("Błąd uwierzytelniania").build();
        }
    }
}
