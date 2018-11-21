--编写事件的维度类相关


--1. 变相相关的udf函数
create function convert_data as 'com.bigdata.analystic.hive.DateDimensionUdf' using jar
 'hdfs://192.168.198.60:9000/ods/udf/jars/hadoop_cdh-1.0.jar';

create function convert_event as 'com.bigdata.analystic.hive.EventDimensionUdf' using
jar 'hdfs://192.168.198.60:9000/ods/udf/jars/hadoop_cdh-1.0.jar';

create function convert_platform as 'com.bigdata.analystic.hive.PlatformDimensionUdf'
using jar 'hdfs://192.168.198.60:9000/ods/udf/jars/hadoop_cdh-1.0.jar';

2. 创建元数据对应的临时表:
create external table if not exists logs_tmp(
ver string,s_time string,en string,u_ud string,
u_mid string,u_sd string,c_time string,l string,
b_iev string,b_rst string,p_url string,p_ref string,
tt string,pl string,ip String,oid String,`on` String,
cua String,cut String,pt String,ca String,ac String,
kv_ String,du String,browserName String,browserVersion String,
osName String,osVersion String,country String,province String,
city string
)
partitioned by(month string,day string)
;

加载数据:
load data inpath '/ods/11/11/*' into table logs_tmp partition(month="11",day="11");

3. 创建hive表：
create external table if not exists logs(
ver string,s_time string,en string,u_ud string,
u_mid string,u_sd string,c_time string,l string,
b_iev string,b_rst string,p_url string,p_ref string,
tt string,pl string,ip String,oid String,`on` String,
cua String,cut String,pt String,ca String,ac String,
kv_ String,du String,browserName String,browserVersion String,
osName String,osVersion String,country String,province String,
city string
)
partitioned by(month string,day string)
stored as orc
;

4. 导入数据
from logs_tmp
insert into logs partition(month=11,day=11)
select
ver,s_time,en,u_ud,u_mid,
u_sd,c_time,l,b_iev,b_rst,
p_url,p_ref,tt,pl,ip,
oid,`on`,cua,cut,pt,
ca,ac,kv_,du,browserName,
browserVersion,osName,osVersion,
country,province,city
where month = 11 and day =11
;

编写hql语句:

结果表:
在hive中创建和mysql最终结果一样的临时表：

CREATE TABLE if not exists `stats_event` (
  `platform_dimension_id` int,
  `date_dimension_id` int,
  `event_dimension_id` int,
  `times` int,
  `created` String
)
;

语句:
设置数据负载均衡，其设置方法为设置hive.groupby.skewindata参数
set hive.groupby.skewindata=true;

select
from_unixtime(cast(l.s_time/1000 as bigint),"yyyy-MM-dd") as dt,
l.pl,
l.ca,
l.ac,
count(*) as ct
from logs l
where month = 11 and day = 11
group by from_unixtime(cast(l.s_time/1000 as bigint),"yyyy-MM-dd"),l.pl,l.ca,l.ac
;

结果:
2018-11-11      java    订单事件        订单超时过期    16
2018-11-11      java_server     null    null    3
2018-11-11      java_server     订单事件        订单超时过期    3
2018-11-11      website event的category名称     event的action名称       13
2018-11-11      website null    null    13
2018-11-11      website 订单事件        下单操作        9
2018-11-11      website 订单事件        订单超时过期    25


备注:cast(l.s_time/1000 as bigint)--->用cast()函数,将s_time的类型转换成bigint类型
----------------思路:--------------------
select
l.s_time,
l.pl,
l.ca,
l.ac
from logs l
where month = 11 and day = 11
;

查询结果:
1541924751921   java_server     null    null
1541924731612   java_server     null    null
1541924731008   website null    null
1541924731005   website null    null
--------------------------------------------------



编写sqoop语句: