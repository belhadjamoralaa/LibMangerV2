package librarymanagement;

import java.util.List;

/**
 * Generic DAO interface that defines standard CRUD operations
 * @param <T> The entity type
 * @param <ID> The ID type
 */
public interface DAO<T, ID> {
    /**
     * Save an entity to the database
     * @param entity The entity to save
     */
    void create(T entity);
    
    /**
     * Find an entity by its ID
     * @param id The ID to search for
     * @return The found entity or null
     */
    T findById(ID id);
    
    /**
     * Find all entities
     * @return A list of all entities
     */
    List<T> findAll();
    
    /**
     * Update an entity
     * @param entity The entity to update
     */
    void update(T entity);
    
    /**
     * Delete an entity by its ID
     * @param id The ID of the entity to delete
     */
    void delete(ID id);
}