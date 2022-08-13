package com.monterdev.controller;

import com.jfoenix.controls.JFXButton;
import com.monterdev.model.RequisitionIssueSlipSignatories;
import com.monterdev.repository.RISSignatoryRepository;
import com.monterdev.util.Prompt;
import com.monterdev.util.StageLoader;
import com.opencsv.bean.CsvToBeanBuilder;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import lombok.Getter;
import net.rgielen.fxweaver.core.FxmlView;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileReader;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

@Component
@FxmlView("RisSignatory.fxml")
@Getter
public class RisSignatoryController implements Initializable {

    @FXML
    private JFXButton btnImport;

    @FXML
    private JFXButton btnCancel;

    @Autowired
    private ConfigurableApplicationContext applicationContext;

    @Autowired
    private RISSignatoryRepository risSignatoryRepository;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        btnCancelOnAction();
        btnImportOnAction();
    }

    private void btnImportOnAction() {
        btnImport.setOnAction(e -> {
            Stage stage = (Stage) btnImport.getScene().getWindow();
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Import Employee List to Database");
            File file = fileChooser.showOpenDialog(stage);
            if (file != null) {
                try {
                    List<RequisitionIssueSlipSignatories> importRisSignatories = new CsvToBeanBuilder(new FileReader(file))
                            .withType(RequisitionIssueSlipSignatories.class)
                            .build().parse();
                    System.out.println(importRisSignatories.size());

                    if (Prompt.confirm("Are you sure you want to import this file? Existing employee list will be deleted.").get().getText().equalsIgnoreCase("OK")) {
                        risSignatoryRepository.truncateRisSignatory();
                        List<RequisitionIssueSlipSignatories> imported = risSignatoryRepository.saveAll(importRisSignatories);
                        if (!ObjectUtils.isEmpty(imported)) {
                            Prompt.success("Employee List was imported successfully!");
                        } else {
                            Prompt.failed("An error occurred while importing data!");
                        }
                    } else {
                        System.out.println("no");
                    }
                } catch (Exception ex) {
                    Prompt.failed("An error occurred while importing data!");
                }
            }
        });
    }

    private void btnCancelOnAction() {
        btnCancel.setOnAction(e -> {
            Stage currentStage = (Stage) btnCancel.getScene().getWindow();
            new StageLoader().load(MainDashboardController.class, applicationContext, currentStage);
        });
    }
}
