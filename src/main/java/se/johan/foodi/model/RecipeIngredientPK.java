/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package se.johan.foodi.model;

import java.io.Serializable;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 *
 * @author johan
 */
@Embeddable
public class RecipeIngredientPK implements Serializable {

  @Basic(optional = false)
  @NotNull
  @Size(min = 1, max = 255)
  @Column(name = "ingredient")
  private String ingredient;
  @Basic(optional = false)
  @NotNull
  @Size(min = 1, max = 36)
  @Column(name = "recipe_id")
  private String recipeId;

  public RecipeIngredientPK() {
  }

  public RecipeIngredientPK(String ingredient, String recipeId) {
    this.ingredient = ingredient;
    this.recipeId = recipeId;
  }

  public String getIngredient() {
    return ingredient;
  }

  public void setIngredient(String ingredient) {
    this.ingredient = ingredient;
  }

  public String getRecipeId() {
    return recipeId;
  }

  public void setRecipeId(String recipeId) {
    this.recipeId = recipeId;
  }

  @Override
  public int hashCode() {
    int hash = 0;
    hash += (ingredient != null ? ingredient.hashCode() : 0);
    hash += (recipeId != null ? recipeId.hashCode() : 0);
    return hash;
  }

  @Override
  public boolean equals(Object object) {
    // TODO: Warning - this method won't work in the case the id fields are not set
    if (!(object instanceof RecipeIngredientPK)) {
      return false;
    }
    RecipeIngredientPK other = (RecipeIngredientPK) object;
    if ((this.ingredient == null && other.ingredient != null) || (this.ingredient != null && !this.ingredient.equals(other.ingredient))) {
      return false;
    }
    if ((this.recipeId == null && other.recipeId != null) || (this.recipeId != null && !this.recipeId.equals(other.recipeId))) {
      return false;
    }
    return true;
  }

  @Override
  public String toString() {
    return "se.johan.foodi.RecipeIngredientPK[ ingredient=" + ingredient + ", recipeId=" + recipeId + " ]";
  }
  
}
