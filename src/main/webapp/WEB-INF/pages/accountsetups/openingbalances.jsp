<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<script type="text/javascript" src="<c:url value="/js/modules/utils/select2builder.js"/>"></script>
<div class="x_panel" id="acct_model">
    <div class="x_title">
        <h2><i class="fa fa-bars"></i> Account Opening Balances</h2>
        <div class="clearfix"></div>
    </div>
<%--    <form class="form-horizontal">--%>
<%--        <div class="item form-group">--%>
<%--            <div class="col-md-6">--%>
<%--                <label for="brn-id" class="label-align col-md-3">--%>
<%--                    Select Period </label>--%>
<%--                <div class="col-md-6">--%>
<%--                    <div id="acct-period-lov" class="form-control"--%>
<%--                         select2-url="<c:url value="/protected/accounts/selAccountingPeriods"/>" >--%>

<%--                    </div>--%>
<%--                </div>--%>
<%--            </div>--%>
<%--            <div class="col-md-6">--%>

<%--            </div>--%>
<%--        </div>--%>
<%--    </form>--%>
    <hr>
    <table id="openingBalanceTbl" class="table table-striped" style="width:100%">
        <thead>
        <tr>
            <th>Account No</th>
            <th>Account Name</th>
            <th>Opening Balance</th>
            <th>Balance</th>
        </tr>
        </thead>
    </table>
</div>


<script>

    $(function() {

        $(document).ready(function () {

            ACCOUNTS.createOpeningBalances();

        });
    });

    var ACCOUNTS = ACCOUNTS || {};


    ACCOUNTS.createOpeningBalances= function(){
        var url = "openingbalances";
        return $('#openingBalanceTbl').DataTable({
            "processing": true,
            "serverSide": true,
            scrollCollapse: true,
            scrollY: '400px',
            "ajax": {
                'url': url,
            },
            lengthMenu: [[20, 30, 40], [20, 30, 40]],
            pageLength: 20,
            destroy: true,
            "columns": [
                {"data": "accountNo"},
                {"data": "accountName"},
                {
                    "data": "balance",
                    "render": function (data, type, full, meta) {

                        return UTILITIES.currencyFormat(full.balance);
                    }
                },
                {
                    "data": "currBalance",
                    "render": function (data, type, full, meta) {

                        return UTILITIES.currencyFormat(full.currBalance);
                    }
                }

            ]
        });
    }

</script>