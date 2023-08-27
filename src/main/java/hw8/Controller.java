package hw8;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import java.util.List;
import java.util.Map;

@RestController
public class Controller {
    static EntityManagerFactory emf;
    static EntityManager em;
    @GetMapping("/transactions")
    public List<Transaction> viewAllTransactions() {
        emf = Persistence.createEntityManagerFactory("bank");
        em = emf.createEntityManager();
        List<Transaction> transactions = em.createQuery("SELECT t FROM Transaction t", Transaction.class).getResultList();
        em.close();
        return transactions;
    }

    @GetMapping("/total-balance")
    public  Map<String, Double> viewTotalBalanceAllClients() {

        emf = Persistence.createEntityManagerFactory("bank");
        em = emf.createEntityManager();

        double UAH = 0.0;
        double EUR = 0.0;
        double USD = 0.0;

        List<User> users = em.createQuery("SELECT a FROM User a", User.class).getResultList();

        ExchangeRate er = new ExchangeRate();
        for (User user : users) {
            UAH += user.getBankAccount().getUah();
            EUR += user.getBankAccount().getEur() * er.getEur();
            USD += user.getBankAccount().getUsd() * er.getUsd();
        }

        Map<String, Double> totalBalance = Map.of("UAH", UAH, "EUR", EUR, "USD", USD);
        em.close();

        return totalBalance;
    }
}
