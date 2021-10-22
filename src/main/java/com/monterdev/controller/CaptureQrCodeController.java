package com.monterdev.controller;

import com.google.zxing.NotFoundException;
import com.jfoenix.controls.JFXButton;
import com.monterdev.model.Item;
import com.monterdev.util.OpenCvUtils;
import com.monterdev.util.QrCodeUtil;
import javafx.application.Platform;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Paint;
import javafx.stage.Stage;
import lombok.Getter;
import net.rgielen.fxweaver.core.FxmlView;
import org.opencv.core.Mat;
import org.opencv.objdetect.QRCodeDetector;
import org.opencv.videoio.VideoCapture;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static com.monterdev.constants.GlobalConfiguration.capturedQrCodeDirectory;
import static com.monterdev.constants.GlobalConfiguration.getConfigValue;

@Component
@FxmlView("CaptureQrCode.fxml")
@Getter
public class CaptureQrCodeController {

    @FXML
    private JFXButton openCamera;

    @FXML
    private JFXButton captor;

    @FXML
    private ImageView currentFrame;

    @Autowired
    private Item item;

    @FXML
    private Label labelStatus = new Label();

    // a timer for acquiring the video stream
    private ScheduledExecutorService timer;
    // the OpenCV object that realizes the video capture
    private VideoCapture capture = new VideoCapture();
    // a flag to change the button behavior
    private boolean cameraActive = false;
    // the id of the camera to be used
    private static int cameraId = 0;

    public void initialize() {

        startCamera(null);
    }


    public void startCamera(ActionEvent actionEvent) {

        if (!this.cameraActive) {
            // start the video capture
            this.capture.open(cameraId);

            // is the video stream available?
            if (this.capture.isOpened()) {
                // this.cameraActive = true;

                // grab a frame every 33 ms (30 frames/sec)
                Runnable frameGrabber = new Runnable() {

                    @Override
                    public void run() {

                        // effectively grab and process a single frame
                        Mat frame = grabFrame();

                        QRCodeDetector decoder = new QRCodeDetector();
                        Mat points = new Mat();
                        String sku = decoder.detectAndDecode(frame, points);
                        if (!ObjectUtils.isEmpty(sku)) {

                            Platform.runLater(() -> {
                                item.setSku(Integer.parseInt(sku));
                                item.setItem_name(sku);
                                Stage stage = (Stage) openCamera.getScene().getWindow();
                                stage.close();
                            });

                            return;

                        }

                        // convert and show the frame
                        Image imageToShow = OpenCvUtils.mat2Image(frame);
                        updateImageView(currentFrame, imageToShow);
                    }
                };

                this.timer = Executors.newSingleThreadScheduledExecutor();
                this.timer.scheduleAtFixedRate(frameGrabber, 0, 33, TimeUnit.MILLISECONDS);

                // update the button content
                this.openCamera.setText("Stop Camera");
            } else {
                // log the error
                System.err.println("Impossible to open the camera connection...");
            }
        } else {
            // the camera is not active at this point
            this.cameraActive = false;
            // update again the button content
            this.openCamera.setText("Start Camera");

            // stop the timer
            this.stopAcquisition();
        }
    }

    /**
     * Get a frame from the opened video stream (if any)
     *
     * @return the {@link Mat} to show
     */
    private Mat grabFrame() {
        // init everything
        Mat frame = new Mat();

        // check if the capture is open
        if (this.capture.isOpened()) {
            try {
                // read the current frame
                this.capture.read(frame);

                // if the frame is not empty, process it
                if (!frame.empty()) {
                    //Imgproc.cvtColor(frame, frame, Imgproc.);
                }

            } catch (Exception e) {
                // log the error
                System.err.println("Exception during the image elaboration: " + e);
            }
        }

        return frame;
    }

    /**
     * Stop the acquisition from the camera and release all the resources
     */
    private void stopAcquisition() {
        if (this.timer != null && !this.timer.isShutdown()) {
            try {
                // stop the timer
                this.timer.shutdown();
                this.timer.awaitTermination(33, TimeUnit.MILLISECONDS);
            } catch (InterruptedException e) {
                // log any exception
                System.err.println("Exception in stopping the frame capture, trying to release the camera now... " + e);
            }
        }

        if (this.capture.isOpened()) {
            // release the camera
            this.capture.release();
        }
    }

    /**
     * Update the {@link ImageView} in the JavaFX main thread
     *
     * @param view  the {@link ImageView} to update
     * @param image the {@link Image} to show
     */
    private void updateImageView(ImageView view, Image image) {
        OpenCvUtils.onFXThread(view.imageProperty(), image);
    }

    /**
     * On application close, stop the acquisition from the camera
     */
    protected void setClosed() {
        this.stopAcquisition();
    }

    public void captureImage(ActionEvent actionEvent) {
        labelStatus.textProperty().addListener((observableValue, oldValue, newValue) -> {

        });

        try {

            Mat frame = grabFrame();

            QRCodeDetector decoder = new QRCodeDetector();
            Mat points = new Mat();
            String data = decoder.detectAndDecode(frame, points);
            System.out.println(data);
            Image imageToShow = OpenCvUtils.mat2Image(frame);
            File outputFile = new File(getConfigValue(capturedQrCodeDirectory));
            BufferedImage bImage = SwingFXUtils.fromFXImage(imageToShow, null);
            try {
                ImageIO.write(bImage, "png", outputFile);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            String sku = QrCodeUtil.readQRCodeFromInputStream(new FileInputStream(outputFile));
            if (!sku.isEmpty()) {
                labelStatus.setText("SUCCESS");
                labelStatus.setTextFill(Paint.valueOf("GREEN"));
                item.setSku(Integer.parseInt(sku));
                Thread.sleep(2000);
                Stage stage = (Stage) openCamera.getScene().getWindow();
                stage.close();
            }

        } catch (IOException | NotFoundException | InterruptedException notFoundException) {
            labelStatus.setText("RETRY");
            labelStatus.setTextFill(Paint.valueOf("RED"));
        }
    }
}
