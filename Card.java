public class Card {
    private String color; // "blue", "yellow", "red", "green", or "" for special cards
    private int value; // 1 to 10, or value for special cards
    private String sign; // "+", "-", "flip", "x2", or "" for normal cards

    public Card(String color, int value, String sign) {
        this.color = color;
        this.value = value;
        this.sign = sign;
    }

    // Getters and setters
    public String getColor() { return color; }
    public void setColor(String color) { this.color = color; }
    public int getValue() { return value; }
    public void setValue(int value) { this.value = value; }
    public String getSign() { return sign; }
    public void setSign(String sign) { this.sign = sign; }

    @Override
    public String toString() {
        return (color.isEmpty() ? "" : color + " ") + value + (sign.isEmpty() ? "" : " " + sign);
    }
}
