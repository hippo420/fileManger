import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FileManger {

    private static final String srcPath = "/Users/gaebabja/IdeaProjects/fileManger/src/main/resources/test";
    private static final String Width ="1521";
    private static final String Height ="866";
    private static final String GUBUN = "/";
    static List<String> sorceList = new ArrayList<String>();

    public static void main(String[] args) throws IOException {
        getSourceList(srcPath);

    }

    public static void getSourceList(String path) throws IOException {

        //List<String> nexSorce  = new ArrayList<String>();

        File file =new File(path);

        File[] list = file.listFiles();

        for ( int i=0;i<list.length;i++){
            String filename = list[i].getName();

            if(list[i].isFile()){

                String regex = "(_man)([0-9][0-9])(.xfdl)";
                Pattern pattern =  Pattern.compile(regex);
                Matcher matcher = pattern.matcher(filename);

                if(matcher.find()){
                    System.out.println("패턴 ===> "+filename);
                    patchSorce(path+GUBUN+filename,filename);

                }
            }else if(list[i].isDirectory()){
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
//        if(!file.createNewFile()){
//            System.out.println("["+filename+"] ==> 파일생성 실패");
//            return;
//        }


        FileWriter fileWriter = new FileWriter(file);
        try(fileWriter){
            BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
            try(bufferedWriter){
                String strCode ="";
                for(int i=0;i< newCode.size();i++)
                    strCode+= newCode.get(i)+"\n";

                bufferedWriter.write(strCode);
                bufferedWriter.close();
                System.out.println("["+filename+"]  ==> 파일변환완료");
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


}
