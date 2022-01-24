package com.monterdev.util;

import com.monterdev.mapper.reports.construction.ReportsMapper;
import com.monterdev.model.PurchaseOrder;
import com.monterdev.model.Reports;
import com.monterdev.model.SupplierGroup;
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
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;

import static com.monterdev.constants.GlobalConfiguration.*;
import static com.monterdev.constants.ReportNamesConstants.*;

@Component
@Getter
@Setter
public class ReportUtil {

    private String selectedMonth;

    private String selectedYear;

    private String selectedReport;

    private String selectedReportRisTypeCode;

    private int reportId;

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

    @Autowired
    private BalanceRepository balanceRepository;

    private File file = null;

    private String balanceCategory = null;

    @Autowired
    private ReportsMapper reportsMapper;

    @Autowired
    private SupplierRepository supplierRepository;

    private double purchaseAmount = 0;

    private double totalMaterialsForUse = 0;

    private double totalMaterialsIssued = 0;


    public  void generateReport() throws JRException, SQLException, FileNotFoundException {
        classifyReport();

    }

    private void classifyReport() throws FileNotFoundException {
        setReportMetadata();

        try{
                if (selectedReport.equalsIgnoreCase("Construction Materials (SOM)")) {
                    selectedReportRisTypeCode = "CM-NEW CONNECTION";
                    file = ResourceUtils.getFile("classpath:system-reports-template/CM-SOM.jrxml");
                    reportId = 1;
                    createReport();
                }

                else if (selectedReport.equalsIgnoreCase("Construction Materials (RAM)")) {
                    selectedReportRisTypeCode = "CM-REPAIRS AND MAINTENANCE";
                    file = ResourceUtils.getFile("classpath:system-reports-template/CM-RAM.jrxml");
                    reportId = 2;
                    createReport();
                }

                else if (selectedReport.equalsIgnoreCase("Construction Materials (CWP)")) {
                    selectedReportRisTypeCode = "CM-CONSTRUCTION WORK IN PROGRESS";
                    file = ResourceUtils.getFile("classpath:system-reports-template/CM-CWP.jrxml");
                    reportId = 3;
                    createReport();
                }

                else if (selectedReport.equalsIgnoreCase("Construction Materials (AR)")) {
                    selectedReportRisTypeCode = "CM-ACCOUNTS RECEIVABLE";
                    file = ResourceUtils.getFile("classpath:system-reports-template/CM-AR.jrxml");
                    reportId = 14;
                    createReport();
                }

                else if (selectedReport.equalsIgnoreCase("Construction Materials (PSE)")) {
                    selectedReportRisTypeCode = "CM-PUMPING STATION EXPENSES";
                    file = ResourceUtils.getFile("classpath:system-reports-template/CM-AR.jrxml");
                    reportId = 4;
                    createReport();
                }

                else if (selectedReport.equalsIgnoreCase("Construction Materials (ODC)")) {
                    selectedReportRisTypeCode = "CM-OTHER DEFFERED CREDITS";
                    file = ResourceUtils.getFile("classpath:system-reports-template/CM-ODC.jrxml");
                    reportId = 6;
                    createReport();
                }

                else if (selectedReport.equalsIgnoreCase("Water Meters (NC)")) {
                    selectedReportRisTypeCode = "WM-NEW CONNECTION";
                    file = ResourceUtils.getFile("classpath:system-reports-template/WM-NC.jrxml");
                    reportId = 7;
                    createReport();
                }

                else if (selectedReport.equalsIgnoreCase("Water Meters (OR)")) {
                    selectedReportRisTypeCode = "WM-OTHER RECEIVABLES";
                    file = ResourceUtils.getFile("classpath:system-reports-template/WM-OTHERS.jrxml");
                    reportId = 5;
                    createReport();
                }

                else if (selectedReport.equalsIgnoreCase("Water Meters (CWM)")) {
                    selectedReportRisTypeCode = "WM-CHANGE WATER METER";
                    file = ResourceUtils.getFile("classpath:system-reports-template/WM-CWM.jrxml");
                    reportId = 9;
                    createReport();
                }

                else if (selectedReport.equalsIgnoreCase("Chemicals")) {
                    selectedReportRisTypeCode = "CHEM";
                    file = ResourceUtils.getFile("classpath:system-reports-template/CHEM.jrxml");
                    reportId = 10;
                    createReport();
                }

                else if (selectedReport.equalsIgnoreCase("Office Supplies")) {
                    selectedReportRisTypeCode = "OS";
                    file = ResourceUtils.getFile("classpath:system-reports-template/OS.jrxml");
                    reportId = 11;
                    createReport();
                }

                else if (selectedReport.equalsIgnoreCase("Accountable Forms")) {
                    selectedReportRisTypeCode = "AF";
                    file = ResourceUtils.getFile("classpath:system-reports-template/AF.jrxml");
                    reportId = 12;
                    createReport();
                }
                else if (selectedReport.equalsIgnoreCase("Non Accountable Forms")) {
                    selectedReportRisTypeCode = "NAF";
                    file = ResourceUtils.getFile("classpath:system-reports-template/NAF.jrxml");
                    reportId = 13;
                    createReport();
                }else if (selectedReport.equalsIgnoreCase("Others")) {
                    selectedReportRisTypeCode = "OTHER";
                    file = ResourceUtils.getFile("classpath:system-reports-template/GENERIC.jrxml");
                    reportId = 15;
                    createReport();
                }else if (selectedReport.equalsIgnoreCase(CONSTRUCTION_MATERIALS_SUMMARY_168)) {
                    balanceCategory = "CONSTRUCTION MATERIALS";
                    selectedReportRisTypeCode = "SUMMARY";
                    file = ResourceUtils.getFile("classpath:system-reports-template/SINGLE_INVENTORY_SUMMARY.jrxml");
                    reportId = 16;
                    createSummaryReport(CONSTRUCTION_MATERIALS_SUMMARY_168);
                }else if (selectedReport.equalsIgnoreCase(WATER_METERS_INVENTORY_SUMMARY_169)) {
                    balanceCategory = "WATER METERS";
                    selectedReportRisTypeCode = "SUMMARY";
                    file = ResourceUtils.getFile("classpath:system-reports-template/SINGLE_INVENTORY_SUMMARY.jrxml");
                    reportId = 17;
                    createSummaryReport(WATER_METERS_INVENTORY_SUMMARY_169);
                }else if (selectedReport.equalsIgnoreCase(NON_ACCOUNTABLE_FORMS_SUMMARY)) {
                    balanceCategory = "NON-ACCOUNTABLE FORMS";
                    selectedReportRisTypeCode = "SUMMARY";
                    file = ResourceUtils.getFile("classpath:system-reports-template/SINGLE_INVENTORY_SUMMARY.jrxml");
                    reportId = 21;
                    createSummaryReport(NON_ACCOUNTABLE_FORMS_SUMMARY);
                }else if (selectedReport.equalsIgnoreCase(CHEMICALS_AND_FILTERING_SUMMARY_158)) {
                    balanceCategory = "CHEMICALS AND FILTERING SUPPLIES";
                    selectedReportRisTypeCode = "SUMMARY";
                    file = ResourceUtils.getFile("classpath:system-reports-template/SINGLE_INVENTORY_SUMMARY.jrxml");
                    reportId = 18;
                    createSummaryReport(CHEMICALS_AND_FILTERING_SUMMARY_158);
                }else if (selectedReport.equalsIgnoreCase(OFFICE_SUPPLIES_SUMMARY_151)) {
                    balanceCategory = "OFFICE SUPPLIES";
                    selectedReportRisTypeCode = "SUMMARY";
                    file = ResourceUtils.getFile("classpath:system-reports-template/SINGLE_INVENTORY_SUMMARY.jrxml");
                    reportId = 19;
                    createSummaryReport(OFFICE_SUPPLIES_SUMMARY_151);
                }else if (selectedReport.equalsIgnoreCase(ACCOUNTABLE_FORMS_SUMMARY)) {
                    balanceCategory = "ACCOUNTABLE FORMS";
                    selectedReportRisTypeCode = "SUMMARY";
                    file = ResourceUtils.getFile("classpath:system-reports-template/SINGLE_INVENTORY_SUMMARY.jrxml");
                    reportId = 20;
                    createSummaryReport(ACCOUNTABLE_FORMS_SUMMARY);
                }

            }catch (JRException e){
                System.out.println(e);
            }

    }

