package ph.edu.dlsu.mobapde.tara;

/**
 * Created by louis on 11/13/2017.
 */

public class Request {
    User requester;
    User requestee;
    Race race;
    boolean accepted;

    private int listPosition;

    public Request(User requester, User requestee, Race race, boolean accepted) {
        this.requester = requester;
        this.requestee = requestee;
        this.race = race;
        this.accepted = accepted;
    }

    public User getRequester() {
        return requester;
    }

    public void setRequester(User requester) {
        this.requester = requester;
    }

    public User getRequestee() {
        return requestee;
    }

    public void setRequestee(User requestee) {
        this.requestee = requestee;
    }

    public Race getRace() {
        return race;
    }

    public void setRace(Race race) {
        this.race = race;
    }

    public boolean getAccepted() {
        return accepted;
    }

    public void setAccepted(boolean accepted) {
        this.accepted = accepted;
    }

    public int getListPosition() {
        return listPosition;
    }

    public void setListPosition(int listPosition) {
        this.listPosition = listPosition;
    }

    @Override
    public String toString() {
        return "Request{" +
                "requester=" + requester +
                ", requestee=" + requestee +
                ", race=" + race +
                ", accepted=" + accepted +
                '}';
    }
}
