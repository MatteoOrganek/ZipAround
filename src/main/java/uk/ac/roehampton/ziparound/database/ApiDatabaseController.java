package uk.ac.roehampton.ziparound.database;

import java.net.URI;
import java.net.URLStreamHandler;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.Map;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import jdk.jshell.execution.Util;
import org.jetbrains.annotations.NotNull;
import uk.ac.roehampton.ziparound.Utils;
import uk.ac.roehampton.ziparound.booking.BookingManager;
import uk.ac.roehampton.ziparound.users.Customer;
import uk.ac.roehampton.ziparound.users.User;
import uk.ac.roehampton.ziparound.users.staff.Staff;
import uk.ac.roehampton.ziparound.users.staff.role.Admin;
import uk.ac.roehampton.ziparound.users.staff.role.BookingAgent;
import uk.ac.roehampton.ziparound.users.staff.role.Manager;
import uk.ac.roehampton.ziparound.users.staff.role.SelfService;

import java.lang.reflect.Type;
import java.io.IOException;
import java.util.List;
import java.util.Stack;

public class ApiDatabaseController {

    private final String apiBaseUrl;
    private final HttpClient client;
    private final Gson gson;
    private static ApiDatabaseController instance;

    private ApiDatabaseController() {
        this.apiBaseUrl = "https://owres.org/ziparound/";
        this.client = HttpClient.newHttpClient();
        this.gson = new Gson();
    }

    /**
     * Get the singleton instance.
     */
    public static synchronized ApiDatabaseController getInstance() {
        // If instance does not exist
        if (instance == null) {
            // Call private constructor
            instance = new ApiDatabaseController();
        }
        return instance;
    }

    // VIEW ALL RECORDS
    // TODO Handle IOException, InterruptedException in here
    public List<Map<String, Object>> getAll(String table) throws IOException, InterruptedException {

        Utils.log();
        Utils.log("Calling %s".formatted(apiBaseUrl + "api.php?table=" + table), 3);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(apiBaseUrl + "api.php?table=" + table))
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());


        Type listType = new TypeToken<List<Map<String, Object>>>(){}.getType();
        // TODO Detect bad response ({status=fail, error=...})
        return gson.fromJson(response.body(), listType);
    }

    // ADD RECORD
    // TODO Handle IOException, InterruptedException in here
    public Map<String, Object> addRecord(String table, Map<String, ?> data) throws IOException, InterruptedException {
        String json = gson.toJson(data);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(apiBaseUrl + "api.php?table=" + table))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();

        return getResponse(request);
    }

    // UPDATE RECORD
    // TODO Handle IOException, InterruptedException in here
    public Map<String, Object> updateRecord(String table, Map<String, ?> data) throws IOException, InterruptedException {
        if (!data.containsKey("id")) {
            throw new IllegalArgumentException("Record must include 'id' for update");
        }

        String json = gson.toJson(data);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(apiBaseUrl + "api.php?table=" + table))
                .header("Content-Type", "application/json")
                .PUT(HttpRequest.BodyPublishers.ofString(json))
                .build();

        return getResponse(request);
    }

    // DELETE RECORD
    // TODO Handle IOException, InterruptedException in here
    public Map<String, Object> deleteRecord(String table, Object id) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(apiBaseUrl + "api.php?table=" + table + "&id=" + id))
                .DELETE()
                .build();

        return getResponse(request);
    }

    public Map<String, Object> getResponse(HttpRequest request) throws IOException, InterruptedException {
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        Type mapType = new TypeToken<Map<String, Object>>(){}.getType();

        // TODO Throw error when status not 200
        if (response.statusCode() == 200) {
            return gson.fromJson(response.body(), mapType);
        }
        return null;
    }

    private List<List<Object>> getAllUserInfo() throws IOException, InterruptedException {

        List<List<Object>> listUsersInfo = new ArrayList<>();

        List<Map<String, Object>> listMaps = getAll("users");

        if (!listMaps.isEmpty()){
            Utils.log("User data successfully fetched.", 2);
        } else {
            Utils.log("No user data found!", 1);
        }

        // Loop over each user
        for (Map<String, Object> userInfo : listMaps) {

            List<Object> listUserInfo = new ArrayList<>();
            // Loop over each info in user
            for (Map.Entry<String, Object> entry : userInfo.entrySet()) {
                // Add info to inner list
                listUserInfo.add(entry.getValue());
            }
            // Add inner list to return list
            listUsersInfo.add(listUserInfo);
        }

        return listUsersInfo;
    }

    public List<User> getAllStaff() throws IOException, InterruptedException {

        List<User> listUsers = new ArrayList<>();

        List<List<Object>> listUsersInfo = getAllUserInfo();

        List<Map<String, Object>> listMaps = getAll("staff");

        if (!listMaps.isEmpty()){
            Utils.log("Staff data successfully fetched.", 2);
        } else {
            Utils.log("No staff data found!", 1);
        }

        // Loop over each staff
        for (Map<String, Object> userInfo : listMaps) {

            List<Object> listStaffInfo = new ArrayList<>();

            // Loop over each info in staff
            for (Map.Entry<String, Object> entry : userInfo.entrySet()) {
                listStaffInfo.add(entry.getValue());
            }


            // Get staff info through listUsersInfo using the Staff's user_id
            String foreName = listUsersInfo.get(Integer.parseInt((String) listStaffInfo.get(1))-1).get(1).toString();
            String lastName = listUsersInfo.get(Integer.parseInt((String) listStaffInfo.get(1))-1).get(2).toString();

            // Get department and assign roles
            Staff staff =  switch (Integer.parseInt((String) listStaffInfo.get(2))) {
                // Admin
                case 1 -> new Admin(Integer.parseInt((String) listStaffInfo.get(1)), foreName, lastName, "Admin");
                // Management
                case 2 -> new Manager(Integer.parseInt((String) listStaffInfo.get(1)), foreName, lastName, "Management");
                // Management
                case 3 -> new BookingAgent(Integer.parseInt((String) listStaffInfo.get(1)), foreName, lastName, "Bookings");
                // Wrong case, switch to default
                default -> new SelfService();
            };

            // Add staff to list of users
            listUsers.add(staff);
        }

        // Customer side
        listMaps = getAll("customer");

        if (!listMaps.isEmpty()){
            Utils.log("Customer data successfully fetched.", 2);
        } else {
            Utils.log("No customer data found!", 1);
        }

        // Loop over each customer
        for (Map<String, Object> userInfo : listMaps) {

            List<Object> listCustomerInfo = new ArrayList<>();

            // Loop over each info in customer
            for (Map.Entry<String, Object> entry : userInfo.entrySet()) {

                listCustomerInfo.add(entry.getValue());
            }

            // Get customer info through listUsersInfo using the Customer's user_id
            String foreName = listUsersInfo.get(Integer.parseInt((String) listCustomerInfo.get(1))-1).get(1).toString();
            String lastName = listUsersInfo.get(Integer.parseInt((String) listCustomerInfo.get(1))-1).get(2).toString();


            // Create customer
            Customer customer = new Customer(Integer.parseInt((String) listCustomerInfo.get(1)), foreName, lastName);

            // Add customer to list of users
            listUsers.add(customer);
        }

        return listUsers;
    }

//    private void convertAll(String table) throws IOException, InterruptedException {
//        List<Map<String, Object>> listObjects = getAll(table);
//        List<E> listObjects = new List<E>();
//    }



}
