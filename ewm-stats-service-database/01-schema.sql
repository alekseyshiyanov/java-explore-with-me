CREATE TABLE IF NOT EXISTS public.apps (
                                            app_id bigserial NOT NULL,
                                            name varchar(255) NOT NULL,
                                            CONSTRAINT App_pkey PRIMARY KEY (app_id)
);

CREATE TABLE IF NOT EXISTS public.hits (
                                            hit_id bigserial NOT NULL,
                                            app bigint NOT NULL,
                                            uri varchar(255) NOT NULL,
                                            ip_address varchar(40) NOT NULL,
                                            created timestamp without time zone NOT NULL,
                                            CONSTRAINT Hit_pkey PRIMARY KEY (hit_id),
                                            CONSTRAINT App_FK FOREIGN KEY(app) REFERENCES public.apps (app_id)
                                                              ON DELETE CASCADE
                                                              ON UPDATE CASCADE
);