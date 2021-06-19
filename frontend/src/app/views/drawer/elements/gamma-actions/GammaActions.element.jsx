import ExpandMore from "@material-ui/icons/ExpandMore";
import React, { useContext } from "react";
import styled from "styled-components";

import {
    useDigitTranslations,
    DigitLayout,
    DigitAvatar,
    DigitMenu
} from "@cthit/react-digit-components";

import GammaUserContext from "common/context/GammaUser.context";
import { getBackendUrl } from "common/utils/configs/envVariablesLoader";

import translations from "./GammaActions.element.translations";

const Nick = styled.h6`
    font-family: Roboto, serif;
    font-size: 20px;
    text-overflow: ellipsis;
    max-width: 152px;
    overflow: hidden;
    white-space: nowrap;
`;

const GammaActions = () => {
    const [user, , [loading]] = useContext(GammaUserContext);
    const [text] = useDigitTranslations(translations);

    //this will update the image if updating your avatar
    if (loading) {
        return null;
    }

    return (
        <DigitLayout.Row
            justifyContent={"center"}
            alignItems={"center"}
            size={{
                width: "100%",
                height: "60px",
                minHeight: "60px",
                maxHeight: "60px"
            }}
        >
            <DigitAvatar
                imageAlt={"Avatar"}
                imageSrc={user.avatarUrl}
                margin={{ right: "16px" }}
            />
            <Nick>{user.nick}</Nick>
            <DigitMenu
                icon={ExpandMore}
                onClick={item => {
                    switch (item) {
                        case "signOut":
                            window.location.href = getBackendUrl() + "/logout";
                            break;
                        default:
                            break;
                    }
                }}
                valueToTextMap={{
                    signOut: text.SignOut
                }}
                order={["signOut"]}
            />
        </DigitLayout.Row>
    );
};

export default GammaActions;
