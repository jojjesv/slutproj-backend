/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package se.johan.foodi.model;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
  
  private static final Logger logger = LoggerFactory.getLogger(Recipe.class);
  
  private static final char[] ID_ALLOWED_CHARS =
    "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWYX0123456789".toCharArray();
  
  public static String generateId(Connection connection) throws SQLException {
    StringBuilder sb = new StringBuilder();
    
    ResultSet checkStmtResult;
    PreparedStatement checkStmt = connection.prepareStatement(
      "SELECT 1 FROM recipe WHERE id = ?"
    );
    int length = 32;
    
    do {
      sb.setLength(0);
      
      for (int i = 0; i < length; i++) {
        sb.append(Recipe.ID_ALLOWED_CHARS[
          (int)Math.floor(Recipe.ID_ALLOWED_CHARS.length * Math.random())]
        );
      }
      
      checkStmt.setString(1, sb.toString());
      checkStmtResult = checkStmt.executeQuery();
      
    } while (checkStmtResult.next());
    
    //  value is guaranteed unique
    
    return sb.toString();
  }

  @OneToMany(cascade = CascadeType.ALL, mappedBy = "recipe")
  private Collection<RecipeIngredient> ingredientRelations;

  @OneToMany(cascade = CascadeType.ALL, mappedBy = "recipe")
  private Collection<Comment> comments;

  @OneToMany(cascade = CascadeType.ALL, mappedBy = "recipe")
  private Collection<Step> steps;

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

  @ManyToMany(mappedBy = "associatedRecipes")
  private List<Category> categories;

  @ManyToMany(mappedBy = "recipes")
  private List<Ingredient> ingredients;

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
  
  /**
   * Ensures that the steps have correct position.
   */
  public void arrangeSteps() {
    int i = 1;
    Iterator<Step> itr = steps.iterator();
    
    while (itr.hasNext()) {
      itr.next().setPosition((short)i);
      i++;
    }
  }

  /**
   * @return categories as inputted strings, splitted by delimiter
   */
  public List<String> getPendingCategories() {
    return pendingCategories;
  }

  /**
   * @return ingredients as inputted strings, splitted by delimiter
   */
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
    return categories;
  }

  /**
   * @return joined categories string
   */
  public String getCategoriesString() {
    if (categories == null || categories.isEmpty()) {
      return "";
    }

    StringBuilder sb = new StringBuilder();
    for (Category cat : categories) {
      if (cat == null) {
        continue;
      }
      sb.append(cat.getName());
      sb.append(",");
    }
    
    if (sb.length() == 0) {
      return "";
    }

    //  remove trailing delimiter
    sb.setLength(sb.length() - 1);

    return sb.toString();
  }

  public void setCategories(List<Category> categories) {
    this.categories = categories;
  }

  @XmlTransient
  public List<Ingredient> getIngredients() {
    return ingredients;
  }

  public void setIngredients(List<Ingredient> ingredients) {
    this.ingredients = ingredients;
  }

  public String getIngredientsString() {
    /*
    LoggerFactory.getLogger(Recipe.class).info("Has ingredients: " + String.valueOf(ingredients.size()));
    for (Ingredient ingr : ingredients) {
      LoggerFactory.getLogger(Recipe.class).info("using ingredient: " + ingr.getName());
    }
     */
    if (ingredients == null || ingredients.isEmpty()) {
      return "";
    }
    
    //  For distincting ingredients
    List<String> usedIngredients = new LinkedList<>();

    StringBuilder sb = new StringBuilder();
    for (Ingredient ingredient : ingredients) {
      if (ingredient == null) {
        continue;
      }
      String name = ingredient.getName();
      if (usedIngredients.contains(name)) {
        continue;
      }
      sb.append(name);
      usedIngredients.add(name);
      sb.append(",");
    }
    
    if (sb.length() == 0) {
      return "";
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
  public Collection<Step> getSteps() {
    return steps;
  }

  public void setSteps(Collection<Step> steps) {
    this.steps = steps;
  }

  @XmlTransient
  public Collection<Comment> getComments() {
    return comments;
  }
  
  public Collection<Comment> getReportedComments() {
    Collection<Comment> items = new ArrayList<>();
    for (Comment c : comments) {
      if (c.getReported()) {
        items.add(c);
      }
    }
    
    return items;
  }

  public void setComments(Collection<Comment> comments) {
    this.comments = comments;
  }

  @XmlTransient
  public Collection<RecipeIngredient> getIngredientRelations() {
    return ingredientRelations;
  }

  public void setIngredientRelations(Collection<RecipeIngredient> ingredientRelations) {
    this.ingredientRelations = ingredientRelations;
  }

}
