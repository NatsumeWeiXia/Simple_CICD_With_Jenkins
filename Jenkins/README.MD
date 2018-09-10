
jenkinsfile

汉化并调整时间日期格式的Jenkins邮件模板


- 使用模板发送邮件

Email Extension Plugin 插件提供邮件模板功能，可以让我们以一种固定格式发送邮件

插件自带模板可以在 JENKINS_HOME/plugins/email-ext/WEB-INF/lib 下解压 email-ext.jar 后进入 hudson/plugins/emailext/templates 目录下查看，以 .jelly 为文件后缀。

我们可以对文件进行修改后，生成符合自身需求的模板，放置在 JENKINS_HOME/email-templates下供自己使用

- 使用方法：

<pre><code>emailext(
             subject: '${PROJECT_NAME} - Build # ${BUILD_NUMBER}!',
             body:'${JELLY_SCRIPT,template="mail-failure"}',
             to: "songyang@XXXXXX.com",
             recipientProviders: [[$class: 'DevelopersRecipientProvider']]
        )</code></pre>
        
其中 body:'${JELLY_SCRIPT,template="mail-failure"}' 语句中的 template= 后填写你想试用的模板文件名
