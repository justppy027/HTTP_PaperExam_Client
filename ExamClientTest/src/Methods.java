import java.awt.*;
import java.io.*;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class Methods {

    public static String getEmptyStringFromNull(String s) {
        return (s == null) ? ("") : (s);
    }

    public static String textutil_join(CharSequence delimiter, Object[] tokens) {
        StringBuilder a = new StringBuilder();
        if(tokens != null) {
            for (int i = 0; i < tokens.length; i++) {
                a.append((i > 0 ? delimiter : "")).append(tokens[i]);
            }
        }
        return a.toString();
    }

    //if folder not exists, create it, return true
    //if folder exists, return false
    public static boolean Make_Exam_DIR(String STU_INFO){
        String path = "D:" + File.separator + STU_INFO + File.separator;
        // Use relative path for Unix systems
        File f = new File(path);
        boolean is_mkdir_success = f.mkdir();

        return is_mkdir_success;
    }

    public static String[] Refresh_ExamTime(int pos){

        HttpConnectionAndCode res = ExamPaperClient.get_ExamTime(pos);
        String Time_Array[];
        if(res.code == 0){
            System.out.println("Refresh Success");
            Time_Array = res.comment.split(" ");
            System.out.println(Time_Array[0]);
            System.out.println(Time_Array[1]);
            System.out.println(Time_Array[2]);

        }else {
            Time_Array = null;
            System.out.println("Fail");
        }

        return Time_Array;
    }

    public static int Open_Paper_File(String fileName){
        int f_success = 0;
        try{
            File f = new File("D:\\"+fileName+"\\test.png");
            if(!Desktop.isDesktopSupported()){
                return f_success;
            }
            Desktop desktop = Desktop.getDesktop();
            if(f.exists()){
                desktop.open(f);
                f_success = 1;
            }
        }catch (Exception e){}
        return f_success;
    }

    public static int Remove_Paper_File(String fileName){
        int clear_success = 0;
        try{
            File folder = new File("D:\\"+fileName);
            File folder_zip = new File("D:\\"+fileName + ".zip");
            if(Files.deleteIfExists(folder.toPath()) && Files.deleteIfExists(folder_zip.toPath())){
                clear_success = 1;
            }
        }catch (IOException ioex){}
        return clear_success;
    }

    public static long DownLoad_Paper_to_Folder(String FolderName, int pos){
        System.out.println("Paper Selected "+ pos);
        long a = 0;
        HttpConnectionAndCode res = ExamPaperClient.get_ExamPaper(pos);
        if(res.code == 0){
            System.out.println("Download receive from server success!");
            a = ExamPaperClient.download_file_from_server(FolderName, a, pos);
            System.out.println("dffs:"+ a);
        }
        else{
            System.out.println("download comment:"+"Fail!");
        }
        return a;
    }

    public static void UpLoad_Paper_to_Server(String FileName, int pos){
        System.out.println("Hand Selected "+ pos % 2);
        HttpConnectionAndCode res;
        res = ExamPaperClient.post_ExamPaper("D:\\" + FileName + ".zip", FileName ,pos);
        if(res.code == 0){
            System.out.println("Hand in Paper success!");
        }
        else{
            System.out.println("Post-comment:"+"Fail!");
        }
    }


    /**
     * A constants for buffer size used to read/write data
     */
    private static final int BUFFER_SIZE = 4096;
    /**
     * Compresses a list of files to a destination zip file
     * @param listFiles A collection of files and directories
     * @param destZipFile The path of the destination zip file
     * @throws FileNotFoundException
     * @throws IOException
     */
    public static void zip(List<File> listFiles, String destZipFile) throws FileNotFoundException,
            IOException {
        ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(destZipFile));
        for (File file : listFiles) {
            if (file.isDirectory()) {
                zipDirectory(file, file.getName(), zos);
            } else {
                zip_file(file, zos);
            }
        }
        zos.flush();
        zos.close();
    }
    /**
     * Compresses files represented in an array of paths
     * @param files a String array containing file paths
     * @param destZipFile The path of the destination zip file
     * @throws FileNotFoundException
     * @throws IOException
     */
    public static void zip(String[] files, String destZipFile) throws FileNotFoundException, IOException {
        List<File> listFiles = new ArrayList<File>();
        for (int i = 0; i < files.length; i++) {
            listFiles.add(new File(files[i]));
        }
        zip(listFiles, destZipFile);
    }
    /**
     * Adds a directory to the current zip output stream
     * @param folder the directory to be  added
     * @param parentFolder the path of parent directory
     * @param zos the current zip output stream
     * @throws FileNotFoundException
     * @throws IOException
     */
    public static void zipDirectory(File folder, String parentFolder,
                              ZipOutputStream zos) throws FileNotFoundException, IOException {
        for (File file : folder.listFiles()) {
            if (file.isDirectory()) {
                zipDirectory(file, parentFolder + "/" + file.getName(), zos);
                continue;
            }
            zos.putNextEntry(new ZipEntry(parentFolder + "/" + file.getName()));
            BufferedInputStream bis = new BufferedInputStream(
                    new FileInputStream(file));
            long bytesRead = 0;
            byte[] bytesIn = new byte[BUFFER_SIZE];
            int read = 0;
            while ((read = bis.read(bytesIn)) != -1) {
                zos.write(bytesIn, 0, read);
                bytesRead += read;
            }
            zos.closeEntry();
        }
    }

    public static void zip_file(File file, ZipOutputStream zos) throws FileNotFoundException, IOException{
        zos.putNextEntry(new ZipEntry(file.getName()));
        BufferedInputStream bis = new BufferedInputStream(new FileInputStream(
                file));
        long bytesRead = 0;
        byte[] bytesIn = new byte[BUFFER_SIZE];
        int read = 0;
        while ((read = bis.read(bytesIn)) != -1) {
            zos.write(bytesIn, 0, read);
            bytesRead += read;
        }
        zos.closeEntry();
    }

    public static void zip_file_main(String folderName){
        String[] myFiles = {"D:/"+folderName };
        String zipFile = "D:/"+folderName+".zip";
        try {
            zip(myFiles, zipFile);
        } catch (Exception ex) {
            // some errors occurred
            ex.printStackTrace();
        }
    }
}
