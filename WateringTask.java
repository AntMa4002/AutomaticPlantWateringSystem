package AutomaticPlantWateringSystem;

//firmata4j imports
import org.firmata4j.Pin;
import org.firmata4j.ssd1306.SSD1306;

//IOException import
import java.io.IOException;

//TimerTask import
import java.util.ArrayList;
import java.util.TimerTask;

public class WateringTask extends TimerTask // PlantWaterTask extends the TimerTask class
{
    // private variables to be used in the constructor and methods
    private final SSD1306 display;
    private final Pin Moisture;
    private final Pin Pump;
    private boolean Moist;
    private final ArrayList<Integer> MoistValues;
    private long MoistureValue;

    public WateringTask(SSD1306 display, Pin Moisture, Pin Pump, ArrayList<Integer> MoistValues) // Constructor
    {
        // this statements
        this.display = display;
        this.Moisture = Moisture;
        this.Pump = Pump;
        this.MoistValues = MoistValues;
    }

    public ArrayList<Integer> getMoistValues() // ArrayList Method
    {
        return MoistValues;
    }

    @Override
    public void run() // run method
    {
        // Moisture reading

        MoistureValue = Moisture.getValue();
        MoistValues.add(Integer.valueOf((int) MoistureValue));
        System.out.println("Moisture value is: " + MoistureValue);

        // if and else if blocks to determine if the plant requires watering
        if (MoistureValue <= 750 && MoistureValue > 720) {
            Moist = false;
            // Display showing the moisture value and state of the plant
            display.getCanvas().clear();
            String MoistVolt = String.valueOf(MoistureValue);
            MoistVolt = "Moisture value is " + MoistVolt + "\nThe plant needs\nwatering!\nPump is on";
            display.getCanvas().drawString(0, 0, MoistVolt);
            display.display();
        } else if (MoistureValue <= 720 && MoistureValue > 705) {
            Moist = false;
            // Display showing the moisture value and state of the plant
            display.getCanvas().clear();
            String MoistVolt = String.valueOf(MoistureValue);
            MoistVolt = "Moisture value is " + MoistVolt + "\nThe plant still\nneeds watering!\nPump is on";
            display.getCanvas().drawString(0, 0, MoistVolt);
            display.display();
        } else if (MoistureValue <= 705 && MoistureValue >= 500) {
            Moist = true;
            // Display showing the moisture value and state of the plant
            display.getCanvas().clear();
            String MoistVolt = String.valueOf(MoistureValue);
            MoistVolt = "Moisture value is " + MoistVolt + "\nThe plant is watered enough!\nPump is off";
            display.getCanvas().drawString(0, 0, MoistVolt);
            display.display();
        }

        // if and else statements to start the pump
        if (!Moist) {
            // try and catch block to accommodate for the Thread.sleep() method
            try {
                // Pump turned on for 1 second and then turned off
                Pump.setValue(1);
                Thread.sleep(900);
                Pump.setValue(0);
            } catch (IOException e) {
                throw new RuntimeException(e);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        } else {
            try {
                // Pump is turned off
                Pump.setValue(0);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

    }
}
