package ph.edu.dlsu.mobapde.tara;

import com.google.android.gms.location.places.Place;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

/**
 * Created by louis on 11/8/2017.
 */

public class Race {
    private String id;
    private String title;
    //private Place location;
    private LatLng location;
    private String locName;
    private Date date;

    private HashMap<String, Boolean> users;
    // private ArrayList<User> users;
    private boolean inProgress;

    public Race(){

    }
    // for placeholder only
    public Race(String title) {
        this.title = title;
    }

    public Race(String title, LatLng location, Date date, String locName) {
        this.title = title;
        this.location = location;
        this.date = date;
        this.locName = locName;
        //users = new ArrayList<User>();
        users = new HashMap<>();
    }


    public Race(String id, Date date, LatLng location, String title, HashMap<String, Boolean> users){
        this.id = id;
        this.date = date;
        this.location = location;
        this.title = title;
        this.users = users;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public LatLng getLocation() {
        return location;
    }

    public void setLocation(LatLng location) {
        this.location = location;
    }

    public String getLocName() {
        return locName;
    }

    public void setLocName(String locName) {
        this.locName = locName;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public HashMap<String, Boolean> getUsers() {
        return users;
    }

    public void setUsers(HashMap<String, Boolean> users) {
        this.users = users;
    }

    public boolean addUser(String u) {
        if(u != null){
            users.put(u, true);
            return true;
        }

        return false;
    }

    // remove user

    public Boolean getUserInRace(int id) {
        return users.containsKey(id);
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
        result.put("location", getLocation());
        result.put("locName", locName);
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