/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package se.johan.foodi.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import org.eclipse.persistence.annotations.UuidGenerator;
import org.slf4j.LoggerFactory;
import se.johan.foodi.RecipeIngredient;

/**
 *
 * @author johan
 */
@Entity
@Table(name = "recipe")
@XmlRootElement
@NamedQueries({
  @NamedQuery(name = "Recipe.findAll", query = "SELECT r FROM Recipe r")
  , @NamedQuery(name = "Recipe.findById", query = "SELECT r FROM Recipe r WHERE r.id = :id")
  , @NamedQuery(name = "Recipe.findByName", query = "SELECT r FROM Recipe r WHERE r.name = :name")
  , @NamedQuery(name = "Recipe.findByDescription", query = "SELECT r FROM Recipe r WHERE r.description = :description")
  , @NamedQuery(name = "Recipe.findByImageUri", query = "SELECT r FROM Recipe r WHERE r.imageUri = :imageUri")})
public class Recipe implements Serializable {

  @OneToMany(cascade = CascadeType.ALL, mappedBy = "recipe")
  private Collection<RecipeIngredient> recipeIngredientCollection;

  @OneToMany(cascade = CascadeType.ALL, mappedBy = "recipeId")
  private Collection<Comment> commentCollection;

  @OneToMany(cascade = CascadeType.ALL, mappedBy = "recipeId")
  private Collection<Step> stepCollection;

  private static final long serialVersionUID = 1L;
  @Id
  @Basic(optional = false)
  @NotNull
  @Size(min = 1, max = 32)
  @Column(name = "id")
  @GeneratedValue(strategy = GenerationType.AUTO)
  private String id;
  @Basic(optional = false)
  @NotNull
  @Size(min = 1, max = 255)
  @Column(name = "name")
  private String name;

  @Size(max = 255)
  @Column(name = "description")
  private String description;

  @Basic(optional = false)
  @Size(min = 1, max = 32)
  @Column(name = "image_uri", nullable = true)
  private String imageUri;

  @ManyToMany(mappedBy = "recipeList")
  private List<Category> categoryList;

  @ManyToMany(mappedBy = "recipeList")
  private List<Ingredient> ingredientList;

  @Transient
  private List<String> pendingCategories;

  @Transient
  private List<String> pendingIngredients;

  public Recipe() {
    super();
  }

  public Recipe(String id) {
    this.id = id;
  }

  public Recipe(String id, String name, String imageUri) {
    this.id = id;
    this.name = name;
    this.imageUri = imageUri;
  }

  public List<String> getPendingCategories() {
    return pendingCategories;
  }

  public List<String> getPendingIngredients() {
    return pendingIngredients;
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public String getImageUri() {
    return imageUri;
  }

  public void setImageUri(String imageUri) {
    this.imageUri = imageUri;
  }

  /**
   * @return
   */
  public String getImageUrl() {
    return String.format(
            "/uploaded/%s",
            imageUri
    );
  }

  @XmlTransient
  public List<Category> getCategoryList() {
    return categoryList;
  }

  public String getCategoriesString() {
    List<Category> categories = getCategoryList();

    LoggerFactory.getLogger(Recipe.class).info("there are " + String.valueOf(categories.size()) + " categories");

    for (Category cat : categories) {
      LoggerFactory.getLogger(Recipe.class).info("cat_ " + cat.getName());
    }

    if (categories == null || categories.size() == 0) {
      return "";
    }

    StringBuilder sb = new StringBuilder();
    for (Category cat : categories) {
      sb.append(cat.getName());
      sb.append(",");
    }

    //  remove trailing delimiter
    sb.setLength(sb.length() - 1);

    return sb.toString();
  }

  public void setCategoryList(List<Category> categoryList) {
    this.categoryList = categoryList;
  }

  @XmlTransient
  public List<Ingredient> getIngredientList() {
    return ingredientList;
  }

  public void setIngredientList(List<Ingredient> ingredientList) {
    this.ingredientList = ingredientList;
  }

  public String getIngredientsString() {
    List<Ingredient> ingredients = getIngredientList();

    /*
    LoggerFactory.getLogger(Recipe.class).info("Has ingredients: " + String.valueOf(ingredients.size()));
    for (Ingredient ingr : ingredients) {
      LoggerFactory.getLogger(Recipe.class).info("using ingredient: " + ingr.getName());
    }
     */
    if (ingredients == null || ingredients.size() == 0) {
      return "";
    }

    StringBuilder sb = new StringBuilder();
    for (Ingredient ingredient : ingredients) {
      sb.append(ingredient.getName());
      sb.append(",");
    }

    //  remove trailing delimiter
    sb.setLength(sb.length() - 1);

    return sb.toString();
  }

  /**
   * Splits and trims the ingredient string and assigns values to pending
   * ingredients, obtainable via getPendingIngredients().
   */
  public void setIngredientsString(String value) {
    String[] split = value.split(",");
    pendingIngredients = new ArrayList<>();

    for (String s : split) {
      pendingIngredients.add(s.trim());
    }

    LoggerFactory.getLogger(Recipe.class).info("There are " + pendingIngredients.size() + " pending ingredients");
  }

  /**
   * Splits and trims the ingredient string and assigns values to pending
   * ingredients, obtainable via getPendingIngredients().
   */
  public void setCategoriesString(String value) {
    String[] split = value.split(",");
    pendingCategories = new ArrayList<>();

    for (String s : split) {
      pendingCategories.add(s.trim());
    }

    LoggerFactory.getLogger(Recipe.class).info("There are " + pendingCategories.size() + " pending categories");
  }

  @Override
  public int hashCode() {
    int hash = 0;
    hash += (id != null ? id.hashCode() : 0);
    return hash;
  }

  @Override
  public boolean equals(Object object) {
    // TODO: Warning - this method won't work in the case the id fields are not set
    if (!(object instanceof Recipe)) {
      return false;
    }
    Recipe other = (Recipe) object;
    if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
      return false;
    }
    return true;
  }

  @Override
  public String toString() {
    return "se.johan.foodi.model.Recipe[ id=" + id + " ]";
  }

  @XmlTransient
  public Collection<Step> getStepCollection() {
    return stepCollection;
  }

  public void setStepCollection(Collection<Step> stepCollection) {
    this.stepCollection = stepCollection;
  }

  @XmlTransient
  public Collection<Comment> getCommentCollection() {
    return commentCollection;
  }
  
  public Collection<Comment> getReportedComments() {
    Collection<Comment> items = new ArrayList<>();
    for (Comment c : commentCollection) {
      if (c.getReported()) {
        items.add(c);
      }
    }
    
    return items;
  }

  public void setCommentCollection(Collection<Comment> commentCollection) {
    this.commentCollection = commentCollection;
  }

  @XmlTransient
  public Collection<RecipeIngredient> getRecipeIngredientCollection() {
    return recipeIngredientCollection;
  }

  public void setRecipeIngredientCollection(Collection<RecipeIngredient> recipeIngredientCollection) {
    this.recipeIngredientCollection = recipeIngredientCollection;
  }

}
