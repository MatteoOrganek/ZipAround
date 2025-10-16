package uk.ac.roehampton.ziparound.users.staff.roles;

import uk.ac.roehampton.ziparound.users.staff.Staff;

public class Admin extends Staff {

    @Override public Boolean canModifyBookings() { return true; }

}
