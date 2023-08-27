package hw8;

import javax.persistence.*;
import java.lang.reflect.Field;
import java.util.Scanner;
import java.util.concurrent.Callable;

public class App {
    static EntityManagerFactory emf;
    static EntityManager em;

    public static void main(String[] args) {
        try (Scanner sc = new Scanner(System.in)) {
            emf = Persistence.createEntityManagerFactory("bank");
            em = emf.createEntityManager();
            ExchangeRate er = em.find(ExchangeRate.class, 1L);
            if (er == null) {
                setRates();
            }
            try {
                while (true) {
                    System.out.println("1: add client");
                    System.out.println("2: refill account");
                    System.out.println("3: send money from account to account");
                    System.out.println("4: convert currencies between accounts");
                    System.out.println("5: show total amount on accounts in UAH");
                    System.out.print("-> ");

                    String s = sc.nextLine();
                    switch (s) {
                        case "1" -> initUser(sc);
                        case "2" -> refill(sc);
                        case "3" -> transferMoney(sc);
                        case "4" -> convertBetweenAccount(sc);
                        case "5" -> getAllMoney(sc);
                        default -> {
                            return;
                        }
                    }
                }
            } finally {
                emf.close();
                em.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static <T> T performTransaction(Callable<T> action) {
        EntityTransaction transaction = em.getTransaction();
        transaction.begin();
        try {
            T result = action.call();
            transaction.commit();

            return result;
        } catch (Exception ex) {
            if (transaction.isActive())
                transaction.rollback();

            throw new RuntimeException(ex);
        }
    }

    public static void setRates() {
        ExchangeRate uah = new ExchangeRate("uah", 0.027, 0.025, 1d);
        ExchangeRate usd = new ExchangeRate("usd", 1d, 0.91, 37.12);
        ExchangeRate eur = new ExchangeRate("eur", 1.1, 1d, 40.65);
        performTransaction(() -> {
            em.persist(uah);
            em.persist(usd);
            em.persist(eur);
            return null;
        });
    }

    public static void initUser(Scanner sc) {
        System.out.println("Enter name: ");
        String name = sc.nextLine();
        BankAccount ba = new BankAccount(0d, 0d, 0d);
        User user = new User(name);
        user.setBankAccount(ba);

        performTransaction(() -> {
            em.persist(user);
            return null;
        });
    }

    public static void refill(Scanner sc) {
        System.out.println("Enter owner: ");
        String owner = sc.nextLine();
        System.out.println("Enter sum: ");
        double sum = Double.parseDouble(sc.nextLine());
        System.out.println("Enter currency: ");
        String currency = sc.nextLine();
        TypedQuery<User> userTypedQuery = em.createQuery("SELECT x FROM User x WHERE x.name =: owner", User.class);
        userTypedQuery.setParameter("owner", owner);
        User user = userTypedQuery.getSingleResult();

        performTransaction(() -> {
            BankAccount bankAccount = user.getBankAccount();
            bankAccount.addMoney(sum, currency);
            return null;
        });
    }

    public static void transferMoney(Scanner sc) {
        System.out.println("Enter sender: ");
        String sender = sc.nextLine();
        System.out.println("Enter receiver: ");
        String receiver = sc.nextLine();
        System.out.println("Enter currency: ");
        String currency = sc.nextLine();
        System.out.println("Enter sum: ");
        double sum = Double.parseDouble(sc.nextLine());
        TypedQuery<User> userSenderQuery = em.createQuery("SELECT x FROM User x WHERE x.name =: owner", User.class);
        userSenderQuery.setParameter("owner", sender);
        User userSender = userSenderQuery.getSingleResult();
        TypedQuery<User> userReceiverQuery = em.createQuery("SELECT x FROM User x WHERE x.name =: owner", User.class);
        userReceiverQuery.setParameter("owner", receiver);
        User userReceiver = userReceiverQuery.getSingleResult();

        performTransaction(() -> {
            BankAccount bankAccountFrom = userSender.getBankAccount();
            BankAccount bankAccountTo = userReceiver.getBankAccount();
            bankAccountFrom.removeMoney(sum, currency);
            bankAccountTo.addMoney(sum, currency);
            return null;
        });
    }

    public static void convertBetweenAccount(Scanner sc) {
        System.out.println("Enter owner: ");
        String owner = sc.nextLine();
        System.out.println("From currency: ");
        String typeFrom = sc.nextLine();
        System.out.println("To currency: ");
        String typeTo = sc.nextLine();
        System.out.println("Enter sum: ");
        double sum = Double.parseDouble(sc.nextLine());
        TypedQuery<User> userTypedQuery = em.createQuery("SELECT x FROM  User x WHERE x.name =: owner", User.class);
        userTypedQuery.setParameter("owner", owner);
        User user = userTypedQuery.getSingleResult();
        TypedQuery<ExchangeRate> rateTypedQuery =
                em.createQuery("SELECT x FROM ExchangeRate x WHERE x.currencyName =: typeFrom", ExchangeRate.class);
        rateTypedQuery.setParameter("typeFrom", typeFrom);
        ExchangeRate er = rateTypedQuery.getSingleResult();

        performTransaction(() -> {
            Field field = er.getClass().getDeclaredField(typeTo);
            field.setAccessible(true);
            double rate = (Double) field.get(er);
            BankAccount bankAccountFrom = user.getBankAccount();
            BankAccount bankAccountTo = user.getBankAccount();
            bankAccountFrom.removeMoney(sum, typeFrom);
            bankAccountTo.addMoney(sum * rate, typeTo);
            return null;
        });
    }

    public static void getAllMoney(Scanner sc) {
        System.out.println("Enter owner: ");
        String owner = sc.nextLine();
        double sum = 0D;
        TypedQuery<User> userTypedQuery = em.createQuery("SELECT x FROM  User x WHERE x.name =: owner", User.class);
        userTypedQuery.setParameter("owner", owner);
        User user = userTypedQuery.getSingleResult();
        TypedQuery<ExchangeRate> uah =
                em.createQuery("SELECT x FROM ExchangeRate x WHERE x.currencyName =: currency", ExchangeRate.class);
        uah.setParameter("currency", "uah");
        ExchangeRate uahRate = uah.getSingleResult();

        BankAccount ba = user.getBankAccount();
        sum = sum + ba.getUah();
        sum = sum + ba.getUsd() / uahRate.getUsd();
        sum = sum + ba.getEur() / uahRate.getEur();
        System.out.println("Sum: " + sum);

    }
}

