package com.monterdev.util;

import com.monterdev.mapper.reports.construction.ReportsMapper;
import com.monterdev.model.Balance;
import com.monterdev.model.PurchaseOrder;
import com.monterdev.model.Reports;
import com.monterdev.repository.*;
import lombok.Getter;
import lombok.Setter;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.ResourceUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.sql.Date;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.*;

import static com.monterdev.constants.DateConstants.*;
import static com.monterdev.constants.GlobalConfiguration.*;
import static com.monterdev.constants.InventoryTypeConstants.*;
import static com.monterdev.constants.ReportFieldsConstant.*;
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

    private double purchaseAmount = 0;

    private double totalMaterialsForUse = 0;

    private double totalMaterialsIssued = 0;

    private static String REPORT_CLASSPATH_URL = "classpath:system-reports-template/";

    private static String staticDateString = "-" + "01" + "  01:01:01";

    private String reportPreparedBy;

    private String reportCheckedBy;

    private String reportNotedBy;

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

    private File FILE_UPPER_PART = null;

    private File FILE_LOWER_PART = null;

    private String balanceCategory = null;

    @Autowired
    private ReportsMapper reportsMapper;

    @Autowired
    private SupplierRepository supplierRepository;

    public void generateReport() throws JRException, SQLException, FileNotFoundException {
        classifyReport();

    }

    private void classifyReport() throws FileNotFoundException {

        setReportMetadata(report);
        try {
            if (selectedReport.equalsIgnoreCase("Construction Materials (SOM)")) {
                selectedReportRisTypeCode = "CM-NEW CONNECTION";
                FILE_UPPER_PART = ResourceUtils.getFile(REPORT_CLASSPATH_URL + "CM-SOM.jrxml");
                reportId = 1;

                createReport();
            } else if (selectedReport.equalsIgnoreCase("Construction Materials (RAM)")) {
                selectedReportRisTypeCode = "CM-REPAIRS AND MAINTENANCE";
                FILE_UPPER_PART = ResourceUtils.getFile(REPORT_CLASSPATH_URL + "CM-RAM.jrxml");
                reportId = 2;
                createReport();
            } else if (selectedReport.equalsIgnoreCase("Construction Materials (CWP)")) {
                selectedReportRisTypeCode = "CM-CONSTRUCTION WORK IN PROGRESS";
                FILE_UPPER_PART = ResourceUtils.getFile(REPORT_CLASSPATH_URL + "CM-CWP.jrxml");
                reportId = 3;
                createReport();
            } else if (selectedReport.equalsIgnoreCase("Construction Materials (AR)")) {
                selectedReportRisTypeCode = "CM-ACCOUNTS RECEIVABLE";
                FILE_UPPER_PART = ResourceUtils.getFile(REPORT_CLASSPATH_URL + "CM-AR.jrxml");
                reportId = 14;
                createReport();
            } else if (selectedReport.equalsIgnoreCase("Construction Materials (PSE)")) {
                selectedReportRisTypeCode = "CM-PUMPING STATION EXPENSES";
                FILE_UPPER_PART = ResourceUtils.getFile(REPORT_CLASSPATH_URL + "CM-AR.jrxml");
                reportId = 4;
                createReport();
            } else if (selectedReport.equalsIgnoreCase("Construction Materials (ODC)")) {
                selectedReportRisTypeCode = "CM-OTHER DEFFERED CREDITS";
                FILE_UPPER_PART = ResourceUtils.getFile(REPORT_CLASSPATH_URL + "CM-ODC.jrxml");
                reportId = 6;
                createReport();
            } else if (selectedReport.equalsIgnoreCase("Water Meters (NC)")) {
                selectedReportRisTypeCode = "WM-NEW CONNECTION";
                FILE_UPPER_PART = ResourceUtils.getFile(REPORT_CLASSPATH_URL + "WM-NC.jrxml");
                reportId = 7;
                createReport();
            } else if (selectedReport.equalsIgnoreCase("Water Meters (OR)")) {
                selectedReportRisTypeCode = "WM-OTHER RECEIVABLES";
                FILE_UPPER_PART = ResourceUtils.getFile(REPORT_CLASSPATH_URL + "WM-OTHERS.jrxml");
                reportId = 5;
                createReport();
            }else if (selectedReport.equalsIgnoreCase("Water Meters (SOM)")) {
                selectedReportRisTypeCode = "WM-SALES OF MATERIALS";
                FILE_UPPER_PART = ResourceUtils.getFile(REPORT_CLASSPATH_URL + "WM-OTHERS.jrxml");
                reportId = 23;
                createReport();
            }
            else if (selectedReport.equalsIgnoreCase("Water Meters (CWM)")) {
                selectedReportRisTypeCode = "WM-CHANGE WATER METER";
                FILE_UPPER_PART = ResourceUtils.getFile(REPORT_CLASSPATH_URL + "WM-CWM.jrxml");
                reportId = 9;
                createReport();
            } else if (selectedReport.equalsIgnoreCase("Chemicals")) {
                selectedReportRisTypeCode = "CHEM";
                FILE_UPPER_PART = ResourceUtils.getFile(REPORT_CLASSPATH_URL + "CHEM.jrxml");
                reportId = 10;
                createReport();
            } else if (selectedReport.equalsIgnoreCase("Office Supplies")) {
                selectedReportRisTypeCode = "OS";
                FILE_UPPER_PART = ResourceUtils.getFile(REPORT_CLASSPATH_URL + "OS.jrxml");
                reportId = 11;
                createReport();
            } else if (selectedReport.equalsIgnoreCase("Accountable Forms")) {
                selectedReportRisTypeCode = "AF";
                FILE_UPPER_PART = ResourceUtils.getFile(REPORT_CLASSPATH_URL + "AF.jrxml");
                reportId = 12;
                createReport();
            } else if (selectedReport.equalsIgnoreCase("Non Accountable Forms")) {
                selectedReportRisTypeCode = "NAF";
                FILE_UPPER_PART = ResourceUtils.getFile(REPORT_CLASSPATH_URL + "NAF.jrxml");
                reportId = 13;
                createReport();
            } else if (selectedReport.equalsIgnoreCase("Others")) {
                selectedReportRisTypeCode = "OTHER";
                FILE_UPPER_PART = ResourceUtils.getFile(REPORT_CLASSPATH_URL + "GENERIC.jrxml");
                reportId = 15;
                createReport();
            } else if (selectedReport.equalsIgnoreCase(CONSTRUCTION_MATERIALS_SUMMARY_168)) {
                balanceCategory = "CONSTRUCTION MATERIALS";
                selectedReportRisTypeCode = "SUMMARY";
                FILE_UPPER_PART = ResourceUtils.getFile(REPORT_CLASSPATH_URL + "SINGLE_INVENTORY_SUMMARY_UPPER_PART.jrxml");
                FILE_LOWER_PART = ResourceUtils.getFile(REPORT_CLASSPATH_URL + "SINGLE_INVENTORY_SUMMARY_LOWER_PART.jrxml");
                reportId = 16;
                createSummaryReport(CONSTRUCTION_MATERIALS_SUMMARY_168);
            } else if (selectedReport.equalsIgnoreCase(WATER_METERS_INVENTORY_SUMMARY_169)) {
                balanceCategory = "WATER METERS";
                selectedReportRisTypeCode = "SUMMARY";
                FILE_UPPER_PART = ResourceUtils.getFile(REPORT_CLASSPATH_URL + "SINGLE_INVENTORY_SUMMARY_UPPER_PART.jrxml");
                FILE_LOWER_PART = ResourceUtils.getFile(REPORT_CLASSPATH_URL + "SINGLE_INVENTORY_SUMMARY_LOWER_PART.jrxml");
                reportId = 17;
                createSummaryReport(WATER_METERS_INVENTORY_SUMMARY_169);
            } else if (selectedReport.equalsIgnoreCase(NON_ACCOUNTABLE_FORMS_SUMMARY)) {
                balanceCategory = "NON-ACCOUNTABLE FORMS";
                selectedReportRisTypeCode = "SUMMARY";
                FILE_UPPER_PART = ResourceUtils.getFile(REPORT_CLASSPATH_URL + "SINGLE_INVENTORY_SUMMARY_UPPER_PART.jrxml");
                FILE_LOWER_PART = ResourceUtils.getFile(REPORT_CLASSPATH_URL + "SINGLE_INVENTORY_SUMMARY_LOWER_PART.jrxml");
                reportId = 21;
                createSummaryReport(NON_ACCOUNTABLE_FORMS_SUMMARY);
            } else if (selectedReport.equalsIgnoreCase(CHEMICALS_AND_FILTERING_SUMMARY_158)) {
                balanceCategory = "CHEMICALS AND FILTERING SUPPLIES";
                selectedReportRisTypeCode = "SUMMARY";
                FILE_UPPER_PART = ResourceUtils.getFile(REPORT_CLASSPATH_URL + "SINGLE_INVENTORY_SUMMARY_UPPER_PART.jrxml");
                FILE_LOWER_PART = ResourceUtils.getFile(REPORT_CLASSPATH_URL + "SINGLE_INVENTORY_SUMMARY_LOWER_PART.jrxml");
                reportId = 18;
                createSummaryReport(CHEMICALS_AND_FILTERING_SUMMARY_158);
            } else if (selectedReport.equalsIgnoreCase(OFFICE_SUPPLIES_SUMMARY_151)) {
                balanceCategory = "OFFICE SUPPLIES";
                selectedReportRisTypeCode = "SUMMARY";
                FILE_UPPER_PART = ResourceUtils.getFile(REPORT_CLASSPATH_URL + "SINGLE_INVENTORY_SUMMARY_UPPER_PART.jrxml");
                FILE_LOWER_PART = ResourceUtils.getFile(REPORT_CLASSPATH_URL + "SINGLE_INVENTORY_SUMMARY_LOWER_PART.jrxml");
                reportId = 19;
                createSummaryReport(OFFICE_SUPPLIES_SUMMARY_151);
            } else if (selectedReport.equalsIgnoreCase(ACCOUNTABLE_FORMS_SUMMARY)) {
                balanceCategory = "ACCOUNTABLE FORMS";
                selectedReportRisTypeCode = "SUMMARY";
                FILE_UPPER_PART = ResourceUtils.getFile(REPORT_CLASSPATH_URL + "SINGLE_INVENTORY_SUMMARY_UPPER_PART.jrxml");
                FILE_LOWER_PART = ResourceUtils.getFile(REPORT_CLASSPATH_URL + "SINGLE_INVENTORY_SUMMARY_LOWER_PART.jrxml");
                reportId = 20;
                createSummaryReport(ACCOUNTABLE_FORMS_SUMMARY);
            }else if (selectedReport.equalsIgnoreCase(TOTAL_INVENTORY_SUMMARY)) {
                balanceCategory = "ALL CATEGORIES";
                FILE_UPPER_PART = ResourceUtils.getFile(REPORT_CLASSPATH_URL + "TotalInventory.jrxml");
                reportId = 24;
                createTotalInventoryReport();
            }

        } catch (JRException e) {
            System.out.println(e);
        }

    }

    private void createSummaryReport(String summaryReport) {

        List<Map> purchaseOrderAndHeaderList = new ArrayList<>();
        List<Map> salesAndFooterList = new ArrayList<>();

        getAllPurchaseIssuedForThisMonth(purchaseOrderAndHeaderList, summaryReport);
        getAllMaterialsIssuedForThisMonth(salesAndFooterList, summaryReport);

        try {
            createInventoryReport(purchaseOrderAndHeaderList, salesAndFooterList);
        } catch (JRException e) {
            e.printStackTrace();
        }
    }

    private void getAllPurchaseIssuedForThisMonth(List<Map> mapList, String summaryReport) {

        String convertedMonth = convertMonth();
        String randomDate = selectedYear + "-" + convertedMonth + staticDateString;
        if (CONSTRUCTION_MATERIALS_SUMMARY_168.equalsIgnoreCase(summaryReport)) {
            List<PurchaseOrder> purchaseOrders = purchaseOrderRepository.findPurchaseOrderByItemCategory(randomDate, CONSTRUCTION_MATERIALS);
            setPurchaseOrderList(mapList, purchaseOrders);
        } else if (WATER_METERS_INVENTORY_SUMMARY_169.equalsIgnoreCase(summaryReport)) {
            List<PurchaseOrder> purchaseOrders = purchaseOrderRepository.findPurchaseOrderByItemCategory(randomDate, WATER_METERS);
            setPurchaseOrderList(mapList, purchaseOrders);
        } else if (NON_ACCOUNTABLE_FORMS_SUMMARY.equalsIgnoreCase(summaryReport)) {
            List<PurchaseOrder> purchaseOrders = purchaseOrderRepository.findPurchaseOrderByItemCategory(randomDate, NON_ACCOUNTABLE_FORMS);
            setPurchaseOrderList(mapList, purchaseOrders);
        } else if (CHEMICALS_AND_FILTERING_SUMMARY_158.equalsIgnoreCase(summaryReport)) {
            List<PurchaseOrder> purchaseOrders = purchaseOrderRepository.findPurchaseOrderByItemCategory(randomDate, CHEMICALS_AND_FILTERING_SUPPLIES);
            setPurchaseOrderList(mapList, purchaseOrders);
        } else if (OFFICE_SUPPLIES_SUMMARY_151.equalsIgnoreCase(summaryReport)) {
            List<PurchaseOrder> purchaseOrders = purchaseOrderRepository.findPurchaseOrderByItemCategory(randomDate, OFFICE_SUPPLIES);
            setPurchaseOrderList(mapList, purchaseOrders);
        } else if (ACCOUNTABLE_FORMS_SUMMARY.equalsIgnoreCase(summaryReport)) {
            List<PurchaseOrder> purchaseOrders = purchaseOrderRepository.findPurchaseOrderByItemCategory(randomDate, ACCOUNTABLE_FORMS);
            setPurchaseOrderList(mapList, purchaseOrders);
        } else {
            Prompt.failed("Report NOT Found!");
        }
    }

    private void setPurchaseOrderList(List<Map> mapList, List<PurchaseOrder> purchaseOrders) {
        purchaseOrders.stream().forEach(po -> {
            String purchaseOrderNumber = supplierRepository.getPurchaseOrderNumberBySkuAndDate(po.getSku(), po.getDatecreated());
            Map<String, Object> objectMap = new HashMap<>();
            objectMap.put("PURCHASE_LIST", po.getItem_name());
            objectMap.put("SALES_INVOICE_NUMBER", purchaseOrderNumber);
            objectMap.put("PURCHASED_AMOUNT", String.valueOf(po.getAmount()));

            mapList.add(objectMap);
            purchaseAmount += po.getAmount();
        });
    }

    private String convertMonth() {
        String convertedMonth = null;
        switch (selectedMonth) {
            case JANUARY:
                convertedMonth = "01";
                break;
            case FEBRUARY:
                convertedMonth = "02";
                break;
            case MARCH:
                convertedMonth = "03";
                break;
            case APRIL:
                convertedMonth = "04";
                break;
            case MAY:
                convertedMonth = "05";
                break;
            case JUNE:
                convertedMonth = "06";
                break;
            case JULY:
                convertedMonth = "07";
                break;
            case AUGUST:
                convertedMonth = "08";
                break;
            case SEPTEMBER:
                convertedMonth = "09";
                break;
            case OCTOBER:
                convertedMonth = "10";
                break;
            case NOVEMBER:
                convertedMonth = "11";
                break;
            default:
                convertedMonth = "12";
                break;
        }
        return convertedMonth;
    }


    private void getAllMaterialsIssuedForThisMonth(List<Map> mapList, String summaryReport) {
        String randomDate = "01" + "-" + selectedMonth + "-" + selectedYear + "  01:01:01";
        if (CONSTRUCTION_MATERIALS_SUMMARY_168.equalsIgnoreCase(summaryReport)) {
            List<String> risTypesList = risRepository.findAllRISTypeByCurrentMonthOfDateSpecifiedAndInventoryType(randomDate, CONSTRUCTION_MATERIALS);
            setRequisitionIssueSliplist(mapList, randomDate, risTypesList);
        } else if (WATER_METERS_INVENTORY_SUMMARY_169.equalsIgnoreCase(summaryReport)) {
            List<String> risTypesList = risRepository.findAllRISTypeByCurrentMonthOfDateSpecifiedAndInventoryType(randomDate, WATER_METERS);
            setRequisitionIssueSliplist(mapList, randomDate, risTypesList);
        } else if (CHEMICALS_AND_FILTERING_SUMMARY_158.equalsIgnoreCase(summaryReport)) {
            List<String> risTypesList = risRepository.findAllRISTypeByCurrentMonthOfDateSpecifiedAndInventoryType(randomDate, CHEMICALS_AND_FILTERING_SUPPLIES);
            setRequisitionIssueSliplist(mapList, randomDate, risTypesList);
        } else if (OFFICE_SUPPLIES_SUMMARY_151.equalsIgnoreCase(summaryReport)) {
            List<String> risTypesList = risRepository.findAllRISTypeByCurrentMonthOfDateSpecifiedAndInventoryType(randomDate, OFFICE_SUPPLIES);
            setRequisitionIssueSliplist(mapList, randomDate, risTypesList);
        } else if (ACCOUNTABLE_FORMS_SUMMARY.equalsIgnoreCase(summaryReport)) {
            List<String> risTypesList = risRepository.findAllRISTypeByCurrentMonthOfDateSpecifiedAndInventoryType(randomDate, ACCOUNTABLE_FORMS);
            setRequisitionIssueSliplist(mapList, randomDate, risTypesList);
        } else if (NON_ACCOUNTABLE_FORMS_SUMMARY.equalsIgnoreCase(summaryReport)) {
            List<String> risTypesList = risRepository.findAllRISTypeByCurrentMonthOfDateSpecifiedAndInventoryType(randomDate, NON_ACCOUNTABLE_FORMS);
            setRequisitionIssueSliplist(mapList, randomDate, risTypesList);
        } else {
            Prompt.failed("Report Not Found!");
        }

    }

    private void setRequisitionIssueSliplist(List<Map> mapList, String randomDate, List<String> risTypesList) {
        risTypesList.stream().forEach(ris -> {
            double totalCostPerRisType = salesRepository.computeTotalCostPerRISType(ris, randomDate);
            Map<String, Object> objectMap = new HashMap<>();
            objectMap.put("RIS_TYPE_NAMES_LIST", risTypeNamesRepository.findRisNameByType(ris).getRisname());
            objectMap.put("TOTAL_COST_OF_RIS", String.valueOf(totalCostPerRisType));
            mapList.add(objectMap);
            totalMaterialsIssued += totalCostPerRisType;
        });
    }


    private String computeBeginningBalanceInventory() {
        int currentMonth = 0;
        switch (selectedMonth) {
            case JANUARY:
                currentMonth = 0;
                break;
            case FEBRUARY:
                currentMonth = 1;
                break;
            case MARCH:
                currentMonth = 2;
                break;
            case APRIL:
                currentMonth = 3;
                break;
            case MAY:
                currentMonth = 4;
                break;
            case JUNE:
                currentMonth = 5;
                break;
            case JULY:
                currentMonth = 6;
                break;
            case AUGUST:
                currentMonth = 7;
                break;
            case SEPTEMBER:
                currentMonth = 8;
                break;
            case OCTOBER:
                currentMonth = 9;
                break;
            case NOVEMBER:
                currentMonth = 10;
                break;
            case DECEMBER:
                currentMonth = 11;
                break;
        }
        Calendar c = Calendar.getInstance();
        c.set(Integer.parseInt(selectedYear), currentMonth, 1);
        c.add(Calendar.MONTH, -1);
        String previousMonth = new SimpleDateFormat("MMM").format(c.getTime());
        String year = new SimpleDateFormat("YYYY").format(c.getTime());
        double result = 0;
        if(!balanceCategory.equalsIgnoreCase(ALL_CATEGORIES)){
            result = balanceRepository.getBeginningBalanceInventoryByCategory(balanceCategory, previousMonth, year);
        }else{
            result = balanceRepository.getTotalBeginningBalanceForThisMonth( previousMonth, year);
        }

        return String.valueOf(result);
    }

    private void createTotalInventoryReport() throws JRException{
        JasperReport totalInventoryReport = JasperCompileManager.compileReport(FILE_UPPER_PART.getAbsolutePath());
        List<Balance> totalBalanceListForThisMonth = balanceRepository.getTotalBalancesForThisMonth(selectedMonth,selectedYear);
        List<Map> inventoryListAndEndingBalances = new ArrayList<>();
        totalBalanceListForThisMonth.stream().forEach(e->{
            Map<String,Object> map = new HashMap<>();
            map.put("INVENTORY_NAME",e.getCategory());
            map.put("INVENTORY_END_BALANCE",String.valueOf(e.getEndbalance()));
            inventoryListAndEndingBalances.add(map);
        });
        JRBeanCollectionDataSource totalInventoryDatasource = new JRBeanCollectionDataSource(inventoryListAndEndingBalances);

        Map<String, Object> titleParams = new HashMap<>();
        String beginningBalance = computeBeginningBalanceInventory();
        titleParams.put(COMPANY_NAME_FIELD, getConfigValue(companyName));
        titleParams.put(STREET_ADDRESS_FIELD, getConfigValue(companyAddress));
        titleParams.put(REPORT_NAME_FIELD, selectedReport);
        titleParams.put(SELECTED_MONTH_FIELD, selectedMonth);
        titleParams.put(SELECTED_YEAR_FIELD, selectedYear);
        titleParams.put(BEGINNING_BALANCE_INVENTORY_FIELD, beginningBalance);


        double totalEndingBalance = totalBalanceListForThisMonth.stream().mapToDouble(Balance::getEndbalance).sum();
        titleParams.put(TOTAL_COST_FIELD, String.valueOf(totalEndingBalance));


        this.setReportPreparedBy(signatoryRepository.findSignatoryByRole(PREPARED_BY_ROLE_FIELD, reportId).getSignatory());
        this.setReportCheckedBy(signatoryRepository.findSignatoryByRole(CHECKED_BY_ROLE_FIELD, reportId).getSignatory());
        this.setReportNotedBy(signatoryRepository.findSignatoryByRole(NOTED_BY_ROLE_FIELD, reportId).getSignatory());

        this.report.setPrepared_by(this.getReportPreparedBy());
        this.report.setChecked_by(this.getReportCheckedBy());
        this.report.setNoted_by(this.getReportNotedBy());

        titleParams.put(PREPARED_BY_FIELD, reportPreparedBy);
        titleParams.put(NOTED_BY_FIELD, reportNotedBy);
        titleParams.put(CHECKED_BY_FIELD, reportCheckedBy);
        titleParams.put(CHECKED_BY_POSITION_FIELD, signatoryRepository.findSignatoryByRole(CHECKED_BY_ROLE_FIELD, reportId).getPosition());
        titleParams.put(PREPARED_BY_POSITION_FIELD, signatoryRepository.findSignatoryByRole(PREPARED_BY_ROLE_FIELD, reportId).getPosition());
        titleParams.put(NOTED_BY_POSITION_FIELD, signatoryRepository.findSignatoryByRole(NOTED_BY_ROLE_FIELD, reportId).getPosition());


        JasperPrint totalInventoryJasperPrint = JasperFillManager.fillReport(totalInventoryReport, titleParams, totalInventoryDatasource);

        JasperExportManager.exportReportToPdfFile(totalInventoryJasperPrint, report.getReport_location());
        saveReport();

        Prompt.success("Done saving report to " + report.getReport_location());

        this.report = null;
    }

    private void createInventoryReport(List<Map> purchaseOrderAndHeaderList, List<Map> salesAndFooterList) throws JRException {
        JasperReport purchaseOrderJasperReport = JasperCompileManager.compileReport(FILE_UPPER_PART.getAbsolutePath());
        JasperReport salesJasperReport = JasperCompileManager.compileReport(FILE_LOWER_PART.getAbsolutePath());

        JRBeanCollectionDataSource purchaseOrderDatasource = new JRBeanCollectionDataSource(purchaseOrderAndHeaderList);
        JRBeanCollectionDataSource saleDatasource = new JRBeanCollectionDataSource(salesAndFooterList);

        Map<String, Object> titleParams = new HashMap<>();
        String beginningBalance = computeBeginningBalanceInventory();
        titleParams.put(COMPANY_NAME_FIELD, getConfigValue(companyName));
        titleParams.put(STREET_ADDRESS_FIELD, getConfigValue(companyAddress));
        titleParams.put(REPORT_NAME_FIELD, selectedReport);
        titleParams.put(SELECTED_MONTH_FIELD, selectedMonth);
        titleParams.put(SELECTED_YEAR_FIELD, selectedYear);
        totalMaterialsForUse = purchaseAmount + Double.parseDouble(beginningBalance);
        titleParams.put(BEGINNING_BALANCE_INVENTORY_FIELD, beginningBalance);
        titleParams.put(TOTAL_COST_FIELD, String.valueOf(totalMaterialsForUse));
        double totalEndingBalance = totalMaterialsForUse - totalMaterialsIssued;
        titleParams.put(TOTAL_ENDING_BALANCE_FIELD, String.valueOf(totalEndingBalance));

        purchaseAmount = 0;
        totalMaterialsForUse = 0;
        totalMaterialsIssued = 0;


        this.setReportPreparedBy(signatoryRepository.findSignatoryByRole(PREPARED_BY_ROLE_FIELD, reportId).getSignatory());
        this.setReportCheckedBy(signatoryRepository.findSignatoryByRole(CHECKED_BY_ROLE_FIELD, reportId).getSignatory());
        this.setReportNotedBy(signatoryRepository.findSignatoryByRole(NOTED_BY_ROLE_FIELD, reportId).getSignatory());

        this.report.setPrepared_by(this.getReportPreparedBy());
        this.report.setChecked_by(this.getReportCheckedBy());
        this.report.setNoted_by(this.getReportNotedBy());

        titleParams.put(PREPARED_BY_FIELD, reportPreparedBy);
        titleParams.put(NOTED_BY_FIELD, reportNotedBy);
        titleParams.put(CHECKED_BY_FIELD, reportCheckedBy);
        titleParams.put(CHECKED_BY_POSITION_FIELD, signatoryRepository.findSignatoryByRole(CHECKED_BY_ROLE_FIELD, reportId).getPosition());
        titleParams.put(PREPARED_BY_POSITION_FIELD, signatoryRepository.findSignatoryByRole(PREPARED_BY_ROLE_FIELD, reportId).getPosition());
        titleParams.put(NOTED_BY_POSITION_FIELD, signatoryRepository.findSignatoryByRole(NOTED_BY_ROLE_FIELD, reportId).getPosition());


        JasperPrint purchaseOrderJasperPrint = JasperFillManager.fillReport(purchaseOrderJasperReport, titleParams, purchaseOrderDatasource);
        JasperPrint salesJasperPrint = JasperFillManager.fillReport(salesJasperReport, titleParams, saleDatasource);

        JasperExportManager.exportReportToPdfFile(purchaseOrderJasperPrint, report.getReport_location());
        String purchaseOrderReportLocation = report.getReport_location();
        saveReport();
        this.report = null;
        setReportMetadata(report);
        String salesReportLocation = report.getReport_location();
        JasperExportManager.exportReportToPdfFile(salesJasperPrint, report.getReport_location());
        saveReport();
        Prompt.success("Done saving report to " + purchaseOrderReportLocation + " and " + salesReportLocation);
        purchaseOrderReportLocation = null;
        salesReportLocation = null;
    }

    private void setReportMetadata(Reports reports) {
        if (reports == null) {
            reports = new Reports();
        }
        reports.setReport_name(selectedMonth + "_" + selectedYear + "_" + selectedReport + "_"+ UUID.randomUUID() + ".pdf");
                reports.setMonth_of_report(selectedMonth);
        reports.setReport_location(getSystemLocationOfReport() + reports.getReport_name());
        reports.setYear_of_report(selectedYear);
        reports.setDay_of_report(Integer.toString(LocalDateTime.now().getDayOfMonth()));
        reports.setChecked_by(this.getReportCheckedBy());
        reports.setNoted_by(this.getReportNotedBy());
        reports.setPrepared_by(this.getReportPreparedBy());
        this.report = reports;
    }

    private String getSystemLocationOfReport() {
        return getSystemReportLocation();
    }

    private void createReport() throws JRException {
        JasperReport jasperReport = JasperCompileManager.compileReport(FILE_UPPER_PART.getAbsolutePath());
        List<Map> mappedReportsList = reportsMapper.map();
        JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(mappedReportsList);
        JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, null, dataSource);
        JasperExportManager.exportReportToPdfFile(jasperPrint, report.getReport_location());
        saveReport();
        Prompt.success("Done saving report to " + report.getReport_location());
    }

    private void saveReport() {
        Reports reports = reportRepository.save(report);
    }
}
