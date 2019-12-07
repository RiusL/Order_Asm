package objects;

/**
 * Created by quang on 22-Jan-18.
 */

public class Table {
    private int number;
    private String position;
    private int numberOfChair;
    private int check;
    private String note;

    public Table(int number, String position, int numberOfChair, int check, String note) {
        this.number = number;
        this.position = position;
        this.numberOfChair = numberOfChair;
        this.check = check;
        this.note = note;
    }

    public int getNumber() {
        return number;
    }

    public String getPosition() {
        return position;
    }

    public int getNumberOfChair() {
        return numberOfChair;
    }

    public int getCheck() {
        return check;
    }

    public String getNote() {
        return note;
    }
}
