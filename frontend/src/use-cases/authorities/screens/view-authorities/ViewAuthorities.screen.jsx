import {
    DigitFAB,
    DigitLayout,
    DigitDesign,
    useDigitTranslations
} from "@cthit/react-digit-components";
import Add from "@material-ui/icons/Add";
import React, { useEffect, useState } from "react";
import translations from "../../Authorities.translations";
import {
    getAuthorities,
    getAuthorityLevels
} from "../../../../api/authorities/get.authorities";
import AuthorityLevelCard from "./elements/authority-level-card";
import styled from "styled-components";

const Grid = styled.div`
    flex: 1;

    display: grid;
    grid-template-columns: repeat(auto-fit, minmax(288px, 1fr));
    column-gap: 16px;
    row-gap: 16px;

    margin-bottom: calc(56px + 16px);
`;

const ViewAuthorities = () => {
    const [authorityLevels, setAuthorityLevels] = useState(null);
    const [authorities, setAuthorities] = useState(null);
    const [text] = useDigitTranslations(translations);
    const [read, setRead] = useState(true);

    useEffect(() => {
        if (read) {
            Promise.all([getAuthorities(), getAuthorityLevels()]).then(
                ([authoritiesResponse, authorityLevelsResponse]) => {
                    const newAuthorities = {};
                    authoritiesResponse.data.authorities.forEach(authority => {
                        const { superGroup, post, authorityLevel } = authority;

                        var auth = newAuthorities[authorityLevel.id];
                        if (auth == null) {
                            auth = [];
                        }

                        auth.push({
                            superGroup,
                            post
                        });

                        newAuthorities[authorityLevel.id] = auth;
                    });

                    setAuthorities(newAuthorities);
                    setAuthorityLevels(
                        authorityLevelsResponse.data.authorityLevels
                    );
                }
            );
        }
        setRead(false);
    }, [read, setRead]);

    if (authorityLevels == null || authorities == null) {
        return null;
    }

    return (
        <>
            <Grid>
                {authorityLevels.map(authorityLevel => (
                    <AuthorityLevelCard
                        key={authorityLevel.id}
                        authorityLevel={authorityLevel}
                        authorities={authorities[authorityLevel.id]}
                        forceUpdate={() => setRead(true)}
                    />
                ))}
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
