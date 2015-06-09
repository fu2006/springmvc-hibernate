CREATE TABLE  ecoemail.userinfo  (   
   userid  int(10) unsigned NOT NULL AUTO_INCREMENT,   
   username  varchar(45) NOT NULL,   
   loginname  varchar(45) NOT NULL,   
   password  varchar(45) NOT NULL,   
   mobile  varchar(20),   
   other  varchar(45),   
   PRIMARY KEY ( userid )   
) ;
CREATE TABLE  ecoemail.message  (   
   id  int(10) unsigned NOT NULL AUTO_INCREMENT,   
   uid  varchar(45) NOT NULL,   
   subject  varchar(200),   
   senddate varchar(100),
   fromname  varchar(100),   
   iscontainattachment  tinyint(1),   
   replysign  tinyint(1),   
   content  longtext,   
   attachmentpath  varchar(100), 
   ispush  tinyint(1),
   PRIMARY KEY ( id )   
) ;