package objects;

import java.io.Serializable;

/**
 * Created by quang on 21-Jan-18.
 */

public class User implements Serializable{
    private String id;
    private String name;
    private String sex;
    private String dateOfBirth;
    private String address;
    private String phone;
    private String position;
    private String dateOfstart;
    private String salaryPerDay;
    private String userPass;
    private String image;

    public User(String id, String name, String sex, String dateOfBirth, String address
            , String phone, String position, String dateOfstart, String salaryPerDay, String userPass, String image) {
        this.id = id;
        this.name = name;
        this.sex = sex;
        this.dateOfBirth = dateOfBirth;
        this.address = address;
        this.phone = phone;
        this.position = position;
        this.dateOfstart = dateOfstart;
        this.salaryPerDay = salaryPerDay;
        this.userPass = userPass;
        this.image = image;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
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

    public String getPosition() {
        return position;
    }

    public String getDateOfstart() {
        return dateOfstart;
    }

    public String getSalaryPerDay() {
        return salaryPerDay;
    }

    public String getUserPass() {
        return userPass;
    }

    public String getImage() {
        return image;
    }
}
