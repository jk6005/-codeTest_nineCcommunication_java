import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

import com.google.gson.*;

public class CodeTest {
    static String CSV_PATH = "src/data/top20.csv";
    static String JSON_DIR = "src/result/";

    //  csv 파일 읽기
    public static List<List<String>> readCSV(String csv_path) {
        List<List<String>> list = new ArrayList<List<String>>();
        // 버퍼 불러와서 CSV 읽기
        BufferedReader bufferedReader = null;
        try {
            bufferedReader = Files.newBufferedReader(Paths.get(csv_path));
            String line = "";

            while ((line = bufferedReader.readLine()) != null) {
                if(line.equals(""))
                    continue;
                String stringArray[] = line.split(",");

                //  리스트에 추가
                List<String> stringList = Arrays.asList(stringArray);
                list.add(stringList);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                assert bufferedReader != null;
                //  버퍼 닫기
                bufferedReader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        //  반환
        return list;
    }

    public static String makeJSON(List<List<String>> top20_list) {

        //  GSON, JsonArray 객체 생성
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        JsonArray jsonArray = new JsonArray();

        List<String> columns = top20_list.get(0);
        top20_list.remove(0);


        //  JsonObject 생성 및 JsonArray 할당
        for (int i = 0; i < top20_list.size(); i++) {
            JsonObject jsonObject = new JsonObject();
            List<String> top_data = top20_list.get(i);

            jsonObject.addProperty(columns.get(0), Integer.parseInt(top_data.get(0)));
            jsonObject.addProperty(columns.get(1), top_data.get(1));
            jsonObject.addProperty(columns.get(2), top_data.get(2));
            jsonObject.addProperty(columns.get(3), top_data.get(3));

            jsonArray.add(jsonObject);
        }

        return gson.toJson(jsonArray);
    }

    public static void saveJSON(String top20_json) {
        //  폴더 생성
        File folder = new File(JSON_DIR);
        if (!folder.exists()) {
            try {
                folder.mkdir();
                System.out.println("※ 알림 - 'src/result/' 폴더가 생성 되었습니다.");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        //  JSON 저장
        try {
            FileWriter file = new FileWriter(JSON_DIR + "top20.json");
            file.write(top20_json);
            file.flush();
            file.close();
            System.out.println("※ 알림 - top20.json 이 저장 되었습니다.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void readJSON() {
        //  JSON 불러오기 및 출력
        try {
            Reader reader = new FileReader(JSON_DIR + "top20.json");

            Gson gson = new Gson();
            JsonArray jsonArray = gson.fromJson(reader, JsonArray.class);

            for (JsonElement data : jsonArray) {
                System.out.println(data.getAsJsonObject().get("licenseOrgan"));
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        //  1. CSV 파일 읽기
        List<List<String>> top20_list = readCSV(CSV_PATH);
        //  2. JSON 형태로 변환
        String top20_json = makeJSON(top20_list);
        //  3. JSON 저장
        saveJSON(top20_json);
        //  4. JSON 불러오기
        readJSON();
    }
}
