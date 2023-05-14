import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;


public class NewsTransfromation {
    String fileName;

    NewsTransfromation(String fileName) {
        this.fileName = fileName;
    }

    public String loadFile() {
        List<com.kwabenaberko.newsapilib.models.Article> articleList = new ArrayList<>();
        String fileContent;
        try (FileReader fr = new FileReader(fileName);) {
             fileContent = new String(Files.readAllBytes(Paths.get(fileName)),
                    StandardCharsets.UTF_8);


        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return fileContent;

    }
    public String cleanup(String fileContent){
        //remove url links
        String URLPatten1="\"https?:\\W+\\w+\\.[a-z]+\\.[a-z]+(\\/[a-z-\\d*]+)*\\/\"";
        fileContent=performCleanupUsingRegex(fileContent,URLPatten1,"\"\"");

        String URLPattenImage="\"https?:\\W+\\w+\\.[(a-zA-Z)\\w\\&=:,\\-\\.\\d*]+(\\/[(a-zA-Z)\\w=,\\&:\\-\\.\\d*]+)*(?:jpg|gif|png)\"";
        fileContent=performCleanupUsingRegex(fileContent,URLPattenImage,"\"\"");

        // removes "\n"
        String newLineCharacterPattern="\\\\n";
        fileContent=performCleanupUsingRegex(fileContent,newLineCharacterPattern,"");


        //removes "\r"
        String carriageReturn="\\\\r";
        fileContent=performCleanupUsingRegex(fileContent,carriageReturn,"");

        //removes unicode characters \u2026
        String unicodeRegex="\\\\\\\\u[0-9a-fA-F]{4}|\\\\\\\\U[0-9a-fA-F]{8}";
        fileContent=performCleanupUsingRegex(fileContent,unicodeRegex,"");

        //removes htmlTags <a> etc
        String htmltagRegex ="<.*?>";
        fileContent=performCleanupUsingRegex(fileContent,htmltagRegex,"");

        //removes [+1241 chars]
        String SquarebracketRegex="\\[\\+\\d+\\s+\\w*\\]";
        fileContent=performCleanupUsingRegex(fileContent,SquarebracketRegex,"");

        // remove pipes "|"
        fileContent=fileContent.replaceAll("/|","");

        // removes all special characters
        fileContent=fileContent.replaceAll("/[%$@#!^&*~]","");

        // remove extra whitespace
        fileContent=fileContent.replaceAll("/  +/g","");

        return fileContent;

    }
    public String performCleanupUsingRegex(String fileContent,String regexPattern,String replace){

        fileContent=fileContent.replaceAll(regexPattern,replace);

        return fileContent;

    }
}
