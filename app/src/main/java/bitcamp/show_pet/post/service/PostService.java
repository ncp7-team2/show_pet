package bitcamp.show_pet.post.service;

import bitcamp.show_pet.post.model.vo.AttachedFile;
import bitcamp.show_pet.post.model.vo.Post;
import java.util.List;


public interface PostService {
    int add(Post post) throws Exception;
    int increaseViewCount(int postId) throws Exception;
    Post get(int postId) throws Exception;
    int update(Post post) throws Exception;
    List<Post> list () throws Exception;
    List<Post> listEtc () throws Exception;
    List<Post> listDog () throws Exception;
    List<Post> listCat () throws Exception;
    List<Post> listBird () throws Exception;
    AttachedFile getAttachedFile(int fileId) throws Exception;
    int delete(int postId) throws Exception;
    int deleteAttachedFile(int fileId) throws Exception;
}
