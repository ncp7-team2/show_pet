package bitcamp.show_pet.post.service;

import bitcamp.show_pet.post.model.vo.AttachedFile;
import bitcamp.show_pet.post.model.vo.Post;
import java.util.List;


public interface PostService {
    int add(Post post) throws Exception;
    int increaseViewCount(int postId) throws Exception;
    List<Post> list () throws Exception;
    AttachedFile getAttachedFile(int fileId) throws Exception;
    int delete(int id) throws Exception;
    int deleteAttachedFile(int fileId) throws Exception;
}
