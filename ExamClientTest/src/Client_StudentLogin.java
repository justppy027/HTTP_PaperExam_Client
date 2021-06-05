import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.Random;

public class Client_StudentLogin extends Frame {
    private Label label_stu_id;
    private Label label_stu_name;
    public TextField tf_stu_id;
    public TextField tf_stu_name;
    private Button btn_login;
    private int exam_pos = new Random().nextInt(100) % 2;

    public Client_StudentLogin(){
        setLayout(null);

        BtnClick1Listener btn_login_listener = new BtnClick1Listener();
        EnterKeyListener enter_key_listener = new EnterKeyListener();
        label_stu_id = new Label("SID:");
        label_stu_id.setBounds(700, 150, 125, 30);
        add(label_stu_id);
        tf_stu_id = new TextField(12);
        tf_stu_id.setBounds(700, 200, 125, 30);
        add(tf_stu_id);
        tf_stu_id.addKeyListener(enter_key_listener);
        label_stu_name = new Label("STU_NAME:");
        label_stu_name.setBounds(700, 250, 125, 30);
        add(label_stu_name);
        tf_stu_name = new TextField(12);
        tf_stu_name.setBounds(700, 300, 125, 30);
        add(tf_stu_name);
        tf_stu_name.addKeyListener(enter_key_listener);
        btn_login = new Button("Login");
        btn_login.setBounds(725, 380, 75, 55);
        add(btn_login);
        btn_login.addActionListener(btn_login_listener);
        btn_login.addKeyListener(enter_key_listener);
        setTitle("Exam assistant");
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setUndecorated(true);
//        setSize(150,300);
        setBackground(Color.GRAY);
        setVisible(true);


    }
    private class BtnClick1Listener implements ActionListener{

        @Override
        public void actionPerformed(ActionEvent e) {
            int login_confirm = JOptionPane.showConfirmDialog(null,
                    "SID :"+tf_stu_id.getText() + "NAME :"+tf_stu_name.getText(), "Check Your INFO", JOptionPane.YES_NO_OPTION);
            if(login_confirm == 0){
                String ExamTime_Array[] = Methods.Refresh_ExamTime(exam_pos);
                if(ExamTime_Array.length != 3 || (Long.parseLong(ExamTime_Array[1]) + Long.parseLong(ExamTime_Array[2]) - Long.parseLong(ExamTime_Array[0])) < 0L ){
                    JOptionPane.showConfirmDialog(null,
                            "There is no exam yet", "", JOptionPane.DEFAULT_OPTION);
                }
                else{
                    setVisible(false);
                    Client_ExamSubmitter client_examSubmitter = new Client_ExamSubmitter(tf_stu_id.getText(), tf_stu_name.getText(), exam_pos);
                    dispose();
                }
            }
        }
    }
    private class EnterKeyListener implements KeyListener{

        @Override
        public void keyTyped(KeyEvent e) {

        }

        @Override
        public void keyPressed(KeyEvent e) {
            if (e.getKeyCode()==KeyEvent.VK_ENTER){
                int login_confirm = JOptionPane.showConfirmDialog(null,
                        "SID :"+tf_stu_id.getText() + "NAME :"+tf_stu_name.getText(), "Check Your INFO", JOptionPane.YES_NO_OPTION);
                if(login_confirm == 0){
                    String ExamTime_Array[] = Methods.Refresh_ExamTime(exam_pos);
                    if(ExamTime_Array.length != 3 || (Long.parseLong(ExamTime_Array[1]) + Long.parseLong(ExamTime_Array[2]) - Long.parseLong(ExamTime_Array[0])) < 0L ){
                        JOptionPane.showConfirmDialog(null,
                                "There is no exam yet", "", JOptionPane.DEFAULT_OPTION);
                    }
                    else{
                        setVisible(false);
                        Client_ExamSubmitter client_examSubmitter = new Client_ExamSubmitter(tf_stu_id.getText(), tf_stu_name.getText(), exam_pos);
                        dispose();
                    }
                }
            }
        }

        @Override
        public void keyReleased(KeyEvent e) {

        }
    }

}
