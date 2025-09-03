<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<script type="text/javascript"
        src="<c:url value="/js/modules/utils/select2builder.js"/>"></script>
<div class="x_panel">
    <div class="x_title">
        <h2>Contracts</h2>
        <ul class="nav navbar-right panel_toolbox">
            <li><a class="collapse-link"><i class="fa fa-chevron-up"></i></a>
            </li>
        </ul>
        <div class="clearfix"></div>
    </div>
    <div class="x_content">
        <div class="col-md-12 col-xs-12 table-responsive">
            <button type="button" class="btn btn-info" id="btn-add-contract">New</button>
            <hr>
            <table id="binder-tbl" class="table table-striped" style="width: 100%">
                <thead>
                <tr class="headings">

                    <th>Contract Name</th>
                    <th>Currency</th>
                    <th>Intermediary</th>
                    <th>Intermediary Type</th>
                    <th>Status</th>
                    <th>Auth Status</th>
                </tr>
                </thead>
            </table>

        </div>
    </div>
</div>
<script>
    $(function(){

        $(document).ready(function() {
            getBinders();

        });

    });

    function getBinders(){

        var url = SERVLET_CONTEXT+"/protected/setups/contracts";
        var currTable = $('#binder-tbl')
            .DataTable(
                {
                    "processing": true,
                    "serverSide": true,
                    "ajax": url,
                    lengthMenu: [[20, 30, 40, 50], [20, 30, 40, 50]],
                    pageLength: 20,
                    "scrollX": true,
                    "autoWidth": false,
                    destroy: true,
                    "columns": [

                        {
                            "data": "contractName"
                        },
                        {
                            "data": "currency",
                        },
                        {
                            "data": "intermediary",
                        },
                        {
                            "data": "intermediaryType",
                        },
                        {
                            "data": "status",
                        },
                        {
                            "data": "authStatus",
                        }
                    ]
                });
        return currTable;
    }

</script>