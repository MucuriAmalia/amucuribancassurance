<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<script type="text/javascript" src="<c:url value="/js/modules/utils/select2builder.js"/>"></script>
<script type="text/javascript" src="<c:url value="/libs/rivets/rivets.js"/>"></script>
<script type="text/javascript" src="<c:url value="/js/modules/claims/parenquiry.js"/>"></script>
<div class="x_panel">
    <div class="x_title">
        <h4>Pre-Auth/Medical Claims Trans Enquiry</h4>
    </div>
    <div class="card-box table-responsive">
    <table id="med_clm_enquiry_tbl" class="table table-striped" style="width: 100%">
        <thead>
        <tr class="headings">
            <th>Trans No</th>
            <th>Trans Revision No</th>
            <th>Event Date</th>
            <th>Member Name</th>
            <th>Notification Date</th>
            <th>Status</th>
            <th>Booked By</th>
            <th>Transaction Type</th>
            <th width="5%"></th>
        </tr>
        </thead>
    </table>
        </div>
</div>