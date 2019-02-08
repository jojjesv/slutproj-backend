package se.johan.foodi.model;

import javax.annotation.Generated;
import javax.persistence.metamodel.CollectionAttribute;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;
import se.johan.foodi.model.Comment;

@Generated(value="EclipseLink-2.5.2.v20140319-rNA", date="2019-02-08T11:02:43")
@StaticMetamodel(Comment.class)
public class Comment_ { 

    public static volatile SingularAttribute<Comment, String> author;
    public static volatile SingularAttribute<Comment, Comment> replyToId;
    public static volatile CollectionAttribute<Comment, Comment> commentCollection;
    public static volatile SingularAttribute<Comment, Integer> id;
    public static volatile SingularAttribute<Comment, String> text;
    public static volatile SingularAttribute<Comment, String> recipeId;

}