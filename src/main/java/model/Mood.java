package model;

public class Mood {
    private Long id;
    private MoodNameEnum name;
    private String description;

    public Mood() {
    }

    public Mood(Long id, MoodNameEnum name, String description) {
        this.id = id;
        this.name = name;
        this.description = description;
    }

    public Long getId() {
        return id;
    }

    public Mood setId(Long id) {
        this.id = id;
        return this;
    }

    public MoodNameEnum getName() {
        return name;
    }

    public Mood setName(MoodNameEnum name) {
        this.name = name;
        return this;
    }

    public String getDescription() {
        return description;
    }

    public Mood setDescription(String description) {
        this.description = description;
        return this;
    }
}
