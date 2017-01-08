<#import "wf.main.layout.ftl" as u>

<@u.page>
<div class="container">
    <div class="row">
        <a href="/_wf_/wf/edit" class="btn btn-primary ">新建工作流</a>
        <a href="/_wf_/wf/reload" class="btn btn-primary ">Reload</a>
    <table class="table">
        <thead>
        <th>Name</th>
        <th>Display Name</th>
        <th>MetaEntity</th>
        <th>&nbsp;</th>
        </thead>
        <tbody>
        <#list workflowList as wf>
            <tr>
                <td>${wf.v("name")}</td>
                <td>${wf.v("displayName")}</td>
                <td>
                    ${meMap[wf.uuid].name}
                </td>
                <td>
                    <a href="/_wf_/wf/edit?uuid=${wf.uuid}" class="btn btn-primary btn-xs">Edit</a>
                    <a href="/_wf_/wf/build?uuid=${wf.uuid}" class="btn btn-primary btn-xs">Build</a>
                </td>
            </tr>
        </#list>
        </tbody>
    </table>
    </div>
</div>

</@u.page>