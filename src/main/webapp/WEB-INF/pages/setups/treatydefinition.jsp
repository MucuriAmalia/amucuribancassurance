<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<script type="text/javascript"
        src="<c:url value="/js/modules/utils/select2builder.js"/>"></script>
<div class="x_panel">
    <div class="x_title">
        <h2>Treaty Definition</h2>
        <ul class="nav navbar-right panel_toolbox">
            <li><a class="collapse-link"><i class="fa fa-chevron-up"></i></a>
            </li>
        </ul>
        <div class="clearfix"></div>
    </div>
<div class="x_content">
<div class="col-md-12 col-xs-12 table-responsive">
    <button type="button" class="btn btn-info" id="btn-add-treaty">New</button>
    <hr>
    <table id="treaty-tbl" class="table" style="width:100%">
        <thead>
        <tr>

            <th>Treaty Type</th>
            <th>WEF</th>
            <th>WET</th>
            <th>Cash Call</th>
            <th>Ces. Rate</th>
            <th>Limit</th>
            <th>Sum Insured</th>
            <th>Currency</th>
            <th>Status</th>
            <th>Raised By</th>
            <th>Auth By</th>
            <th></th>
            <th></th>
        </tr>
        </thead>
    </table>

</div>


<div class="x_content treaty-children">
    <div class="" role="tabpanel" data-example-id="togglable-tabs">
        <ul id="myTab-2" class="nav nav-tabs bar_tabs" role="tablist">
            <li role="presentation" class="active"><a href="#tab_content1"
                                                      id="home-tab" role="tab" data-toggle="tab"
                                                      aria-expanded="true">Classification Configuration</a>
            </li>
            <li role="presentation"><a href="#tab_content2"
                                                      id="home-tab-2" role="tab" data-toggle="tab"
                                                      aria-expanded="true">Participants Configuration</a>
            </li>
        </ul>
    </div>

        <div id="myTabContent" class="tab-content">
            <div role="tabpanel" class="tab-pane active"
                 id="tab_content1" aria-labelledby="home-tab">
                <button type="button" class="btn btn-info float-right" id="btn-add-treaty-class">New</button>
                <div class="col-md-12 col-xs-12 table-responsive">
                <table id="treaty-classes-tbl" class="table" style="width:100%">
                    <thead>
                    <tr>

                        <th>Classification</th>
                        <th>Retention Limit</th>
                        <th>Facre Cede Rate</th>
                        <th>Claim Limit</th>
                        <th>Insured Limit</th>
                        <th>RI Premium Tax Rate</th>
                        <th>Min Estimated Max Loss</th>
                        <th></th>
                        <th></th>
                    </tr>
                    </thead>
                </table>
                </div>
            </div>
            <div role="tabpanel" class="tab-pane fade"
                 id="tab_content2" aria-labelledby="profile-tab">
                <button type="button" class="btn btn-info float-right" id="btn-add-treaty-participant">New</button>
                <div class="col-md-12 col-xs-12 table-responsive">
                    <table id="treaty-participants-tbl" class="table" style="width:100%">
                        <thead>
                        <tr>

                            <th>Participant</th>
                            <th>Broker</th>
                            <th>Rate</th>
                            <th>Tax Type?</th>
                            <th>Revenue Item</th>
                            <th>Tax Rate</th>
                            <th>Recovery Percent</th>
                            <th></th>
                            <th></th>
                        </tr>
                        </thead>
                    </table>
                </div>
            </div>
        </div>

</div>
</div>
</div>

