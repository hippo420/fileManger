package FileList;

import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FileListTest {

    private String path = "/Users/gaebabja/IdeaProjects/fileManger/src/main/resources/workspace/";
    private final String PRJ_CD="filetest";
    private static final String GUBUN = "/";

    @Test
    public void getFileList() throws IOException {

        List<String> fileList = new ArrayList<>();

        getSourceList(path, fileList);
        if(fileList.size()!=0){
            for(String vo : fileList){
                System.out.println("파일명 : "+vo);
            }
        }else{
            System.out.println("검색된 파일 없음");
        }


    }

    public static void getSourceList(String path,List<String> fileList) throws IOException {


        File file =new File(path);

        File[] list = file.listFiles();

        for ( int i=0;i<list.length;i++){
            String filename = list[i].getName();

            if(list[i].isFile()){
                if(filename.indexOf("_man") >=0||filename.indexOf("_pop") >=0||filename.indexOf("_tab") >=0) {
                    if (filename.indexOf(".xfdl") >= 0) {
                        String AbPath = list[i].getAbsolutePath();
                        String pgm_id = list[i].getAbsolutePath().replace("/Users/gaebabja/IdeaProjects/fileManger/src/main/resources/workspace/", "");
                        fileList.add(pgm_id+ " ==> " + filename.substring(0, filename.indexOf("_")));
                    }
                }


            }else if(list[i].isDirectory()){
                getSourceList(path+GUBUN+filename,fileList);
            }
        }


    }
}
