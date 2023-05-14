import com.kwabenaberko.newsapilib.NewsApiClient;
import com.kwabenaberko.newsapilib.models.request.EverythingRequest;
import com.kwabenaberko.newsapilib.models.response.ArticleResponse;
import com.mongodb.*;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.result.InsertOneResult;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Main {
    public static void main(String args[]){
        NewsApiClient newsApiClient = new NewsApiClient("1c3d62de163d418f8239dd0f54c32216");
        String[] queries={"Canada","Halifax","hockey","hurricane","electricity","house","inflation"};
        List<String> queryList= Arrays.asList(queries);


        for (String keyword :queryList){
            NewsExtraction newsExtract= new NewsExtraction(newsApiClient,keyword,new ResponseAction(keyword));
            newsExtract.extractNews();
        }

        for (String keyword :queryList){
            String fileName="./src/main/resources/"+keyword+".txt";
            NewsTransfromation newsTransform= new NewsTransfromation(fileName);
            String fileContent=newsTransform.loadFile();
            String fileContentCleaned =newsTransform.cleanup(fileContent);
            JSONObject transformedNews= new JSONObject(fileContentCleaned);



            //uploading documents to mongodb
            //connection code copied from mongodb connection suggestions https://cloud.mongodb.com/
            ConnectionString connectionString = new ConnectionString("mongodb+srv://dataa3:rootroot@cluster0.zmhpfgm.mongodb.net/?retryWrites=true&w=majority");
            MongoClientSettings settings = MongoClientSettings.builder()
                    .applyConnectionString(connectionString)
                    .serverApi(ServerApi.builder()
                            .version(ServerApiVersion.V1)
                            .build())
                    .build();
            MongoClient mongoClient = MongoClients.create(settings);

            MongoDatabase database = mongoClient.getDatabase("BigMongoNews");
            MongoCollection<Document> collection=database.getCollection(keyword);

            for (Object articles:(JSONArray) transformedNews.get("articles")){
            JSONObject articlejson=(JSONObject) articles;
            try {

                InsertOneResult result = collection.insertOne(new Document()
                        .append("_id", new ObjectId())
                        .append("publishedAt",articlejson.get("publishedAt").toString() )
                        .append("author", articlejson.get("publishedAt").toString())
                        .append("description", articlejson.get("description").toString())
                        .append("content", articlejson.get("content").toString())
                        .append("source",articlejson.get("source").toString()));
            } catch (MongoException e) {
                System.out.println("error in creating  document in Mongo Db");
            }


            }
            System.out.println("inserted all articles for file "+keyword);


        }

    }
}
