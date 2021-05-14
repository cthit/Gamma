import React from "react";
import styled from "styled-components";

import {
    DigitTextField,
    DigitText,
    DigitSelect,
    useDigitTranslations
} from "@cthit/react-digit-components";

import {
    USER_FIRST_NAME,
    USER_LAST_NAME,
    USER_NICK
} from "api/users/props.users.api";

import translations from "./CreateMembership.view.translations";

const CustomRow = styled.div`
    display: flex;
    flex-direction: row;
    margin-bottom: 16px;
    align-items: baseline;

    @media (max-width: 600px) {
        flex-direction: column;
        align-self: center;
    }
`;

function getDifferentPostNames(posts, activeLanguage) {
    const output = {};
    posts.forEach(post => {
        output[post.id] = post[activeLanguage];
    });

    return output;
}

const CreateMembership = ({ posts, value, onChange, innerInputs }) => {
    const [text, activeLanguage] = useDigitTranslations(translations);

    return (
        <CustomRow>
            <div style={{ flex: "1" }}>
                <DigitText.Text
                    alignCenter
                    text={
                        value[USER_FIRST_NAME] +
                        ' "' +
                        value[USER_NICK] +
                        '" ' +
                        value[USER_LAST_NAME]
                    }
                />
            </div>
            <DigitSelect
                flex={"1"}
                outlined
                valueToTextMap={getDifferentPostNames(posts, activeLanguage)}
                upperLabel={text.Post}
                {...innerInputs.postId}
            />
            <DigitTextField
                flex={"1"}
                outlined
                upperLabel={text.UnofficialPostName}
                {...innerInputs.unofficialPostName}
            />
        </CustomRow>
    );
};

export default CreateMembership;
