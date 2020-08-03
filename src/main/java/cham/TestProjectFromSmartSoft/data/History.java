package cham.TestProjectFromSmartSoft.data;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class History {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    private String username;
    private String currencyLeft;
    private String currencyRight;
    private String rateLeft;
    private String rateRight;
    private String dateConvert;

    public History(){
    }

    public History(String username, String currencyLeft, String currencyRight, String rateLeft, String rateRight, String dateConvert) {
        this.username = username;
        this.currencyLeft = currencyLeft;
        this.currencyRight = currencyRight;
        this.rateLeft = rateLeft;
        this.rateRight = rateRight;
        this.dateConvert = dateConvert;
    }

    public String getUsername() {
        return username;
    }

    public String getCurrencyLeft() {
        return currencyLeft;
    }

    public String getCurrencyRight() {
        return currencyRight;
    }

    public String getRateLeft() {
        return rateLeft;
    }

    public String getRateRight() {
        return rateRight;
    }

    public String getDateConvert() {
        return dateConvert;
    }
}
