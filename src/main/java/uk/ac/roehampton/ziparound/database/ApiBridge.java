/**
 * ApiBridge.java
 * Singleton class that is able to fetch data from a database using requests through an api in owres.org
 *
 * @author Matteo Organek
 * @version 1.0
 * @since 01/01/2026
 */

package uk.ac.roehampton.ziparound.database;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import javafx.concurrent.Task;
import javafx.scene.control.ProgressBar;
import uk.ac.roehampton.ziparound.Utils;
import uk.ac.roehampton.ziparound.application.Updatable;
import uk.ac.roehampton.ziparound.booking.Bookable;
import uk.ac.roehampton.ziparound.booking.Booking;
import uk.ac.roehampton.ziparound.equipment.Equipment;
import uk.ac.roehampton.ziparound.equipment.vehicle.Electric;
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


/***
 * This class deals with the communication between the database and this client.
 * It is able to translate all custom Objects into json to be sent to the database and vice versa.
 */
public class ApiBridge {

    // Function declaration
    private final String apiBaseUrl;
    private final HttpClient client;
    private final Gson gson;
    private static ApiBridge instance;

    /**
     * This constructor configures the instance.
     */
    private ApiBridge() {
        this.apiBaseUrl = "https://owres.org/ziparound/";
        this.client = HttpClient.newHttpClient();
        this.gson = new Gson();
    }

    /**
     * This function sets up the singleton instance.
     */
    public static synchronized ApiBridge getInstance() {
        // If instance does not exist
        if (instance == null) {
            // Call private constructor
            instance = new ApiBridge();
        }
        return instance;
    }

    /**
     * This function sends a GET request to the db to fetch all the data from a table.
     * @param table Table name used to fetch objects.
     * @return A map of all the entries in a table.
     * @throws IOException Exception thrown if the addition process was interrupted.
     * @throws InterruptedException Exception thrown if the addition process was interrupted.
     */
    public List<Map<String, Object>> getAll(String table) throws IOException, InterruptedException {

        Utils.log("Calling %s".formatted(apiBaseUrl + "api.php?table=" + table), 3);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(apiBaseUrl + "api.php?table=" + table))
                .GET()
                .build();

        assert request != null;

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assert response != null;

