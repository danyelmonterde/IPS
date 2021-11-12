package com.monterdev.util;

import com.monterdev.mapper.reports.construction.ReportsMapper;
import com.monterdev.model.Reports;
import com.monterdev.repository.ReportRepository;
import lombok.Getter;
import lombok.Setter;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;
import org.springframework.util.ResourceUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.sql.SQLException;
import java.util.*;

@Component
@Getter
@Setter
public class ReportUtil {

    private String selectedMonth;

    private String selectedYear;

    private String selectedReport;

    private String selectedReportRisTypeCode;

    @Autowired
    private Reports report;

    @Autowired
    private ReportRepository reportRepository;

    private File file = null;

    @Autowired
    private ReportsMapper reportsMapper;

    public  void generateReport() throws JRException, SQLException, FileNotFoundException {
        classifyReport();

    }

    private void classifyReport() throws FileNotFoundException {
        setReportMetadata();

        try{
                if (selectedReport.equalsIgnoreCase("Construction Materials(New Connection)")) {
                    selectedReportRisTypeCode = "CM-NEW CONNECTION";
                    file = ResourceUtils.getFile("classpath:system-reports-template/CM-SOM.jrxml");
                }

                createReport();

            }catch (JRException e){
                System.out.println(e);
            }

    }

    private void setReportMetadata() {

        report.setReport_name(selectedReport + "_" + selectedMonth + "_" + selectedYear +"_"+UUID.randomUUID()+".pdf");
        report.setMonth_of_report(selectedMonth);
        report.setReport_location("C:\\Users\\Daniel\\Desktop\\GeneratedReports\\" + report.getReport_name());
        report.setChecked_by("checked by");
        report.setNoted_by("noted by");
        report.setPrepared_by("prepared by");
    }

    private void createReport() throws JRException {
        JasperReport jasperReport = JasperCompileManager.compileReport(file.getAbsolutePath());
        List<Map> mappedReportsList = reportsMapper.map();
        JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(mappedReportsList);
        Map<String, Object> map = new HashMap<>();
        map.put("month_param", selectedMonth);
        JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, map, dataSource);
        JasperExportManager.exportReportToPdfFile(jasperPrint, report.getReport_location());
    }

    private void saveReport(){
        Reports reports = reportRepository.save(report);
        if(!ObjectUtils.isEmpty(reports)){
            Prompt.success("Report was successfully generated at "+reports.getReport_location());
        }else{
            Prompt.failed("Report was not generated!");
        }
    }
}
