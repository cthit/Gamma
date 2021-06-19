import React from "react";
import translations from "../EditAuthority.screen.translations.json";
import {
    DigitAutocompleteSelectSingle,
    DigitEditDataCard,
    DigitSelect,
    useDigitToast,
    useDigitTranslations
} from "@cthit/react-digit-components";
import * as yup from "yup";
import { addUserToAuthorityLevel } from "../../../../../api/authorities/post.authoritites";

const AddUserAuthority = ({ userOptions, setRead, authorityLevelName }) => {
    const [text] = useDigitTranslations(translations);
    const [queueToast] = useDigitToast();

    return (
        <DigitEditDataCard
            margin={"0px"}
            size={{ width: "100%", maxWidth: "400px" }}
            alignSelf={"center"}
            centerFields
            submitText={text.Add}
            validationSchema={yup.object().shape({
                user: yup.string().required(text.User + text.IsRequired)
            })}
            onSubmit={(values, actions) =>
                addUserToAuthorityLevel({
                    userId: values.user,
                    authorityLevelName
                })
                    .then(() => {
                        setRead(true);
                        actions.resetForm();
                        queueToast({
                            text: text.AddedToAuthorityLevel
                        });
                    })
                    .catch(() => {
                        queueToast({
                            text: text.FailedToAuthorityLevel
                        });
                    })
            }
            keysOrder={["user"]}
            keysComponentData={{
                user: {
                    component: DigitAutocompleteSelectSingle,
                    componentProps: {
                        upperLabel: text.User,
                        options: userOptions,
                        outlined: true,
                        size: { width: "300px" }
                    }
                }
            }}
        />
    );
};

export default AddUserAuthority;
