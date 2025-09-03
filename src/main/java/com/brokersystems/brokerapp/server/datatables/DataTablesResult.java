package com.brokersystems.brokerapp.server.datatables;

import com.brokersystems.brokerapp.updatepolno.dto.UpdateClientPolNoDTO;
import org.springframework.data.domain.Page;

import java.util.Collection;
import java.util.List;


public class DataTablesResult<T> {

    private Integer draw;

    private Long recordsTotal;

    private Long recordsFiltered;

    private Collection<T> data;

    public DataTablesResult(List<UpdateClientPolNoDTO> clientPolNoDTOS) {
    }

    public DataTablesResult(Integer draw, Long recordsTotal, Collection<T> data) {
        this.draw = draw;
        this.recordsTotal = recordsTotal;
        this.recordsFiltered = recordsTotal;
        this.data = data;
    }

    public DataTablesResult(DataTablesRequest request, Page<T> page) {
        this.recordsTotal = page.getTotalElements();
        this.recordsFiltered = page.getTotalElements();
        this.data = page.getContent();
        this.draw = request.getDraw();
    }

    public DataTablesResult(DataTablesRequest request, Collection<T> data) {
        this.recordsTotal = Long.valueOf(data.size());
        this.recordsFiltered = Long.valueOf(data.size());
        this.data = data;
        this.draw = request.getDraw();
    }

    public Integer getDraw() {
        return draw;
    }

    public void setDraw(Integer draw) {
        this.draw = draw;
    }

    public Long getRecordsTotal() {
        return recordsTotal;
    }

    public void setRecordsTotal(Long recordsTotal) {
        this.recordsTotal = recordsTotal;
    }

    public Long getRecordsFiltered() {
        return recordsFiltered;
    }

    public void setRecordsFiltered(Long recordsFiltered) {
        this.recordsFiltered = recordsFiltered;
    }

    public Collection<T> getData() {
        return data;
    }

    public void setData(Collection<T> data) {
        this.data = data;
    }
}
