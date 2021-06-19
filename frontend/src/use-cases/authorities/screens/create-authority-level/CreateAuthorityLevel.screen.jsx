import React from "react";
import { useHistory } from "react-router-dom";
import * as yup from "yup";

import {
    DigitLayout,
    DigitEditDataCard,
    DigitTextField,
    useDigitTranslations,
    useDigitToast
} from "@cthit/react-digit-components";

import { addAuthorityLevel } from "api/authorities/post.authoritites";

import translations from "./CreateAuthorityLevel.screen.translations";

const CreateAuthorityLevel = () => {
    const [text] = useDigitTranslations(translations);
    const [queueToast] = useDigitToast();
    const history = useHistory();

    return (
        <DigitLayout.Center flex={"1"}>
            <DigitEditDataCard
                flex={"1"}
                size={{ minWidth: "280px" }}
                titleText={text.CreateAuthorityLevel}
                centerFields
                onSubmit={(values, actions) => {
                    addAuthorityLevel(values)
                        .then(() => {
                            actions.resetForm();
                            queueToast({
                                text: values.authorityLevel + text.AddSuccessful
                            });
                        })
                        .catch(() => {
                            queueToast({
                                text: text.AddError
                            });
                        });
                }}
                keysComponentData={{
                    authorityLevel: {
                        component: DigitTextField,
                        componentProps: {
                            outlined: true,
                            upperLabel: text.AuthorityLevel,
                            maxLength: 20
                        }
                    }
                }}
                keysOrder={["authorityLevel"]}
                validationSchema={yup.object().shape({
                    authorityLevel: yup
                        .string()
                        .required(text.AuthorityLevel + text.IsRequired)
                })}
                extraButton={{
                    text: text.Back,
                    onClick: () => history.goBack()
                }}
                submitText={text.Create}
            />
        </DigitLayout.Center>
    );
};

export default CreateAuthorityLevel;
