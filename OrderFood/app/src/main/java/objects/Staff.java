package objects;

import java.io.Serializable;

/**
 * Created by quang on 31-Jan-18.
 */

public class Staff implements Serializable{
    private String name;
    private String id;
    private String sex;
    private String dateOfBirth;
    private String address;
    private String phone;
    private String dateStart;
    private String image;
    private int checkOnline;
    private String salary;
    private String userPass;
    private String position;

    public Staff(String name, String id, String sex, String dateOfBirth
            , String address, String phone, String dateStart
            , String image, int checkOnline, String salary) {
        this.name = name;
        this.id = id;
        this.sex = sex;
        this.dateOfBirth = dateOfBirth;
        this.address = address;
        this.phone = phone;
        this.dateStart = dateStart;
        this.image = image;
        this.checkOnline = checkOnline;
        this.salary = salary;
        this.userPass = "";
        this.position = "";
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public String getUserPass() {
        return userPass;
    }

    public void setUserPass(String userPass) {
        this.userPass = userPass;
    }

    public String getSalary() {
        return salary;
    }

    public String getName() {
        return name;
    }

    public String getId() {
        return id;
    }

    public String getSex() {
        return sex;
    }

    public String getDateOfBirth() {
        return dateOfBirth;
    }

    public String getAddress() {
        return address;
    }

    public String getPhone() {
        return phone;
    }

    public String getDateStart() {
        return dateStart;
    }

    public String getImage() {
        return image;
    }

    public int getCheckOnline() {
        return checkOnline;
    }
}
