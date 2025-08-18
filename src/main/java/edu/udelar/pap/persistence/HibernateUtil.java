package edu.udelar.pap.persistence;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

public final class HibernateUtil {
    private static final SessionFactory SESSION_FACTORY = buildSessionFactory();

    private HibernateUtil() {}

    private static SessionFactory buildSessionFactory() {
        try {
            String db = System.getProperty("db", "mysql");
            String cfg;
            
            switch (db.toLowerCase()) {
                case "mysql":
                    cfg = "hibernate-mysql.cfg.xml";
                    break;
                case "mysql-team":
                    cfg = "hibernate-mysql-team.cfg.xml";
                    break;
                case "h2":
                default:
                    cfg = "hibernate-h2.cfg.xml";
                    break;
            }
            
            Configuration configuration = new Configuration();
            configuration.configure(cfg);
            return configuration.buildSessionFactory();
        } catch (Exception ex) {
            throw new RuntimeException("Error inicializando Hibernate SessionFactory", ex);
        }
    }

    public static SessionFactory getSessionFactory() {
        return SESSION_FACTORY;
    }
}


