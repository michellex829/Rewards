package com.michelleweixu.rewards.userdata;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class UserProfile implements Comparable<UserProfile>, Serializable {
    public String username;
    public String firstName;
    public String lastName;
    public String department;
    public String story;
    public String position;
    public String password;

    public void setLocation(String location) {
        this.location = location;
    }

    public String location;
    public String imageString64;
    public int pointsAwarded, remainingPointsToAward;
    public List<UserReward> rewardsList = new ArrayList<>();

    public UserProfile(String firstName, String lastName,
                       String username, String department, String story, String position,
                       String password, String location, String imageString64,
                       int remainingPointsToAward, List<UserReward> rewardsHistory) {
        this.remainingPointsToAward = remainingPointsToAward;
        this.firstName = firstName;
        this.lastName = lastName;
        this.username = username;
        this.department = department;
        this.story = story;
        this.position = position;
        this.password = password;
        this.location = location;
        this.imageString64 = imageString64;
        this.rewardsList = rewardsHistory;

        if (rewardsList != null && rewardsList.size() != 0) {
            for (UserReward reward : this.rewardsList)
                this.pointsAwarded += reward.amount;
        }
    }

    @Override
    public int compareTo(UserProfile user) { // to sort and display officials by last names alphabetically
        Integer pointsOne = this.pointsAwarded;
        Integer pointsTwo = user.pointsAwarded;

        return pointsTwo.compareTo(pointsOne);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserProfile user = (UserProfile) o;
        return this.username.equals(user.username);
    }

}
