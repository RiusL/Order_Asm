package objects;

import java.io.Serializable;

/**
 * Created by quang on 24-Jan-18.
 */

public class ItemMenu implements Serializable{
    private String group;
    private String name;
    private String price;
    private String unit;
    private String check;
    private String image;
    private int count = 0;

    public ItemMenu(String group, String name, String price, String unit, String check, String image) {
        this.group = group;
        this.name = name;
        this.price = price;
        this.unit = unit;
        this.check = check;
        this.image = image;
        this.count = 0;
    }

    public String getGroup() {
        return group;
    }

    public String getName() {
        return name;
    }

    public String getPrice() {
        return price;
    }

    public String getUnit() {
        return unit;
    }

    public String getCheck() {
        return check;
    }

    public String getImage() {
        return image;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }
}
