import React from "react";

import {
    DigitLayout,
    DigitTextField,
    DigitTranslations,
    DigitText,
    DigitSelect
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

const CreateMembership = ({ posts, value, onChange }) => (
    <DigitTranslations
        translations={translations}
        render={(text, activeLanguage) => (
            <DigitLayout.Row centerVertical>
                <DigitLayout.Size absWidth="200px">
                    <DigitText.Text
                        text={
                            value[FIRST_NAME] +
                            ' "' +
                            value[NICK] +
                            '" ' +
                            value[LAST_NAME]
                        }
                    />
                </DigitLayout.Size>
                <DigitLayout.Size absWidth="200px">
                    <DigitSelect
                        value={value.postId || ""}
                        onChange={e => {
                            onChange({
                                ...value,
                                postId: e.target.value
                            });
                        }}
                        valueToTextMap={getDifferentPostNames(
                            posts,
                            activeLanguage
                        )}
                        upperLabel={text.Post}
                    />
                </DigitLayout.Size>
                <DigitLayout.Size absWidth="200px">
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
                </DigitLayout.Size>
            </DigitLayout.Row>
        )}
    />
);

export default CreateMembership;
