CREATE TABLE `tbl_database_lock` (
                                     `id` bigint(20) NOT NULL AUTO_INCREMENT,
                                     `resource` varchar(500) NOT NULL COMMENT '锁定的资源，可以是方法名或者业务唯一标志',
                                     `description` varchar(1000) NOT NULL DEFAULT '' COMMENT '业务场景描述',
                                     `request_id` varchar(64) DEFAULT NULL COMMENT '唯一标识',
                                     `request_time` datetime DEFAULT NULL COMMENT '抢锁时间',
                                     PRIMARY KEY (`id`),
                                     UNIQUE KEY `uiq_idx_resource` (`resource`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COMMENT='数据库分布式悲观锁表';

INSERT INTO tbl_database_lock (resource,description,request_id,request_time)
VALUES ('cn.com.kun.component.redo.core.RedoManager.findAndRedo:kunsharedemo27','','b7cc038d-7f5e-48e7-8625-05fbe3a546a2','2024-11-20 10:20:19');