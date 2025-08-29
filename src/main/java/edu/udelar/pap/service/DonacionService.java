package edu.udelar.pap.service;

import edu.udelar.pap.domain.Libro;
import edu.udelar.pap.domain.ArticuloEspecial;
import edu.udelar.pap.persistence.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import java.util.List;
import java.time.LocalDate;

/**
 * Servicio para la gestión de donaciones
 * Maneja la lógica de negocio y acceso a datos
 */
public class DonacionService {
    
    private final SessionFactory sessionFactory;
    
    public DonacionService() {
        this.sessionFactory = HibernateUtil.getSessionFactory();
    }
    
    /**
     * Guarda un nuevo libro en la base de datos
     */
    public void guardarLibro(Libro libro) {
        try (Session session = sessionFactory.openSession()) {
            Transaction tx = session.beginTransaction();
            session.persist(libro);
            tx.commit();
        }
    }
    
    /**
     * Guarda un nuevo artículo especial en la base de datos
     */
    public void guardarArticuloEspecial(ArticuloEspecial articulo) {
        try (Session session = sessionFactory.openSession()) {
            Transaction tx = session.beginTransaction();
            session.persist(articulo);
            tx.commit();
        }
    }
    
    /**
     * Actualiza un libro existente
     */
    public void actualizarLibro(Libro libro) {
        try (Session session = sessionFactory.openSession()) {
            Transaction tx = session.beginTransaction();
            session.merge(libro);
            tx.commit();
        }
    }
    
    /**
     * Actualiza un artículo especial existente
     */
    public void actualizarArticuloEspecial(ArticuloEspecial articulo) {
        try (Session session = sessionFactory.openSession()) {
            Transaction tx = session.beginTransaction();
            session.merge(articulo);
            tx.commit();
        }
    }
    
    /**
     * Elimina un libro
     */
    public void eliminarLibro(Libro libro) {
        try (Session session = sessionFactory.openSession()) {
            Transaction tx = session.beginTransaction();
            session.remove(libro);
            tx.commit();
        }
    }
    
    /**
     * Elimina un artículo especial
     */
    public void eliminarArticuloEspecial(ArticuloEspecial articulo) {
        try (Session session = sessionFactory.openSession()) {
            Transaction tx = session.beginTransaction();
            session.remove(articulo);
            tx.commit();
        }
    }
    
    /**
     * Obtiene un libro por ID
     */
    public Libro obtenerLibroPorId(Long id) {
        try (Session session = sessionFactory.openSession()) {
            return session.get(Libro.class, id);
        }
    }
    
    /**
     * Obtiene un artículo especial por ID
     */
    public ArticuloEspecial obtenerArticuloEspecialPorId(Long id) {
        try (Session session = sessionFactory.openSession()) {
            return session.get(ArticuloEspecial.class, id);
        }
    }
    
    /**
     * Obtiene todos los libros disponibles
     */
    public List<Libro> obtenerLibrosDisponibles() {
        try (Session session = sessionFactory.openSession()) {
            return session.createQuery("FROM Libro ORDER BY titulo", Libro.class).list();
        }
    }
    
    /**
     * Obtiene todos los artículos especiales disponibles
     */
    public List<ArticuloEspecial> obtenerArticulosEspecialesDisponibles() {
        try (Session session = sessionFactory.openSession()) {
            return session.createQuery("FROM ArticuloEspecial ORDER BY descripcion", ArticuloEspecial.class).list();
        }
    }
    
    /**
     * Busca libros por título
     */
    public List<Libro> buscarLibrosPorTitulo(String titulo) {
        try (Session session = sessionFactory.openSession()) {
            return session.createQuery(
                "FROM Libro WHERE titulo LIKE :titulo ORDER BY titulo", 
                Libro.class)
                .setParameter("titulo", "%" + titulo + "%")
                .list();
        }
    }
    
    /**
     * Busca artículos especiales por descripción
     */
    public List<ArticuloEspecial> buscarArticulosPorDescripcion(String descripcion) {
        try (Session session = sessionFactory.openSession()) {
            return session.createQuery(
                "FROM ArticuloEspecial WHERE descripcion LIKE :descripcion ORDER BY descripcion", 
                ArticuloEspecial.class)
                .setParameter("descripcion", "%" + descripcion + "%")
                .list();
        }
    }
    
    /**
     * Verifica si existe un libro con el título dado
     */
    public boolean existeLibroConTitulo(String titulo) {
        try (Session session = sessionFactory.openSession()) {
            return session.createQuery(
                "FROM Libro WHERE titulo = :titulo", 
                Libro.class)
                .setParameter("titulo", titulo)
                .uniqueResult() != null;
        }
    }
    
    /**
     * Obtiene todas las donaciones (libros y artículos especiales) ordenadas por fecha de ingreso
     */
    public List<Object> obtenerTodasLasDonaciones() {
        try (Session session = sessionFactory.openSession()) {
            // Obtener libros
            List<Libro> libros = session.createQuery(
                "FROM Libro ORDER BY fechaIngreso DESC", 
                Libro.class).list();
            
            // Obtener artículos especiales
            List<ArticuloEspecial> articulos = session.createQuery(
                "FROM ArticuloEspecial ORDER BY fechaIngreso DESC", 
                ArticuloEspecial.class).list();
            
            // Combinar y ordenar por fecha de ingreso
            List<Object> todasLasDonaciones = new java.util.ArrayList<>();
            todasLasDonaciones.addAll(libros);
            todasLasDonaciones.addAll(articulos);
            
            // Ordenar por fecha de ingreso (más recientes primero)
            todasLasDonaciones.sort((o1, o2) -> {
                LocalDate fecha1 = null;
                LocalDate fecha2 = null;
                
                if (o1 instanceof Libro) {
                    fecha1 = ((Libro) o1).getFechaIngreso();
                } else if (o1 instanceof ArticuloEspecial) {
                    fecha1 = ((ArticuloEspecial) o1).getFechaIngreso();
                }
                
                if (o2 instanceof Libro) {
                    fecha2 = ((Libro) o2).getFechaIngreso();
                } else if (o2 instanceof ArticuloEspecial) {
                    fecha2 = ((ArticuloEspecial) o2).getFechaIngreso();
                }
                
                if (fecha1 == null && fecha2 == null) return 0;
                if (fecha1 == null) return 1;
                if (fecha2 == null) return -1;
                
                return fecha2.compareTo(fecha1); // Orden descendente
            });
            
            return todasLasDonaciones;
        }
    }
}
