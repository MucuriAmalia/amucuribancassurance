<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<script type="text/javascript" src="<c:url value="/js/modules/utils/select2builder.js"/>"></script>
<script type="text/javascript" src="<c:url value="/libs/rivets/rivets.js"/>"></script>
<script type="text/javascript"
        src="<c:url value="/js/modules/products/product.js"/>"></script>
<div class="x_panel">
    <div class="x_title">
        <h2><i class="fa fa-bars"></i> Global Rates</h2>
        <ul class="nav navbar-right panel_toolbox">
            <li><a class="collapse-link"><i class="fa fa-chevron-up"></i></a>
            </li>
        </ul>
        <div class="clearfix"></div>
    </div>
    <div class="x_content">
        <form id="prg-grp-form" class="form-horizontal">
            <div class="item form-group form-required">
                <div class="col-md-6">
                    <label for="brn-id" class="col-md-5 label-align">
                        Select Product Group
                    </label>
                    <div class="col-md-7">
                        <input type="hidden" id="prg-id" rv-value="prggrp.prgCode"/>
                        <input type="hidden" id="prg-name">
                        <div id="prd-group" class="form-control"
                             select2-url="<c:url value="/protected/setups/products/selprodgroups"/>" >
                        </div>
                    </div>
                </div>
                <div class="col-md-2">
                </div>
            </div>
        </form>
    </div>
</div>
