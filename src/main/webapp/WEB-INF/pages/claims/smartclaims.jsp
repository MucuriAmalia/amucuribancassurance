<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<script type="text/javascript" src="<c:url value="/js/modules/utils/select2builder.js"/>"></script>
<script type="text/javascript" src="<c:url value="/libs/rivets/rivets.js"/>"></script>
<script type="text/javascript" src="<c:url value="/js/modules/claims/parenquiry.js"/>"></script>
<div class="x_panel">
    <div class="x_title">
        <h4>Smart Claims Processing</h4>
    </div>
    <div class="table-responsive">
    <table id="smart_clm_enquiry_tbl" class="table table-striped" style="width: 100%">
        <thead>
        <tr class="headings">
            <th>Claim No</th>
            <th>Smart Reference No</th>
            <th>Event Date</th>
            <th>Member Name</th>
            <th>Service Provider</th>
            <th>Status</th>
            <th width="5%"></th>
        </tr>
        </thead>
    </table>
        </div>
    <input type="button" class="btn btn-info float-left"
           value="Process" id="btn-save-clm">
</div>
