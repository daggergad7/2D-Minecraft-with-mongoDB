import com.mongodb.client.FindIterable;
import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Iterator;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.types.ObjectId;
import static com.mongodb.client.model.Filters.eq;
import java.lang.Exception;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.io.IOException;
import java.util.Scanner;


import org.bson.Document;
import com.mongodb.MongoClient;

public class LoadDB{
  String maputf = new String();
  String mapop = null;

  public void PushDB(String date){
    try{
      maputf = readFile("./src/Map.txt", StandardCharsets.UTF_8);
    }
    catch(Exception e){
      System.out.println("Error Reading File");
    }
    MongoClient mongo = new MongoClient( "localhost" , 27017 );

    System.out.println("Connected to the database successfully");

    // Accessing the database
    MongoDatabase database = mongo.getDatabase("Mappp");

    MongoCollection<Document> collection = database.getCollection("mapColl");
    System.out.println("Collection loaded successfully");

    Document document = new Document("date", date)
            .append("data", maputf);

    collection.insertOne(document);
    System.out.println("Map Uploaded successfully");
  }
  public LoadDB(){

    // Creating a Mongo client
    MongoClient mongo=new MongoClient("localhost",27017);

    System.out.println("Connected to the database successfully");

    // Accessing the database
    MongoDatabase database=mongo.getDatabase("Mappp");

    MongoCollection<Document> collection=database.getCollection("mapColl");
    System.out.println("Collection loaded successfully");

    //temp

    // Getting the iterable object
    FindIterable<Document> iterDoc = collection.find();
    int i = 1;

    // Getting the iterator
    Iterator it = iterDoc.iterator();

    while (it.hasNext()) {
      Document x = new Document();
      x = (Document)it.next();
      i++;
    }


  }

  public Object[][] GetVector(){
    // Creating a Mongo client
    MongoClient mongo = new MongoClient( "localhost" , 27017 );

    // Accessing the database
    MongoDatabase database = mongo.getDatabase("Mappp");

    MongoCollection<Document> collection = database.getCollection("mapColl");
    System.out.println("Collection loaded successfully");

    // Getting the iterable object
    FindIterable<Document> iterDoc = collection.find();
    int i = 0;

    // Getting the iterator
    Iterator it = iterDoc.iterator();

    while (it.hasNext()) {
      System.out.println(it.next());
      i++;
    }
    it = iterDoc.iterator();

    Object[][] tempStore = new Object[i][2];
    int j=0;

    while (it.hasNext()) {
      Document x = new Document();
      x = (Document) it.next();

      tempStore[j][0] = x.get("_id");
      tempStore[j][1] = x.get("date");
      j++;
    }

    return tempStore;

  }
  public void setMap(String uid){
    File mapFile = new File("./src/Map.txt");

    // Creating a Mongo client
    MongoClient mongo = new MongoClient( "localhost" , 27017 );

    // Accessing the database
    MongoDatabase database = mongo.getDatabase("Mappp");

    MongoCollection<Document> collection = database.getCollection("mapColl");
    System.out.println("Collection loaded successfully");

    Document document = collection.find(eq("_id", new ObjectId(uid))).first();
    if (document == null) {
      System.out.println("Error Occured");
    } else {

      mapop = (String)document.get("data");
      System.out.println(mapop);


      try {
        Scanner scanner = new Scanner(mapFile);
        if(mapFile.exists())
          mapFile.delete();
        mapFile.createNewFile();

        FileWriter fileWriter = new FileWriter(mapFile);
        fileWriter.write(mapop);
        fileWriter.flush();
        fileWriter.close();
      }catch(Exception e){
        System.out.println("Err1");
      }
    }
  }
  public String readFile(String path, Charset encoding)throws IOException
  {
    byte[] encoded = Files.readAllBytes(Paths.get(path));
    return new String(encoded, encoding);
  }
}

