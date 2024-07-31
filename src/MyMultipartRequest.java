import java.io.*;
import java.util.*;

/**
 * 멀티파트 파일 읽어서 파일로 저장
 * <예시>
 *     MultipartFile firstFile = myMultipartRequest.getMultipartFile("text1");
 *     firstFile.store("c:/output/first.txt);
 *
 *     MultipartFile secondFile = myMultipartRequest.getMultipartFile("text2");
 *     secondFile.store("c:/output/second.txt);
 */
class MyMultipartRequest {
    Map<String, String> headers;
    String boundary;
    boolean methodPass;
    boolean boundaryOpen;
    boolean currentLineBoundary;

    boolean fileContentOpen;
    int boundaryCount;
    List<MultipartFile> multipartFileList;

    public MyMultipartRequest() {
        headers = new HashMap<>();
        boundary = "";
        multipartFileList = new ArrayList<>();
    }

    void parse(File data) {
        try {
            Scanner scanner = new Scanner(data);
            MultipartFile multipartFile = null;

            while(scanner.hasNextLine()) {
                String currentLine = scanner.nextLine();

                //문서 첫번쨰 줄 작업
                if (!methodPass) {
                    String method = createHttpMethod(currentLine);
                    headers.put("method", method);
                    continue;
                }
                currentLineBoundary = false;
                //나머지 줄 작업
                String currentLineHeaderKey = lineContentSplit(currentLine);
                if (currentLineBoundary) {
                    continue;
                }

                if (boundaryOpen) {
                    multipartFile = new MultipartFile(headers);
                    multipartFile.startLineParse(headers, currentLineHeaderKey);
                    boundaryOpen = false;
                    continue;
                }

                //빈 값 다음 줄 내용 저장하기
                // TODO: 2024-07-31 만약 컨텐츠 내용이 한줄이 아니라면?
                if (fileContentOpen && !currentLine.isEmpty()) {
                    multipartFile.multipart.put("content", currentLine);
                    fileContentOpen = false;
                }

                // TODO: 2024-07-31 첫번쨰 멀티파트 부분은 널조건으로 처리했지만 두번쨰 바운더리 시작점에서 boundary count가 증가해 중복으로 리스트에 담김
                // lineContentSplit 메서드 후에 currentLineBoundary로 일단 해결
                if ((boundaryCount > multipartFileList.size()) && multipartFile != null) {
                    multipartFileList.add(multipartFile);
                }

            }
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }

    }
    String createHttpMethod(String line) {
        String methodName = line.substring(0, line.indexOf("/"));
        this.methodPass = true;
        return methodName.trim();
    }
    String getMethod() {
        return this.headers.get("method");
    }
    String lineContentSplit(String line) {
        //빈 라인: 다음 줄은 컨텐츠를 알리기 위한 작업
        if (line.isEmpty()) {
            fileContentOpen = true;
            return "";
        }

        String headerKey = "";
        String headerValue = "";

        if (line.contains(":")) {
            String[] headerArr = line.split(":", 2);

            headerKey = headerArr[0].trim();
            headerValue = headerArr[1].trim();
            headers.put(headerKey, headerValue);

            if (headerValue.contains("boundary")) {
                this.boundary = headerValue.substring(headerValue.indexOf("boundary=") + "boundary=".length());
            }
        }
        if (!this.boundary.isEmpty()) {
            boundaryLineSearch(line);
        }
        return headerKey;
    }

    void boundaryLineSearch(String line) {
        if (line.indexOf(this.boundary) == 2 && line.contains(this.boundary)) {
            boundaryOpen = true;
            currentLineBoundary = true;
            boundaryCount++;
        }
    }

    MultipartFile getMultipartFile(String searchFileName) {
        if (searchFileName.isEmpty()) {
            System.out.println("찾는 파일 이름으로는 존재하지 않습니다.");
            return null;
        }
        for (MultipartFile mf : multipartFileList) {
            if (mf.multipart.get("name").equals(searchFileName)) {
                return mf;
            }
        }
        return null;
    }

}
