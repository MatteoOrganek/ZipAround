package uk.ac.roehampton.ziparound.database;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Instant;
import java.util.*;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import uk.ac.roehampton.ziparound.Utils;
import uk.ac.roehampton.ziparound.booking.Bookable;
import uk.ac.roehampton.ziparound.booking.Booking;
import uk.ac.roehampton.ziparound.equipment.Equipment;
import uk.ac.roehampton.ziparound.equipment.vehicle.Vehicle;
import uk.ac.roehampton.ziparound.equipment.vehicle.type.EBike;
import uk.ac.roehampton.ziparound.equipment.vehicle.type.Scooter;
import uk.ac.roehampton.ziparound.users.Customer;
import uk.ac.roehampton.ziparound.users.User;
import uk.ac.roehampton.ziparound.users.staff.Staff;
import uk.ac.roehampton.ziparound.users.staff.role.Admin;
import uk.ac.roehampton.ziparound.users.staff.role.BookingAgent;
import uk.ac.roehampton.ziparound.users.staff.role.Manager;
import uk.ac.roehampton.ziparound.users.staff.role.SelfService;

import java.lang.reflect.Type;
import java.io.IOException;

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

    // DATA FETCHING

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


    // DATA TRANSLATION

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

    public List<User> getAllUsers() throws IOException, InterruptedException {

        // Get all users
        List<List<Object>> listUsersInfo = getAllUserInfo();

        // Staff side
        List<Map<String, Object>> listMaps = getAll("staff");

        if (!listMaps.isEmpty()){
            Utils.log("Staff data successfully fetched.", 2);
        } else {
            Utils.log("No staff data found!", 1);
        }

        // Loop over each staff
        for (Map<String, Object> staffInfo : listMaps) {

            // Get user id
            int userID = Integer.parseInt((String) staffInfo.get("user_id"));

            // Get staff info through listUsersInfo using the Staff's user_id - 1 as the database index starts with 1.
            String foreName = listUsersInfo.get(userID - 1).get(1).toString();
            String lastName = listUsersInfo.get(userID - 1).get(2).toString();

            // Get department and assign roles
            Staff staff =  switch (Integer.parseInt((String) staffInfo.get("department_id"))) {
                // Admin
                case 1 -> new Admin(userID, foreName, lastName, "Admin");
                // Management
                case 2 -> new Manager(userID, foreName, lastName, "Management");
                // Management
                case 3 -> new BookingAgent(userID, foreName, lastName, "Bookings");
                // Wrong case, switch to default
                default -> new SelfService();
            };

            // Add staff to list of users
            Utils.bookingManagerInstance.addUser(staff);
        }

        // Customer side
        listMaps = getAll("customer");

        if (!listMaps.isEmpty()){
            Utils.log("Customer data successfully fetched.", 2);
        } else {
            Utils.log("No customer data found!", 1);
        }

        // Loop over each customer
        for (Map<String, Object> customerInfo : listMaps) {

            // Get user id
            int userID = Integer.parseInt((String) customerInfo.get("user_id"));

            // Get customer info through listUsersInfo using the Customer's user_id - 1 as the database index starts with 1.
            String foreName = listUsersInfo.get(userID - 1).get(1).toString();
            String lastName = listUsersInfo.get(userID - 1).get(2).toString();

            // Create customer (using user's id)
            Customer customer = new Customer(userID, foreName, lastName);

            // Add customer to list of users
            Utils.bookingManagerInstance.addUser(customer);
        }

        // Return list
        return Utils.bookingManagerInstance.getUserArrayList();
    }

    public List<Bookable> getAllBookables() throws IOException, InterruptedException {

        List<Map<String, Object>> listMaps = getAll("vehicle");

        if (!listMaps.isEmpty()){
            Utils.log("Vehicle data successfully fetched.", 2);
        } else {
            Utils.log("No vehicle data found!", 1);
        }

        // Loop over each vehicle
        for (Map<String, Object> vehicleInfo : listMaps) {

            // Create the vehicle based on the type
            Vehicle vehicle =  switch ((String) vehicleInfo.get("type")) {
                case "EBike" -> new EBike(
                        Integer.parseInt((String) vehicleInfo.get("id")),
                        (String) vehicleInfo.get("brand"),
                        (String) vehicleInfo.get("number_plate"),
                        Float.parseFloat((String) vehicleInfo.get("total_miles")),
                        Boolean.getBoolean((String) vehicleInfo.get("available")),
                        Integer.parseInt((String) vehicleInfo.get("max_power_kw")),
                        Integer.parseInt((String) vehicleInfo.get("amount_of_batteries"))
                );
                case "Scooter" -> new Scooter(
                        Integer.parseInt((String) vehicleInfo.get("id")),
                        (String) vehicleInfo.get("brand"),
                        (String) vehicleInfo.get("number_plate"),
                        Float.parseFloat((String) vehicleInfo.get("total_miles")),
                        Boolean.getBoolean((String) vehicleInfo.get("available")),
                        Integer.parseInt((String) vehicleInfo.get("max_power_kw")),
                        Integer.parseInt((String) vehicleInfo.get("amount_of_batteries"))
                );
                default -> throw new IllegalStateException("Unexpected value: " + vehicleInfo.get("type"));
            };

            // Add it to the bookable list in booking manager (Cast it to Bookable as the abstract class Vehicle is not Bookable, (EBike and ))
            Utils.bookingManagerInstance.addBookable((Bookable) vehicle);
        }

        // Get all equipment
        listMaps = getAll("equipment");

        if (!listMaps.isEmpty()){
            Utils.log("Vehicle data successfully fetched.", 2);
        } else {
            Utils.log("No vehicle data found!", 1);
        }

        // Loop over each equipment
        for (Map<String, Object> equipmentInfo : listMaps) {


            Equipment equipment = new Equipment(
                    Integer.parseInt((String) equipmentInfo.get("id")),
                    (String) equipmentInfo.get("name"),
                    (String) equipmentInfo.get("description"),
                    Boolean.getBoolean((String) equipmentInfo.get("available"))
            );

            // Add it to the bookable list in booking manager
            Utils.bookingManagerInstance.addBookable(equipment);
        }

        return Utils.bookingManagerInstance.getBookableArrayList();
    }

