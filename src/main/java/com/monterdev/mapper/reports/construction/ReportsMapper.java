package com.monterdev.mapper.reports.construction;

import com.monterdev.model.RequisitionIssueSlip;
import com.monterdev.model.RisType;
import com.monterdev.model.RisTypeFields;
import com.monterdev.model.Sales;
import com.monterdev.repository.*;
import com.monterdev.util.AppTime;
import com.monterdev.util.ReportUtil;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import java.util.*;

import static com.monterdev.configuration.GlobalConfiguration.*;
import static com.monterdev.constants.InventoryTypeConstants.SUMMARY;

@Component
public class ReportsMapper {

    @Autowired
    private RisRepository risRepository;

    @Autowired
    private RisTypeFieldsRepository risTypeFieldsRepository;

    @Autowired
    private RisTypeRepository risTypeRepository;

    @Autowired
    private SalesRepository salesRepository;

    @Autowired
    private ReportUtil reportUtil;

    @Autowired
    private ReportNamesRepository reportNamesRepository;

    @Autowired
    private SignatoryRepository signatoryRepository;

    private double totalSales;

    private double totalCost;

    private double totalIncome;

    @Autowired
    private ConfigurableApplicationContext applicationContext;

    public List<Map> map() {

        totalSales = 0.0;
        totalCost = 0.0;
        totalIncome = 0.0;
        List<Map> mappedReportsList = new ArrayList<>();
        String selectedMonth = reportUtil.getSelectedMonth();
        String selectedYear = reportUtil.getSelectedYear();
        String randomDate = "01";
        String selectedDate = randomDate + "-" + selectedMonth + "-" + selectedYear + "  01:01:01";
        List<RequisitionIssueSlip> requisitionIssueSlipListForTheSpecifiedMonth = new ArrayList<>();
        try {
            if (reportUtil.getSelectedReportRisTypeCode() != SUMMARY) {
                requisitionIssueSlipListForTheSpecifiedMonth = risRepository.findAllRequisitionSlipByCurrentMonthOfTheSpecifiedDate(selectedDate, reportUtil.getSelectedReportRisTypeCode());
            } else {

            }

        } catch (Exception e) {
            System.out.println(e);
        }


        requisitionIssueSlipListForTheSpecifiedMonth.stream().forEach(s -> {
            Map<String, Object> mappedReport = new HashMap<>();

            List<RisTypeFields> risTypeFieldsList = risTypeFieldsRepository.findByControlNumber(s.getControl_number());
            List<RisType> risTypeList = risTypeRepository.findByRisType(reportUtil.getSelectedReportRisTypeCode());

            if (!ObjectUtils.isEmpty(risTypeFieldsList) && !ObjectUtils.isEmpty(risTypeList)) {
                //ADDITIONAL FIELDS
                risTypeList.stream().forEach(r1 -> {
                    risTypeFieldsList.stream().forEach(r2 -> {
                        if (r1.getRisfield().equalsIgnoreCase(r2.getRis_field())) {
                            mappedReport.put(r1.getRisfield(), r2.getRis_value());
                        }
                    });
                });
            }
            List<Sales> salesList = salesRepository.findByControlNumber(s.getControl_number());
            Optional<Sales> optionalSales = salesList.stream().filter(e -> e.getControl_number().equalsIgnoreCase(s.getControl_number())).findFirst();
            Sales sales = optionalSales.isPresent() ? optionalSales.get() : null;
            if (!ObjectUtils.isEmpty(sales)) {
                totalSales += sales.getTotal_sales();
                totalCost += sales.getTotal_cost();
                totalIncome += sales.getIncome();
                mapSales(selectedMonth, selectedYear, mappedReport, sales);
            }

            mappedReportsList.add(mappedReport);

        });
        if (ObjectUtils.isEmpty(requisitionIssueSlipListForTheSpecifiedMonth)) {
            //Empty list because no transactions
            Map<String, Object> mappedReport = new HashMap<>();
            List<String> risTypeFieldsList = risTypeRepository.findRisFieldsByRisType(reportUtil.getSelectedReportRisTypeCode());
            List<RisType> risTypeList = risTypeRepository.findByRisType(reportUtil.getSelectedReportRisTypeCode());

            if (!ObjectUtils.isEmpty(risTypeFieldsList) && !ObjectUtils.isEmpty(risTypeList)) {
                //ADDITIONAL FIELDS
                risTypeList.stream().forEach(r1 -> {
                    risTypeFieldsList.stream().forEach(r2 -> {
                        if (r1.getRisfield().equalsIgnoreCase(r2)) {
                            mappedReport.put(r1.getRisfield(), "empty");
                        }
                    });
                });
            }

            Sales sales = new Sales();
            sales.setTotal_cost(totalCost);
            sales.setIncome(totalIncome);
            sales.setControl_number("");
            sales.setTotal_sales(totalSales);
            sales.setDate_transacted(String.valueOf(AppTime.now()));
            sales.setId(0);
            mapSales(selectedMonth, selectedYear, mappedReport, sales);

            mappedReportsList.add(mappedReport);
        }


        return mappedReportsList;
    }

    private void mapSales(String selectedMonth, String selectedYear, Map<String, Object> mappedReport, Sales sales) {
        //FIXED COLUMNS
        mappedReport.put("COMPANY_NAME", getConfigValue(companyName));
        mappedReport.put("ADDRESS", getConfigValue(companyAddress));
        mappedReport.put("MONTH", selectedMonth + " " + selectedYear);
        mappedReport.put("DOCUMENT_TRACKING_NUMBER", RandomStringUtils.randomAlphanumeric(5));
        mappedReport.put("REPORT_NAME", reportUtil.getSelectedReport());
        mappedReport.put("REPORT_DESCRIPTION", getReportDescription(reportUtil.getSelectedReport()));
        mappedReport.put("SALES", sales.getTotal_sales());
        mappedReport.put("COST", sales.getTotal_cost());
        mappedReport.put("INCOME", sales.getIncome());
        mappedReport.put("DATE", sales.getDate_transacted());
        mappedReport.put("TOTAL_SALES", String.format("%d", (long) totalSales));
        mappedReport.put("TOTAL_COST", String.format("%d", (long) totalCost));
        mappedReport.put("TOTAL_INCOME", String.format("%d", (long) totalIncome));
        mappedReport.put("PREPARED_BY", signatoryRepository.findSignatoryByRole("preparedBy", reportUtil.getReportId()).getSignatory());
        mappedReport.put("NOTEDBY", signatoryRepository.findSignatoryByRole("notedBy", reportUtil.getReportId()).getSignatory());
        mappedReport.put("CHECKED_BY", signatoryRepository.findSignatoryByRole("checkedBy", reportUtil.getReportId()).getSignatory());
        mappedReport.put("CHECKED_BY_POSITION", signatoryRepository.findSignatoryByRole("checkedBy", reportUtil.getReportId()).getPosition());
        mappedReport.put("PREPARED_BY_POSITION", signatoryRepository.findSignatoryByRole("preparedBy", reportUtil.getReportId()).getPosition());
        mappedReport.put("NOTED_BY_POSITION", signatoryRepository.findSignatoryByRole("notedBy", reportUtil.getReportId()).getPosition());
    }

    private String getReportDescription(String reportName) {
        return reportNamesRepository.findReportDescription(reportName);
    }
}
