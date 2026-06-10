-- 蚁心安巡初始化数据
SET NAMES utf8mb4;
SET CHARACTER SET utf8mb4;

USE yixin_patrol;

-- 初始化组织机构数据
INSERT INTO `sys_organization` (`id`, `name`, `code`, `parent_id`, `org_type`, `sort_order`, `status`) VALUES
(1, '市教育局', 'EDU001', 0, 1, 1, 1),
(2, '第一中学', 'SCH001', 1, 2, 1, 1),
(3, '第二中学', 'SCH002', 1, 2, 2, 1),
(4, '实验小学', 'SCH003', 1, 2, 3, 1),
(5, '第一中学-安全部', 'DEPT001', 2, 3, 1, 1),
(6, '第一中学-后勤部', 'DEPT002', 2, 3, 2, 1),
(7, '第二中学-安全部', 'DEPT003', 3, 3, 1, 1),
(8, '实验小学-安全部', 'DEPT004', 4, 3, 1, 1);

-- 初始化用户数据（仓库中不保存明文密码，仅保存 SHA-256 摘要后的 BCrypt 值）
INSERT INTO `sys_user` (`id`, `username`, `password`, `real_name`, `phone`, `org_id`, `role_type`, `status`) VALUES
(1, 'admin', '$2b$12$XHwwIPcANX.TYfXbfaA04.ttc/6e2esTOmjjKoAj11F3iVIPFj45y', '系统管理员', '13800000001', 1, 1, 1),
(2, 'school1_admin', '$2b$12$XHwwIPcANX.TYfXbfaA04.ttc/6e2esTOmjjKoAj11F3iVIPFj45y', '张校长', '13800000002', 2, 2, 1),
(3, 'school2_admin', '$2b$12$XHwwIPcANX.TYfXbfaA04.ttc/6e2esTOmjjKoAj11F3iVIPFj45y', '李校长', '13800000003', 3, 2, 1),
(4, 'inspector1', '$2b$12$XHwwIPcANX.TYfXbfaA04.ttc/6e2esTOmjjKoAj11F3iVIPFj45y', '王安全员', '13800000004', 5, 3, 1),
(5, 'inspector2', '$2b$12$XHwwIPcANX.TYfXbfaA04.ttc/6e2esTOmjjKoAj11F3iVIPFj45y', '刘安全员', '13800000005', 6, 3, 1),
(6, 'inspector3', '$2b$12$XHwwIPcANX.TYfXbfaA04.ttc/6e2esTOmjjKoAj11F3iVIPFj45y', '陈安全员', '13800000006', 7, 3, 1);

-- 初始化检查模板
INSERT INTO `task_template` (`id`, `name`, `description`, `category`, `check_items`, `create_user_id`, `status`) VALUES
(1, '日常安全检查', '学校日常安全检查模板', '日常检查', 
'[{"id":1,"name":"消防设施检查","required":true,"description":"检查灭火器、消防栓等设施是否完好"},{"id":2,"name":"电气设备检查","required":true,"description":"检查用电设备、线路是否安全"},{"id":3,"name":"门窗检查","required":true,"description":"检查门窗是否完好、锁具是否正常"},{"id":4,"name":"监控设备检查","required":false,"description":"检查监控摄像头是否正常运行"}]', 
1, 1),
(2, '月度综合安全检查', '学校每月综合安全检查模板', '综合检查',
'[{"id":1,"name":"消防安全","required":true,"description":"全面检查消防设施及消防通道"},{"id":2,"name":"食品安全","required":true,"description":"检查食堂卫生及食品存储"},{"id":3,"name":"校舍安全","required":true,"description":"检查建筑物结构安全"},{"id":4,"name":"交通安全","required":true,"description":"检查校园交通设施"},{"id":5,"name":"实验室安全","required":false,"description":"检查实验室危险品存放"}]',
1, 1),
(3, '节假日前安全检查', '重大节假日前安全排查模板', '专项检查',
'[{"id":1,"name":"水电气关闭","required":true,"description":"检查非必要水电气是否关闭"},{"id":2,"name":"门窗封锁","required":true,"description":"检查所有门窗是否锁好"},{"id":3,"name":"值班安排","required":true,"description":"确认值班人员安排"},{"id":4,"name":"应急预案","required":true,"description":"确认应急联系方式畅通"}]',
1, 1);

-- 初始化示例任务
INSERT INTO `patrol_task` (`id`, `title`, `description`, `template_id`, `org_id`, `executor_id`, `assigner_id`, `priority`, `status`, `deadline`) VALUES
(1, '2024年春季开学安全检查', '开学前全面安全检查，确保校园安全', 2, 2, 4, 2, 3, 0, DATE_ADD(NOW(), INTERVAL 7 DAY)),
(2, '消防设施日常巡检', '检查教学楼消防设施完好情况', 1, 2, 4, 2, 2, 1, DATE_ADD(NOW(), INTERVAL 3 DAY)),
(3, '食堂卫生检查', '检查学校食堂卫生及食品安全', 2, 3, 6, 3, 2, 0, DATE_ADD(NOW(), INTERVAL 5 DAY)),
(4, '实验室安全专项检查', '化学实验室危险品存放检查', 3, 2, 5, 2, 3, 2, DATE_ADD(NOW(), INTERVAL 1 DAY)),
(5, '校园监控系统检查', '检查监控摄像头运行状态', 1, 3, 6, 3, 1, 3, DATE_SUB(NOW(), INTERVAL 2 DAY));

-- 初始化示例上报记录
INSERT INTO `task_report` (`id`, `task_id`, `report_content`, `check_result`, `location`, `report_user_id`, `review_status`) VALUES
(1, 4, '已完成实验室安全检查，发现部分问题需要整改', 
'[{"itemId":1,"itemName":"消防安全","passed":true,"remark":"消防设施完好"},{"itemId":2,"itemName":"危险品存放","passed":false,"remark":"发现部分化学品标签不清"}]',
'化学实验楼201室', 5, 0),
(2, 5, '监控系统运行正常，所有摄像头画面清晰',
'[{"itemId":1,"itemName":"摄像头检查","passed":true,"remark":"全部32个摄像头正常"},{"itemId":2,"itemName":"录像存储","passed":true,"remark":"存储空间充足"}]',
'监控中心', 6, 1);
