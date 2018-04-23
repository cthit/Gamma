--
-- PostgreSQL database dump
--

-- Dumped from database version 10.3 (Debian 10.3-1.pgdg90+1)
-- Dumped by pg_dump version 10.3

SET statement_timeout = 0;
SET lock_timeout = 0;
SET idle_in_transaction_session_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SELECT pg_catalog.set_config('search_path', '', false);
SET check_function_bodies = false;
SET client_min_messages = warning;
SET row_security = off;

SET default_tablespace = '';

SET default_with_oids = false;

--
-- Name: fkit_group; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.fkit_group (
    id uuid NOT NULL,
    name character varying(50) NOT NULL,
    description text,
    email character varying(100) NOT NULL,
    type character varying(30) NOT NULL
);


ALTER TABLE public.fkit_group OWNER TO postgres;

--
-- Name: ituser; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.ituser (
    id uuid NOT NULL,
    cid character varying(10) NOT NULL,
    password character varying(255) NOT NULL,
    nick character varying(50) NOT NULL,
    first_name character varying(50),
    last_name character varying(50),
    email character varying(100) NOT NULL,
    phone character varying(15),
    language character varying(5),
    avatar_url character varying(255),
    gdpr boolean DEFAULT false NOT NULL,
    user_agreement boolean DEFAULT false NOT NULL,
    acceptance_year integer,
    created_at timestamp without time zone DEFAULT CURRENT_TIMESTAMP NOT NULL,
    last_modified_at timestamp without time zone DEFAULT CURRENT_TIMESTAMP NOT NULL,
    CONSTRAINT ituser_valid_year CHECK ((acceptance_year >= 2001))
);


ALTER TABLE public.ituser OWNER TO postgres;

--
-- Name: membership; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.membership (
    ituser_id uuid NOT NULL,
    fkit_group_id uuid NOT NULL,
    post_id uuid NOT NULL,
    year integer NOT NULL,
    unofficial_post_name character varying(100),
    CONSTRAINT membership_valid_year CHECK ((year >= 2001))
);


ALTER TABLE public.membership OWNER TO postgres;

--
-- Name: post; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.post (
    id uuid NOT NULL,
    post_name character varying(50)
);


ALTER TABLE public.post OWNER TO postgres;

--
-- Name: fkit_group fkit_group_email_unique; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.fkit_group
    ADD CONSTRAINT fkit_group_email_unique UNIQUE (email);


--
-- Name: fkit_group fkit_group_name_unique; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.fkit_group
    ADD CONSTRAINT fkit_group_name_unique UNIQUE (name);


--
-- Name: fkit_group fkit_group_pk; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.fkit_group
    ADD CONSTRAINT fkit_group_pk PRIMARY KEY (id);


--
-- Name: ituser ituser_cid_unique; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.ituser
    ADD CONSTRAINT ituser_cid_unique UNIQUE (cid);


--
-- Name: ituser ituser_email_unique; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.ituser
    ADD CONSTRAINT ituser_email_unique UNIQUE (email);


--
-- Name: ituser ituser_pk; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.ituser
    ADD CONSTRAINT ituser_pk PRIMARY KEY (id);


--
-- Name: membership membership_pk; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.membership
    ADD CONSTRAINT membership_pk PRIMARY KEY (ituser_id, fkit_group_id);


--
-- Name: post post_name_unique; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.post
    ADD CONSTRAINT post_name_unique UNIQUE (post_name);


--
-- Name: post post_pk; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.post
    ADD CONSTRAINT post_pk PRIMARY KEY (id);


--
-- Name: membership membership_fkit_group_fk; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.membership
    ADD CONSTRAINT membership_fkit_group_fk FOREIGN KEY (fkit_group_id) REFERENCES public.fkit_group(id);


--
-- Name: membership membership_ituser_fk; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.membership
    ADD CONSTRAINT membership_ituser_fk FOREIGN KEY (ituser_id) REFERENCES public.ituser(id);


--
-- Name: membership membership_post_fk; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.membership
    ADD CONSTRAINT membership_post_fk FOREIGN KEY (post_id) REFERENCES public.post(id);


--
-- PostgreSQL database dump complete
--

