package com.monterdev.controller;

import com.jfoenix.controls.JFXButton;
import com.monterdev.model.Item;
import com.monterdev.util.OpenCvUtils;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import lombok.Getter;
import net.rgielen.fxweaver.core.FxmlView;
import org.opencv.core.Mat;
import org.opencv.objdetect.QRCodeDetector;
import org.opencv.videoio.VideoCapture;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Component
@FxmlView("CaptureQrCode.fxml")
@Getter
public class CaptureQrCodeController {

    @FXML
    private JFXButton openCamera;

    @FXML
    private JFXButton cancelButton;

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
        labelStatus.textProperty().addListener((observable, oldValue, newValue) -> {
            if (oldValue != newValue) {

            }
        });
        startCamera(null);
    }

    public void cancelCapture(ActionEvent actionEvent) {
        stopAcquisition();
        Stage stage = (Stage) cancelButton.getScene().getWindow();
        stage.close();
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
                        Image imageToShow = null;
                        String sku = decoder.detectAndDecode(frame, points).intern();
                        if (!ObjectUtils.isEmpty(sku)) {
                            imageToShow = OpenCvUtils.mat2Image(frame);
                            updateImageView(currentFrame, imageToShow);
                            stopAcquisition();
                            points = null;
                            decoder = null;
                            frame = null;

                            Platform.runLater(() -> {

                                labelStatus.setText(sku);
                                item.setSku(Integer.parseInt(sku));
                                item.setItem_name(sku);
                                System.gc();
                                Stage stage = (Stage) openCamera.getScene().getWindow();
                                stage.close();

                            });

                            return;

                        } else {
                            points = null;
                            decoder = null;
                            frame = null;
                            Platform.runLater(() -> {
                                labelStatus.setText("Retry");
                            });

                        }

                        // convert and show the frame


                    }
                };

                this.timer = Executors.newSingleThreadScheduledExecutor();
                this.timer.scheduleAtFixedRate(frameGrabber, 0, 1, TimeUnit.SECONDS);

                // update the button content
                // this.openCamera.setText("Stop Camera");
            } else {
                // log the error
                System.err.println("Impossible to open the camera connection...");
            }
        } else {
            // the camera is not active at this point
            //     this.cameraActive = false;
            // update again the button content
            //   this.openCamera.setText("Start Camera");

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


            } catch (Exception e) {

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

}
