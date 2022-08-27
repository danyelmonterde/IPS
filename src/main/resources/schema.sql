
DROP TABLE IF EXISTS balance;
DROP TABLE IF EXISTS barcode;
DROP TABLE IF EXISTS customer;
DROP TABLE IF EXISTS deleteditems;
DROP TABLE IF EXISTS history;
DROP TABLE IF EXISTS inventorytype;
DROP TABLE IF EXISTS item;
DROP TABLE IF EXISTS itemcategory;
DROP TABLE IF EXISTS purchaseorder;
DROP TABLE IF EXISTS reportnames;
DROP TABLE IF EXISTS requisitionissueslip;
DROP TABLE IF EXISTS requisitionissueslipsignatories;
DROP TABLE IF EXISTS risfields;
DROP TABLE IF EXISTS ristype;
DROP TABLE IF EXISTS ristypefields;
DROP TABLE IF EXISTS ristypenames;
DROP TABLE IF EXISTS sales;
DROP TABLE IF EXISTS signatory;
DROP TABLE IF EXISTS stockadjustment;
DROP TABLE IF EXISTS supplier;
DROP TABLE IF EXISTS unit;
DROP TABLE IF EXISTS user;

CREATE TABLE balance (
  id int(11) NOT NULL AUTO_INCREMENT,
  beginbalance double NOT NULL,
  endbalance double NOT NULL,
  month varchar(45) NOT NULL,
  year varchar(45) NOT NULL,
  datecreated varchar(45) NOT NULL,
  category varchar(45) NOT NULL,
  PRIMARY KEY (id)
) ENGINE=InnoDB AUTO_INCREMENT=32 DEFAULT CHARSET=latin1;

CREATE TABLE barcode (
  id int(11) NOT NULL AUTO_INCREMENT,
  bar_code_path varchar(255) DEFAULT NULL,
  date_created datetime DEFAULT NULL,
  sku int(11) DEFAULT NULL,
  PRIMARY KEY (id)
) ENGINE=InnoDB AUTO_INCREMENT=328 DEFAULT CHARSET=latin1;

CREATE TABLE customer (
  id int(11) NOT NULL AUTO_INCREMENT,
  first_name varchar(45) DEFAULT NULL,
  middle_name varchar(45) DEFAULT NULL,
  last_name varchar(45) DEFAULT NULL,
  cluster varchar(45) DEFAULT NULL,
  PRIMARY KEY (id)
) ENGINE=InnoDB AUTO_INCREMENT=38 DEFAULT CHARSET=latin1;

