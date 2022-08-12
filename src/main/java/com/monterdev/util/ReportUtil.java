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
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.ObjectUtils;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.monterdev.configuration.GlobalConfiguration.*;
import static com.monterdev.constants.DateConstants.*;
import static com.monterdev.constants.InventoryTypeConstants.ALL_CATEGORIES;
import static com.monterdev.constants.InventoryTypeConstants.SUMMARY;
import static com.monterdev.constants.ReportFieldsConstant.*;


@Getter
@Setter
public class ReportUtil {

    private String selectedMonth;

    private String selectedYear;

    private String selectedReport;

    private String selectedReportRisTypeCode;

    private String inventoryType;

    private int reportId;

    private double purchaseAmount = 0;

    private double totalMaterialsForUse = 0;

    private double totalMaterialsIssued = 0;

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

    private InputStream FILE_UPPER_PART = null;

    private InputStream FILE_LOWER_PART = null;

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
            if (getSelectedReportRisTypeCode().equalsIgnoreCase(SUMMARY)) {
                setBalanceCategory(risTypeNamesRepository.findRisNameByType(getSelectedReportRisTypeCode()).getInventorytype());
                createSummaryReport();
            } else if (getSelectedReportRisTypeCode().equalsIgnoreCase(ALL_CATEGORIES)) {
                createTotalInventoryReport();
            } else {
                createReport();
            }

        } catch (JRException e) {
            System.out.println(e);
        }

    }

    private void createSummaryReport() {

        List<Map> purchaseOrderAndHeaderList = new ArrayList<>();
        List<Map> salesAndFooterList = new ArrayList<>();

        getAllPurchaseIssuedForThisMonth(purchaseOrderAndHeaderList);
        getAllMaterialsIssuedForThisMonth(salesAndFooterList);

        try {
            createInventoryReport(purchaseOrderAndHeaderList, salesAndFooterList);
        } catch (JRException e) {
            e.printStackTrace();
        }
    }

    private void getAllPurchaseIssuedForThisMonth(List<Map> mapList) {

        String convertedMonth = convertMonth();
        String randomDate = selectedYear + "-" + convertedMonth + staticDateString;
        List<PurchaseOrder> purchaseOrders = purchaseOrderRepository.findPurchaseOrderByItemCategory(randomDate, getInventoryType());

        if (ObjectUtils.isEmpty(purchaseOrders)) {
            PurchaseOrder purchaseOrder = new PurchaseOrder();
            purchaseOrder.setItem_category("empty");
            purchaseOrder.setPurchase_cost(0);
            purchaseOrder.setId(0);
            purchaseOrder.setQuantity(0);
            purchaseOrder.setItem_name("empty");
            purchaseOrder.setSku(0);
            purchaseOrder.setAmount(0);
            purchaseOrder.setStock_before(0);
            purchaseOrder.setDatecreated(AppTime.now());
            purchaseOrder.setIn_stock(0);
            purchaseOrders.add(purchaseOrder);
        }
        setPurchaseOrderList(mapList, purchaseOrders);

    }

    private void setPurchaseOrderList(List<Map> mapList, List<PurchaseOrder> purchaseOrders) {
        purchaseOrders.stream().forEach(po -> {
            String purchaseOrderNumber = supplierRepository.getPurchaseOrderNumberBySkuAndDate(po.getSku(), po.getDatecreated());
            if (ObjectUtils.isEmpty(purchaseOrderNumber)) {
                purchaseOrderNumber = "empty";
            }
            Map<String, Object> objectMap = new HashMap<>();
            objectMap.put("PURCHASE_LIST", po.getItem_name());
            objectMap.put("SALES_INVOICE_NUMBER", purchaseOrderNumber);
            objectMap.put("PURCHASED_AMOUNT", String.format("%d", (long) po.getAmount()));

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


    private void getAllMaterialsIssuedForThisMonth(List<Map> mapList) {
        String randomDate = "01" + "-" + selectedMonth + "-" + selectedYear + "  01:01:01";
        // List<String> risTypesList = risRepository.findAllRISTypeByCurrentMonthOfDateSpecifiedAndInventoryType(randomDate, getInventoryType());
        List<String> risTypesList = risRepository.findAllRISTypeByInventoryType(getInventoryType());
        setRequisitionIssueSliplist(mapList, randomDate, risTypesList);
    }

    private void setRequisitionIssueSliplist(List<Map> mapList, String randomDate, List<String> risTypesList) {
        risTypesList.stream().forEach(ris -> {
            String totalCostPerRisType = salesRepository.computeTotalCostPerRISType(ris, randomDate);
            if (!ObjectUtils.isEmpty(totalCostPerRisType)) {
                Map<String, Object> objectMap = new HashMap<>();
                objectMap.put("RIS_TYPE_NAMES_LIST", ris);
                objectMap.put("TOTAL_COST_OF_RIS", totalCostPerRisType);
                mapList.add(objectMap);
                totalMaterialsIssued += DataUtil.formatDouble(totalCostPerRisType);
            } else {
                Map<String, Object> objectMap = new HashMap<>();
                objectMap.put("RIS_TYPE_NAMES_LIST", ris);
                objectMap.put("TOTAL_COST_OF_RIS", "0");
                mapList.add(objectMap);
                totalMaterialsIssued = 0;
            }


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
        if (!getInventoryType().equalsIgnoreCase(ALL_CATEGORIES) || !getInventoryType().equalsIgnoreCase(SUMMARY)) {
            result = balanceRepository.getBeginningBalanceInventoryByCategory(getInventoryType(), previousMonth, year);
        } else {
            result = balanceRepository.getTotalBeginningBalanceForThisMonth(previousMonth, year, getBalanceCategory());
        }

        return String.format("%d", (long) result);
    }

    private void createTotalInventoryReport() throws JRException {
        JasperReport totalInventoryReport = JasperCompileManager.compileReport(FILE_UPPER_PART);
        List<Balance> totalBalanceListForThisMonth = balanceRepository.getTotalBalancesForThisMonth(selectedMonth, selectedYear);
        List<Map> inventoryListAndEndingBalances = new ArrayList<>();
        totalBalanceListForThisMonth.stream().forEach(e -> {
            Map<String, Object> map = new HashMap<>();
            map.put("INVENTORY_NAME", e.getCategory());
            map.put("INVENTORY_END_BALANCE", String.format("%d", (long) e.getEndbalance()));
            inventoryListAndEndingBalances.add(map);
        });
        JRBeanCollectionDataSource totalInventoryDatasource = new JRBeanCollectionDataSource(inventoryListAndEndingBalances);

        Map<String, Object> titleParams = new HashMap<>();
        titleParams.put(COMPANY_NAME_FIELD, getConfigValue(companyName));
        titleParams.put(STREET_ADDRESS_FIELD, getConfigValue(companyAddress));
        titleParams.put(REPORT_NAME_FIELD, selectedReport);
        titleParams.put(SELECTED_MONTH_FIELD, selectedMonth);
        titleParams.put(SELECTED_YEAR_FIELD, selectedYear);


        double totalEndingBalance = totalBalanceListForThisMonth.stream().mapToDouble(Balance::getEndbalance).sum();
        titleParams.put(TOTAL_COST_FIELD, String.format("%d", (long) totalEndingBalance));


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
        JasperReport purchaseOrderJasperReport = JasperCompileManager.compileReport(FILE_UPPER_PART);
        //JasperReport salesJasperReport = JasperCompileManager.compileReport(FILE_LOWER_PART);

        List<Map> combinedPurchaseOrderAndSalesOrders = Stream.of(purchaseOrderAndHeaderList, salesAndFooterList)
                .flatMap(Collection::stream)
                .collect(Collectors.toList());

        combinedPurchaseOrderAndSalesOrders.stream().forEach(data->{

            System.out.println(data);
            System.out.println("**********************");
        });

        JRBeanCollectionDataSource purchaseOrderDatasource = new JRBeanCollectionDataSource(combinedPurchaseOrderAndSalesOrders);
        JRBeanCollectionDataSource saleDatasource = new JRBeanCollectionDataSource(salesAndFooterList);

        Map<String, Object> titleParams = new HashMap<>();
        String beginningBalance = computeBeginningBalanceInventory();
        titleParams.put(COMPANY_NAME_FIELD, getConfigValue(companyName));
        titleParams.put(STREET_ADDRESS_FIELD, getConfigValue(companyAddress));
        titleParams.put(REPORT_NAME_FIELD, selectedReport);
        titleParams.put(SELECTED_MONTH_FIELD, selectedMonth);
        titleParams.put(SELECTED_YEAR_FIELD, selectedYear);
        totalMaterialsForUse = purchaseAmount + DataUtil.formatDouble(beginningBalance);
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
        //JasperPrint salesJasperPrint = JasperFillManager.fillReport(salesJasperReport, titleParams, saleDatasource);

        // new JasperViewerFX().viewReport("Test ",purchaseOrderJasperPrint);
        JasperExportManager.exportReportToPdfFile(purchaseOrderJasperPrint, report.getReport_location());
        String purchaseOrderReportLocation = report.getReport_location();
        saveReport();
        this.report = null;
        setReportMetadata(report);
        String salesReportLocation = report.getReport_location();
        //JasperExportManager.exportReportToPdfFile(salesJasperPrint, report.getReport_location());
        //saveReport();
        //Prompt.success("Done saving report to " + purchaseOrderReportLocation );
        purchaseOrderReportLocation = null;
        salesReportLocation = null;
    }

    private void singleInventoryReport(List<Map> purchaseOrderAndHeaderList) throws JRException {
        JasperReport purchaseOrderJasperReport = JasperCompileManager.compileReport(FILE_UPPER_PART);

        JRBeanCollectionDataSource purchaseOrderDatasource = new JRBeanCollectionDataSource(purchaseOrderAndHeaderList);

        Map<String, Object> titleParams = new HashMap<>();
        String beginningBalance = computeBeginningBalanceInventory();
        titleParams.put(COMPANY_NAME_FIELD, getConfigValue(companyName));
        titleParams.put(STREET_ADDRESS_FIELD, getConfigValue(companyAddress));
        titleParams.put(REPORT_NAME_FIELD, selectedReport);
        titleParams.put(SELECTED_MONTH_FIELD, selectedMonth);
        titleParams.put(SELECTED_YEAR_FIELD, selectedYear);
        totalMaterialsForUse = purchaseAmount + DataUtil.formatDouble(beginningBalance);
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

        JasperExportManager.exportReportToPdfFile(purchaseOrderJasperPrint, report.getReport_location());
        String purchaseOrderReportLocation = report.getReport_location();
        saveReport();
        this.report = null;
        setReportMetadata(report);

        purchaseOrderReportLocation = null;

    }

    private void setReportMetadata(Reports reports) {
        if (reports == null) {
            reports = new Reports();
        }
        reports.setReport_name(selectedMonth + "_" + selectedYear + "_" + selectedReport + "_" + UUID.randomUUID() + ".pdf");
        reports.setMonth_of_report(selectedMonth);
        reports.setReport_location(FileUtils.getUserDirectoryPath() + "\\" + getSystemLocationOfReport() + reports.getReport_name());
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

    private void createReport() throws JRException, FileNotFoundException {
        JasperReport jasperReport = JasperCompileManager.compileReport(FILE_UPPER_PART);
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
