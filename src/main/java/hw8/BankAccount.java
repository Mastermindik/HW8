package hw8;

import javax.persistence.*;

@Entity
@Table(name = "Accounts")
public class BankAccount {
    @Id
    @GeneratedValue
    private Long id;

    private Double usd;

    private Double eur;

    private Double uah;

    @OneToOne(mappedBy = "bankAccount")
    private User user;

    public BankAccount() {
    }

    public BankAccount(Double usd, Double eur, Double uah) {
        this.usd = usd;
        this.eur = eur;
        this.uah = uah;
    }

    public void addMoney(double sum, String currency) {
        if (currency.equals("usd")) {
            this.usd = this.usd + sum;
        } else if (currency.equals("uer")) {
            this.eur = this.eur + sum;
        } else if (currency.equals("ush")) {
            this.uah = this.uah + sum;
        }
    }

    public void removeMoney(double sum, String currency) {
        if (currency.equals("usd")) {
            this.usd = this.usd - sum;
        } else if (currency.equals("uer")) {
            this.eur = this.eur - sum;
        } else if (currency.equals("ush")) {
            this.uah = this.uah - sum;
        }
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

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}