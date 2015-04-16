select count(*) from INFO_TXN_LOG_MAPPING as t 
-- yyyyMMddHHmmss
where str_to_date(CONCAT(create_date, CREATE_TIME),'%Y%m%d%H%i%s') 
-- BETWEEN DATE_SUB(sysdate(),INTERVAL 48 HOUR) AND sysdate();
-- BETWEEN DATE_SUB(sysdate(),INTERVAL 1 DAY) AND sysdate();
