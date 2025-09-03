<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<script type="text/javascript"
        src="<c:url value="/js/modules/utils/select2builder.js"/>"></script>
<script type="text/javascript"
        src="<c:url value="/libs/rivets/rivets.js"/>"></script>
<script type="text/javascript"
        src="<c:url value="/js/modules/medicalsetups/providercontracts.js"/>"></script>

<div class="x_panel">
    <div class="x_title">
        <h2>Service Provider Contracts</h2>
        <ul class="nav navbar-right panel_toolbox">
            <li><a class="collapse-link"><i class="fa fa-chevron-up"></i></a>
            </li>
        </ul>
        <div class="clearfix"></div>
    </div>
    <div class="x_content">
        <input type="hidden" id="provd-contract-pk">
        <button type="button" class="btn btn-info float-right"
                id="btn-add-contracts">New</button>
        <div class="cutom-container">
        <table id="providerContractTbl" class="table" style="width:100%">
            <thead>
            <tr>

                <th>Contract No</th>
                <th>Contract Type</th>
                <th>Provider</th>
                <th>Provider Type</th>
                <th>Status</th>
                <th>Issue Date</th>
                <th>Begin Date</th>
                <th>End Date</th>
                <th width="5%"></th>
                <th width="5%"></th>
            </tr>
            </thead>
        </table>
            </div>
        </div>
    </div>

<div class="modal fade" id="provContractModal" tabindex="-1" role="dialog"
     aria-labelledby="provContractModalLabel" aria-hidden="true">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal"
                        aria-label="Close">
                    <span aria-hidden="true">&times;</span>
                </button>
                <h4 class="modal-title" id="provContractModalLabel">Edit/Add Service Provider Contract</h4>
            </div>
            <div class="modal-body">

                <form id="prov-contract-form" class="form-horizontal">
                    <input type="hidden" class="form-control" id="provtype-code" name="spcId">
                    <input type="hidden" class="form-control" id="provtype-contract-code" name="contractNo">
                    <div class="item form-group">
                        <label for="cou-name" class="col-md-3 label-align">
                            Contract No</label>

                        <div class="col-md-8">
                            <p class="form-control-static" id="contract-no"></p>
                        </div>
                    </div>
                    <div class="item form-group">
                        <label for="brn-id" class="col-md-3 label-align">
                            Service Provider</label>

                        <div class="col-md-8">
                            <input type="hidden" id="provider-id" name="serviceProviders"/>
                            <input type="hidden" id="provider-name">
                            <div id="provider-frm" class="form-control"
                                 select2-url="<c:url value="/protected/medical/setups/providersel"/>" >

                            </div>
                        </div>
                    </div>
                    <div class="item form-group">
                        <label for="cou-name" class="col-md-3 label-align">
                            Provider Type</label>

                        <div class="col-md-8">
                            <p class="form-control-static" id="provider-type"></p>
                        </div>
                    </div>
                    <div class="item form-group">
                        <label for="cou-name" class="col-md-3 label-align">
                            Contract Type</label>

                        <div class="col-md-8">
                            <select class="form-control" id="contract-type" name="contractType"
                                    required>
                                <option value="">Select Contract Type</option>
                                <option value="P">Permanent</option>
                            </select>
                        </div>
                    </div>

                    <div class="item form-group">
                        <label for="cou-name" class="col-md-3 label-align">
                            Contract From</label>

                        <div class="col-md-8">
                            <div class='input-group date datepicker-input' id="wef-date">
                                <input  class="form-control float-right" name="wefDate"
                                        id="from-date" required />
                                <div class="input-group-addon">
                                    <span class="fa fa-calendar"></span>
                                </div>
                            </div>
                        </div>
                    </div>
                    <div class="item form-group">
                        <label for="cou-name" class="col-md-3 label-align">
                            Contract To</label>

                        <div class="col-md-8">
                            <div class='input-group date datepicker-input' id="wet-date">
                                <input  class="form-control float-right" name="wetDate"
                                        id="contract-to-date" required/>
                                <div class="input-group-addon">
                                    <span class="fa fa-calendar"></span>
                                </div>
                            </div>
                        </div>
                    </div>
                    <div class="item form-group">
                        <label for="cou-name" class="col-md-3 label-align">
                           Status</label>

                        <div class="col-md-8">
                            <select class="form-control" id="contract-status" name="status"
                                    required>
                                <option value="">Select Status</option>
                                <option value="A">Active</option>
                                <option value="I">InActive</option>
                            </select>
                        </div>
                    </div>
                    <div class="item form-group">
                        <label for="cou-name" class="col-md-3 label-align">
                           Remarks</label>

                        <div class="col-md-8">
                            <textarea class="form-control" rows="3" id="contr-notes" name="notes"></textarea>
                        </div>
                    </div>


                </form>
            </div>
            <div class="modal-footer">
                <button data-loading-text="Saving..." id="saveProvderContractsBtn"
                        type="button" class="btn btn-success">Save</button>
                <button type="button" class="btn btn-default" data-dismiss="modal">
                    Cancel</button>
            </div>
        </div>
    </div>
</div>