package etu.kefengroup.turistomer.dao;

import etu.kefengroup.turistomer.entity.Hotel;
import jakarta.persistence.EntityManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

public class HotelDAOImpl implements HotelDAO{
    private EntityManager entityManager;

    @Autowired
    public HotelDAOImpl(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    @Transactional
    public void save(Hotel hotel) {
        entityManager.persist(hotel);
    }

    @Override
    public Hotel findById(Integer id) {
        return entityManager.find(Hotel.class,id);
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
    public void update(Hotel hotel) {
        entityManager.merge(hotel);
    }

    @Override
    @Transactional
    public void delete(Integer id) {
        Hotel hotel = entityManager.getReference(Hotel.class, id);
        entityManager.remove(hotel);
    }
}
