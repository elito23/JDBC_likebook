package service;

import model.Post;
import model.User;
import model.UserRole;
import model.UserRoleEnum;
import repositories.PostRepository;
import repositories.UserRoleRepository;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public class PostService {
    private final PostRepository postRepository;
    private final UserRoleRepository userRoleRepository;

    public PostService(PostRepository postRepository, UserRoleRepository userRoleRepository) {
        this.postRepository = postRepository;
        this.userRoleRepository = userRoleRepository;
    }

    public void createPost(Post post) throws SQLException {
        if (post.getUser() == null) {
            throw new IllegalArgumentException("You must specify a user!");
        } else if (post.getMood() == null) {
            throw new IllegalArgumentException("You must specify a mood!");
        } else if (post.getContent() == null || post.getContent().isEmpty()) {
            throw new IllegalArgumentException("You must enter a post content!!");
        }
        postRepository.savePost(post);
        System.out.println("Successfully added a post");
    }

    public  void deleteById(Long id, User currentUser ) throws SQLException {
        Optional<Post> post = postRepository.findById(id);
        User user=post.get().getUser();
        boolean isAdmin = currentUser.getUserRoles().stream()
                .anyMatch(role -> role.getUserRole() == UserRoleEnum.ADMIN);
        if (currentUser.getId() != user.getId() && !isAdmin) {
            throw new SQLException("You can only delete posts made by this account!");
        }

        postRepository.deleteById(id);
        System.out.println("Deleted successfully!");
    }

    public  List<Post> getAll() throws SQLException {
        return postRepository.findAll();
    }

    public Post getById(Long id) throws SQLException {
        return postRepository.findById(id)
                .orElseThrow(() -> new SQLException("Post with id " + id + " not found!"));
    }

    public List<Post> getAllByUser_Username(String username) throws SQLException {
        List<Post> posts = postRepository.findAllByUser_Username(username);
        printIfEmpty(posts, "No posts found for user: " + username);
        return posts;
    }

    public List<Post> getAllByUser_UsernameNot(String username) throws SQLException {
        List<Post> posts = postRepository.findAllByUser_UsernameNot(username);
        printIfEmpty(posts, "No posts found for user: " + username);
        return posts;
    }

    public void likePost(Long postId, User user) throws SQLException {
        postRepository.likePost(postId, user);
    }

    public int getLikeCount(Long postId) throws SQLException {
        return postRepository.getLikeCount(postId);
    }

    public Optional<Post> getPostById(Long id) throws SQLException {
        return postRepository.findById(id);
    }

    private void printIfEmpty(List<Post> posts, String message) {
        if (posts.isEmpty()) {
            System.out.println(message);
        }
    }
}
