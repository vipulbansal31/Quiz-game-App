package gui;

import connection.DBConnect;
import javax.swing.*;
import java.awt.*;
import java.sql.*;
import java.util.*;
import javax.swing.Timer;

public class QuizFrame extends JFrame {
    int userId, quizId, score = 0, index = 0, timeLeft = 30;
    java.util.List<Question> questions;
    JLabel qLabel, timerLabel;
    JRadioButton[] options;
    ButtonGroup group;
    JButton nextBtn;
    Timer timer;

    class Question {
        int id;
        String text, a,b,c,d;
        String correct;
        Question(int id, String t, String a, String b, String c, String d, String correct){
            this.id=id; this.text=t; this.a=a; this.b=b; this.c=c; this.d=d; this.correct=correct;
        }
    }

    public QuizFrame(int userId, int quizId) {
        this.userId = userId;
        this.quizId = quizId;

        setTitle("Quiz");
        setBounds(200,100,600,400);
        setLayout(new BorderLayout());

        qLabel = new JLabel("Question", SwingConstants.CENTER);
        qLabel.setFont(new Font("Arial", Font.BOLD, 16));
        add(qLabel, BorderLayout.NORTH);

        JPanel centerPanel = new JPanel(new GridLayout(4,1));
        options = new JRadioButton[4];
        group = new ButtonGroup();
        for(int i=0;i<4;i++){
            options[i] = new JRadioButton();
            group.add(options[i]);
            centerPanel.add(options[i]);
        }
        add(centerPanel, BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel(new BorderLayout());
        timerLabel = new JLabel("Time: 30s", SwingConstants.LEFT);
        bottomPanel.add(timerLabel, BorderLayout.WEST);

        nextBtn = new JButton("Next");
        bottomPanel.add(nextBtn, BorderLayout.EAST);

        add(bottomPanel, BorderLayout.SOUTH);

        nextBtn.addActionListener(e -> nextQuestion());

        loadQuestions();
        showQuestion(index);
        startTimer();

        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setVisible(true);
    }

    private void loadQuestions() {
        questions = new ArrayList<>();
        try (Connection con = DBConnect.getConnection()) {
            String sql = "SELECT * FROM questions WHERE quiz_id=? ORDER BY DBMS_RANDOM.VALUE";
            PreparedStatement pst = con.prepareStatement(sql);
            pst.setInt(1, quizId);
            ResultSet rs = pst.executeQuery();
            while(rs.next()) {
                questions.add(new Question(
                        rs.getInt("question_id"),
                        rs.getString("question_text"),
                        rs.getString("option_a"),
                        rs.getString("option_b"),
                        rs.getString("option_c"),
                        rs.getString("option_d"),
                        rs.getString("correct_option")
                ));
            }
        } catch(Exception ex){ ex.printStackTrace(); }
    }

    private void showQuestion(int i){
        if(i>=questions.size()){ endQuiz(); return; }
        group.clearSelection();
        Question q = questions.get(i);
        qLabel.setText((i+1)+". "+q.text);
        options[0].setText(q.a);
        options[1].setText(q.b);
        options[2].setText(q.c);
        options[3].setText(q.d);
    }

    private void nextQuestion(){
        checkAnswer();
        index++;
        if(index<questions.size()) showQuestion(index);
        else endQuiz();
    }

    private void checkAnswer() {
        if(index < questions.size()) {
            Question q = questions.get(index);
            char correct = Character.toUpperCase(q.correct.charAt(0));

            int selected = -1;
            for(int i=0; i<4; i++) {
                if(options[i].isSelected()) selected = i;
            }

            if(selected != -1 && selected == (correct - 'A')) {
                score++;
            }
        }
    }


    private void startTimer(){
        timer = new Timer(1000, e -> {
            timeLeft--;
            timerLabel.setText("Time: "+timeLeft+"s");
            if(timeLeft<=0) endQuiz();
        });
        timer.start();
    }

    private void endQuiz(){
        if(timer!=null) timer.stop();
        checkAnswer();
        saveResult();
        JOptionPane.showMessageDialog(this, "Quiz Finished! Your Score: "+score);
        dispose();
        new ResultFrame(userId);
    }

    private void saveResult() {
        try(Connection con = DBConnect.getConnection()) {
            String sql = "INSERT INTO results(user_id,quiz_id,score) VALUES(?,?,?)";
            PreparedStatement pst = con.prepareStatement(sql);
            pst.setInt(1, userId);
            pst.setInt(2, quizId);
            pst.setInt(3, score);
            pst.executeUpdate();
        } catch(Exception e){ e.printStackTrace(); }
    }
}
