import React from "react";
import {
    DigitTextField,
    DigitText,
    DigitSelect,
    useDigitTranslations
} from "@cthit/react-digit-components";
import translations from "./CreateMembership.view.translations";
import {
    USER_FIRST_NAME,
    USER_LAST_NAME,
    USER_NICK
} from "../../../../../../api/users/props.users.api";
import styled from "styled-components";

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

const CreateMembership = ({ posts, value, onChange }) => {
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
                value={value.postId || ""}
                onChange={e => {
                    onChange({
                        ...value,
                        postId: e.target.value
                    });
                }}
                valueToTextMap={getDifferentPostNames(posts, activeLanguage)}
                upperLabel={text.Post}
            />
            <DigitTextField
                flex={"1"}
                outlined
                upperLabel={text.UnofficialPostName}
                value={value.unofficialPostName || ""}
                onChange={e => {
                    onChange({
                        ...value,
                        unofficialPostName: e.target.value
                    });
                }}
            />
        </CustomRow>
    );
};

export default CreateMembership;
