package bitcamp.show_pet.post.service;

import bitcamp.show_pet.post.model.dao.PostDao;
import bitcamp.show_pet.post.model.vo.AttachedFile;
import bitcamp.show_pet.post.model.vo.Post;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class DefaultPostService implements PostService {

    PostDao postDao;

    public DefaultPostService(PostDao postDao) { this.postDao = postDao; }

    @Transactional
    @Override
    public int add(Post post) throws Exception {
        int count = postDao.insert(post);
        if (post.getAttachedFiles().size() > 0) {
            postDao.insertFiles(post);
        }
        return count;
    }

    @Override
    public Post get(int postId) throws Exception {
        return postDao.findBy(postId);
    }

    @Transactional
    @Override
    public int update(Post post) throws Exception {
        int count = postDao.update(post);
        if (count > 0 && post.getAttachedFiles().size() > 0) {
            postDao.insertFiles(post);
        }
        return count;
    }

    @Override
    public List<Post> list() throws Exception {
        return postDao.findAll();
    }

    @Override
    public List<Post> listEtc() throws Exception {
        return postDao.findEtc();
    }

    @Override
    public List<Post> listDog() throws Exception {
        return postDao.findDog();
    }

    @Override
    public List<Post> listCat() throws Exception {
        return postDao.findCat();
    }

    @Override
    public List<Post> listBird() throws Exception {
        return postDao.findBird();
    }

    @Override
    public AttachedFile getAttachedFile(int fileId) throws Exception {
        return postDao.findFileBy(fileId);
    }

    @Override
    public int increaseViewCount(int boardNo) throws Exception {
        return postDao.updateCount(boardNo);
    }

    @Transactional
    @Override
    public int delete(int postId) throws Exception {
        postDao.deleteFiles(postId);
        postDao.deleteLikes(postId);
        return postDao.delete(postId);
    }

    @Override
    public int deleteAttachedFile(int fileId) throws Exception {
        return postDao.deleteFile(fileId);
    }
}
