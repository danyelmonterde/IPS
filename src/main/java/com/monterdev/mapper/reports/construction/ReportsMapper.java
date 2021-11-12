package com.monterdev.mapper.reports.construction;

import com.monterdev.model.RequisitionIssueSlip;
import com.monterdev.model.RisType;
import com.monterdev.model.RisTypeFields;
import com.monterdev.model.Sales;
import com.monterdev.model.reports.construction.SaleOfMaterials;
import com.monterdev.repository.RisRepository;
import com.monterdev.repository.RisTypeFieldsRepository;
import com.monterdev.repository.RisTypeRepository;
import com.monterdev.repository.SalesRepository;
import com.monterdev.util.ReportUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.*;

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

    public List<Map> map(){
        List<Map> mappedReportsList = new ArrayList<>();
        String selectedMonth = reportUtil.getSelectedMonth();
        String selectedYear = reportUtil.getSelectedYear();
        String randomDate = "01";
        String selectedDate = randomDate+"-"+selectedMonth+"-"+selectedYear+"  01:01:01";
        List<RequisitionIssueSlip> requisitionIssueSlipListForTheSpecifiedMonth =
                risRepository.findAllRequisitionSlipByCurrentMonthOfTheSpecifiedDate(selectedDate);



        requisitionIssueSlipListForTheSpecifiedMonth.stream().forEach(s->{
            Map<String,Object> mappedReport = new HashMap<>();

            List<RisTypeFields> risTypeFieldsList = risTypeFieldsRepository.findByControlNumber(s.getControl_number());
            List<RisType> risTypeList = risTypeRepository.findByRisType(reportUtil.getSelectedReportRisTypeCode());

            if(!ObjectUtils.isEmpty(risTypeFieldsList) && !ObjectUtils.isEmpty(risTypeList)){
                //ADDITIONAL FIELDS
                risTypeList.stream().forEach(r1 -> {
                    risTypeFieldsList.stream().forEach(r2 ->{
                        if(r1.getRisfield().equalsIgnoreCase(r2.getRis_field())){
                            mappedReport.put(r1.getRisfield(),r2.getRis_value());
                        }
                    });
                });
            }
            List<Sales> salesList = salesRepository.findByControlNumber(s.getControl_number());
            Optional<Sales> optionalSales = salesList.stream().filter(e->e.getControl_number().equalsIgnoreCase(s.getControl_number())).findFirst();
            Sales sales = optionalSales.isPresent() ? optionalSales.get():null;
            if(!ObjectUtils.isEmpty(sales)){
                //FIXED COLUMNS
                mappedReport.put("SALES",sales.getTotal_sales());
                mappedReport.put("COST",sales.getTotal_cost());
                mappedReport.put("INCOME", sales.getIncome());
                mappedReport.put("DATE",sales.getDate_transacted());
            }
            mappedReportsList.add(mappedReport);
        });
        return mappedReportsList;
    }
}
