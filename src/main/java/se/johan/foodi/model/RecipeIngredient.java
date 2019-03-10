/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package se.johan.foodi.model;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;
import se.johan.foodi.model.Ingredient;
import se.johan.foodi.model.Recipe;

/**
 *
 * @author johan
 */
@Entity
@Table(name = "recipe_ingredient")
@XmlRootElement
@NamedQueries({
  @NamedQuery(name = "RecipeIngredient.findAll", query = "SELECT r FROM RecipeIngredient r")
  , @NamedQuery(name = "RecipeIngredient.findByIngredient", query = "SELECT r FROM RecipeIngredient r WHERE r.recipeIngredientPK.ingredient = :ingredient")
  , @NamedQuery(name = "RecipeIngredient.findByRecipeId", query = "SELECT r FROM RecipeIngredient r WHERE r.recipeIngredientPK.recipeId = :recipeId")
  , @NamedQuery(name = "RecipeIngredient.findByQuantity", query = "SELECT r FROM RecipeIngredient r WHERE r.quantity = :quantity")})
public class RecipeIngredient implements Serializable {

  private static final long serialVersionUID = 1L;
  @EmbeddedId
  protected RecipeIngredientPK recipeIngredientPK;
  @Size(max = 64)
  @Column(name = "quantity")
  private String quantity;
  @JoinColumn(name = "ingredient", referencedColumnName = "name", insertable = false, updatable = false)
  @ManyToOne(optional = false)
  private Ingredient ingredient1;
  @JoinColumn(name = "recipe_id", referencedColumnName = "id", insertable = false, updatable = false)
  @ManyToOne(optional = false)
  private Recipe recipe;

  public RecipeIngredient() {
  }

  public RecipeIngredient(RecipeIngredientPK recipeIngredientPK) {
    this.recipeIngredientPK = recipeIngredientPK;
  }

  public RecipeIngredient(String ingredient, String recipeId) {
    this.recipeIngredientPK = new RecipeIngredientPK(ingredient, recipeId);
  }

  public RecipeIngredientPK getRecipeIngredientPK() {
    return recipeIngredientPK;
  }

  public void setRecipeIngredientPK(RecipeIngredientPK recipeIngredientPK) {
    this.recipeIngredientPK = recipeIngredientPK;
  }

  public String getQuantity() {
    return quantity;
  }

  public void setQuantity(String quantity) {
    this.quantity = quantity;
  }

  public Ingredient getIngredient1() {
    return ingredient1;
  }

  public void setIngredient1(Ingredient ingredient1) {
    this.ingredient1 = ingredient1;
  }

  public Recipe getRecipe() {
    return recipe;
  }

  public void setRecipe(Recipe recipe) {
    this.recipe = recipe;
  }

  @Override
  public int hashCode() {
    int hash = 0;
    hash += (recipeIngredientPK != null ? recipeIngredientPK.hashCode() : 0);
    return hash;
  }

  @Override
  public boolean equals(Object object) {
    // TODO: Warning - this method won't work in the case the id fields are not set
    if (!(object instanceof RecipeIngredient)) {
      return false;
    }
    RecipeIngredient other = (RecipeIngredient) object;
    if ((this.recipeIngredientPK == null && other.recipeIngredientPK != null) || (this.recipeIngredientPK != null && !this.recipeIngredientPK.equals(other.recipeIngredientPK))) {
      return false;
    }
    return true;
  }

  @Override
  public String toString() {
    return "se.johan.foodi.RecipeIngredient[ recipeIngredientPK=" + recipeIngredientPK + " ]";
  }
  
}
