import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.util.concurrent.TimeUnit;

public class Client_ExamSubmitter extends Frame {
    private Label label_timer;
    public Button btn1;
    private int signal_submit_confirm = 0;
    private int signal_drt_over = 0;
    private int signal_exam_ing = 0;
    private int signal_exam_exist = 0;
    private String folder_name = null;
    private int exam_pos = 0;

    public Client_ExamSubmitter(String SID, String NAME, int pos){
        System.out.println("Submitter:  "+ pos);
        folder_name = SID + NAME;
        exam_pos = pos;
        setLayout(new GridLayout());
        label_timer = new Label("Waiting For Exam");
        add(label_timer);
        btn1 = new Button("Submit");
        add(btn1);
        BtnClick1Listener listener1 = new BtnClick1Listener();
        btn1.addActionListener(listener1);
        setTitle("STUDENT  :" + folder_name);
        setSize(500,80);
        setVisible(true);

        new Thread(new Runnable() {
            @Override
            public void run() {
                int pe = Prepare_Exam(folder_name);
                if(pe != 1){
                    JOptionPane.showConfirmDialog(null,
                            "Something Wrong, Please Contact Your Teacher", "Error!", JOptionPane.DEFAULT_OPTION);
                    return;
                }
                CountDown_Timer(folder_name);

            }
        }).start();
    }



    private class BtnClick1Listener implements ActionListener{

        @Override
        public void actionPerformed(ActionEvent e) {
            int submit_confirm = JOptionPane.showConfirmDialog(null,
                    "Want to Submit your Exam Paper?", "Caution", JOptionPane.YES_NO_OPTION);
            if(submit_confirm == 0){
                signal_exam_ing = 0;
                signal_submit_confirm = 1;
                Methods.zip_file_main(folder_name);
                Methods.UpLoad_Paper_to_Server(folder_name, exam_pos);
                Methods.Remove_Paper_File(folder_name);
                JOptionPane.showConfirmDialog(null,
                        "Your test paper has been turned in", "", JOptionPane.DEFAULT_OPTION);
            }
        }
    }

    private int Prepare_Exam(String FileName){
        int Sign_All_Ready = 0;
        int Sign_Folder_Ready = 0;
        int Sign_Paper_Ready = 0;
        int Sign_Open_Ready = 0;
        long FileLen_success = 0;
        System.out.println("Prepare_Exam   :");
        String ExamTime_Array[] = Methods.Refresh_ExamTime(exam_pos);
        if(ExamTime_Array.length != 3 || (Long.parseLong(ExamTime_Array[1]) + Long.parseLong(ExamTime_Array[2]) - Long.parseLong(ExamTime_Array[0])) < 0L ){
            return 2;
        }
        for(int i=3;i>0;i--){

            if(Methods.Make_Exam_DIR(FileName) == true){
                Sign_Folder_Ready = 1;
                System.out.println("Create Folder Success");
                break;
            }
            if(Files.exists(Path.of("D:\\" + FileName ), LinkOption.NOFOLLOW_LINKS) == true){
                Sign_Folder_Ready = 1;
                System.out.println("Folder Exists");
                break;
            }
        }
        for(int i=5;i>0;i--){
            FileLen_success = Methods.DownLoad_Paper_to_Folder(FileName, exam_pos);

            if(FileLen_success > 0L){
                Sign_Paper_Ready = 1;
                System.out.println("DownLoad Paper Success");
                break;
            }
        }
        for(int i=3;i>0;i--){
            Sign_Open_Ready = Methods.Open_Paper_File(FileName);

            if(Sign_Open_Ready == 1){
                System.out.println("Open Paper Success");
                break;
            }
        }
        if(Sign_Folder_Ready == 1 && Sign_Paper_Ready == 1 && Sign_Open_Ready == 1){
            Sign_All_Ready = 1;
        }

        return Sign_All_Ready;
    }

    private void CountDown_Timer(String folderName){
        int s = 0;
        String ExamTime_Array[] = null;
        System.out.println("CountDown_Timer   ");
        while(s == 0){
            ExamTime_Array = Methods.Refresh_ExamTime(exam_pos);
            if(ExamTime_Array.length == 3 && (Long.parseLong(ExamTime_Array[1]) + Long.parseLong(ExamTime_Array[2]) - Long.parseLong(ExamTime_Array[0])) >= 0L ){
                signal_exam_ing = 1;
                try{
                    TimeUnit.SECONDS.sleep(1);
                }catch(InterruptedException interex){ }
                if(signal_submit_confirm == 1 && signal_drt_over == 0){
                    Display_Remaining_Time(0L, 0L, 1L);
                    signal_drt_over = 1;
                    signal_exam_ing = 0;
                    break;
                }
                if(signal_exam_ing == 1 && signal_submit_confirm == 0 && signal_drt_over == 0){
                    s = Display_Remaining_Time(Long.parseLong(ExamTime_Array[0]), Long.parseLong(ExamTime_Array[1]), Long.parseLong(ExamTime_Array[2]));
                }
                if(signal_submit_confirm == 0 && s == 1){
                    signal_drt_over = 1;
                    signal_submit_confirm = 1;
                    signal_exam_ing = 0;
                    Methods.zip_file_main(folderName);
                    Methods.UpLoad_Paper_to_Server(folder_name, exam_pos);
                    Methods.Remove_Paper_File(folderName);
                    JOptionPane.showConfirmDialog(null,
                            "Your test paper has been turned in", "Time Over", JOptionPane.DEFAULT_OPTION);
                }
            }
        }
        setVisible(false);
        dispose();
        new Client_StudentLogin();
    }

    private int Display_Remaining_Time(long current_timestamp, long begin_timestamp, long exam_duration){
        long remaining_time_seconds = begin_timestamp + exam_duration - current_timestamp;
        if(remaining_time_seconds <= 0){
            return 1;
        }
        long l_days = remaining_time_seconds / (60L*60L*24L) ;
        long l_hours = remaining_time_seconds / (60L*60L) - l_days*24L ;
        long l_min = remaining_time_seconds/60L - l_hours*60L - l_days*24L*60L ;
        long l_sec = remaining_time_seconds - l_min*60L - l_hours*60L*60L - l_days*24L*60L*60L ;

        String Left_time = l_days + "Days" + l_hours + "h" + l_min + "m" + l_sec + "s";
        this.label_timer.setText("RemainingTime: "+ Left_time );
        return 0;
    }

}
