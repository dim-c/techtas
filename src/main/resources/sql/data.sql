/*generate startup data*/
INSERT INTO CUSTOMER(id,name,surname, blacklisted ) VALUES(10001,'Ivan', 'Ivanov', FALSE);
INSERT INTO CUSTOMER(id,name,surname, blacklisted ) VALUES(10002,'Ivan', 'Banan', TRUE);
INSERT INTO CUSTOMER(id,name,surname, blacklisted ) VALUES(10003,'Mykola', 'Petrenko', FALSE);


INSERT INTO LOAN(amount, term, customer_id, country) VALUES(120000, {ts '2022-09-17 18:47:52.69'}, 10001, 'UA');
INSERT INTO LOAN(amount, term, customer_id, country) VALUES(350, {ts '2022-10-17 18:47:52.69'}, 10001, 'UK');
INSERT INTO LOAN(amount, term, customer_id, country) VALUES(120000.25, {ts '2032-10-17 18:47:52.69'}, 10001, 'UA');

INSERT INTO LOAN(amount, term, customer_id, country) VALUES(120000, {ts '2022-09-17 18:47:52.69'}, 10002,'US');

INSERT INTO LOAN(amount, term, customer_id, country) VALUES(50000, {ts '2022-09-17 18:47:52.69'}, 10003, 'UA');
INSERT INTO LOAN(amount, term, customer_id, country) VALUES(111, {ts '2022-10-17 18:47:52.69'}, 10003, 'AU');
INSERT INTO LOAN(amount, term, customer_id, country) VALUES(125.25, {ts '2032-10-17 18:47:52.69'}, 10003, 'IR');