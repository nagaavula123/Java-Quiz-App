package com.vox.drei;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Quiz {
    private String id;
    private String name;
    private String category;
    private List<Question> questions;

    public Quiz() {
        this.id = UUID.randomUUID().toString();
        this.questions = new ArrayList<>();
    }

    public Quiz(String name, String category) {
        this();
        this.name = name;
        this.category = category;
    }

    // Getters and setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    public List<Question> getQuestions() { return questions; }
    public void setQuestions(List<Question> questions) { this.questions = questions; }

}