<table cellpadding="0" cellspacing="0" align="center"
       style="background-color:#F7F8FA;font-family: 'Microsoft YaHei UI', Arial, sans-serif;">
    <tbody>
    <tr>
        <td align="center">
            <div style="margin: 0 auto;width: 860px;padding: 22px 50px;border-radius: 4px;overflow: hidden;">
                <table width="100%" cellpadding="0" cellspacing="0" align="center">
                    <thead>
                    <tr>
                        <th align="left"
                            style="text-align:left;font-size:20px;font-weight: normal;border-bottom:1px solid #E9EBEE;">
                            <span style="position:relative;z-index:1;display:inline-block;background:transparent url(https://plugins.jetbrains.com/files/21811/337605/icon/pluginIcon.svg) 0 8px no-repeat;background-size:34px 34px;padding-left:42px;height:50px;line-height:48px;overflow:hidden;color: #5D5D5D;font-size: 20px;">mysoft-devtools</span>
                        </th>
                        <th align="right" style="border-bottom:1px solid #E9EBEE;">
                            <div style="font-weight:normal;height:50px;line-height:50px;font-size:16px;text-align:right;color:#888;">
                                用户错误日志反馈
                            </div>
                        </th>
                    </tr>
                    </thead>
                    <tbody>
                    <tr>
                        <td colspan="2">
                            <table width="100%" cellpadding="0" cellspacing="0" align="center"
                                   style="background-color: #ffffff;padding:0px 30px 20px 30px;border-top:2px solid #03A4FF;">

                                <tbody>
                                <tr>
                                    <th>
                                        <div style="margin:0;width:112px;height:36px;line-height:36px;padding-top:16px;color:#222;font-weight:normal;font-size:14px;text-align:left;overflow:hidden;">
                                            插件版本：
                                        </div>
                                    </th>
                                    <td>
                                        <div style="margin:0;width:668px;line-height:36px;padding-top:16px;font-size:14px;text-align:left;overflow:hidden;">
                                            <a href="${context.pluginUrl}"
                                               style="color:#34A6F8;text-decoration:none;"
                                               target="_blank">${context.pluginVersion}</a>
                                        </div>
                                    </td>
                                </tr>
                                <tr>
                                    <th>
                                        <div style="margin:0;width:112px;height:36px;line-height:36px;color:#222;font-weight:normal;font-size:14px;text-align:left;overflow:hidden;">
                                            IDE名称：
                                        </div>
                                    </th>
                                    <td>
                                        <div style="margin:0;width:668px;line-height:36px;color:#666;font-size:14px;text-align:left;overflow:hidden;">
                                            ${context.ideInfo}
                                        </div>
                                    </td>
                                </tr>
                                <tr>
                                    <th>
                                        <div style="margin:0;width:112px;height:36px;line-height:36px;color:#222;font-weight:normal;font-size:14px;text-align:left;overflow:hidden;">
                                            JVM版本：
                                        </div>
                                    </th>
                                    <td>
                                        <div style="margin:0;width:668px;line-height:36px;color:#666;font-size:14px;text-align:left;overflow:hidden;">
                                            ${context.jvmInfo}
                                        </div>
                                    </td>
                                </tr>
                                <tr>
                                    <th>
                                        <div style="margin:0;width:112px;height:36px;line-height:36px;color:#222;font-weight:normal;font-size:14px;text-align:left;overflow:hidden;">
                                            OS版本：
                                        </div>
                                    </th>
                                    <td>
                                        <div style="margin:0;width:668px;line-height:36px;color:#666;font-size:14px;text-align:left;overflow:hidden;">
                                            ${context.osInfo}
                                        </div>
                                    </td>
                                </tr>
                                <tr>
                                    <th>
                                        <div style="margin:0;width:112px;height:36px;line-height:36px;color:#222;font-weight:normal;font-size:14px;text-align:left;overflow:hidden;">
                                            用户信息：
                                        </div>
                                    </th>
                                    <td>
                                        <div style="margin:0;width:668px;line-height:36px;color:#666;font-size:14px;text-align:left;overflow:hidden;">
                                            ${context.osUser}
                                        </div>
                                    </td>
                                </tr>
                                <tr>
                                    <th>
                                        <div style="margin:0;width:112px;height:36px;line-height:36px;color:#222;font-weight:normal;font-size:14px;text-align:left;overflow:hidden;">
                                            国家/地区：
                                        </div>
                                    </th>
                                    <td>
                                        <div style="margin:0;width:668px;line-height:36px;color:#666;font-size:14px;text-align:left;overflow:hidden;">
                                            ${context.area}
                                        </div>
                                    </td>
                                </tr>
                                <tr>
                                    <th>
                                        <div style="margin:0;width:112px;height:36px;line-height:36px;color:#222;font-weight:normal;font-size:14px;text-align:left;overflow:hidden;">
                                            产品信息：
                                        </div>
                                    </th>
                                    <td>
                                        <div style="margin:0;width:668px;line-height:36px;color:#666;font-size:14px;text-align:left;overflow:hidden;">
                                            ${context.mysoftAppInfo}
                                        </div>
                                    </td>
                                </tr>
                                <tr>
                                    <th>
                                        <div style="margin:0;width:112px;height:36px;line-height:36px;color:#222;font-weight:normal;font-size:14px;text-align:left;overflow:hidden;">
                                            操作时间：
                                        </div>
                                    </th>
                                    <td>
                                        <div style="margin:0;width:668px;line-height:36px;color:#666;font-size:14px;text-align:left;overflow:hidden;">
                                            ${context.operateTime}
                                        </div>
                                    </td>
                                </tr>
                                <tr>
                                    <th>
                                        <div style="margin:0;width:112px;height:36px;line-height:36px;color:#222;font-weight:normal;font-size:14px;text-align:left;overflow:hidden;">
                                            附加信息：
                                        </div>
                                    </th>
                                    <td>
                                        <div style="margin:0;width:668px;line-height:36px;color:#666;font-size:14px;text-align:left;overflow:hidden;">
                                            ${context.additionalInfo}
                                        </div>
                                    </td>
                                </tr>

                                <tr>
                                    <th>
                                        <div style="margin:0;width:112px;height:36px;line-height:36px;color:#222;font-weight:normal;font-size:14px;text-align:left;overflow:hidden;">
                                            事件信息：
                                        </div>
                                    </th>
                                    <td>
                                        <div style="margin:0;width:668px;line-height:36px;color:#666;font-size:14px;text-align:left;overflow:hidden;">

                                        </div>
                                    </td>
                                </tr>
                                <tr>
                                    <td colspan="2">
                                        <div>
                                            <pre style="background-color: #5D5D5D;color: white; max-height: 600px;overflow-y: auto!important;">
                                                <code style="display: block;padding: 10px">
${context.stack}
                                                </code>
                                            </pre>
                                        </div>


                                    </td>
                                </tr>


                                <tr>
                                    <td colspan="2" align="left">
                                        <div style="padding-top:50px;margin:0;height:28px;line-height:28px;color:#222;font-size:14px;text-align:left;overflow:hidden;">
                                            明源云ERP-开发工具箱
                                        </div>
                                    </td>
                                </tr>
                                <tr>
                                    <td colspan="2" align="left">
                                        <div style="padding-bottom:20px;margin:0;height:28px;line-height:28px;color:#222;font-size:14px;text-align:left;overflow:hidden;">
                                            <span style="border-bottom:1px dashed #ccc;" t="5"
                                                  times=" 18:00">2023-05-19</span> 18:00
                                        </div>
                                    </td>
                                </tr>
                                </tbody>
                            </table>
                        </td>
                    </tr>
                    </tbody>
                    <tfoot>
                    <tr>
                        <td colspan="2" align="center" style="color:#777;font-size:12px;padding-top:20px;">
                            该邮件是由客户端上报，请安排处理！
                        </td>
                    </tr>
                    </tfoot>
                </table>
            </div>
        </td>
    </tr>
    </tbody>
</table>