# mma-microservice
尚医通即为网上预约挂号系统，网上预约挂号是近年来开展的一项便民就医服务，旨在缓解看病难、挂号难的就医难题，许多患者为看一次病要跑很多次医院，最终还不一定能保证看得上医生。网上预约挂号全面提供的预约挂号业务从根本上解决了这一就医难题。原型为北京114预约挂号系统，资料来源是bilibili尚硅谷的课程。
技术栈：
后端
SpringBoot
SpringCloud
MyBatis-Plus：持久层框架
Redis：内存缓存
RabbitMQ：消息中间件
HTTPClient: Http协议客户端
Swagger2：Api接口文档工具
Nginx：负载均衡 （前期使用nginx，后期使用spring-gateway网管替代）
Mysql：关系型数据库 5.7版本
MongoDB：面向文档的NoSQL数据库
Docker ：容器技术
Git：代码管理工具
前端
前端医院后台管理系统和尚医通统一挂号平台
Vue+nuxt：vue-cli 脚手架
Node.js： JavaScript 运行环境
Axios：Axios 是一个基于 promise 的 HTTP 库
NPM：包管理器
前端有两个，一个是医院后台管理系统，另一个是尚医通统一挂号平台
业务流程
image

服务架构
image

