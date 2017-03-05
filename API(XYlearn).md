# 客户端发送消息API

## 包名：NetEvent
## 类名：Client

## 重要通知：（老通知）

    //Client使用方法
    public static Client client = new Client();
    /*
     *...
     */
    client.start();
    //其他使用方法不变

### 上传下载功能已实现，上传文件都经过md5加密，存储在bucket根目录下。该功能一般用于图片的上传下载，上传文件不保留原文件名，上传一般文件的功能待完善
2017-3-2 更新方法：
* sendContent
* uploadFile
* uploadFiles
* downloadFile
* downloadFiles

2017-3-3 更新方法：
* createQuestion

-----

##### 原型：
public boolean registerRequest(String username, String password, String mailAddress, String signature)

##### 介绍:
注册

##### 参数：
|参数名  |参数类型|参数介绍|
|:-------:|:-----:|:-----:|
|username|String|用户名|
|password|String|密码|
|mailAddress|String|邮箱|
|signature|String|个性签名|

##### 返回值：
* boolean 注册信息是否正确

#### 服务器反馈消息:
* status : 成功状态
* information : 字符串消息

-----

##### 原型：
public void launchRequest(String username, String password) throws IOException

##### 介绍:
登录请求

##### 参数：
|参数名  |参数类型|参数介绍|
|:-------:|:-----:|:-----:|
|username|String|用户名|
|password|String|密码|

##### 返回值：无

#### 服务器反馈消息：
* status:成功状态
* information:说明信息（登录成功，登录失败原因之类）
* userMessage:用户信息

userMessage结构（以下同）：
* username:用户名
* good:得赞数
* questionNum:提问总数
* solvedQuestionNum:解决问题的数量
* bonus:点数
* signature:签名
* mail_address:邮箱地址
* pic_url:头像地址

-----

##### 原型：
public void logout() throws IOException

##### 介绍:
用户登出

##### 参数：无

##### 返回值：无

#### 服务器反馈消息:
无

-----

##### 原型：
public void sendContent(String contents,ArrayList<String> pictures,String questionID) throws IOException

##### 介绍:
发送消息，默认属性为DEFAULT，具体属性定义见下文

##### 参数：
|参数名  |参数类型|参数介绍|
|:-------:|:--------------:|:-----:|
|contents|String|发送的消息|
|pictures|ArrayList< String >|发送消息中图片的MD5码|
|questionID|String|目标房间号|

##### 返回值：无

#### 服务器反馈消息：
* sendContent消息体（与发送包相同）

sendContent结构:
* questionID:发送到的问题号
* content:发送内容
* time:发送时间
* user:发送者
* pictures:(map结构){发送的图片存储名,cos签名}
* success:是否成功
* isMyself:是否为自己

-----

##### 原型：
public void sendContent(
    String contents,
    ArrayList<String> pictures,
    String questionID，
    ClientSendMessage.CONTENT_MARK mark,
    long markID) throws IOException

##### 介绍:
发送消息

##### 参数：
|参数名  |参数类型|参数介绍|
|:-------:|:--------------:|:-----:|
|contents|String|发送的消息|
|pictures|ArrayList< String >|发送消息中图片的MD5码|
|questionID|String|目标房间号|
|markMap|Map <Integer, Long>|聊天记录属性表，key为CONTENT_MARK枚举值（在Client中定义）, value为属性对应的目标房间号|

* (PS : 属性对应房间号表示属性标注的对象房间，比如说属性为FURTHERASK (表示追问) 要追问的大一记录record id为1,则map的键为FURTHERASK、值为1 ) *

#### 枚举CONTENT_MARK:

    enum CONTENT_MARK {
        DEFAULT,    //默认属性
        DOUBTED,    //被质疑
        FURTHURASKED,      //被追问
        DOUBT,      //质疑
        FURTHERASK,    //追问
        ANONIMOUS,      //匿名发送
    }

##### 返回值：无

#### 服务器反馈消息：
* sendContent消息体（与发送包相同，但若属性含匿名，则sendContent的user字段为"匿名"）

sendContent结构:
* questionID:发送到的问题号
* content:发送内容
* time:发送时间
* user:发送者
* pictures:(map结构){发送的图片存储名,cos签名}
* success:是否成功
* isMyself:是否为自己
* mark:聊天记录属性
* markID:属性对应房间号

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

