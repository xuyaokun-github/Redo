CREATE TABLE `tbl_redo_task` (
                                 `id` int(11) NOT NULL AUTO_INCREMENT,
                                 `redo_task_id` varchar(64) CHARACTER SET utf8 NOT NULL COMMENT '补偿任务业务ID',
                                 `application_name` varchar(255) CHARACTER SET utf8 DEFAULT NULL COMMENT '微服务名',
                                 `max_attempts` int(11) DEFAULT NULL COMMENT '最大重试次数',
                                 `exec_times` int(11) NOT NULL DEFAULT '0' COMMENT '已执行次数',
                                 `try_forever` varchar(4) CHARACTER SET utf8 DEFAULT NULL COMMENT '是否永久尝试',
                                 `expired_date` datetime DEFAULT NULL COMMENT '过期时间',
                                 `req_param` varchar(2000) CHARACTER SET utf8 DEFAULT NULL COMMENT '请求参数',
                                 `create_time` datetime DEFAULT NULL COMMENT '创建时间',
                                 `query_time` datetime DEFAULT NULL COMMENT '查询时间',
                                 PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4;