package com.brokersystems.brokerapp.schedules.service.impl;

import com.brokersystems.brokerapp.schedules.model.QScheduleMapping;
import com.brokersystems.brokerapp.schedules.model.ScheduleBean;
import com.brokersystems.brokerapp.schedules.model.ScheduleColMapping;
import com.brokersystems.brokerapp.schedules.model.ScheduleMapping;
import com.brokersystems.brokerapp.schedules.repository.ScheduleMappingRepo;
import com.brokersystems.brokerapp.schedules.service.ScheduleService;
import com.brokersystems.brokerapp.server.datatables.DataTablesRequest;
import com.brokersystems.brokerapp.server.datatables.DataTablesResult;
import com.brokersystems.brokerapp.server.exception.BadRequestException;
import com.brokersystems.brokerapp.setup.model.PremRatesDef;
import com.brokersystems.brokerapp.setup.model.QPremRatesDef;
import com.brokersystems.brokerapp.setup.model.SectionsDef;
import com.brokersystems.brokerapp.setup.model.SubClassDef;
import com.brokersystems.brokerapp.setup.repository.SectionRepo;
import com.brokersystems.brokerapp.uw.model.RiskTrans;
import com.brokersystems.brokerapp.uw.repository.RiskTransRepo;
import com.mysema.query.types.expr.BooleanExpression;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by peter on 2/20/2017.
 */
@Service
public class ScheduleServiceImpl implements ScheduleService{

    @Autowired
    private ScheduleMappingRepo mappingRepo;

    @Autowired
    private RiskTransRepo riskTransRepo;

    @Autowired
    private SectionRepo sectionRepo;

    @Override
    @Transactional(readOnly = true)
    public DataTablesResult<ScheduleMapping> findSubclassMapping(DataTablesRequest request, Long subCode) throws IllegalAccessException {
        BooleanExpression pred = QScheduleMapping.scheduleMapping.subclass.subId.eq(subCode);
        Page<ScheduleMapping> page = mappingRepo.findAll(pred.and(request.searchPredicate(QScheduleMapping.scheduleMapping)), request);
        return new DataTablesResult<>(request, page);
    }

    @Override
    @Transactional(readOnly = false)
    public void createScheduleMapping(ScheduleMapping scheduleMapping) throws BadRequestException {
        Long count = mappingRepo.count(QScheduleMapping.scheduleMapping.subclass.subId.eq(scheduleMapping.getSubclass().getSubId()))+1;
        if(scheduleMapping.getSmId()==null)
        scheduleMapping.setColumnIndex(count+"");
        if(scheduleMapping.getPremItem()!=null && "on".equalsIgnoreCase(scheduleMapping.getPremItem())){
            scheduleMapping.setPremItem("Y");
            if(scheduleMapping.getSections()==null)
                throw new BadRequestException("Section is Mandatory if Prem Item is checked...");
        }
        else scheduleMapping.setPremItem("N");
        mappingRepo.save(scheduleMapping);

    }

    @Override
    @Transactional(readOnly = false)
    public void deleteScheduleMapping(Long schedId) {
        SubClassDef subClassDef = mappingRepo.findOne(schedId).getSubclass();
        mappingRepo.delete(schedId);
        Iterable<ScheduleMapping> mappings = mappingRepo.findAll(QScheduleMapping.scheduleMapping.subclass.subId.eq(subClassDef.getSubId()));
        List<ScheduleMapping> schedMappings = new ArrayList<>();
        int index = 1;
        for(ScheduleMapping mapping:mappings){
            mapping.setColumnIndex(index+"");
            schedMappings.add(mapping);
            index++;
        }
        mappingRepo.save(schedMappings);
    }

    @Override
    public Page<SectionsDef> findSectionsForSel(String term, Pageable pageable, Long subId) {
        return sectionRepo.getSubclassSections(subId, term, pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public ScheduleBean generateScheduleColumns(Long riskId) throws BadRequestException {
        Long subId = riskTransRepo.getSubclassCode(riskId);
        Long colCount = mappingRepo.count(QScheduleMapping.scheduleMapping.subclass.subId.eq(subId));
        if(colCount==0) return new ScheduleBean();
        ScheduleBean scheduleBean = new ScheduleBean();
        scheduleBean.setColCount(colCount);
        Iterable<ScheduleMapping> mappings =mappingRepo.findAll(QScheduleMapping.scheduleMapping.subclass.subId.eq(subId));
        List<ScheduleColMapping> colMappings = new ArrayList<>();
        for(ScheduleMapping mapping:mappings){
            colMappings.add(new ScheduleColMapping(mapping.getColumnIndex(),mapping.getColumnName(),mapping.getColumnType()
                    ,mapping.getOptions(),mapping.getPremItem(),(mapping.getSections()!=null)?mapping.getSections().getId():null));
        }
        colMappings.sort((ScheduleColMapping a,ScheduleColMapping b)-> {
             return Integer.parseInt(a.getKey())-Integer.parseInt(b.getKey());
        });
        scheduleBean.setMappings(colMappings);
        return scheduleBean;
    }
}