#### 服务器反馈消息：
* success:是否成功
（结构待完善）

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

#### 服务器反馈消息：
* success: 是否成功
(结构代完善)

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

#### 服务器反馈消息：
* allow:进入是否成功
* questionMessage:问题信息

questionMessage结构（下同）:
* id:问题号
* stem:提干
* stempic:题干图片
* addition:补充
* addtionpic:补充图片
* time:提问时间
* owner:提问者
* record:问答记录
* solved:解决与否
* good:赞数

record（问答记录）结构：
content:内容
time:发送时间
user:发送者
recordID:记录号
recordpic:记录图片MD5文件名列表
markMap:记录属性（匿名追问等）与目标房间号的映射，无目标房间，则与-1对应

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

#### 服务器反馈消息：
* exist:问题师傅存在
* questionMessage:问题信息(见上文)

-----

##### 原型：
public void requestQuestionList(ClientSendMessage.LIST_REFERENCE reference,ClientSendMessage.RANKORDER rankorder,int	questionNum) throws  IOException
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

#### 服务器反馈消息：
* num:返回的问题列表项数（小于等于请求的列表项数）
* (repeated)questionListMessage:问题列表项信息

questionListMessage结构(下同):
* questionID:问题号
* questionDescription:问题描述
* good:得赞数
* userNum:在线用户人数
* time:创建时间
* owner:拥有者
（待完善）

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

#### 服务器反馈消息：
* exist:用户是否存在
* userMessage:见上文

-----

##### 原型：(旧版，不推荐))
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

#### 服务器反馈消息：
* questionMessage:见上文

-----

##### 原型：（新版，推荐）
public void createQuestion(String stem, String addition, List<String> keywords, List<String> stempics, List<String> additionpics) throws IOException

##### 介绍:
创建问题（每次扣除3点数）

##### 参数：
|参数名  |参数类型|参数介绍|
|:-------:|:-----:|:-----:|
|stem|String|问题题干|
|addition|String|补充问题|
|keywords|ArrayList< String >|题干和补充的关键分词|
|stempics|List< String >|stem图片路径表|
|additionpics|List< String >|addition图片路径表|

##### 返回值：无

#### 服务器反馈消息：
* questionMessage:见上文

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

#### 服务器反馈消息：
* success:是否成功,不成功说明权限不足

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

#### 服务器反馈消息：
* (repeated)questionListMessage:见上

-----

##### 原型：
public void solveQuestion(long questionID) throws IOException

##### 介绍:
标志问题为已解决状态(仅问题提问者可以执行，管理员也可以删除问题)

##### 参数：
|参数名  |参数类型|参数介绍|
|:-------:|:-----:|:-----:|
|questionID|long|问题号|

##### 返回值：无

#### 服务器反馈消息:
1. success:成功与否
2. questionID:问题题号

-----

##### 原型：
public boolean uploadFile(String filePath) throws IOException

##### 介绍:
上传文件

##### 参数：
|参数名  |参数类型|参数介绍|
|:-------:|:-----:|:-----:|
|filePath|String|文件名|

##### 返回值：无

#### 服务器反馈消息:
无需关心

-----

##### 原型：
public boolean uploadFiles(Iterable<String> filePaths) throws IOException

##### 介绍:
批量上传文件

##### 参数：
|参数名  |参数类型|参数介绍|
|:-------:|:-----:|:-----:|
|filePaths|Iterable< String >|文件名的序列|

##### 返回值：无

#### 服务器反馈消息:
无需关心

-----

##### 原型：
public void downloadFile(String filename) throws IOException

##### 介绍:
下载文件

##### 参数：
|参数名  |参数类型|参数介绍|
|:-------:|:-----:|:-----:|
|filename|String|文件名|

##### 返回值：无

#### 服务器反馈消息:
无需关心

-----

##### 原型：
public void downloadFiles(Iterable<String> filenames) throws IOException

##### 介绍:
批量上传文件

##### 参数：
|参数名  |参数类型|参数介绍|
|:-------:|:-----:|:-----:|
|filenames|Iterable< String >|文件名序列|

##### 返回值：无

#### 服务器反馈消息:
无需关心

-----
