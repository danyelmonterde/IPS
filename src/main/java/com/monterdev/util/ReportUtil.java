package com.monterdev.util;

import com.monterdev.mapper.reports.construction.ReportsMapper;
import com.monterdev.model.Reports;
import lombok.Getter;
import lombok.Setter;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.ResourceUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@Getter
@Setter
public class ReportUtil {

    private String selectedMonth;

    private String selectedYear;

    private String selectedReport;

    @Autowired
    private Reports report;

    private File file = null;

    @Autowired
    private ReportsMapper reportsMapper;

    public  void generateReport() throws JRException, SQLException, FileNotFoundException {
        classifyReport();

    }

    private void classifyReport() throws FileNotFoundException {
        report.setReport_name(selectedReport + "_" + selectedMonth + "_" + selectedYear + ".pdf");
        report.setMonth_of_report(selectedMonth);
        report.setReport_location("C:\\Users\\Daniel\\Desktop\\GeneratedReports\\" + report.getReport_name());
        report.setChecked_by("checked by");
        report.setNoted_by("noted by");
        report.setPrepared_by("prepared by");

            try{
                if (selectedReport.equalsIgnoreCase("Construction Materials(New Connection)")) {
                    file = ResourceUtils.getFile("classpath:system-reports-template/CM-SOM.jrxml");

                    JasperReport jasperReport = JasperCompileManager.compileReport(file.getAbsolutePath());

                    List<Map> mappedReportsList = reportsMapper.map();
                    JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(mappedReportsList);
                    Map<String, Object> map = new HashMap<>();
                    map.put("month_param", selectedMonth);
                    JasperPrint jasperPrint = null;

                    jasperPrint = JasperFillManager.fillReport(jasperReport, map, dataSource);
                    JasperExportManager.exportReportToPdfFile(jasperPrint, report.getReport_location());
                }

                System.out.println("***********************************************");
            }catch (JRException e){
                System.out.println(e);
            }

    }
}
