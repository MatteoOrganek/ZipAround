package uk.ac.roehampton.ziparound.database;

import java.awt.print.Book;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import javafx.concurrent.Task;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import uk.ac.roehampton.ziparound.Utils;
import uk.ac.roehampton.ziparound.application.Updatable;
import uk.ac.roehampton.ziparound.application.controllers.components.BookingCardController;
import uk.ac.roehampton.ziparound.application.controllers.components.HeaderController;
import uk.ac.roehampton.ziparound.booking.Bookable;
import uk.ac.roehampton.ziparound.booking.Booking;
import uk.ac.roehampton.ziparound.booking.BookingManager;
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

    public ArrayList<Object> getObjects(String table) throws IOException, InterruptedException {

        // Initialize return list
        ArrayList<Object> listObject = new ArrayList<>();

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

                        Bookable bookable = null;
                        int bookableID = Integer.parseInt((String) bookingInfo.get("bookable_id"));

                        // Assemble List of Bookables
                        List<Bookable> listBookable = new ArrayList<>();
                        listBookable.addAll(Utils.bookingManagerInstance.getVehicleArrayList());
                        listBookable.addAll(Utils.bookingManagerInstance.getEquipmentArrayList());

                        for (Bookable currentBookable : listBookable) {
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
                                "1".equals(bookingInfo.get("approved")),
                                staff
                        );

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
                                    Boolean.getBoolean((String) vehicleInfo.get("available")),
                                    Integer.parseInt((String) vehicleInfo.get("max_power_kw")),
                                    Integer.parseInt((String) vehicleInfo.get("amount_of_batteries"))
                            );
                            case "Scooter" -> new Scooter(
                                    Integer.parseInt((String) vehicleInfo.get("id")),
                                    (String) vehicleInfo.get("brand"),
                                    (String) vehicleInfo.get("model"),
                                    (String) vehicleInfo.get("number_plate"),
                                    Float.parseFloat((String) vehicleInfo.get("total_miles")),
                                    Boolean.getBoolean((String) vehicleInfo.get("available")),
                                    Integer.parseInt((String) vehicleInfo.get("max_power_kw")),
                                    Integer.parseInt((String) vehicleInfo.get("amount_of_batteries"))
                            );
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


                        Equipment equipment = new Equipment(
                                Integer.parseInt((String) equipmentInfo.get("id")),
                                (String) equipmentInfo.get("name"),
                                (String) equipmentInfo.get("model"),
                                (String) equipmentInfo.get("description"),
                                Boolean.getBoolean((String) equipmentInfo.get("available"))
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

    public void updateObject(Object object) throws IOException, InterruptedException {
        Utils.log("Updating record...", 3);
        String objectTable = getTable(object);
        updateRecord(objectTable, getMap(object, true));
        update();
    }

    public void addObject(Object object) throws IOException, InterruptedException {
        Utils.log("Adding record...", 3);
        String objectTable = getTable(object);
        addRecord(objectTable, getMap(object, false));
        update();
    }

    public void deleteObject(Object object) throws IOException, InterruptedException {
        Utils.log("Deleting record...", 3);
        String objectTable = getTable(object);
        deleteRecord(objectTable, getMap(object, true).get("id"));
        update();
    }

    private Map<String, Object> getMap(Object object, Boolean idRequired) {

        Map<String, Object> objectInfo = new HashMap<>();

        if (object instanceof Booking) {

            Booking booking = (Booking) object;

            LocalDateTime start = LocalDateTime.ofInstant(booking.getBookedStartTime(Utils.currentStaff), ZoneId.systemDefault());
            LocalDateTime end = LocalDateTime.ofInstant(booking.getBookedEndTime(Utils.currentStaff), ZoneId.systemDefault());
            LocalDateTime createdOn = LocalDateTime.ofInstant(booking.getCreatedOn(Utils.currentStaff), ZoneId.systemDefault());

            if (idRequired) objectInfo.put("id", booking.getID(Utils.currentStaff));
            objectInfo.put("booked_start_time", start.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
            objectInfo.put("booked_end_time", end.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
            objectInfo.put("created_on", createdOn.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
            objectInfo.put("user_id", booking.getUser(Utils.currentStaff).getID(Utils.currentStaff));
            objectInfo.put("bookable_id", booking.getBookableObject(Utils.currentStaff).getID(Utils.currentStaff));
            objectInfo.put("approved", booking.getApproved(Utils.currentStaff));
            objectInfo.put("staff_approved_id", ((User) booking.getStaff(Utils.currentStaff)).getID(Utils.currentStaff));

        } else if (object instanceof Customer) {

            Customer customer = (Customer) object;
            if (idRequired) objectInfo.put("id", customer.getID(Utils.currentStaff));
            objectInfo.put("fore_name", customer.getForeName(Utils.currentStaff));
            objectInfo.put("last_name", customer.getLastName(Utils.currentStaff));

        } else if (object instanceof Staff) {

            Staff staff = (Staff) object;
            if (idRequired) objectInfo.put("id", staff.getStaffID(Utils.currentStaff));
            objectInfo.put("user_id", staff.getID(Utils.currentStaff));

            if (staff instanceof Admin) objectInfo.put("department_id", 1);
            if (staff instanceof Manager) objectInfo.put("department_id", 2);
            if (staff instanceof BookingAgent) objectInfo.put("department_id", 3);

        } else if (object instanceof Vehicle) {

            Vehicle vehicle = (Vehicle) object;
            if (idRequired) objectInfo.put("id", vehicle.getID(Utils.currentStaff));
            objectInfo.put("name", vehicle.getName(Utils.currentStaff));
            objectInfo.put("model", vehicle.getModel(Utils.currentStaff));
            objectInfo.put("type", vehicle.getType(Utils.currentStaff));
            objectInfo.put("number_plate", vehicle.getNumberPlate(Utils.currentStaff));
            objectInfo.put("total_miles", vehicle.getTotalMiles(Utils.currentStaff));
            objectInfo.put("available", vehicle.isAvailable(Utils.currentStaff));

            Boolean isElectric = vehicle instanceof EBike || vehicle instanceof Scooter;
            if (isElectric) objectInfo.put("max_power_kw", ((Electric) vehicle).getMaxPowerKw(Utils.currentStaff));
            if (isElectric) objectInfo.put("amount_of_batteries", ((Electric) vehicle).getAmountOfBatteries(Utils.currentStaff));

        } else if (object instanceof Equipment) {

            Equipment equipment = (Equipment) object;
            if (idRequired) objectInfo.put("id", equipment.getID(Utils.currentStaff));
            objectInfo.put("name", equipment.getName(Utils.currentStaff));
            objectInfo.put("model", equipment.getModel(Utils.currentStaff));
            objectInfo.put("description", equipment.getDescription(Utils.currentStaff));
            objectInfo.put("available", equipment.isAvailable(Utils.currentStaff));
        }
        if (!objectInfo.isEmpty()) {
            return objectInfo;
        } else {
            throw new Error("Unhandled case!");
        }
    }

    private String getTable(Object object) {
        if (object instanceof Booking) {
            return "booking";
        } else if (object instanceof Vehicle) {
            return "vehicle";
        }  else if (object instanceof Equipment) {
            return "equipment";
        } else if (object instanceof Customer) {
            return "users";
        } else if (object instanceof Staff) {
            return "staff";
        } else {
            throw new Error("Unhandled case!");
        }
    }

    // Update data using different threads from ui thread
    public void update() {

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

                return true;
            }
        };


        Utils.currentController.getHeaderController().loadBar.progressProperty().bind(updateTask.progressProperty());
        // Disable entire UI while task runs
        Utils.currentScene.getRoot().disableProperty().bind(updateTask.runningProperty());

        updateTask.setOnSucceeded(event -> {
            try {
                Utils.currentController.update();

            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            Utils.log("Successfully updated.", 2);
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
