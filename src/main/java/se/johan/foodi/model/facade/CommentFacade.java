/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package se.johan.foodi.model.facade;

import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Root;
import se.johan.foodi.model.Comment;

/**
 *
 * @author johan
 */
@Stateless
public class CommentFacade extends AbstractFacade<Comment> {

    @PersistenceContext(unitName = "com.mycompany_SlutProjektBackend_war_1.0-SNAPSHOTPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public CommentFacade() {
        super(Comment.class);
    }

    /**
     * Queries all comments by recipe id.
     *
     * @see
     * https://stackoverflow.com/questions/5136366/jpa-select-query-with-where-clause
     */
    public List<Comment> findAll(String recipeId) {
        CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
        javax.persistence.criteria.CriteriaQuery cq = cb.createQuery();
        Root<Comment> from = cq.from(Comment.class);

        cq.where(cb.equal(from.get("recipe_id"), recipeId));

        return getEntityManager().createQuery(cq).getResultList();
    }
}
