insert into orders(id, customer_id, status, created_at, updated_at, version)
values ('00000000-0000-0000-0000-000000000001', 'customer-1', 'PENDING', now(), now(), 0);

insert into order_items(order_id, sku, quantity, unit_price, currency)
values ('00000000-0000-0000-0000-000000000001', 'JAVA-25', 1, 25.00, 'EUR');
