package edu.udelar.pap.repository.impl;

import edu.udelar.pap.domain.Prestamo;
import edu.udelar.pap.domain.EstadoPrestamo;
import edu.udelar.pap.repository.PrestamoRepository;
import edu.udelar.pap.persistence.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import java.util.List;
import java.util.Optional;
import java.time.LocalDate;

/**
 * Implementación del repositorio de préstamos usando Hibernate
 * Implementa el patrón Repository para desacoplar la lógica de acceso a datos
 */
public class PrestamoRepositoryImpl implements PrestamoRepository {
    
    private final SessionFactory sessionFactory;
    
    public PrestamoRepositoryImpl() {
        this.sessionFactory = HibernateUtil.getSessionFactory();
    }
    
    @Override
    public void guardar(Prestamo prestamo) {
        try (Session session = sessionFactory.openSession()) {
            Transaction tx = session.beginTransaction();
            session.persist(prestamo);
            tx.commit();
        }
    }
    
    @Override
    public void actualizar(Prestamo prestamo) {
        try (Session session = sessionFactory.openSession()) {
            Transaction tx = session.beginTransaction();
            session.merge(prestamo);
            tx.commit();
        }
    }
    
    @Override
    public void eliminar(Prestamo prestamo) {
        try (Session session = sessionFactory.openSession()) {
            Transaction tx = session.beginTransaction();
            session.remove(prestamo);
            tx.commit();
        }
    }
    
    @Override
    public Optional<Prestamo> buscarPorId(Long id) {
        try (Session session = sessionFactory.openSession()) {
            Prestamo prestamo = session.get(Prestamo.class, id);
            return Optional.ofNullable(prestamo);
        }
    }
    
    @Override
    public List<Prestamo> obtenerTodos() {
        try (Session session = sessionFactory.openSession()) {
            Query<Prestamo> query = session.createQuery("FROM Prestamo", Prestamo.class);
            return query.list();
        }
    }
    
    @Override
    public List<Prestamo> obtenerPorLector(Long lectorId) {
        try (Session session = sessionFactory.openSession()) {
            Query<Prestamo> query = session.createQuery(
                "FROM Prestamo p WHERE p.lector.id = :lectorId", Prestamo.class);
            query.setParameter("lectorId", lectorId);
            return query.list();
        }
    }
    
    @Override
    public List<Prestamo> obtenerPorBibliotecario(Long bibliotecarioId) {
        try (Session session = sessionFactory.openSession()) {
            Query<Prestamo> query = session.createQuery(
                "FROM Prestamo p WHERE p.bibliotecario.id = :bibliotecarioId", Prestamo.class);
            query.setParameter("bibliotecarioId", bibliotecarioId);
            return query.list();
        }
    }
    
    @Override
    public List<Prestamo> obtenerPorEstado(EstadoPrestamo estado) {
        try (Session session = sessionFactory.openSession()) {
            Query<Prestamo> query = session.createQuery(
                "FROM Prestamo p WHERE p.estado = :estado", Prestamo.class);
            query.setParameter("estado", estado);
            return query.list();
        }
    }
    
    @Override
    public List<Prestamo> obtenerPrestamosVencidos() {
        try (Session session = sessionFactory.openSession()) {
            Query<Prestamo> query = session.createQuery(
                "FROM Prestamo p WHERE p.fechaDevolucion < :fechaActual AND p.estado = :estado", 
                Prestamo.class);
            query.setParameter("fechaActual", LocalDate.now());
            query.setParameter("estado", EstadoPrestamo.EN_CURSO);
            return query.list();
        }
    }
    
    @Override
    public List<Object[]> obtenerMaterialesPendientes() {
        try (Session session = sessionFactory.openSession()) {
            Query<Object[]> query = session.createQuery(
                "SELECT p.material, COUNT(p) as cantidad " +
                "FROM Prestamo p " +
                "WHERE p.estado = :estado " +
                "GROUP BY p.material " +
                "ORDER BY cantidad DESC", Object[].class);
            query.setParameter("estado", EstadoPrestamo.EN_CURSO);
            return query.list();
        }
    }
    
    @Override
    public long contarPorEstado(EstadoPrestamo estado) {
        try (Session session = sessionFactory.openSession()) {
            Query<Long> query = session.createQuery(
                "SELECT COUNT(p) FROM Prestamo p WHERE p.estado = :estado", Long.class);
            query.setParameter("estado", estado);
            return query.uniqueResult();
        }
    }
    
    @Override
    public long contarPrestamosVencidos() {
        try (Session session = sessionFactory.openSession()) {
            Query<Long> query = session.createQuery(
                "SELECT COUNT(p) FROM Prestamo p " +
                "WHERE p.fechaDevolucion < :fechaActual AND p.estado = :estado", Long.class);
            query.setParameter("fechaActual", LocalDate.now());
            query.setParameter("estado", EstadoPrestamo.EN_CURSO);
            return query.uniqueResult();
        }
    }
}
