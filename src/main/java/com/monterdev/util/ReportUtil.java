//package com.monterdev.util;
//
//import net.sf.jasperreports.engine.*;
//import net.sf.jasperreports.engine.export.JRPdfExporter;
//import net.sf.jasperreports.export.SimpleExporterInput;
//import net.sf.jasperreports.export.SimpleOutputStreamExporterOutput;
//import net.sf.jasperreports.export.SimplePdfExporterConfiguration;
//import net.sf.jasperreports.export.SimplePdfReportConfiguration;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.context.ConfigurableApplicationContext;
//import org.springframework.stereotype.Component;
//
//import javax.sql.DataSource;
//import java.io.InputStream;
//import java.sql.SQLException;
//import java.util.HashMap;
//import java.util.Map;
//
//@Component
//public class ReportUtil {
//
//    @Autowired
//    private DataSource dataSource ;
//
//    public  void generateReport() throws JRException, SQLException {
//
//        Map<String, Object> parameters = new HashMap<>();
//        parameters.put("param1", "Employee Report");
//        InputStream employeeReportStream
//                = ReportUtil.class.getResourceAsStream("/sample.jrxml");
//        JasperReport jasperReport
//                = JasperCompileManager.compileReport(employeeReportStream);
//        JRPdfExporter exporter = new JRPdfExporter();
//        System.out.println(dataSource);
//        JasperPrint jasperPrint = JasperFillManager.fillReport(
//                jasperReport, parameters, dataSource.getConnection());
//
//        exporter.setExporterInput(new SimpleExporterInput(jasperPrint));
//        exporter.setExporterOutput(
//                new SimpleOutputStreamExporterOutput("employeeReport.pdf"));
//
//        SimplePdfReportConfiguration reportConfig
//                = new SimplePdfReportConfiguration();
//        reportConfig.setSizePageToContent(true);
//        reportConfig.setForceLineBreakPolicy(false);
//
//        SimplePdfExporterConfiguration exportConfig
//                = new SimplePdfExporterConfiguration();
//        exportConfig.setMetadataAuthor("baeldung");
//        exportConfig.setEncrypted(true);
//        exportConfig.setAllowedPermissionsHint("PRINTING");
//
//        exporter.setConfiguration(reportConfig);
//        exporter.setConfiguration(exportConfig);
//
//        exporter.exportReport();
//    }
//}
