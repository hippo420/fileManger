import java.io.*;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.lang.Thread.sleep;

public class FileManger {

    private static final String srcPath = "/Users/gaebabja/IdeaProjects/fileManger/src/main/resources/test";
    private static final String Width ="1521";
    private static final String Height ="866";
    private static final String GUBUN = "/";

    private static Integer TotCnt=0;
    private static Integer FailCnt=0;
    private static Integer SuccCnt=0;

    static List<String> sorceList = new ArrayList<String>();

    public static void main(String[] args) throws IOException, InterruptedException {
        Calendar startTime = Calendar.getInstance();
        long startime = startTime.getTimeInMillis();
        System.out.println(startTime.getTimeInMillis());
        System.out.println("====================파일변환====================");
        System.out.println(">>>>>>>Start!!.....");

        getSourceList(srcPath);
        Calendar endTime = Calendar.getInstance();

        long endtime =endTime.getTimeInMillis();
        long diff = (endtime-startime)/1000;
        System.out.println(">>>>>>>Transfer Completed....총 "+diff/60  +"분"+ diff%60+"초 소요");
        System.out.println("# 전체 파일수 : "+TotCnt+"개");
        System.out.println("# 성공 파일수 : "+SuccCnt+"개");
        System.out.println("# 실패 파일수 : "+(TotCnt-SuccCnt)+"개");
    }

    public static void getSourceList(String path) throws IOException {

        //List<String> nexSorce  = new ArrayList<String>();

        File file =new File(path);

        File[] list = file.listFiles();

        for ( int i=0;i<list.length;i++){
            String filename = list[i].getName();

            if(list[i].isFile()){
                if(!checkFile(filename,"FILE"))
                    continue;
                String regex = "(_man)([0-9][0-9])(.xfdl)";
                Pattern pattern =  Pattern.compile(regex);
                Matcher matcher = pattern.matcher(filename);

                if(matcher.find()){
                    TotCnt++;
                    patchSorce(path+GUBUN+filename,filename);

                }
            }else if(list[i].isDirectory()){
                if(!checkFile(filename,"DIR"))
                    continue;
                getSourceList(path+GUBUN+filename);
            }
        }


    }

    public static void patchSorce(String filepath, String filename) throws IOException {

        List<String> patchedSorce = new ArrayList<String>();
        FileReader filereader = new FileReader(filepath);

        try(filereader){
            BufferedReader bufReader = new BufferedReader(filereader);

            try(bufReader){
                List<String> readFile = makeData(bufReader);
                patchedSorce.addAll(transCode(readFile));
            }catch(Exception e){
                System.out.println("["+filename+"] BufferError : "+e);
                bufReader.close();
            }finally {
                bufReader.close();
            }


        }catch(Exception e){
            System.out.println("["+filename+"] FileError : "+e);
            filereader.close();
        }finally {
            filereader.close();
        }

        makeNewFile(patchedSorce,filepath,filename);

    }

    public static List<String> makeData(BufferedReader bufReader ) throws IOException
    {
        List<String> lines = new ArrayList<>();
        // 파일 읽기
        String line = "";
        while ((line = bufReader.readLine()) != null) {
            lines.add(line);
        }
        bufReader.close();

        return lines;
    }

    public static void makeNewFile(List<String> newCode,String filepath, String filename) throws IOException {
        File file = new File(filepath);

        try{
            FileOutputStream fileOutputStream = new FileOutputStream(file,false);
        }catch(Exception e){
            e.printStackTrace();
        }

        FileWriter fileWriter = new FileWriter(file);

        try(fileWriter){
            BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
            try(bufferedWriter){
                String strCode ="";
                for(int i=0;i< newCode.size();i++)
                    strCode+= newCode.get(i)+"\n";

                bufferedWriter.write(strCode);
                bufferedWriter.close();
                SuccCnt++;
                System.out.println("["+filepath+"]  ==> 파일변환완료");
            }catch (Exception e){
                bufferedWriter.close();
            }finally {
                bufferedWriter.close();
            }

        }catch (Exception e){
            fileWriter.close();
        }finally {

            fileWriter.close();
        }

    }

    public static List<String> transCode(List<String> code){
        List<String> sorce = new ArrayList<>();
        for(int i=0;i<code.size() ; i++){
            if(code.get(i).indexOf("<FDL version")>=0){

                for(int j=i+1;j<code.size() ; j++){
                    if(code.get(j).indexOf("<Form")>=0)
                    {
                        String[] str = code.get(j).split(" ");
                        String newStr = "";
                        for(String vo : str){
                            if(vo.indexOf("width")>=0){
                                vo = "width="+'"'+Width+'"';
                            }else if(vo.indexOf("height")>=0){
                                vo = "width="+'"'+Height+'"';
                            }
                            newStr+=vo+' ';
                        }
                        code.remove(j);
                        code.add(j,newStr);
                    }
                    else if(code.get(j).indexOf("<Layout")>=0 && !"<Layouts>".equals(code.get(j)))
                    {
                        String[] str = code.get(j).split(" ");
                        String newStr = "";
                        for(String vo : str){
                            if(vo.indexOf("width")>=0){
                                vo = "width="+'"'+Width+'"';
                            }else if(vo.indexOf("height")>=0){
                                vo = "width="+'"'+Height+'"';
                            }
                            newStr+=vo+' ';
                        }
                        code.remove(j);
                        code.add(j,newStr);
                        break;
                    }
                }
            }else {
                sorce.add(code.get(i));
            }
        }
        return sorce;
    }

    public static Boolean checkFile( String filename , String Type){
        String[] dir ={"dev",};
        String[] file = {"NoTrans_man01.xfdl",};
        if("DIR".equals(Type)) {
            for (String vo : dir) {
                if (filename.equals(vo))
                    return false;
            }
        }else {
            for (String vo : file) {
                if (filename.equals(vo))
                    return false;
            }
        }
        return true;
    }

}
