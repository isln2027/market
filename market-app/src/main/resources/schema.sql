CREATE TABLE IF NOT EXISTS public.items (
	id bigserial primary key,
	count int4 NULL,
	price int8 NULL,
	description varchar(255) NULL,
	img_path varchar(255) NULL,
	title varchar(255) NULL
);

CREATE TABLE IF NOT EXISTS public.orders (
	id bigserial primary key
);

CREATE TABLE IF NOT EXISTS public.order_item_connection (
	id bigserial primary key,
	item_id int8 NOT NULL,
	order_id int8 NOT NULL,
	item_count int4 NOT NULL,
	FOREIGN KEY (order_id) REFERENCES public.orders(id),
	FOREIGN KEY (item_id) REFERENCES public.items(id)
);