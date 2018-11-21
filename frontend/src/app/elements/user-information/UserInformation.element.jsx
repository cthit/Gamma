import {
    DigitButton,
    DigitTranslations,
    DigitText
} from "@cthit/react-digit-components";
import React from "react";
import styled from "styled-components";
import { Center, Spacing } from "../../../common-ui/layout";
import IfElseRendering from "../../../common/declaratives/if-else-rendering";

const UserInformation = ({
    loaded,
    loggedIn,
    user,
    logout,
    currentPath,
    toastOpen
}) => (
    <IfElseRendering
        test={loaded == null ? false : loaded && loggedIn}
        ifRender={() => (
            <DigitTranslations
                render={text => (
                    <Container>
                        {console.log(text)}
                        <Center>
                            <DigitText.Title white text={user.nick} />
                        </Center>
                        <Spacing />
                        <DigitButton
                            text={text.Logout}
                            onClick={() => logout(text.LoggedOut)}
                        />
                    </Container>
                )}
            />
        )}
    />
);

const Container = styled.div`
    display: flex;
    flex-direction: row;
    justify-content: center;
`;

export default UserInformation;
