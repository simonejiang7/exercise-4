package solid;

import cartago.Artifact;
import cartago.OPERATION;
import cartago.OpFeedbackParam;

import java.io.IOException;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;



/**
 * A CArtAgO artifact that agent can use to interact with LDP containers in a Solid pod.
 */
public class Pod extends Artifact {

    private String podURL; // the location of the Solid pod 
    

  /**
   * Method called by CArtAgO to initialize the artifact. 
   *
   * @param podURL The location of a Solid pod
   */
    public void init(String podURL) {
        this.podURL = podURL;
        log("Pod artifact initialized for: " + this.podURL);
    }

  /**
   * CArtAgO operation for creating a Linked Data Platform container in the Solid pod
   *
   * @param containerName The name of the container to be created
   * 
   */
    @OPERATION
    // performs an action that creates an LDP container "personal-data" using the Pod artifact
    public void createContainer(String containerName) {
        log("1. Implement the method createContainer()");
        String containerURL = podURL + containerName + "/";
        String containerMetadata = "@prefix ldp: <http://www.w3.org/ns/ldp#> .\n" +
                                    "@prefix dcterms: <http://purl.org/dc/terms/> .\n" +
                                    "<> a ldp:Container, ldp:BasicContainer;\n" +
                                    "dcterms:title \"A new container\" ;\n" +
                                    "dcterms:description \"This is a new container.\" .";

        
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpPost httpPost = new HttpPost(podURL);
            httpPost.setHeader("Content-Type", "text/turtle");
            httpPost.setHeader("Link", "<http://www.w3.org/ns/ldp/BasicContainer>; rel=\"type\"");
            httpPost.setHeader("Slug", containerName);

            HttpEntity entity = new StringEntity(containerMetadata, ContentType.create("text/turtle", "UTF-8"));
            httpPost.setEntity(entity);

            // test
            // HttpGet httpGet = new HttpGet(podURL);


            try (CloseableHttpResponse response = httpClient.execute(httpPost)) {
                int statusCode = response.getStatusLine().getStatusCode();
                String responseBody = EntityUtils.toString(response.getEntity());

                System.out.println("Status Code: " + statusCode);
                System.out.println("Response Body: " + responseBody);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        log("Container created: " + containerURL);

            // try (CloseableHttpResponse response = httpClient.execute(httpGet)) {
            //     int statusCode = response.getStatusLine().getStatusCode();
            //     String responseBody = EntityUtils.toString(response.getEntity());

            //     System.out.println("Status Code: " + statusCode);
            //     System.out.println("Response Body: " + responseBody);
            //     }
            // } catch (IOException e) {
            //     e.printStackTrace();
            // }
        }


  /**
   * CArtAgO operation for publishing data within a .txt file in a Linked Data Platform container of the Solid pod
   * 
   * @param containerName The name of the container where the .txt file resource will be created
   * @param fileName The name of the .txt file resource to be created in the container
   * @param data An array of Object data that will be stored in the .txt file
   */
    @OPERATION
    public void publishData(String containerName, String fileName, Object[] data) {
        log("2. Implement the method publishData()");
    }

  /**
   * CArtAgO operation for reading data of a .txt file in a Linked Data Platform container of the Solid pod
   * 
   * @param containerName The name of the container where the .txt file resource is located
   * @param fileName The name of the .txt file resource that holds the data to be read
   * @param data An array whose elements are the data read from the .txt file
   */
    @OPERATION
    public void readData(String containerName, String fileName, OpFeedbackParam<Object[]> data) {
        data.set(readData(containerName, fileName));
    }

  /**
   * Method for reading data of a .txt file in a Linked Data Platform container of the Solid pod
   * 
   * @param containerName The name of the container where the .txt file resource is located
   * @param fileName The name of the .txt file resource that holds the data to be read
   * @return An array whose elements are the data read from the .txt file
   */
    public Object[] readData(String containerName, String fileName) {
        log("3. Implement the method readData(). Currently, the method returns mock data");

        // Remove the following mock responses once you have implemented the method
        switch(fileName) {
            case "watchlist.txt":
                Object[] mockWatchlist = new Object[]{"The Matrix", "Inception", "Avengers: Endgame"};
                return mockWatchlist;
            case "sleep.txt":
                Object[] mockSleepData = new Object[]{"6", "7", "5"};
                return mockSleepData;
            case "trail.txt":
                Object[] mockTrailData = new Object[]{"3", "5.5", "5.5"};
                return mockTrailData; 
            default:
                return new Object[0];
        }

    }

  /**
   * Method that converts an array of Object instances to a string, 
   * e.g. the array ["one", 2, true] is converted to the string "one\n2\ntrue\n"
   *
   * @param array The array to be converted to a string
   * @return A string consisting of the string values of the array elements separated by "\n"
   */
    public static String createStringFromArray(Object[] array) {
        StringBuilder sb = new StringBuilder();
        for (Object obj : array) {
            sb.append(obj.toString()).append("\n");
        }
        return sb.toString();
    }

  /**
   * Method that converts a string to an array of Object instances computed by splitting the given string with delimiter "\n"
   * e.g. the string "one\n2\ntrue\n" is converted to the array ["one", "2", "true"]
   *
   * @param str The string to be converted to an array
   * @return An array consisting of string values that occur by splitting the string around "\n"
   */
    public static Object[] createArrayFromString(String str) {
        return str.split("\n");
    }


  /**
   * CArtAgO operation for updating data of a .txt file in a Linked Data Platform container of the Solid pod
   * The method reads the data currently stored in the .txt file and publishes in the file the old data along with new data 
   * 
   * @param containerName The name of the container where the .txt file resource is located
   * @param fileName The name of the .txt file resource that holds the data to be updated
   * @param data An array whose elements are the new data to be added in the .txt file
   */
    @OPERATION
    public void updateData(String containerName, String fileName, Object[] data) {
        Object[] oldData = readData(containerName, fileName);
        Object[] allData = new Object[oldData.length + data.length];
        System.arraycopy(oldData, 0, allData, 0, oldData.length);
        System.arraycopy(data, 0, allData, oldData.length, data.length);
        publishData(containerName, fileName, allData);
    }
}
