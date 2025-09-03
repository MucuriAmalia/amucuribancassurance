<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<div class="modal fade" id="editStretchyReportModal" tabindex="-1" role="dialog"
     aria-labelledby="editStretchyReportModalLabel" aria-hidden="true">
  <div class="modal-dialog">
    <div class="modal-content">
      <div class="modal-header">
        <h4 class="modal-title" id="createStretchyReportModalLabel">
          Create/Edit Custom Report
        </h4>
        <button type="button" class="close" data-dismiss="modal"
                aria-label="Close">
          <span aria-hidden="true">&times;</span>
        </button>

      </div>
      <div class="modal-body">

        <form id="report-form" class="form-horizontal">
          <input type="hidden" class="form-control" id="stretchy-id-pk" name="strId">

          <div class="item form-group">
            <label for="report-name" class="label-align col-md-3">Report Name*</label>
            <div class="col-md-9 col-xs-12">
              <input type="text" name="strRptName" id="report-name" class="form-control"
                     placeholder="Report Name" required>
            </div>
          </div>
          <div class="item form-group">
            <label>Report Type*</label>
            <div class="col-md-9 col-xs-12 gender">

              <select class="form-control" id="report-type" name="reportType" name="moduleCode" required>
                <option value="">Report Type</option>
                <option value="T">Table</option>
                <option value="C">Charts</option>
              </select>
            </div>
          </div>
          <div class="item form-group">
            <label>Report Category*</label>
            <div class="col-md-9 col-xs-12 gender">

              <select name="reportCategory" class="form-control" id="report-category" name="moduleCode" required>
                <option value="">Report Category</option>
                <option value="U">Underwriting</option>
                <option value="A">Accounts</option>
                <option value="C">Claims</option>
                <option value="M">Medical</option>
              </select>
            </div>
          </div>
          <div class="item form-group">
            <label for="report-sql" class="label-align col-md-3">Report SQL*</label>
            <div class="col-md-9 col-xs-12">
                        <textarea class="form-control" name="reportSql" id="report-sql"
                                  placeholder="Report SQL"></textarea>
            </div>
          </div>
          <div class="item form-group">
            <label for="description" class="label-align col-md-3">Description</label>
            <div class="col-md-9 col-xs-12">
                        <textarea class="resizable_textarea form-control" name="description" id="description"
                                  placeholder="Give a brief description..."></textarea>
            </div>
          </div>



        </form>
      </div>
      <div class="modal-footer">
        <button data-loading-text="Saving..." id="saveStretchyParameterBtn"
                type="button" class="btn btn-success">
          Save
        </button>
        <button type="button" class="btn btn-default" data-dismiss="modal">
          Cancel
        </button>
      </div>
    </div>
  </div>
</div>
<script>
  var redirectUrl = "<c:url value='/protected/stretchyreports/rpts'/>";
</script>
