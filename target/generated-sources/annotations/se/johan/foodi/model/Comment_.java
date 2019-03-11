package se.johan.foodi.model;

import javax.annotation.Generated;
import javax.persistence.metamodel.CollectionAttribute;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;
import se.johan.foodi.model.Comment;
import se.johan.foodi.model.CommentLike;
import se.johan.foodi.model.Recipe;

@Generated(value="EclipseLink-2.6.1.v20150916-rNA", date="2019-03-11T09:09:13")
@StaticMetamodel(Comment.class)
public class Comment_ { 

    public static volatile CollectionAttribute<Comment, CommentLike> commentLikeCollection;
    public static volatile SingularAttribute<Comment, String> author;
    public static volatile SingularAttribute<Comment, Comment> replyToId;
    public static volatile CollectionAttribute<Comment, Comment> commentCollection;
    public static volatile SingularAttribute<Comment, Boolean> reported;
    public static volatile SingularAttribute<Comment, Integer> id;
    public static volatile SingularAttribute<Comment, String> text;
    public static volatile SingularAttribute<Comment, Recipe> recipeId;

}