//    public List<Booking> getAllBookings() throws IOException, InterruptedException {
//
//        List<Map<String, Object>> listMaps = getAll("booking");
//
//        // Loop over each booking
//        for (Map<String, Object> userInfo : listMaps) {
//
//            List<Object> listBookingInfo = new ArrayList<>();
//
//            // Loop over each info in booking
//            for (Map.Entry<String, Object> entry : userInfo.entrySet()) {
//                Utils.log("%s - %s".formatted(entry.getKey(), entry.getValue()));
//                listBookingInfo.add(entry.getValue());
//            }
//
//            // Get time
//            Instant startTime = Utils.convertStringToInstant((String) listBookingInfo.get(1));
//            Instant endTime = Utils.convertStringToInstant((String) listBookingInfo.get(2));
//            Instant createdOnTime = Utils.convertStringToInstant((String) listBookingInfo.get(3));
//
//            User user;
//            Bookable bookable;
//            Staff staff;
//
//            Booking booking = new Booking(
//                    Integer.parseInt((String) listBookingInfo.get(0)),
//                    startTime,
//                    endTime,
//                    createdOnTime,
//                    user,
//                    bookable,
//                    Boolean.parseBoolean((String) listBookingInfo.get(7)),
//                    staff
//            );
//            Utils.bookingManagerInstance.addBooking(booking);
//        }
//
//        return Utils.bookingManagerInstance.getBookingArrayList();
//    }

    public List<Object> listAllObjects(String s) throws IOException, InterruptedException {

        // Initialize return list
        List<Object> listObject = new ArrayList<>();

        // Get objects
        List<Map<String, Object>> listMaps = getAll(s);

        // Loop over each Objects
        for (Map<String, Object> objectInfo : listMaps) {

            // Loop over each info in Object
            for (Map.Entry<String, Object> entry : objectInfo.entrySet()) {

                Utils.log("%s - %s".formatted(entry.getKey(), entry.getValue()));
            }
        }

        return listObject;
    }

    public void update() throws IOException, InterruptedException {
        getAllUsers();
        getAllBookables();
//        getAllBookings();
    }

}
