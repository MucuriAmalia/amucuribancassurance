package com.brokersystems.brokerapp.server.datatables;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.core.MethodParameter;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.support.WebArgumentResolver;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

public class DataTableRequestResolver implements HandlerMethodArgumentResolver {

    @Override
    public boolean supportsParameter(MethodParameter methodParameter) {
        DataTable tableParamAnnotation = methodParameter.getParameterAnnotation(DataTable.class);
        return tableParamAnnotation != null;
    }

    @Override
    public Object resolveArgument(MethodParameter methodParameter, ModelAndViewContainer modelAndViewContainer, NativeWebRequest nativeWebRequest, WebDataBinderFactory webDataBinderFactory) throws Exception {
        DataTable tableParamAnnotation = methodParameter.getParameterAnnotation(DataTable.class);
        if (tableParamAnnotation != null) {
            HttpServletRequest httpRequest = (HttpServletRequest) nativeWebRequest.getNativeRequest();

            Integer draw = Integer.parseInt(httpRequest.getParameter("draw"));
            Integer start = Integer.parseInt(httpRequest.getParameter("start"));
            Integer length = Integer.parseInt(httpRequest.getParameter("length"));
            Search search = new Search();
            search.setValue(httpRequest.getParameter("search[value]"));
            search.setRegex(Boolean.parseBoolean(httpRequest.getParameter("search[regex]")));

            int columnCount = countRecords(httpRequest);
            List<Column> columns = new ArrayList<Column>();
            List<Order> orders = new ArrayList<Order>();
            if (columnCount > 0) {
                for (int i = 0 ; i < columnCount; i++) {
                    String data = httpRequest.getParameter("columns[" + i + "][data]");
                    String name = httpRequest.getParameter("columns["+ i +"][name]");
                    Boolean searchable = Boolean.parseBoolean(httpRequest.getParameter("columns[" + i + "][searchable]"));
                    Boolean orderable = Boolean.parseBoolean(httpRequest.getParameter("columns[" + i + "][orderable]"));
                    Search columnSearch = new Search();
                    columnSearch.setValue(httpRequest.getParameter("columns[" + i + "][search][value]"));
                    columnSearch.setRegex(Boolean.parseBoolean(httpRequest.getParameter("columns[" + i + "][search][regex]")));

                    Column column = new Column();
                    column.setData(data);
                    column.setName(name);
                    column.setSearchable(searchable);
                    column.setOrderable(orderable);
                    column.setSearch(columnSearch);

                    columns.add(column);

                    String tempOrder = httpRequest.getParameter("order["+ i +"][column]");
                    if (tempOrder != null) {
                        Integer orderColumn = Integer.parseInt(httpRequest.getParameter("order[" + i + "][column]"));
                        String dir = httpRequest.getParameter("order[" + i + "][dir]");
                        Order order = new Order();
                        order.setColumn(orderColumn);
                        order.setDir(dir);
                        orders.add(order);
                    }
                }
            }

            List<Sort.Order> dataOrders = new ArrayList<Sort.Order>();
            for (Order order : orders) {
                dataOrders.add(new Sort.Order(order.getDir().toLowerCase().equals("asc") ? Sort.Direction.ASC : Sort.Direction.DESC, columns.get(order.getColumn()).getData()));
            }
            Sort dataSort = new Sort(dataOrders);

            final DataTablesRequest dataTablesRequest = new DataTablesRequest(start / length, length, dataSort);
            dataTablesRequest.setDraw(draw);
            dataTablesRequest.setSearch(search);
            dataTablesRequest.setColumns(columns);
            return dataTablesRequest;
        }
        return WebArgumentResolver.UNRESOLVED;
    }


    private Integer countRecords(HttpServletRequest httpRequest) {
        int count = 0;
        boolean hasRow = true;
        while (hasRow) {
            String temp = httpRequest.getParameter("columns[" + count + "][data]");
            if (temp != null) {
                count++;
            } else {
                hasRow = false;
            }
        }
        return count;
    }
}
