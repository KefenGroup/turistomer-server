package etu.kefengroup.turistomer.dao;

import etu.kefengroup.turistomer.entity.Restaurant;
import jakarta.persistence.EntityManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public class RestaurantDAOImpl implements RestaurantDAO{

    private EntityManager entityManager;

    @Autowired
    public RestaurantDAOImpl(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    @Transactional
    public void save(Restaurant restaurant) {
        entityManager.persist(restaurant);
    }

    @Override
    public Restaurant findById(Integer id) {
        return entityManager.find(Restaurant.class,id);
    }

    /**
     * @implNote
     * To update an entity you can:
     * <code>T obj = DAO.findByID(entity_id);
     * <code>obj.setAttr(value);
     * <code>DAO.update(obj);
     * */
    @Override
    @Transactional
    public void update(Restaurant restaurant) {
        entityManager.merge(restaurant);
    }

    @Override
    @Transactional
    public void delete(Integer id) {
        Restaurant restaurant = entityManager.getReference(Restaurant.class, id);
        entityManager.remove(restaurant);
    }
}
