<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<script type="text/javascript" src="<c:url value="/js/modules/utils/select2builder.js"/>"></script>
<script type="text/javascript" src="<c:url value="/js/modules/claims/claimants.js"/>"></script>

<div class="x_panel">
    <button class="btn btn-success btn btn-info float-right" id="btn-add-claimant">New</button>
    <div class="x_title">
        <h2><i class="fa fa-bars"></i> Claimants</h2>
        <div class="clearfix"></div>
    </div>
    <div class="table-responsive">
    <table id="claimant-tbl" class="table table-striped" style="width: 100%">
        <thead>
        <tr class="headings">

            <th>Surname</th>
            <th>Other Names</th>
            <th>Occupation</th>
            <th>ID Number</th>
            <th>Email</th>
            <th>Address</th>
            <th>Mobile Number</th>
            <th>Updated By</th>
            <th>Date Updated</th>
            <th width="5%"></th>
            <th width="5%"></th>
        </tr>
        </thead>
    </table>
        </div>
</div>


<div class="modal fade" id="claimantsModal" tabindex="-1" role="dialog"
     aria-labelledby="claimantsModalLabel" aria-hidden="true">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header">
                <h4 class="modal-title" id="claimantsModalLabel">
                    Edit/Add Claimant
                </h4>
                <button type="button" class="close" data-dismiss="modal"
                        aria-label="Close">
                    <span aria-hidden="true">&times;</span>
                </button>

            </div>
            <div class="modal-body" id="branch_model">
                <form id="claimants-form" class="form-horizontal">
                    <input type="hidden" class="form-control" id="clmnt-id" name="claimantId">
                    <div class="item form-group">
                        <label for="brn-id" class="col-md-3 label-align">Surname</label>

                        <div class="col-md-8">
                            <input type="text" class="form-control" id="clmnt-surname"
                                   name="surname"  required>
                        </div>
                    </div>
                    <div class="item form-group">
                        <label for="unit-id" class="col-md-3 label-align">Other Names</label>

                        <div class="col-md-8">
                            <input type="text" class="form-control" id="clmnt-othernames"
                                   name="otherNames"  required>
                        </div>
                    </div>
                    <div class="item form-group">
                        <label for="unit-id" class="col-md-3 label-align">Occupation</label>

                        <div class="col-md-8">
                            <input type="hidden" id="clmt-occupt" name="occupation"/>
                            <input type="hidden" id="clmt-occupt-names"/>
                            <div id="occup-def" class="form-control"
                                 select2-url="<c:url value="/protected/claims/selOccupations"/>" >

                            </div>
                        </div>
                    </div>
                    <div class="item form-group">
                        <label for="unit-id" class="col-md-3 label-align">ID Number</label>

                        <div class="col-md-8">
                            <input type="text" class="form-control" id="clmnt-idnumber"
                                   name="idNumber"  required>
                        </div>
                    </div>
                    <div class="item form-group">
                            <label for="unit-id" class="col-md-3 label-align">Email Address</label>
                                   <div class="col-md-8">
                                                <textarea class="form-control" rows="2" id="clmnt-email" name="email"></textarea>
                                            </div>
                                        </div>
                    <div class="item form-group">
                        <label for="unit-id" class="col-md-3 label-align">Address</label>

                        <div class="col-md-8">
                            <textarea class="form-control" rows="2" id="clmnt-address" name="address"></textarea>
                        </div>
                    </div>
                    <div class="item form-group">
                        <label for="unit-id" class="col-md-3 label-align">Mobile Number</label>

                        <div class="col-md-8">
                            <input type="text" class="form-control" id="clmnt-mobnumber"
                                   name="mobileNo"  required>
                        </div>
                    </div>

                </form>
            </div>
            <div class="modal-footer">
                <button data-loading-text="Saving..." id="saveClaimantDef"
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