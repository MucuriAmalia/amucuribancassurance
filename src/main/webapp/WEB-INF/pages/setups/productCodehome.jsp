<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<script type="text/javascript" src="<c:url value="/js/modules/utils/select2builder.js"/>"></script>
<script type="text/javascript" src="<c:url value="/libs/rivets/rivets.js"/>"></script>
<div class="x_panel">
    <div class="x_title">
        <h2><i class="fa fa-bars"></i> Product Codes</h2>
        <ul class="nav navbar-right panel_toolbox">
            <li><a class="collapse-link"><i class="fa fa-chevron-up"></i></a>
            </li>
        </ul>
        <div class="clearfix"></div>
    </div>
    <div class="x_content">
        <div class="table-responsive">
            <table id="instrans-tbl" class="table table-striped" style="width:100%">
                <thead>
                <tr>
                    <th>SAP Product code</th>
                    <th>SAP Product Descriptipn</th>
                </tr>
                </thead>
            </table>
        </div>
    </div>
</div>

<script>

    function insuranceTrans() {
        var url = "allProductCodes";

        var currTable = $('#instrans-tbl').DataTable({
            "processing": true,
            "serverSide": true,
            autoWidth: true,
            "ajax": {
                'url': url,
                'dataSrc': 'data'
            },
            lengthMenu: [[10], [10]],
            pageLength: 10,
            destroy: true,
            "columns": [
                { "data": "productCode" },
                { "data": "productDescription" },
            ]
        });

        return currTable;
    }

    $(document).ready(function() {
        insuranceTrans();
    });

</script>
