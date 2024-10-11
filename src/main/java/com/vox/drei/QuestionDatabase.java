package com.vox.drei;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class QuestionDatabase {
    private static final String DB_URL = "jdbc:sqlite:quiz_app.db";

    static {
        try (Connection conn = DriverManager.getConnection(DB_URL);
             Statement stmt = conn.createStatement()) {
            // Create Quiz table
            stmt.execute("CREATE TABLE IF NOT EXISTS quizzes (id TEXT PRIMARY KEY, name TEXT, category TEXT)");

            // Create Question table
            stmt.execute("CREATE TABLE IF NOT EXISTS questions (id TEXT PRIMARY KEY, quiz_id TEXT, question TEXT, type TEXT, FOREIGN KEY(quiz_id) REFERENCES quizzes(id))");

            // Create Answer table
            stmt.execute("CREATE TABLE IF NOT EXISTS answers (id TEXT PRIMARY KEY, question_id TEXT, answer TEXT, is_correct INTEGER, FOREIGN KEY(question_id) REFERENCES questions(id))");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    public static void addQuiz(Quiz quiz) {
        String sql = "INSERT INTO quizzes(id, name, category) VALUES(?,?,?)";
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, quiz.getId());
            pstmt.setString(2, quiz.getName());
            pstmt.setString(3, quiz.getCategory());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void updateQuiz(Quiz quiz) {
        String sql = "UPDATE quizzes SET name = ?, category = ? WHERE id = ?";
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, quiz.getName());
            pstmt.setString(2, quiz.getCategory());
            pstmt.setString(3, quiz.getId());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void deleteQuiz(String quizId) {
        String sql = "DELETE FROM quizzes WHERE id = ?";
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, quizId);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static List<Quiz> loadQuizzes() {
        List<Quiz> quizzes = new ArrayList<>();
        String sql = "SELECT * FROM quizzes";
        try (Connection conn = DriverManager.getConnection(DB_URL);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Quiz quiz = new Quiz();
                quiz.setId(rs.getString("id"));
                quiz.setName(rs.getString("name"));
                quiz.setCategory(rs.getString("category"));
                quiz.setQuestions(getQuestionsForQuiz(quiz.getId()));
                quizzes.add(quiz);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return quizzes;
    }

    public static void addQuestion(Question question, String quizId) {
        String sql = "INSERT INTO questions(id, quiz_id, question, type) VALUES(?,?,?,?)";
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, question.getId());
            pstmt.setString(2, quizId);
            pstmt.setString(3, question.getQuestion());
            pstmt.setString(4, question.getType());
            pstmt.executeUpdate();

            for (int i = 0; i < question.getAnswers().size(); i++) {
                addAnswer(question.getId(), question.getAnswers().get(i), i == question.getCorrectAnswerIndex());
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void addAnswer(String questionId, String answer, boolean isCorrect) {
        String sql = "INSERT INTO answers(id, question_id, answer, is_correct) VALUES(?,?,?,?)";
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, UUID.randomUUID().toString());
            pstmt.setString(2, questionId);
            pstmt.setString(3, answer);
            pstmt.setInt(4, isCorrect ? 1 : 0);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void updateQuestion(Question question) {
        String sql = "UPDATE questions SET question = ?, type = ? WHERE id = ?";
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, question.getQuestion());
            pstmt.setString(2, question.getType());
            pstmt.setString(3, question.getId());
            pstmt.executeUpdate();

            // Delete old answers
            deleteAnswers(question.getId());

            // Add new answers
            for (String answer : question.getAnswers()) {
                addAnswer(question.getId(), answer, question.getAnswers().indexOf(answer) == question.getCorrectAnswerIndex());
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void deleteAnswers(String questionId) {
        String sql = "DELETE FROM answers WHERE question_id = ?";
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, questionId);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void deleteQuestion(String questionId) {
        deleteAnswers(questionId);
        String sql = "DELETE FROM questions WHERE id = ?";
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, questionId);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static List<Question> getQuestionsForQuiz(String quizId) {
        List<Question> questions = new ArrayList<>();
        String sql = "SELECT * FROM questions WHERE quiz_id = ?";
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, quizId);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                Question question = new Question();
                question.setId(rs.getString("id"));
                question.setQuestion(rs.getString("question"));
                question.setType(rs.getString("type"));
                question.setAnswers(getAnswersForQuestion(question.getId()));
                question.setCorrectAnswerIndex(getCorrectAnswerIndex(question.getId()));
                questions.add(question);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return questions;
    }

    private static List<String> getAnswersForQuestion(String questionId) {
        List<String> answers = new ArrayList<>();
        String sql = "SELECT answer FROM answers WHERE question_id = ?";
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, questionId);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                answers.add(rs.getString("answer"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return answers;
    }

    private static int getCorrectAnswerIndex(String questionId) {
        String sql = "SELECT * FROM answers WHERE question_id = ? ORDER BY id";
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, questionId);
            ResultSet rs = pstmt.executeQuery();
            int index = 0;
            while (rs.next()) {
                if (rs.getInt("is_correct") == 1) {
                    return index;
                }
                index++;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    public static List<Quiz> searchQuizzes(String searchTerm, String category, int numberOfQuestions) {
        List<Quiz> quizzes = new ArrayList<>();
        String sql = "SELECT q.*, COUNT(qu.id) as question_count FROM quizzes q " +
                "LEFT JOIN questions qu ON q.id = qu.quiz_id " +
                "WHERE (q.name LIKE ? OR q.category LIKE ?) " +
                (category != null && !category.isEmpty() ? "AND q.category = ? " : "") +
                "GROUP BY q.id " +
                (numberOfQuestions > 0 ? "HAVING question_count >= ? " : "") +
                "ORDER BY q.name";
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            int paramIndex = 1;
            pstmt.setString(paramIndex++, "%" + searchTerm + "%");
            pstmt.setString(paramIndex++, "%" + searchTerm + "%");
            if (category != null && !category.isEmpty()) {
                pstmt.setString(paramIndex++, category);
            }
            if (numberOfQuestions > 0) {
                pstmt.setInt(paramIndex, numberOfQuestions);
            }
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                Quiz quiz = new Quiz();
                quiz.setId(rs.getString("id"));
                quiz.setName(rs.getString("name"));
                quiz.setCategory(rs.getString("category"));
                quiz.setQuestions(getQuestionsForQuiz(quiz.getId()));
                quizzes.add(quiz);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return quizzes;
    }
}