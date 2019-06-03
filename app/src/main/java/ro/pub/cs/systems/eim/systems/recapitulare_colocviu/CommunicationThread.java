package ro.pub.cs.systems.eim.systems.recapitulare_colocviu;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;

import cz.msebera.android.httpclient.NameValuePair;
import cz.msebera.android.httpclient.client.HttpClient;
import cz.msebera.android.httpclient.client.ResponseHandler;
import cz.msebera.android.httpclient.client.entity.UrlEncodedFormEntity;
import cz.msebera.android.httpclient.client.methods.HttpGet;
import cz.msebera.android.httpclient.client.methods.HttpPost;
import cz.msebera.android.httpclient.impl.client.BasicResponseHandler;
import cz.msebera.android.httpclient.impl.client.DefaultHttpClient;
import cz.msebera.android.httpclient.message.BasicNameValuePair;
import cz.msebera.android.httpclient.protocol.HTTP;

public class CommunicationThread extends Thread {

    private ServerThread serverThread;
    private Socket socket;

    public CommunicationThread(ServerThread serverThread, Socket socket) {
        this.serverThread = serverThread;
        this.socket = socket;
    }

    @Override
    public void run() {
        if (socket == null) {
            Log.e(Constants.TAG, "[COMMUNICATION THREAD] Socket is null!");
            return;
        }
        try {
            // luam bufferul
            BufferedReader bufferedReader = Utilities.getReader(socket);
            PrintWriter printWriter = Utilities.getWriter(socket);

            if (bufferedReader == null || printWriter == null) {
                Log.e(Constants.TAG, "[COMMUNICATION THREAD] Buffered Reader / Print Writer are null!");
                return;
            }

            Log.i(Constants.TAG, "[COMMUNICATION THREAD] Waiting for parameters from client (city / information type!");

            // citim cuvantul si lungimea lui
            String city = bufferedReader.readLine();
            String lungime = bufferedReader.readLine();
//            String informationType = bufferedReader.readLine();
            if (city == null || city.isEmpty() || lungime == null || lungime.isEmpty()) {
                Log.e(Constants.TAG, "[COMMUNICATION THREAD] Error receiving parameters from client (city / information type!");
                return;
            }

            HashMap<String, WeatherForecastInformation> data = serverThread.getData();
            WeatherForecastInformation weatherForecastInformation = null;

            // daca e in chache o luam de acolo
            if (data.containsKey(city)) {
                Log.i(Constants.TAG, "[COMMUNICATION THREAD] Getting the information from the cache...");
                weatherForecastInformation = data.get(city);

            // altfel o luam de pe net
            } else {
                Log.i(Constants.TAG, "[COMMUNICATION THREAD] Getting the information from the webservice...");
                HttpClient httpClient = new DefaultHttpClient();

                // folosim post
//                HttpPost httpPost = new HttpPost(Constants.WEB_SERVICE_ADDRESS);
//                Log.i(Constants.TAG, "ce e asta?    " + httpPost.toString());


                // afisam orasul si lungimea
                Log.i(Constants.TAG, "CUVANTUL SI LUNGIMEA:    " + city + " " + lungime);
                //  folosim get
                HttpGet httpPost = new HttpGet(Constants.WEB_SERVICE_ADDRESS + city + "&minLetters=" + lungime);
//                HttpGet httpPost = new HttpGet("http://services.aonaware.com/CountCheatService/CountCheatService.asmx/LetterSolutionsMin?anagram=programming&minLetters=7");
                Log.i(Constants.TAG, "ce e asta?    " + httpPost.toString());

                //  adaugare parametri
                List<NameValuePair> params = new ArrayList<>();
                params.add(new BasicNameValuePair(Constants.QUERY_ATTRIBUTE, city));
                UrlEncodedFormEntity urlEncodedFormEntity = new UrlEncodedFormEntity(params, HTTP.UTF_8);
//                httpPost.setEntity(urlEncodedFormEntity);

                Log.i(Constants.TAG, "am ajuns aici");

                ResponseHandler<String> responseHandler = new BasicResponseHandler();
                String pageSourceCode = httpClient.execute(httpPost, responseHandler);

                Log.i(Constants.TAG, "am trecut de aici");

                if (pageSourceCode == null) {
                    Log.e(Constants.TAG, "[COMMUNICATION THREAD] Error getting the information from the webservice!");
                    return;
                }

//                afisare codul sursa
                Log.i(Constants.TAG, "Document:    " + pageSourceCode.toString());


                // parsare pagina web
                Document document = Jsoup.parse(pageSourceCode);
                Log.i(Constants.TAG, "Document:    " + document.toString());

                //parsare
                String cuvinte = "";
                Scanner scanner = new Scanner(document.toString());
                Log.i(Constants.TAG, "De afisat HOOOOOOO: \n");
                while (scanner.hasNextLine()) {
                    String line = scanner.nextLine();
//                    Log.i(Constants.TAG, line);
                    if (!line.contains("<"))
                        cuvinte += " " + line;
                }
                scanner.close();
                Log.i(Constants.TAG, "De afisat ALELUIAAA: \n" + cuvinte);

                // parsare, johnule!!!
//                JSONObject content = new JSONObject(pageSourceCode);
//                String currentObservation = content.getString("RESULTS");
//                Log.i(Constants.TAG, "currentObservation:1    " + currentObservation);
//                content = new JSONObject(currentObservation.substring(1,currentObservation.length()-1));
//                String timezone = content.getString("tz");
//                //JSONObject currentObservation = content.getJSONObject("RESULTS");
//                Log.i(Constants.TAG, "timezone:2    " + timezone);

//                Element element = document.child(0);
//                Log.i(Constants.TAG, "Element:    " + element.toString());

//                Elements elements = element.getElementsByTag(Constants.SCRIPT_TAG);
//                Log.i(Constants.TAG, "Elements:    " + elements.toString());





//                actualizam informatiile
                weatherForecastInformation  = new WeatherForecastInformation(cuvinte);
                Log.i("ORASUL: ", city);
                serverThread.setData(city, weatherForecastInformation);


//                for (Element script: elements) {
//                    String scriptData = script.data();
//                    Log.i(Constants.TAG, "Script data:    " + scriptData.toString());
//
//                    if (scriptData.contains(Constants.SEARCH_KEY)) {
//                        int position = scriptData.indexOf(Constants.SEARCH_KEY) + Constants.SEARCH_KEY.length();
//                        scriptData = scriptData.substring(position);
//                        JSONObject content = new JSONObject(scriptData);
//                        JSONObject currentObservation = content.getJSONObject(Constants.CURRENT_OBSERVATION);
//                        String temperature = currentObservation.getString(Constants.TEMPERATURE);
//                        String windSpeed = currentObservation.getString(Constants.WIND_SPEED);
//                        String condition = currentObservation.getString(Constants.CONDITION);
//                        String pressure = currentObservation.getString(Constants.PRESSURE);
//                        String humidity = currentObservation.getString(Constants.HUMIDITY);
//                        weatherForecastInformation = new WeatherForecastInformation(
//                                temperature, windSpeed, condition, pressure, humidity
//                        );
//                        serverThread.setData(city, weatherForecastInformation);
//                        break;
//                    }
//                }

            }

            Log.i(Constants.TAG, "De afisat:    " + weatherForecastInformation.toString());
            String result = weatherForecastInformation.toString();

            printWriter.println(result);
            printWriter.flush();
        } catch (IOException ioException) {
            Log.e(Constants.TAG, "[COMMUNICATION THREAD] An exception has occurred: " + ioException.getMessage());
            if (Constants.DEBUG) {
                ioException.printStackTrace();
            }
//        } catch (JSONException jsonException) {
//            Log.e(Constants.TAG, "[COMMUNICATION THREAD] An exception has occurred: " + jsonException.getMessage());
//            if (Constants.DEBUG) {
//                jsonException.printStackTrace();
//            }
        } finally {
            if (socket != null) {
                try {
                    socket.close();
                } catch (IOException ioException) {
                    Log.e(Constants.TAG, "[COMMUNICATION THREAD] An exception has occurred: " + ioException.getMessage());
                    if (Constants.DEBUG) {
                        ioException.printStackTrace();
                    }
                }
            }
        }
    }

}