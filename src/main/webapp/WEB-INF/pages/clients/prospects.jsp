<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<script type="text/javascript" src="<c:url value="/js/modules/utils/select2builder.js"/>"></script>
<div class="x_panel">
    <div class="x_title">
        <h2><i class="fa fa-bars"></i> Prospects List</h2>
        <div class="clearfix"></div>
    </div>

    <button id="btn-add-prs" class="btn btn-info float-right">New</button>
    <div class="table-responsive">
    <table id="tenTbl" class="table table-striped" style="width: 100%">
        <thead>
        <tr class="headings">
            <th>Prospect ID</th>
            <th>Name</th>
            <th>Phone</th>
            <th>Prospect Type</th>
            <th>Date of Birth</th>
            <th>Status</th>
            <th>Category</th>
            <th>Branch</th>
<%--            <th>Sub Agent</th>--%>
            <th>Comment</th>
            <th>Created By</th>
            <th width="5%"></th>
            <th width="5%"></th>
        </tr>
        </thead>
    </table>
        </div>
</div>

<jsp:include page="../modals/prospectmodals.jsp"></jsp:include>
<script type="text/javascript" src="<c:url value="/js/modules/clients/prospectsHelper.js"/>"></script>

<script>
    /**
     *
     */

    $(function() {

        $(document).ready(function () {

            PROSPECTS_UTILITIES.createProspectList();
            PROSPECTS_UTILITIES.addProspects("P");
            PROSPECTS_UTILITIES.populateClientTypeLov();

            $(".datepicker-input").each(function() {
                $(this).datetimepicker({
                    format: 'DD/MM/YYYY'
                });

            });

            $(document).ajaxStart(function () {
                $("#saveProspectsBtn").attr("disabled", true);
            });
            $(document).ajaxComplete(function () {
                $("#saveProspectsBtn").attr("disabled", false);
            });


        });

    });
</script>