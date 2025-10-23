/**
 * Equipment.java
 * Handles equipment information and provides methods for management.
 *
 * @author Matteo Organek
 * @version 1.0
 * @since 22/10/2025
 */

package uk.ac.roehampton.ziparound.equipment;

import uk.ac.roehampton.ziparound.Utils;
import uk.ac.roehampton.ziparound.users.staff.Staff;

public class Equipment {

    private final Integer equipmentID;
    private String name;
    private String description;
    private Boolean available;

    // Constructor
    public Equipment(Integer equipmentID, String name, String description, Boolean available) {
        this.equipmentID = equipmentID;
        this.name = name;
        this.description = description;
        this.available = available;
    }

    // Getter for "equipmentID"
    public Integer getID(Staff staff) {
        if (staff.canViewEquipmentInfo()) { return equipmentID; }
        else { throw new SecurityException(Utils.UNAUTHORIZED_ACCESS); }
    }

    // Getter / Setter for "name"
    public String getName(Staff staff) {
        if (staff.canViewEquipmentInfo()) { return name; }
        else { throw new SecurityException(Utils.UNAUTHORIZED_ACCESS); }
    }

    public void setName(String name, Staff staff) {
        if (staff.canModifyEquipment()) { this.name = name; }
        else { throw new SecurityException(Utils.UNAUTHORIZED_MODIFICATION); }
    }

    // Getter / Setter for "description"
    public String getDescription(Staff staff) {
        if (staff.canViewEquipmentInfo()) { return description; }
        else { throw new SecurityException(Utils.UNAUTHORIZED_ACCESS); }
    }

    public void setDescription(String description, Staff staff) {
        if (staff.canModifyEquipment()) { this.description = description; }
        else { throw new SecurityException(Utils.UNAUTHORIZED_MODIFICATION); }
    }

    // Getter / Setter for "available"
    public Boolean isAvailable(Staff staff) {
        if (staff.canViewEquipmentInfo()) { return available; }
        else { throw new SecurityException(Utils.UNAUTHORIZED_ACCESS); }
    }

    public void setAvailable(Boolean available, Staff staff) {
        if (staff.canModifyEquipment()) { this.available = available; }
        else { throw new SecurityException(Utils.UNAUTHORIZED_MODIFICATION); }
    }

}
