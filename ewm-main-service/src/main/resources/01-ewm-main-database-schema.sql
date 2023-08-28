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

CREATE TABLE IF NOT EXISTS public.events    (
                                                id bigserial NOT NULL,
                                                title varchar(2000) NOT NULL,
                                                annotation varchar(2000) NOT NULL,
                                                category_id bigint NOT NULL,
                                                description varchar(7000) NOT NULL,
                                                event_date timestamp without time zone NOT NULL,
                                                created timestamp without time zone NOT NULL,
                                                published timestamp without time zone,
                                                latitude numeric(12,10) NOT NULL,
                                                longitude numeric(13,10) NOT NULL,
                                                views bigint NOT NULL,
                                                user_id bigint NOT NULL,
                                                paid boolean NOT NULL,
                                                participant_limit integer NOT NULL,
                                                request_moderation boolean NOT NULL,
                                                state integer NOT NULL,
                                                confirmed_requests bigint NOT NULL,
                                                CONSTRAINT Events_pkey PRIMARY KEY (id),
                                                CONSTRAINT Categories_FK FOREIGN KEY (category_id) REFERENCES public.categories (id)
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
                                                  created timestamp without time zone NOT NULL,
                                                  status integer NOT NULL,
                                                  CONSTRAINT Requests_pkey PRIMARY KEY (id),
                                                  CONSTRAINT User_FK FOREIGN KEY (user_id) REFERENCES public.users (id)
                                                      ON DELETE RESTRICT
                                                      ON UPDATE CASCADE,
                                                  CONSTRAINT Event_FK FOREIGN KEY (event_id) REFERENCES public.events (id)
                                                      ON DELETE RESTRICT
                                                      ON UPDATE CASCADE,
                                                  CONSTRAINT Unique_Request UNIQUE (user_id, event_id)
);

CREATE TABLE IF NOT EXISTS public.compilations    (
                                                      id bigserial NOT NULL,
                                                      pinned boolean NOT NULL,
                                                      title varchar(50) NOT NULL,
                                                      CONSTRAINT Compilations_pkey PRIMARY KEY (id),
                                                      CONSTRAINT Unique_Compilation_Title UNIQUE (title)
);

CREATE TABLE IF NOT EXISTS public.compilation_array    (
                                                           id bigserial NOT NULL,
                                                           compilation_id bigint NOT NULL,
                                                           event_id bigint,
                                                           CONSTRAINT Compilations_Array_pkey PRIMARY KEY (id),
                                                           CONSTRAINT Compilations_FK FOREIGN KEY (compilation_id) REFERENCES public.compilations (id)
                                                               ON DELETE CASCADE
                                                               ON UPDATE CASCADE,
                                                           CONSTRAINT Compilations_Array_Event_FK FOREIGN KEY (event_id) REFERENCES public.events (id)
                                                               ON DELETE CASCADE
                                                               ON UPDATE CASCADE
);

CREATE TABLE IF NOT EXISTS public.likes (
                                            id bigserial NOT NULL,
                                            user_id bigint NOT NULL,
                                            event_id bigint NOT NULL,
                                            grade integer NOT NULL,
                                            CONSTRAINT Likes_pkey PRIMARY KEY (id),
                                            CONSTRAINT User_Likes_FK FOREIGN KEY (user_id) REFERENCES public.users (id)
                                                ON DELETE RESTRICT
                                                ON UPDATE CASCADE,
                                            CONSTRAINT Event_Likes_FK FOREIGN KEY (event_id) REFERENCES public.events (id)
                                                ON DELETE RESTRICT
                                                ON UPDATE CASCADE,
                                            CONSTRAINT Unique_Like UNIQUE (user_id, event_id)
);

CREATE INDEX IF NOT EXISTS likes_user_id_index ON public.likes USING btree (user_id) WITH (FILLFACTOR=75);