<div class="modal fade" id="addTreatyModal" tabindex="-1" role="dialog"
     aria-labelledby="addTreatyModalLabel" aria-hidden="true">
    <div class="modal-dialog modal-md">
        <div class="modal-content">
            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal"
                        aria-label="Close">
                    <span aria-hidden="true">&times;</span>
                </button>
                <h4 class="modal-title" id="addTreatyModalLabel">
                   Add/Edit Treaty
                </h4>
            </div>

            <div class="modal-body">

                <form id="treaty-form" data-parsley-validate class="form-horizontal form-label-left">
                    <input type="hidden" name="treatyId" id="treaty-pk">

                    <div class="item form-group form-required">
                        <div class="col-md-12 col-xs-12">
                            <label for="treatyType" class="label-align col-md-4">Select Treaty<span class="required">*</span></label>
                            <div class="col-md-8 col-xs-12">
                                <select class="form-control" id="treatyType" name="treatyType">
                                    <option value="">Select Treaty</option>
                                    <option value="MAN">Mandatory Treaty</option>
                                    <option value="QUOTA">Quota Share</option>
                                    <option value="FIRSTSUP">1st Surplus</option>
                                    <option value="SECONDSUP">2nd Surplus</option>
                                    <option value="FACRE">Facultative</option>
                                    <option value="XOL">Excess of Loss</option>
                                </select>
                            </div>
                        </div>
                    </div>
                    <div class="item form-group form-required">
                        <div class="col-md-12 col-xs-12">
                            <label for="curr-frm" class="label-align col-md-4">
                                Currency<span class="required">*</span></label>
                            <div class="col-md-8 col-xs-12">
                                <input type="hidden" id="cur-id" name="currencyId"/>
                                <input type="hidden" id="cur-name">
                                <div id="curr-frm" class="form-control"
                                     select2-url="<c:url value="/protected/uw/policies/uwcurrencies"/>" >

                                </div>
                            </div>
                        </div>
                    </div>
                    <div class="item form-group">
                        <div class="col-md-12 col-xs-12">
                        <label for="cession-rate" class="col-md-4 label-align">Treaty Cession Rate</label>

                        <div class="col-md-8">
                            <input type="text" class="form-control" id="cession-rate"
                                   name="cessionRate">
                        </div>
                        </div>
                    </div>
                    <div class="item form-group">
                        <div class="col-md-12 col-xs-12">
                            <label for="cession-rate-type" class="col-md-4 label-align">Rate Type</label>

                            <div class="col-md-8">
                                <select class="form-control" id="cession-rate-type" name="rateType">
                                    <option value="">Select Rate Type</option>
                                    <option value="P">Percentage</option>
                                    <option value="L">Lines</option>
                                </select>
                            </div>
                        </div>
                    </div>
                    <div class="item form-group">
                        <div class="col-md-12 col-xs-12">
                            <label for="retention-limit" class="col-md-4 label-align">Retention Limit</label>

                            <div class="col-md-8">
                                <input type="text" class="form-control amount" id="retention-limit"
                                       name="limit">
                            </div>
                        </div>
                    </div>
                    <div class="item form-group">
                        <div class="col-md-12 col-xs-12">
                            <label for="cash-call" class="col-md-4 label-align">Cash Call</label>

                            <div class="col-md-8">
                                <input type="text" class="form-control amount" id="cash-call"
                                       name="cashCall">
                            </div>
                        </div>
                    </div>
                    <div class="item form-group">
                        <div class="col-md-12 col-xs-12">
                            <label for="cash-call" class="col-md-4 label-align">Sum Insured From</label>

                            <div class="col-md-8">
                                <input type="text" class="form-control amount" id="si-from"
                                       name="sumInsuredFrom">
                            </div>
                        </div>
                    </div>
                    <div class="item form-group">
                        <div class="col-md-12 col-xs-12">
                            <label for="comm-rate" class="col-md-4 label-align">Commission Rate(%)</label>

                            <div class="col-md-8">
                                <input type="number" class="form-control" id="comm-rate"
                                       name="commRate" min="0" max="100">
                            </div>
                        </div>
                    </div>
                    <div class="item form-group">
                        <div class="col-md-12 col-xs-12">
                            <label for="profit-comm" class="col-md-4 label-align">Profit Commission (%)</label>

                            <div class="col-md-8">
                                <input type="text" class="form-control" id="profit-comm"
                                       name="profitCommission" min="0" max="100">
                            </div>
                        </div>
                    </div>
                    <div class="item form-group">
                        <div class="col-md-12 col-xs-12">
                            <label for="profit-comm" class="col-md-4 label-align">Premium Portfolio (%)</label>

                            <div class="col-md-8">
                                <input type="text" class="form-control" id="prem-portfolio"
                                       name="premiumPortfolio" min="0" max="100">
                            </div>
                        </div>
                    </div>
                    <div class="item form-group form-required">
                        <div class="col-md-12 col-xs-12">
                        <label for="wef-date" class="col-md-4 label-align">
                            Validity From</label>

                        <div class="col-md-8">
                            <div class='input-group date datepicker-input' id="wef-date">
                                <input  class="form-control float-right" name="wef"
                                        id="comm-from-date" required />
                                <div class="input-group-addon">
                                    <span class="fa fa-calendar"></span>
                                </div>
                            </div>
                        </div>
                        </div>
                    </div>
                    <div class="item form-group form-required">
                        <div class="col-md-12 col-xs-12">
                            <label for="wef-date" class="col-md-4 label-align">
                                Validity To</label>

                            <div class="col-md-8">
                                <div class='input-group date datepicker-input' id="wet-date">
                                    <input  class="form-control float-right" name="wet"
                                            id="comm-to-date" required />
                                    <div class="input-group-addon">
                                        <span class="fa fa-calendar"></span>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                </form>
            </div>
            <div class="modal-footer">
                <button data-loading-text="Saving..." id="saveTreaty"
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
<div class="modal fade" id="addTreatyClassModal" tabindex="-1" role="dialog"
     aria-labelledby="addTreatyClassModalLabel" aria-hidden="true">
    <div class="modal-dialog modal-md">
        <div class="modal-content">
            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal"
                        aria-label="Close">
                    <span aria-hidden="true">&times;</span>
                </button>
                <h4 class="modal-title" id="addTreatyClassModalLabel">
                    Add/Edit Treaty Class
                </h4>
            </div>

            <div class="modal-body">

                <form id="treaty-class-form" data-parsley-validate class="form-horizontal form-label-left">
                    <input type="hidden" name="treatyClassId" id="treaty-class-pk">
                    <input type="hidden" name="treatyId" id="treaty-class-treaty-pk">

                    <div class="item form-group form-required">
                        <div class="col-md-12 col-xs-12">
                            <label for="curr-frm" class="label-align col-md-4">
                                Classification<span class="required">*</span></label>
                            <div class="col-md-7 col-xs-12">
                                <input type="hidden" id="sub-code" name="subclassId"/>
                                <input type="hidden" id="sub-name">
                                <div id="subclass-frm" class="form-control"
                                     select2-url="<c:url value="/protected/setups/selSubclasses"/>">

                                </div>
                            </div>
                        </div>
                    </div>
                    <div class="item form-group">
                        <div class="col-md-12 col-xs-12">
                            <label for="cession-rate" class="col-md-4 label-align">Retention Limit</label>

                            <div class="col-md-8">
                                <input type="text" class="form-control amount" id="class-retention-limit"
                                       name="retentionLimit">
                            </div>
                        </div>
                    </div>

                    <div class="item form-group">
                        <div class="col-md-12 col-xs-12">
                            <label for="retention-limit" class="col-md-4 label-align">Min. Estimated Max Loss</label>

                            <div class="col-md-8">
                                <input type="text" class="form-control amount" id="minimum-eml"
                                       name="minEml">
                            </div>
                        </div>
                    </div>
                    <div class="item form-group">
                        <div class="col-md-12 col-xs-12">
                            <label for="cash-call" class="col-md-4 label-align">Insured Limit</label>

                            <div class="col-md-8">
                                <input type="text" class="form-control amount" id="insured-limit"
                                       name="insuredLimit">
                            </div>
                        </div>
                    </div>
                    <div class="item form-group">
                        <div class="col-md-12 col-xs-12">
                            <label for="comm-rate" class="col-md-4 label-align">Claim Limit</label>

                            <div class="col-md-8">
                                <input type="text" class="form-control amount" id="claim-limit"
                                       name="claimLimit">
                            </div>
                        </div>
                    </div>
                </form>
            </div>
            <div class="modal-footer">
                <button data-loading-text="Saving..." id="saveTreatyClass"
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
<div class="modal fade" id="addTreatyParticipantModal" tabindex="-1" role="dialog"
     aria-labelledby="addTreatyParticipantModalLabel" aria-hidden="true">
    <div class="modal-dialog modal-md">
        <div class="modal-content">
            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal"
                        aria-label="Close">
                    <span aria-hidden="true">&times;</span>
                </button>
                <h4 class="modal-title" id="addTreatyParticipantModalLabel">
                    Add/Edit Treaty Participant
                </h4>
            </div>

            <div class="modal-body">

                <form id="treaty-participant-form" data-parsley-validate class="form-horizontal form-label-left">
                    <input type="hidden" name="treatyClassId" id="treaty-participant-pk">
                    <input type="hidden" name="treatyId" id="treaty-participant-treaty-pk">

                    <div class="item form-group form-required">
                        <div class="col-md-12 col-xs-12">
                            <label for="curr-frm" class="label-align col-md-4">
                                Participant<span class="required">*</span></label>
                            <div class="col-md-8 col-xs-12">
                                <input type="hidden" id="participant-code" name="participantId"/>
                                <input type="hidden" id="cc">
                                <div id="participants-frm" class="form-control"
                                     select2-url="<c:url value="/protected/setups/selReinsurers"/>">

                                </div>
                            </div>
                        </div>
                    </div>
                    <div class="item form-group">
                        <label for="broker-appl" class="col-md-4 label-align">Broker Applicable?</label>

                        <div class="col-md-8 checkbox">
                            <label>
                                <input type="checkbox" id="broker-appl" name="brokerType"/>
                            </label>
                        </div>
                    </div>
                    <div class="item form-group broker-group">
                        <div class="col-md-12 col-xs-12">
                            <label for="curr-frm" class="label-align col-md-4">
                                Broker</label>
                            <div class="col-md-8 col-xs-12">
                                <input type="hidden" id="broker-code" name="brokerId"/>
                                <input type="hidden" id="broker-name">
                                <div id="broker-frm" class="form-control"
                                     select2-url="<c:url value="/protected/setups/selBrokers"/>">

                                </div>
                            </div>
                        </div>
                    </div>

                    <div class="item form-group">
                        <div class="col-md-12 col-xs-12">
                            <label for="retention-limit" class="col-md-4 12 col-xs-12 label-align">Rate</label>

                            <div class="col-md-8 12 col-xs-12">
                                <input type="number" class="form-control" id="rate"
                                       name="rate" min="0" max="100">
                            </div>
                        </div>
                    </div>
                    <div class="item form-group">
                        <label for="rev-item-id" class="col-md-4 12 col-xs-12 label-align">Revenue Item</label>

                        <div class="col-md-8 12 col-xs-12">
                            <input type="hidden" id="rev-item-id"  name="revenueItemId"/>
                            <input type="hidden" id="rev-item-name">
                            <div id="rev-item-def" class="form-control"
                                 select2-url="<c:url value="/protected/setups/selAllRevItems"/>" >

                            </div>
                        </div>
                    </div>
                    <div class="item form-group">
                        <label for="tax-type" class="col-md-4 12 col-xs-12 label-align">Tax Type</label>

                        <div class="col-md-8 12 col-xs-12">
                            <select class="form-control" id="tax-type" name="taxChargeable">
                                <option value="">Select Tax Type</option>
                                <option value="PT">Reinsurance Premium Tax</option>
                                <option value="UT">Underwriting Tax</option>
                                <option value="B">Both</option>
                            </select>
                        </div>
                    </div>
                    <div class="item form-group">
                        <div class="col-md-12 col-xs-12">
                            <label for="retention-limit" class="col-md-4 12 col-xs-12 label-align">Tax Rate</label>

                            <div class="col-md-8 12 col-xs-12">
                                <input type="text" class="form-control amount" id="tax-rate"
                                       name="taxRate">
                            </div>
                        </div>
                    </div>
                    <div class="item form-group">
                        <label for="tax-rate-type" class="col-md-4 12 col-xs-12 label-align">Tax Rate Type</label>

                        <div class="col-md-8 12 col-xs-12">
                            <select class="form-control" id="tax-rate-type" name="taxRateType">
                                <option value="">Select Rate Type</option>
                                <option value="P">Percentage</option>
                                <option value="M">Per Mille</option>
                                <option value="A">Amount</option>

                            </select>
                        </div>
                    </div>
                    <div class="item form-group">
                        <div class="col-md-12 col-xs-12">
                            <label for="recovery-pct" class="col-md-4 12 col-xs-12 label-align">Recovery(%)</label>

                            <div class="col-md-8 12 col-xs-12">
                                <input type="number" class="form-control" id="recovery-pct"
                                       name="recoveryPercent">
                            </div>
                        </div>
                    </div>
                </form>
            </div>
            <div class="modal-footer">
                <button data-loading-text="Saving..." id="saveTreatyParticipant"
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


        $(document).ready(function() {
            $(".treaty-children").hide();
            $('.amount').number( true, 2 );
        });



        $(function(){
            $(".broker-group").css('display','none');
            $(".datepicker-input").each(function() {
                $(this).datetimepicker({
                    format: 'DD/MM/YYYY'
                });
            });
            $("#broker-appl").change(function() {
                $("#broker-frm").select2("val", "");
                if(this.checked) {
                    ReinsuranceSetup.populateBrokersLov();
                    $(".broker-group").css('display','block');
                }
                else{
                    $(".broker-group").css('display','none');
                }
            });
            ReinsuranceSetup.getTreaties();
            ReinsuranceSetup.getTreatyClasses();
            ReinsuranceSetup.getTreatyParticipants();
            ReinsuranceSetup.populateCurrencyLov();
            $("#btn-add-treaty").click(function(){
                $('#addTreatyModal').modal({
                    backdrop: 'static',
                    keyboard: true
                });
            });

            $("#btn-add-treaty-participant").click(function(){
                if($("#treaty-participant-treaty-pk").val()!=''){
                    $('#treaty-participant-form').find("input[type=text],input[type=number],input[type=mobileNumber],input[type=emailFull],input[type=password], textarea,select").val("");
                    $("#treaty-participant-pk").val("");
                    ReinsuranceSetup.populateReinsuresLov();
                    ReinsuranceSetup.populateRevItems();
                    $('#addTreatyParticipantModal').modal({
                        backdrop: 'static',
                        keyboard: true
                    });
                }
                else{

                    Swal.fire({
                        title: 'Error',
                        text: 'Select Treaty to add Participant',
                        icon: 'error'
                    });

                }


            });

            $("#btn-add-treaty-class").click(function(){
                if($("#treaty-class-treaty-pk").val()!=''){
                    $('#treaty-class-form').find("input[type=text],input[type=number],input[type=mobileNumber],input[type=emailFull],input[type=password], textarea,select").val("");
                    $("#treaty-class-pk").val("");
                    ReinsuranceSetup.populateSubclassLov();
                    $('#addTreatyClassModal').modal({
                        backdrop: 'static',
                        keyboard: true
                    });
                }
                else{
                    Swal.fire({
                        title: 'Error',
                        text: 'Select Treaty to add Classification',
                        icon: 'error'
                    });
                }


            });

            $("#saveTreaty").click(function(){
                ReinsuranceSetup.saveTreaty();
            });
            $("#saveTreatyClass").click(function(){
                ReinsuranceSetup.saveTreatyClass();
            });
            $("#saveTreatyParticipant").click(function(){
                ReinsuranceSetup.saveTreatyParticipant();
            });
        });


    var ReinsuranceSetup = {
        saveTreatyClass: function (){
            var $classForm = $('#treaty-class-form');
            var validator = $classForm.validate();

            if (!$classForm.valid()) {
                return;
            }
            var data = {};
            $classForm.serializeArray().map(function(x){data[x.name] = x.value;});
            var url = "createTreatyClasses";
            var request = $.post(url, data );
            request.success(function(){
                Swal.fire({
                title: 'Success',
                text: 'Record created/updated Successfully',
                icon: 'success'
            });
                $('#treaty-classes-tbl').DataTable().ajax.reload();
                $classForm.find("input[type=text],input[type=number],input[type=mobileNumber],input[type=emailFull],input[type=password],input[type=hidden], textarea,select").val("");
                $('#addTreatyClassModal').modal('hide');

            });

            request.error(function(jqXHR, textStatus, errorThrown){
                Swal.fire({
                title: 'Error',
                text: jqXHR.responseText,
                icon: 'error'
            });
            });
        },
        saveTreatyParticipant: function (){
            var $classForm = $('#treaty-participant-form');
            $classForm.validate();
            if (!$classForm.valid()) {
                return;
            }
            var data = {};
            $classForm.serializeArray().map(function(x){data[x.name] = x.value;});
            console.log(data);
            var url = "createTreatyParticipant";
            var request = $.post(url, data );
            request.success(function(){
                Swal.fire({
                title: 'Success',
                text: 'Record created/updated Successfully',
                icon: 'success'
            });
                $('#treaty-participants-tbl').DataTable().ajax.reload();
                $classForm.find("input[type=text],input[type=number],input[type=mobileNumber],input[type=emailFull],input[type=password],input[type=hidden], textarea,select").val("");
                $('#addTreatyParticipantModal').modal('hide');

            });

            request.error(function(jqXHR, textStatus, errorThrown){
                Swal.fire({
                title: 'Error',
                text: jqXHR.responseText,
                icon: 'error'
            });
            });
        },
        saveTreaty: function (){
            var $classForm = $('#treaty-form');
            var validator = $classForm.validate();

            if (!$classForm.valid()) {
                return;
            }
            var data = {};
            $classForm.serializeArray().map(function(x){data[x.name] = x.value;});
            var url = "createTreaty";
            var request = $.post(url, data );
            request.success(function(){
                Swal.fire({
                title: 'Success',
                text: 'Record created/updated Successfully',
                icon: 'success'
            });
                $('#treaty-tbl').DataTable().ajax.reload();
                $classForm.find("input[type=text],input[type=number],input[type=mobileNumber],input[type=emailFull],input[type=password],input[type=hidden], textarea,select").val("");
                $('#addTreatyModal').modal('hide');
            });

            request.error(function(jqXHR, textStatus, errorThrown){
                Swal.fire({
                title: 'Error',
                text: jqXHR.responseText,
                icon: 'error'
            });
            });
        },
        populateRevItems: function(){
            if($("#rev-item-def").filter("div").html() != undefined)
            {
                Select2Builder.initAjaxSelect2({
                    containerId : "rev-item-def",
                    sort : 'revenueItem',
                    change: function(e, a, v){
                        $("#rev-item-id").val(e.added.revenueId);
                    },
                    formatResult : function(a)
                    {
                        return a.revenueItem;
                    },
                    formatSelection : function(a)
                    {
                        return a.revenueItem;
                    },
                    initSelection: function (element, callback) {
                        var code = $("#rev-item-id").val();
                        var name = $("#rev-item-name").val();
                        var data = {item:name,revenueId:code};
                        callback(data);
                    },
                    id: "revenueId",
                    placeholder:"Select Revenue Item",
                });
            }
        },
        populateCurrencyLov: function (){
            if($("#curr-frm").filter("div").html() != undefined)
            {
                Select2Builder.initAjaxSelect2({
                    containerId : "curr-frm",
                    sort : 'curName',
                    change: function(e, a, v){
                        $("#cur-id").val(e.added.curCode);

                    },
                    formatResult : function(a)
                    {
                        return a.curName;
                    },
                    formatSelection : function(a)
                    {
                        return a.curName;
                    },
                    initSelection: function (element, callback) {

                    },
                    id: "curCode",
                    width:"250px",
                    placeholder:"Select Currency"

                });
            }
            $("#curr-frm").on("select2-removed", function(e) {
                $("#cur-id").val('');
            })
        },
        populateSubclassLov : function(){
            if($("#subclass-frm").filter("div").html() != undefined)
            {
                Select2Builder.initAjaxSelect2({
                    containerId : "subclass-frm",
                    sort : 'subId',
                    change: function(e, a, v){
                        $("#sub-code").val(e.added.subId);

                    },
                    formatResult : function(a)
                    {
                        return a.subDesc;
                    },
                    formatSelection : function(a)
                    {
                        return a.subDesc;
                    },
                    initSelection: function (element, callback) {
                        var code  = $('#sub-code').val();
                        var name = $("#sub-name").val();
                        var data = {subDesc:name,subId:code};
                        callback(data);
                    },
                    id: "subDesc",
                    width:"250px",
                    placeholder:"Select Classification"

                });
            }
        },
        populateReinsuresLov : function(){
            if($("#participants-frm").filter("div").html() != undefined)
            {
                Select2Builder.initAjaxSelect2({
                    containerId : "participants-frm",
                    sort : 'acctId',
                    change: function(e, a, v){
                        $("#participant-code").val(e.added.acctId);

                    },
                    formatResult : function(a)
                    {
                        return a.name;
                    },
                    formatSelection : function(a)
                    {
                        return a.name;
                    },
                    initSelection: function (element, callback) {
                        var code  = $('#participant-code').val();
                        var name = $("#participant-name").val();
                        var data = {name:name,subId:code};
                        callback(data);
                    },
                    id: "name",
                    width:"250px",
                    placeholder:"Select Participant"

                });
            }
        },
        populateBrokersLov : function(){
            if($("#broker-frm").filter("div").html() != undefined)
            {
                Select2Builder.initAjaxSelect2({
                    containerId : "broker-frm",
                    sort : 'acctId',
                    change: function(e, a, v){
                        $("#broker-code").val(e.added.acctId);

                    },
                    formatResult : function(a)
                    {
                        return a.name;
                    },
                    formatSelection : function(a)
                    {
                        return a.name;
                    },
                    initSelection: function (element, callback) {
                        var code  = $('#broker-code').val();
                        var name = $("#broker-name").val();
                        var data = {name:name,subId:code};
                        callback(data);
                    },
                    id: "name",
                    width:"250px",
                    placeholder:"Select Broker"

                });
            }
        },
        getTreatyParticipants: function (){
            var url = "treatyparticipants/-2000";
            var currTable =   $('#treaty-participants-tbl').DataTable(
                    {
                        "processing": true,
                        "serverSide": true,
                        "ajax": url,
                        lengthMenu: [[20, 30, 40, 50], [20, 30, 40, 50]],
                        pageLength: 20,
                        "autoWidth": true,
                        destroy: true,
                        "columns": [

                            {
                                "data": "participant"
                            },
                            {
                                "data": "broker"
                            },
                            {
                                "data": "rate",
                                "render": function (data, type, full, meta) {
                                    return UTILITIES.currencyFormat(full.rate);
                                }
                            },
                            {
                                "data": "taxChargeable"
                            },
                            {
                                "data": "revenueItemDesc"
                            },
                            {
                                "data": "taxRate",
                                "render": function (data, type, full, meta) {
                                    return UTILITIES.currencyFormat(full.taxRate)+" "+full.taxRateType;
                                }
                            },
                            {
                                "data": "recoveryPercent",
                                "render": function (data, type, full, meta) {
                                    return UTILITIES.currencyFormat(full.recoveryPercent);
                                }
                            },
                            {
                                "data": "treatyClassId",
                                "render": function (data, type, full, meta) {
                                    return '<button type="button" class="btn btn-success btn btn-info btn-sm" type="button" data-trans=' + encodeURI(JSON.stringify(full)) + '  onclick="authTrans(this);">Edit</button>';

                                }
                            },
                            {
                                "data": "treatyClassId",
                                "render": function (data, type, full, meta) {
                                    return '<button type="button" class="btn btn-danger btn btn-info btn-sm" type="button" data-trans=' + encodeURI(JSON.stringify(full)) + '  onclick="rejectRequistion(this);">Delete</button>';

                                }
                            },
                        ]
                    });
        },
        getTreatyClasses: function (){
            var url = "treatyclasses/-2000";
            var currTable =   $('#treaty-classes-tbl')
                .DataTable(
                    {
                        "processing": true,
                        "serverSide": true,
                        "ajax": url,
                        lengthMenu: [[20, 30, 40, 50], [20, 30, 40, 50]],
                        pageLength: 20,
                        "autoWidth": true,
                        destroy: true,
                        "columns": [

                            {
                                "data": "subclassDesc"
                            },
                            {
                                "data": "retentionLimit",
                                "render": function (data, type, full, meta) {
                                    return UTILITIES.currencyFormat(full.retentionLimit);
                                }
                            },
                            {
                                "data": "facCedeRate",
                                "render": function (data, type, full, meta) {
                                    return UTILITIES.currencyFormat(full.facCedeRate);
                                }
                            },
                            {
                                "data": "claimLimit",
                                "render": function (data, type, full, meta) {
                                    return UTILITIES.currencyFormat(full.claimLimit);
                                }
                            },
                            {
                                "data": "insuredLimit",
                                "render": function (data, type, full, meta) {
                                    return UTILITIES.currencyFormat(full.insuredLimit);
                                }
                            },
                            {
                                "data": "riPremTaxRate",
                                "render": function (data, type, full, meta) {
                                    return UTILITIES.currencyFormat(full.riPremTaxRate);
                                }
                            },
                            {
                                "data": "minEml",
                                "render": function (data, type, full, meta) {
                                    return UTILITIES.currencyFormat(full.minEml);
                                }
                            },
                            {
                                "data": "treatyClassId",
                                "render": function (data, type, full, meta) {
                                    return '<button type="button" class="btn btn-success btn btn-info btn-sm" type="button" data-trans=' + encodeURI(JSON.stringify(full)) + '  onclick="authTrans(this);">Edit</button>';

                                }
                            },
                            {
                                "data": "treatyClassId",
                                "render": function (data, type, full, meta) {
                                    return '<button type="button" class="btn btn-danger btn btn-danger btn-sm" type="button" data-trans=' + encodeURI(JSON.stringify(full)) + '  onclick="rejectRequistion(this);">Delete</button>';

                                }
                            },
                        ]
                    });
        },
        getTreaties: function (){
            var url = "treatylist";
            var currTable =   $('#treaty-tbl')
                .DataTable(
                    {
                        "processing": true,
                        "serverSide": true,
                        "ajax": url,
                        lengthMenu: [[20, 30, 40, 50], [20, 30, 40, 50]],
                        pageLength: 20,
                        "scrollX": true,
                        "autoWidth": true,
                        destroy: true,
                        "columns": [

                            {
                                "data": "treatyType"
                            },
                            {
                                "data": "wef",
                                "render": function (data, type, full, meta) {
                                    return moment(full.wef).format('DD/MM/YYYY');
                                },

                            },
                            {
                                "data": "wet",
                                "render": function (data, type, full, meta) {
                                    return moment(full.wet).format('DD/MM/YYYY');
                                },

                            },
                            {
                                "data": "cashCall",
                                "render": function (data, type, full, meta) {
                                    return UTILITIES.currencyFormat(full.cashCall);
                                }
                            },
                            {
                                "data": "cessionRate",
                                "render": function (data, type, full, meta) {
                                    var rateType = '';
                                    if(full.rateType && full.rateType==='L'){
                                        rateType = "Lines";
                                    }
                                    else if(full.rateType && full.rateType==='P'){
                                        rateType = "Percent";
                                    }
                                    return UTILITIES.currencyFormat(full.cessionRate)+' '+rateType;
                                }
                            },
                            {
                                "data": "limit",
                                "render": function (data, type, full, meta) {
                                    return UTILITIES.currencyFormat(full.limit);
                                }
                            },
                            {
                                "data": "sumInsuredFrom",
                                "render": function (data, type, full, meta) {
                                    return UTILITIES.currencyFormat(full.sumInsuredFrom);
                                }
                            },
                            {
                                "data": "currencyDesc",
                            },
                            {
                                "data": "status",
                            },
                            {
                                "data": "raisedBy",
                            },
                            {
                                "data": "authBy",
                            },
                            {
                                "data": "treatyId",
                                "render": function (data, type, full, meta) {
                                    return '<button type="button" class="btn btn-success btn btn-info btn-sm" type="button" data-trans=' + encodeURI(JSON.stringify(full)) + '  onclick="authTrans(this);">Approve</button>';

                                }
                            },
                            {
                                "data": "treatyId",
                                "render": function (data, type, full, meta) {
                                    return '<button type="button" class="btn btn-danger btn btn-info btn-sm" type="button" data-trans=' + encodeURI(JSON.stringify(full)) + '  onclick="rejectRequistion(this);">Reject</button>';

                                }
                            },
                        ]
                    });
            $('#treaty-tbl tbody').on( 'click', 'tr', function () {
                $(this).addClass('table-primary').siblings().removeClass('table-primary');
                var aData = currTable.rows('.table-primary').data();
                if (aData[0] === undefined || aData[0] === null){

                }
                else{
                    $("#treaty-class-treaty-pk").val(aData[0].treatyId);
                    $("#treaty-participant-treaty-pk").val(aData[0].treatyId);
                    $('#treaty-classes-tbl').DataTable().ajax.url( "treatyclasses/"+aData[0].treatyId ).load();
                    $('#treaty-participants-tbl').DataTable().ajax.url( "treatyparticipants/"+aData[0].treatyId ).load();
                    $(".treaty-children").show();
                }
            } );
        },

    }

</script>