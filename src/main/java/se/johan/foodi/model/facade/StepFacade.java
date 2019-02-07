/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package se.johan.foodi.model.facade;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import se.johan.foodi.model.Step;

/**
 *
 * @author johan
 */
@Stateless
public class StepFacade extends AbstractFacade<Step> {

    @PersistenceContext(unitName = "com.mycompany_SlutProjektBackend_war_1.0-SNAPSHOTPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public StepFacade() {
        super(Step.class);
    }
    
}
