CREATE TABLE IF NOT EXISTS public.items (
	id bigserial primary key,
	price int8 NULL,
	description varchar(255) NULL,
	img_path varchar(255) NULL,
	title varchar(255) NULL
);

CREATE TABLE IF NOT EXISTS public.orders (
	id bigserial primary key,
	user_login varchar(255),
	FOREIGN KEY (user_login) REFERENCES public.users(login)
);

CREATE TABLE IF NOT EXISTS public.order_item_connection (
	id bigserial primary key,
	item_id int8 NOT NULL,
	order_id int8 NOT NULL,
	item_count int4 NOT NULL,
	FOREIGN KEY (order_id) REFERENCES public.orders(id),
	FOREIGN KEY (item_id) REFERENCES public.items(id)
);

CREATE TABLE IF NOT EXISTS public.users (
	id bigserial primary key,
	login varchar(255) UNIQUE NULL,
	password varchar(255) NULL
);

CREATE TABLE IF NOT EXISTS public.cart_items (
	id bigserial primary key,
	item_id int8 NOT NULL,
	user_login varchar(255) NOT NULL,
	item_count int4 NOT NULL,
	FOREIGN KEY (user_login) REFERENCES public.users(login),
	FOREIGN KEY (item_id) REFERENCES public.items(id)
);