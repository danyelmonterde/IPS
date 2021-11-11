package com.monterdev.controller;

import com.monterdev.model.Country;
import com.monterdev.model.GeneralUtils;
import com.monterdev.model.Product;
import com.monterdev.model.TableDetails;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import lombok.Getter;
import lombok.Setter;
import net.rgielen.fxweaver.core.FxmlView;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.export.HtmlExporter;
import net.sf.jasperreports.engine.util.JRSaver;
import net.sf.jasperreports.export.SimpleExporterInput;
import net.sf.jasperreports.export.SimpleHtmlExporterOutput;
import org.springframework.stereotype.Component;

import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
@FxmlView("Mainform.fxml")
@Getter
@Setter
public class MainReportController {

    @FXML
    private TableView<TableDetails> tableView;

    @FXML
    private Button buttonPrint;

    @FXML
    void initialize() {

        tableView.getColumns().clear();
        tableView.setItems(FXCollections.observableList(GeneralUtils.details));

        {
            addTableColumn("Product", "product", 200.);
        }

        {
            addTableColumn("Country", "country", 200.);
        }

        {
            addTableColumn("Price", "price", 100.);
        }

        {
            addTableColumn("Amount", "amount", 100.);
        }

        {
            addTableColumn("Total", "total", 100.);
        }

        buttonPrint.setOnAction(handler -> {
            try {
                print();
            } catch (JRException e) {
                e.printStackTrace();
            }
        });

    }

    private void addTableColumn(String amount, String amount2, double v) {
        TableColumn<TableDetails, Integer> column = new TableColumn<>(amount);
        column.setCellValueFactory(new PropertyValueFactory<>(amount2));
        column.setMinWidth(v);
        tableView.getColumns().add(column);
    }

    private void print() throws JRException {

        URL resource = getClass().getResource("/Blank_A4.jrxml");
        if(resource == null) {
            return;
        }

        //report object creating
        JasperReport report = JasperCompileManager.compileReport(resource.getPath());
        //setting file name for compiled report
        JRSaver.saveObject(report, "report.jasper");

        //Map with parameters (some output values in the header side of the report)
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("title", "Printing test");

        //datasource for report, can be replaced with data from database for example
        List<Map<String, Object>> data = GeneralUtils.details
                .stream().map(td -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("product", td.getProduct().getName());
                    map.put("country", td.getCountry().getName());
                    map.put("price", td.getPrice().doubleValue());
                    map.put("amount", td.getAmount().doubleValue());
                    map.put("total", td.getTotal().doubleValue());
                    return map;
                }).collect(Collectors.toList());

        //Jasper-wrapper for datasource
        JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(data);

        //filling report
        JasperPrint jasperPrint = JasperFillManager.fillReport("report.jasper", parameters, dataSource);

        //export report to html
        //you can use another Exporter to export into different format
        HtmlExporter exporter = new HtmlExporter();
        exporter.setExporterInput(new SimpleExporterInput(jasperPrint));
        exporter.setExporterOutput(new SimpleHtmlExporterOutput("report.html"));
        exporter.exportReport();

    }

}
