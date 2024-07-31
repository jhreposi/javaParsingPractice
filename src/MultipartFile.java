import java.io.*;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class MultipartFile {
    //특정헤더면 해당 라인과
    // 다음 특정헤더 라인 전까지의 라인들을 받아오기

    //라인들 정보 조합해서 out file 만들기
    Map<String, String> multipart;

    public MultipartFile(Map<String, String> headers) {
        this.multipart = new HashMap<>();
    }

    //멀티 파트 첫번째 라인 파서
    void startLineParse(Map<String , String> headers, String currentKey) {
        String mergedKeyValue = headers.get(currentKey);
        String [] mergedKeyValues = mergedKeyValue.split(";");

        for(int i=0; i<mergedKeyValues.length; i++) {
            if (i==0) {
                multipart.put(currentKey, mergedKeyValues[i]);
                continue;
            }
            String [] temp = mergedKeyValues[i].split("=");
            multipart.put(temp[0].trim(), temp[1].trim());
        }
    }

    void store() {
        if( this.multipart == null) {
            return;
        }
        try {
            String path = "E:\\output\\" + this.multipart.get("filename");
            File file = new File(path);

            if (!file.exists()) {
                file.createNewFile();
            }
            FileWriter fileWriter = new FileWriter(file);
            BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
            bufferedWriter.write(this.multipart.get("content"));

            System.out.println(this.multipart.get("name") + "으로 찾은 파일 " + path + "에 생성 완료" );
            bufferedWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
