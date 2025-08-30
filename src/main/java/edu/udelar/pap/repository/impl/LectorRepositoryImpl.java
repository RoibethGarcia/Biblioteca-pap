package edu.udelar.pap.repository.impl;

import edu.udelar.pap.repository.LectorRepository;
import edu.udelar.pap.domain.Lector;
import edu.udelar.pap.domain.EstadoLector;
import edu.udelar.pap.persistence.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import java.util.List;

/**
 * Implementación del Repository para Lector usando Hibernate
 */
public class LectorRepositoryImpl implements LectorRepository {
    
    private final SessionFactory sessionFactory;
    
    public LectorRepositoryImpl() {
        this.sessionFactory = HibernateUtil.getSessionFactory();
    }
    
    @Override
    public void save(Lector lector) {
        try (Session session = sessionFactory.openSession()) {
            Transaction tx = session.beginTransaction();
            session.persist(lector);
            tx.commit();
        }
    }
    
    @Override
    public void update(Lector lector) {
        try (Session session = sessionFactory.openSession()) {
            Transaction tx = session.beginTransaction();
            session.merge(lector);
            tx.commit();
        }
    }
    
    @Override
    public void delete(Lector lector) {
        try (Session session = sessionFactory.openSession()) {
            Transaction tx = session.beginTransaction();
            session.remove(lector);
            tx.commit();
        }
    }
    
    @Override
    public Lector findById(Long id) {
        try (Session session = sessionFactory.openSession()) {
            return session.get(Lector.class, id);
        }
    }
    
    @Override
    public List<Lector> findAll() {
        try (Session session = sessionFactory.openSession()) {
            return session.createQuery("FROM Lector ORDER BY nombre", Lector.class).list();
        }
    }
    
    @Override
    public List<Lector> findByEstado(EstadoLector estado) {
        try (Session session = sessionFactory.openSession()) {
            return session.createQuery("FROM Lector WHERE estado = :estado ORDER BY nombre", Lector.class)
                .setParameter("estado", estado)
                .list();
        }
    }
    
    @Override
    public List<Lector> findByNombreContaining(String nombre) {
        try (Session session = sessionFactory.openSession()) {
            return session.createQuery(
                "FROM Lector WHERE LOWER(nombre) LIKE LOWER(:nombre) ORDER BY nombre", 
                Lector.class)
                .setParameter("nombre", "%" + nombre + "%")
                .list();
        }
    }
    
    @Override
    public Lector findByEmail(String email) {
        try (Session session = sessionFactory.openSession()) {
            return session.createQuery("FROM Lector WHERE email = :email", Lector.class)
                .setParameter("email", email)
                .uniqueResult();
        }
    }
    
    @Override
    public List<Lector> findByNombreAndApellido(String nombre, String apellido) {
        try (Session session = sessionFactory.openSession()) {
            String hql;
            if (nombre != null && !nombre.trim().isEmpty() && apellido != null && !apellido.trim().isEmpty()) {
                hql = "FROM Lector WHERE LOWER(nombre) LIKE LOWER(:nombre) AND LOWER(nombre) LIKE LOWER(:apellido) ORDER BY nombre";
                return session.createQuery(hql, Lector.class)
                    .setParameter("nombre", "%" + nombre + "%")
                    .setParameter("apellido", "%" + apellido + "%")
                    .list();
            } else if (nombre != null && !nombre.trim().isEmpty()) {
                hql = "FROM Lector WHERE LOWER(nombre) LIKE LOWER(:nombre) ORDER BY nombre";
                return session.createQuery(hql, Lector.class)
                    .setParameter("nombre", "%" + nombre + "%")
                    .list();
            } else if (apellido != null && !apellido.trim().isEmpty()) {
                hql = "FROM Lector WHERE LOWER(nombre) LIKE LOWER(:apellido) ORDER BY nombre";
                return session.createQuery(hql, Lector.class)
                    .setParameter("apellido", "%" + apellido + "%")
                    .list();
            } else {
                return findAll();
            }
        }
    }
    
    @Override
    public boolean existsByEmail(String email) {
        return findByEmail(email) != null;
    }
    
    @Override
    public boolean updateEstado(Long lectorId, EstadoLector nuevoEstado) {
        try (Session session = sessionFactory.openSession()) {
            Transaction tx = session.beginTransaction();
            try {
                Lector lector = session.get(Lector.class, lectorId);
                if (lector != null) {
                    lector.setEstado(nuevoEstado);
                    session.merge(lector);
                    tx.commit();
                    return true;
                }
                tx.rollback();
                return false;
            } catch (Exception e) {
                tx.rollback();
                throw e;
            }
        }
    }
}
