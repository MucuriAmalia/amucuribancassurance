<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<script type="text/javascript" src="<c:url value="/js/modules/uwtrans/adminfee.js"/>"></script>
<sec:authorize access="hasAnyAuthority('ACCESS_NEW_BUSINESS')" var="canAccessNB"></sec:authorize>
<script type="text/javascript">
    var canAccess = ${canAccessNB};

</script>
<div class="x_panel">
    <div class="x_title">
        <h2><i class="fa fa-bars"></i> Admin Fee Transactions</h2>
        <div class="clearfix"></div>
    </div>
    <form>
        <label class="radio-inline">
            <input type="radio" name="optradio" value="U">UnAuthorised Trans
        </label>
        <label class="radio-inline">
            <input type="radio" name="optradio" value="A">Authorised Trans
        </label>
    </form>
    <sec:authorize access="hasAnyAuthority('ACCESS_NEW_BUSINESS')">
        <a href="<c:url value='/protected/trans/adminfee/createAdminFee'/> " class="btn btn-info float-right">New</a>
    </sec:authorize>
    <div class="cutom-container">
    <table id="admin_fee_tbl" class="table table-striped" style="width:100%">
        <thead>
        <tr>
            <th>Ref No</th>
            <th>Client</th>
            <th>Currency</th>
            <th>Gross Amount</th>
            <th>VAT Amount</th>
            <th>Excise Duty</th>
            <th>Net AMount</th>
            <th>Prep. By</th>
            <th>Date Prepared</th>
            <th>Status</th>
            <th>Paid Status</th>
            <th width="5%"></th>
        </tr>
        </thead>
    </table>
        </div>
</div>
