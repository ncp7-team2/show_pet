package bitcamp.show_pet.post.model.dao;

import bitcamp.show_pet.post.model.vo.AttachedFile;
import bitcamp.show_pet.post.model.vo.Post;
import org.apache.ibatis.annotations.Mapper;
import java.util.List;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface PostDao {
    int insert(Post post);
    Post findBy(int id);
    int updateCount(int no);
    int insertFiles(Post post);
    int update(Post post);
    AttachedFile findFileBy(int id);
    List<Post> findAll();
    List<Post> findEtc();
    List<Post> findDog();
    List<Post> findCat();
    List<Post> findBird();
    int delete(int id);
    int deleteFile(int fileId);
    int deleteFiles(int postId);
    int deleteLikes(int postId);
    int updateLikeCount(@Param("postId") int postId, @Param("amount") int amount);
    int insertLike(@Param("postId") int postId, @Param("memberId") int memberId);
    int deleteLike(@Param("postId") int postId, @Param("memberId") int memberId);
    boolean isLiked(@Param("postId") int postId, @Param("memberId") int memberId);
    int getLikeCount(int postId);
}
