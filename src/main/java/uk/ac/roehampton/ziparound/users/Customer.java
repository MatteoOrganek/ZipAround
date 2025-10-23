/**
 * Customer.java
 *
 * @author Matteo Organek
 * @version 1.0
 * @since 22/10/2025
 */

package uk.ac.roehampton.ziparound.users;

public class Customer extends User {
    public Customer(Integer userID, String foreName, String lastname) {
        this.userID = userID;
        this.foreName = foreName;
        this.lastname = lastname;
    }
}
