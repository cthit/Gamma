import {
    DigitButton,
    DigitLayout,
    DigitText,
    DigitTranslations,
    DigitIfElseRendering
} from "@cthit/react-digit-components";
import React from "react";
import styled from "styled-components";

const UserInformation = ({
    loaded,
    loggedIn,
    user,
    logout,
    currentPath,
    toastOpen
}) => (
    <DigitIfElseRendering
        test={loaded == null ? false : loaded && loggedIn}
        ifRender={() => (
            <DigitTranslations
                render={text => (
                    <Container>
                        {console.log(text)}
                        <DigitLayout.Center>
                            <DigitText.Title white text={user.nick} />
                        </DigitLayout.Center>
                        <DigitLayout.Spacing />
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
