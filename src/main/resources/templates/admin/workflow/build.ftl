<#import "wf.main.layout.ftl" as u>

<@u.page>
<div class="container">
    <div class="row">
        <h4>${workflow.v("name")}</h4>
        
        <div class="panel panel-default">
            <div class="panel-heading">
                STATES
            </div>
            <div class="panel-body">
                <form action="/_wf_/wf/editState">
                    <input type="hidden" value="${workflow.uuid}" name="wfUuid">
                    <table class="table">
                        <thead>
                        <th>Name</th>
                        <th>Display Name</th>
                        <th>Type</th>
                        <th></th>
                        </thead>
                        <tbody>
                        <#list workflowStateList as state>
                            <tr>
                                <td>
                                    ${state.v("name")}
                                </td>
                                <td>
                                    ${state.v("displayName")}
                                </td>
                                <td>
                                    ${state.v("type")}
                                </td>
                            </tr>
                        </#list>
                        <tr>
                            <td>
                                <input name="workflowState_name" id="state_name" placeholder="Name"/>
                            </td>
                            <td>
                                <input name="workflowState_displayName" id="state_displayName" placeholder="Display Name"/>
                            </td>
                            <td>
                                <button class="btn btn-primary">Add</button>
                            </td>
                        </tr>
                        </tbody>
                    </table>
                </form>
            </div>
        </div>
        
        <div class="panel panel-default">
            <form action="/_wf_/wf/editAction">
                <input type="hidden" value="${workflow.uuid}" name="wfUuid">
                <div class="panel-heading">
                    ACTIONS
                </div>
                <div class="panel-body">
                    <table class="table">
                        <thead>
                        <th>Name</th>
                        <th>Display Name</th>
                        <th>From State</th>
                        <th>To State</th>
                        <th>Scripts</th>
                        </thead>
                        <tbody>
                        <#list workflowActionList as action>
                            <tr>
                                <td>${action.v("name")}</td>
                                <td>${action.v("displayName")}</td>
                                <td>
                                    <#assign stateName = action.v("fromState") />
                                    <#if stateName == "__ANY__">
                                        __ANY__
                                    <#else>
                                        <#assign state = action.sr(dbSession, "fromState") />
                                        ${state.v("name")}
                                    </#if>
                                    
                                </td>
                                <td>
                                    <#assign stateName = action.v("toState") />
                                    <#if stateName == "__SELF__">
                                        __SELF__
                                    <#else>
                                        <#assign state = action.sr(dbSession, "toState") />
                                        ${state.v("name")}
                                    </#if>
                                    
                                </td>
                                <td>
                                    <p>/wf_script/${workflow.v("name")}/${action.v("name")}_canDo</p>
                                    <p>/wf_script/${workflow.v("name")}/${action.v("name")}_before</p>
                                    <p>/wf_script/${workflow.v("name")}/${action.v("name")}_after</p>
                                </td>
                            </tr>
                        </#list>
                        <tr>
                            <td>
                                <input name="workflowAction_name" id="workflowAction_name" placeholder="Name"/>
                            </td>
                            <td>
                                <input name="workflowAction_displayName" id="workflowAction_displayName" placeholder="Display Name"/>
                            </td>
                            <td>
                                <select name="workflowAction_fromState" id="workflowAction_fromState">
                                    <option value="__ANY__">__ANY__</option>
                                    <#list workflowStateList as state>
                                    <option value="${state.uuid}">${state.v("name")}</option>
                                    </#list>
                                </select>
                            </td>
                            <td>
                                <select name="workflowAction_toState" id="workflowAction_toState">
                                    <option value="__SELF__">__SELF__</option>
                                    <#list workflowStateList as state>
                                        <option value="${state.uuid}">${state.v("name")}</option>
                                    </#list>
                                </select>
                            </td>
                            <td>
                                <button class="btn btn-primary btn-xs">Add</button>
                            </td>
                        </tr>
                        </tbody>
                    </table>
                </div>
            </form>
        </div>
        
    </div>
</div>
</@u.page>