    private void createSummaryReport(String summaryReport) {
        Map<String, Object> mappedReport = new HashMap<>();
        List<Map> mappedSingleReportList = new ArrayList<>();

        mappedReport.put("PREPARED_BY" , signatoryRepository.findSignatoryByRole("preparedBy",reportId).getSignatory());
        mappedReport.put("NOTED_BY" , signatoryRepository.findSignatoryByRole("notedBy",reportId).getSignatory());
        mappedReport.put("CHECKED_BY" , signatoryRepository.findSignatoryByRole("checkedBy",reportId).getSignatory());
        mappedReport.put("CHECKED_BY_POSITION" , signatoryRepository.findSignatoryByRole("checkedBy",reportId).getPosition());
        mappedReport.put("PREPARED_BY_POSITION" , signatoryRepository.findSignatoryByRole("preparedBy",reportId).getPosition());
        mappedReport.put("NOTED_BY_POSITION" , signatoryRepository.findSignatoryByRole("notedBy",reportId).getPosition());



        getAllPurchaseIssuedForThisMonth(mappedSingleReportList,summaryReport);
        getAllMaterialsIssuedForThisMonth(mappedSingleReportList,summaryReport);

        mappedSingleReportList.add(mappedReport);
        try {
            createInventoryReport(mappedSingleReportList,mappedReport);
        } catch (JRException e) {
            e.printStackTrace();
        }
    }

