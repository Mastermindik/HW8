package hw8;

import javax.persistence.*;

@Entity
@Table(name = "Transactions")
public class Transaction {
    @Id
    @GeneratedValue
    private long id;

    @Column(nullable = false)
    private String type;

    @Column(nullable = false)
    private Double sum;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    public Transaction() {
    }

    public Transaction(String type, Double sum) {
        this.type = type;
        this.sum = sum;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Double getSum() {
        return sum;
    }

    public void setSum(Double sum) {
        this.sum = sum;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
