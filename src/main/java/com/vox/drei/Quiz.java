package com.vox.drei;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Quiz {
    private String id;
    private String name;
    private List<String> questionIds;

    public Quiz() {
        this.id = UUID.randomUUID().toString();
        this.questionIds = new ArrayList<>();
    }

    public Quiz(String name) {
        this();
        this.name = name;
    }

    // Getters and setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public List<String> getQuestionIds() { return questionIds; }
    public void setQuestionIds(List<String> questionIds) { this.questionIds = questionIds; }

    public void addQuestionId(String questionId) {
        this.questionIds.add(questionId);
    }

    public void removeQuestionId(String questionId) {
        this.questionIds.remove(questionId);
    }
}