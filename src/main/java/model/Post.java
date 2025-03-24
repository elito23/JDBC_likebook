package model;

import java.util.List;

public class Post {
    private Long id;
    private String content;
    private User user;
    private List<User> likes;
    private Mood mood;

    public Post() {
    }

    public Post(Long id, String content, User user, List<User> likes, Mood mood) {
        this.id = id;
        this.content = content;
        this.user = user;
        this.likes = likes;
        this.mood = mood;
    }

    public Long getId() {
        return id;
    }

    public Post setId(Long id) {
        this.id = id;
        return this;
    }

    public String getContent() {
        return content;
    }

    public Post setContent(String content) {
        this.content = content;
        return this;
    }

    public User getUser() {
        return user;
    }

    public Post setUser(User user) {
        this.user = user;
        return this;
    }

    public List<User> getLikes() {
        return likes;
    }

    public Post setLikes(List<User> likes) {
        this.likes = likes;
        return this;
    }

    public Mood getMood() {
        return mood;
    }

    public Post setMood(Mood mood) {
        this.mood = mood;
        return this;
    }
}