    private void getAllPurchaseIssuedForThisMonth(List<Map> mapList, String summaryReport){

        String convertedMonth =null;
        switch(selectedMonth){
            case "Jan":
                convertedMonth= "01";
                break;
            case "Feb":
                convertedMonth= "02";
                break;
            case "Mar":
                convertedMonth= "03";
                break;
            case "Apr":
                convertedMonth= "04";
                break;
            case "May":
                convertedMonth= "05";
                break;
            case "Jun":
                convertedMonth= "06";
                break;
            case "Jul":
                convertedMonth= "07";
                break;
            case "Aug":
                convertedMonth= "08";
                break;
            case "Sep":
                convertedMonth= "09";
                break;
            case "Oct":
                convertedMonth= "10";
                break;
            case "Nov":
                convertedMonth= "11";
                break;
            default:
                convertedMonth="12";
                break;
        }
        String randomDate =selectedYear+"-"+ convertedMonth + "-" + "01" + "  01:01:01";
        if(CONSTRUCTION_MATERIALS_SUMMARY_168.equalsIgnoreCase(summaryReport)){
            List<PurchaseOrder> purchaseOrders = purchaseOrderRepository.findPurchaseOrderByItemCategory(randomDate, "CONSTRUCTION MATERIALS");

            purchaseOrders.stream().forEach(po -> {
                String purchaseOrderNumber = supplierRepository.getPurchaseOrderNumberBySkuAndDate(po.getSku(),po.getDatecreated());
                Map<String,Object> objectMap = new HashMap<>();
                objectMap.put("PURCHASE_LIST",po.getItem_name());
                objectMap.put("SALES_INVOICE_NUMBER",purchaseOrderNumber);
                objectMap.put("PURCHASED_AMOUNT",String.valueOf(po.getAmount()));
                mapList.add(objectMap);
                purchaseAmount += po.getAmount();
            });

        }
    }


