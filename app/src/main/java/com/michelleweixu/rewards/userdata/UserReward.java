package com.michelleweixu.rewards.userdata;

import java.io.Serializable;
import java.util.Date;

public class UserReward  implements Comparable<UserReward>, Serializable {
    public String giverName, note, awardDate;
    public int amount;

    public UserReward(String giverName, String note, int amount, String awardDate) {
        this.giverName = giverName;
        this.note = note;
        this.amount = amount;
        this.awardDate = awardDate;
    }

    @Override
    public int compareTo(UserReward r) { // to sort and display officials by last names alphabetically
        return (r.awardDate).compareTo(this.awardDate);
    }

}
