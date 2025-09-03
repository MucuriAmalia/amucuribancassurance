<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<script type="text/javascript"
        src="<c:url value="/js/modules/utils/select2builder.js"/>"></script>
<script type="text/javascript" src="<c:url value="/js/modules/schedules/schedulemap.js"/>"></script>

<div class="x_panel">
    <div class="x_title">
        <h4>Schedule Mapping Setups</h4>
    </div>
    <form class="form-horizontal">
        <div class="item form-group">
            <label for="brn-id" class="col-md-3 label-align">Select
                Sub Class</label>

            <div class="col-md-4">
                <input type="hidden" id="sub-code"/>
                <div id="sub-class-def" class="form-control"
                     select2-url="<c:url value="/protected/setups/clauses/subclassSelect"/>">

                </div>
                <input type="hidden" id="class-pk">

            </div>
        </div>

    </form>


    <button class="btn btn-success btn btn-info float-right" id="btn-add-sched-map">New</button>
    <div class="cutom-container">
    <table id="sched-mappings-tbl" class="table" style="width:100%">
        <thead>
        <tr>

            <th>Column Name</th>
            <th>Column Type</th>
            <th>Lov Name</th>
            <th>Order</th>
            <th>Prem. Item Appl</th>
            <th>Premium Item</th>
            <th>Mapped Risk Column</th>
            <th>Mapped Prem. Item</th>
            <th width="5%"></th>
            <th width="5%"></th>
        </tr>
        </thead>
    </table>
        </div>
</div>

<div class="modal fade" id="schedMappingModal" tabindex="-1" role="dialog"
     aria-labelledby="schedMappingModalLabel" aria-hidden="true">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal"
                        aria-label="Close">
                    <span aria-hidden="true">&times;</span>
                </button>
                <h4 class="modal-title" id="schedMappingModalLabel">
                    Edit/Add Schedule Mapping
                </h4>
            </div>
            <div class="modal-body">

                <form id="sched-map-form" class="form-horizontal">
                    <input type="hidden" class="form-control" id="sched-code" name="smId">
                    <input type="hidden" class="form-control" id="sched-sub-det" name="subclass">
                    <input type="hidden" class="form-control" id="sched-col-index" name="columnIndex">

                    <div class="item form-group">
                        <label for="brn-id" class="col-md-3 label-align">Column Name</label>

                        <div class="col-md-8">
                            <input type="text" class="form-control" id="sched-col-name"
                                   name="columnName"  required>
                        </div>
                    </div>
                    <div class="item form-group">
                        <label for="rate-taxable" class="col-md-3 label-align">Premium Item?</label>
                        <div class="col-md-9 checkbox">
                            <label>
                                <input type="checkbox" name="premItem" id="chk-prem-item">
                            </label>

                        </div>
                    </div>
                    <div class="item form-group">
                        <label for="brn-id" class="col-md-3 label-align">Premium Item Affected</label>

                        <div class="col-md-8">
                            <input type="hidden" id="sect-pk-name"/>
                            <input type="hidden" id="sect-pk-code" name="sections"/>
                            <div id="sect-def" class="form-control"
                                 select2-url="<c:url value="/protected/setups/schedules/sclsections"/>">

                            </div>
                        </div>
                    </div>
                    <div class="item form-group">
                        <label for="cou-name" class="col-md-3 label-align">Column Type</label>

                        <div class="col-md-8">
                            <select class="form-control" id="column-type" name="columnType" required>
                                <option value="">Select Column Type</option>
                                <option value="D">Date</option>
                                <option value="N">Number</option>
                                <option value="T">Text</option>
                                <option value="O">Options</option>
                                <option value="L">LOV</option>
                            </select>
                        </div>
                    </div>
                    <div class="item form-group">
                        <label for="cou-name" class="col-md-3 label-align">Options(separated by commas)</label>

                        <div class="col-md-8">
                            <textarea class="form-control" rows="5" id="option-name" name="options" required></textarea>
                        </div>
                    </div>

                    <div class="item form-group">
                        <label for="cou-name" class="col-md-3 label-align">Select LOV Name</label>

                        <div class="col-md-8">
                            <select class="form-control" id="lov-name" name="lovName" required>
                                <option value="">Select LOV Name</option>
                                <option value="C">Models</option>
                            </select>
                        </div>
                    </div>
                    <div class="item form-group">
                        <label for="cou-name" class="col-md-3 label-align">Mapped Risk Column</label>

                        <div class="col-md-8">
                            <select class="form-control" id="risk-col-name" name="mappedRiskColumn">
                                <option value="">Select Mapped Risk Column</option>
                                <option value="riskId">Risk Id</option>
                                <option value="riskDesc">Risk Description</option>
                                <option value="riskPrem">Risk Premium</option>
                                <option value="riskValue">Risk Value</option>
                            </select>
                        </div>
                    </div>
                    <div class="item form-group">
                        <label for="brn-id" class="col-md-3 label-align">Mapped Prem. Item</label>

                        <div class="col-md-8">
                            <input type="hidden" id="map-sect-pk-name"/>
                            <input type="hidden" id="map-sect-pk-code" name="mappedSections"/>
                            <div id="map-sect-def" class="form-control"
                                 select2-url="<c:url value="/protected/setups/schedules/sclsections"/>">

                            </div>
                        </div>
                    </div>
                </form>
            </div>
            <div class="modal-footer">
                <button data-loading-text="Saving..." id="saveschedMapsBtn"
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