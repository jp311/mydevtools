/**
 *  ${context.data.name}代理类<#if (context.data.comment != null)??>：context.data.comment</#if>
 * @author ${context.author} ${context.date} 本代码由代码生成器自动生成，请不要手工调整
 */
define("${context.packageName}.${context.data.name}", function (require, exports, module) {
    var utility = require("utility");

    var service = ${context.data.pubServiceValue?replace("/","")};
    var functionCode = ${context.data.businessCode};
    var ns = {
        <#list context.data.methods as method>
        <#assign paramNames = []>
        <#assign dataParamNames = []>
        /**
         * ${method.comment}
         <#list method.parameters as parameter>
         * @param ${parameter.name} ${parameter.comment}
         <#assign paramNames = paramNames + [parameter.name]>
         <#assign dataParamNames = dataParamNames + [(parameter.name + ": " + parameter.name)]>
         </#list>
         */
        ${method.name}: function (${paramNames?join(", ")}) {
            return utility.api({
                service: service,
                functionCode: functionCode,
                action: "${method.name}",
                data: {
                    ${dataParamNames?join(",\n\t\t\t\t\t")}
                }
            });
        }<#if method?is_last == false>,</#if>
        </#list>
    }
})