package se.johan.foodi.model;

import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;
import se.johan.foodi.model.Comment;

@Generated(value="EclipseLink-2.6.1.v20150916-rNA", date="2019-03-14T10:23:36")
@StaticMetamodel(CommentLike.class)
public class CommentLike_ { 

    public static volatile SingularAttribute<CommentLike, String> senderIdentifier;
    public static volatile SingularAttribute<CommentLike, Comment> commentId;
    public static volatile SingularAttribute<CommentLike, Integer> id;

}