package bitcamp.show_pet.post.model.dao;

import bitcamp.show_pet.post.model.vo.AttachedFile;
import bitcamp.show_pet.post.model.vo.Post;
import org.apache.ibatis.annotations.Mapper;
import java.util.List;

@Mapper
public interface PostDao {
    int insert(Post post);
    int insertFiles(Post post);
    AttachedFile findFileBy(int id);
    List<Post> findAll();
}
