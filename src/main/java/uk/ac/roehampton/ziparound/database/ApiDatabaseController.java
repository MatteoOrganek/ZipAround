package uk.ac.roehampton.ziparound.database;

import java.awt.print.Book;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Instant;
import java.util.*;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import javafx.concurrent.Task;
import uk.ac.roehampton.ziparound.Utils;
import uk.ac.roehampton.ziparound.application.Updatable;
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
import java.util.concurrent.CompletableFuture;


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

        if (listMaps != null){
            Utils.log("User data successfully fetched.", 2);

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

        } else {
            Utils.log("No user data found!", 1);
            return null;
        }
    }

    public List<User> getAllUsers() throws IOException, InterruptedException {

        // Get all users
        List<List<Object>> listUsersInfo = getAllUserInfo();

        // Staff side
        List<Map<String, Object>> listMaps = getAll("staff");

        if (listMaps != null && listUsersInfo != null){

            Utils.log("Staff data successfully fetched.", 2);
            // Loop over each staff
            for (Map<String, Object> staffInfo : listMaps) {

                // Get staff id
                int staffID = Integer.parseInt((String) staffInfo.get("id"));

                // Get user id
                int userID = Integer.parseInt((String) staffInfo.get("user_id"));


                // Get staff info through listUsersInfo using the Staff's user_id - 1 as the database index starts with 1.
                String foreName = listUsersInfo.get(userID - 1).get(1).toString();
                String lastName = listUsersInfo.get(userID - 1).get(2).toString();

                // Get department and assign roles
                Staff staff =  switch (Integer.parseInt((String) staffInfo.get("department_id"))) {
                    // Admin
                    case 1 -> new Admin(userID, staffID, foreName, lastName, "Admin");
                    // Management
                    case 2 -> new Manager(userID, staffID, foreName, lastName, "Management");
                    // Management
                    case 3 -> new BookingAgent(userID, staffID, foreName, lastName, "Bookings");
                    // Wrong case, switch to default
                    default -> new SelfService();
                };

                // Add staff to list of users
                Utils.bookingManagerInstance.addUser(staff);
            }
        } else {
            Utils.log("No staff data found!", 1);
        }


        // Loop over each customer (Users)
        for (List<Object> user : listUsersInfo) {

            // Get user id
            int userID = Integer.parseInt((String) user.get(0));

            // Get customer info through listUsersInfo 1-forename, 2-lastname.
            String foreName = user.get(1).toString();
            String lastName = user.get(2).toString();

            // Create customer (using user's id)
            Customer customer = new Customer(userID, foreName, lastName);

            // Add customer to list of users
            Utils.bookingManagerInstance.addUser(customer);
        }

        // Return userArray list from the bookingManagerInstance. Will return an empty list if no items have been inserted.
        return Utils.bookingManagerInstance.getUserArrayList();
    }

    public List<Bookable> getAllBookables() throws IOException, InterruptedException {

        List<Map<String, Object>> listMaps = getAll("vehicle");

        if (listMaps != null){
            Utils.log("Vehicle data successfully fetched.", 2);
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

                // Add it to the bookable list in booking manager (Cast it to Bookable as the abstract class Vehicle is not Bookable, (EBike and Scooters, etc.))
                Utils.bookingManagerInstance.addBookable(vehicle);
            }
        } else {
            Utils.log("No vehicle data found!", 1);
        }


        // Get all equipment
        listMaps = getAll("equipment");

        if (listMaps != null){
            Utils.log("Vehicle data successfully fetched.", 2);
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


        } else {
            Utils.log("No vehicle data found!", 1);
        }

        // Return bookableArray list from the bookingManagerInstance. Will return an empty list if no items have been inserted.
        return Utils.bookingManagerInstance.getBookableArrayList();
    }

    public List<Booking> getAllBookings() throws IOException, InterruptedException {

        List<Map<String, Object>> listMaps = getAll("booking");

        if (    listMaps != null &&
                !Utils.bookingManagerInstance.getBookableArrayList().isEmpty() &&
                !Utils.bookingManagerInstance.getUserArrayList().isEmpty()
        ){
            Utils.log("Booking data successfully fetched.", 2);
            // Loop over each booking
            for (Map<String, Object> bookingInfo : listMaps) {

                // Get time
                Instant startTime = Utils.convertStringToInstant((String) bookingInfo.get("booked_start_time"));
                Instant endTime = Utils.convertStringToInstant((String) bookingInfo.get("booked_end_time"));
                Instant createdOnTime = Utils.convertStringToInstant((String) bookingInfo.get("created_on"));

                // Initialize user and get userID
                User user = null;
                int userID = Integer.parseInt((String) bookingInfo.get("user_id"));

                // Initialize user and get staffID
                Staff staff = null;
                String staffStr = (String) bookingInfo.get("staff_approved_id");

                int staffID = -1;
                // Set staffID if not null in the db (-1 if so)
                if (Utils.isNumeric(staffStr)) {
                    staffID = Integer.parseInt(staffStr);
                }

                // For each user in current booking manager list
                for (User currentUser : Utils.bookingManagerInstance.getUserArrayList()) {

                    // If the current user's id matches the one that needs to be found
                    if (Objects.equals(currentUser.getID(Utils.currentStaff), userID)) {
                        // Set the current user as the booking user
                        user = currentUser;
                    }
                    // If the staff is null, the current user is staff and the current staffID matches the staffID to be found
                    if (staffID != -1 && currentUser instanceof Staff && ((Staff) currentUser).getStaffID(Utils.currentStaff) == staffID ) {
                        // Set the current user as the staff that approved the booking
                        staff = (Staff) currentUser;
                    }

                    // TODO Check if list is in order
                    // User array List in bookingManagerInstance is crated with order in mind, where staff is listed at the beginning of the list.
                    // If user is not null and the current user is a customer, the window of opportunity to identify the staff is already gone, hence run the condition.
                    if (user != null && currentUser instanceof Customer) {
                        break;
                    }
                }

                Bookable bookable = null;
                int bookableID = Integer.parseInt((String) bookingInfo.get("bookable_id"));

                for (Bookable currentBookable : Utils.bookingManagerInstance.getBookableArrayList()) {
                    if (currentBookable.getID(Utils.currentStaff) == bookableID) {
                        bookable = currentBookable;
                        break;
                    }
                }

                Booking booking = new Booking(
                        Integer.parseInt((String) bookingInfo.get("id")),
                        startTime,
                        endTime,
                        createdOnTime,
                        user,
                        bookable,
                        Boolean.parseBoolean((String) bookingInfo.get("approved")),
                        staff
                );

                Utils.bookingManagerInstance.addBooking(booking);
            }

        } else {
            Utils.log("No booking data found!", 1);
        }

        // Return bookingArray list from the bookingManagerInstance. Will return an empty list if no items have been inserted.
        return Utils.bookingManagerInstance.getBookingArrayList();
    }

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

    // Update data using different threads from ui thread
    public void update() {

        Task<Boolean> updateTask = new Task<>() {
            @Override
            protected Boolean call() throws Exception {
                getAllUsers();
                getAllBookables();
                getAllBookings();
                return true;
            }
        };

        // Disable entire UI while task runs
        Utils.currentScene.getRoot().disableProperty().bind(updateTask.runningProperty());

        updateTask.setOnSucceeded(event -> {
            try {
                Utils.currentController.update();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            Utils.log("Successful update.", 2);
        });

        updateTask.setOnFailed(event -> {
            Throwable ex = updateTask.getException();
            Utils.log("Update failed: " + ex.getMessage(), 5);
            ex.printStackTrace();
        });

        Thread thread = new Thread(updateTask);
        // Prevent app from hanging on exit
        thread.setDaemon(true);
        thread.start();
    }

}
