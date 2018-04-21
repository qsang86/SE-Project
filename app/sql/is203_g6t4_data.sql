drop schema if exists SEProject;
create schema if not exists SEProject;
use SEProject;

#------------------------------------------------------------------------

#Users
create table users
(macaddress char(60) not null,
name varchar(60) not null,
password varchar(50) not null,
email char(100) not null,
gender char(1) not null,
constraint user_pk primary key(macaddress)
);

#Location
CREATE TABLE location (
    location_id INT NOT NULL,
    semantic_place VARCHAR(40) NOT NULL,
    CONSTRAINT location_pk PRIMARY KEY (location_id)
);

#User_Location
CREATE TABLE user_locations (
	time_stamp timestamp not null,
    macaddress char(40) not null,
    location_id int not null,
    CONSTRAINT user_locations_pk PRIMARY KEY (time_stamp,macaddress,location_id),
    CONSTRAINT user_locations_fk FOREIGN KEY (macaddress) references users(macaddress),
    CONSTRAINT user_locations_fk2 FOREIGN KEY (location_id) references location(location_id)
);

INSERT INTO users(`macaddress`, `name`, `password`, `email`, `gender`) VALUES 
('9ebf2ca29ccf68cb7b3a27358f725fbb09696cf5','admin','SETeam4','admin@sis.smu.edu.sg','M');

INSERT INTO users(`macaddress`, `name`, `password`, `email`, `gender`) VALUES 
('9ebf2ca29ccf68cb7b3a27358f725fbb09696cf6','test','SETeam4','test@sis.smu.edu.sg','M');



-- temporary location for TopKNextCompanion SQL -------------


-- finding specific user 
select t1.location_id as firstroom,t2.location_id as secondroom,start_time,end_time,timestampdiff(second,start_time,end_time) as duration from 
(select macaddress, location_id,time_stamp as start_time from user_locations where  time_stamp>=('2017-02-06 10:15:00' - interval 15 minute) 
and time_stamp <'2017-02-06 10:15:00' group by macaddress,location_id,time_stamp) as t1, 

(select macaddress, location_id,time_stamp as end_time from user_locations where 
time_stamp>=('2017-02-06 10:15:00' - interval 15 minute) and time_stamp <'2017-02-06 10:15:00' group by macaddress,location_id,time_stamp) as t2 
 
where start_time<end_time and t1.macaddress=t2.macaddress and t1.macaddress='6ebc97de2603d58257d1b804478369fdd9dd55a7' group by t1.macaddress,start_time order by t1.macaddress,start_time;


-- finding other users 
select t1.macaddress, t1.location_id as firstroom,t2.location_id as secondroom,start_time,end_time,timestampdiff(second,start_time,end_time) as duration from 
(select macaddress, location_id,time_stamp as start_time from user_locations where  time_stamp>=('2017-02-06 10:15:00' - interval 15 minute) 
and time_stamp <'2017-02-06 10:15:00' group by macaddress,location_id,time_stamp) as t1, 

(select macaddress, location_id,time_stamp as end_time from user_locations where 
time_stamp>=('2017-02-06 10:15:00' - interval 15 minute) and time_stamp <'2017-02-06 10:15:00' group by macaddress,location_id,time_stamp) as t2 
 
where start_time<end_time and t1.macaddress=t2.macaddress and macaddress<>'6ebc97de2603d58257d1b804478369fdd9dd55a7' group by macaddress,start_time order by t1.macaddress,start_time;


