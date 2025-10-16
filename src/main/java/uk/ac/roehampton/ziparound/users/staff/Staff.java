package uk.ac.roehampton.ziparound.users.staff;

import uk.ac.roehampton.ziparound.users.User;

public class Staff extends User implements Permissions {

    private Integer accessLevel;

    public Integer getAccessLevel() {
        return accessLevel;
    }

    public void setAccessLevel(Integer accessLevel) {
        this.accessLevel = accessLevel;
    }
}
