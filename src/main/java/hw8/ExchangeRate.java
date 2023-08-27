package hw8;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "ExchangeRates")
public class ExchangeRate {
    @Id
    @GeneratedValue
    private Long id;

    private String currencyName;
    private Double usd;
    private Double eur;
    private Double uah;

    public ExchangeRate() {
    }

    public ExchangeRate(String currencyName, Double usd, Double eur, Double uah) {
        this.currencyName = currencyName;
        this.usd = usd;
        this.eur = eur;
        this.uah = uah;
    }

    public String getCurrencyName() {
        return currencyName;
    }

    public void setCurrencyName(String currencyName) {
        this.currencyName = currencyName;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Double getUsd() {
        return usd;
    }

    public void setUsd(Double usd) {
        this.usd = usd;
    }

    public Double getEur() {
        return eur;
    }

    public void setEur(Double eur) {
        this.eur = eur;
    }

    public Double getUah() {
        return uah;
    }

    public void setUah(Double uah) {
        this.uah = uah;
    }
}
