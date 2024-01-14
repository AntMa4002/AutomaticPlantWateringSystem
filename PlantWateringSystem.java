package AutomaticPlantWateringSystem;

//firmata4j imports
import javafx.scene.control.Button;
import org.firmata4j.I2CDevice;
import org.firmata4j.IOEvent;
import org.firmata4j.Pin;
import org.firmata4j.PinEventListener;
import org.firmata4j.firmata.FirmataDevice;
import org.firmata4j.ssd1306.SSD1306;

//IOException import
import java.io.IOException;

//Timer import
import java.util.Timer;

//StdDraw import
import edu.princeton.cs.introcs.StdDraw;

//ArrayList import
import java.util.ArrayList;

public class PlantWateringSystem {
    private static int samples;

    public static void main(String[] args)
            throws IOException, InterruptedException {

        // Firmata object initialization
        var arduino = new FirmataDevice("COM3");

        // try-catch-finally block in case there is an error
        try {
            // arduino startup code
            arduino.start();
            System.out.println("Board started");
            arduino.ensureInitializationIsDone();
        } catch (Exception ex) {
            // error code
            System.out.println("Board did not connect");
        } finally {
            // ArrayList for the Integer
            ArrayList<Integer> MoistureValues = new ArrayList<Integer>();
            samples = 1;

            // Moisture sensor initialization
            var MoistureObject = arduino.getPin(Pins.A1); // Moisture sensor on A1
            MoistureObject.setMode(Pin.Mode.ANALOG);

            // Pump initialization
            var PumpObject = arduino.getPin(Pins.D2); // Water pump on D2
            PumpObject.setMode(Pin.Mode.OUTPUT);

            // OLED screen initialization
            I2CDevice i2cObject = arduino.getI2CDevice((byte) 0x3C); // OLED on I2C
            SSD1306 OledObject = new SSD1306(i2cObject, SSD1306.Size.SSD1306_128_64); // 128x64 OLED SSD1515
            OledObject.init();

            // Button Initialization
            var button = arduino.getPin(Pins.D6);
            button.setMode(Pin.Mode.INPUT);

            // timer code
            Timer timer = new Timer();
            // PlantWaterTask constructor
            var task = new WateringTask(OledObject, MoistureObject, PumpObject, MoistureValues);
            // PlantWaterTask to be peformed every 500 miliseconds or 0.5 seconds
            timer.schedule(task, 0, 500);

            // EventListener
            button.addEventListener(new PinEventListener() {
                @Override
                public void onModeChange(IOEvent ioEvent) {
                }

                @Override
                public void onValueChange(IOEvent ioEvent) {
                    if (button.getValue() == 1) {
                        // cancel task
                        timer.cancel();

                        // Graph code
                        // Window scaling
                        StdDraw.setXscale(-10, 100);
                        StdDraw.setYscale(-2, 6);

                        // Pen initialization
                        StdDraw.setPenRadius(0.005);
                        StdDraw.setPenColor(StdDraw.BLACK);

                        // X and Y axes
                        StdDraw.line(0, 0, 0, 5);
                        StdDraw.line(0, 0, 100, 0);

                        // X-axis labels
                        StdDraw.text(50, -0.25, "Samples");
                        StdDraw.text(0, -0.25, "0");
                        StdDraw.text(99, -0.25, "100");

                        // Y-axis labels
                        StdDraw.text(-7, 3, "Voltage");
                        StdDraw.text(-2, 5, "5");
                        StdDraw.text(-3, 2.5, "2.5");
                        StdDraw.text(-2, 0, "0");

                        // title
                        StdDraw.text(50, 6, "Sensor Voltage Over Samples");
                        // foreach loop to graph moisture sensor data
                        for (double a : task.getMoistValues()) {
                            // Moisture reading is converted from an analog value to a voltage value to fit
                            // the graph
                            StdDraw.text(samples, (5 * a) / 1023, "*");
                            // Sample number counter
                            samples++;
                        }
                    }

                }
            });
        }
    }

}
