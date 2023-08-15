CREATE TABLE IF NOT EXISTS public.users (
                                            id bigserial NOT NULL,
                                            email varchar(255) NOT NULL,
                                            name varchar(255) NOT NULL,
                                            CONSTRAINT Users_pkey PRIMARY KEY (id),
                                            CONSTRAINT Users_Email_unique UNIQUE (email)
);

CREATE TABLE IF NOT EXISTS public.categories    (
                                                    id bigserial NOT NULL,
                                                    name varchar(255) NOT NULL,
                                                    CONSTRAINT Categories_pkey PRIMARY KEY (id),
                                                    CONSTRAINT Category_Name_unique UNIQUE (name)
);

CREATE TABLE IF NOT EXISTS public.locations    (
                                                    id bigserial NOT NULL,
                                                    latitude varchar(255) NOT NULL,
                                                    longitude varchar(255) NOT NULL,
                                                    CONSTRAINT Locations_pkey PRIMARY KEY (id),
                                                    CONSTRAINT Locations_unique UNIQUE (latitude, longitude)
);

CREATE TABLE IF NOT EXISTS public.events    (
                                                    id bigserial NOT NULL,
                                                    title varchar(2000) NOT NULL,
                                                    annotation varchar(2000) NOT NULL,
                                                    category_id bigint NOT NULL,
                                                    description varchar(7000) NOT NULL,
                                                    event_date timestamp without time zone NOT NULL,
                                                    location_id bigint NOT NULL,
                                                    user_id bigint NOT NULL,
                                                    paid boolean NOT NULL,
                                                    participant_limit integer NOT NULL,
                                                    requestModeration boolean NOT NULL,
                                                    CONSTRAINT Events_pkey PRIMARY KEY (id),
                                                    CONSTRAINT Categories_FK FOREIGN KEY (category_id) REFERENCES public.categories (id)
                                                        ON DELETE RESTRICT
                                                        ON UPDATE CASCADE,
                                                    CONSTRAINT Location_FK FOREIGN KEY (location_id) REFERENCES public.locations (id)
                                                        ON DELETE RESTRICT
                                                        ON UPDATE CASCADE,
                                                    CONSTRAINT User_FK FOREIGN KEY (user_id) REFERENCES public.users (id)
                                                        ON DELETE RESTRICT
                                                        ON UPDATE CASCADE
);

CREATE TABLE IF NOT EXISTS public.requests    (
                                                   id bigserial NOT NULL,
                                                   user_id bigint NOT NULL,
                                                   event_id bigint NOT NULL,
                                                   CONSTRAINT Requests_pkey PRIMARY KEY (id),
                                                   CONSTRAINT User_FK FOREIGN KEY (user_id) REFERENCES public.users (id)
                                                       ON DELETE RESTRICT
                                                       ON UPDATE CASCADE,
                                                   CONSTRAINT Event_FK FOREIGN KEY (event_id) REFERENCES public.events (id)
                                                       ON DELETE RESTRICT
                                                       ON UPDATE CASCADE
);