# 简介
这是一套基于spring的demo，用来解决一下生产常规问题或者提供一些解决方案。
## 问题一：分库分表分区
分库：解决数据库服务器压力  
分表：解决单表压力  
分区：和分表类似，操作不同  
### 技术选型
* jdk 8  
* springboot 2.2.1  
* mybatis-plus 3.2.0  
* sharding-jdbc 3.0.0  
* swargger2 2.9.2  
## 问题二：redis-cache缓存
利用redis和cache把数据库查询的结果放入到缓存中，加快查询速度。  
详情查看[double-cache](https://github.com/wangtoye/double-cache)  