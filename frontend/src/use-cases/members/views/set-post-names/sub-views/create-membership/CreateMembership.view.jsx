import React from "react";

import {
    DigitLayout,
    DigitTextField,
    DigitText,
    DigitSelect,
    useDigitTranslations
} from "@cthit/react-digit-components";

import translations from "./CreateMembership.view.translations";
import {
    FIRST_NAME,
    LAST_NAME,
    NICK
} from "../../../../../../api/users/props.users.api";

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
        <DigitLayout.Row centerVertical alignItems={"baseline"}>
            <DigitText.Text
                text={
                    value[FIRST_NAME] +
                    ' "' +
                    value[NICK] +
                    '" ' +
                    value[LAST_NAME]
                }
            />
            <DigitSelect
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
                upperLabel={text.UnofficialPostName}
                value={value.unofficialPostName || ""}
                onChange={e => {
                    onChange({
                        ...value,
                        unofficialPostName: e.target.value
                    });
                }}
            />
        </DigitLayout.Row>
    );
};

export default CreateMembership;
