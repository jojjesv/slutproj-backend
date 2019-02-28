/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package se.johan.foodi.model;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import java.io.Serializable;
import java.util.Collection;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 *
 * @author johan
 */
@Entity
@Table(name = "comment")
@XmlRootElement
@NamedQueries({
  @NamedQuery(name = "Comment.findAll", query = "SELECT c FROM Comment c")
  , @NamedQuery(name = "Comment.findById", query = "SELECT c FROM Comment c WHERE c.id = :id")
  , @NamedQuery(name = "Comment.findByText", query = "SELECT c FROM Comment c WHERE c.text = :text")
  , @NamedQuery(name = "Comment.findByAuthor", query = "SELECT c FROM Comment c WHERE c.author = :author")
  , @NamedQuery(name = "Comment.findByRecipeId", query = "SELECT c FROM Comment c WHERE c.recipeId = :recipeId")})
public class Comment implements Serializable {

  @JoinColumn(name = "recipe_id", referencedColumnName = "id")
  @ManyToOne(optional = false)
  private Recipe recipeId;
  @OneToMany(cascade = CascadeType.ALL, mappedBy = "commentId")
  private Collection<CommentLike> commentLikeCollection;

  private static final long serialVersionUID = 1L;
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Basic(optional = false)
  @Column(name = "id")
  private Integer id;
  @Basic(optional = false)
  @NotNull
  @Size(min = 1, max = 255)
  @Column(name = "text")
  private String text;
  @Basic(optional = false)
  @NotNull
  @Size(min = 1, max = 255)
  @Column(name = "author")
  private String author;
  @OneToMany(mappedBy = "replyToId")
  private Collection<Comment> commentCollection;
  @JoinColumn(name = "reply_to_id", referencedColumnName = "id")
  @ManyToOne
  private Comment replyToId;

  public Comment() {
  }

  public Comment(Integer id) {
    this.id = id;
  }

  public Comment(Integer id, String text, String author) {
    this.id = id;
    this.text = text;
    this.author = author;
  }

  public Integer getId() {
    return id;
  }

  public void setId(Integer id) {
    this.id = id;
  }

  public String getText() {
    return text;
  }

  public void setText(String text) {
    this.text = text;
  }

  public String getAuthor() {
    return author;
  }

  public void setAuthor(String author) {
    this.author = author;
  }

  @XmlTransient
  public Collection<Comment> getCommentCollection() {
    return commentCollection;
  }

  public void setCommentCollection(Collection<Comment> commentCollection) {
    this.commentCollection = commentCollection;
  }

  public Comment getReplyToId() {
    return replyToId;
  }

  public void setReplyToId(Comment replyToId) {
    this.replyToId = replyToId;
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
    if (!(object instanceof Comment)) {
      return false;
    }
    Comment other = (Comment) object;
    if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
      return false;
    }
    return true;
  }

  @Override
  public String toString() {
    return "se.johan.foodi.model.Comment[ id=" + id + " ]";
  }

  public Recipe getRecipeId() {
    return recipeId;
  }

  public void setRecipeId(Recipe recipeId) {
    this.recipeId = recipeId;
  }

  @XmlTransient
  public Collection<CommentLike> getCommentLikeCollection() {
    return commentLikeCollection;
  }

  public void setCommentLikeCollection(Collection<CommentLike> commentLikeCollection) {
    this.commentLikeCollection = commentLikeCollection;
  }

  /**
   * @param senderIdentifier Sender identifier for comments.
   * @return 
   */
  public JSONObject toJSONObject(String senderIdentifier) {
    JSONObject obj = new JSONObject();
    obj.put("id", getId());
    obj.put("author", getAuthor());
    obj.put("message", getText());
    obj.put("likeCount", getCommentLikeCollection().size());

    JSONArray replies = new JSONArray();
    for (Comment reply : getCommentCollection()) {
      replies.add(reply.toJSONObject(senderIdentifier));
    }
    obj.put("replies", replies);

    boolean currentUserLiked = false;
    for (CommentLike like : getCommentLikeCollection()) {
      if (like.getSenderIdentifier().equals(senderIdentifier)) {
        currentUserLiked = true;
        break;
      }
    }
    obj.put("currentUserLiked", currentUserLiked);

    return obj;
  }
}
