package edu.udelar.pap.service;

import edu.udelar.pap.domain.Lector;
import edu.udelar.pap.persistence.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import java.util.List;
import edu.udelar.pap.domain.EstadoLector;
import edu.udelar.pap.domain.Zona;

/**
 * Servicio para la gestión de lectores
 * Maneja la lógica de negocio y acceso a datos
 */
public class LectorService {
    
    private final SessionFactory sessionFactory;
    
    public LectorService() {
        this.sessionFactory = HibernateUtil.getSessionFactory();
    }
    
    /**
     * Guarda un nuevo lector en la base de datos
     * Incluye validaciones de negocio
     */
    public void guardarLector(Lector lector) throws IllegalStateException {
        // Validaciones de negocio
        if (lector == null) {
            throw new IllegalStateException("El lector no puede ser nulo");
        }
        
        if (lector.getNombre() == null || lector.getNombre().trim().isEmpty()) {
            throw new IllegalStateException("El nombre del lector es obligatorio");
        }
        
        if (lector.getEmail() == null || lector.getEmail().trim().isEmpty()) {
            throw new IllegalStateException("El email del lector es obligatorio");
        }
        
        if (lector.getDireccion() == null || lector.getDireccion().trim().isEmpty()) {
            throw new IllegalStateException("La dirección del lector es obligatoria");
        }
        
        // Verificar que el email no esté ya en uso
        if (existeLectorConEmail(lector.getEmail())) {
            throw new IllegalStateException("Ya existe un lector con el email: " + lector.getEmail());
        }
        
        // Validar formato de email básico
        if (!lector.getEmail().contains("@") || !lector.getEmail().contains(".")) {
            throw new IllegalStateException("El formato del email no es válido");
        }
        
        try (Session session = sessionFactory.openSession()) {
            Transaction tx = session.beginTransaction();
            session.persist(lector);
            tx.commit();
        }
    }
    
    /**
     * Actualiza un lector existente
     */
    public void actualizarLector(Lector lector) {
        try (Session session = sessionFactory.openSession()) {
            Transaction tx = session.beginTransaction();
            session.merge(lector);
            tx.commit();
        }
    }
    
    /**
     * Elimina un lector
     */
    public void eliminarLector(Lector lector) {
        try (Session session = sessionFactory.openSession()) {
            Transaction tx = session.beginTransaction();
            session.remove(lector);
            tx.commit();
        }
    }
    
    /**
     * Obtiene un lector por ID
     */
    public Lector obtenerLectorPorId(Long id) {
        try (Session session = sessionFactory.openSession()) {
            return session.get(Lector.class, id);
        }
    }
    
    /**
     * Obtiene todos los lectores activos
     */
    public List<Lector> obtenerLectoresActivos() {
        try (Session session = sessionFactory.openSession()) {
            return session.createQuery("FROM Lector WHERE estado = :estado ORDER BY nombre", Lector.class)
                .setParameter("estado", EstadoLector.ACTIVO)
                .list();
        }
    }
    
    /**
     * Obtiene todos los lectores
     */
    public List<Lector> obtenerTodosLosLectores() {
        try (Session session = sessionFactory.openSession()) {
            return session.createQuery("FROM Lector ORDER BY nombre", Lector.class).list();
        }
    }
    
    /**
     * Busca lectores por nombre
     */
    public List<Lector> buscarLectoresPorNombre(String nombre) {
        try (Session session = sessionFactory.openSession()) {
            return session.createQuery(
                "FROM Lector WHERE LOWER(nombre) LIKE LOWER(:nombre) ORDER BY nombre", 
                Lector.class)
                .setParameter("nombre", "%" + nombre + "%")
                .list();
        }
    }
    

    
    /**
     * Busca lectores por email
     */
    public Lector buscarLectorPorEmail(String email) {
        try (Session session = sessionFactory.openSession()) {
            return session.createQuery(
                "FROM Lector WHERE email = :email", 
                Lector.class)
                .setParameter("email", email)
                .uniqueResult();
        }
    }
    
    /**
     * Verifica si existe un lector con el email dado
     */
    public boolean existeLectorConEmail(String email) {
        return buscarLectorPorEmail(email) != null;
    }

    /**
     * Obtiene lectores por estado
     */
    public List<Lector> obtenerLectoresPorEstado(EstadoLector estado) {
        try (Session session = sessionFactory.openSession()) {
            return session.createQuery(
                "FROM Lector WHERE estado = :estado ORDER BY nombre", 
                Lector.class)
                .setParameter("estado", estado)
                .list();
        }
    }

    /**
     * Cambia el estado de un lector
     */
    public boolean cambiarEstadoLector(Long lectorId, EstadoLector nuevoEstado) {
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

    /**
     * Cambia la zona de un lector
     */
    public boolean cambiarZonaLector(Long lectorId, Zona nuevaZona) {
        try (Session session = sessionFactory.openSession()) {
            Transaction tx = session.beginTransaction();
            try {
                Lector lector = session.get(Lector.class, lectorId);
                if (lector != null) {
                    lector.setZona(nuevaZona);
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

    /**
     * Busca lectores por nombre y apellido
     * Nota: Como el modelo actual solo tiene un campo 'nombre', 
     * se busca por nombre completo que puede contener nombre y apellido
     */
    public List<Lector> buscarLectoresPorNombreYApellido(String nombre, String apellido) {
        try (Session session = sessionFactory.openSession()) {
            String hql;
            if (nombre != null && !nombre.trim().isEmpty() && apellido != null && !apellido.trim().isEmpty()) {
                // Buscar por ambos términos en el campo nombre - case insensitive
                hql = "FROM Lector WHERE LOWER(nombre) LIKE LOWER(:nombre) AND LOWER(nombre) LIKE LOWER(:apellido) ORDER BY nombre";
                return session.createQuery(hql, Lector.class)
                    .setParameter("nombre", "%" + nombre + "%")
                    .setParameter("apellido", "%" + apellido + "%")
                    .list();
            } else if (nombre != null && !nombre.trim().isEmpty()) {
                // Buscar solo por nombre - case insensitive
                hql = "FROM Lector WHERE LOWER(nombre) LIKE LOWER(:nombre) ORDER BY nombre";
                return session.createQuery(hql, Lector.class)
                    .setParameter("nombre", "%" + nombre + "%")
                    .list();
            } else if (apellido != null && !apellido.trim().isEmpty()) {
                // Buscar solo por apellido en el campo nombre - case insensitive
                hql = "FROM Lector WHERE LOWER(nombre) LIKE LOWER(:apellido) ORDER BY nombre";
                return session.createQuery(hql, Lector.class)
                    .setParameter("apellido", "%" + apellido + "%")
                    .list();
            } else {
                // Si no hay criterios, devolver todos
                return obtenerTodosLosLectores();
            }
        }
    }
}
