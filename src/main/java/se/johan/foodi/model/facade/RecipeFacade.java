/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package se.johan.foodi.model.facade;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import se.johan.foodi.model.Recipe;

/**
 *
 * @author johan
 */
@Stateless
public class RecipeFacade extends AbstractFacade<Recipe> {

    @PersistenceContext(unitName = "com.mycompany_SlutProjektBackend_war_1.0-SNAPSHOTPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }
    
    @Deprecated
    public void clearCache(Object entity) {
      //em.refresh(entity);
    }

    public RecipeFacade() {
        super(Recipe.class);
    }
    
}
