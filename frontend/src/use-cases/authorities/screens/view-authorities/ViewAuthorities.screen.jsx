import Add from "@material-ui/icons/Add";
import React, { useEffect, useState } from "react";
import styled from "styled-components";

import {
    DigitFAB,
    DigitLayout,
    DigitDesign,
    useDigitTranslations,
    DigitLoading
} from "@cthit/react-digit-components";

import { getAuthorityLevels } from "api/authorities/get.authorities";

import translations from "../../Authorities.translations";
import AuthorityLevelCard from "./elements/authority-level-card";

const Grid = styled.div`
    flex: 1;

    display: grid;
    grid-template-columns: repeat(auto-fit, minmax(288px, 400px));
    column-gap: 16px;
    row-gap: 16px;

    justify-content: center;

    margin-bottom: calc(56px + 16px);
`;

const ViewAuthorities = () => {
    const [authorities, setAuthorities] = useState(null);
    const [text] = useDigitTranslations(translations);
    const [read, setRead] = useState(true);

    useEffect(() => {
        if (read) {
            getAuthorityLevels().then(authoritiesResponse =>
                setAuthorities(authoritiesResponse.data)
            );
        }
        setRead(false);
    }, [read, setRead]);

    if (authorities == null) {
        return <DigitLoading loading alignSelf={"center"} margin={"auto"} />;
    }

    return (
        <>
            <Grid>
                {authorities.map(
                    ({ authorityLevelName, users, posts, superGroups }) => (
                        <AuthorityLevelCard
                            key={authorityLevelName}
                            authorityLevel={authorityLevelName}
                            forceUpdate={() => setRead(true)}
                            users={users}
                            posts={posts}
                            superGroups={superGroups}
                        />
                    )
                )}
            </Grid>
            <DigitLayout.DownRightPosition>
                <DigitDesign.Link to={"/authorities/create-authority-level"}>
                    <DigitFAB primary icon={Add} text={text.CreateAuthority} />
                </DigitDesign.Link>
            </DigitLayout.DownRightPosition>
        </>
    );
};

export default ViewAuthorities;
