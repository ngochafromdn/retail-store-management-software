package Server;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;

public class MongoDBConnection {

    private MongoClient mongoClient;
    private MongoDatabase database;

    // MongoDB connection string and database name
    private static final String CONNECTION_STRING = "mongodb+srv://nguyenhoangngocha:lJSUQlBOYMCLXZ1c@cluster1.e8nvi.mongodb.net/?retryWrites=true&w=majority&appName=Cluster1";  // Replace with your MongoDB URI
    private static final String DATABASE_NAME = "Project2";  // Replace with your database name

    // Constructor to initialize MongoDB connection
    public MongoDBConnection() {
        this.mongoClient = MongoClients.create(CONNECTION_STRING);
        this.database = mongoClient.getDatabase(DATABASE_NAME);
    }

    // Method to get the MongoDB database
    public MongoDatabase getDatabase() {
        return database;
    }

    // Method to close the MongoDB connection
    public void close() {
        if (mongoClient != null) {
            mongoClient.close();
        }
    }

    // Main method for testing
    public static void main(String[] args) {
        MongoDBConnection mongoDBConnection = new MongoDBConnection();
        System.out.println("MongoDB connection established successfully.");
        mongoDBConnection.close();
    }
}
