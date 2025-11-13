package com.example.Migration.dao;

import java.util.List;


import org.springframework.stereotype.Repository;

import com.example.Migration.model.OrderItem;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;

@Repository
public class OrderDAO {

    @PersistenceContext
    private EntityManager em;

    public List<OrderItem> findItems(Long orderId) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<OrderItem> query = cb.createQuery(OrderItem.class);
        Root<OrderItem> root = query.from(OrderItem.class);

        // ✅ Join by nested field (order.id)
        query.select(root)
             .where(cb.equal(root.get("order").get("id"), orderId))
             .orderBy(cb.asc(root.get("itemId")));

        return em.createQuery(query).getResultList();
    }
}
