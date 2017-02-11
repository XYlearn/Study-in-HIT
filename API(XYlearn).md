# 客户端发送消息API

## 包名：NetEvent
## 类名：Client

##### 原型：
public void launchRequest(String username, String password) throws IOException

##### 介绍:
登录请求

##### 参数：
|参数名  |参数类型|参数介绍|
|:-------:|:-----:|:-----:|
|username|String|用户名|
|password|String|经过加密的密码|

##### 返回值：无

-----

##### 原型：
public void logout() throws IOException

##### 介绍:
用户登出

##### 参数：无

##### 返回值：无

-----

##### 原型：
public void sendContent(String contents,ArrayList<String> pictures,String questionID) throws IOException

##### 介绍:
发送消息

##### 参数：
|参数名  |参数类型|参数介绍|
|:-------:|:--------------:|:-----:|
|contents|String|发送的消息|
|pictures|ArrayList< String >|发送消息中图片的MD5码|

##### 返回值：无

-----

##### 原型：
public void goodUser(String user) throws IOException

##### 介绍:
赞用户请求

##### 参数：
|参数名  |参数类型|参数介绍|
|:-------:|:-----:|:-----:|
|user|String|要赞用户的用户名，不能是自己|

##### 返回值：无

-----

##### 原型：
public void goodQuestion(String questionID) throws IOException

##### 介绍:
赞问题请求

##### 参数：
|参数名  |参数类型|参数介绍|
|:-------:|:-----:|:-----:|
|questionID|String|问题号|

##### 返回值：无

-----

##### 原型：
public void enterQuestion(String questionID) throws IOException

##### 介绍:
进入房间请求

##### 参数：
|参数名  |参数类型|参数介绍|
|:-------:|:-----:|:-----:|
|questionID|String|问题号|

##### 返回值：无

-----

##### 原型：
public void requestQuestionInfo(String questionID) throws IOException

##### 介绍:
请求问题信息
将包含以下信息(可以继续增加):

|变量名|变量类型|变量介绍|
|:-------:|:--------:|:--------:|
|stem|String|题目题干|
|addition|String|问题补充|
|time|String|提问时间|
|owner|String|提问者|
|record|List< Record >|答疑记录|
|solved|boolean|解决状态|
|good|int|得赞次数|

##### 参数：
|参数名  |参数类型|参数介绍|
|:-------:|:-----:|:-----:|
|questionID|String|问题号|

##### 返回值：无

-----

##### 原型：
public void requestQuestionList(ClientSendMessage.LIST_REFERENCE reference,
      ClientSendMessage.RANKORDER rankorder,
      int	questionNum) throws  IOException
##### 介绍:
请求获得问题列表
列表项中将包含以下信息(可以继续增加)：

|变量名|变量类型|变量介绍|
|:------:|:-------:|:-------:|
|questionID|long|问题号|
|questionDescription|string|问题描述(应该是题干)|
|good|int|得赞数目|
|userNum|int|在问题房间中的用户人数|
|time|String|最后发言时间|
|owner|String|提问者|

##### 参数：
|参数名  |参数类型|参数介绍|
|:-------:|:-----:|:-----:|
|reference|LIST_REFERENCE|获取问题列表的依据的量如点赞数(具体的定义在ClientSendMessage类中)|
|rankorder|RANKORDER|排列的顺序，有升序和降序两种|
|questionNum|int|请求获取的问题列表项数目|

##### 返回值：无

-----

##### 原型：
public void requestUserInfo(String user) throws IOException

##### 介绍:
请求用户信息
<br> 将包含以下内容： </br>

|变量名  |变量类型|变量介绍|
|:-------:|:-----:|:-----:|
|username|String|用户名|
|good|int|得赞数目|
|questionNum|int|提问次数|
|solvedQuestionNum|int|解决问题次数|
|bonus|int|点数|
|signature|String|用户签名|
|mail_address|String|邮箱名|
|pic_url|String|头像url|

##### 参数：
|参数名  |参数类型|参数介绍|
|:-------:|:-----:|:-----:|
|user|String|用户名|

##### 返回值：无

-----

##### 原型：
public void createQuestion(String stem, String addition, ArrayList<String> keywords) throws IOException

##### 介绍:
创建问题（每次扣除3点数）

##### 参数：
|参数名  |参数类型|参数介绍|
|:-------:|:-----:|:-----:|
|stem|String|问题题干|
|addition|String|补充问题|
|keywords|ArrayList< String >|题干和补充的关键分词|

##### 返回值：无

-----

##### 原型：
public void abandonQuestion(long questionID) throws IOException

##### 介绍:
删除问题（表面上只有提问者可以删除）

##### 参数：
|参数名  |参数类型|参数介绍|
|:-------:|:-----:|:-----:|
|questionID|long|问题号|

##### 返回值：无

-----

##### 原型：
public void searchInformation(ArrayList< String > keywords) throws IOException

##### 介绍:
搜索问题

##### 参数：
|参数名  |参数类型|参数介绍|
|:-------:|:-----:|:-----:|
|keywords|ArrayList< String >|问题题干和补充的关键分词|

##### 返回值：无

-----
