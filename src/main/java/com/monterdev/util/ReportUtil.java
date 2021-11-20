package com.monterdev.util;

import com.monterdev.mapper.reports.construction.ReportsMapper;
import com.monterdev.model.PurchaseOrder;
import com.monterdev.model.Reports;
import com.monterdev.repository.*;
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
import java.util.concurrent.atomic.AtomicReference;

import static com.monterdev.constants.ReportNamesConstants.*;

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

    @Autowired
    private SalesRepository salesRepository;

    @Autowired
    private RisRepository risRepository;

    @Autowired
    private RisTypeNamesRepository risTypeNamesRepository;

    @Autowired
    private PurchaseOrderRepository purchaseOrderRepository;

    @Autowired
    private SignatoryRepository signatoryRepository;

    private File file = null;

    @Autowired
    private ReportsMapper reportsMapper;

    public  void generateReport() throws JRException, SQLException, FileNotFoundException {
        classifyReport();

    }

    private void classifyReport() throws FileNotFoundException {
        setReportMetadata();

        try{
                if (selectedReport.equalsIgnoreCase("Construction Materials (SOM)")) {
                    selectedReportRisTypeCode = "CM-NEW CONNECTION";
                    file = ResourceUtils.getFile("classpath:system-reports-template/CM-SOM.jrxml");
                    createReport();
                }

                else if (selectedReport.equalsIgnoreCase("Construction Materials (RAM)")) {
                    selectedReportRisTypeCode = "CM-REPAIRS AND MAINTENANCE";
                    file = ResourceUtils.getFile("classpath:system-reports-template/CM-RAM.jrxml");
                    createReport();
                }

                else if (selectedReport.equalsIgnoreCase("Construction Materials (CWP)")) {
                    selectedReportRisTypeCode = "CM-CONSTRUCTION WORK IN PROGRESS";
                    file = ResourceUtils.getFile("classpath:system-reports-template/CM-CWP.jrxml");
                    createReport();
                }

                else if (selectedReport.equalsIgnoreCase("Construction Materials (AR)")) {
                    selectedReportRisTypeCode = "CM-ACCOUNTS RECEIVABLE";
                    file = ResourceUtils.getFile("classpath:system-reports-template/CM-AR.jrxml");
                    createReport();
                }

                else if (selectedReport.equalsIgnoreCase("Construction Materials (PSE)")) {
                    selectedReportRisTypeCode = "CM-PUMPING STATION EXPENSES";
                    file = ResourceUtils.getFile("classpath:system-reports-template/CM-AR.jrxml");
                    createReport();
                }

                else if (selectedReport.equalsIgnoreCase("Construction Materials (ODC)")) {
                    selectedReportRisTypeCode = "CM-OTHER DEFFERED CREDITS";
                    file = ResourceUtils.getFile("classpath:system-reports-template/CM-ODC.jrxml");
                    createReport();
                }

                else if (selectedReport.equalsIgnoreCase("Water Meters (NC)")) {
                    selectedReportRisTypeCode = "WM-NEW CONNECTION";
                    file = ResourceUtils.getFile("classpath:system-reports-template/WM-NC.jrxml");
                    createReport();
                }

                else if (selectedReport.equalsIgnoreCase("Water Meters (OR)")) {
                    selectedReportRisTypeCode = "WM-OTHER RECEIVABLES";
                    file = ResourceUtils.getFile("classpath:system-reports-template/WM-OTHERS.jrxml");
                    createReport();
                }

                else if (selectedReport.equalsIgnoreCase("Water Meters (CWM)")) {
                    selectedReportRisTypeCode = "WM-CHANGE WATER METER";
                    file = ResourceUtils.getFile("classpath:system-reports-template/WM-CWM.jrxml");
                    createReport();
                }

                else if (selectedReport.equalsIgnoreCase("Chemicals")) {
                    selectedReportRisTypeCode = "CHEM";
                    file = ResourceUtils.getFile("classpath:system-reports-template/CHEM.jrxml");
                    createReport();
                }

                else if (selectedReport.equalsIgnoreCase("Office Supplies")) {
                    selectedReportRisTypeCode = "OS";
                    file = ResourceUtils.getFile("classpath:system-reports-template/OS.jrxml");
                    createReport();
                }

                else if (selectedReport.equalsIgnoreCase("Accountable Forms")) {
                    selectedReportRisTypeCode = "AF";
                    file = ResourceUtils.getFile("classpath:system-reports-template/AF.jrxml");
                    createReport();
                }
                else if (selectedReport.equalsIgnoreCase("Non Accountable Forms")) {
                    selectedReportRisTypeCode = "NAF";
                    file = ResourceUtils.getFile("classpath:system-reports-template/NAF.jrxml");
                    createReport();
                }else if (selectedReport.equalsIgnoreCase("Others")) {
                    selectedReportRisTypeCode = "OTHER";
                    file = ResourceUtils.getFile("classpath:system-reports-template/GENERIC.jrxml");
                    createReport();
                }else if (selectedReport.equalsIgnoreCase(CONSTRUCTION_MATERIALS_SUMMARY_168)) {
                    selectedReportRisTypeCode = "SUMMARY";
                    file = ResourceUtils.getFile("classpath:system-reports-template/SINGLE_INVENTORY_SUMMARY.jrxml");
                    createSummaryReport(CONSTRUCTION_MATERIALS_SUMMARY_168);
                }else if (selectedReport.equalsIgnoreCase(WATER_METERS_INVENTORY_SUMMARY_169)) {
                    selectedReportRisTypeCode = "SUMMARY";
                    file = ResourceUtils.getFile("classpath:system-reports-template/SINGLE_INVENTORY_SUMMARY.jrxml");
                    createSummaryReport(WATER_METERS_INVENTORY_SUMMARY_169);
                }else if (selectedReport.equalsIgnoreCase(NON_ACCOUNTABLE_FORMS_SUMMARY)) {
                    selectedReportRisTypeCode = "SUMMARY";
                    file = ResourceUtils.getFile("classpath:system-reports-template/SINGLE_INVENTORY_SUMMARY.jrxml");
                    createSummaryReport(NON_ACCOUNTABLE_FORMS_SUMMARY);
                }else if (selectedReport.equalsIgnoreCase(CHEMICALS_AND_FILTERING_SUMMARY_158)) {
                    selectedReportRisTypeCode = "SUMMARY";
                    file = ResourceUtils.getFile("classpath:system-reports-template/SINGLE_INVENTORY_SUMMARY.jrxml");
                    createSummaryReport(CHEMICALS_AND_FILTERING_SUMMARY_158);
                }else if (selectedReport.equalsIgnoreCase(OFFICE_SUPPLIES_SUMMARY_151)) {
                    selectedReportRisTypeCode = "SUMMARY";
                    file = ResourceUtils.getFile("classpath:system-reports-template/SINGLE_INVENTORY_SUMMARY.jrxml");
                    createSummaryReport(OFFICE_SUPPLIES_SUMMARY_151);
                }else if (selectedReport.equalsIgnoreCase(ACCOUNTABLE_FORMS_SUMMARY)) {
                    selectedReportRisTypeCode = "SUMMARY";
                    file = ResourceUtils.getFile("classpath:system-reports-template/SINGLE_INVENTORY_SUMMARY.jrxml");
                    createSummaryReport(ACCOUNTABLE_FORMS_SUMMARY);
                }




            }catch (JRException e){
                System.out.println(e);
            }

    }

    private void createSummaryReport(String summaryReport) {
        Map<String, Object> mappedReport = new HashMap<>();
        List<Map> mappedSingleReportList = new ArrayList<>();

        mappedReport.put("COMPANY_NAME","INFANTA QUEZON WATER DISTRICT");
        mappedReport.put("STREET_ADDRESS","INFANTA, QUEZON");
        mappedReport.put("REPORT_NAME",selectedReport);
        mappedReport.put("SELECTED_MONTH",selectedMonth);
        mappedReport.put("SELECTED_YEAR",selectedYear);
        mappedReport.put("TOTAL_ENDING_BALANCE","0");
        mappedReport.put("PREPARED_BY" , signatoryRepository.findSignatoryByRole("preparedBy").getSignatory());
        mappedReport.put("NOTED_BY" , signatoryRepository.findSignatoryByRole("notedBy").getSignatory());
        mappedReport.put("CHECKED_BY" , signatoryRepository.findSignatoryByRole("checkedBy").getSignatory());
        mappedReport.put("CHECKED_BY_POSITION" , signatoryRepository.findSignatoryByRole("checkedBy").getPosition());
        mappedReport.put("PREPARED_BY_POSITION" , signatoryRepository.findSignatoryByRole("preparedBy").getPosition());
        mappedReport.put("NOTED_BY_POSITION" , signatoryRepository.findSignatoryByRole("notedBy").getPosition());

        String beginningBalance = computeBeginningBalanceInventory(summaryReport);

        getAllPurchaseIssuedForThisMonth(mappedSingleReportList,summaryReport);
        getAllMaterialsIssuedForThisMonth(mappedSingleReportList,summaryReport);

        mappedReport.put("BEGINNING_BALANCE_INVENTORY",beginningBalance);

        mappedSingleReportList.add(mappedReport);
        try {
            createInventoryReport(mappedSingleReportList);
        } catch (JRException e) {
            e.printStackTrace();
        }
    }

    private void getAllPurchaseIssuedForThisMonth(List<Map> mappedReportList, String summaryReport){
        String randomDate = "01" + "-" + selectedMonth + "-" + selectedYear + "  01:01:01";
        if(CONSTRUCTION_MATERIALS_SUMMARY_168.equalsIgnoreCase(summaryReport)){
            List<PurchaseOrder> purchaseOrders = purchaseOrderRepository.findPurchaseOrderByItemCategory(randomDate, "CONSTRUCTION MATERIALS");
           // AtomicReference<Double> totalCost = new AtomicReference<>((double) 0);
            purchaseOrders.stream().forEach(po -> {
                Map<String,Object> map = new HashMap<>();
                map.put("PURCHASE_LIST",po.getItem_name());
                map.put("SALES_INVOICE_NUMBER","1111");
                map.put("PURCHASED_AMOUT",String.valueOf(po.getAmount()));

                mappedReportList.add(map);
            });
            Map<String,Object> totalCostMap = new HashMap<>();
            totalCostMap.put("TOTAL_COST",String.valueOf("9999"));
            mappedReportList.add(totalCostMap);
        }
    }

    private void getAllMaterialsIssuedForThisMonth(List<Map> mappedReportList, String summaryReport) {
        String randomDate = "01" + "-" + selectedMonth + "-" + selectedYear + "  01:01:01";
        if(CONSTRUCTION_MATERIALS_SUMMARY_168.equalsIgnoreCase(summaryReport)){
            List<String> risTypesList = risRepository.findAllRISTypeByCurrentMonthOfDateSpecifiedAndInventoryType(randomDate,"CONSTRUCTION MATERIALS");
            risTypesList.stream().forEach(ris -> {
                Map<String, Object> materialIssuedMap = new HashMap<>();
                double totalCostPerRisType = salesRepository.computeTotalCostPerRISType(ris,randomDate);
                materialIssuedMap.put("RIS_TYPE_NAMES_LIST",risTypeNamesRepository.findRisNameByType(ris).getRisname());
                materialIssuedMap.put("TOTAL_COST_OF_RIS",String.valueOf(totalCostPerRisType));
                mappedReportList.add(materialIssuedMap);
            });
        }

    }




    private String computeBeginningBalanceInventory(String summaryReport) {
        //TO DO: if else summaryreport is... then
        return "0";
    }

    private void createInventoryReport(List<Map> mappedSingleReport) throws JRException {
        JasperReport jasperReport = JasperCompileManager.compileReport(file.getAbsolutePath());
        JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(mappedSingleReport);
        JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, null, dataSource);
        JasperExportManager.exportReportToPdfFile(jasperPrint, report.getReport_location());
        Prompt.success("Done saving report to "+report.getReport_location());
    }

    private void setReportMetadata() {

        report.setReport_name(selectedMonth + "_" + selectedYear +"_"+UUID.randomUUID()+".pdf");
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
        JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, null, dataSource);
        JasperExportManager.exportReportToPdfFile(jasperPrint, report.getReport_location());
        Prompt.success("Done saving report to "+report.getReport_location());
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
