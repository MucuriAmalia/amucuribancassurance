<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<script type="text/javascript" src="<c:url value="/js/modules/utils/select2builder.js"/>"></script>
<script type="text/javascript" src="<c:url value="/libs/rivets/rivets.js"/>"></script>
<script type="text/javascript" src="<c:url value="/js/modules/medicalsetups/bedtypes.js"/>"></script>

<div class="x_panel">
    <button class="btn btn-success btn btn-info float-right" id="btn-add-lab-tests">New</button>
    <div class="x_title">
        <h4>Medical Services</h4>
    </div>
    <div class="cutom-container">
    <table id="lab-tests-tbl" class="table" style="width:100%">
        <thead>
        <tr>

            <th>Service ID</th>
            <th>Service Desc</th>
            <th>Upper Limit</th>
            <th>CPT Code</th>
            <th width="5%"></th>
            <th width="5%"></th>
        </tr>
        </thead>
    </table>
        </div>
</div>
<div class="x_panel">
    <button class="btn btn-success btn btn-info float-right" id="btn-add-benefits">New</button>
    <div class="x_title">
        <h4>Activities</h4>
    </div>
    <div class="cutom-container">
    <table id="benefits-tbl" class="table" style="width:100%">
        <thead>
        <tr>

            <th>Activity ID</th>
            <th>Activity Desc</th>
            <th>Affected Benefit</th>
            <th width="5%"></th>
            <th width="5%"></th>
        </tr>
        </thead>
    </table>
        </div>
</div>

<div class="modal fade" id="benefitsModal" tabindex="-1" role="dialog"
     aria-labelledby="benefitsModalLabel" aria-hidden="true">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal"
                        aria-label="Close">
                    <span aria-hidden="true">&times;</span>
                </button>
                <h4 class="modal-title" id="bedTypesModalLabel">
                    Edit/Add Activity
                </h4>
            </div>
            <input type="hidden" id="lab-pk">
            <div class="modal-body">
                <form id="benefits-form" class="form-horizontal">
                    <input type="hidden" class="form-control" id="benefit-pk" name="benId">
                    <input type="hidden" class="form-control" id="lab-benefit-pk" name="services">
                    <div class="item form-group">
                        <label for="unit-id" class="col-md-3 label-align">Activity ID</label>

                        <div class="col-md-8">
                            <input type="text" class="form-control" id="benefit-id"
                                   name="benShtDesc"  required>
                        </div>
                    </div>
                    <div class="item form-group">
                        <label for="unit-id" class="col-md-3 label-align">Activity Desc</label>

                        <div class="col-md-8">
                            <input type="text" class="form-control" id="benefit-desc"
                                   name="benDesc"  required>
                        </div>
                    </div>
                    <div class="item form-group">
                        <label for="rate-taxable" class="col-md-3 label-align">Benefit Affected</label>
                        <div class="col-md-8">
                            <input type="hidden" id="benefit-sec-id" name="section"/>
                            <input type="hidden" id="benefit-sec-dec"/>
                            <div id="section-frm" class="form-control"
                                 select2-url="<c:url value="/protected/medical/setups/medsectionssel"/>" >

                            </div>
                        </div>
                    </div>

                </form>
            </div>
            <div class="modal-footer">
                <button data-loading-text="Saving..." id="saveBenefits"
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


<div class="modal fade" id="labtestsModal" tabindex="-1" role="dialog"
     aria-labelledby="labtestsModalLabel" aria-hidden="true">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal"
                        aria-label="Close">
                    <span aria-hidden="true">&times;</span>
                </button>
                <h4 class="modal-title" id="labtestsModalLabel">
                    Edit/Add Lab Tests
                </h4>
            </div>
            <div class="modal-body">
                <form id="lab-test-form" class="form-horizontal">
                    <input type="hidden" class="form-control" id="lab-test-pk" name="labId">
                    <div class="item form-group">
                        <label for="unit-id" class="col-md-3 label-align">Lab Test ID</label>

                        <div class="col-md-8">
                            <input type="text" class="form-control" id="lab-test-id"
                                   name="labShtDesc"  required>
                        </div>
                    </div>
                    <div class="item form-group">
                        <label for="unit-id" class="col-md-3 label-align">Lab Test</label>

                        <div class="col-md-8">
                            <input type="text" class="form-control" id="lab-test-desc"
                                   name="labDesc"  required>
                        </div>
                    </div>
                    <div class="item form-group">
                        <label for="unit-id" class="col-md-3 label-align">Upper Limit</label>

                        <div class="col-md-8">
                            <input type="text" class="form-control" id="lab-upper-limit"
                                   name="upperLimit"  required>
                        </div>
                    </div>
                    <div class="item form-group">
                        <label for="unit-id" class="col-md-3 label-align">CPT Code</label>

                        <div class="col-md-8">
                            <input type="text" class="form-control" id="lab-cpt-code"
                                   name="cptCode"  required>
                        </div>
                    </div>

                </form>
            </div>
            <div class="modal-footer">
                <button data-loading-text="Saving..." id="saveLabTest"
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
