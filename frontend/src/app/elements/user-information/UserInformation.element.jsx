import {
    DigitButton,
    DigitLayout,
    DigitText,
    useDigitTranslations,
    useGammaSignOut,
    useGammaUser
} from "@cthit/react-digit-components";
import React from "react";
import styled from "styled-components";

const UserInformation = () => {
    const [text] = useDigitTranslations();
    const signOut = useGammaSignOut();
    const user = useGammaUser();

    if (user == null) {
        return user;
    }

    return (
        <Container>
            <DigitLayout.Center>
                <DigitText.Title white text={user.nick} />
            </DigitLayout.Center>
            <DigitLayout.Spacing />
            <DigitButton text={text.Logout} onClick={signOut} />
        </Container>
    );
};

const Container = styled.div`
    display: flex;
    flex-direction: row;
    justify-content: center;
`;

export default UserInformation;
