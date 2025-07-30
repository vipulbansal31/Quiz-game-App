package gui;

import connection.DBConnect;
import javax.swing.*;
import java.awt.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ResultFrame extends JFrame {
    JTable table;

    public ResultFrame(int userId){
        setTitle("Your Quiz Results");
        setBounds(300,150,500,300);

        String[] columns = {"Result ID","Score","Date"};
        Object[][] data = fetchResults(userId);

        table = new JTable(data, columns);
        add(new JScrollPane(table), BorderLayout.CENTER);

        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setVisible(true);
    }

    private Object[][] fetchResults(int userId){
        List<Object[]> rows = new ArrayList<>();

        try(Connection con = DBConnect.getConnection()) {
            PreparedStatement pst = con.prepareStatement(
                    "SELECT result_id, score, quiz_date FROM results WHERE user_id=? ORDER BY quiz_date DESC"
            );
            pst.setInt(1,userId);
            ResultSet rs = pst.executeQuery();

            while(rs.next()) {
                rows.add(new Object[]{
                        rs.getInt("result_id"),
                        rs.getInt("score"),
                        rs.getDate("quiz_date")
                });
            }
        } catch(Exception e){
            e.printStackTrace();
        }

        Object[][] data = new Object[rows.size()][3];
        for(int i=0;i<rows.size();i++){
            data[i] = rows.get(i);
        }
        return data;
    }
}