CREATE TABLE deleteditems (
  sku int(11) NOT NULL,
  item_name varchar(50) DEFAULT NULL,
  quantity int(11) DEFAULT '0',
  cost double DEFAULT '0',
  in_stock int(11) DEFAULT '0',
  low_stock int(11) DEFAULT '0',
  tag varchar(45) DEFAULT NULL,
  unit varchar(45) DEFAULT NULL,
  item_category varchar(50) DEFAULT NULL,
  PRIMARY KEY (sku)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

CREATE TABLE history (
  id int(11) NOT NULL AUTO_INCREMENT,
  date datetime DEFAULT NULL,
  item_name varchar(50) DEFAULT NULL,
  item_category varchar(50) DEFAULT NULL,
  reason varchar(50) DEFAULT NULL,
  adjustment int(11) NOT NULL DEFAULT '0',
  stock_after int(11) NOT NULL DEFAULT '0',
  stock_before int(11) NOT NULL DEFAULT '0',
  updated_by varchar(200) NOT NULL,
  PRIMARY KEY (id)
) ENGINE=InnoDB AUTO_INCREMENT=205 DEFAULT CHARSET=latin1;

CREATE TABLE inventorytype (
  id int(11) NOT NULL AUTO_INCREMENT,
  inventory_type varchar(100) NOT NULL,
  PRIMARY KEY (id),
  UNIQUE KEY inventory_type_UNIQUE (inventory_type)
) ENGINE=InnoDB AUTO_INCREMENT=10011 DEFAULT CHARSET=latin1;

CREATE TABLE item (
  sku int(11) NOT NULL AUTO_INCREMENT,
  item_name varchar(50) NOT NULL,
  unit varchar(45) NOT NULL,
  quantity int(11) NOT NULL DEFAULT '0',
  cost double NOT NULL DEFAULT '0',
  in_stock int(11) NOT NULL DEFAULT '0',
  low_stock int(11) NOT NULL DEFAULT '0',
  tag varchar(45) DEFAULT NULL,
  item_category varchar(50) NOT NULL,
  PRIMARY KEY (sku),
  UNIQUE KEY item_name_UNIQUE (item_name)
) ENGINE=InnoDB AUTO_INCREMENT=328 DEFAULT CHARSET=latin1;

CREATE TABLE itemcategory (
  id int(11) NOT NULL AUTO_INCREMENT,
  category_name varchar(100) DEFAULT NULL,
  PRIMARY KEY (id),
  UNIQUE KEY category_name_UNIQUE (category_name)
) ENGINE=InnoDB AUTO_INCREMENT=10011 DEFAULT CHARSET=latin1;

CREATE TABLE purchaseorder (
  id int(11) NOT NULL AUTO_INCREMENT,
  item_name varchar(50) DEFAULT NULL,
  item_category varchar(50) DEFAULT NULL,
  stock_before int(11) NOT NULL DEFAULT '0',
  in_stock int(11) NOT NULL DEFAULT '0',
  quantity int(11) NOT NULL DEFAULT '0',
  purchase_cost double NOT NULL DEFAULT '0',
  sku int(11) NOT NULL DEFAULT '0',
  amount double NOT NULL DEFAULT '0',
  datecreated datetime NOT NULL,
  PRIMARY KEY (id)
) ENGINE=InnoDB AUTO_INCREMENT=21 DEFAULT CHARSET=latin1;

CREATE TABLE reportnames (
  id int(11) NOT NULL AUTO_INCREMENT,
  name varchar(200) DEFAULT NULL,
  inventorytype varchar(45) DEFAULT NULL,
  description varchar(100) DEFAULT NULL,
  risTypeName varchar(100) DEFAULT NULL,
  jrxmlReportFileName varchar(100) DEFAULT NULL,
  PRIMARY KEY (id)
) ENGINE=InnoDB AUTO_INCREMENT=25 DEFAULT CHARSET=latin1;

CREATE TABLE requisitionissueslip (
  id int(11) NOT NULL AUTO_INCREMENT,
  ristype varchar(45) DEFAULT NULL,
  control_number varchar(45) NOT NULL,
  is_customer_new tinyint(1) DEFAULT NULL,
  customer_name varchar(255) DEFAULT NULL,
  requisition_and_issue_slip_number varchar(45) DEFAULT NULL,
  purpose varchar(255) DEFAULT NULL,
  requested_by varchar(45) DEFAULT NULL,
  designation varchar(45) DEFAULT NULL,
  division varchar(45) DEFAULT NULL,
  office varchar(45) DEFAULT NULL,
  unit varchar(45) DEFAULT NULL,
  responsibility_center_code varchar(45) DEFAULT NULL,
  date_transacted varchar(45) DEFAULT NULL,
  PRIMARY KEY (id),
  UNIQUE KEY control_number_UNIQUE (control_number)
) ENGINE=InnoDB AUTO_INCREMENT=56 DEFAULT CHARSET=latin1;

CREATE TABLE requisitionissueslipsignatories (
  id int(11) NOT NULL AUTO_INCREMENT,
  requested_by varchar(45) DEFAULT NULL,
  designation varchar(45) DEFAULT NULL,
  division varchar(45) DEFAULT NULL,
  unit varchar(45) DEFAULT NULL,
  PRIMARY KEY (id)
) ENGINE=InnoDB AUTO_INCREMENT=81 DEFAULT CHARSET=latin1;

CREATE TABLE risfields (
  id int(11) NOT NULL AUTO_INCREMENT,
  ris_field varchar(45) NOT NULL,
  PRIMARY KEY (id),
  UNIQUE KEY ris_field_UNIQUE (ris_field)
) ENGINE=InnoDB AUTO_INCREMENT=20 DEFAULT CHARSET=latin1 COMMENT='list of all possible ris fields';

CREATE TABLE ristype (
  id int(11) NOT NULL AUTO_INCREMENT,
  inventory_type varchar(45) DEFAULT NULL,
  ristype varchar(45) DEFAULT NULL,
  risfield varchar(45) DEFAULT NULL,
  risfieldtype varchar(45) DEFAULT NULL COMMENT 'FIELD DATA TYPE : TEXT, DATE, NUMBER, MONEY',
  risname varchar(100) DEFAULT NULL,
  PRIMARY KEY (id)
) ENGINE=InnoDB AUTO_INCREMENT=45 DEFAULT CHARSET=latin1 COMMENT='example of ristype. construction-saleofmaterials\nor watermeter-newconnection';

CREATE TABLE ristypefields (
  id int(11) NOT NULL AUTO_INCREMENT,
  ristype varchar(45) DEFAULT NULL COMMENT 'Type of RIS\n(ex. New Connection, Sale of Materials, Repairs and Maintenance, Construction Work in Progress)',
  ris_field varchar(45) DEFAULT NULL,
  ris_field_type varchar(45) DEFAULT NULL,
  ris_value varchar(45) DEFAULT NULL,
  control_number varchar(45) NOT NULL,
  PRIMARY KEY (id)
) ENGINE=InnoDB AUTO_INCREMENT=158 DEFAULT CHARSET=latin1 COMMENT='Extra RIS Fields and its values\n(example for O.R number)\n1.ristype: Sales of Material\n   ris_field: or_number\n   ris_value: 023-35234\n\n';

CREATE TABLE ristypenames (
  id int(11) NOT NULL AUTO_INCREMENT,
  name varchar(100) DEFAULT NULL,
  inventorytype varchar(45) DEFAULT NULL,
  risname varchar(100) DEFAULT NULL,
  PRIMARY KEY (id),
  UNIQUE KEY name_UNIQUE (name)
) ENGINE=InnoDB AUTO_INCREMENT=17 DEFAULT CHARSET=latin1;

CREATE TABLE sales (
  id int(11) NOT NULL AUTO_INCREMENT,
  control_number varchar(45) NOT NULL,
  total_sales double NOT NULL DEFAULT '0',
  total_cost double NOT NULL DEFAULT '0',
  income double DEFAULT NULL,
  date_transacted varchar(45) DEFAULT NULL,
  PRIMARY KEY (id),
  UNIQUE KEY controlNumber_UNIQUE (control_number)
) ENGINE=InnoDB AUTO_INCREMENT=53 DEFAULT CHARSET=latin1;

CREATE TABLE signatory (
  id int(11) NOT NULL AUTO_INCREMENT,
  signatory varchar(100) DEFAULT NULL,
  position varchar(100) DEFAULT NULL,
  role varchar(45) DEFAULT NULL,
  reportid int(11) DEFAULT NULL,
  PRIMARY KEY (id)
) ENGINE=InnoDB AUTO_INCREMENT=77 DEFAULT CHARSET=latin1;

CREATE TABLE stockadjustment (
  id int(11) NOT NULL AUTO_INCREMENT,
  reason varchar(50) DEFAULT NULL,
  sku int(11) NOT NULL DEFAULT '0',
  in_stock int(11) NOT NULL DEFAULT '0',
  add_stock int(11) NOT NULL DEFAULT '0',
  cost double NOT NULL DEFAULT '0',
  stock_after int(11) NOT NULL DEFAULT '0',
  expected_stock int(11) NOT NULL DEFAULT '0',
  remove_stock int(11) NOT NULL DEFAULT '0',
  notes varchar(200) DEFAULT NULL,
  PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

CREATE TABLE supplier (
  id int(11) NOT NULL AUTO_INCREMENT,
  supplier varchar(45) DEFAULT NULL,
  receipt_number varchar(45) DEFAULT NULL,
  purchase_order_number varchar(45) DEFAULT NULL,
  sku int(11) NOT NULL,
  date_created datetime DEFAULT NULL,
  PRIMARY KEY (id)
) ENGINE=InnoDB AUTO_INCREMENT=21 DEFAULT CHARSET=latin1;

CREATE TABLE unit (
  id int(11) NOT NULL AUTO_INCREMENT,
  unit varchar(100) DEFAULT NULL,
  PRIMARY KEY (id),
  UNIQUE KEY unit_UNIQUE (unit)
) ENGINE=InnoDB AUTO_INCREMENT=14 DEFAULT CHARSET=latin1;

CREATE TABLE user (
  id int(11) NOT NULL AUTO_INCREMENT,
  is_admin tinyint(1) NOT NULL DEFAULT '0',
  is_super_admin tinyint(1) NOT NULL DEFAULT '0',
  password varchar(255) DEFAULT NULL,
  username varchar(255) DEFAULT NULL,
  PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
