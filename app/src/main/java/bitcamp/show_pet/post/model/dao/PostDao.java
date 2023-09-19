package bitcamp.show_pet.post.model.dao;

import bitcamp.show_pet.post.model.vo.AttachedFile;
import bitcamp.show_pet.post.model.vo.Post;
import org.apache.ibatis.annotations.Mapper;
import java.util.List;

@Mapper
public interface PostDao {
    int insert(Post post);
    int updateCount(int no);
    int insertFiles(Post post);
    AttachedFile findFileBy(int id);
    List<Post> findAll();
    List<Post> findEtc();
    List<Post> findDog();
    List<Post> findCat();
    List<Post> findBird();
    int delete(int id);
    int deleteFile(int fileId);
    int deleteFiles(int postId);
}
