package ph.edu.dlsu.mobapde.tara;

/**
 * Created by louis on 11/13/2017.
 */

public class Request {
    String race_id;
    String sender;

    private int listPosition;

    public Request() {

    }

    public Request(String race_id, String sender) {
        this.race_id = race_id;
        this.sender = sender;
    }

    public String getRace_id() {
        return race_id;
    }

    public void setRace_id(String race_id) {
        this.race_id = race_id;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public int getListPosition() {
        return listPosition;
    }

    public void setListPosition(int listPosition) {
        this.listPosition = listPosition;
    }
}