        Type listType = new TypeToken<List<Map<String, Object>>>(){}.getType();
        // TODO Detect bad response ({status=fail, error=...})
        return gson.fromJson(response.body(), listType);
    }

    /**
     * This function sends a POST request to the db.
     * @param table Table name where the object needs to be added.
     * @param data Map of the entry that will be added.
     * @return Response of the request.
     * @throws IOException Exception thrown if the addition process was interrupted.
     * @throws InterruptedException Exception thrown if the addition process was interrupted.
     */
    private Map<String, Object> addRecord(String table, Map<String, ?> data) throws IOException, InterruptedException {

        // Convert Map to JSON
        String json = gson.toJson(data);

        // Create POST request
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(apiBaseUrl + "api.php?table=" + table))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();

        assert request != null;
        // Return the request's response
        return getResponse(request);
    }

    /**
     * This function sends a PUT request to the db.
     * @param table Table name where the object that needs to be updated resides.
     * @param data Map of the entry that needs to be edited.
     * @return Response of the request.
     * @throws IOException Exception thrown if the update process was interrupted.
     * @throws InterruptedException Exception thrown if the update process was interrupted.
     */
    private Map<String, Object> updateRecord(String table, Map<String, ?> data) throws IOException, InterruptedException {

        // Check if the data contains an id, if not, throw error
        if (!data.containsKey("id")) {
            throw new IllegalArgumentException("Record must include 'id' for update");
        }

        // Convert Map to JSON
        String json = gson.toJson(data);

        // Create PUT request
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(apiBaseUrl + "api.php?table=" + table))
                .header("Content-Type", "application/json")
                .PUT(HttpRequest.BodyPublishers.ofString(json))
                .build();

        assert request != null;
        // Return the request's response
        return getResponse(request);
    }

    /**
     * This function sends a DELETE request to the db.
     * @param table Table name where the object that needs to be deleted resides.
     * @param id Object (Integer | String) id of the entry that needs to be deleted.
     * @return Response of the request.
     * @throws IOException Exception thrown if the delete process was interrupted.
     * @throws InterruptedException Exception thrown if the delete process was interrupted.
     */
    private Map<String, Object> deleteRecord(String table, Object id) throws IOException, InterruptedException {

        // Create DELETE request
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(apiBaseUrl + "api.php?table=" + table + "&id=" + id))
                .DELETE()
                .build();

        assert request != null;
        // Return the request's response
        return getResponse(request);
    }

    /**
     * This function handles the JSON output given by the db.
     * @param request HttpRequest to be sent to the db.
     * @return Response of the request, translated to hashmap.
     * @throws IOException Exception thrown if the delete process was interrupted.
     * @throws InterruptedException Exception thrown if the delete process was interrupted.
     */
    private Map<String, Object> getResponse(HttpRequest request) throws IOException, InterruptedException {

        // Send request and accept response
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        // Create map type to only accept String, Object
        Type mapType = new TypeToken<Map<String, Object>>(){}.getType();

        // Check if the server send an OK response
        if (response.statusCode() == 200) {
            // Translate body to hashmap
            return gson.fromJson(response.body(), mapType);
        }
        return null;
    }

    /***
     * This function is able to differentiate through each table name to get a list of all the objects in it, translating each entry in object's data.
     * @param table The name of the table in which reside all objects.
     * @return List of objects requested.
     * @throws IOException Exception thrown if the fetch process was interrupted.
     * @throws InterruptedException Exception thrown if the fetch process was interrupted.
     */
    public ArrayList<Object> getObjects(String table) throws IOException, InterruptedException {

        // Initialize return list
        ArrayList<Object> listObject = new ArrayList<>();

        // Get maps of all items in table
        List<Map<String, Object>> listMaps = getAll(table);

        switch (table) {
            case "booking":

                // Reset bookings list
                Utils.bookingManagerInstance.resetBookingArrayList();

                if ( listMaps != null &&
                        !Utils.bookingManagerInstance.getVehicleArrayList().isEmpty() &&
                        !Utils.bookingManagerInstance.getEquipmentArrayList().isEmpty() &&
                        !Utils.bookingManagerInstance.getCustomerArrayList().isEmpty() &&
                        !Utils.bookingManagerInstance.getStaffArrayList().isEmpty() ){

                    Utils.log("Booking data successfully fetched.", 2);

                    // Assemble List of Users
                    List<User> listUsers = new ArrayList<>();
                    listUsers.addAll(Utils.bookingManagerInstance.getStaffArrayList());
                    listUsers.addAll(Utils.bookingManagerInstance.getCustomerArrayList());

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
                        for (User currentUser : listUsers) {

                            // If the current user's id matches the one that needs to be found
                            if (Objects.equals(currentUser.getID(Utils.currentStaff), userID)) {
                                // Set the current user as the booking user
                                user = currentUser;
                            }
                            // If the staff is null, the current user is staff and the current staffID matches the staffID to be found
                            if (currentUser instanceof Staff && (Utils.currentStaff).canViewStaffInfo()) {
                                if (staffID != -1 && ((Staff) currentUser).getStaffID(Utils.currentStaff) == staffID) {
                                    // Set the current user as the staff that approved the booking
                                    staff = (Staff) currentUser;
                                }
                            }

                            // TODO Check if list is in order
                            // Staff is listed at the beginning of the list.
                            // If user is not null and the current user is a customer, the window of opportunity to identify the staff is already gone, hence run the condition.
                            if (user != null && currentUser instanceof Customer) {
                                break;
                            }
                        }

                        // Initialize bookable
                        Bookable bookable = null;

                        // Get vehicle and equipment id
                        int vehicleID = Integer.parseInt((String) bookingInfo.get("vehicle_id"));
                        int equipmentID = Integer.parseInt((String) bookingInfo.get("equipment_id"));

                        // If vehicle id is 0, it means that an equipment has been inputted
                        if (vehicleID != 0) {
                            // For each bookable in vehicleArrayList
                            for (Bookable currentBookable : Utils.bookingManagerInstance.getVehicleArrayList()) {
                                // If the current bookable has the same id
                                if (currentBookable.getID(Utils.currentStaff) == vehicleID) {
                                    // Bookable has been found, stop the loop
                                    bookable = currentBookable;
                                    break;
                                }
                            }
                        } else {
                            // For each bookable in equipmentArrayList
                            for (Bookable currentBookable : Utils.bookingManagerInstance.getEquipmentArrayList()) {
                                // If the current bookable has the same id
                                if (currentBookable.getID(Utils.currentStaff) == equipmentID) {
                                    // Bookable has been found, stop the loop
                                    bookable = currentBookable;
                                    break;
                                }
                            }
                        }
//                        Utils.log("%s %s".formatted(bookingInfo.get("id"), bookable));


                        // Create the booking
                        Booking booking = new Booking(
                                Integer.parseInt((String) bookingInfo.get("id")),
                                startTime,
                                endTime,
                                createdOnTime,
                                user,
                                bookable,
                                "1".equals(bookingInfo.get("approved")),
                                staff
                        );

                        // Add it to the booking manager instance
                        Utils.bookingManagerInstance.addBooking(booking);
                    }

                } else {
                    Utils.log("No booking data found!", 1);
                }

                // Return bookingArray list from the bookingManagerInstance. Will return an empty list if no items have been inserted.
                listObject.addAll(Utils.bookingManagerInstance.getBookingArrayList());
                return listObject;

            case "vehicle":

                // Reset bookable list
                Utils.bookingManagerInstance.resetVehicleArrayList();

                if (listMaps != null){
                    Utils.log("Vehicle data successfully fetched.", 2);
                    // Loop over each vehicle
                    for (Map<String, Object> vehicleInfo : listMaps) {

                        // Create the vehicle based on the type
                        Vehicle vehicle =  switch ((String) vehicleInfo.get("type")) {
                            case "EBike" -> new EBike(
                                    Integer.parseInt((String) vehicleInfo.get("id")),
                                    (String) vehicleInfo.get("brand"),
                                    (String) vehicleInfo.get("model"),
                                    (String) vehicleInfo.get("number_plate"),
                                    Float.parseFloat((String) vehicleInfo.get("total_miles")),
                                    "1".equals(vehicleInfo.get("available")),
                                    Integer.parseInt((String) vehicleInfo.get("max_power_kw")),
                                    Integer.parseInt((String) vehicleInfo.get("amount_of_batteries")),
                                    Integer.parseInt((String) vehicleInfo.get("amount_of_bookings"))
                            );
                            case "Scooter" -> new Scooter(
                                    Integer.parseInt((String) vehicleInfo.get("id")),
                                    (String) vehicleInfo.get("brand"),
                                    (String) vehicleInfo.get("model"),
                                    (String) vehicleInfo.get("number_plate"),
                                    Float.parseFloat((String) vehicleInfo.get("total_miles")),
                                    "1".equals(vehicleInfo.get("available")),
                                    Integer.parseInt((String) vehicleInfo.get("max_power_kw")),
                                    Integer.parseInt((String) vehicleInfo.get("amount_of_batteries")),
                                    Integer.parseInt((String) vehicleInfo.get("amount_of_bookings"))
                            );
                            // Throw error if vehicle type has not been found
                            default -> throw new IllegalStateException("Unexpected value: " + vehicleInfo.get("type"));
                        };

                        // Add it to the vehicle list in booking manager
                        Utils.bookingManagerInstance.addVehicle(vehicle);
                    }
                } else {
                    Utils.log("No vehicle data found!", 1);
                }

                // Return vehicleArray list from the bookingManagerInstance. Will return an empty list if no items have been inserted.
                listObject.addAll(Utils.bookingManagerInstance.getVehicleArrayList());
                return listObject;

            case "equipment":

                // Get all equipment

                Utils.bookingManagerInstance.resetEquipmentArrayList();

                if (listMaps != null){
                    Utils.log("Vehicle data successfully fetched.", 2);
                    // Loop over each equipment
                    for (Map<String, Object> equipmentInfo : listMaps) {

                        // Build the equipment
                        Equipment equipment = new Equipment(
                                Integer.parseInt((String) equipmentInfo.get("id")),
                                (String) equipmentInfo.get("name"),
                                (String) equipmentInfo.get("model"),
                                (String) equipmentInfo.get("description"),
                                "1".equals(equipmentInfo.get("available")),
                                Integer.parseInt((String) equipmentInfo.get("amount_of_bookings"))
                        );

                        // Add it to the bookable list in booking manager
                        Utils.bookingManagerInstance.addEquipment(equipment);
                    }


                } else {
                    Utils.log("No equipment data found!", 1);
                }

                // Return equipmentArray list from the bookingManagerInstance. Will return an empty list if no items have been inserted.
                listObject.addAll(Utils.bookingManagerInstance.getEquipmentArrayList());
                return listObject;

            case "users":

                // Reset user list
                Utils.bookingManagerInstance.resetCustomerArrayList();

                if (listMaps != null) {
                    Utils.log("Customer data successfully fetched.", 2);
                    // Loop over each user
                    for (Map<String, Object> customerInfo : listMaps) {

                        // Get user id
                        int userID = Integer.parseInt((String) customerInfo.get("id"));

                        // Get customer info
                        String foreName = customerInfo.get("fore_name").toString();
                        String lastName = customerInfo.get("last_name").toString();

                        // Create customer (using user's id)
                        Customer customer = new Customer(userID, foreName, lastName);

                        // Add customer to list of users
                        Utils.bookingManagerInstance.addCustomer(customer);
                    }
                }

                // Return customerArray list from the bookingManagerInstance. Will return an empty list if no items have been inserted.
                listObject.addAll(Utils.bookingManagerInstance.getCustomerArrayList());
                return listObject;

            case "staff":

                // Reset user list
                Utils.bookingManagerInstance.resetStaffArrayList();

                // Get all users
                List<Customer> listCustomers = Utils.bookingManagerInstance.getCustomerArrayList();

                if (listMaps != null && listCustomers != null){

                    Utils.log("Staff data successfully fetched.", 2);
                    // Loop over each staff
                    for (Map<String, Object> staffInfo : listMaps) {

                        // Get staff id
                        int staffID = Integer.parseInt((String) staffInfo.get("id"));

                        // Get user id
                        int userID = Integer.parseInt((String) staffInfo.get("user_id"));


                        // Get staff info through Customer's data (Staff can be a Customer too)
                        String foreName = "";
                        String lastName = "";
                        for (Customer customer : listCustomers) {
                            if (customer.getID(Utils.currentStaff) == userID) {
                                foreName = customer.getForeName(Utils.currentStaff);
                                lastName = customer.getLastName(Utils.currentStaff);
                            }
                        }

                        // Get department and assign roles
                        Staff staff = switch (Integer.parseInt((String) staffInfo.get("department_id"))) {
                            // Maintenance
                            case -2 -> new Admin(userID, staffID, foreName, lastName, "Maintenance");
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
                        Utils.bookingManagerInstance.addStaff(staff);
                    }
                } else {
                    Utils.log("No staff data found!", 1);
                }

                // Return staffArray list from the bookingManagerInstance. Will return an empty list if no items have been inserted.
                listObject.addAll(Utils.bookingManagerInstance.getStaffArrayList());
                return listObject;

            default:
                throw new Error("Table outside of bounds.");

        }
    }

    /***
     * This function adds an abstract object's data to the database.
     *
     * @param object object to be added.
     * @throws IOException Exception thrown if the addition process was interrupted.
     * @throws InterruptedException Exception thrown if the addition process was interrupted.
     */
    public void addObject(Object object) throws IOException, InterruptedException {
        Utils.log("Adding record...", 3);
        // Get object's table name
        String objectTable = getTable(object);
        // The object's id is not required as the SQL server will handle the id assignment.
        addRecord(objectTable, getMap(object, false));
        // Fetch updated data
        update();
    }

    /***
     * This function updates the data of an abstract object in the database.
     *
     * @param object object to be updated.
     * @throws IOException Exception thrown if the update process was interrupted.
     * @throws InterruptedException Exception thrown if the update process was interrupted.
     */
    public void updateObject(Object object) throws IOException, InterruptedException {
        Utils.log("Updating record...", 3);
        // Get object's table name
        String objectTable = getTable(object);
        // The object's id is required to modify the object.
        updateRecord(objectTable, getMap(object, true));
        // Fetch updated data
        update();
    }

    /***
     * This function deletes the data of an abstract object in the database.
     *
     * @param object object to be deleted.
     * @throws IOException Exception thrown if the delete process was interrupted.
     * @throws InterruptedException Exception thrown if the delete process was interrupted.
     */
    public void deleteObject(Object object) throws IOException, InterruptedException {
        Utils.log("Deleting record...", 3);
        // Get object's table name
        String objectTable = getTable(object);
        // The object's id is required to delete the object.
        deleteRecord(objectTable, getMap(object, true).get("id"));
        // Fetch updated data
        update();
    }

    /***
     * This function creates a map of an object based on its class. This is an helper class to be able to translate
     * Objects to JSON files.
     *
     * @param object Object to create a map from.
     * @param isIDRequired Boolean that determines whether the id is required or not.
     * @return
     */
    private Map<String, Object> getMap(Object object, Boolean isIDRequired) {

        // the return hashmap
        Map<String, Object> objectInfo = new HashMap<>();

        // If the object is a booking
        if (object instanceof Booking booking) {

            Utils.log("Processing booking record...");

            // Get start, end and created on time
            LocalDateTime start = LocalDateTime.ofInstant(booking.getBookedStartTime(Utils.currentStaff), ZoneId.systemDefault());
            LocalDateTime end = LocalDateTime.ofInstant(booking.getBookedEndTime(Utils.currentStaff), ZoneId.systemDefault());
            LocalDateTime createdOn = LocalDateTime.ofInstant(booking.getCreatedOn(Utils.currentStaff), ZoneId.systemDefault());

            // Populate map with keys and values
            if (isIDRequired) objectInfo.put("id", booking.getID(Utils.currentStaff));
            objectInfo.put("booked_start_time", start.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
            objectInfo.put("booked_end_time", end.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
            objectInfo.put("created_on", createdOn.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
            objectInfo.put("user_id", booking.getUser(Utils.currentStaff).getID(Utils.currentStaff));
            objectInfo.put("approved", booking.getApproved(Utils.currentStaff));

            // Differentiate bookable's id as the booking table has vehicle_id and equipment_id
            Bookable bookable = booking.getBookableObject(Utils.currentStaff);
            if (bookable instanceof Vehicle) {
                objectInfo.put("vehicle_id", bookable.getID(Utils.currentStaff));
            } else if (bookable instanceof Equipment) {
                objectInfo.put("equipment_id", bookable.getID(Utils.currentStaff));
            }

            // Check if any staff approved the booking (null if false)
            if (booking.getStaff(Utils.currentStaff) != null) {
                objectInfo.put("staff_approved_id", ((User) booking.getStaff(Utils.currentStaff)).getID(Utils.currentStaff));
            }

        // If the object is a customer
        } else if (object instanceof Customer customer) {

            Utils.log("Processing customer record...");

            // Populate map with keys and values
            if (isIDRequired) objectInfo.put("id", customer.getID(Utils.currentStaff));
            objectInfo.put("fore_name", customer.getForeName(Utils.currentStaff));
            objectInfo.put("last_name", customer.getLastName(Utils.currentStaff));

        // If the object is staff
        } else if (object instanceof Staff staff) {

            Utils.log("Processing staff record...");

            // Populate map with keys and values
            if (isIDRequired) objectInfo.put("id", staff.getStaffID(Utils.currentStaff));
            objectInfo.put("user_id", staff.getID(Utils.currentStaff));
            // Differentiate between Admin, Manager and BookingAgent as the db has the roles numbered
            if (staff instanceof Admin) objectInfo.put("department_id", 1);
            if (staff instanceof Manager) objectInfo.put("department_id", 2);
            if (staff instanceof BookingAgent) objectInfo.put("department_id", 3);

        // If the object is a vehicle
        } else if (object instanceof Vehicle vehicle) {

            Utils.log("Processing vehicle record...");

            // Populate map with keys and values
            if (isIDRequired) objectInfo.put("id", vehicle.getID(Utils.currentStaff));
            objectInfo.put("brand", vehicle.getBrand(Utils.currentStaff));
            objectInfo.put("model", vehicle.getModel(Utils.currentStaff));
            objectInfo.put("type", vehicle.getType(Utils.currentStaff));
            objectInfo.put("number_plate", vehicle.getNumberPlate(Utils.currentStaff));
            objectInfo.put("total_miles", vehicle.getTotalMiles(Utils.currentStaff));
            objectInfo.put("available", vehicle.isAvailable(Utils.currentStaff));
            objectInfo.put("amount_of_bookings", vehicle.getAmountOfBookings(Utils.currentStaff));

            // Check if the vehicle is electric
            boolean isElectric = vehicle instanceof EBike || vehicle instanceof Scooter;
            if (isElectric) objectInfo.put("max_power_kw", ((Electric) vehicle).getMaxPowerKw(Utils.currentStaff));
            if (isElectric) objectInfo.put("amount_of_batteries", ((Electric) vehicle).getAmountOfBatteries(Utils.currentStaff));

        // If the object is an equipment
        } else if (object instanceof Equipment) {

            Utils.log("Processing equipment record...");

            // Cast object to equipment
            Equipment equipment = (Equipment) object;

            // Populate map with keys and values
            if (isIDRequired) objectInfo.put("id", equipment.getID(Utils.currentStaff));
            objectInfo.put("name", equipment.getName(Utils.currentStaff));
            objectInfo.put("model", equipment.getModel(Utils.currentStaff));
            objectInfo.put("description", equipment.getDescription(Utils.currentStaff));
            objectInfo.put("available", equipment.isAvailable(Utils.currentStaff));
            objectInfo.put("amount_of_bookings", equipment.getID(Utils.currentStaff));
        }

        // If the map is not empty, return it
        if (!objectInfo.isEmpty()) {
            return objectInfo;

        // If the map is empty, it means that the object was not in the pool of possible db entities
        } else {
            throw new Error("Unhandled case!");
        }
    }

    /**
     * This function returns the corresponding table based on the class of the object.
     *
     * @param object Object used to find the table name.
     * @return Table name.
     */
    private String getTable(Object object) {
        // Compare the object with each possible classes. If none have been found, throw an error.
        return switch (object) {
            case Booking   ignored -> "booking";
            case Vehicle   ignored -> "vehicle";
            case Equipment ignored -> "equipment";
            case Customer  ignored -> "users";
            case Staff     ignored -> "staff";
            case null, default -> throw new Error("Unhandled case!");
        };
    }

    /**
     * This function updates all the data in bookingManager sequentially in a different thread from the UI thread.
     */
    public void update() {

        // Create a new task that will be used to be run in another thread.
        Task<Boolean> updateTask = new Task<>() {
            @Override
            protected Boolean call() throws Exception {
                // Get users
                updateProgress(1, 5);
                getObjects("users");
                updateProgress(2, 5);
                getObjects("staff");

                // Get bookables
                updateProgress(3, 5);
                getObjects("vehicle");
                updateProgress(4, 5);
                getObjects("equipment");

                // Get bookings
                updateProgress(5, 5);
                getObjects("booking");

                // Reset Progress
                updateProgress(0, 0);

                // No issues found, update ended successfully.
                return true;
            }
        };

        // Get the current controller's header (if updatable), find its load bar and bind it to updateTask's progressProperty.
        // This enables the task to control the loadBar from another thread.
        if (Utils.currentController != null) Utils.currentController.getHeaderController().loadBar.progressProperty().bind(updateTask.progressProperty());

        // Disable entire UI while task runs
        if (Utils.currentScene != null) Utils.currentScene.getRoot().disableProperty().bind(updateTask.runningProperty());

        // Add an event handler that handles a successful result of updateTask.
        updateTask.setOnSucceeded(event -> {

            try {
                // Try to update the UI and logic.
                if (Utils.currentController != null) Utils.currentController.update();

            } catch (IOException e) {
                // Handle Interrupted state.
                throw new RuntimeException(e);
            }

            Utils.log("Successfully updated.", 2);

        });

        // Add an event handler that handles a unsuccessful result of updateTask.
        updateTask.setOnFailed(event -> {
            // Get exception.
            Throwable exception = updateTask.getException();
            Utils.log("Update failed: " + exception.getMessage(), 5);
        });

        // Run task in a new thread.
        Thread thread = new Thread(updateTask);
        // Prevent app from hanging on exit.
        thread.setDaemon(true);
        // Start thread process.
        thread.start();
    }

}
