CREATE TABLE it_user_approval (
    ituser_id UUID REFERENCES ituser(id),
    itclient_id UUID REFERENCES itclient(id),
    CONSTRAINT it_user_approval_pk PRIMARY KEY(ituser_id, itclient_id)
);
