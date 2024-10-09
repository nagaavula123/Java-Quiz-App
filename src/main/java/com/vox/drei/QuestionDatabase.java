package com.vox.drei;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class QuestionDatabase {
    private static final String JSON_FILE = "src/main/resources/questions.json";
    private static final ObjectMapper objectMapper = new ObjectMapper();

    public static List<Question> loadQuestions() {
        try {
            return objectMapper.readValue(new File(JSON_FILE), new TypeReference<List<Question>>(){});
        } catch (IOException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    public static void saveQuestions(List<Question> questions) {
        try {
            objectMapper.writeValue(new File(JSON_FILE), questions);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void addQuestion(Question question) {
        List<Question> questions = loadQuestions();
        questions.add(question);
        saveQuestions(questions);
    }
}