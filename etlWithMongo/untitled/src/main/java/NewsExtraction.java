import com.kwabenaberko.newsapilib.NewsApiClient;
import com.kwabenaberko.newsapilib.models.request.EverythingRequest;
import com.kwabenaberko.newsapilib.models.response.ArticleResponse;

import java.util.List;

public class NewsExtraction {

    NewsApiClient client;
    String keyword;
    NewsApiClient.ArticlesResponseCallback responseCallback;

    NewsExtraction(NewsApiClient client, String keyword,NewsApiClient.ArticlesResponseCallback action){
        this.client=client;
        this.keyword=keyword;
        this.responseCallback=action;
    }
    public void  extractNews(){
        //code used from https://newsapi.org/docs/client-libraries/java to use the client for the news api
        //code is modified to store the response to files rather printing

            client.getEverything(
                    new EverythingRequest.Builder()
                            .q(keyword)
                            .build(),responseCallback
            );
    }

}
