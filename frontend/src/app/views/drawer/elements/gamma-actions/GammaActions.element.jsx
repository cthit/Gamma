import React from "react";
import useGammaUser from "../../../../../common/hooks/use-gamma-user/useGammaUser";
import ExpandMore from "@material-ui/icons/ExpandMore";
import { getBackendUrl } from "../../../../../common/utils/configs/envVariablesLoader";
import {
    useDigitTranslations,
    DigitLayout,
    DigitAvatar,
    DigitMenu
} from "@cthit/react-digit-components";
import styled from "styled-components";
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
    const user = useGammaUser();
    const [text] = useDigitTranslations(translations);

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
