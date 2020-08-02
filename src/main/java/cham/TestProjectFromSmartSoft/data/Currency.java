package cham.TestProjectFromSmartSoft.data;

import javax.persistence.*;

@Entity
public class Currency {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    private String name;
    private Double rate;
    private String updateDate;

    public Currency(){
    }

    public Currency(String name, Double rate, String update_date) {
        this.name = name;
        this.rate = rate;
        this.updateDate = update_date;
    }

    public Integer getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Double getRate() {
        return rate;
    }

    public String getUpdate_date() {
        return updateDate;
    }
}
