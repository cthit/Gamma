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

const ViewAuthorities = () => {
    const [authorityLevels, setAuthorityLevels] = useState(null);
    const [authorities, setAuthorities] = useState(null);
    const [text] = useDigitTranslations(translations);

    useEffect(() => {
        getAuthorities().then(response => {
            const newAuthorities = {};
            response.data.authorities.forEach(authority => {
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
        });
        getAuthorityLevels().then(response => {
            setAuthorityLevels(response.data.authorityLevels);
        });
    }, []);

    if (authorityLevels == null || authorities == null) {
        return null;
    }

    return (
        <>
            <DigitLayout.UniformGrid minItemWidth={"300px"}>
                {authorityLevels.map(authorityLevel => (
                    <AuthorityLevelCard
                        key={authorityLevel.id}
                        authorityLevel={authorityLevel}
                        authorities={authorities[authorityLevel.id]}
                    />
                ))}
            </DigitLayout.UniformGrid>
            <DigitLayout.DownRightPosition>
                <DigitDesign.Link to={"/authorities/create-authority-level"}>
                    <DigitFAB primary icon={Add} text={text.CreateAuthority} />
                </DigitDesign.Link>
            </DigitLayout.DownRightPosition>
        </>
    );
};

export default ViewAuthorities;
