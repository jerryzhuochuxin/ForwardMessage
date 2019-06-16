# ForwardMessage
# 关于阿里云物联网平台在服务端订阅sdk只提供Java,.net的解决方案

这是使用java编写的一个中间层,使用者只用搭建一个post方法的url,将从阿里云的得到的一些参数与搭建的post方法url写进message.jar的myProp.properties文件,然后运行这个message.jar就可以了,同时使用了log4j作为日志,默认产生的日志在./MessageForward目录中,若想修改日志配置,直接打开修改log4j.properties文件亦可



# 使用方法
直接下载message.jar
用zip格式打开,编辑里面的myProp.properties文件
最后运行 java -jar message.jar



# 关于一些阿里云需要的参数
accessKey: 您的账号AccessKey ID,登录阿里云控制台，将光标移至账号头像上，然后单击accesskeys，跳转至用户信息管理页，即可获取。

uid: 您的账号ID。用主账号登录阿里云控制台，单击账号头像，跳转至账号管理控制台，即可获取账号UID。

regionId: 您的物联网平台服务所在地域代码。在物联网平台控制台页，右上方即可查看地域（Region）。RegionId 的表达方法，请参见文档地域和可用区。

url: 您需要转发到的url
