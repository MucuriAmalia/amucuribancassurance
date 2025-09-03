<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<script type="text/javascript" src="<c:url value="/js/modules/medicalsetups/ailments.js"/>"></script>

<div class="x_panel">
    <button class="btn btn-success btn btn-info float-right" id="btn-add-ailments">New</button>
    <div class="x_title">
        <h4>Ailments</h4>
    </div>
    <div class="cutom-container">
    <table id="ailments-tbl" class="table" style="width:100%">
        <thead>
        <tr>

            <th>Ailment Code</th>
            <th>Ailment Desc</th>
            <th>Waiting Days</th>
            <th>Chronic</th>
            <th>Gender Affected</th>
            <th width="5%"></th>
            <th width="5%"></th>
        </tr>
        </thead>
    </table>
        </div>
</div>
<div class="x_panel">
    <button class="btn btn-success btn btn-info float-right" id="btn-add-diagnosis">New</button>
    <div class="x_title">
        <h4>Diagnosis</h4>
    </div>
    <input type="hidden" class="form-control" id="diag-ailment-pk">
    <div class="cutom-container">
    <table id="diagnosis-tbl" class="table" style="width:100%">
        <thead>
        <tr>

            <th>Diagnosis Code</th>
            <th>Diagnosis Desc</th>
            <th>Waiting Days</th>
            <th>Chronic</th>
            <th>Gender Affected</th>
            <th width="5%"></th>
            <th width="5%"></th>
        </tr>
        </thead>
    </table>
        </div>
</div>
<div class="modal fade" id="ailmentModal" tabindex="-1" role="dialog"
     aria-labelledby="ailmentModalLabel" aria-hidden="true">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal"
                        aria-label="Close">
                    <span aria-hidden="true">&times;</span>
                </button>
                <h4 class="modal-title" id="ailmentModalLabel">
                    Edit/Add Ailment
                </h4>
            </div>
            <div class="modal-body">
                <form id="ailment-form" class="form-horizontal">
                    <input type="hidden" class="form-control" id="ailment-pk" name="baId">
                    <input type="hidden" class="form-control" id="diag-ail-pk" name="parentAilment">
                    <div class="item form-group">
                        <label for="unit-id" class="col-md-3 label-align">Ailment ID</label>

                        <div class="col-md-8">
                            <input type="text" class="form-control" id="ailment-id"
                                   name="baShtDesc"  required>
                        </div>
                    </div>
                    <div class="item form-group">
                        <label for="unit-id" class="col-md-3 label-align">Ailment Desc</label>

                        <div class="col-md-8">
                            <input type="text" class="form-control" id="ailment-desc"
                                   name="baDesc"  required>
                        </div>
                    </div>
                    <div class="item form-group">
                        <label for="unit-id" class="col-md-3 label-align">Cost Per Claim</label>

                        <div class="col-md-8">
                            <input type="text" class="form-control" id="cost-per-limit"
                                   name="costPerClaim"  required>
                        </div>
                    </div>
                    <div class="item form-group">
                        <label for="unit-id" class="col-md-3 label-align">Upper Limit</label>

                        <div class="col-md-8">
                            <input type="text" class="form-control" id="ailment-upper-limit"
                                   name="upperLimit"  required>
                        </div>
                    </div>
                    <div class="item form-group">
                        <label for="unit-id" class="col-md-3 label-align">Waiting Days</label>

                        <div class="col-md-8">
                            <input type="number" class="form-control" id="ailment-wait-days"
                                   name="waitingDays"  required>
                        </div>
                    </div>
                    <div class="item form-group">
                        <label for="rate-taxable" class="col-md-3 label-align">Chronic?</label>
                        <div class="col-md-9 checkbox">
                            <label>
                                <input type="checkbox" name="chronic" id="chronic">
                            </label>
                        </div>
                    </div>
                    <div class="item form-group">
                        <label for="cou-name" class="col-md-3 label-align">Gender Affected</label>

                        <div class="col-md-8">
                            <select class="form-control" id="ail-gender" name="genderAffected" required>
                                <option value="">Select Gender</option>
                                <option value="M">Male</option>
                                <option value="F">Female</option>
                                <option value="B">Both</option>

                            </select>
                        </div>
                    </div>

                </form>
            </div>
            <div class="modal-footer">
                <button data-loading-text="Saving..." id="saveAilments"
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
