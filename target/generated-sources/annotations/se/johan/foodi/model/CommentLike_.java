package se.johan.foodi.model;

import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;
import se.johan.foodi.model.Comment;

@Generated(value="EclipseLink-2.5.2.v20140319-rNA", date="2019-02-25T16:03:41")
@StaticMetamodel(CommentLike.class)
public class CommentLike_ { 

    public static volatile SingularAttribute<CommentLike, String> senderIdentifier;
    public static volatile SingularAttribute<CommentLike, Comment> commentId;
    public static volatile SingularAttribute<CommentLike, Integer> id;

}