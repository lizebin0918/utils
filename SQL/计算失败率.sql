SELECT ROUND(t1.fTotal/t2.total*100,2) as 'ʧ����' 
FROM (SELECT COUNT(IFNULL(`STATUS`,0)) AS fTotal FROM TXN_SSDS WHERE `STATUS` = 'F') t1,
(SELECT COUNT(`STATUS`) AS total FROM TXN_SSDS) t2;
SELECT IFNULL(COUNT(`STATUS`),0) AS fTotal FROM TXN_SSDS WHERE `STATUS` = 'F';
SELECT COUNT(`STATUS`) AS total FROM TXN_SSDS;