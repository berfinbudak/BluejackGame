// Card class that keeps track of the color, sign and value of a card object.
public class Card {
    private String color; // "blue", "yellow", "red", "green", or "" for special cards
    private int value; // 1 to 10, or value for special cards
    private String sign; // "+", "-", "flip", "x2", or "" for normal cards

    // Card history object
    public Card(String color, int value, String sign) {
        this.color = color;
        this.value = value;
        this.sign = sign;
    }

    // Get methods to get the attributes of the Card object
    public String getColor() { return color; }
    public int getValue() { return value; }
    public String getSign() { return sign; }

    // Set methods to set the attributes of the Card object
    public void setColor(String color) { this.color = color; }
    public void setValue(int value) { this.value = value; }
    public void setSign(String sign) { this.sign = sign; }

    // toString() method to print the Card object
    @Override
    public String toString() {
        return (color.isEmpty() ? "" : color + " ") + value + (sign.isEmpty() ? "" : " " + sign);
    }
}
