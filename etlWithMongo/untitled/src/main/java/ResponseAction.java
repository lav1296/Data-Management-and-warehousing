import com.kwabenaberko.newsapilib.NewsApiClient;
import com.kwabenaberko.newsapilib.models.response.ArticleResponse;
import org.json.*;


import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class ResponseAction implements NewsApiClient.ArticlesResponseCallback {
    String keyword;

    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }
    ResponseAction(String keyword){
        this.keyword=keyword;
    }

    @Override
    public void onSuccess(ArticleResponse response) {

        response.getArticles().toArray();

        JSONObject jsonobj = new JSONObject(response);
        String path="./src/main/resources/"+keyword+".txt";

        try( FileWriter fw =new FileWriter(path)) {

            fw.write(jsonobj.toString());
            System.out.println("Extracted news for " +keyword);
        } catch (IOException e) {
            throw new RuntimeException(e);

        }


    }


    @Override
    public void onFailure(Throwable throwable) {
        System.out.println(throwable.getMessage());
    }
}
