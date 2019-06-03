package ro.pub.cs.systems.eim.systems.recapitulare_colocviu;

import android.util.Log;

import java.util.Scanner;

public class WeatherForecastInformation {

    private String temperature;
    private String windSpeed;
    private String condition;
    private String pressure;
    private String humidity;
    private String all;

    public WeatherForecastInformation() {
        this.temperature = null;
        this.windSpeed = null;
        this.condition = null;
        this.pressure = null;
        this.humidity = null;
    }

    public WeatherForecastInformation(String temperature, String windSpeed, String condition, String pressure, String humidity) {
        this.temperature = temperature;
        this.windSpeed = windSpeed;
        this.condition = condition;
        this.pressure = pressure;
        this.humidity = humidity;
    }

    public WeatherForecastInformation(String all) {
        this.all = all;
    }

    public String getTemperature() {
        return temperature;
    }

    public void setTemperature(String temperature) {
        this.temperature = temperature;
    }

    public String getWindSpeed() {
        return windSpeed;
    }

    public void setWindSpeed(String windSpeed) {
        this.windSpeed = windSpeed;
    }

    public String getCondition() {
        return condition;
    }

    public void setCondition(String condition) {
        this.condition = condition;
    }

    public String getPressure() {
        return pressure;
    }

    public void setPressure(String pressure) {
        this.pressure = pressure;
    }

    public String getHumidity() {
        return humidity;
    }

    public void setHumidity(String humidity) {
        this.humidity = humidity;
    }

//    @Override
//    public String toString() {
//        return "WeatherForecastInformation{" +
//                "temperature='" + temperature + '\'' +
//                ", windSpeed='" + windSpeed + '\'' +
//                ", condition='" + condition + '\'' +
//                ", pressure='" + pressure + '\'' +
//                ", humidity='" + humidity + '\'' +
//                '}';
//    }

    @Override
    public String toString() {
        String afisare = "'WeatherForecastInformation: '\'";

        Scanner scanner = new Scanner(all);
        Log.i(Constants.TAG, "De afisat linie: \n");
        while (scanner.hasNextLine()) {
            String line = scanner.nextLine();
            Log.i(Constants.TAG, line);

            afisare += "" + line;
        }
        scanner.close();


//        return "WeatherForecastInformation: " + afisare;
        return afisare;
    }

}
