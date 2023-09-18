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
    public List<Post> list() throws Exception {
        return postDao.findAll();
    }

    @Override
    public AttachedFile getAttachedFile(int fileId) throws Exception {
        return postDao.findFileBy(fileId);
    }

    @Override
    public int increaseViewCount(int boardNo) throws Exception {
        return postDao.updateCount(boardNo);
    }


}
