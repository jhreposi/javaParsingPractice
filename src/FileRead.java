import java.io.File;
import java.nio.file.Files;
import java.util.Map;

public class FileRead {
    public static void main(String[] args) {
        String dir = "request-dummy.txt";
        File multipartData = new File(dir);
        //System.out.println(multipartData);

        MyMultipartRequest request = new MyMultipartRequest();
        request.parse(multipartData);

        System.out.println("get method : " + request.getMethod());
        System.out.println("get host : " + request.headers.get("Host"));
        System.out.println("get host : " + request.headers.get("User-Agent"));

        MultipartFile firstFile = request.getMultipartFile("text1");
        firstFile.store();

        MultipartFile secondFile = request.getMultipartFile("text2");
        secondFile.store();



    }
}