    private void getAllMaterialsIssuedForThisMonth(List<Map> mapList, String summaryReport) {
        String randomDate = "01" + "-" + selectedMonth + "-" + selectedYear + "  01:01:01";
        if(CONSTRUCTION_MATERIALS_SUMMARY_168.equalsIgnoreCase(summaryReport)){
            List<String> risTypesList = risRepository.findAllRISTypeByCurrentMonthOfDateSpecifiedAndInventoryType(randomDate,"CONSTRUCTION MATERIALS");
            risTypesList.stream().forEach(ris -> {
                double totalCostPerRisType = salesRepository.computeTotalCostPerRISType(ris,randomDate);
                Map<String,Object> objectMap = new HashMap<>();
                objectMap.put("RIS_TYPE_NAMES_LIST",risTypeNamesRepository.findRisNameByType(ris).getRisname());
                objectMap.put("TOTAL_COST_OF_RIS",String.valueOf(totalCostPerRisType));
                mapList.add(objectMap);
                totalMaterialsIssued += totalCostPerRisType;
            });
        }

    }




    private String computeBeginningBalanceInventory() {
        int currentMonth = 0;
        switch (selectedMonth){
            case "Jan":
                currentMonth = 0;
                break;
            case "Feb":
                currentMonth = 1;
                break;
            case "Mar":
                currentMonth = 2;
                break;
            case "Apr":
                currentMonth = 3;
                break;
            case "May":
                currentMonth = 4;
                break;
            case "Jun":
                currentMonth = 5;
                break;
            case "Jul":
                currentMonth = 6;
                break;
            case "Aug":
                currentMonth = 7;
                break;
            case "Sep":
                currentMonth = 8;
                break;
            case "Oct":
                currentMonth = 9;
                break;
            case "Nov":
                currentMonth = 10;
                break;
            case "Dec":
                currentMonth = 11;
                break;
        }
        Calendar c = Calendar.getInstance();
        c.set(Integer.parseInt(selectedYear),currentMonth,1);
        c.add(Calendar.MONTH, -1);
        String previousMonth = new SimpleDateFormat("MMM").format(c.getTime());
        String year = new SimpleDateFormat("YYYY").format(c.getTime());
        double result = balanceRepository.getBeginningBalanceInventoryByCategory(balanceCategory,previousMonth,year);
        return String.valueOf(result);
    }

    private void createInventoryReport(List<Map> mappedSingleReport, Map<String, Object> params) throws JRException {
        JasperReport jasperReport = JasperCompileManager.compileReport(file.getAbsolutePath());
        JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(mappedSingleReport);
        Map<String,Object> titleParams = new HashMap<>();
        String beginningBalance = computeBeginningBalanceInventory();
        titleParams.put("COMPANY_NAME",getConfigValue(companyName));
        titleParams.put("STREET_ADDRESS",getConfigValue(companyAddress));
        titleParams.put("REPORT_NAME",selectedReport);
        titleParams.put("SELECTED_MONTH",selectedMonth);
        titleParams.put("SELECTED_YEAR",selectedYear);
        totalMaterialsForUse = purchaseAmount+Double.parseDouble(beginningBalance);
        titleParams.put("BEGINNING_BALANCE_INVENTORY", beginningBalance);
        titleParams.put("TOTAL_COST", String.valueOf(totalMaterialsForUse));
        double totalEndingBalance = totalMaterialsForUse-totalMaterialsIssued;
        titleParams.put("TOTAL_ENDING_BALANCE",String.valueOf(totalEndingBalance));
        purchaseAmount =0;
        totalMaterialsForUse = 0;
        totalMaterialsIssued =0;
        JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, titleParams, dataSource);
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
