import java.io.*;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;

public class ExamPaperClient {

    public static String url_time_all = "http://127.0.0.1:23456/examtime";
    public static String url_paper_all = "http://127.0.0.1:23456/paper";
    public static String url_hand_all = "http://127.0.0.1:23456/handinpaper";

    public static String agent = "";
    public static String referer = "";

    public static HttpConnectionAndCode get_ExamTime(int param_pos) {
        HttpConnectionAndCode res = Get.get(
                url_time_all,
                new String[]{"pos="+param_pos},
                agent,
                referer,
                null,
                null,
                null,
                null,
                null,
                null,
                null
        );
        return res;
    }

    public static HttpConnectionAndCode get_ExamPaper(int param_pos) {
        HttpConnectionAndCode res = Get.get(
                url_paper_all,
                new String[]{"pos="+param_pos},
                agent,
                referer,
                null,
                null,
                null,
                null,
                null,
                null,
                null
        );
        return res;
    }


    public static HttpConnectionAndCode post_ExamPaper(String FilePath, String FileName ,int param_pos){
        HttpConnectionAndCode res = Post.post(
                url_hand_all,
                new String[]{"pwd="+"_Hand_iN_px","pos="+param_pos,"fn="+FileName+".zip"},
                agent,
                referer,
                null,
                FilePath,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null
        );
        return res;
    }



    public static long download_file_from_server(String FolderName, long a, int pos){
        try{
            ReadableByteChannel readableByteChannel = Channels.newChannel(new URL(url_paper_all+"?pos="+pos).openStream());
            System.out.println("download_file_from_server   :");
            if(readableByteChannel != null){
                System.out.println("ReadableByteChannel success");
            }else{
                System.out.println("ReadableByteChannel fail");
            }
            FileOutputStream fileos = new FileOutputStream("D:\\" + FolderName + "\\test.png");
            if(fileos != null){
                System.out.println("FileOutputStream success");
            }else{
                System.out.println("FileOutputStream fail");
            }
            FileChannel writechan = fileos.getChannel();
            while(true){
                a = writechan.transferFrom(readableByteChannel, 0 , Long.MAX_VALUE);
                if(a > 0) break;
            }
        }catch (IOException ioex){
            ioex.printStackTrace();
            System.out.println("Exception!");
        }
        return a;
    }
}

