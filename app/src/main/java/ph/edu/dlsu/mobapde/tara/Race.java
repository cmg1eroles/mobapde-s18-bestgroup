package ph.edu.dlsu.mobapde.tara;

import com.google.android.gms.location.places.Place;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

/**
 * Created by louis on 11/8/2017.
 */

public class Race {
    private long id;
    private String title;
    private Place location;
    private Date date;

    private ArrayList<User> users;
    private boolean inProgress;

    // for placeholder only
    public Race(String title) {
        this.title = title;
    }

    public Race(String title, Place location, Date date) {
        this.title = title;
        this.location = location;
        this.date = date;
        users = new ArrayList<User>();
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Place getLocation() {
        return location;
    }

    public void setLocation(Place location) {
        this.location = location;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public ArrayList<User> getUsers() {
        return users;
    }

    public void setUsers(ArrayList<User> users) {
        this.users = users;
    }

    public boolean addUser(User u) {
        if(u == null)
            return false;

        return true;
    }

    // remove user

    public User getUser(int id) {
        return users.get(id);
    }

    public boolean isInProgress() {
        return inProgress;
    }

    public void setInProgress(boolean inProgress) {
        this.inProgress = inProgress;
    }

    public HashMap<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();

        result.put("title", title);
        result.put("location", location.getLatLng());
        result.put("date", date);

        return result;
    }

    @Override
    public String toString() {
        return "Race{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", location=" + location +
                ", date=" + date +
                ", users=" + users +
                ", inProgress=" + inProgress +
                '}';
    }
}
